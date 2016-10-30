/*
 * Copyright (C) 2011-2015 PILE Project, Inc. <dev@pileproject.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pileproject.drive.execution;

import android.os.Bundle;

import com.pileproject.drive.programming.visual.block.BlockBase;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * A concrete class of {@link rx.Observable.OnSubscribe}, which generates
 * a stream of blocks that was executed during a program execution.
 * <p/>
 * The stream emits {@link Bundle} objects for the subscriber.
 * The bundles contain message type whose key is {@link RxObservableProgram#KEY_MESSAGE_TYPE},
 * and optionally one message argument whose key is {@link RxObservableProgram#KEY_MESSAGE_ARG}.
 * The message types are:
 * <ul>
 *     <li>{@link RxObservableProgram#START}: emitted at the beginning of the execution</li>
 *     <li>{@link RxObservableProgram#END}: emitted at the end of the execution</li>
 *     <li>{@link RxObservableProgram#PAUSE}: emitted at {@link RxObservableProgram#requestPause()} is called</li>
 *     <li>{@link RxObservableProgram#BLOCK_INDEX}: emitted when a block was executed.
 *         this message contains an argument which is the index of the block </li>
 * </ul>
 * <p/>
 * Typically you can use this class with code like below.
 * Note that the process of this class is heavy, including I/O connection.
 * Do not run this on your UI thread.
 *
 * <code>
 *     RxObservableProgram program = new RxObservableProgram(...);
 *     Observable.create(program)
 *         .subscribeOn(Schedulers.newThread())
 *         .subscribe(...);
 * </code>
 */
public class RxObservableProgram implements Observable.OnSubscribe<Bundle> {

    public static final String KEY_MESSAGE_TYPE = "key";
    public static final String KEY_MESSAGE_ARG = "arg";
    public static final int START = 1;
    public static final int BLOCK_INDEX = 3;
    public static final int PAUSE = 4;
    public static final int END = 2;

    private final MachineController controller;

    private final ExecutionCondition condition;

    private boolean terminateRequest;

    private boolean pauseRequest;

    private Thread runningThread;

    /**
     * @param program list of blocks to be executed.
     * @param controller controller for the machine to be manipulated.
     */
    public RxObservableProgram(List<BlockBase> program, MachineController controller) {

        this.controller = controller;
        this.condition = new ExecutionCondition(program);
    }

    @Override
    public void call(Subscriber<? super Bundle> subscriber) {

        runningThread = Thread.currentThread();
        subscriber.onNext(makeMessageBundle(START));

        try {
            mainLoop(subscriber);
        } catch (RuntimeException e) {
            subscriber.onError(e);
        } finally {
            controller.close();
        }

        subscriber.onCompleted();
    }

    private void mainLoop(Subscriber<? super Bundle> subscriber) throws RuntimeException {

        boolean haltAlreadyCalled = false;

        while (!condition.hasProgramFinished()) {

            if (terminateRequest) {
                subscriber.onNext(makeMessageBundle(END));
                break;
            }

            if (pauseRequest) {
                if (!haltAlreadyCalled) {
                    controller.halt();
                    condition.decrementProgramCount();

                    subscriber.onNext(makeMessageBundle(PAUSE));
                }

                haltAlreadyCalled = true;

                continue;
            }

            haltAlreadyCalled = false;

            if (!BlockProgramLogic.willCurrentBlockBeExecuted(condition)) {
                continue;
            }

            int programCount = condition.getProgramCount();

            subscriber.onNext(makeBlockIndexBundle(programCount));

            BlockBase block = condition.getCurrentBlock();

            int delay = block.action(controller, condition);
            sleep(delay);

            condition.incrementProgramCount();
        }
    }

    /**
     * Requests the current execution to be paused.
     */
    public void requestPause() {
        pauseRequest = true;
        runningThread.interrupt();
    }

    /**
     * Requests the current execution to be restarted.
     */
    public void requestRestart() {
        pauseRequest = false;
        runningThread.interrupt();
    }

    /**
     * Requests the current execution to be terminated.
     */
    public void requestTerminate() {
        terminateRequest = true;
        runningThread.interrupt();
    }

    private void sleep(int milliseconds) {

        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private Bundle makeMessageBundle(int message) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MESSAGE_TYPE, message);
        return bundle;
    }

    private Bundle makeBlockIndexBundle(int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MESSAGE_TYPE, BLOCK_INDEX);
        bundle.putInt(KEY_MESSAGE_ARG, index);
        return bundle;
    }
}

