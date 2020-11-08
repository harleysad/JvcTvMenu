package com.mediatek.tv.ini;

import java.util.ArrayList;
import java.util.List;

public final class Section {
    private final List<KeyValue> mKeys = new ArrayList();
    private String mSectionName;

    public String getSectionName() {
        return this.mSectionName;
    }

    public void setSectionName(String sectionName) {
        this.mSectionName = sectionName;
    }

    public List<KeyValue> getKeyValues() {
        return this.mKeys;
    }

    public Section(List<String> srcLines) {
        try {
            for (String srcLine : srcLines) {
                if (!srcLine.startsWith("#")) {
                    if (!srcLine.startsWith(";")) {
                        if (srcLine.startsWith("[")) {
                            this.mSectionName = srcLine.trim();
                            this.mSectionName = this.mSectionName.substring(1, this.mSectionName.length() - 1);
                        } else if (srcLine.contains("=") && !srcLine.startsWith("#")) {
                            this.mKeys.add(new KeyValue(srcLine));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return "Section: \n" + this.mSectionName + "\nKeyValues:" + convertListToString(this.mKeys) + "\n";
    }

    private static String convertListToString(List<KeyValue> list) {
        String builder = "";
        for (KeyValue str : list) {
            builder = builder + str + "\n";
        }
        return builder;
    }
}
