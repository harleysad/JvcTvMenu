package com.android.tv.settings.inputmethod;

import android.view.inputmethod.InputMethodInfo;
import java.util.function.Predicate;

/* renamed from: com.android.tv.settings.inputmethod.-$$Lambda$InputMethodHelper$MNG-fukE9GMAaUF3hHJt7jfmo3A  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$InputMethodHelper$MNGfukE9GMAaUF3hHJt7jfmo3A implements Predicate {
    public static final /* synthetic */ $$Lambda$InputMethodHelper$MNGfukE9GMAaUF3hHJt7jfmo3A INSTANCE = new $$Lambda$InputMethodHelper$MNGfukE9GMAaUF3hHJt7jfmo3A();

    private /* synthetic */ $$Lambda$InputMethodHelper$MNGfukE9GMAaUF3hHJt7jfmo3A() {
    }

    public final boolean test(Object obj) {
        return ((InputMethodInfo) obj).isAuxiliaryIme();
    }
}
