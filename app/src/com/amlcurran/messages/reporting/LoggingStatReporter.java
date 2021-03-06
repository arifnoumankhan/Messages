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

package com.amlcurran.messages.reporting;

import android.app.Activity;

import com.amlcurran.messages.MessagesLog;

public class LoggingStatReporter implements StatReporter {
    @Override
    public void sendUiEvent(String label) {
        MessagesLog.d(this, "UI EVENT : " + label);
    }

    @Override
    public void start(Activity activity) {

    }

    @Override
    public void stop(Activity activity) {

    }

    @Override
    public void sendEvent(String label) {
        MessagesLog.d(this, "MILESTONE EVENT : " + label);
    }
}
