package com.mediatek.wwtv.setting.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import com.mediatek.wwtv.setting.widget.detailui.BaseDialogFragment;

public final class UriUtils {
    private static final String ACCOUNT_IMAGE_CHANGE_NOTIFY_URI = "change_notify_uri";
    private static final String DETAIL_DIALOG_URI_DIALOG_ACTION_START_INDEX = "detail_dialog_action_start_index";
    private static final String DETAIL_DIALOG_URI_DIALOG_ACTION_START_NAME = "detail_dialog_action_start_name";
    private static final String DETAIL_DIALOG_URI_DIALOG_DESCRIPTION = "detail_dialog_description";
    private static final String DETAIL_DIALOG_URI_DIALOG_TITLE = "detail_dialog_title";
    private static final String HTTPS_PREFIX = "https";
    private static final String HTTP_PREFIX = "http";
    private static final String SCHEME_ACCOUNT_IMAGE = "image.account";
    private static final String SCHEME_DELIMITER = "://";
    private static final String SCHEME_SHORTCUT_ICON_RESOURCE = "shortcut.icon.resource";
    private static final String URI_PACKAGE_DELIMITER = ":";
    private static final String URI_PATH_DELIMITER = "/";

    private UriUtils() {
    }

    public static String getAndroidResourceUri(Context context, int resourceId) {
        return getAndroidResourceUri(context.getResources(), resourceId);
    }

    public static String getAndroidResourceUri(Resources resources, int resourceId) {
        return "android.resource://" + resources.getResourceName(resourceId).replace(URI_PACKAGE_DELIMITER, URI_PATH_DELIMITER);
    }

    public static Drawable getDrawable(Context context, Intent.ShortcutIconResource r) throws PackageManager.NameNotFoundException {
        Resources resources = context.getPackageManager().getResourcesForApplication(r.packageName);
        if (resources == null) {
            return null;
        }
        return resources.getDrawable(resources.getIdentifier(r.resourceName, (String) null, (String) null));
    }

    public static Uri getShortcutIconResourceUri(Intent.ShortcutIconResource iconResource) {
        return Uri.parse("shortcut.icon.resource://" + iconResource.packageName + URI_PATH_DELIMITER + iconResource.resourceName.replace(URI_PACKAGE_DELIMITER, URI_PATH_DELIMITER));
    }

    public static Uri getAndroidResourceUri(String resourceName) {
        return Uri.parse("android.resource://" + resourceName.replace(URI_PACKAGE_DELIMITER, URI_PATH_DELIMITER));
    }

    public static boolean isAndroidResourceUri(Uri uri) {
        return "android.resource".equals(uri.getScheme());
    }

    public static Uri getAccountImageUri(String accountName) {
        return Uri.parse("image.account://" + accountName);
    }

    public static Uri getAccountImageUri(String accountName, Uri changeNotifyUri) {
        Uri uri = Uri.parse("image.account://" + accountName);
        if (changeNotifyUri != null) {
            return uri.buildUpon().appendQueryParameter(ACCOUNT_IMAGE_CHANGE_NOTIFY_URI, changeNotifyUri.toString()).build();
        }
        return uri;
    }

    public static boolean isAccountImageUri(Uri uri) {
        if (uri == null) {
            return false;
        }
        return SCHEME_ACCOUNT_IMAGE.equals(uri.getScheme());
    }

    public static String getAccountName(Uri uri) {
        if (isAccountImageUri(uri)) {
            return uri.getAuthority() + uri.getPath();
        }
        throw new IllegalArgumentException("Invalid account image URI. " + uri);
    }

    public static Uri getAccountImageChangeNotifyUri(Uri uri) {
        if (isAccountImageUri(uri)) {
            String notifyUri = uri.getQueryParameter(ACCOUNT_IMAGE_CHANGE_NOTIFY_URI);
            if (notifyUri == null) {
                return null;
            }
            return Uri.parse(notifyUri);
        }
        throw new IllegalArgumentException("Invalid account image URI. " + uri);
    }

    public static boolean isContentUri(Uri uri) {
        return BaseDialogFragment.TAG_CONTENT.equals(uri.getScheme()) || "file".equals(uri.getScheme());
    }

    public static boolean isShortcutIconResourceUri(Uri uri) {
        return SCHEME_SHORTCUT_ICON_RESOURCE.equals(uri.getScheme());
    }

