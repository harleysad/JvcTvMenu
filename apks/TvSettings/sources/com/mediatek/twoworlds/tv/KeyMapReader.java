package com.mediatek.twoworlds.tv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/* compiled from: MtkTvKeyEventBase */
class KeyMapReader {
    protected HashMap<Integer, Integer> Android_to_MTK_keyCode = new HashMap<>();

    public KeyMapReader(String filename) throws Exception {
        Exception ex = null;
        BufferedReader reader = null;
        try {
            this.Android_to_MTK_keyCode.clear();
            reader = new BufferedReader(new FileReader(filename));
            read(reader);
            try {
                reader.close();
            } catch (Exception e) {
                ex = e;
            }
        } catch (Exception e2) {
            ex = e2;
            if (reader != null) {
                reader.close();
            }
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e3) {
                    Exception ex2 = e3;
                }
            }
            throw th;
        }
        if (ex != null) {
            throw ex;
        }
    }

    /* access modifiers changed from: protected */
    public void read(BufferedReader reader) throws IOException {
        while (true) {
            String readLine = reader.readLine();
            String line = readLine;
            if (readLine != null) {
                parseLine(line);
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void parseLine(String line) {
        String currentLine = line.trim();
        if (currentLine.indexOf("#") != 0) {
            String[] Count = currentLine.split("\\s+");
            this.Android_to_MTK_keyCode.put(new Integer(Integer.parseInt(Count[3])), new Integer(Integer.parseInt(Count[1], 16)));
        }
    }

    public int getMTKKeyCode(int AndroidKeyCode) {
        if (this.Android_to_MTK_keyCode.containsKey(Integer.valueOf(AndroidKeyCode))) {
            return this.Android_to_MTK_keyCode.get(Integer.valueOf(AndroidKeyCode)).intValue();
        }
        return -1;
    }
}
