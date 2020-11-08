package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedActionsStylist;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.subtitle.Cea708CCParser;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.util.GuidedActionsAlignUtil;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import java.util.List;

public class EnterPasswordState implements State {
    private static final int ACTION_ID_CHECKBOX = 999;
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public EnterPasswordState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = new EnterPasswordFragment();
        ((State.FragmentChangeListener) this.mActivity).onFragmentChange(this.mFragment, true);
    }

    public void processBackward() {
        this.mFragment = new EnterPasswordFragment();
        ((State.FragmentChangeListener) this.mActivity).onFragmentChange(this.mFragment, false);
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class EnterPasswordFragment extends WifiConnectivityGuidedStepFragment {
        private static final int PSK_MIN_LENGTH = 8;
        private static final int WEP_MIN_LENGTH = 5;
        /* access modifiers changed from: private */
        public CheckBox mCheckBox;
        private boolean mEditFocused = false;
        /* access modifiers changed from: private */
        public GuidedAction mPasswordAction;
        private StateMachine mStateMachine;
        /* access modifiers changed from: private */
        public EditText mTextInput;
        /* access modifiers changed from: private */
        public UserChoiceInfo mUserChoiceInfo;

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.wifi_setup_input_password, this.mUserChoiceInfo.getWifiConfiguration().getPrintableSsid()), (String) null, (String) null, (Drawable) null);
        }

        public GuidedActionsStylist onCreateActionsStylist() {
            return new GuidedActionsStylist() {
                public GuidedActionsStylist.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(onProvideItemLayoutId(viewType), parent, false);
                    if (viewType == EnterPasswordState.ACTION_ID_CHECKBOX) {
                        return new CheckBoxViewHolder(v);
                    }
                    return new GuidedActionsAlignUtil.SetupViewHolder(v);
                }

                public int getItemViewType(GuidedAction action) {
                    return (int) action.getId();
                }

                public void onBindViewHolder(GuidedActionsStylist.ViewHolder vh, GuidedAction action) {
                    super.onBindViewHolder(vh, action);
                    if (action.getId() == 999) {
                        CheckBoxViewHolder checkBoxVH = (CheckBoxViewHolder) vh;
                        CheckBox unused = EnterPasswordFragment.this.mCheckBox = checkBoxVH.mCheckbox;
                        checkBoxVH.itemView.setOnClickListener(
                        /*  JADX ERROR: Method code generation error
                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x001e: INVOKE  
                              (wrap: android.view.View : 0x0017: IGET  (r1v4 android.view.View) = 
                              (r0v6 'checkBoxVH' com.android.tv.settings.connectivity.setup.EnterPasswordState$EnterPasswordFragment$CheckBoxViewHolder)
                             com.android.tv.settings.connectivity.setup.EnterPasswordState.EnterPasswordFragment.CheckBoxViewHolder.itemView android.view.View)
                              (wrap: com.android.tv.settings.connectivity.setup.-$$Lambda$EnterPasswordState$EnterPasswordFragment$1$talHM-HMVGVst-Z1EpGLSi-s3Hc : 0x001b: CONSTRUCTOR  (r2v4 com.android.tv.settings.connectivity.setup.-$$Lambda$EnterPasswordState$EnterPasswordFragment$1$talHM-HMVGVst-Z1EpGLSi-s3Hc) = 
                              (r4v0 'this' com.android.tv.settings.connectivity.setup.EnterPasswordState$EnterPasswordFragment$1 A[THIS])
                             call: com.android.tv.settings.connectivity.setup.-$$Lambda$EnterPasswordState$EnterPasswordFragment$1$talHM-HMVGVst-Z1EpGLSi-s3Hc.<init>(com.android.tv.settings.connectivity.setup.EnterPasswordState$EnterPasswordFragment$1):void type: CONSTRUCTOR)
                             android.view.View.setOnClickListener(android.view.View$OnClickListener):void type: VIRTUAL in method: com.android.tv.settings.connectivity.setup.EnterPasswordState.EnterPasswordFragment.1.onBindViewHolder(android.support.v17.leanback.widget.GuidedActionsStylist$ViewHolder, android.support.v17.leanback.widget.GuidedAction):void, dex: classes4.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(Unknown Source)
                            	at java.base/java.util.ArrayList.forEach(Unknown Source)
                            	at java.base/java.util.stream.SortedOps$RefSortingSink.end(Unknown Source)
                            	at java.base/java.util.stream.Sink$ChainedReference.end(Unknown Source)
                            	at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
                            	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
                            	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(Unknown Source)
                            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(Unknown Source)
                            	at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
                            	at java.base/java.util.stream.ReferencePipeline.forEach(Unknown Source)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:314)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(Unknown Source)
                            	at java.base/java.util.ArrayList.forEach(Unknown Source)
                            	at java.base/java.util.stream.SortedOps$RefSortingSink.end(Unknown Source)
                            	at java.base/java.util.stream.Sink$ChainedReference.end(Unknown Source)
                            	at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
                            	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
                            	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(Unknown Source)
                            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(Unknown Source)
                            	at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
                            	at java.base/java.util.stream.ReferencePipeline.forEach(Unknown Source)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(Unknown Source)
                            	at java.base/java.util.ArrayList.forEach(Unknown Source)
                            	at java.base/java.util.stream.SortedOps$RefSortingSink.end(Unknown Source)
                            	at java.base/java.util.stream.Sink$ChainedReference.end(Unknown Source)
                            	at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
                            	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
                            	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(Unknown Source)
                            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(Unknown Source)
                            	at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
                            	at java.base/java.util.stream.ReferencePipeline.forEach(Unknown Source)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x001b: CONSTRUCTOR  (r2v4 com.android.tv.settings.connectivity.setup.-$$Lambda$EnterPasswordState$EnterPasswordFragment$1$talHM-HMVGVst-Z1EpGLSi-s3Hc) = 
                              (r4v0 'this' com.android.tv.settings.connectivity.setup.EnterPasswordState$EnterPasswordFragment$1 A[THIS])
                             call: com.android.tv.settings.connectivity.setup.-$$Lambda$EnterPasswordState$EnterPasswordFragment$1$talHM-HMVGVst-Z1EpGLSi-s3Hc.<init>(com.android.tv.settings.connectivity.setup.EnterPasswordState$EnterPasswordFragment$1):void type: CONSTRUCTOR in method: com.android.tv.settings.connectivity.setup.EnterPasswordState.EnterPasswordFragment.1.onBindViewHolder(android.support.v17.leanback.widget.GuidedActionsStylist$ViewHolder, android.support.v17.leanback.widget.GuidedAction):void, dex: classes4.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 79 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: com.android.tv.settings.connectivity.setup.-$$Lambda$EnterPasswordState$EnterPasswordFragment$1$talHM-HMVGVst-Z1EpGLSi-s3Hc, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 85 more
                            */
                        /*
                            this = this;
                            super.onBindViewHolder(r5, r6)
                            long r0 = r6.getId()
                            r2 = 999(0x3e7, double:4.936E-321)
                            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                            if (r0 != 0) goto L_0x0035
                            r0 = r5
                            com.android.tv.settings.connectivity.setup.EnterPasswordState$EnterPasswordFragment$CheckBoxViewHolder r0 = (com.android.tv.settings.connectivity.setup.EnterPasswordState.EnterPasswordFragment.CheckBoxViewHolder) r0
                            com.android.tv.settings.connectivity.setup.EnterPasswordState$EnterPasswordFragment r1 = com.android.tv.settings.connectivity.setup.EnterPasswordState.EnterPasswordFragment.this
                            android.widget.CheckBox r2 = r0.mCheckbox
                            android.widget.CheckBox unused = r1.mCheckBox = r2
                            android.view.View r1 = r0.itemView
                            com.android.tv.settings.connectivity.setup.-$$Lambda$EnterPasswordState$EnterPasswordFragment$1$talHM-HMVGVst-Z1EpGLSi-s3Hc r2 = new com.android.tv.settings.connectivity.setup.-$$Lambda$EnterPasswordState$EnterPasswordFragment$1$talHM-HMVGVst-Z1EpGLSi-s3Hc
                            r2.<init>(r4)
                            r1.setOnClickListener(r2)
                            com.android.tv.settings.connectivity.setup.EnterPasswordState$EnterPasswordFragment r1 = com.android.tv.settings.connectivity.setup.EnterPasswordState.EnterPasswordFragment.this
                            android.widget.CheckBox r1 = r1.mCheckBox
                            com.android.tv.settings.connectivity.setup.EnterPasswordState$EnterPasswordFragment r2 = com.android.tv.settings.connectivity.setup.EnterPasswordState.EnterPasswordFragment.this
                            com.android.tv.settings.connectivity.setup.UserChoiceInfo r2 = r2.mUserChoiceInfo
                            boolean r2 = r2.isPasswordHidden()
                            r1.setChecked(r2)
                            goto L_0x0052
                        L_0x0035:
                            long r0 = r6.getId()
                            r2 = -7
                            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                            if (r0 != 0) goto L_0x0052
                            com.android.tv.settings.connectivity.setup.EnterPasswordState$EnterPasswordFragment r0 = com.android.tv.settings.connectivity.setup.EnterPasswordState.EnterPasswordFragment.this
                            android.view.View r1 = r5.itemView
                            r2 = 2131361997(0x7f0a00cd, float:1.8343762E38)
                            android.view.View r1 = r1.findViewById(r2)
                            android.widget.EditText r1 = (android.widget.EditText) r1
                            android.widget.EditText unused = r0.mTextInput = r1
                            r4.openInEditMode(r6)
                        L_0x0052:
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.connectivity.setup.EnterPasswordState.EnterPasswordFragment.AnonymousClass1.onBindViewHolder(android.support.v17.leanback.widget.GuidedActionsStylist$ViewHolder, android.support.v17.leanback.widget.GuidedAction):void");
                    }

                    public static /* synthetic */ void lambda$onBindViewHolder$0(AnonymousClass1 r2, View view) {
                        EnterPasswordFragment.this.mCheckBox.setChecked(!EnterPasswordFragment.this.mCheckBox.isChecked());
                        if (EnterPasswordFragment.this.mPasswordAction != null) {
                            EnterPasswordFragment.this.setSelectedActionPosition(0);
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void onEditingModeChange(GuidedActionsStylist.ViewHolder vh, boolean editing, boolean withTransition) {
                        super.onEditingModeChange(vh, editing, withTransition);
                        EnterPasswordFragment.this.updatePasswordInputObfuscation();
                    }

                    public int onProvideItemLayoutId(int viewType) {
                        if (viewType == EnterPasswordState.ACTION_ID_CHECKBOX) {
                            return R.layout.password_checkbox;
                        }
                        return R.layout.setup_password_item;
                    }
                };
            }

            public void onCreate(Bundle savedInstanceState) {
                this.mUserChoiceInfo = (UserChoiceInfo) ViewModelProviders.of(getActivity()).get(UserChoiceInfo.class);
                this.mStateMachine = (StateMachine) ViewModelProviders.of(getActivity()).get(StateMachine.class);
                super.onCreate(savedInstanceState);
            }

            public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
                int i;
                Context context = getActivity();
                CharSequence prevPassword = this.mUserChoiceInfo.getPageSummary(2);
                boolean isPasswordHidden = this.mUserChoiceInfo.isPasswordHidden();
                GuidedAction.Builder builder = (GuidedAction.Builder) new GuidedAction.Builder(context).title(prevPassword == null ? "" : prevPassword);
                if (isPasswordHidden) {
                    i = 128;
                } else {
                    i = Cea708CCParser.Const.CODE_C1_SPA;
                }
                this.mPasswordAction = ((GuidedAction.Builder) ((GuidedAction.Builder) ((GuidedAction.Builder) builder.editInputType(i | 1)).id(-7)).editable(true)).build();
                actions.add(this.mPasswordAction);
                actions.add(((GuidedAction.Builder) new GuidedAction.Builder(context).id(999)).build());
            }

            public long onGuidedActionEditedAndProceed(GuidedAction action) {
                if (action.getId() == -7) {
                    String password = action.getTitle().toString();
                    if (password.length() >= 5) {
                        this.mUserChoiceInfo.put(2, action.getTitle().toString());
                        this.mUserChoiceInfo.setPasswordHidden(this.mCheckBox.isChecked());
                        setWifiConfigurationPassword(password);
                        this.mStateMachine.getListener().onComplete(18);
                    }
                }
                return action.getId();
            }

            public void onGuidedActionFocused(GuidedAction action) {
                boolean newEditFocused = action == this.mPasswordAction;
                if (!this.mEditFocused && newEditFocused) {
                    openInEditMode(action);
                }
                this.mEditFocused = newEditFocused;
            }

            /* access modifiers changed from: private */
            public void updatePasswordInputObfuscation() {
                int i;
                EditText editText = this.mTextInput;
                if (this.mCheckBox.isChecked()) {
                    i = 128;
                } else {
                    i = Cea708CCParser.Const.CODE_C1_SPA;
                }
                editText.setInputType(i | 1);
            }

            private void setWifiConfigurationPassword(String password) {
                int wifiSecurity = this.mUserChoiceInfo.getWifiSecurity();
                WifiConfiguration wifiConfiguration = this.mUserChoiceInfo.getWifiConfiguration();
                if (wifiSecurity == 1) {
                    int length = password.length();
                    if ((length == 10 || length == 26 || length == 32 || length == 58) && password.matches("[0-9A-Fa-f]*")) {
                        wifiConfiguration.wepKeys[0] = password;
                    } else if (length == 5 || length == 13 || length == 16 || length == 29) {
                        String[] strArr = wifiConfiguration.wepKeys;
                        strArr[0] = '\"' + password + '\"';
                    }
                } else if (wifiSecurity == 2 && password.length() < 8) {
                } else {
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        wifiConfiguration.preSharedKey = password;
                        return;
                    }
                    wifiConfiguration.preSharedKey = '\"' + password + '\"';
                }
            }

            private static class CheckBoxViewHolder extends GuidedActionsAlignUtil.SetupViewHolder {
                CheckBox mCheckbox;

                CheckBoxViewHolder(View v) {
                    super(v);
                    this.mCheckbox = (CheckBox) v.findViewById(R.id.password_checkbox);
                }
            }
        }
    }
