package com.android.tv.settings.autofill;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.autofill.AutofillManager;

public class AutofillPickerTrampolineActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String packageName = intent.getData().getSchemeSpecificPart();
        ComponentName currentService = AutofillHelper.getCurrentAutofillAsComponentName(this);
        if (currentService == null || !currentService.getPackageName().equals(packageName)) {
            AutofillManager afm = (AutofillManager) getSystemService(AutofillManager.class);
            if (afm == null || !afm.hasAutofillFeature() || !afm.isAutofillSupported()) {
                setResult(0);
                finish();
                return;
            }
            startActivity(new Intent(this, AutofillPickerActivity.class).setFlags(33554432).setData(intent.getData()));
            finish();
            return;
        }
        setResult(-1);
        finish();
    }
}
