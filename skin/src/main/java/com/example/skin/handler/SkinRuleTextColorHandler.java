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
package com.example.skin.handler;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import com.example.skin.SkinHelper;

import org.jetbrains.annotations.NotNull;

public class SkinRuleTextColorHandler extends SkinRuleColorStateListHandler {

    @Override
    protected void handle(@NotNull View view, @NotNull String name, ColorStateList colorStateList) {
        if (colorStateList == null) {
            return;
        }
        /*if (view instanceof TextView) {
            ((TextView) view).setTextColor(colorStateList);
        } else if (view instanceof QMUIQQFaceView) {
            ((QMUIQQFaceView) view).setTextColor(colorStateList.getDefaultColor());
        }else if(view instanceof QMUIProgressBar){
            ((QMUIProgressBar) view).setTextColor(colorStateList.getDefaultColor());
        }else{
            QMUISkinHelper.warnRuleNotSupport(view, name);
        }*/
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(colorStateList);
        } else {
            SkinHelper.warnRuleNotSupport(view, name);
        }
    }
}
