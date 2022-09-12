package com.example.skin;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.LinkedList;

public class SkinValueBuilder {
    public static final String BACKGROUND = "background";
    public static final String TEXT_COLOR = "textColor";
    public static final String HINT_COLOR = "hintColor";
    public static final String SECOND_TEXT_COLOR = "secondTextColor";
    public static final String SRC = "src";
    public static final String BORDER = "border";
    public static final String TOP_SEPARATOR = "topSeparator";
    public static final String BOTTOM_SEPARATOR = "bottomSeparator";
    public static final String RIGHT_SEPARATOR = "rightSeparator";
    public static final String LEFT_SEPARATOR = "LeftSeparator";
    public static final String ALPHA = "alpha";
    public static final String TINT_COLOR = "tintColor";
    public static final String BG_TINT_COLOR = "bgTintColor";
    public static final String PROGRESS_COLOR = "progressColor";
    public static final String TEXT_COMPOUND_TINT_COLOR = "tcTintColor";
    public static final String TEXT_COMPOUND_LEFT_SRC = "tclSrc";
    public static final String TEXT_COMPOUND_RIGHT_SRC = "tcrSrc";
    public static final String TEXT_COMPOUND_TOP_SRC = "tctSrc";
    public static final String TEXT_COMPOUND_BOTTOM_SRC = "tcbSrc";
    public static final String UNDERLINE = "underline";
    public static final String MORE_TEXT_COLOR = "moreTextColor";
    public static final String MORE_BG_COLOR = "moreBgColor";
    private static LinkedList<SkinValueBuilder> sValueBuilderPool;

    public static SkinValueBuilder acquire() {
        if (sValueBuilderPool == null) {
            return new SkinValueBuilder();
        }
        SkinValueBuilder valueBuilder = sValueBuilderPool.poll();
        if (valueBuilder != null) {
            return valueBuilder;
        }
        return new SkinValueBuilder();
    }

    public static void release(@NonNull SkinValueBuilder valueBuilder) {
        valueBuilder.clear();
        if (sValueBuilderPool == null) {
            sValueBuilderPool = new LinkedList<>();
        }
        if (sValueBuilderPool.size() < 2) {
            sValueBuilderPool.push(valueBuilder);
        }
    }

    private SkinValueBuilder() {

    }

    private HashMap<String, String> mValues = new HashMap<>();

