/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amlcurran.messages.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.amlcurran.messages.events.BroadcastManagerEventBus;

public class LocalMessageReceiver extends BroadcastReceiver {

    private Context context;
    private Listener listener;

    public LocalMessageReceiver(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void startListening(String[] actions) {
        LocalBroadcastManager.getInstance(context).registerReceiver(this, buildMessageFilter(actions));
    }

    public void stopListening() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        listener.onMessageReceived();
    }

    private IntentFilter buildMessageFilter(String[] actions) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastManagerEventBus.BROADCAST_MESSAGE_SENT);
        filter.addAction(BroadcastManagerEventBus.BROADCAST_MESSAGE_RECEIVED);
        for (String action : actions) {
            filter.addAction(action);
        }
        return filter;
    }

    public interface Listener {
        void onMessageReceived();
    }

}
