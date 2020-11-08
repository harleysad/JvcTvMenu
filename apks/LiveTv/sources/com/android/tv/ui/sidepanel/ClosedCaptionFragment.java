package com.android.tv.ui.sidepanel;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.tv.TvTrackInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClosedCaptionFragment extends SideFragment {
    private static final String TAG = "ClosedCaptionFragment";
    private static final String TRACKER_LABEL = "closed caption";
    private String mClosedCaptionLanguage;
    private int mClosedCaptionOption;
    private String mClosedCaptionTrackId;
    private boolean mResetClosedCaption;
    /* access modifiers changed from: private */
    public ClosedCaptionOptionItem mSelectedItem;
    private int mSelectedPosition = 0;

    public ClosedCaptionFragment() {
        super(KeyMap.KEYCODE_MTKIR_MTKIR_CC, 47);
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        if (1 == MarketRegionInfo.getCurrentMarketRegion() || 2 == MarketRegionInfo.getCurrentMarketRegion()) {
            return getString(R.string.side_panel_title_closed_caption);
        }
        return getString(R.string.menu_setup_subtitle);
    }

    /* access modifiers changed from: protected */
    public void refreshUI() {
        setSelectedPosition(this.mSelectedPosition);
    }

    /* access modifiers changed from: protected */
    public List<Item> getItemList() {
        String selectedTrackId;
        List<Item> items = new ArrayList<>();
        this.mSelectedItem = null;
        TurnkeyUiMainActivity.getInstance().getTvView().setCaptionEnabled(true);
        String currentCCId = TurnkeyUiMainActivity.getInstance().getTvView().getSelectedTrack(2);
        List<TvTrackInfo> tracks = TurnkeyUiMainActivity.getInstance().getTvView().getTracks(2);
        MtkLog.d(TAG, "currentCCId=" + currentCCId);
        if (tracks != null && !tracks.isEmpty()) {
            if (currentCCId != null) {
                int i = 0;
                while (i < tracks.size() && !currentCCId.equals(tracks.get(i).getId())) {
                    i++;
                }
                selectedTrackId = tracks.get(i % tracks.size()).getId();
            } else {
                selectedTrackId = null;
            }
            MtkLog.d(TAG, "selectedTrackId=" + selectedTrackId);
            ClosedCaptionOptionItem item = new ClosedCaptionOptionItem((TvTrackInfo) null, (Integer) null);
            items.add(item);
            if (selectedTrackId == null) {
                this.mSelectedItem = item;
                item.setChecked(true);
                this.mSelectedPosition = 0;
            }
            for (int i2 = 0; i2 < tracks.size(); i2++) {
                ClosedCaptionOptionItem item2 = new ClosedCaptionOptionItem(tracks.get(i2), Integer.valueOf(i2));
                if (TextUtils.equals(selectedTrackId, tracks.get(i2).getId())) {
                    this.mSelectedItem = item2;
                    item2.setChecked(true);
                    this.mSelectedPosition = i2 + 1;
                }
                items.add(item2);
            }
        }
        items.add(new ActionItem(getString(R.string.closed_caption_system_settings), getString(R.string.closed_caption_system_settings_description)) {
            /* access modifiers changed from: protected */
            public void onSelected() {
                try {
                    ClosedCaptionFragment.this.getMainActivity().startActivity(new Intent("android.settings.CAPTIONING_SETTINGS"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ClosedCaptionFragment.this.getMainActivity(), ClosedCaptionFragment.this.getString(R.string.msg_unable_to_start_system_captioning_settings), 0).show();
                }
            }

            /* access modifiers changed from: protected */
            public void onFocused() {
                super.onFocused();
            }
        });
        return items;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /* access modifiers changed from: private */
    public String getLabel(TvTrackInfo track, Integer trackIndex) {
        if (track == null) {
            return getString(R.string.closed_caption_option_item_off);
        }
        if (track.getLanguage() != null) {
            return new Locale(track.getLanguage()).getDisplayName();
        }
        return getString(R.string.closed_caption_unknown_language, new Object[]{Integer.valueOf(trackIndex.intValue() + 1)});
    }

    private class ClosedCaptionOptionItem extends RadioButtonItem {
        private final int mOption;
        private final String mTrackId;

        private ClosedCaptionOptionItem(TvTrackInfo track, Integer trackIndex) {
            super(ClosedCaptionFragment.this.getLabel(track, trackIndex));
            if (track == null) {
                this.mOption = 0;
                this.mTrackId = null;
                return;
            }
            this.mOption = 1;
            this.mTrackId = track.getId();
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            super.onSelected();
            ClosedCaptionOptionItem unused = ClosedCaptionFragment.this.mSelectedItem = this;
            MtkLog.d(ClosedCaptionFragment.TAG, "onSelected mTrackId=" + this.mTrackId);
            TurnkeyUiMainActivity.getInstance().getTvView().selectTrack(2, this.mTrackId);
            ClosedCaptionFragment.this.getActivity().finish();
        }
    }

    /* access modifiers changed from: protected */
    public int getFragmentLayoutResourceId() {
        return R.layout.multi_audio_fragment;
    }
}
