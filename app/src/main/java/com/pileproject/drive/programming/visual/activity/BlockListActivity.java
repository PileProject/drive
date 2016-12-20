/**
 * Copyright (C) 2011-2016 PILE Project, Inc. <dev@pileproject.com>
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
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.pileproject.drive.R;
import com.pileproject.drive.app.DriveApplication;
import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.BlockCategory;
import com.pileproject.drive.programming.visual.block.BlockProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * An Activity that shows a list of blocks as a dialog interface.
 */
public class BlockListActivity extends Activity {

    public static final String KEY_BLOCK_CATEGORY = "block-list-activity-key-category";

    public static final String KEY_BLOCK_NAME = "block-list-activity-key-block-name";

    private static final int[] CATEGORY_STRINGS = {
            R.string.programming_sequence,
            R.string.programming_repetition,
            R.string.programming_selection
    };

    @Inject
    public BlockProvider mBlockProvider;

    @BlockCategory
    private int mCategory;

    private List<BlockClassHolder> mBlockClassHolders;

    /**
     * Returns an intent having appropriate extras for invoking this Activity.
     * Use this method to create an intent for this Activity.
     *
     * @param context context that will be passed to the constructor of {@link Intent}
     * @param blockCategory the Activity will display this category of blocks in its dialog
     * @return intent
     */
    public static Intent createIntent(Context context, @BlockCategory int blockCategory) {

        Intent intent = new Intent(context, BlockListActivity.class);
        intent.putExtra(KEY_BLOCK_CATEGORY, blockCategory);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_block_list);

        inject();

        setResult(Activity.RESULT_CANCELED); // set the default result as "canceled"

        // noinspection WrongConstant Safe;
        mCategory = getIntent().getIntExtra(KEY_BLOCK_CATEGORY, BlockCategory.SEQUENCE);

        mBlockClassHolders = createBlockClassHoldersByCategory(mCategory);

        // show title based on the category
        setTitle(getString(R.string.blockList_label) + getString(CATEGORY_STRINGS[mCategory]));

        setUpViews();
    }

    private void inject() {
        ((DriveApplication) getApplication()).getAppComponent().inject(this);
    }

    private void setUpViews() {
        GridView gridview = (GridView) findViewById(R.id.blockList_screen);
        gridview.setAdapter(new BlockIconAdapter(this, R.layout.view_block_icon, mBlockClassHolders));
        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                BlockClassHolder selectedBlock = mBlockClassHolders.get(position);

                Intent data = new Intent();
                data.putExtra(KEY_BLOCK_CATEGORY, mCategory);
                data.putExtra(KEY_BLOCK_NAME, selectedBlock.getBlockName());

                // set the result as "ok" and return the data
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });

        findViewById(R.id.blockList_helpButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                final int[] helpImages = {
                        R.drawable.help_sequence, R.drawable.help_repetition, R.drawable.help_selection,
                };

                // create a view witch has a help image
                LayoutInflater inflater = LayoutInflater.from(BlockListActivity.this);
                View view = inflater.inflate(R.layout.view_help, null);

                // choose a help image based on the category
                ImageView help = (ImageView) view.findViewById(R.id.help_showHelpImage);
                help.setImageResource(helpImages[mCategory]);
                help.setAdjustViewBounds(true);
                help.setScaleType(ScaleType.FIT_CENTER);

                // create an AlertDialog that shows helps
                new AlertDialog.Builder(BlockListActivity.this)
                        .setTitle(R.string.blockList_howToUseBlock)
                        .setMessage(getString(CATEGORY_STRINGS[mCategory]) + getString(R.string.blockList_theseBlocksAreUsedLikeThis))
                        .setView(view)
                        .setPositiveButton(R.string.close, null)
                        .show();
            }
        });

        findViewById(R.id.blockList_cancelButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    private List<BlockClassHolder> createBlockClassHoldersByCategory(@BlockCategory int category) {

        List<Class<? extends BlockBase>> blockClasses;

        switch (category) {
            case BlockCategory.SEQUENCE: {
                blockClasses = mBlockProvider.getSequenceBlockClasses();
                break;
            }

            case BlockCategory.REPETITION: {
                blockClasses = mBlockProvider.getRepetitionBlockClasses();
                break;
            }

            case BlockCategory.SELECTION: {
                blockClasses = mBlockProvider.getSelectionBlockClasses();
                break;
            }

            default: {
                throw new IllegalArgumentException("Illegal category: " + category);
            }
        }

        List<BlockClassHolder> blockClassHolders = new ArrayList<>();

        for (Class<? extends BlockBase> blockClass : blockClasses) {
            blockClassHolders.add(new BlockClassHolder(blockClass));
        }

        return blockClassHolders;
    }

    /**
     * A holder class that has a block data as a class.
     */
    private class BlockClassHolder {

        private final Class<? extends BlockBase> mClass;

        BlockClassHolder(Class<? extends BlockBase> clazz) {
            mClass = clazz;
        }

        @StringRes
        int getDescriptionStringRes() {
            String className = mClass.getSimpleName();
            StringBuilder resourceName = new StringBuilder(className);

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
            return getResources().getIdentifier(resourceName.toString(), "string", getPackageName());
        }

        String getBlockName() {
            return mClass.getName();
        }

        @DrawableRes
        int getIconDrawableRes() {
            String className = mClass.getSimpleName();

            // TODO: consider using CaseFormat in Google Guava
            // https://github.com/google/guava/wiki/StringsExplained#caseformat

            // convert the class name from CamelCase to snake_case
            // e.g., "ForwardSecBlock" -> "forward_sec_block"
            String snake = className.replaceAll("([A-Z0-9]+)([A-Z][a-z])", "$1_$2")
                                    .replaceAll("([a-z])([A-Z])", "$1_$2");
            StringBuilder resourceName = new StringBuilder(snake.toLowerCase(Locale.getDefault()));

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
     * An adapter class that has icons.
     */
    private static class BlockIconAdapter extends ArrayAdapter<BlockClassHolder> {
        private final LayoutInflater mInflater;
        private final int mLayoutId;

        /**
         * @param context  the context of the Activity that calls this adapter
         * @param layoutId the resource id of the icon view
         * @param objects  sets of Icon data
         */
        public BlockIconAdapter(Context context, @LayoutRes int layoutId, List<BlockClassHolder> objects) {
            super(context, 0, objects);
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mLayoutId = layoutId;
        }

        @Override @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
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
            holder.description.setText(data.getDescriptionStringRes());
            holder.image.setImageResource(data.getIconDrawableRes());
            return convertView;
        }

        /**
         * An icon holder that has a TextView and an ImageView.
         */
        class IconHolder {
            TextView description;
            ImageView image;
        }
    }
}
