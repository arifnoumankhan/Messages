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

package com.amlcurran.messages.conversationlist;

import com.amlcurran.messages.UpdateNotificationListener;
import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.preferences.PreferenceStore;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.notifications.Notifier;

import java.util.ArrayList;
import java.util.List;

public class ConversationList {

    private final MessagesLoader messagesLoader;
    private final Notifier notifier;
    private final PreferenceStore preferenceStore;
    private final List<Callbacks> callbacksList = new ArrayList<Callbacks>();
    private final List<Conversation> conversationList = new ArrayList<Conversation>();
    private LoadingState state;

    public ConversationList(MessagesLoader messagesLoader, Notifier notifier, PreferenceStore preferenceStore) {
        this.messagesLoader = messagesLoader;
        this.notifier = notifier;
        this.preferenceStore = preferenceStore;
        this.state = LoadingState.INITIAL_LOAD;
        this.preferenceStore.listenToPreferenceChanges(new PokeCallbacksListener());
    }

    public void addCallbacks(Callbacks callbacks) {
        loadIfFirstAttach();
        callbacksList.add(callbacks);
        updateCallback(callbacks);
    }

    private void loadIfFirstAttach() {
        if (state == LoadingState.INITIAL_LOAD) {
            reloadConversations();
        }
    }

    public void removeCallbacks(Callbacks callbacks) {
        callbacksList.remove(callbacks);
    }

    private void updateCallback(Callbacks callbacks) {
        switch (state) {

            case INITIAL_LOAD:
                callbacks.listLoading();
                break;

            case LOADED:
                callbacks.listLoaded(conversationList);
                break;

            case INVALIDATED:
                callbacks.listInvalidated(conversationList);

        }
    }

    public void reloadConversations() {
        if (state != LoadingState.INITIAL_LOAD) {
            state = LoadingState.INVALIDATED;
        }
        for (Callbacks callbacks : callbacksList) {
            updateCallback(callbacks);
        }
        messagesLoader.loadConversationList(new ConversationListListener() {
            @Override
            public void onConversationListLoaded(List<Conversation> conversations) {
                state = LoadingState.LOADED;
                new UpdateNotificationListener(notifier).onConversationListLoaded(conversations);
                for (Callbacks callbacks : callbacksList) {
                    callbacks.listLoaded(conversations);
                }
                updateInternalList(conversations);
            }
        }, preferenceStore.getConversationSort());
    }

    private void updateInternalList(List<Conversation> conversations) {
        conversationList.clear();
        conversationList.addAll(conversations);
    }

    public interface Callbacks {
        void listLoading();

        void listLoaded(List<Conversation> conversations);

        void listInvalidated(List<Conversation> invalidatedList);
    }

    private enum LoadingState {
        INITIAL_LOAD, LOADED, INVALIDATED
    }

    private class PokeCallbacksListener implements PreferenceStore.PreferenceChangedListener {
        @Override
        public void preferenceChanged(String key) {
            for (Callbacks callbacks : callbacksList) {
                updateCallback(callbacks);
            }
        }
    }
}
