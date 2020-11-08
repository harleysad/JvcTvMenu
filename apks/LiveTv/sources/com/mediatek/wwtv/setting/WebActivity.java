package com.mediatek.wwtv.setting;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import com.android.tv.util.LicenseUtils;
import com.mediatek.wwtv.tvcenter.R;

public class WebActivity extends Activity {
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_URL = "url";

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String titleString = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(titleString)) {
            setTitle(titleString);
        }
        String stringExtra = getIntent().getStringExtra(EXTRA_URL);
        TextView textView = new TextView(this);
        textView.setText(LicenseUtils.getTextFromResource(this, R.raw.third_party_licenses, 0, -1));
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setBackgroundColor(-1);
        textView.setTextColor(ViewCompat.MEASURED_STATE_MASK);
        setContentView(textView);
    }
}
