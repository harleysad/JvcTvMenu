package com.android.tv.common.actions;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class InputSetupActionUtils {
    public static final String EXTRA_ACTIVITY_AFTER_COMPLETION = "com.android.tv.intent.extra.ACTIVITY_AFTER_COMPLETION";
    @Deprecated
    private static final String EXTRA_GOOGLE_ACTIVITY_AFTER_COMPLETION = "com.google.android.tv.intent.extra.ACTIVITY_AFTER_COMPLETION";
    @Deprecated
    private static final String EXTRA_GOOGLE_SETUP_INTENT = "com.google.android.tv.extra.SETUP_INTENT";
    public static final String EXTRA_INPUT_ID = "android.media.tv.extra.INPUT_ID";
    public static final String EXTRA_SETUP_INTENT = "com.android.tv.extra.SETUP_INTENT";
    public static final String INTENT_ACTION_INPUT_SETUP = "com.android.tv.action.LAUNCH_INPUT_SETUP";
    @Deprecated
    private static final String INTENT_GOOGLE_ACTION_INPUT_SETUP = "com.google.android.tv.action.LAUNCH_INPUT_SETUP";

    public static void removeSetupIntent(Bundle extras) {
        extras.remove("com.android.tv.extra.SETUP_INTENT");
        extras.remove(EXTRA_GOOGLE_SETUP_INTENT);
    }

    @Nullable
    public static Intent getExtraSetupIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return null;
        }
        Intent setupIntent = (Intent) extras.getParcelable("com.android.tv.extra.SETUP_INTENT");
        return setupIntent != null ? setupIntent : (Intent) extras.getParcelable(EXTRA_GOOGLE_SETUP_INTENT);
    }

    @Nullable
    public static Intent getExtraActivityAfter(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return null;
        }
        Intent setupIntent = (Intent) extras.getParcelable("com.android.tv.intent.extra.ACTIVITY_AFTER_COMPLETION");
        if (setupIntent != null) {
            return setupIntent;
        }
        return (Intent) extras.getParcelable(EXTRA_GOOGLE_ACTIVITY_AFTER_COMPLETION);
    }

    public static boolean hasInputSetupAction(Intent intent) {
        String action = intent.getAction();
        return "com.android.tv.action.LAUNCH_INPUT_SETUP".equals(action) || INTENT_GOOGLE_ACTION_INPUT_SETUP.equals(action);
    }
}
