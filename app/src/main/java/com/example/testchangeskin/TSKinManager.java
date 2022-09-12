package com.example.testchangeskin;

import android.content.Context;

import com.example.skin.SkinManager;

public class TSKinManager {
    public static final int SKIN_BLUE = 1;
    public static final int SKIN_DARK = 2;
    public static final int SKIN_WHITE = 3;

    public static void install(Context context) {
        SkinManager skinManager = SkinManager.defaultInstance(context);
        skinManager.addSkin(SKIN_BLUE, R.style.app_skin_blue);
        skinManager.addSkin(SKIN_DARK, R.style.app_skin_dark);
        skinManager.addSkin(SKIN_WHITE, R.style.app_skin_white);

        skinManager.changeSkin(SKIN_DARK);
    }

    public static void changeSkin(int index) {
        SkinManager.defaultInstance(AppApplication.getContext()).changeSkin(index);
    }
}
