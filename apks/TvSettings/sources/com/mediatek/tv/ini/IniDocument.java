package com.mediatek.tv.ini;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class IniDocument {
    protected static final String IGNORE_COMMENTS_IDEA_START = ";";
    protected static final String IGNORE_COMMENTS_START = "#";
    protected static final String IGNORE_TAG_END = "]";
    protected static final String IGNORE_TAG_START = "[";
    protected static final String TAG_APPEND = "\\";
    private List<Section> mSections;

    public List<Section> getContent() {
        return this.mSections;
    }

    public List<String> get(String key) {
        for (Section section : this.mSections) {
            Iterator<KeyValue> it = section.getKeyValues().iterator();
            while (true) {
                if (it.hasNext()) {
                    KeyValue keyValue = it.next();
                    if (keyValue.getKey().equals(key)) {
                        return keyValue.getValue();
                    }
                }
            }
        }
        return null;
    }

    public String getTagValue(String sectionName, String keyName) {
        for (Section section : this.mSections) {
            if (section.getSectionName().equals(sectionName)) {
                for (KeyValue keyValue : section.getKeyValues()) {
                    if (keyValue.getKey().equals(keyName)) {
                        List<String> valueList = keyValue.getValue();
                        if (valueList.size() > 0) {
                            return valueList.get(0);
                        }
                        return "";
                    }
                }
                return "";
            }
        }
        return "";
    }

    public IniDocument(String path) {
        try {
            parse(new FileInputStream(new File(path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public IniDocument(InputStream inputStream) {
        parse(inputStream);
    }

    private void parse(InputStream inputStream) {
        BufferedReader reader = null;
        this.mSections = new ArrayList();
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String fullline = "";
            List<String> sectionRawData = null;
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                System.out.println(line);
                if (!line.startsWith(IGNORE_COMMENTS_START)) {
                    if (line.contains(IGNORE_COMMENTS_IDEA_START)) {
                        String[] str = line.split(IGNORE_COMMENTS_IDEA_START);
                        if (str.length >= 1) {
                            line = str[0];
                        }
                    }
                    if (line.startsWith(IGNORE_TAG_START)) {
                        if (sectionRawData != null) {
                            this.mSections.add(new Section(sectionRawData));
                        }
                        sectionRawData = new ArrayList<>();
                    }
                    if (line.endsWith(TAG_APPEND)) {
                        fullline = fullline + line.trim().substring(0, line.length() - 1);
                    } else {
                        String fullline2 = fullline + line.trim();
                        if (sectionRawData != null) {
                            sectionRawData.add(fullline2);
                        }
                        fullline = "";
                    }
                }
            }
            if (sectionRawData != null) {
                this.mSections.add(new Section(sectionRawData));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public String toString() {
        return "Ini:-------------\n" + convertListToString(this.mSections);
    }

    private static String convertListToString(List<Section> list) {
        String builder = "";
        for (Section str : list) {
            builder = builder + str + "\n";
        }
        return builder;
    }
}
