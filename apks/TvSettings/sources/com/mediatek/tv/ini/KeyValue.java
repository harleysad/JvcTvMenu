package com.mediatek.tv.ini;

import java.util.Arrays;
import java.util.List;

public final class KeyValue {
    private String mKey;
    private String mRawData;
    private List<String> mValue;

    public KeyValue(String rawData) {
        setRawData(rawData);
        String[] data = rawData.replace(" ", "").split("=");
        if (data.length > 0) {
            setKey(data[0]);
        }
        if (data.length > 1) {
            setValue(Arrays.asList(data[1].split(",")));
        }
    }

    public String getRawData() {
        return this.mRawData;
    }

    public void setRawData(String rawData) {
        this.mRawData = rawData;
    }

    public String getKey() {
        return this.mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public List<String> getValue() {
        return this.mValue;
    }

    public void setValue(List<String> value) {
        this.mValue = value;
    }

    public String toString() {
        return "[Key: " + this.mKey + ", Value:" + convertListToString(this.mValue) + "]";
    }

    private static String convertListToString(List<String> list) {
        String builder = "";
        for (String str : list) {
            builder = builder + str + ",";
        }
        return builder.substring(0, builder.length() - 1);
    }
}
