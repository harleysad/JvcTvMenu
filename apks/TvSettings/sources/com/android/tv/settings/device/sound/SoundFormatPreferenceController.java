package com.android.tv.settings.device.sound;

import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public class SoundFormatPreferenceController extends AbstractPreferenceController {
    private static final String TAG = "SoundFormatController";
    private AudioManager mAudioManager;
    private int mFormatId;
    private Map<Integer, Boolean> mFormats;
    private Map<Integer, Boolean> mReportedFormats;

    public SoundFormatPreferenceController(Context context, int formatId, Map<Integer, Boolean> formats, Map<Integer, Boolean> reportedFormats) {
        super(context);
        this.mFormatId = formatId;
        this.mFormats = formats;
        this.mReportedFormats = reportedFormats;
    }

    public boolean isAvailable() {
        return true;
    }

    public String getPreferenceKey() {
        return "surround_sound_format_" + this.mFormatId;
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference.getKey().equals(getPreferenceKey())) {
            preference.setEnabled(getFormatPreferencesEnabledState());
            ((SwitchPreference) preference).setChecked(getFormatPreferenceCheckedState());
        }
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(getPreferenceKey())) {
            setSurroundManualFormatsSetting(((SwitchPreference) preference).isChecked());
        }
        return super.handlePreferenceTreeClick(preference);
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x004d A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x004e  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x005d  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0062 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0063 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean getFormatPreferenceCheckedState() {
        /*
            r5 = this;
            android.content.Context r0 = r5.mContext
            java.lang.String r0 = com.android.tv.settings.device.sound.SoundFragment.getSurroundPassthroughSetting(r0)
            int r1 = r0.hashCode()
            r2 = -1414557169(0xffffffffabaf920f, float:-1.2475037E-12)
            r3 = 1
            r4 = 0
            if (r1 == r2) goto L_0x003f
            r2 = -1081415738(0xffffffffbf8ae7c6, float:-1.0851982)
            if (r1 == r2) goto L_0x0035
            r2 = 3005871(0x2dddaf, float:4.212122E-39)
            if (r1 == r2) goto L_0x002b
            r2 = 104712844(0x63dca8c, float:3.5695757E-35)
            if (r1 == r2) goto L_0x0021
            goto L_0x0049
        L_0x0021:
            java.lang.String r1 = "never"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0049
            r0 = r4
            goto L_0x004a
        L_0x002b:
            java.lang.String r1 = "auto"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0049
            r0 = 2
            goto L_0x004a
        L_0x0035:
            java.lang.String r1 = "manual"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0049
            r0 = 3
            goto L_0x004a
        L_0x003f:
            java.lang.String r1 = "always"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0049
            r0 = r3
            goto L_0x004a
        L_0x0049:
            r0 = -1
        L_0x004a:
            switch(r0) {
                case 0: goto L_0x0063;
                case 1: goto L_0x0062;
                case 2: goto L_0x005d;
                case 3: goto L_0x004e;
                default: goto L_0x004d;
            }
        L_0x004d:
            return r4
        L_0x004e:
            java.util.HashSet r0 = r5.getFormatsEnabledInManualMode()
            int r1 = r5.mFormatId
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            boolean r0 = r0.contains(r1)
            return r0
        L_0x005d:
            boolean r0 = r5.isReportedFormat()
            return r0
        L_0x0062:
            return r3
        L_0x0063:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.device.sound.SoundFormatPreferenceController.getFormatPreferenceCheckedState():boolean");
    }

    private boolean getFormatPreferencesEnabledState() {
        return SoundFragment.getSurroundPassthroughSetting(this.mContext) == "manual";
    }

    private HashSet<Integer> getFormatsEnabledInManualMode() {
        HashSet<Integer> formats = new HashSet<>();
        String enabledFormats = Settings.Global.getString(this.mContext.getContentResolver(), "encoded_surround_output_enabled_formats");
        if (enabledFormats == null) {
            formats.addAll(this.mFormats.keySet());
        } else {
            try {
                IntStream mapToInt = Arrays.stream(TextUtils.split(enabledFormats, ",")).mapToInt($$Lambda$SoundFormatPreferenceController$wddj3hVVrg0MkscpMtYt3BzY8Y.INSTANCE);
                Objects.requireNonNull(formats);
                mapToInt.forEach(new IntConsumer(formats) {
                    private final /* synthetic */ HashSet f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void accept(int i) {
                        boolean unused = this.f$0.add(Integer.valueOf(i));
                    }
                });
            } catch (NumberFormatException e) {
                Log.w(TAG, "ENCODED_SURROUND_OUTPUT_ENABLED_FORMATS misformatted.", e);
            }
        }
        return formats;
    }

    private void setSurroundManualFormatsSetting(boolean enabled) {
        HashSet<Integer> formats = getFormatsEnabledInManualMode();
        if (enabled) {
            formats.add(Integer.valueOf(this.mFormatId));
        } else {
            formats.remove(Integer.valueOf(this.mFormatId));
        }
        Settings.Global.putString(this.mContext.getContentResolver(), "encoded_surround_output_enabled_formats", TextUtils.join(",", formats));
    }

    private boolean isReportedFormat() {
        return (this.mReportedFormats == null || this.mReportedFormats.get(Integer.valueOf(this.mFormatId)) == null) ? false : true;
    }
}
