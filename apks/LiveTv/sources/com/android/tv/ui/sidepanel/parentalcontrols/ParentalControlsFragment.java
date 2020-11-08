package com.android.tv.ui.sidepanel.parentalcontrols;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.android.tv.dialog.PinDialogFragment;
import com.android.tv.ui.sidepanel.ActionItem;
import com.android.tv.ui.sidepanel.Item;
import com.android.tv.ui.sidepanel.SideFragment;
import com.android.tv.ui.sidepanel.SideFragmentManager;
import com.android.tv.ui.sidepanel.SubMenuItem;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.input.InputUtil;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.List;

public class ParentalControlsFragment extends SideFragment {
    private static final String TAG = "ParentalControlsFragment";
    private boolean isComponentSource;
    private boolean isCompositeSource;
    private boolean isTVOrCTSSource;
    private List<ActionItem> mActionItems;
    /* access modifiers changed from: private */
    public EditChannel mEidtChannel;
    /* access modifiers changed from: private */
    public final SideFragment.SideFragmentListener mSideFragmentListener;

    public void onCreate(Bundle savedInstanceState) {
        this.mEidtChannel = EditChannel.getInstance(getActivity().getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    public ParentalControlsFragment() {
        this.isTVOrCTSSource = true;
        this.isComponentSource = true;
        this.isCompositeSource = true;
        this.mSideFragmentListener = new SideFragment.SideFragmentListener() {
            public void onSideFragmentViewDestroyed() {
                ParentalControlsFragment.this.notifyDataSetChanged();
            }
        };
        this.isTVOrCTSSource = isTVOrCTSSource();
        this.isComponentSource = isComponentSource();
        this.isCompositeSource = isCompositeSource();
    }

    private boolean isTVOrCTSSource() {
        String path = CommonIntegration.getInstance().getCurrentFocus();
        boolean isCurrentTvSource = InputSourceManager.getInstance().isCurrentTvSource(path);
        boolean isCTSSource = InputSourceManager.getInstance().isCTSSource(path);
        boolean isTVOrCTSSource2 = isCurrentTvSource || isCTSSource;
        MtkLog.d(TAG, "isCurrentTvSourc=" + isCurrentTvSource + ",isCTSSource=" + isCTSSource + ",isTVOrCTSSource:" + isTVOrCTSSource2);
        return isTVOrCTSSource2;
    }

    private boolean isComponentSource() {
        int inputID = InputSourceManager.getInstance().getCurrentInputSourceHardwareId();
        MtkLog.d(TAG, "isComponentSource inputID = " + inputID);
        return InputUtil.getInput(inputID).isComponent();
    }

    private boolean isCompositeSource() {
        int inputID = InputSourceManager.getInstance().getCurrentInputSourceHardwareId();
        MtkLog.d(TAG, "isCompositeSource inputID = " + inputID);
        return InputUtil.getInput(inputID).isComposite();
    }

    private boolean isCTSSource() {
        return InputSourceManager.getInstance().isCurrentTvSource(CommonIntegration.getInstance().getCurrentFocus());
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        return getString(R.string.menu_channel_parental_controls);
    }

    /* access modifiers changed from: protected */
    public List<Item> getItemList() {
        List<Item> items = new ArrayList<>();
        if (this.isTVOrCTSSource || this.isComponentSource || this.isCompositeSource) {
            if (!this.isComponentSource && !this.isCompositeSource) {
                SubMenuItem channelBlockItem = new ChannelBlockItem(getString(R.string.option_channels_locked), "", this.mSideFragmentManager);
                channelBlockItem.setEnabled(CommonIntegration.getInstance().getChannelAllNumByAPI() > 0);
                items.add(channelBlockItem);
            }
            items.add(new ProgramBlockItem(getString(R.string.option_program_restrictions), ProgramRestrictionsFragment.getDescription(getMainActivity()), this.mSideFragmentManager));
            if (CommonIntegration.isSARegion()) {
                items.add(new ChannelScheduleBlockItem(getString(R.string.menu_parental_channel_schedule_block), getMainActivity().getSideFragmentManager()));
            }
        }
        items.add(new SubMenuItem(getString(R.string.option_inputs_locked), this.mSideFragmentManager) {
            /* access modifiers changed from: protected */
            public SideFragment getFragment() {
                SideFragment fragment = new InputsBlockedFragment();
                fragment.setListener(ParentalControlsFragment.this.mSideFragmentListener);
                return fragment;
            }
        });
        items.add(new ActionItem(getString(R.string.option_change_pin)) {
            /* access modifiers changed from: protected */
            public void onSelected() {
                PinDialogFragment fragment = PinDialogFragment.create(3);
                ParentalControlsFragment.this.getMainActivity().hide();
                fragment.show(ParentalControlsFragment.this.getMainActivity().getFragmentManager(), "PinDialogFragment");
            }
        });
        return items;
    }

    class ChannelScheduleBlockItem extends SubMenuItem {
        public ChannelScheduleBlockItem(String title, SideFragmentManager fragmentManager) {
            super(title, fragmentManager);
        }

        /* access modifiers changed from: protected */
        public SideFragment getFragment() {
            SideFragment fragment = new ChannelScheduleBlockedFragment();
            fragment.setListener(ParentalControlsFragment.this.mSideFragmentListener);
            return fragment;
        }
    }

    class ProgramBlockItem extends SubMenuItem {
        public ProgramBlockItem(String title, SideFragmentManager fragmentManager) {
            super(title, fragmentManager);
        }

        public ProgramBlockItem(String title, String description, SideFragmentManager fragmentManager) {
            super(title, description, fragmentManager);
        }

        /* access modifiers changed from: protected */
        public SideFragment getFragment() {
            SideFragment fragment = new ProgramRestrictionsFragment();
            fragment.setListener(ParentalControlsFragment.this.mSideFragmentListener);
            return fragment;
        }
    }

    class ChannelBlockItem extends SubMenuItem {
        TextView mDescriptionView;

        public ChannelBlockItem(String title, SideFragmentManager fragmentManager) {
            super(title, fragmentManager);
        }

        public ChannelBlockItem(String title, String description, SideFragmentManager fragmentManager) {
            super(title, description, fragmentManager);
        }

        /* access modifiers changed from: protected */
        public SideFragment getFragment() {
            SideFragment fragment = new ChannelsBlockedFragment();
            fragment.setListener(ParentalControlsFragment.this.mSideFragmentListener);
            return fragment;
        }

        /* access modifiers changed from: protected */
        public void onBind(View view) {
            super.onBind(view);
            this.mDescriptionView = (TextView) view.findViewById(R.id.description);
        }

        /* access modifiers changed from: protected */
        public void onUpdate() {
            super.onUpdate();
            int lockedAndBrowsableChannelCount = ParentalControlsFragment.this.mEidtChannel.getBlockChannelNumForSource();
            MtkLog.d(ParentalControlsFragment.TAG, "lockedAndBrowsableChannelCount:" + lockedAndBrowsableChannelCount);
            if (lockedAndBrowsableChannelCount > 0) {
                this.mDescriptionView.setText(Integer.toString(lockedAndBrowsableChannelCount));
            } else {
                this.mDescriptionView.setText(ParentalControlsFragment.this.getMainActivity().getString(R.string.option_no_locked_channel));
            }
        }

        /* access modifiers changed from: protected */
        public void onUnbind() {
            super.onUnbind();
            this.mDescriptionView = null;
        }
    }
}
