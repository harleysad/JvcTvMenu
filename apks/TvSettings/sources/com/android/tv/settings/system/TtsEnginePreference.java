package com.android.tv.settings.system;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.speech.tts.TextToSpeech;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.Log;
import android.widget.Checkable;
import android.widget.RadioButton;
import com.android.tv.settings.R;

public class TtsEnginePreference extends Preference {
    private static final String TAG = "TtsEnginePreference";
    private final TextToSpeech.EngineInfo mEngineInfo;
    private RadioButton mRadioButton;
    private final RadioButtonGroupState mSharedState;

    public interface RadioButtonGroupState {
        Checkable getCurrentChecked();

        String getCurrentKey();

        void setCurrentChecked(Checkable checkable);

        void setCurrentKey(String str);
    }

    public TtsEnginePreference(Context context, TextToSpeech.EngineInfo info, RadioButtonGroupState state) {
        super(context);
        setWidgetLayoutResource(R.layout.radio_preference_widget);
        this.mSharedState = state;
        this.mEngineInfo = info;
        setKey(this.mEngineInfo.name);
        setTitle((CharSequence) this.mEngineInfo.label);
    }

    public void onBindViewHolder(PreferenceViewHolder viewHolder) {
        super.onBindViewHolder(viewHolder);
        RadioButton rb = (RadioButton) viewHolder.findViewById(16908289);
        boolean isChecked = getKey().equals(this.mSharedState.getCurrentKey());
        if (isChecked) {
            this.mSharedState.setCurrentChecked(rb);
        }
        rb.setChecked(isChecked);
        this.mRadioButton = rb;
    }

    /* access modifiers changed from: protected */
    public void onClick() {
        super.onClick();
        onRadioButtonClicked(this.mRadioButton, !this.mRadioButton.isChecked());
    }

    private boolean shouldDisplayDataAlert() {
        return !this.mEngineInfo.system;
    }

    private void displayDataAlert(DialogInterface.OnClickListener positiveOnClickListener, DialogInterface.OnClickListener negativeOnClickListener) {
        Log.i(TAG, "Displaying data alert for :" + this.mEngineInfo.name);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(17039380).setMessage(getContext().getString(R.string.tts_engine_security_warning, new Object[]{this.mEngineInfo.label})).setCancelable(true).setPositiveButton(17039370, positiveOnClickListener).setNegativeButton(17039360, negativeOnClickListener);
        builder.create().show();
    }

    private void onRadioButtonClicked(final Checkable buttonView, boolean isChecked) {
        if (this.mSharedState.getCurrentChecked() == buttonView || !isChecked) {
            return;
        }
        if (shouldDisplayDataAlert()) {
            displayDataAlert(new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    TtsEnginePreference.this.makeCurrentEngine(buttonView);
                }
            }, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    buttonView.setChecked(false);
                }
            });
        } else {
            makeCurrentEngine(buttonView);
        }
    }

    /* access modifiers changed from: private */
    public void makeCurrentEngine(Checkable current) {
        if (this.mSharedState.getCurrentChecked() != null) {
            this.mSharedState.getCurrentChecked().setChecked(false);
        }
        this.mSharedState.setCurrentChecked(current);
        this.mSharedState.setCurrentKey(getKey());
        callChangeListener(this.mSharedState.getCurrentKey());
    }
}
