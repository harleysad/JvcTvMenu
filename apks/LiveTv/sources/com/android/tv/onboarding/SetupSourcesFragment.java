package com.android.tv.onboarding;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedActionsStylist;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.tv.common.ui.setup.SetupGuidedStepFragment;
import com.android.tv.common.ui.setup.SetupMultiPaneFragment;
import com.android.tv.data.TvInputNewComparator;
import com.android.tv.ui.GuidedActionsStylistWithDivider;
import com.android.tv.util.SetupUtils;
import com.android.tv.util.TvInputManagerHelper;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetupSourcesFragment extends SetupMultiPaneFragment {
    public static final String ACTION_CATEGORY = "com.android.tv.onboarding.SetupSourcesFragment";
    public static final int ACTION_ONLINE_STORE = 1;
    public static final String ACTION_PARAM_KEY_INPUT_ID = "input_id";
    public static final int ACTION_SETUP_INPUT = 2;
    private static final String DTV_ID = "com.mediatek.tvinput/.tuner.TunerInputService/HW0";
    private static final String SETUP_TRACKER_LABEL = "Setup fragment";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /* access modifiers changed from: protected */
    public void onEnterTransitionEnd() {
        SetupGuidedStepFragment f = getContentFragment();
        if (f instanceof ContentFragment) {
            ((ContentFragment) f).executePendingAction();
        }
    }

    /* access modifiers changed from: protected */
    public SetupGuidedStepFragment onCreateContentFragment() {
        SetupGuidedStepFragment f = new ContentFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(SetupGuidedStepFragment.KEY_THREE_PANE, true);
        f.setArguments(arguments);
        return f;
    }

    /* access modifiers changed from: protected */
    public String getActionCategory() {
        return ACTION_CATEGORY;
    }

    public static class ContentFragment extends SetupGuidedStepFragment {
        private static final int ACTION_HEADER = 3;
        private static final int ACTION_INPUT_START = 4;
        private static final int PENDING_ACTION_CHANNEL_CHANGED = 2;
        private static final int PENDING_ACTION_INPUT_CHANGED = 1;
        private static final int PENDING_ACTION_NONE = 0;
        private TIFChannelManager mChannelDataManager;
        private final TIFChannelManager.Listener mChannelDataManagerListener = new TIFChannelManager.Listener() {
            public void onLoadFinished() {
                handleChannelChanged();
            }

            public void onChannelListUpdated() {
                handleChannelChanged();
            }

            public void onChannelBrowsableChanged() {
                handleChannelChanged();
            }

            private void handleChannelChanged() {
                if (!ContentFragment.this.mParentFragment.isEnterTransitionRunning()) {
                    ContentFragment.this.updateActions();
                } else if (ContentFragment.this.mPendingAction != 1) {
                    int unused = ContentFragment.this.mPendingAction = 2;
                }
            }
        };
        private int mDoneInputStartIndex;
        private final TvInputManager.TvInputCallback mInputCallback = new TvInputManager.TvInputCallback() {
            public void onInputAdded(String inputId) {
                handleInputChanged();
            }

            public void onInputRemoved(String inputId) {
                handleInputChanged();
            }

            public void onInputUpdated(String inputId) {
                handleInputChanged();
            }

            public void onTvInputInfoUpdated(TvInputInfo inputInfo) {
                handleInputChanged();
            }

            private void handleInputChanged() {
                if (ContentFragment.this.mParentFragment.isEnterTransitionRunning()) {
                    int unused = ContentFragment.this.mPendingAction = 1;
                    return;
                }
                ContentFragment.this.buildInputs();
                ContentFragment.this.updateActions();
            }
        };
        private TvInputManagerHelper mInputManager;
        private List<TvInputInfo> mInputs;
        private int mKnownInputStartIndex;
        private String mNewlyAddedInputId;
        /* access modifiers changed from: private */
        public SetupSourcesFragment mParentFragment;
        /* access modifiers changed from: private */
        public int mPendingAction = 0;
        private SetupUtils mSetupUtils;

        public void onCreate(Bundle savedInstanceState) {
            Context context = getActivity();
            this.mInputManager = ((DestroyApp) context.getApplicationContext()).getTvInputManagerHelper();
            this.mChannelDataManager = TvSingletons.getSingletons().getChannelDataManager();
            this.mSetupUtils = SetupUtils.createForTvSingletons(context);
            buildInputs();
            this.mInputManager.addCallback(this.mInputCallback);
            this.mChannelDataManager.addListener(this.mChannelDataManagerListener);
            super.onCreate(savedInstanceState);
            this.mParentFragment = (SetupSourcesFragment) getParentFragment();
        }

        public void onDestroy() {
            super.onDestroy();
            this.mChannelDataManager.removeListener(this.mChannelDataManagerListener);
            this.mInputManager.removeCallback(this.mInputCallback);
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.setup_sources_text), getString(R.string.setup_sources_description), (String) null, (Drawable) null);
        }

        public GuidedActionsStylist onCreateActionsStylist() {
            return new SetupSourceGuidedActionsStylist();
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            createActionsInternal(actions);
        }

        /* access modifiers changed from: private */
        public void buildInputs() {
            List<TvInputInfo> oldInputs = this.mInputs;
            this.mInputs = this.mInputManager.getTvInputInfos(true, true);
            if (oldInputs != null) {
                List<TvInputInfo> newList = new ArrayList<>(this.mInputs);
                for (TvInputInfo input : oldInputs) {
                    newList.remove(input);
                }
                if (newList.size() <= 0 || !this.mSetupUtils.isNewInput(newList.get(0).getId())) {
                    this.mNewlyAddedInputId = null;
                } else {
                    this.mNewlyAddedInputId = newList.get(0).getId();
                }
            }
            Collections.sort(this.mInputs, new TvInputNewComparator(this.mSetupUtils, this.mInputManager));
            this.mKnownInputStartIndex = 0;
            this.mDoneInputStartIndex = 0;
            for (TvInputInfo input2 : this.mInputs) {
                if (this.mSetupUtils.isNewInput(input2.getId())) {
                    this.mSetupUtils.markAsKnownInput(input2.getId());
                    this.mKnownInputStartIndex++;
                }
                if (!this.mSetupUtils.isSetupDone(input2.getId())) {
                    this.mDoneInputStartIndex++;
                }
            }
        }

        /* access modifiers changed from: private */
        public void updateActions() {
            List<GuidedAction> actions = new ArrayList<>();
            createActionsInternal(actions);
            setActions(actions);
        }

        /* JADX WARNING: type inference failed for: r10v0 */
        /* JADX WARNING: type inference failed for: r11v0 */
        /* JADX WARNING: type inference failed for: r10v1, types: [boolean] */
        /* JADX WARNING: type inference failed for: r10v2 */
        /* JADX WARNING: type inference failed for: r11v2 */
        /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r11v1, types: [int, boolean] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void createActionsInternal(java.util.List<android.support.v17.leanback.widget.GuidedAction> r19) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = -1
                r3 = 0
                r4 = 0
                r5 = 1
                int r6 = r0.mDoneInputStartIndex
                r7 = 0
                r8 = 3
                r10 = 0
                r11 = 1
                if (r6 <= 0) goto L_0x0046
                android.support.v17.leanback.widget.GuidedAction$Builder r6 = new android.support.v17.leanback.widget.GuidedAction$Builder
                android.app.Activity r12 = r18.getActivity()
                r6.<init>(r12)
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r6 = r6.id(r8)
                android.support.v17.leanback.widget.GuidedAction$Builder r6 = (android.support.v17.leanback.widget.GuidedAction.Builder) r6
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r6 = r6.title((java.lang.CharSequence) r7)
                android.support.v17.leanback.widget.GuidedAction$Builder r6 = (android.support.v17.leanback.widget.GuidedAction.Builder) r6
                r12 = 2131692373(0x7f0f0b55, float:1.9013844E38)
                java.lang.String r12 = r0.getString(r12)
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r6 = r6.description((java.lang.CharSequence) r12)
                android.support.v17.leanback.widget.GuidedAction$Builder r6 = (android.support.v17.leanback.widget.GuidedAction.Builder) r6
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r6 = r6.focusable(r10)
                android.support.v17.leanback.widget.GuidedAction$Builder r6 = (android.support.v17.leanback.widget.GuidedAction.Builder) r6
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r6 = r6.infoOnly(r11)
                android.support.v17.leanback.widget.GuidedAction$Builder r6 = (android.support.v17.leanback.widget.GuidedAction.Builder) r6
                android.support.v17.leanback.widget.GuidedAction r6 = r6.build()
                r1.add(r6)
            L_0x0046:
                r6 = r5
                r5 = r4
                r4 = r3
                r3 = r2
                r2 = r10
            L_0x004b:
                java.util.List<android.media.tv.TvInputInfo> r12 = r0.mInputs
                int r12 = r12.size()
                if (r2 >= r12) goto L_0x018f
                int r12 = r0.mDoneInputStartIndex
                if (r2 != r12) goto L_0x008e
                int r4 = r4 + 1
                android.support.v17.leanback.widget.GuidedAction$Builder r12 = new android.support.v17.leanback.widget.GuidedAction$Builder
                android.app.Activity r13 = r18.getActivity()
                r12.<init>(r13)
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r12 = r12.id(r8)
                android.support.v17.leanback.widget.GuidedAction$Builder r12 = (android.support.v17.leanback.widget.GuidedAction.Builder) r12
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r12 = r12.title((java.lang.CharSequence) r7)
                android.support.v17.leanback.widget.GuidedAction$Builder r12 = (android.support.v17.leanback.widget.GuidedAction.Builder) r12
                r13 = 2131692372(0x7f0f0b54, float:1.9013842E38)
                java.lang.String r13 = r0.getString(r13)
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r12 = r12.description((java.lang.CharSequence) r13)
                android.support.v17.leanback.widget.GuidedAction$Builder r12 = (android.support.v17.leanback.widget.GuidedAction.Builder) r12
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r12 = r12.focusable(r10)
                android.support.v17.leanback.widget.GuidedAction$Builder r12 = (android.support.v17.leanback.widget.GuidedAction.Builder) r12
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r12 = r12.infoOnly(r11)
                android.support.v17.leanback.widget.GuidedAction$Builder r12 = (android.support.v17.leanback.widget.GuidedAction.Builder) r12
                android.support.v17.leanback.widget.GuidedAction r12 = r12.build()
                r1.add(r12)
            L_0x008e:
                java.util.List<android.media.tv.TvInputInfo> r12 = r0.mInputs
                java.lang.Object r12 = r12.get(r2)
                android.media.tv.TvInputInfo r12 = (android.media.tv.TvInputInfo) r12
                java.lang.String r13 = r12.getId()
                r14 = 0
                android.app.Activity r15 = r18.getActivity()
                java.lang.CharSequence r15 = r12.loadLabel(r15)
                java.lang.String r15 = r15.toString()
                java.lang.String r7 = "com.mediatek.tvinput/.tuner.TunerInputService/HW0"
                boolean r7 = r13.equals(r7)
                r8 = 2131692552(0x7f0f0c08, float:1.9014207E38)
                if (r7 == 0) goto L_0x00dc
                boolean r7 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()
                if (r7 == 0) goto L_0x00c4
                com.mediatek.wwtv.tvcenter.util.CommonIntegration r7 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
                boolean r7 = r7.isCurrentSourceATV()
                if (r7 == 0) goto L_0x00c4
                goto L_0x0186
            L_0x00c4:
                com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r7 = r0.mChannelDataManager
                int r7 = r7.getDTVTIFChannelsForSourceSetup()
                boolean r9 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isUSRegion()
                if (r9 == 0) goto L_0x0114
                int r5 = r5 + r7
                r7 = r5
                if (r6 == 0) goto L_0x00d7
                r6 = 0
                goto L_0x0186
            L_0x00d7:
                java.lang.String r15 = r0.getString(r8)
                goto L_0x0114
            L_0x00dc:
                java.lang.String r7 = "com.mediatek.tvinput/.tuner.TunerInputService"
                boolean r7 = r13.startsWith(r7)
                if (r7 == 0) goto L_0x010e
                boolean r7 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()
                if (r7 == 0) goto L_0x00f6
                com.mediatek.wwtv.tvcenter.util.CommonIntegration r7 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
                boolean r7 = r7.isCurrentSourceDTV()
                if (r7 == 0) goto L_0x00f6
                goto L_0x0186
            L_0x00f6:
                com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r7 = r0.mChannelDataManager
                int r7 = r7.getATVTIFChannelsForSourceSetup()
                boolean r9 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isUSRegion()
                if (r9 == 0) goto L_0x0114
                int r5 = r5 + r7
                r7 = r5
                if (r6 == 0) goto L_0x0109
                r6 = 0
                goto L_0x0186
            L_0x0109:
                java.lang.String r15 = r0.getString(r8)
                goto L_0x0114
            L_0x010e:
                com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r7 = r0.mChannelDataManager
                int r7 = r7.getChannelCountForInput(r13)
            L_0x0114:
                com.android.tv.util.SetupUtils r8 = r0.mSetupUtils
                boolean r8 = r8.isSetupDone(r13)
                if (r8 != 0) goto L_0x0133
                if (r7 <= 0) goto L_0x011f
                goto L_0x0133
            L_0x011f:
                int r8 = r0.mKnownInputStartIndex
                if (r2 < r8) goto L_0x012b
                r8 = 2131692431(0x7f0f0b8f, float:1.9013962E38)
                java.lang.String r8 = r0.getString(r8)
                goto L_0x013c
            L_0x012b:
                r8 = 2131692429(0x7f0f0b8d, float:1.9013958E38)
                java.lang.String r8 = r0.getString(r8)
                goto L_0x0151
            L_0x0133:
                if (r7 != 0) goto L_0x013d
                r8 = 2131692430(0x7f0f0b8e, float:1.901396E38)
                java.lang.String r8 = r0.getString(r8)
            L_0x013c:
                goto L_0x0151
            L_0x013d:
                android.content.res.Resources r8 = r18.getResources()
                r9 = 2131558401(0x7f0d0001, float:1.8742117E38)
                java.lang.Object[] r14 = new java.lang.Object[r11]
                java.lang.Integer r16 = java.lang.Integer.valueOf(r7)
                r14[r10] = r16
                java.lang.String r8 = r8.getQuantityString(r9, r7, r14)
                goto L_0x013c
            L_0x0151:
                int r4 = r4 + 1
                java.lang.String r9 = r12.getId()
                java.lang.String r14 = r0.mNewlyAddedInputId
                boolean r9 = r9.equals(r14)
                if (r9 == 0) goto L_0x0161
                r3 = r4
            L_0x0161:
                android.support.v17.leanback.widget.GuidedAction$Builder r9 = new android.support.v17.leanback.widget.GuidedAction$Builder
                android.app.Activity r14 = r18.getActivity()
                r9.<init>(r14)
                r14 = 4
                int r14 = r14 + r2
                long r10 = (long) r14
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r9 = r9.id(r10)
                android.support.v17.leanback.widget.GuidedAction$Builder r9 = (android.support.v17.leanback.widget.GuidedAction.Builder) r9
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r9 = r9.title((java.lang.CharSequence) r15)
                android.support.v17.leanback.widget.GuidedAction$Builder r9 = (android.support.v17.leanback.widget.GuidedAction.Builder) r9
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r9 = r9.description((java.lang.CharSequence) r8)
                android.support.v17.leanback.widget.GuidedAction$Builder r9 = (android.support.v17.leanback.widget.GuidedAction.Builder) r9
                android.support.v17.leanback.widget.GuidedAction r9 = r9.build()
                r1.add(r9)
            L_0x0186:
                int r2 = r2 + 1
                r7 = 0
                r8 = 3
                r10 = 0
                r11 = 1
                goto L_0x004b
            L_0x018f:
                r2 = -1
                if (r3 == r2) goto L_0x019d
                android.support.v17.leanback.widget.GuidedActionsStylist r2 = r18.getGuidedActionsStylist()
                android.support.v17.leanback.widget.VerticalGridView r2 = r2.getActionsGridView()
                r2.setSelectedPosition(r3)
            L_0x019d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.tv.onboarding.SetupSourcesFragment.ContentFragment.createActionsInternal(java.util.List):void");
        }

        /* access modifiers changed from: protected */
        public String getActionCategory() {
            return SetupSourcesFragment.ACTION_CATEGORY;
        }

        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == 1) {
                boolean unused = this.mParentFragment.onActionClick(SetupSourcesFragment.ACTION_CATEGORY, (int) action.getId());
                return;
            }
            int index = ((int) action.getId()) - 4;
            if (index >= 0) {
                Bundle params = new Bundle();
                params.putString("input_id", this.mInputs.get(index).getId());
                boolean unused2 = this.mParentFragment.onActionClick(SetupSourcesFragment.ACTION_CATEGORY, 2, params);
            }
        }

        /* access modifiers changed from: package-private */
        public void executePendingAction() {
            switch (this.mPendingAction) {
                case 1:
                    buildInputs();
                    break;
                case 2:
                    break;
            }
            updateActions();
            this.mPendingAction = 0;
        }

        private class SetupSourceGuidedActionsStylist extends GuidedActionsStylistWithDivider {
            private static final float ALPHA_CATEGORY = 1.0f;
            private static final float ALPHA_INPUT_DESCRIPTION = 0.5f;

            private SetupSourceGuidedActionsStylist() {
            }

            public void onBindViewHolder(GuidedActionsStylist.ViewHolder vh, GuidedAction action) {
                super.onBindViewHolder(vh, action);
                TextView descriptionView = vh.getDescriptionView();
                if (descriptionView == null) {
                    return;
                }
                if (action.getId() == 3) {
                    descriptionView.setAlpha(1.0f);
                    descriptionView.setTextColor(ContentFragment.this.getResources().getColor(R.color.setup_category, (Resources.Theme) null));
                    descriptionView.setTypeface(Typeface.create(ContentFragment.this.getString(R.string.condensed_font), 0));
                    return;
                }
                descriptionView.setAlpha(ALPHA_INPUT_DESCRIPTION);
                descriptionView.setTextColor(ContentFragment.this.getResources().getColor(R.color.common_setup_input_description, (Resources.Theme) null));
                descriptionView.setTypeface(Typeface.create(ContentFragment.this.getString(R.string.font), 0));
            }
        }
    }
}
