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
import android.content.res.Resources;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.skin.SkinManager;
import com.example.skin.utils.ResHelper;

import org.jetbrains.annotations.NotNull;

public abstract class SkinRuleColorStateListHandler implements ISkinRuleHandler {
    @Override
    public final void handle(@NotNull SkinManager skinManager, @NotNull View view, @NotNull Resources.Theme theme,
                             @NotNull String name, int attr) {
        handle(view, name, ResHelper.getAttrColorStateList(view.getContext(), theme, attr));
    }

    protected abstract void handle(@NonNull View view,
                                   @NonNull String name,
                                   ColorStateList colorStateList);
}
