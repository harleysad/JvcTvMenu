package android.support.v17.leanback.widget;

import android.support.annotation.RestrictTo;
import android.view.View;

interface FocusHighlightHandler {
    void onInitializeView(View view);

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    void onItemFocused(View view, boolean z);
}
