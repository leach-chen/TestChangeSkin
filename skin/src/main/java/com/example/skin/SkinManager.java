package com.example.skin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Trace;
import android.text.Spanned;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SimpleArrayMap;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.skin.defaultAttr.SkinDefaultAttrProvider;
import com.example.skin.handler.ISkinRuleHandler;
import com.example.skin.handler.SkinRuleBackgroundHandler;
import com.example.skin.handler.SkinRuleTextColorHandler;
import com.example.skin.utils.LangHelper;
import com.example.skin.utils.ResHelper;
import com.example.skin.utils.UILog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SkinManager {
    private static final String TAG = "SkinManager";
    private static final String DEFAULT_NAME = "default";
    private static ArrayMap<String, SkinManager> sInstances = new ArrayMap<>();
    private static final String[] EMPTY_ITEMS = new String[]{};
    public static final int DEFAULT_SKIN = -1;
    public static final DispatchListenStrategySelector DEFAULT_DISPATCH_LISTEN_STRATEGY_SELECTOR = new DispatchListenStrategySelector() {
        @NonNull
        @Override
        public DispatchListenStrategy select(@NonNull ViewGroup viewGroup) {
            if (viewGroup instanceof RecyclerView ||
                    viewGroup instanceof ViewPager ||
                    viewGroup instanceof AdapterView) {
                return DispatchListenStrategy.LISTEN_ON_HIERARCHY_CHANGE;
            }
            return DispatchListenStrategy.LISTEN_ON_LAYOUT;
        }
    };
    private static DispatchListenStrategySelector sDispatchListenStrategySelector = DEFAULT_DISPATCH_LISTEN_STRATEGY_SELECTOR;

    public static void setDispatchListenStrategySelector(DispatchListenStrategySelector dispatchListenStrategySelector) {
        if (dispatchListenStrategySelector == null) {
            sDispatchListenStrategySelector = DEFAULT_DISPATCH_LISTEN_STRATEGY_SELECTOR;
        } else {
            sDispatchListenStrategySelector = dispatchListenStrategySelector;
        }
    }

    private static ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener = new ViewGroup.OnHierarchyChangeListener() {
        @Override
        public void onChildViewAdded(View parent, View child) {
            ViewSkinCurrent current = getViewSkinCurrent(parent);
            if (current != null) {
                ViewSkinCurrent childTheme = getViewSkinCurrent(child);
                if (!current.equals(childTheme)) {
                    of(current.managerName, child.getContext()).dispatch(child, current.index);
                }
            }
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {

        }
    };

    // Actually, ViewGroup.OnHierarchyChangeListener is a better choice, but it only has a setter.
    // Add child will trigger onLayoutChange
    private static View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() {

        @Override
        public void onLayoutChange(
                View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (v instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) v;
                int childCount = viewGroup.getChildCount();
                if (childCount > 0) {
                    ViewSkinCurrent current = getViewSkinCurrent(viewGroup);
                    if (current != null) {
                        View child;
                        for (int i = 0; i < childCount; i++) {
                            child = viewGroup.getChildAt(i);
                            ViewSkinCurrent childTheme = getViewSkinCurrent(child);
                            if (!current.equals(childTheme)) {
                                of(current.managerName, child.getContext()).dispatch(child, current.index);
                            }
                        }
                    }
                }
            }
        }
    };



    private String mName;
    private Resources mResources;
    private String mPackageName;
    private SparseArray<SkinItem> mSkins = new SparseArray<>();
    private static HashMap<Integer, Resources.Theme> sStyleIdThemeMap = new HashMap<>();
    private static HashMap<String, ISkinRuleHandler> sRuleHandlers = new HashMap<>();
    private boolean mIsInSkinChangeDispatch = false;

    static {
        sRuleHandlers.put(SkinValueBuilder.BACKGROUND, new SkinRuleBackgroundHandler());
        sRuleHandlers.put(SkinValueBuilder.TEXT_COLOR, new SkinRuleTextColorHandler());
    }

    private int mCurrentSkin = DEFAULT_SKIN;
    private final List<WeakReference<?>> mSkinObserverList = new ArrayList<>();
    private final List<OnSkinChangeListener> mSkinChangeListeners = new ArrayList<>();

    public SkinManager(String name, Resources resources, String packageName) {
        mName = name;
        mResources = resources;
        mPackageName = packageName;
    }

    @MainThread
    public static SkinManager defaultInstance(Context context) {
        context = context.getApplicationContext();
        return of(DEFAULT_NAME, context.getResources(), context.getPackageName());
    }

    @MainThread
    public static SkinManager of(String name, Resources resources, String packageName) {
        SkinManager instance = sInstances.get(name);
        if (instance == null) {
            instance = new SkinManager(name, resources, packageName);
            sInstances.put(name, instance);
        }
        return instance;
    }

    @MainThread
    public static SkinManager of(String name, Context context) {
        context = context.getApplicationContext();
        return of(name, context.getResources(), context.getPackageName());
    }


    //==============================================================================================

    @MainThread
    public void addSkin(int index, int styleRes) {
        if (index <= 0) {
            throw new IllegalArgumentException("index must greater than 0");
        }
        SkinItem skinItem = mSkins.get(index);
        if (skinItem != null) {
            if (skinItem.getStyleRes() == styleRes) {
                return;
            }
            throw new RuntimeException("already exist the theme item for " + index);
        }
        skinItem = new SkinItem(styleRes);
        mSkins.append(index, skinItem);
    }


    public void dispatch(View view, int skinIndex) {
        if (view == null) {
            return;
        }
        if (false) {
            Trace.beginSection("Skin::dispatch");
        }
        SkinItem skinItem = mSkins.get(skinIndex);
        Resources.Theme theme;
        if (skinItem == null) {
            if (skinIndex != DEFAULT_SKIN) {
                throw new IllegalArgumentException("The skin " + skinIndex + " does not exist");
            }
            theme = view.getContext().getTheme();
        } else {
            theme = skinItem.getTheme();
        }
        runDispatch(view, skinIndex, theme);
        if (false) {
            Trace.endSection();
        }
    }

    private void runDispatch(@NonNull View view, int skinIndex, Resources.Theme theme) {
        ViewSkinCurrent currentTheme = getViewSkinCurrent(view);
        if (currentTheme != null && currentTheme.index == skinIndex && Objects.equals(currentTheme.managerName, mName)) {
            return;
        }
        view.setTag(R.id.skin_current, new ViewSkinCurrent(mName, skinIndex));

        if (view instanceof ISkinDispatchInterceptor) {
            if (((ISkinDispatchInterceptor) view).intercept(skinIndex, theme)) {
                return;
            }
        }

        Object interceptTag = view.getTag(R.id.skin_intercept_dispatch);
        if (interceptTag instanceof Boolean && ((Boolean) interceptTag)) {
            return;
        }

        Object ignoreApplyTag = view.getTag(R.id.skin_ignore_apply);
        boolean ignoreApply = ignoreApplyTag instanceof Boolean && ((Boolean) ignoreApplyTag);
        if (!ignoreApply) {
            applyTheme(view, skinIndex, theme);
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            if (sDispatchListenStrategySelector.select(viewGroup) == DispatchListenStrategy.LISTEN_ON_HIERARCHY_CHANGE) {
                viewGroup.setOnHierarchyChangeListener(mOnHierarchyChangeListener);
            } else {
                viewGroup.addOnLayoutChangeListener(mOnLayoutChangeListener);
            }
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                runDispatch(viewGroup.getChildAt(i), skinIndex, theme);
            }
        } else if (!ignoreApply && (view instanceof TextView)) {
            CharSequence text = null;
            if (view instanceof TextView) {
                text = ((TextView) view).getText();
            }
            if (text instanceof Spanned) {
                ISkinHandlerSpan[] spans = ((Spanned) text).getSpans(0, text.length(), ISkinHandlerSpan.class);
                if (spans != null) {
                    for (int i = 0; i < spans.length; i++) {
                        spans[i].handle(view, this, skinIndex, theme);
                    }
                }
                view.invalidate();
            }
        }
    }


    static ViewSkinCurrent getViewSkinCurrent(View view) {
        Object current = view.getTag(R.id.skin_current);
        if (current instanceof ViewSkinCurrent) {
            return (ViewSkinCurrent) current;
        }
        return null;
    }


    void refreshTheme(@NonNull View view, int skinIndex) {
        SkinItem skinItem = mSkins.get(skinIndex);
        if (skinItem != null) {
            applyTheme(view, skinIndex, skinItem.getTheme());
        }
    }

    private void applyTheme(@NonNull View view, int skinIndex, Resources.Theme theme) {
        SimpleArrayMap<String, Integer> attrs = getSkinAttrs(view);
        try {
            if (view instanceof ISkinHandlerView) {
                ((ISkinHandlerView) view).handle(this, skinIndex, theme, attrs);
            } else {
                defaultHandleSkinAttrs(view, theme, attrs);
            }

            Object skinApplyListener = view.getTag(R.id.skin_apply_listener);
            if (skinApplyListener instanceof ISkinApplyListener) {
                ((ISkinApplyListener) skinApplyListener).onApply(view, skinIndex, theme);
            }

            if (view instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) view;
                int itemDecorationCount = recyclerView.getItemDecorationCount();
                for (int i = 0; i < itemDecorationCount; i++) {
                    RecyclerView.ItemDecoration itemDecoration = recyclerView.getItemDecorationAt(i);
                    if (itemDecoration instanceof ISkinHandlerDecoration) {
                        ((ISkinHandlerDecoration) itemDecoration).handle(recyclerView, this, skinIndex, theme);
                    }
                }
            }
        } catch (Throwable throwable) {
            UILog.printErrStackTrace(TAG, throwable,
                    "catch error when apply theme: " + view.getClass().getSimpleName() +
                            "; " + skinIndex + "; attrs = " + (attrs == null ? "null" : attrs.toString()));
        }
    }

    public void defaultHandleSkinAttrs(@NonNull View view, Resources.Theme theme, @Nullable SimpleArrayMap<String, Integer> attrs) {
        if (attrs != null) {
            for (int i = 0; i < attrs.size(); i++) {
                String key = attrs.keyAt(i);
                Integer attr = attrs.valueAt(i);
                if (attr == null) {
                    continue;
                }
                defaultHandleSkinAttr(view, theme, key, attr);
            }
        }
    }

    public void defaultHandleSkinAttr(View view, Resources.Theme theme, String name, int attr) {
        if (attr == 0) {
            return;
        }
        ISkinRuleHandler handler = sRuleHandlers.get(name);
        if (handler == null) {
            UILog.w(TAG, "Do not find handler for skin attr name: " + name);
            return;
        }
        handler.handle(this, view, theme, name, attr);
    }

    @Nullable
    private SimpleArrayMap<String, Integer> getSkinAttrs(View view) {
        String skinValue = (String) view.getTag(R.id.skin_value);
        String[] items;
        if (skinValue == null || skinValue.isEmpty()) {
            items = EMPTY_ITEMS;
        } else {
            items = skinValue.split("[|]");
        }

        SimpleArrayMap<String, Integer> attrs = null;
        if (view instanceof SkinDefaultAttrProvider) {
            SimpleArrayMap<String, Integer> defaultAttrs = ((SkinDefaultAttrProvider) view).getDefaultSkinAttrs();
            if (defaultAttrs != null && !defaultAttrs.isEmpty()) {
                attrs = new SimpleArrayMap<>(defaultAttrs);
            }
        }
        SkinDefaultAttrProvider provider = (SkinDefaultAttrProvider) view.getTag(
                R.id.skin_default_attr_provider);
        if (provider != null) {
            SimpleArrayMap<String, Integer> providedAttrs = provider.getDefaultSkinAttrs();
            if (providedAttrs != null && !providedAttrs.isEmpty()) {
                if (attrs != null) {
                    attrs.putAll(providedAttrs);
                } else {
                    attrs = new SimpleArrayMap<>(providedAttrs);
                }
            }
        }

        if (attrs == null) {
            if (items.length <= 0) {
                return null;
            }
            attrs = new SimpleArrayMap<>(items.length);
        }

        for (String item : items) {
            String[] kv = item.split(":");
            if (kv.length != 2) {
                continue;
            }
            String key = kv[0].trim();
            if (LangHelper.isNullOrEmpty(key)) {
                continue;
            }
            int attr = getAttrFromName(kv[1].trim());
            if (attr == 0) {
                UILog.w(TAG, "Failed to get attr id from name: " + kv[1]);
                continue;
            }
            attrs.put(key, attr);
        }
        return attrs;
    }

    public int getAttrFromName(String attrName) {
        return mResources.getIdentifier(attrName, "attr", mPackageName);
    }


    // =====================================================================================

    public void register(@NonNull Activity activity) {
        if (!containSkinObserver(activity)) {
            mSkinObserverList.add(new WeakReference<>(activity));
        }
        dispatch(activity.findViewById(Window.ID_ANDROID_CONTENT), mCurrentSkin);
    }

    public void unRegister(@NonNull Activity activity) {
        removeSkinObserver(activity);
    }

    public void register(@NonNull Fragment fragment) {
        if (!containSkinObserver(fragment)) {
            mSkinObserverList.add(new WeakReference<>(fragment));
        }
        dispatch(fragment.getView(), mCurrentSkin);
    }

    public void unRegister(@NonNull Fragment fragment) {
        removeSkinObserver(fragment);
    }

    public void register(@NonNull View view) {
        if (!containSkinObserver(view)) {
            mSkinObserverList.add(new WeakReference<>(view));
        }
        dispatch(view, mCurrentSkin);
    }

    public void unRegister(@NonNull View view) {
        removeSkinObserver(view);
    }

    public void register(@NonNull Dialog dialog) {
        if (!containSkinObserver(dialog)) {
            mSkinObserverList.add(new WeakReference<>(dialog));
        }
        Window window = dialog.getWindow();
        if (window != null) {
            dispatch(window.getDecorView(), mCurrentSkin);
        }
    }

    public void unRegister(@NonNull Dialog dialog) {
        removeSkinObserver(dialog);
    }

    public void register(@NonNull PopupWindow popupWindow) {
        if (!containSkinObserver(popupWindow)) {
            mSkinObserverList.add(new WeakReference<>(popupWindow));
        }
        dispatch(popupWindow.getContentView(), mCurrentSkin);
    }

    public void unRegister(@NonNull PopupWindow popupWindow) {
        removeSkinObserver(popupWindow);
    }

    public void register(@NonNull Window window) {
        if (!containSkinObserver(window)) {
            mSkinObserverList.add(new WeakReference<>(window));
        }
        dispatch(window.getDecorView(), mCurrentSkin);
    }

    public void unRegister(@NonNull Window window) {
        removeSkinObserver(window);
    }

    private boolean containSkinObserver(Object object) {
        //reverse order for remove
        for (int i = mSkinObserverList.size() - 1; i >= 0; i--) {
            Object item = mSkinObserverList.get(i).get();
            if (item == object) {
                return true;
            } else if (item == null) {
                mSkinObserverList.remove(i);
            }
        }
        return false;
    }

    private void removeSkinObserver(Object object) {
        for (int i = mSkinObserverList.size() - 1; i >= 0; i--) {
            Object item = mSkinObserverList.get(i).get();
            if (item == object) {
                mSkinObserverList.remove(i);
                return;
            } else if (item == null) {
                mSkinObserverList.remove(i);
            }
        }
    }

    @MainThread
    public void changeSkin(int index) {
        if (mCurrentSkin == index) {
            return;
        }
        int oldIndex = mCurrentSkin;
        mCurrentSkin = index;
        mIsInSkinChangeDispatch = true;
        for (int i = mSkinObserverList.size() - 1; i >= 0; i--) {
            Object item = mSkinObserverList.get(i).get();
            if (item == null) {
                mSkinObserverList.remove(i);
            } else {
                if (item instanceof Activity) {
                    Activity activity = (Activity) item;
                    activity.getWindow().setBackgroundDrawable(ResHelper.getAttrDrawable(
                            activity, mSkins.get(index).getTheme(), R.attr.skin_support_activity_background));
                    dispatch(activity.findViewById(Window.ID_ANDROID_CONTENT), index);
                } else if (item instanceof Fragment) {
                    dispatch(((Fragment) item).getView(), index);
                } else if (item instanceof Dialog) {
                    Window window = ((Dialog) item).getWindow();
                    if (window != null) {
                        dispatch(window.getDecorView(), index);
                    }
                } else if (item instanceof PopupWindow) {
                    dispatch(((PopupWindow) item).getContentView(), index);
                } else if (item instanceof Window) {
                    dispatch(((Window) item).getDecorView(), index);
                } else if (item instanceof View) {
                    dispatch((View) item, index);
                }
            }
        }

        for (int i = mSkinChangeListeners.size() - 1; i >= 0; i--) {
            OnSkinChangeListener item = mSkinChangeListeners.get(i);
            item.onSkinChange(this, oldIndex, mCurrentSkin);
        }
        mIsInSkinChangeDispatch = false;
    }

    class ViewSkinCurrent {
        String managerName;
        int index;

        ViewSkinCurrent(String managerName, int index) {
            this.managerName = managerName;
            this.index = index;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ViewSkinCurrent that = (ViewSkinCurrent) o;
            return index == that.index &&
                    Objects.equals(managerName, that.managerName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(managerName, index);
        }
    }

    class SkinItem {
        private int styleRes;

        SkinItem(int styleRes) {
            this.styleRes = styleRes;
        }

        public int getStyleRes() {
            return styleRes;
        }

        @NonNull
        Resources.Theme getTheme() {
            Resources.Theme theme = sStyleIdThemeMap.get(styleRes);
            if (theme == null) {
                theme = mResources.newTheme();
                theme.applyStyle(styleRes, true);
                sStyleIdThemeMap.put(styleRes, theme);
            }
            return theme;
        }
    }

    public interface DispatchListenStrategySelector {
        @NonNull
        DispatchListenStrategy select(@NonNull ViewGroup viewGroup);
    }

    public enum DispatchListenStrategy {
        LISTEN_ON_LAYOUT,
        LISTEN_ON_HIERARCHY_CHANGE
    }

    public interface OnSkinChangeListener {
        void onSkinChange(SkinManager skinManager, int oldSkin, int newSkin);
    }

}
