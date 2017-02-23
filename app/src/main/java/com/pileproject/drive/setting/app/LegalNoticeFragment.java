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

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;

import com.pileproject.drive.R;

/**
 * A fragment for showing the legal notice. This fragment will be used by {@link LegalNoticePreference}.
 */
public class LegalNoticeFragment extends DialogFragment {
    private WebView mLegalNoticePage;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setTitle(R.string.setting_legalNotice);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_webview, container, false);
        mLegalNoticePage = (WebView) v.findViewById(R.id.plain_html);

        // load a legal notice page located in assets
        String htmlPath = "file:///android_asset/legal_notice.html";
        mLegalNoticePage.loadUrl(htmlPath);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resizeDialog();
    }

    /**
     * This function should be called in {@link DialogFragment#onActivityCreated(Bundle)}.
     * Otherwise, the dialog size will never be changed.
     */
    private void resizeDialog() {
        Dialog dialog = getDialog();

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        // resize window large enough to display list views
        int dialogWidth = (int) (metrics.widthPixels * 0.9);
        int dialogHeight = (int) (metrics.heightPixels * 0.9);

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = dialogWidth;
        lp.height = dialogHeight;
        dialog.getWindow().setAttributes(lp);
    }
}
