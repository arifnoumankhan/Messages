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

package com.amlcurran.messages.ui.contact;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.conversationlist.ConversationModalMarshall;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.Task;
import com.amlcurran.messages.ui.ViewContactClickListener;

public class DefaultContactView extends LinearLayout implements ContactView {

    private final ImageView contactImageView;
    private final TextView nameTextField;
    private final TextView secondTextField;
    protected Contact contact;
    private Task currentTask;

    public DefaultContactView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultContactView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(LayoutInflater.from(context));
        contactImageView = (ImageView) findViewById(R.id.image);
        contactImageView.setAlpha(0f);
        nameTextField = (TextView) findViewById(android.R.id.text1);
        secondTextField = (TextView) findViewById(android.R.id.text2);
    }

    protected void inflate(LayoutInflater inflater) {
        inflater.inflate(R.layout.view_contact, this, true);
    }

    @Override
    public void setContact(final Contact contact, MessagesLoader loader) {
        cancelCurrentTask();
        this.contact = contact;
        nameTextField.setText(contact.getDisplayName());
        secondTextField.setText(contact.getNumber().flatten());
        currentTask = loader.loadPhoto(contact, new AlphaInSettingListener(contactImageView));
    }

    private void cancelCurrentTask() {
        if (currentTask != null) {
            currentTask.cancel();
        }
    }

    public void setClickToView(ConversationModalMarshall.Callback callback, boolean clickToView) {
        if (clickToView) {
            enableClick(callback);
        } else {
            disableClick();
        }
    }

    private void disableClick() {
        setOnClickListener(null);
    }

    private void enableClick(ContactClickListener callback) {
        setOnClickListener(new ViewContactClickListener(contact, callback));
    }
}