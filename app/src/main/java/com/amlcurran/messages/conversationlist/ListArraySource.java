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

import com.espian.utils.data.Source;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListArraySource<T> implements Source<T> {

    private final ArrayList<T> list;

    public ListArraySource() {
        list = new ArrayList<T>();
    }

    public void addAll(Collection<T> conversations) {
        list.addAll(conversations);
    }

    public void clear() {
        list.clear();
    }

    @Override
    public T getAtPosition(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void replace(List<T> conversations) {
        list.clear();
        list.addAll(conversations);
    }
}
