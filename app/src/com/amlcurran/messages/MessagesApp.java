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

package com.amlcurran.messages;

import android.app.Application;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.util.Linkify;

import com.amlcurran.messages.core.events.Broadcast;
import com.amlcurran.messages.events.BroadcastEventBus;
import com.amlcurran.messages.events.BroadcastEventSubscriber;
import com.amlcurran.messages.events.EventBus;
import com.amlcurran.messages.loaders.ExecutorMessagesLoader;
import com.amlcurran.messages.loaders.MemoryMessagesCache;
import com.amlcurran.messages.loaders.MessagesCache;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.notifications.Notifier;
import com.amlcurran.messages.preferences.PreferenceStore;
import com.amlcurran.messages.reporting.NullStatReporter;
import com.amlcurran.messages.reporting.StatReporter;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MessagesApp extends Application implements BroadcastEventSubscriber.Listener {

    private BroadcastEventSubscriber subscriber;
    private MessagesCache cache;
    MessagesLoader loader;
    Notifier notifier;
    EventBus eventBus;
    StatReporter statsReporter;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(R.attr.fontPath);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        ExecutorService executor = Executors.newCachedThreadPool();
        Handler uiHandler = new Handler(getMainLooper());

        cache = new MemoryMessagesCache();
        notifier = new Notifier(this);
        eventBus = new BroadcastEventBus(this);
        loader = new ExecutorMessagesLoader(this, executor, cache, eventBus, uiHandler);
        subscriber = new BroadcastEventSubscriber(this, this);
        subscriber.startListening(
                new Broadcast(BroadcastEventBus.BROADCAST_MESSAGE_SENT, null),
                new Broadcast(BroadcastEventBus.BROADCAST_MESSAGE_RECEIVED, null),
                new Broadcast(BroadcastEventBus.BROADCAST_LIST_INVALIDATED, null));
        statsReporter = new NullStatReporter();
        primeZygote(executor);

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyFlashScreen()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                    .build());
        }

    }

    private void primeZygote(ExecutorService executor) {
        executor.submit(new PrimePreferencesTask());
        executor.submit(new PrimeLinkifyTask());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        subscriber.stopListening();
        loader.cancelAll();
    }

    @Override
    public void onMessageReceived() {
        cache.invalidate();
        loader.loadConversationList(null, new PreferenceStore(this).getConversationSort());
    }

    private class PrimeLinkifyTask implements Callable<Object> {

        @Override
        public Object call() throws Exception {
            SpannableStringBuilder builder = new SpannableStringBuilder("911");
            Linkify.addLinks(builder, Linkify.ALL);
            return null;
        }

    }

    private class PrimePreferencesTask implements Callable<Object> {
        @Override
        public Object call() throws Exception {
            PreferenceManager.getDefaultSharedPreferences(MessagesApp.this).getBoolean("test", false);
            return null;
        }
    }
}