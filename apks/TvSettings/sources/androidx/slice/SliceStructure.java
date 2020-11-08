package androidx.slice;

public class SliceStructure {
    private final String mStructure;

    public SliceStructure(Slice s) {
        StringBuilder str = new StringBuilder();
        getStructure(s, str);
        this.mStructure = str.toString();
    }

    public int hashCode() {
        return this.mStructure.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SliceStructure)) {
            return false;
        }
        return this.mStructure.equals(((SliceStructure) obj).mStructure);
    }

    private static void getStructure(Slice s, StringBuilder str) {
        str.append("s{");
        for (SliceItem item : s.getItems()) {
            getStructure(item, str);
        }
        str.append("}");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void getStructure(androidx.slice.SliceItem r2, java.lang.StringBuilder r3) {
        /*
            java.lang.String r0 = r2.getFormat()
            int r1 = r0.hashCode()
            switch(r1) {
                case -1422950858: goto L_0x0052;
                case -1377881982: goto L_0x0048;
                case 104431: goto L_0x003e;
                case 3327612: goto L_0x0034;
                case 3556653: goto L_0x002a;
                case 100313435: goto L_0x0020;
                case 100358090: goto L_0x0016;
                case 109526418: goto L_0x000c;
                default: goto L_0x000b;
            }
        L_0x000b:
            goto L_0x005c
        L_0x000c:
            java.lang.String r1 = "slice"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x005c
            r0 = 0
            goto L_0x005d
        L_0x0016:
            java.lang.String r1 = "input"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x005c
            r0 = 6
            goto L_0x005d
        L_0x0020:
            java.lang.String r1 = "image"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x005c
            r0 = 3
            goto L_0x005d
        L_0x002a:
            java.lang.String r1 = "text"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x005c
            r0 = 2
            goto L_0x005d
        L_0x0034:
            java.lang.String r1 = "long"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x005c
            r0 = 5
            goto L_0x005d
        L_0x003e:
            java.lang.String r1 = "int"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x005c
            r0 = 4
            goto L_0x005d
        L_0x0048:
            java.lang.String r1 = "bundle"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x005c
            r0 = 7
            goto L_0x005d
        L_0x0052:
            java.lang.String r1 = "action"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x005c
            r0 = 1
            goto L_0x005d
        L_0x005c:
            r0 = -1
        L_0x005d:
            switch(r0) {
                case 0: goto L_0x007a;
                case 1: goto L_0x006d;
                case 2: goto L_0x0067;
                case 3: goto L_0x0061;
                default: goto L_0x0060;
            }
        L_0x0060:
            goto L_0x0082
        L_0x0061:
            r0 = 105(0x69, float:1.47E-43)
            r3.append(r0)
            goto L_0x0082
        L_0x0067:
            r0 = 116(0x74, float:1.63E-43)
            r3.append(r0)
            goto L_0x0082
        L_0x006d:
            r0 = 97
            r3.append(r0)
            androidx.slice.Slice r0 = r2.getSlice()
            getStructure((androidx.slice.Slice) r0, (java.lang.StringBuilder) r3)
            goto L_0x0082
        L_0x007a:
            androidx.slice.Slice r0 = r2.getSlice()
            getStructure((androidx.slice.Slice) r0, (java.lang.StringBuilder) r3)
        L_0x0082:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.SliceStructure.getStructure(androidx.slice.SliceItem, java.lang.StringBuilder):void");
    }
}
