/**
 * Copyright (C) 2011-2017 The PILE Developers <pile-dev@googlegroups.com>
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
package com.pileproject.drive.setting.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.pileproject.drive.R;

import java.util.List;

/**
 * A list view adapter class for {@link ProgramData}.
 */
public class ProgramDataAdapter extends ArrayAdapter<ProgramData> {
    private LayoutInflater mLayoutInflater;

    public ProgramDataAdapter(Context context, int resourceId, List<ProgramData> objects) {
        super(context, resourceId, objects);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProgramData item = getItem(position);
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.view_program_item, null);
        }

        CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.programList_checkBox);
        checkbox.setText(item.getProgramName());
        return convertView;
    }
}
