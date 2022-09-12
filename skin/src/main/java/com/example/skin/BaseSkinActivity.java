package com.example.skin;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.LayoutInflaterCompat;
import androidx.lifecycle.Lifecycle;

public class BaseSkinActivity extends AppCompatActivity {

    private SkinManager mSkinManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (useSkinLayoutInflaterFactory()) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            LayoutInflaterCompat.setFactory2(layoutInflater,
                    new SkinLayoutInflaterFactory(this, layoutInflater));
        }
        super.onCreate(savedInstanceState);
    }

    protected boolean useSkinLayoutInflaterFactory() {
        return true;
    }

    public SkinManager getSkinManager() {
        return mSkinManager;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSkinManager != null) {
            mSkinManager.register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mSkinManager != null) {
            mSkinManager.unRegister(this);
        }
    }

    public void setSkinManager(@Nullable SkinManager skinManager) {
        if (mSkinManager != null) {
            mSkinManager.unRegister(this);
        }
        mSkinManager = skinManager;
        if (skinManager != null) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                skinManager.register(this);
            }
        }
    }
}