    public SkinValueBuilder background(int attr) {
        mValues.put(BACKGROUND, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder background(String attrName) {
        mValues.put(BACKGROUND, attrName);
        return this;
    }

    public SkinValueBuilder underline(int attr) {
        mValues.put(UNDERLINE, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder underline(String attrName) {
        mValues.put(UNDERLINE, attrName);
        return this;
    }

    public SkinValueBuilder moreTextColor(int attr) {
        mValues.put(MORE_TEXT_COLOR, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder moreTextColor(String attrName) {
        mValues.put(MORE_TEXT_COLOR, attrName);
        return this;
    }

    public SkinValueBuilder moreBgColor(int attr) {
        mValues.put(MORE_BG_COLOR, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder moreBgColor(String attrName) {
        mValues.put(MORE_BG_COLOR, attrName);
        return this;
    }

    public SkinValueBuilder textCompoundTintColor(int attr) {
        mValues.put(TEXT_COMPOUND_TINT_COLOR, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder textCompoundTintColor(String attrName) {
        mValues.put(TEXT_COMPOUND_TINT_COLOR, attrName);
        return this;
    }

    public SkinValueBuilder textCompoundTopSrc(int attr) {
        mValues.put(TEXT_COMPOUND_TOP_SRC, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder textCompoundTopSrc(String attrName) {
        mValues.put(TEXT_COMPOUND_TOP_SRC, attrName);
        return this;
    }

    public SkinValueBuilder textCompoundRightSrc(int attr) {
        mValues.put(TEXT_COMPOUND_RIGHT_SRC, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder textCompoundRightSrc(String attrName) {
        mValues.put(TEXT_COMPOUND_RIGHT_SRC, attrName);
        return this;
    }

    public SkinValueBuilder textCompoundBottomSrc(int attr) {
        mValues.put(TEXT_COMPOUND_BOTTOM_SRC, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder textCompoundBottomSrc(String attrName) {
        mValues.put(TEXT_COMPOUND_BOTTOM_SRC, attrName);
        return this;
    }

    public SkinValueBuilder textCompoundLeftSrc(int attr) {
        mValues.put(TEXT_COMPOUND_LEFT_SRC, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder textCompoundLeftSrc(String attrName) {
        mValues.put(TEXT_COMPOUND_LEFT_SRC, attrName);
        return this;
    }

    public SkinValueBuilder textColor(int attr) {
        mValues.put(TEXT_COLOR, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder textColor(String attrName) {
        mValues.put(TEXT_COLOR, attrName);
        return this;
    }

    public SkinValueBuilder hintColor(int attr) {
        mValues.put(HINT_COLOR, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder hintColor(String attrName) {
        mValues.put(HINT_COLOR, attrName);
        return this;
    }

    public SkinValueBuilder progressColor(int attr) {
        mValues.put(PROGRESS_COLOR, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder progressColor(String attrName) {
        mValues.put(PROGRESS_COLOR, attrName);
        return this;
    }

    public SkinValueBuilder src(int attr) {
        mValues.put(SRC, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder src(String attrName) {
        mValues.put(SRC, attrName);
        return this;
    }

    public SkinValueBuilder border(int attr) {
        mValues.put(BORDER, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder border(String attrName) {
        mValues.put(BORDER, attrName);
        return this;
    }

    public SkinValueBuilder topSeparator(int attr) {
        mValues.put(TOP_SEPARATOR, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder topSeparator(String attrName) {
        mValues.put(TOP_SEPARATOR, attrName);
        return this;
    }

    public SkinValueBuilder rightSeparator(int attr) {
        mValues.put(RIGHT_SEPARATOR, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder rightSeparator(String attrName) {
        mValues.put(RIGHT_SEPARATOR, attrName);
        return this;
    }

    public SkinValueBuilder bottomSeparator(int attr) {
        mValues.put(BOTTOM_SEPARATOR, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder bottomSeparator(String attrName) {
        mValues.put(BOTTOM_SEPARATOR, attrName);
        return this;
    }

    public SkinValueBuilder leftSeparator(int attr) {
        mValues.put(LEFT_SEPARATOR, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder leftSeparator(String attrName) {
        mValues.put(LEFT_SEPARATOR, attrName);
        return this;
    }

    public SkinValueBuilder alpha(int attr) {
        mValues.put(ALPHA, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder alpha(String attrName) {
        mValues.put(ALPHA, attrName);
        return this;
    }

    public SkinValueBuilder tintColor(int attr) {
        mValues.put(TINT_COLOR, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder tintColor(String attrName) {
        mValues.put(TINT_COLOR, attrName);
        return this;
    }

    public SkinValueBuilder bgTintColor(int attr) {
        mValues.put(BG_TINT_COLOR, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder bgTintColor(String attrName) {
        mValues.put(BG_TINT_COLOR, attrName);
        return this;
    }

    public SkinValueBuilder secondTextColor(int attr) {
        mValues.put(SECOND_TEXT_COLOR, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder secondTextColor(String attrName) {
        mValues.put(SECOND_TEXT_COLOR, attrName);
        return this;
    }

    public SkinValueBuilder custom(String name, int attr) {
        mValues.put(name, String.valueOf(attr));
        return this;
    }

    public SkinValueBuilder custom(String name, String attrName) {
        mValues.put(name, attrName);
        return this;
    }

    public SkinValueBuilder clear() {
        mValues.clear();
        return this;
    }

    public SkinValueBuilder convertFrom(String value) {
        String[] items = value.split("[|]");
        for (String item : items) {
            String[] kv = item.split(":");
            if (kv.length != 2) {
                continue;
            }
            mValues.put(kv[0].trim(), kv[1].trim());
        }
        return this;
    }

    public boolean isEmpty() {
        return mValues.isEmpty();
    }

    public String build() {
        StringBuilder builder = new StringBuilder();
        boolean isFirstItem = true;
        for (String name : mValues.keySet()) {
            String itemValue = mValues.get(name);
            if (itemValue == null || itemValue.isEmpty()) {
                continue;
            }
            if (!isFirstItem) {
                builder.append("|");
            }
            builder.append(name);
            builder.append(":");
            builder.append(itemValue);
            isFirstItem = false;
        }
        return builder.toString();
    }

    public void release() {
        SkinValueBuilder.release(this);
    }
}
