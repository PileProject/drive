/**
 * Copyright (C) 2011-2016 PILE Project, Inc. <dev@pileproject.com>
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
 *
 * This class is one-off.
 * Create this instance whenever you want to execute and DO NOT REUSE the instance.
 * <p>
 * The stream emits {@link Bundle} objects for the subscriber.
 * The bundles contain message type whose key is {@link RxObservableProgram#KEY_MESSAGE_TYPE},
 * and optionally one message argument whose key is {@link RxObservableProgram#KEY_MESSAGE_ARG}.
 * The message types are:
 * <ul>
 *     <li>{@link RxObservableProgram#MESSAGE_STARTED}: emitted at the beginning of the execution</li>
 *     <li>{@link RxObservableProgram#MESSAGE_TERMINATED}: emitted when {@link RxObservableProgram#requestTerminate()} ()} is called</li>
 *     <li>{@link RxObservableProgram#MESSAGE_PAUSED}: emitted when {@link RxObservableProgram#requestPause()} is called</li>
 *     <li>{@link RxObservableProgram#MESSAGE_RESTARTED}: emitted when {@link RxObservableProgram#requestRestart()} is called</li>
 *     <li>{@link RxObservableProgram#MESSAGE_BLOCK_EXECUTED}: emitted when a block is executed.
 *         this message contains an argument which is the index of the block and can be accessed
 *         with {@link RxObservableProgram#KEY_MESSAGE_ARG} </li>
 * </ul>
 * When the execution of the program that this class holds is ended, {@link rx.Subscriber#onCompleted} is called,
 * no matter the type of ending (terminated by a user/ended normally).
 * <p>
 * Typically you can use this class with code like below.
 * Note that the process of this class is heavy, including I/O connection.
 * Do not run this on your UI thread.
 *
 * <pre><code>
 * RxObservableProgram program = new RxObservableProgram(...);
 * Observable.create(program)
 *     .subscribeOn(Schedulers.newThread())
 *     .subscribe( ... ); // your code goes here
 * </code></pre>
 */
public class RxObservableProgram implements Observable.OnSubscribe<Bundle> {

    public static final String KEY_MESSAGE_TYPE = "rx-observable-program-key-message-type";
    public static final String KEY_MESSAGE_ARG = "rx-observable-program-key-message-arg";

    public static final int MESSAGE_STARTED = 1;
    public static final int MESSAGE_TERMINATED = 2;
    public static final int MESSAGE_PAUSED = 3;
    public static final int MESSAGE_RESTARTED = 4;
    public static final int MESSAGE_BLOCK_EXECUTED = 5;

    private final MachineController mMachineController;

    private final ExecutionCondition mExecutionCondition;

    private boolean mTerminateRequest;

    private boolean mPauseRequest;

    private Thread mRunningThread;

    /**
     * @param program list of blocks to be executed.
     * @param machineController MachineController for the machine to be manipulated.
     */
    public RxObservableProgram(List<BlockBase> program, MachineController machineController) {

        mMachineController = machineController;
        mExecutionCondition = new ExecutionCondition(program);
    }

    @Override
    public void call(Subscriber<? super Bundle> subscriber) {

        mRunningThread = Thread.currentThread();
        subscriber.onNext(makeMessageBundle(MESSAGE_STARTED));

        try {
            mainLoop(subscriber);
        } catch (RuntimeException e) {
            subscriber.onError(e);
        } finally {
            mMachineController.close();
        }

        subscriber.onCompleted();
    }

    private void mainLoop(Subscriber<? super Bundle> subscriber) throws RuntimeException {

        boolean haltNotCalledYet = true;

        while (!mExecutionCondition.hasProgramFinished()) {

            if (mTerminateRequest) {
                subscriber.onNext(makeMessageBundle(MESSAGE_TERMINATED));
                break;
            }

            if (mPauseRequest) {
                // NOTE:
                //   because MachineController#halt and ExecutionCondition#decrementProgramCount should
                //   not be called twice or more, we have to guard this code-block by the haltNotCalledYet flag.
                // FIXME: more elegant guard

                if (haltNotCalledYet) {
                    mMachineController.halt();

                    // next execution should begin at the current block
                    mExecutionCondition.decrementProgramCount();

                    subscriber.onNext(makeMessageBundle(MESSAGE_PAUSED));
                    haltNotCalledYet = false;
                }

                continue;
            }

            // if haltNotCalledYet is false and reached here, the execution was resumed
            if (!haltNotCalledYet) {

                // after the requestRestart is called, mPauseRequest becomes false
                // so for next requestPause calls, haltNotCalledYet should be true
                haltNotCalledYet = true;

                subscriber.onNext(makeMessageBundle(MESSAGE_RESTARTED));
            }


            if (!BlockProgramLogic.willCurrentBlockBeExecuted(mExecutionCondition)) {
                continue;
            }

            int programCount = mExecutionCondition.getProgramCount();

            subscriber.onNext(makeBlockIndexBundle(programCount));

            BlockBase block = mExecutionCondition.getCurrentBlock();

            int delay = block.action(mMachineController, mExecutionCondition);
            sleep(delay);

            mExecutionCondition.incrementProgramCount();
        }
    }

    /**
     * Requests the current execution to be paused.
     */
    public void requestPause() {
        mPauseRequest = true;

        if (mRunningThread != null) {
            mRunningThread.interrupt();
        }
    }

    /**
     * Requests the current execution to be restarted.
     */
    public void requestRestart() {
        mPauseRequest = false;

        if (mRunningThread != null) {
            mRunningThread.interrupt();
        }
    }

    /**
     * Requests the current execution to be terminated.
     */
    public void requestTerminate() {
        mTerminateRequest = true;

        if (mRunningThread != null) {
            mRunningThread.interrupt();
        }
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
        bundle.putInt(KEY_MESSAGE_TYPE, MESSAGE_BLOCK_EXECUTED);
        bundle.putInt(KEY_MESSAGE_ARG, index);
        return bundle;
    }
}

