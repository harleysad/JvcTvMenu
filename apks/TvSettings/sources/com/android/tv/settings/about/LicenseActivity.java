package com.android.tv.settings.about;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.android.settingslib.license.LicenseHtmlLoader;
import com.android.tv.settings.R;
import com.android.tv.settings.dialog.old.BaseDialogFragment;
import java.io.File;

public class LicenseActivity extends Activity implements LoaderManager.LoaderCallbacks<File> {
    private static final String DEFAULT_LICENSE_PATH = "/system/etc/NOTICE.html.gz";
    private static final String FILE_PROVIDER_AUTHORITY = "com.android.settings.files";
    private static final int LOADER_ID_LICENSE_HTML_LOADER = 0;
    private static final String PROPERTY_LICENSE_PATH = "ro.config.license_path";
    private static final String TAG = "LicenseActivity";

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String licenseHtmlPath = SystemProperties.get(PROPERTY_LICENSE_PATH, DEFAULT_LICENSE_PATH);
        if (isFilePathValid(licenseHtmlPath)) {
            showSelectedFile(licenseHtmlPath);
        } else {
            showHtmlFromDefaultXmlFiles();
        }
    }

    public Loader<File> onCreateLoader(int id, Bundle args) {
        return new LicenseHtmlLoader(this);
    }

    public void onLoadFinished(Loader<File> loader, File generatedHtmlFile) {
        showGeneratedHtmlFile(generatedHtmlFile);
    }

    public void onLoaderReset(Loader<File> loader) {
    }

    private void showHtmlFromDefaultXmlFiles() {
        getLoaderManager().initLoader(0, Bundle.EMPTY, this);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Uri getUriFromGeneratedHtmlFile(File generatedHtmlFile) {
        return FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, generatedHtmlFile);
    }

    private void showGeneratedHtmlFile(File generatedHtmlFile) {
        if (generatedHtmlFile != null) {
            showHtmlFromUri(getUriFromGeneratedHtmlFile(generatedHtmlFile));
            return;
        }
        Log.e(TAG, "Failed to generate.");
        showErrorAndFinish();
    }

    private void showSelectedFile(String path) {
        if (TextUtils.isEmpty(path)) {
            Log.e(TAG, "The system property for the license file is empty");
            showErrorAndFinish();
            return;
        }
        File file = new File(path);
        if (!isFileValid(file)) {
            Log.e(TAG, "License file " + path + " does not exist");
            showErrorAndFinish();
            return;
        }
        showHtmlFromUri(Uri.fromFile(file));
    }

    private void showHtmlFromUri(Uri uri) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        intent.putExtra("android.intent.extra.TITLE", getString(R.string.about_legal_license));
        if (BaseDialogFragment.TAG_CONTENT.equals(uri.getScheme())) {
            intent.addFlags(1);
        }
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setPackage("com.android.htmlviewer");
        try {
            startActivity(intent);
            finish();
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to find viewer", e);
            showErrorAndFinish();
        }
    }

    private void showErrorAndFinish() {
        Toast.makeText(this, R.string.about_license_activity_unavailable, 1).show();
        finish();
    }

    private boolean isFilePathValid(String path) {
        return !TextUtils.isEmpty(path) && isFileValid(new File(path));
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isFileValid(File file) {
        return file.exists() && file.length() != 0;
    }
}
