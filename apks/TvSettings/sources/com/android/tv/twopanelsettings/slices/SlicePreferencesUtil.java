package com.android.tv.twopanelsettings.slices;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.text.TextUtils;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.compat.SliceProviderCompat;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import com.android.tv.settings.dialog.old.BaseDialogFragment;
import com.android.tv.twopanelsettings.IconUtil;
import com.android.tv.twopanelsettings.R;
import java.util.ArrayList;
import java.util.List;

public final class SlicePreferencesUtil {
    static Preference getPreference(SliceItem item, ContextThemeWrapper contextThemeWrapper, String className) {
        Preference preference = null;
        Data data = extract(item);
        boolean subtitleExists = false;
        if (item.getSubType() != null) {
            String subType = item.getSubType();
            if (subType.equals("TYPE_PREFERENCE") || subType.equals("TYPE_PREFERENCE_EMBEDDED")) {
                if (data.mInfoItems.size() <= 0) {
                    if (data.mIntentItem == null) {
                        if (data.mEndItems.size() > 0 && data.mEndItems.get(0) != null) {
                            SliceActionImpl action = new SliceActionImpl(data.mEndItems.get(0));
                            switch (getButtonStyle(item)) {
                                case 0:
                                    preference = new SliceSwitchPreference((Context) contextThemeWrapper, action);
                                    break;
                                case 1:
                                    preference = new SliceCheckboxPreference(contextThemeWrapper, action);
                                    break;
                                case 2:
                                    preference = new SliceRadioPreference(contextThemeWrapper, action);
                                    preference.setLayoutResource(R.layout.preference_reversed_widget);
                                    break;
                            }
                        }
                    } else {
                        SliceActionImpl action2 = new SliceActionImpl(data.mIntentItem);
                        preference = new SlicePreference(contextThemeWrapper);
                        ((SlicePreference) preference).setSliceAction(action2);
                        if (data.mFollowupIntentItem != null) {
                            ((SlicePreference) preference).setFollowupSliceAction(new SliceActionImpl(data.mFollowupIntentItem));
                        }
                    }
                } else {
                    preference = new InfoPreference(contextThemeWrapper, getInfoList(data.mInfoItems));
                }
                CharSequence uri = getText(data.mTargetSliceItem);
                if (uri != null && !TextUtils.isEmpty(uri)) {
                    if (preference == null) {
                        preference = new SlicePreference(contextThemeWrapper);
                    }
                    ((SlicePreference) preference).setUri(uri.toString());
                    preference.setFragment(className);
                } else if (preference == null) {
                    preference = new Preference(contextThemeWrapper);
                }
            } else if (item.getSubType().equals("TYPE_PREFERENCE_CATEGORY")) {
                preference = new PreferenceCategory(contextThemeWrapper);
            }
        }
        if (preference != null) {
            if ((preference instanceof InfoPreference) || !enabled(item)) {
                preference.setEnabled(false);
            }
            CharSequence key = getKey(item);
            if (key != null) {
                preference.setKey(key.toString());
            }
            Icon icon = getIcon(data.mStartItem);
            if (icon != null) {
                boolean isIconNeedToBeProcessed = isIconNeedsToBeProcessed(item);
                Drawable iconDrawable = icon.loadDrawable(contextThemeWrapper);
                if (isIconNeedToBeProcessed) {
                    preference.setIcon(IconUtil.getCompoundIcon(contextThemeWrapper, iconDrawable));
                } else {
                    preference.setIcon(iconDrawable);
                }
            }
            if (data.mTitleItem != null) {
                preference.setTitle(getText(data.mTitleItem));
            }
            CharSequence subtitle = data.mSubtitleItem != null ? data.mSubtitleItem.getText() : null;
            if (!TextUtils.isEmpty(subtitle) || (data.mSubtitleItem != null && data.mSubtitleItem.hasHint("partial"))) {
                subtitleExists = true;
            }
            if (subtitleExists) {
                preference.setSummary(subtitle);
            } else if (data.mSummaryItem != null) {
                preference.setSummary(getText(data.mSummaryItem));
            }
        }
        return preference;
    }

    static class Data {
        List<SliceItem> mEndItems = new ArrayList();
        SliceItem mFollowupIntentItem;
        List<SliceItem> mInfoItems = new ArrayList();
        SliceItem mIntentItem;
        SliceItem mStartItem;
        SliceItem mSubtitleItem;
        SliceItem mSummaryItem;
        SliceItem mTargetSliceItem;
        SliceItem mTitleItem;

        Data() {
        }
    }

