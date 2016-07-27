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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.pileproject.drive.R;
import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.BlockFactory;

import java.util.Locale;

/**
 * An Activity that shows a list of blocks as a dialog interface
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 18-June-2013
 */
public abstract class BlockListActivityBase extends Activity {
    private final int mCategoryTexts[] = {R.string.sequence, R.string.repetition, R.string.selection,};
    private final int mHelpImages[] = {
            R.drawable.help_sequence, R.drawable.help_repetition, R.drawable.help_selection,
    };
    private int mCategory = 0;
    private BlockClassHolder[][] mBlocks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_block_list);

        setResult(Activity.RESULT_CANCELED); // set the default result as "canceled"

        Intent intent = getIntent();
        mCategory = intent.getIntExtra("category", BlockFactory.SEQUENCE);

        // show title based on the category
        setTitle(getString(R.string.blockList_label) + getString(mCategoryTexts[mCategory]));

        mBlocks = getBlockIcons();

        setUpViews();
    }

    private void setUpViews() {
        GridView gridview = (GridView) findViewById(R.id.blockList_screen);
        gridview.setAdapter(new BlockIconAdapter(this, R.layout.view_block_icon, mBlocks[mCategory]));
        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent data = new Intent();
                // repetitionBreakBlock should be created in the same way in which a SequenceBlock is created
                if (mBlocks[mCategory][position].isRepetitionBreakBlock()) {
                    data.putExtra("how_to_make", BlockFactory.SEQUENCE);
                } else {
                    data.putExtra("how_to_make", mCategory);
                }
                data.putExtra("block_name", mBlocks[mCategory][position].getBlockName());

                // set the result as "ok" and return the data
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });

        Button helpButton = (Button) findViewById(R.id.blockList_cancelButton);
        helpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a view witch has a help image
                LayoutInflater inflater = LayoutInflater.from(BlockListActivityBase.this);
                View view = inflater.inflate(R.layout.view_help, null);

                // choose a help image based on the category
                ImageView help = (ImageView) view.findViewById(R.id.help_showHelpImage);
                help.setImageResource(mHelpImages[mCategory]);
                help.setAdjustViewBounds(true);
                help.setScaleType(ScaleType.FIT_CENTER);

                // create an AlertDialog that shows helps
                new AlertDialog.Builder(BlockListActivityBase.this)
                        .setTitle(R.string.blockList_howToUseBlock)
                        .setMessage(getString(mCategoryTexts[mCategory]) + getString(R.string.blockList_theseBlocksAreUsedLikeThis))
                        .setView(view)
                        .setPositiveButton(R.string.close, null)
                        .show();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.blockList_cancelButton);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

    }

    protected abstract BlockClassHolder[][] getBlockIcons();

    /**
     * A holder class that has a block data as a class
     */
    protected class BlockClassHolder {
        private Class<? extends BlockBase> mClass;

        public BlockClassHolder(Class<? extends BlockBase> clazz) {
            mClass = clazz;
        }

        public String getDescription() {
            String className = mClass.getSimpleName();
            StringBuffer resourceName = new StringBuffer(className);

            // remove "Block"
            // e.g., "ForwardSecBlock" -> "ForwardSec"
            int nameLength = className.length();
            resourceName.replace(nameLength - "Block".length(), nameLength, "");

            // convert the initial character from upper case to lower case
            // e.g., "ForwardSec" -> "forwardSec"
            resourceName.setCharAt(0, className.toLowerCase(Locale.getDefault()).charAt(0));

            // add a prefix "blocks."
            // e.g., "forwardSec" -> "blocks.forwardSec"
            // NOTE: "blocks_" does not work as a resource id in this case
            resourceName.insert(0, "blocks.");

            // get the description of this class
            return getString(getResources().getIdentifier(resourceName.toString(), "string", getPackageName()));
        }

        public String getBlockName() {
            return mClass.getName();
        }

        public boolean isRepetitionBreakBlock() {
            return mClass.getSimpleName().equals("RepetitionBreakBlock");
        }

        public int getIconResourceId() {
            String className = mClass.getSimpleName();

            // convert the class name from CamelCase to snake_case
            // e.g., "ForwardSecBlock" -> "forward_sec_block"
            String snake = className.replaceAll("([A-Z0-9]+)([A-Z][a-z])", "$1_$2")
                                    .replaceAll("([a-z])([A-Z])", "$1_$2");
            StringBuffer resourceName = new StringBuffer(snake.toLowerCase(Locale.getDefault()));

            // remove "_block"
            // e.g., "forward_sec_block" -> "forward_sec"
            int nameLength = resourceName.length();
            resourceName.replace(nameLength - "_block".length(), nameLength, "");

            // add a prefix "icon_"
            // e.g., "forward_sec" -> "icon_forward_sec"
            resourceName.insert(0, "icon_");

            // get the image of this class
            return getResources().getIdentifier(resourceName.toString(), "drawable", getPackageName());
        }
    }

    /**
     * An adapter class that has icons
     */
    protected class BlockIconAdapter extends ArrayAdapter<BlockClassHolder> {
        private LayoutInflater mInflater;
        private int mLayoutId;

        /**
         * Constructor
         *
         * @param context  the context of the Activity that calls this adapter
         * @param layoutId the resource id of the icon view
         * @param objects  sets of Icon data
         */
        public BlockIconAdapter(Context context, int layoutId, BlockClassHolder[] objects) {
            super(context, 0, objects);
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mLayoutId = layoutId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            IconHolder holder;

            // reuse view as much as possible
            if (convertView == null) {
                convertView = mInflater.inflate(mLayoutId, parent, false);
                holder = new IconHolder();
                holder.description = (TextView) convertView.findViewById(R.id.blockIcon_blockText);
                holder.image = (ImageView) convertView.findViewById(R.id.blockIcon_blockImage);
                convertView.setTag(holder);
            } else {
                holder = (IconHolder) convertView.getTag();
            }
            BlockClassHolder data = getItem(position);
            holder.description.setText(data.getDescription());
            holder.image.setImageResource(data.getIconResourceId());
            return convertView;
        }

        // an icon holder that has a TextView and an ImageView
        class IconHolder {
            TextView description;
            ImageView image;
        }
    }

}