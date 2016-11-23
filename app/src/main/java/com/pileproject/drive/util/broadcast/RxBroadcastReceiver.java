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
package com.pileproject.drive.util.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.util.Pair;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * A class which produces Observable of RxJava.
 * This class wraps {@link BroadcastReceiver} in Android API
 */
public class RxBroadcastReceiver {
    /**
     * Create {@link Observable} which will produce a stream
     * when the events which specified by {@link IntentFilter} has been issued.
     * @param context context
     * @param intentFilter intent filter that will be passed to {@link Context#registerReceiver(BroadcastReceiver, IntentFilter)}
     * @return observable
     */
    public static Observable<Pair<Context, Intent>> create(final Context context, final IntentFilter intentFilter) {
        return Observable.create(new rx.Observable.OnSubscribe<Pair<Context, Intent>>() {

            @Override
            public void call(final Subscriber<? super Pair<Context, Intent>> subscriber) {
                final BroadcastReceiver receiver = new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        subscriber.onNext(Pair.create(context, intent));
                    }
                };

                context.registerReceiver(receiver, intentFilter);

                subscriber.add(Subscriptions.create(new Action0() {

                    @Override
                    public void call() {
                        // this line is called when the subscribers un-subscribe this Observable
                        context.unregisterReceiver(receiver);
                    }
                }));

            }
        });
    }

    /**
     * Get the context in {@code Pair<Context, Intent>}.
     * Use like {@code fst}.
     * @return context
     */
    public static Func1<Pair<Context, Intent>, Context> getContext() {
         return new Func1<Pair<Context, Intent>, Context>() {

            @Override
            public Context call(Pair<Context, Intent> pair) {
                return pair.first;
            }
        };
    }

    /**
     *
     * Get the context in {@code Pair<Context, Intent>}.
     * Use like {@code snd}.
     * @return context
     */
    public static Func1<Pair<Context, Intent>, Intent> getIntent() {
        return new Func1<Pair<Context, Intent>, Intent>() {

            @Override
            public Intent call(Pair<Context, Intent> pair) {
                return pair.second;
            }
        };
    }


}