    static Data extract(SliceItem sliceItem) {
        Data data = new Data();
        List<SliceItem> possibleStartItems = SliceQuery.findAll(sliceItem, (String) null, "title", (String) null);
        if (possibleStartItems.size() > 0) {
            String format = possibleStartItems.get(0).getFormat();
            if ((BaseDialogFragment.TAG_ACTION.equals(format) && SliceQuery.find(possibleStartItems.get(0), "image") != null) || SliceProviderCompat.EXTRA_SLICE.equals(format) || "long".equals(format) || "image".equals(format)) {
                data.mStartItem = possibleStartItems.get(0);
            }
        }
        List<SliceItem> items = sliceItem.getSlice().getItems();
        for (int i = 0; i < items.size(); i++) {
            SliceItem item = items.get(i);
            String subType = item.getSubType();
            if (subType != null) {
                char c = 65535;
                int hashCode = subType.hashCode();
                if (hashCode != -2115433654) {
                    if (hashCode != -1471081183) {
                        if (hashCode != 1227665283) {
                            if (hashCode == 1766958119 && subType.equals("SUBTYPE_INFO_PREFERENCE")) {
                                c = 0;
                            }
                        } else if (subType.equals("TAG_TARGET_URI")) {
                            c = 3;
                        }
                    } else if (subType.equals("SUBTYPE_INTENT")) {
                        c = 1;
                    }
                } else if (subType.equals("SUBTYPE_FOLLOWUP_INTENT")) {
                    c = 2;
                }
                switch (c) {
                    case 0:
                        data.mInfoItems.add(item);
                        break;
                    case 1:
                        data.mIntentItem = item;
                        break;
                    case 2:
                        data.mFollowupIntentItem = item;
                        break;
                    case 3:
                        data.mTargetSliceItem = item;
                        break;
                }
            } else if (!"text".equals(item.getFormat()) || item.getSubType() != null) {
                data.mEndItems.add(item);
            } else if ((data.mTitleItem == null || !data.mTitleItem.hasHint("title")) && item.hasHint("title") && !item.hasHint("summary")) {
                data.mTitleItem = item;
            } else if (data.mSubtitleItem == null && !item.hasHint("summary")) {
                data.mSubtitleItem = item;
            } else if (data.mSummaryItem == null && item.hasHint("summary")) {
                data.mSummaryItem = item;
            }
        }
        data.mEndItems.remove(data.mStartItem);
        return data;
    }

    private static List<Pair<CharSequence, CharSequence>> getInfoList(List<SliceItem> sliceItems) {
        List<Pair<CharSequence, CharSequence>> infoList = new ArrayList<>();
        for (SliceItem item : sliceItems) {
            Slice itemSlice = item.getSlice();
            if (itemSlice != null) {
                CharSequence title = null;
                CharSequence summary = null;
                for (SliceItem element : itemSlice.getItems()) {
                    if (element.getHints().contains("title")) {
                        title = element.getText();
                    } else if (element.getHints().contains("summary")) {
                        summary = element.getText();
                    }
                }
                infoList.add(new Pair(title, summary));
            }
        }
        return infoList;
    }

    private static CharSequence getKey(SliceItem item) {
        SliceItem target = SliceQuery.findSubtype(item, "text", "TAG_KEY");
        if (target != null) {
            return target.getText();
        }
        return null;
    }

    static SliceItem getScreenTitleItem(List<SliceItem> sliceItems) {
        for (SliceItem item : sliceItems) {
            if (item.getSubType() != null && item.getSubType().equals("TYPE_PREFERENCE_SCREEN_TITLE")) {
                return item;
            }
        }
        return null;
    }

    static SliceItem getEmbeddedItem(List<SliceItem> sliceItems) {
        for (SliceItem item : sliceItems) {
            if (item.getSubType() != null && item.getSubType().equals("TYPE_PREFERENCE_EMBEDDED")) {
                return item;
            }
        }
        return null;
    }

    private static boolean isIconNeedsToBeProcessed(SliceItem sliceItem) {
        for (SliceItem item : sliceItem.getSlice().getItems()) {
            if (item.getSubType() != null && item.getSubType().equals("SUBTYPE_ICON_NEED_TO_BE_PROCESSED")) {
                if (item.getInt() == 1) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    private static int getButtonStyle(SliceItem sliceItem) {
        for (SliceItem item : sliceItem.getSlice().getItems()) {
            if (item.getSubType() != null && item.getSubType().equals("SUBTYPE_BUTTON_STYLE")) {
                return item.getInt();
            }
        }
        return -1;
    }

    private static boolean enabled(SliceItem sliceItem) {
        for (SliceItem item : sliceItem.getSlice().getItems()) {
            if (item.getSubType() != null && item.getSubType().equals("SUBTYPE_IS_ENABLED")) {
                if (item.getInt() == 1) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }

    static CharSequence getText(SliceItem item) {
        if (item == null) {
            return null;
        }
        return item.getText();
    }

    static Icon getIcon(SliceItem startItem) {
        if (startItem == null || startItem.getSlice() == null || startItem.getSlice().getItems() == null || startItem.getSlice().getItems().size() <= 0) {
            return null;
        }
        SliceItem iconItem = startItem.getSlice().getItems().get(0);
        if ("image".equals(iconItem.getFormat())) {
            return iconItem.getIcon().toIcon();
        }
        return null;
    }

    static Uri getStatusPath(String uriString) {
        return Uri.parse(uriString).buildUpon().path("/status").build();
    }
}
