package com.android.tv.parental;

import java.util.Comparator;

/* renamed from: com.android.tv.parental.-$$Lambda$ContentRatingSystem$yjyLZRXUKMr7p4To7QE869hbi0M  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ContentRatingSystem$yjyLZRXUKMr7p4To7QE869hbi0M implements Comparator {
    public static final /* synthetic */ $$Lambda$ContentRatingSystem$yjyLZRXUKMr7p4To7QE869hbi0M INSTANCE = new $$Lambda$ContentRatingSystem$yjyLZRXUKMr7p4To7QE869hbi0M();

    private /* synthetic */ $$Lambda$ContentRatingSystem$yjyLZRXUKMr7p4To7QE869hbi0M() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((ContentRatingSystem) obj).getDisplayName().compareTo(((ContentRatingSystem) obj2).getDisplayName());
    }
}
