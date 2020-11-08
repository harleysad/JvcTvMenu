package com.android.tv.ui.sidepanel;

import android.os.Bundle;
import com.mediatek.wwtv.setting.util.LanguageUtil;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.nav.util.SundryImplement;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import java.util.ArrayList;
import java.util.List;

public class MultiAudioFragment extends SideFragment {
    /* access modifiers changed from: private */
    public String key = "g_audio__aud_mts";
    /* access modifiers changed from: private */
    public MenuConfigManager mConfigManager;
    private int mInitialSelectedPosition = -1;
    private LanguageUtil mOsdLanguage;
    private String[] multiAudio;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mConfigManager = MenuConfigManager.getInstance(getActivity());
        CommonIntegration ci = TvSingletons.getSingletons().getCommonIntegration();
        this.mOsdLanguage = new LanguageUtil(getActivity().getApplicationContext());
        this.multiAudio = getActivity().getResources().getStringArray(R.array.menu_tv_audio_channel_mts_array);
        if (CommonIntegration.getInstance().isCurrentSourceATV() || !ci.isCurrentSourceHasSignal()) {
            SundryImplement sundryIm = SundryImplement.getInstanceNavSundryImplement(getActivity());
            this.multiAudio = sundryIm.getAllMtsModes();
            this.mInitialSelectedPosition = getIndexOfMtsMode(this.multiAudio, sundryIm.getMtsModeString(TVContent.getInstance(getActivity()).getConfigValue("g_audio__aud_mts")));
            return;
        }
        this.multiAudio = getActivity().getResources().getStringArray(R.array.menu_tv_audio_language_array);
        this.key = "g_aud_lang__aud_language";
        this.mInitialSelectedPosition = this.mOsdLanguage.getAudioLanguage(this.key);
    }

    private int getIndexOfMtsMode(String[] modes, String mode) {
        for (int i = 0; i < modes.length; i++) {
            if (mode.equalsIgnoreCase(modes[i])) {
                return i;
            }
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        return getString(R.string.options_item_multi_audio);
    }

    /* access modifiers changed from: protected */
    public List getItemList() {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < this.multiAudio.length; i++) {
            MultiAudioOptionItem item = new MultiAudioOptionItem(this.multiAudio[i], i);
            if (this.mInitialSelectedPosition == i) {
                item.setChecked(true);
            }
            items.add(item);
        }
        return items;
    }

    public void onResume() {
        super.onResume();
        if (this.mInitialSelectedPosition != -1 && this.mInitialSelectedPosition < this.multiAudio.length) {
            setSelectedPosition(this.mInitialSelectedPosition);
        }
    }

    private class MultiAudioOptionItem extends RadioButtonItem {
        private int position;

        public MultiAudioOptionItem(String title, int position2) {
            super(title);
            this.position = position2;
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            super.onSelected();
            MultiAudioFragment.this.mConfigManager.setValue(MultiAudioFragment.this.key, this.position);
            MultiAudioFragment.this.getActivity().finish();
        }
    }

    /* access modifiers changed from: protected */
    public int getFragmentLayoutResourceId() {
        return R.layout.multi_audio_fragment;
    }
}
