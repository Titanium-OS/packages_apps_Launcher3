/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.titanium.launcher.settings;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.ListPreference;

import com.android.launcher3.settings.SettingsIcons;

public class ReloadingListPreference extends ListPreference
        implements SettingsIcons.OnResumePreferenceCallback {
    public interface OnReloadListener {
        void updateList(ListPreference pref);
    }

    private OnReloadListener mOnReloadListener;

    public ReloadingListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ReloadingListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ReloadingListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReloadingListPreference(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        loadEntries();
        super.onClick();
    }

    public void setOnReloadListener(OnReloadListener onReloadListener) {
        mOnReloadListener = onReloadListener;
        loadEntries();

    }
    @Override
    public void onResume() {
        loadEntries();
    }

    private void loadEntries() {
        if (mOnReloadListener != null) {
            mOnReloadListener.updateList(this);
        }
    }
}
