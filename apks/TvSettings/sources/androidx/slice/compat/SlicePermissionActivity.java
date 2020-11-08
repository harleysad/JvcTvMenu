package androidx.slice.compat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.RestrictTo;
import android.support.v4.text.BidiFormatter;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import androidx.slice.core.R;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public class SlicePermissionActivity extends Activity implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private static final float MAX_LABEL_SIZE_PX = 500.0f;
    private static final String TAG = "SlicePermissionActivity";
    private String mCallingPkg;
    private String mProviderPkg;
    private Uri mUri;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mUri = (Uri) getIntent().getParcelableExtra(SliceProviderCompat.EXTRA_BIND_URI);
        this.mCallingPkg = getIntent().getStringExtra(SliceProviderCompat.EXTRA_PKG);
        this.mProviderPkg = getIntent().getStringExtra(SliceProviderCompat.EXTRA_PROVIDER_PKG);
        try {
            PackageManager pm = getPackageManager();
            CharSequence app1 = BidiFormatter.getInstance().unicodeWrap(loadSafeLabel(pm, pm.getApplicationInfo(this.mCallingPkg, 0)).toString());
            CharSequence app2 = BidiFormatter.getInstance().unicodeWrap(loadSafeLabel(pm, pm.getApplicationInfo(this.mProviderPkg, 0)).toString());
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle((CharSequence) getString(R.string.abc_slice_permission_title, new Object[]{app1, app2})).setView(R.layout.abc_slice_permission_request).setNegativeButton(R.string.abc_slice_permission_deny, (DialogInterface.OnClickListener) this).setPositiveButton(R.string.abc_slice_permission_allow, (DialogInterface.OnClickListener) this).setOnDismissListener(this).show();
            ((TextView) dialog.getWindow().getDecorView().findViewById(R.id.text1)).setText(getString(R.string.abc_slice_permission_text_1, new Object[]{app2}));
            ((TextView) dialog.getWindow().getDecorView().findViewById(R.id.text2)).setText(getString(R.string.abc_slice_permission_text_2, new Object[]{app2}));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Couldn't find package", e);
            finish();
        }
    }

    private CharSequence loadSafeLabel(PackageManager pm, ApplicationInfo appInfo) {
        String labelStr = Html.fromHtml(appInfo.loadLabel(pm).toString()).toString();
        int labelLength = labelStr.length();
        String labelStr2 = labelStr;
        int offset = 0;
        while (true) {
            if (offset >= labelLength) {
                break;
            }
            int codePoint = labelStr2.codePointAt(offset);
            int type = Character.getType(codePoint);
            if (type == 13 || type == 15 || type == 14) {
                labelStr2 = labelStr2.substring(0, offset);
            } else {
                if (type == 12) {
                    labelStr2 = labelStr2.substring(0, offset) + " " + labelStr2.substring(Character.charCount(codePoint) + offset);
                }
                offset += Character.charCount(codePoint);
            }
        }
        String labelStr3 = labelStr2.trim();
        if (labelStr3.isEmpty()) {
            return appInfo.packageName;
        }
        TextPaint paint = new TextPaint();
        paint.setTextSize(42.0f);
        return TextUtils.ellipsize(labelStr3, paint, MAX_LABEL_SIZE_PX, TextUtils.TruncateAt.END);
    }

    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) {
            SliceProviderCompat.grantSlicePermission(this, getPackageName(), this.mCallingPkg, this.mUri.buildUpon().path("").build());
        }
        finish();
    }

    public void onDismiss(DialogInterface dialog) {
        finish();
    }
}
