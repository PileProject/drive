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
package com.pileproject.drive.comm;

import com.pileproject.drivecommand.machine.MachineBase;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * A class which produces Observable of RxJava for connecting to a {@link MachineBase}.
 *
 */
public class RxMachineConnector {

    private RxMachineConnector() {
        throw new AssertionError("This class cannot be instantiated");
    }

    /**
     * Creates {@link Observable} which will produce a stream
     * for connecting to the given {@link MachineBase} instance.
     * When connection is succeeded, {@link Subscriber#onNext} will be called with the
     * connected machine instance, and then {@link Subscriber#onCompleted()} will be called
     * immediately.
     * If {@link MachineBase#connect} causes exceptions, {@link Subscriber#onError(Throwable)} is
     * called with the caught exception.
     *
     * @param machineBase machine instance to be connected.
     * @return Observable with the stream
     */
    public static Observable<MachineBase> connect(final MachineBase machineBase) {
        return Observable.create(new Observable.OnSubscribe<MachineBase>() {

            @Override
            public void call(Subscriber<? super MachineBase> subscriber) {
                try {
                    machineBase.connect();
                    subscriber.onNext(machineBase);
                    subscriber.onCompleted();

                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }
}
