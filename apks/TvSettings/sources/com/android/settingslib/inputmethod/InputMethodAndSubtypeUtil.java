package com.android.settingslib.inputmethod;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.icu.text.ListFormatter;
import android.provider.Settings;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.app.LocaleHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InputMethodAndSubtypeUtil {
    private static final boolean DEBUG = false;
    private static final char INPUT_METHOD_SEPARATER = ':';
    private static final char INPUT_METHOD_SUBTYPE_SEPARATER = ';';
    private static final int NOT_A_SUBTYPE_ID = -1;
    private static final String TAG = "InputMethdAndSubtypeUtl";
    private static final TextUtils.SimpleStringSplitter sStringInputMethodSplitter = new TextUtils.SimpleStringSplitter(':');
    private static final TextUtils.SimpleStringSplitter sStringInputMethodSubtypeSplitter = new TextUtils.SimpleStringSplitter(INPUT_METHOD_SUBTYPE_SEPARATER);

    private static String buildInputMethodsAndSubtypesString(HashMap<String, HashSet<String>> imeToSubtypesMap) {
        StringBuilder builder = new StringBuilder();
        for (String imi : imeToSubtypesMap.keySet()) {
            if (builder.length() > 0) {
                builder.append(':');
            }
            builder.append(imi);
            Iterator<String> it = imeToSubtypesMap.get(imi).iterator();
            while (it.hasNext()) {
                builder.append(INPUT_METHOD_SUBTYPE_SEPARATER);
                builder.append(it.next());
            }
        }
        return builder.toString();
    }

    private static String buildInputMethodsString(HashSet<String> imiList) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> it = imiList.iterator();
        while (it.hasNext()) {
            String imi = it.next();
            if (builder.length() > 0) {
                builder.append(':');
            }
            builder.append(imi);
        }
        return builder.toString();
    }

    private static int getInputMethodSubtypeSelected(ContentResolver resolver) {
        try {
            return Settings.Secure.getInt(resolver, "selected_input_method_subtype");
        } catch (Settings.SettingNotFoundException e) {
            return -1;
        }
    }

    private static boolean isInputMethodSubtypeSelected(ContentResolver resolver) {
        return getInputMethodSubtypeSelected(resolver) != -1;
    }

    private static void putSelectedInputMethodSubtype(ContentResolver resolver, int hashCode) {
        Settings.Secure.putInt(resolver, "selected_input_method_subtype", hashCode);
    }

    private static HashMap<String, HashSet<String>> getEnabledInputMethodsAndSubtypeList(ContentResolver resolver) {
        return parseInputMethodsAndSubtypesString(Settings.Secure.getString(resolver, "enabled_input_methods"));
    }

    private static HashMap<String, HashSet<String>> parseInputMethodsAndSubtypesString(String inputMethodsAndSubtypesString) {
        HashMap<String, HashSet<String>> subtypesMap = new HashMap<>();
        if (TextUtils.isEmpty(inputMethodsAndSubtypesString)) {
            return subtypesMap;
        }
        sStringInputMethodSplitter.setString(inputMethodsAndSubtypesString);
        while (sStringInputMethodSplitter.hasNext()) {
            sStringInputMethodSubtypeSplitter.setString(sStringInputMethodSplitter.next());
            if (sStringInputMethodSubtypeSplitter.hasNext()) {
                HashSet<String> subtypeIdSet = new HashSet<>();
                String imiId = sStringInputMethodSubtypeSplitter.next();
                while (sStringInputMethodSubtypeSplitter.hasNext()) {
                    subtypeIdSet.add(sStringInputMethodSubtypeSplitter.next());
                }
                subtypesMap.put(imiId, subtypeIdSet);
            }
        }
        return subtypesMap;
    }

    private static HashSet<String> getDisabledSystemIMEs(ContentResolver resolver) {
        HashSet<String> set = new HashSet<>();
        String disabledIMEsStr = Settings.Secure.getString(resolver, "disabled_system_input_methods");
        if (TextUtils.isEmpty(disabledIMEsStr)) {
            return set;
        }
        sStringInputMethodSplitter.setString(disabledIMEsStr);
        while (sStringInputMethodSplitter.hasNext()) {
            set.add(sStringInputMethodSplitter.next());
        }
        return set;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x005e, code lost:
        if (com.android.settingslib.inputmethod.InputMethodSettingValuesWrapper.getInstance(r24.getActivity()).isAlwaysCheckedIme(r8, r24.getActivity()) == false) goto L_0x0062;
     */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x010c  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0112  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void saveInputMethodSubtypeList(android.support.v14.preference.PreferenceFragment r24, android.content.ContentResolver r25, java.util.List<android.view.inputmethod.InputMethodInfo> r26, boolean r27) {
        /*
            r0 = r24
            r1 = r25
            java.lang.String r2 = "default_input_method"
            java.lang.String r2 = android.provider.Settings.Secure.getString(r1, r2)
            int r3 = getInputMethodSubtypeSelected(r25)
            java.util.HashMap r4 = getEnabledInputMethodsAndSubtypeList(r25)
            java.util.HashSet r5 = getDisabledSystemIMEs(r25)
            r6 = 0
            java.util.Iterator r7 = r26.iterator()
        L_0x001c:
            boolean r8 = r7.hasNext()
            if (r8 == 0) goto L_0x011d
            java.lang.Object r8 = r7.next()
            android.view.inputmethod.InputMethodInfo r8 = (android.view.inputmethod.InputMethodInfo) r8
            java.lang.String r9 = r8.getId()
            android.support.v7.preference.Preference r10 = r0.findPreference(r9)
            if (r10 != 0) goto L_0x0033
            goto L_0x001c
        L_0x0033:
            boolean r11 = r10 instanceof android.support.v7.preference.TwoStatePreference
            if (r11 == 0) goto L_0x003f
            r11 = r10
            android.support.v7.preference.TwoStatePreference r11 = (android.support.v7.preference.TwoStatePreference) r11
            boolean r11 = r11.isChecked()
            goto L_0x0043
        L_0x003f:
            boolean r11 = r4.containsKey(r9)
        L_0x0043:
            boolean r12 = r9.equals(r2)
            boolean r13 = com.android.internal.inputmethod.InputMethodUtils.isSystemIme(r8)
            if (r27 != 0) goto L_0x0061
            android.app.Activity r14 = r24.getActivity()
            com.android.settingslib.inputmethod.InputMethodSettingValuesWrapper r14 = com.android.settingslib.inputmethod.InputMethodSettingValuesWrapper.getInstance(r14)
            r15 = r7
            android.app.Activity r7 = r24.getActivity()
            boolean r7 = r14.isAlwaysCheckedIme(r8, r7)
            if (r7 != 0) goto L_0x0064
            goto L_0x0062
        L_0x0061:
            r15 = r7
        L_0x0062:
            if (r11 == 0) goto L_0x00f2
        L_0x0064:
            boolean r7 = r4.containsKey(r9)
            if (r7 != 0) goto L_0x0072
            java.util.HashSet r7 = new java.util.HashSet
            r7.<init>()
            r4.put(r9, r7)
        L_0x0072:
            java.lang.Object r7 = r4.get(r9)
            java.util.HashSet r7 = (java.util.HashSet) r7
            r14 = 0
            r16 = r10
            int r10 = r8.getSubtypeCount()
            r17 = 0
        L_0x0081:
            r18 = r17
            r19 = r6
            r6 = r18
            if (r6 >= r10) goto L_0x00eb
            r20 = r10
            android.view.inputmethod.InputMethodSubtype r10 = r8.getSubtypeAt(r6)
            r21 = r8
            int r8 = r10.hashCode()
            java.lang.String r8 = java.lang.String.valueOf(r8)
            r22 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r9)
            r2.append(r8)
            java.lang.String r2 = r2.toString()
            android.support.v7.preference.Preference r2 = r0.findPreference(r2)
            android.support.v7.preference.TwoStatePreference r2 = (android.support.v7.preference.TwoStatePreference) r2
            if (r2 != 0) goto L_0x00b6
            r0 = r19
            goto L_0x00df
        L_0x00b6:
            if (r14 != 0) goto L_0x00bf
            r7.clear()
            r17 = 1
            r14 = 1
            goto L_0x00c1
        L_0x00bf:
            r17 = r19
        L_0x00c1:
            boolean r18 = r2.isEnabled()
            if (r18 == 0) goto L_0x00da
            boolean r18 = r2.isChecked()
            if (r18 == 0) goto L_0x00da
            r7.add(r8)
            if (r12 == 0) goto L_0x00dd
            int r0 = r10.hashCode()
            if (r3 != r0) goto L_0x00dd
            r0 = 0
            goto L_0x00df
        L_0x00da:
            r7.remove(r8)
        L_0x00dd:
            r0 = r17
        L_0x00df:
            int r17 = r6 + 1
            r6 = r0
            r10 = r20
            r8 = r21
            r2 = r22
            r0 = r24
            goto L_0x0081
        L_0x00eb:
            r22 = r2
            r21 = r8
            r6 = r19
            goto L_0x0102
        L_0x00f2:
            r22 = r2
            r21 = r8
            r16 = r10
            r4.remove(r9)
            if (r12 == 0) goto L_0x0100
            r0 = 0
            r2 = r0
            goto L_0x0102
        L_0x0100:
            r2 = r22
        L_0x0102:
            if (r13 == 0) goto L_0x0117
            if (r27 == 0) goto L_0x0117
            boolean r0 = r5.contains(r9)
            if (r0 == 0) goto L_0x0112
            if (r11 == 0) goto L_0x0117
            r5.remove(r9)
            goto L_0x0117
        L_0x0112:
            if (r11 != 0) goto L_0x0117
            r5.add(r9)
        L_0x0117:
            r7 = r15
            r0 = r24
            goto L_0x001c
        L_0x011d:
            r22 = r2
            java.lang.String r0 = buildInputMethodsAndSubtypesString(r4)
            java.lang.String r2 = buildInputMethodsString(r5)
            if (r6 != 0) goto L_0x012f
            boolean r7 = isInputMethodSubtypeSelected(r25)
            if (r7 != 0) goto L_0x0133
        L_0x012f:
            r7 = -1
            putSelectedInputMethodSubtype(r1, r7)
        L_0x0133:
            java.lang.String r7 = "enabled_input_methods"
            android.provider.Settings.Secure.putString(r1, r7, r0)
            int r7 = r2.length()
            if (r7 <= 0) goto L_0x0143
            java.lang.String r7 = "disabled_system_input_methods"
            android.provider.Settings.Secure.putString(r1, r7, r2)
        L_0x0143:
            java.lang.String r7 = "default_input_method"
            if (r22 == 0) goto L_0x014a
            r8 = r22
            goto L_0x014c
        L_0x014a:
            java.lang.String r8 = ""
        L_0x014c:
            android.provider.Settings.Secure.putString(r1, r7, r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.inputmethod.InputMethodAndSubtypeUtil.saveInputMethodSubtypeList(android.support.v14.preference.PreferenceFragment, android.content.ContentResolver, java.util.List, boolean):void");
    }

    public static void loadInputMethodSubtypeList(PreferenceFragment context, ContentResolver resolver, List<InputMethodInfo> inputMethodInfos, Map<String, List<Preference>> inputMethodPrefsMap) {
        HashMap<String, HashSet<String>> enabledSubtypes = getEnabledInputMethodsAndSubtypeList(resolver);
        for (InputMethodInfo imi : inputMethodInfos) {
            String imiId = imi.getId();
            Preference pref = context.findPreference(imiId);
            if (pref instanceof TwoStatePreference) {
                boolean isEnabled = enabledSubtypes.containsKey(imiId);
                ((TwoStatePreference) pref).setChecked(isEnabled);
                if (inputMethodPrefsMap != null) {
                    for (Preference childPref : inputMethodPrefsMap.get(imiId)) {
                        childPref.setEnabled(isEnabled);
                    }
                }
                setSubtypesPreferenceEnabled(context, inputMethodInfos, imiId, isEnabled);
            }
        }
        updateSubtypesPreferenceChecked(context, inputMethodInfos, enabledSubtypes);
    }

    private static void setSubtypesPreferenceEnabled(PreferenceFragment context, List<InputMethodInfo> inputMethodProperties, String id, boolean enabled) {
        PreferenceScreen preferenceScreen = context.getPreferenceScreen();
        for (InputMethodInfo imi : inputMethodProperties) {
            if (id.equals(imi.getId())) {
                int subtypeCount = imi.getSubtypeCount();
                for (int i = 0; i < subtypeCount; i++) {
                    InputMethodSubtype subtype = imi.getSubtypeAt(i);
                    TwoStatePreference pref = (TwoStatePreference) preferenceScreen.findPreference(id + subtype.hashCode());
                    if (pref != null) {
                        pref.setEnabled(enabled);
                    }
                }
            }
        }
    }

    private static void updateSubtypesPreferenceChecked(PreferenceFragment context, List<InputMethodInfo> inputMethodProperties, HashMap<String, HashSet<String>> enabledSubtypes) {
        PreferenceScreen preferenceScreen = context.getPreferenceScreen();
        for (InputMethodInfo imi : inputMethodProperties) {
            String id = imi.getId();
            if (enabledSubtypes.containsKey(id)) {
                HashSet<String> enabledSubtypesSet = enabledSubtypes.get(id);
                int subtypeCount = imi.getSubtypeCount();
                for (int i = 0; i < subtypeCount; i++) {
                    String hashCode = String.valueOf(imi.getSubtypeAt(i).hashCode());
                    TwoStatePreference pref = (TwoStatePreference) preferenceScreen.findPreference(id + hashCode);
                    if (pref != null) {
                        pref.setChecked(enabledSubtypesSet.contains(hashCode));
                    }
                }
            }
        }
    }

    public static void removeUnnecessaryNonPersistentPreference(Preference pref) {
        SharedPreferences prefs;
        String key = pref.getKey();
        if (!pref.isPersistent() && key != null && (prefs = pref.getSharedPreferences()) != null && prefs.contains(key)) {
            prefs.edit().remove(key).apply();
        }
    }

    public static String getSubtypeLocaleNameAsSentence(InputMethodSubtype subtype, Context context, InputMethodInfo inputMethodInfo) {
        if (subtype == null) {
            return "";
        }
        return LocaleHelper.toSentenceCase(subtype.getDisplayName(context, inputMethodInfo.getPackageName(), inputMethodInfo.getServiceInfo().applicationInfo).toString(), getDisplayLocale(context));
    }

    public static String getSubtypeLocaleNameListAsSentence(List<InputMethodSubtype> subtypes, Context context, InputMethodInfo inputMethodInfo) {
        if (subtypes.isEmpty()) {
            return "";
        }
        Locale locale = getDisplayLocale(context);
        int subtypeCount = subtypes.size();
        CharSequence[] subtypeNames = new CharSequence[subtypeCount];
        for (int i = 0; i < subtypeCount; i++) {
            subtypeNames[i] = subtypes.get(i).getDisplayName(context, inputMethodInfo.getPackageName(), inputMethodInfo.getServiceInfo().applicationInfo);
        }
        return LocaleHelper.toSentenceCase(ListFormatter.getInstance(locale).format((Object[]) subtypeNames), locale);
    }

    private static Locale getDisplayLocale(Context context) {
        if (context == null) {
            return Locale.getDefault();
        }
        if (context.getResources() == null) {
            return Locale.getDefault();
        }
        Configuration configuration = context.getResources().getConfiguration();
        if (configuration == null) {
            return Locale.getDefault();
        }
        Locale configurationLocale = configuration.getLocales().get(0);
        if (configurationLocale == null) {
            return Locale.getDefault();
        }
        return configurationLocale;
    }
}
