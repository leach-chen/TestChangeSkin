/*
 * Tencent is pleased to support the open source community by making QMUI_Android available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.skin;

import android.view.View;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.example.skin.utils.UILog;

public class SkinHelper {

    public static SkinValueBuilder sSkinValueBuilder = SkinValueBuilder.acquire();

    public static void setSkinValue(@NonNull View view, SkinValueBuilder skinValueBuilder) {
        setSkinValue(view, skinValueBuilder.build());
    }

    public static void setSkinValue(@NonNull View view, String value) {
        view.setTag(R.id.skin_value, value);
        refreshViewSkin(view);
    }

    @MainThread
    public static void setSkinValue(@NonNull View view, SkinWriter writer) {
        writer.write(sSkinValueBuilder);
        setSkinValue(view, sSkinValueBuilder.build());
        sSkinValueBuilder.clear();
    }

    public static void refreshViewSkin(@NonNull View view) {
        SkinManager.ViewSkinCurrent skinCurrent = SkinManager.getViewSkinCurrent(view);
        if (skinCurrent != null) {
            SkinManager.of(skinCurrent.managerName, view.getContext()).refreshTheme(view, skinCurrent.index);
        }
    }

    public static void warnRuleNotSupport(View view, String rule) {
        UILog.w("QMUISkinManager",
                view.getClass().getSimpleName() + " does't support " + rule);
    }

}
