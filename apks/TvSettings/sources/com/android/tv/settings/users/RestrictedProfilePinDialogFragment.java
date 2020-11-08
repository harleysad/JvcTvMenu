package com.android.tv.settings.users;

import android.content.Context;
import android.os.Bundle;
import com.android.tv.settings.dialog.PinDialogFragment;

public class RestrictedProfilePinDialogFragment extends PinDialogFragment {
    private static final String PREF_DISABLE_PIN_UNTIL = "disable_pin_until";
    private static final String SHARED_PREFERENCE_NAME = "RestrictedProfilePinDialogFragment";

    public interface Callback extends PinDialogFragment.ResultListener {
        boolean checkPassword(String str, int i);

        void clearLockPassword(String str);

        boolean hasLockscreenSecurity();

        void saveLockPassword(String str, String str2, int i);
    }

    public static RestrictedProfilePinDialogFragment newInstance(int type) {
        RestrictedProfilePinDialogFragment fragment = new RestrictedProfilePinDialogFragment();
        Bundle b = new Bundle(1);
        b.putInt("type", type);
        fragment.setArguments(b);
        return fragment;
    }

    public static long getDisablePinUntil(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0).getLong(PREF_DISABLE_PIN_UNTIL, 0);
    }

    public static void setDisablePinUntil(Context context, long timeMillis) {
        context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0).edit().putLong(PREF_DISABLE_PIN_UNTIL, timeMillis).apply();
    }

    public long getPinDisabledUntil() {
        return getDisablePinUntil(getActivity());
    }

    public void setPinDisabledUntil(long retryDisableTimeout) {
        setDisablePinUntil(getActivity(), retryDisableTimeout);
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.app.Fragment] */
    /* JADX WARNING: type inference failed for: r2v4, types: [android.app.Activity] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setPin(java.lang.String r4, java.lang.String r5) {
        /*
            r3 = this;
            r0 = 0
            android.app.Fragment r1 = r3.getTargetFragment()
            boolean r2 = r1 instanceof com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback
            if (r2 == 0) goto L_0x000c
            r0 = r1
            com.android.tv.settings.users.RestrictedProfilePinDialogFragment$Callback r0 = (com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback) r0
        L_0x000c:
            if (r0 != 0) goto L_0x001d
            android.app.Activity r2 = r3.getActivity()
            boolean r2 = r2 instanceof com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback
            if (r2 == 0) goto L_0x001d
            android.app.Activity r2 = r3.getActivity()
            r0 = r2
            com.android.tv.settings.users.RestrictedProfilePinDialogFragment$Callback r0 = (com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback) r0
        L_0x001d:
            if (r0 == 0) goto L_0x0024
            r2 = 65536(0x10000, float:9.18355E-41)
            r0.saveLockPassword(r4, r5, r2)
        L_0x0024:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.users.RestrictedProfilePinDialogFragment.setPin(java.lang.String, java.lang.String):void");
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.app.Fragment] */
    /* JADX WARNING: type inference failed for: r2v3, types: [android.app.Activity] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void deletePin(java.lang.String r4) {
        /*
            r3 = this;
            r0 = 0
            android.app.Fragment r1 = r3.getTargetFragment()
            boolean r2 = r1 instanceof com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback
            if (r2 == 0) goto L_0x000c
            r0 = r1
            com.android.tv.settings.users.RestrictedProfilePinDialogFragment$Callback r0 = (com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback) r0
        L_0x000c:
            if (r0 != 0) goto L_0x001d
            android.app.Activity r2 = r3.getActivity()
            boolean r2 = r2 instanceof com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback
            if (r2 == 0) goto L_0x001d
            android.app.Activity r2 = r3.getActivity()
            r0 = r2
            com.android.tv.settings.users.RestrictedProfilePinDialogFragment$Callback r0 = (com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback) r0
        L_0x001d:
            if (r0 == 0) goto L_0x0022
            r0.clearLockPassword(r4)
        L_0x0022:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.users.RestrictedProfilePinDialogFragment.deletePin(java.lang.String):void");
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.app.Fragment] */
    /* JADX WARNING: type inference failed for: r2v5, types: [android.app.Activity] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isPinCorrect(java.lang.String r7) {
        /*
            r6 = this;
            r0 = 0
            android.app.Fragment r1 = r6.getTargetFragment()
            boolean r2 = r1 instanceof com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback
            if (r2 == 0) goto L_0x000c
            r0 = r1
            com.android.tv.settings.users.RestrictedProfilePinDialogFragment$Callback r0 = (com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback) r0
        L_0x000c:
            if (r0 != 0) goto L_0x001d
            android.app.Activity r2 = r6.getActivity()
            boolean r2 = r2 instanceof com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback
            if (r2 == 0) goto L_0x001d
            android.app.Activity r2 = r6.getActivity()
            r0 = r2
            com.android.tv.settings.users.RestrictedProfilePinDialogFragment$Callback r0 = (com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback) r0
        L_0x001d:
            r2 = 0
            if (r0 == 0) goto L_0x0046
            android.app.Activity r3 = r6.getActivity()
            android.os.UserManager r3 = android.os.UserManager.get(r3)
            int r4 = android.os.UserHandle.myUserId()
            android.content.pm.UserInfo r3 = r3.getUserInfo(r4)
            if (r3 == 0) goto L_0x0045
            int r4 = r3.restrictedProfileParentId
            r5 = -10000(0xffffffffffffd8f0, float:NaN)
            if (r4 != r5) goto L_0x003b
            r4 = r2
            goto L_0x003d
        L_0x003b:
            int r4 = r3.restrictedProfileParentId
        L_0x003d:
            boolean r4 = r0.checkPassword(r7, r4)
            if (r4 == 0) goto L_0x0045
            r2 = 1
        L_0x0045:
            return r2
        L_0x0046:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.users.RestrictedProfilePinDialogFragment.isPinCorrect(java.lang.String):boolean");
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.app.Fragment] */
    /* JADX WARNING: type inference failed for: r2v7, types: [android.app.Activity] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isPinSet() {
        /*
            r4 = this;
            r0 = 0
            android.app.Fragment r1 = r4.getTargetFragment()
            boolean r2 = r1 instanceof com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback
            if (r2 == 0) goto L_0x000c
            r0 = r1
            com.android.tv.settings.users.RestrictedProfilePinDialogFragment$Callback r0 = (com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback) r0
        L_0x000c:
            if (r0 != 0) goto L_0x001d
            android.app.Activity r2 = r4.getActivity()
            boolean r2 = r2 instanceof com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback
            if (r2 == 0) goto L_0x001d
            android.app.Activity r2 = r4.getActivity()
            r0 = r2
            com.android.tv.settings.users.RestrictedProfilePinDialogFragment$Callback r0 = (com.android.tv.settings.users.RestrictedProfilePinDialogFragment.Callback) r0
        L_0x001d:
            if (r0 == 0) goto L_0x0041
            android.app.Activity r2 = r4.getActivity()
            android.os.UserManager r2 = android.os.UserManager.get(r2)
            int r3 = android.os.UserHandle.myUserId()
            android.content.pm.UserInfo r2 = r2.getUserInfo(r3)
            if (r2 == 0) goto L_0x0037
            boolean r3 = r2.isRestricted()
            if (r3 != 0) goto L_0x003d
        L_0x0037:
            boolean r3 = r0.hasLockscreenSecurity()
            if (r3 == 0) goto L_0x003f
        L_0x003d:
            r3 = 1
            goto L_0x0040
        L_0x003f:
            r3 = 0
        L_0x0040:
            return r3
        L_0x0041:
            java.lang.IllegalStateException r2 = new java.lang.IllegalStateException
            java.lang.String r3 = "Can't call isPinSet when not attached"
            r2.<init>(r3)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.users.RestrictedProfilePinDialogFragment.isPinSet():boolean");
    }
}