    public static Intent.ShortcutIconResource getIconResource(Uri uri) {
        if (isAndroidResourceUri(uri)) {
            Intent.ShortcutIconResource iconResource = new Intent.ShortcutIconResource();
            iconResource.packageName = uri.getAuthority();
            iconResource.resourceName = uri.toString().substring("android.resource".length() + SCHEME_DELIMITER.length()).replaceFirst(URI_PATH_DELIMITER, URI_PACKAGE_DELIMITER);
            return iconResource;
        } else if (isShortcutIconResourceUri(uri)) {
            Intent.ShortcutIconResource iconResource2 = new Intent.ShortcutIconResource();
            iconResource2.packageName = uri.getAuthority();
            iconResource2.resourceName = uri.toString().substring(SCHEME_SHORTCUT_ICON_RESOURCE.length() + SCHEME_DELIMITER.length() + iconResource2.packageName.length() + URI_PATH_DELIMITER.length()).replaceFirst(URI_PATH_DELIMITER, URI_PACKAGE_DELIMITER);
            return iconResource2;
        } else {
            throw new IllegalArgumentException("Invalid resource URI. " + uri);
        }
    }

    public static boolean isWebUri(Uri resourceUri) {
        String scheme;
        if (resourceUri.getScheme() == null) {
            scheme = null;
        } else {
            scheme = resourceUri.getScheme().toLowerCase();
        }
        return HTTP_PREFIX.equals(scheme) || HTTPS_PREFIX.equals(scheme);
    }

    public static Uri getSubactionDialogUri(Uri uri, String dialogTitle, String dialogDescription) {
        return getSubactionDialogUri(uri, dialogTitle, dialogDescription, (String) null, -1);
    }

    public static Uri getSubactionDialogUri(Uri uri, String dialogTitle, String dialogDescription, int startIndex) {
        return getSubactionDialogUri(uri, dialogTitle, dialogDescription, (String) null, startIndex);
    }

    public static Uri getSubactionDialogUri(Uri uri, String dialogTitle, String dialogDescription, String startName) {
        return getSubactionDialogUri(uri, dialogTitle, dialogDescription, startName, -1);
    }

    public static Uri getSubactionDialogUri(Uri uri, String dialogTitle, String dialogDescription, String startName, int startIndex) {
        if (uri == null || !isContentUri(uri)) {
            return null;
        }
        Uri.Builder builder = uri.buildUpon();
        if (!TextUtils.isEmpty(dialogTitle)) {
            builder.appendQueryParameter(DETAIL_DIALOG_URI_DIALOG_TITLE, dialogTitle);
        }
        if (!TextUtils.isEmpty(DETAIL_DIALOG_URI_DIALOG_DESCRIPTION)) {
            builder.appendQueryParameter(DETAIL_DIALOG_URI_DIALOG_DESCRIPTION, dialogDescription);
        }
        if (startIndex != -1) {
            builder.appendQueryParameter(DETAIL_DIALOG_URI_DIALOG_ACTION_START_INDEX, Integer.toString(startIndex));
        }
        if (!TextUtils.isEmpty(startName)) {
            builder.appendQueryParameter(DETAIL_DIALOG_URI_DIALOG_ACTION_START_NAME, startName);
        }
        return builder.build();
    }

    public static String getSubactionDialogTitle(Uri uri) {
        if (uri == null || !isContentUri(uri)) {
            return null;
        }
        return uri.getQueryParameter(DETAIL_DIALOG_URI_DIALOG_TITLE);
    }

    public static String getSubactionDialogDescription(Uri uri) {
        if (uri == null || !isContentUri(uri)) {
            return null;
        }
        return uri.getQueryParameter(DETAIL_DIALOG_URI_DIALOG_DESCRIPTION);
    }

    public static int getSubactionDialogActionStartIndex(Uri uri) {
        if (uri == null || !isContentUri(uri)) {
            return -1;
        }
        String startIndexStr = uri.getQueryParameter(DETAIL_DIALOG_URI_DIALOG_ACTION_START_INDEX);
        if (TextUtils.isEmpty(startIndexStr) || !TextUtils.isDigitsOnly(startIndexStr)) {
            return -1;
        }
        return Integer.parseInt(startIndexStr);
    }

    public static String getSubactionDialogActionStartName(Uri uri) {
        if (uri == null || !isContentUri(uri)) {
            return null;
        }
        return uri.getQueryParameter(DETAIL_DIALOG_URI_DIALOG_ACTION_START_NAME);
    }
}
