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
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.pileproject.drive.R;
import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.BlockFactory;

import java.util.Locale;

/**
 * An Activity that shows block lists as a dialog
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 18-June-2013
 */
public abstract class BlockListActivityBase extends Activity implements OnClickListener, OnItemClickListener {
    private final int mCategoryText[] = {R.string.sequence, R.string.repetition, R.string.selection,};
    private final int mHelpImage[] = {
            R.drawable.help_sequence, R.drawable.help_repetition, R.drawable.help_selection,
    };
    private Button mCancelButton;
    private Button mHelpButton;
    private int mCategory = 0;
    // Icons
    private BlockClassHolder[][] mBlocks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_block_list);

        // Set result
        setResult(Activity.RESULT_CANCELED);

        // Get category
        Intent intent = getIntent();
        mCategory = intent.getIntExtra("category", BlockFactory.SEQUENCE);

        // Show title
        setTitle(getString(R.string.blockList_label) + getString(mCategoryText[mCategory]));

        mBlocks = getBlockIcons();

        // Initialize GridView
        GridView gridview = (GridView) findViewById(R.id.blockList_screen);
        gridview.setAdapter(new BlockIconAdapter(this, R.layout.view_block_icon, mBlocks[mCategory]));
        gridview.setOnItemClickListener(this);

        findViews();
        mHelpButton.setOnClickListener(this);

        mCancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    protected abstract BlockClassHolder[][] getBlockIcons();

    private void findViews() {
        mHelpButton = (Button) findViewById(R.id.blockList_helpButton);
        mCancelButton = (Button) findViewById(R.id.blockList_cancelButton);
    }

    @Override
    public void onItemClick(
            AdapterView<?> parent, View v, int position, long id) {
        Intent data = new Intent();
        // RepetitionBreakBlock will be created in the same way in which
        // SequenceBlock is created
        if (mBlocks[mCategory][position].isRepetitionBreakBlock()) {
            data.putExtra("how_to_make", BlockFactory.SEQUENCE);
        } else {
            data.putExtra("how_to_make", mCategory);
        }
        data.putExtra("block_name", mBlocks[mCategory][position].getBlockName());

        // Set result OK and return data
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    public void onClick(View v) {
        // Create a view witch has a help image
        LayoutInflater inflater = LayoutInflater.from(BlockListActivityBase.this);
        View view = inflater.inflate(R.layout.view_help, null);
        ImageView help = (ImageView) view.findViewById(R.id.help_showHelpImage);
        help.setImageResource(mHelpImage[mCategory]);
        help.setAdjustViewBounds(true);
        help.setScaleType(ScaleType.FIT_CENTER);

        // Create an AlertDialog that shows helps
        new AlertDialog.Builder(BlockListActivityBase.this).setTitle(R.string.blockList_howToUseBlock)
                .setMessage(
                        getString(mCategoryText[mCategory]) + getString(R.string.blockList_theseBlocksAreUsedLikeThis))
                .setView(view)
                .setPositiveButton(R.string.close, null)
                .show();
    }

    // Data that has class
    protected class BlockClassHolder {
        private Class<? extends BlockBase> mClass;

        public BlockClassHolder(Class<? extends BlockBase> clazz) {
            mClass = clazz;
        }

        public String getDescription() {
            String className = mClass.getSimpleName();
            StringBuffer name = new StringBuffer(className);

            // Get the length of the class name
            int nameLength = className.length();

            // Remove "Block"
            name.replace(nameLength - "Block".length(), nameLength, "");

            // Convert the initial character from upper case to lower case
            name.setCharAt(0, className.toLowerCase(Locale.getDefault()).charAt(0));
            name.insert(0, "blocks."); // Add prefix ("blocks_" does not work)

            return getString(getResources().getIdentifier(name.toString(), "string", getPackageName()));
        }

        public String getBlockName() {
            return mClass.getName();
        }

        public boolean isRepetitionBreakBlock() {
            return mClass.getSimpleName().equals("RepetitionBreakBlock");
        }

        public int getIconResourceId() {
            String className = mClass.getSimpleName();

            // Convert string from CamelCase to snake_case
            String snake =
                    className.replaceAll("([A-Z0-9]+)([A-Z][a-z])", "$1_$2").replaceAll("([a-z])([A-Z])", "$1_$2");
            StringBuffer name = new StringBuffer(snake.toLowerCase(Locale.getDefault()));

            // Get the length of the class name
            int nameLength = name.length();
            // Remove "_block"
            name.replace(nameLength - "_block".length(), nameLength, "");
            name.insert(0, "icon_"); // Add prefix

            return getResources().getIdentifier(name.toString(), "drawable", getPackageName());
        }
    }
}