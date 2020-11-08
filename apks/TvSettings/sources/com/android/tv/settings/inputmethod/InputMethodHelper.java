package com.android.tv.settings.inputmethod;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import java.util.ArrayList;
import java.util.List;

public class InputMethodHelper {
    public static final String TAG = "InputMethodHelper";

    public static List<InputMethodInfo> getEnabledSystemInputMethodList(Context context) {
        List<InputMethodInfo> enabledInputMethodInfos = new ArrayList<>(((InputMethodManager) context.getSystemService("input_method")).getEnabledInputMethodList());
        enabledInputMethodInfos.removeIf($$Lambda$InputMethodHelper$MNGfukE9GMAaUF3hHJt7jfmo3A.INSTANCE);
        return enabledInputMethodInfos;
    }

    public static String getDefaultInputMethodId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "default_input_method");
    }

    public static void setDefaultInputMethodId(Context context, String imid) {
        if (imid != null) {
            try {
                Settings.Secure.putStringForUser(context.getContentResolver(), "default_input_method", imid, ActivityManager.getService().getCurrentUser().id);
                Intent intent = new Intent("android.intent.action.INPUT_METHOD_CHANGED");
                intent.addFlags(536870912);
                intent.putExtra("input_method_id", imid);
                context.sendBroadcastAsUser(intent, UserHandle.CURRENT);
            } catch (RemoteException e) {
                Log.d(TAG, "set default input method remote exception", e);
            }
        } else {
            throw new IllegalArgumentException("Null ID");
        }
    }

    public static InputMethodInfo findInputMethod(String imid, List<InputMethodInfo> enabledInputMethodInfos) {
        int size = enabledInputMethodInfos.size();
        for (int i = 0; i < size; i++) {
            InputMethodInfo info = enabledInputMethodInfos.get(i);
            if (TextUtils.equals(info.getId(), imid)) {
                return info;
            }
        }
        return null;
    }

    public static Intent getInputMethodSettingsIntent(InputMethodInfo imi) {
        String settingsActivity = imi.getSettingsActivity();
        if (TextUtils.isEmpty(settingsActivity)) {
            return null;
        }
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClassName(imi.getPackageName(), settingsActivity);
        return intent;
    }
}
