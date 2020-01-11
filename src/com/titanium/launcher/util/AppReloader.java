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

package com.titanium.launcher.util;

import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;

import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.util.ComponentKey;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.titanium.launcher.customization.IconDatabase;

public class AppReloader {
    private static AppReloader sInstance;

    public static synchronized AppReloader get(Context context) {
        if (sInstance == null) {
            sInstance = new AppReloader(context);
        }
        return sInstance;
    }

    private final Context mContext;
    private final LauncherModel mModel;
    private final UserManagerCompat mUsers;
    private final DeepShortcutManager mShortcuts;
    private final LauncherAppsCompat mApps;

    private AppReloader(Context context) {
        mContext = context;
        mModel = LauncherAppState.getInstance(context).getModel();
        mUsers = UserManagerCompat.getInstance(context);
        mShortcuts = DeepShortcutManager.getInstance(context);
        mApps = LauncherAppsCompat.getInstance(context);
    }

    public Set<ComponentKey> withIconPack(String iconPack) {
        Set<ComponentKey> reloadKeys = new HashSet<>();
        for (UserHandle user : mUsers.getUserProfiles()) {
            for (LauncherActivityInfo info : mApps.getActivityList(null, user)) {
                ComponentKey key = new ComponentKey(info.getComponentName(), info.getUser());
                if (IconDatabase.getByComponent(mContext, key).equals(iconPack)) {
                    reloadKeys.add(key);
                }
            }
        }
        return reloadKeys;
    }

    public void reload() {
        for (UserHandle user : mUsers.getUserProfiles()) {
            Set<String> pkgsSet = new HashSet<>();
            for (LauncherActivityInfo info : mApps.getActivityList(null, user)) {
                pkgsSet.add(info.getComponentName().getPackageName());
            }
            for (String pkg : pkgsSet) {
                reload(user, pkg);
            }
        }
    }

    public void reload(ComponentKey key) {
        reload(key.user, key.componentName.getPackageName());
    }

    public void reload(Collection<ComponentKey> keys) {
        for (ComponentKey key : keys) {
            reload(key);
        }
    }

    private void reload(UserHandle user, String pkg) {
        mModel.onPackageChanged(pkg, user);
        List<ShortcutInfo> shortcuts = mShortcuts.queryForPinnedShortcuts(pkg, user);
        if (!shortcuts.isEmpty()) {
            mModel.updatePinnedShortcuts(pkg, shortcuts, user);
        }
    }
}
