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

package com.amlcurran.messages.loaders;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import java.util.concurrent.Callable;

class ThreadTask implements Callable<Object> {

    private final ContentResolver contentResolver;
    private final String threadId;
    private final Uri contentUri;
    private final CursorLoadListener loadListener;

    public ThreadTask(ContentResolver contentResolver, String threadId, CursorLoadListener loadListener) {
        this(contentResolver, threadId, Telephony.Sms.CONTENT_URI, loadListener);
    }

    public ThreadTask(ContentResolver contentResolver, String threadId, Uri contentUri, CursorLoadListener loadListener) {
        this.contentResolver = contentResolver;
        this.threadId = threadId;
        this.contentUri = contentUri;
        this.loadListener = loadListener;
    }

    @Override
    public Object call() throws Exception {
        String selection = Telephony.Sms.THREAD_ID + "=?";
        String[] selectionArgs = {threadId};
        final Cursor cursor = contentResolver.query(contentUri, null, selection, selectionArgs, Telephony.Sms.DEFAULT_SORT_ORDER.replace("DESC", "ASC"));
        loadListener.onCursorLoaded(cursor);
        return null;
    }
}