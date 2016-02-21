/*
 * Copyright (C) 2011-2015 PILE Project, Inc. <dev@pileproject.com>
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

package com.pileproject.drive.programming.visual.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pileproject.drive.R;

/**
 * An adapter that is used to show icons and descriptions
 * on GridView
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 4-June-2013
 */
public class BlockIconAdapter extends ArrayAdapter<BlockListActivityBase.BlockClassHolder> {
    private LayoutInflater mInflater;
    private int mLayoutId;

    /**
     * Constructor
     *
     * @param context  The context of Activity that calls this manager
     * @param layoutId The resource id of layout
     * @param objects  The data sets of BindData
     */
    public BlockIconAdapter(Context context,
                            int layoutId,
                            BlockListActivityBase.BlockClassHolder[] objects) {
        super(context, 0, objects);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mLayoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IconImageHolder holder;

        // Reuse as much as possible
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutId, parent, false);
            holder = new IconImageHolder();
            holder.blockDescription = (TextView) convertView.findViewById(R.id.blockIcon_blockText);
            holder.icon = (ImageView) convertView.findViewById(R.id.blockIcon_blockImage);
            convertView.setTag(holder);
        } else {
            holder = (IconImageHolder) convertView.getTag();
        }
        BlockListActivityBase.BlockClassHolder data = getItem(position);
        holder.blockDescription.setText(data.getDescription());
        holder.icon.setImageResource(data.getIconResourceId());
        return convertView;
    }

    // View Holder that has a TextView and an ImageView
    static class IconImageHolder {
        TextView blockDescription;
        ImageView icon;
    }
}