package com.mediatek.wwtv.setting.fragments;

import android.app.Fragment;
import android.content.Context;
import android.media.tv.TvContentRating;
import android.media.tv.TvInputManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import com.mediatek.twoworlds.tv.model.MtkTvUSTvRatingSettingInfoBase;
import com.mediatek.wwtv.setting.util.RatingConst;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingFragment extends Fragment {
    private Context context;
    private boolean isPositionView = false;
    private Action mAction;
    private List<RadioButton> mGroup = new ArrayList();
    private LayoutInflater mInflater;
    private ViewGroup mRootView;
    TVContent mTV;
    TvInputManager mTvInputManager;
    List<TvContentRating> mWholeRatingList = new ArrayList();
    List<String> subRatingsList = new ArrayList();
    private RadioButton vRB_14_A;
    private boolean vRB_14_A_Check;
    private RadioButton vRB_14_D;
    private boolean vRB_14_D_Check;
    private RadioButton vRB_14_L;
    private boolean vRB_14_L_Check;
    private RadioButton vRB_14_S;
    private boolean vRB_14_S_Check;
    private RadioButton vRB_14_V;
    private boolean vRB_14_V_Check;
    private RadioButton vRB_G_A;
    private boolean vRB_G_A_Check;
    private RadioButton vRB_MA_A;
    private boolean vRB_MA_A_Check;
    private RadioButton vRB_MA_L;
    private boolean vRB_MA_L_Check;
    private RadioButton vRB_MA_S;
    private boolean vRB_MA_S_Check;
    private RadioButton vRB_MA_V;
    private boolean vRB_MA_V_Check;
    private RadioButton vRB_PG_A;
    private boolean vRB_PG_A_Check;
    private RadioButton vRB_PG_D;
    private boolean vRB_PG_D_Check;
    private RadioButton vRB_PG_L;
    private boolean vRB_PG_L_Check;
    private RadioButton vRB_PG_S;
    private boolean vRB_PG_S_Check;
    private RadioButton vRB_PG_V;
    private boolean vRB_PG_V_Check;
    private RadioButton vRB_Y7_A;
    private boolean vRB_Y7_A_Check;
    private RadioButton vRB_Y7_FV;
    private boolean vRB_Y7_FV_Check;
    private RadioButton vRB_Y_A;
    private boolean vRB_Y_A_Check;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        this.mTvInputManager = (TvInputManager) this.context.getSystemService("tv_input");
    }

    public void setAction(Action action) {
        this.mAction = action;
        if (action.mDataType == Action.DataType.POSITIONVIEW) {
            this.isPositionView = true;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mRootView = (ViewGroup) inflater.inflate(R.layout.menu_rating_view, (ViewGroup) null);
        init();
        setListener();
        if (MarketRegionInfo.isFunctionSupport(21)) {
            initRatingFromTIF();
        } else {
            initRatingSetting();
        }
        return this.mRootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!this.vRB_Y_A.hasFocus()) {
            this.vRB_Y_A.requestFocus();
        }
    }

    private void setListener() {
        for (int i = 0; i < this.mGroup.size(); i++) {
            this.mGroup.get(i).setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() != 0) {
                        return true;
                    }
                    if (keyCode == 23 || keyCode == 66) {
                        if (((RadioButton) v).isChecked()) {
                            MtkLog.d("Rating", "setChecked(false)");
                            ((RadioButton) v).setChecked(false);
                            RatingFragment.this.setRatingInfo(v, false);
                            if (MarketRegionInfo.isFunctionSupport(21)) {
                                RatingFragment.this.initRatingSettingForTIF();
                                RatingFragment.this.generateContentRatingPlus();
                            } else {
                                RatingFragment.this.initRatingSetting();
                            }
                        } else {
                            MtkLog.d("Rating", "setChecked(true)");
                            ((RadioButton) v).setChecked(true);
                            RatingFragment.this.setRatingInfo(v, true);
                            if (MarketRegionInfo.isFunctionSupport(21)) {
                                RatingFragment.this.initRatingSettingForTIF();
                                RatingFragment.this.generateContentRatingPlus();
                            } else {
                                RatingFragment.this.initRatingSetting();
                            }
                        }
                    }
                    return false;
                }
            });
        }
    }

    public void init() {
        this.mTV = TVContent.getInstance(this.context);
        this.vRB_Y_A = (RadioButton) this.mRootView.findViewById(R.id.radioButton1);
        this.vRB_Y7_A = (RadioButton) this.mRootView.findViewById(R.id.radioButton7);
        this.vRB_Y7_FV = (RadioButton) this.mRootView.findViewById(R.id.radioButton12);
        this.vRB_G_A = (RadioButton) this.mRootView.findViewById(R.id.radioButton13);
        this.vRB_PG_A = (RadioButton) this.mRootView.findViewById(R.id.radioButton19);
        this.vRB_PG_D = (RadioButton) this.mRootView.findViewById(R.id.radioButton20);
        this.vRB_PG_L = (RadioButton) this.mRootView.findViewById(R.id.radioButton21);
        this.vRB_PG_S = (RadioButton) this.mRootView.findViewById(R.id.radioButton22);
        this.vRB_PG_V = (RadioButton) this.mRootView.findViewById(R.id.radioButton23);
        this.vRB_14_A = (RadioButton) this.mRootView.findViewById(R.id.radioButton25);
        this.vRB_14_D = (RadioButton) this.mRootView.findViewById(R.id.radioButton26);
        this.vRB_14_L = (RadioButton) this.mRootView.findViewById(R.id.radioButton27);
        this.vRB_14_S = (RadioButton) this.mRootView.findViewById(R.id.radioButton28);
        this.vRB_14_V = (RadioButton) this.mRootView.findViewById(R.id.radioButton29);
        this.vRB_MA_A = (RadioButton) this.mRootView.findViewById(R.id.radioButton31);
        this.vRB_MA_L = (RadioButton) this.mRootView.findViewById(R.id.radioButton33);
        this.vRB_MA_S = (RadioButton) this.mRootView.findViewById(R.id.radioButton34);
        this.vRB_MA_V = (RadioButton) this.mRootView.findViewById(R.id.radioButton35);
        this.mGroup.clear();
        this.mGroup.add(this.vRB_Y_A);
        this.mGroup.add(this.vRB_Y7_A);
        this.mGroup.add(this.vRB_Y7_FV);
        this.mGroup.add(this.vRB_G_A);
        this.mGroup.add(this.vRB_PG_A);
        this.mGroup.add(this.vRB_PG_D);
        this.mGroup.add(this.vRB_PG_L);
        this.mGroup.add(this.vRB_PG_S);
        this.mGroup.add(this.vRB_PG_V);
        this.mGroup.add(this.vRB_14_A);
        this.mGroup.add(this.vRB_14_D);
        this.mGroup.add(this.vRB_14_L);
        this.mGroup.add(this.vRB_14_S);
        this.mGroup.add(this.vRB_14_V);
        this.mGroup.add(this.vRB_MA_A);
        this.mGroup.add(this.vRB_MA_L);
        this.mGroup.add(this.vRB_MA_S);
        this.mGroup.add(this.vRB_MA_V);
        this.vRB_14_A.setNextFocusLeftId(R.id.radioButton29);
        this.vRB_14_D.setNextFocusDownId(R.id.radioButton20);
        this.vRB_MA_A.setNextFocusDownId(R.id.radioButton1);
        this.vRB_MA_A.setNextFocusLeftId(R.id.radioButton35);
        this.vRB_MA_A.setNextFocusRightId(R.id.radioButton33);
        this.vRB_MA_L.setNextFocusLeftId(R.id.radioButton31);
        this.vRB_14_V.setNextFocusRightId(R.id.radioButton25);
        this.vRB_MA_L.setNextFocusDownId(R.id.radioButton21);
        this.vRB_MA_S.setNextFocusDownId(R.id.radioButton22);
        this.vRB_MA_V.setNextFocusDownId(R.id.radioButton23);
        this.vRB_MA_V.setNextFocusRightId(R.id.radioButton31);
        this.vRB_Y_A.setNextFocusDownId(R.id.radioButton7);
        this.vRB_Y_A.setNextFocusUpId(R.id.radioButton31);
        this.vRB_Y_A.setNextFocusLeftId(R.id.radioButton1);
        this.vRB_Y_A.setNextFocusRightId(R.id.radioButton1);
        this.vRB_Y7_A.setNextFocusDownId(R.id.radioButton13);
        this.vRB_Y7_A.setNextFocusUpId(R.id.radioButton1);
        this.vRB_Y7_A.setNextFocusLeftId(R.id.radioButton12);
        this.vRB_Y7_A.setNextFocusRightId(R.id.radioButton12);
        this.vRB_Y7_FV.setNextFocusDownId(R.id.radioButton12);
        this.vRB_Y7_FV.setNextFocusUpId(R.id.radioButton12);
        this.vRB_Y7_FV.setNextFocusLeftId(R.id.radioButton7);
        this.vRB_Y7_FV.setNextFocusRightId(R.id.radioButton7);
        this.vRB_G_A.setNextFocusDownId(R.id.radioButton19);
        this.vRB_G_A.setNextFocusUpId(R.id.radioButton7);
        this.vRB_G_A.setNextFocusLeftId(R.id.radioButton13);
        this.vRB_G_A.setNextFocusRightId(R.id.radioButton13);
        this.vRB_PG_A.setNextFocusDownId(R.id.radioButton25);
        this.vRB_PG_A.setNextFocusUpId(R.id.radioButton13);
        this.vRB_PG_A.setNextFocusLeftId(R.id.radioButton23);
        this.vRB_PG_A.setNextFocusRightId(R.id.radioButton20);
        this.vRB_PG_D.setNextFocusDownId(R.id.radioButton26);
        this.vRB_PG_D.setNextFocusUpId(R.id.radioButton26);
        this.vRB_PG_D.setNextFocusLeftId(R.id.radioButton19);
        this.vRB_PG_D.setNextFocusRightId(R.id.radioButton21);
        this.vRB_PG_L.setNextFocusDownId(R.id.radioButton27);
        this.vRB_PG_L.setNextFocusUpId(R.id.radioButton33);
        this.vRB_PG_L.setNextFocusLeftId(R.id.radioButton20);
        this.vRB_PG_L.setNextFocusRightId(R.id.radioButton22);
        this.vRB_PG_S.setNextFocusDownId(R.id.radioButton28);
        this.vRB_PG_S.setNextFocusUpId(R.id.radioButton34);
        this.vRB_PG_S.setNextFocusLeftId(R.id.radioButton21);
        this.vRB_PG_S.setNextFocusRightId(R.id.radioButton23);
        this.vRB_PG_V.setNextFocusDownId(R.id.radioButton29);
        this.vRB_PG_V.setNextFocusUpId(R.id.radioButton35);
        this.vRB_PG_V.setNextFocusLeftId(R.id.radioButton22);
        this.vRB_PG_V.setNextFocusRightId(R.id.radioButton19);
    }

    public void initRatingFromTIF() {
        List<String> subRatings;
        for (TvContentRating rate : this.mTvInputManager.getBlockedRatings()) {
            if (rate.getMainRating().equals("US_TV_Y")) {
                List<String> subRatings2 = rate.getSubRatings();
                if (subRatings2 != null && subRatings2.size() == 1 && subRatings2.get(0).equals("US_TV_A")) {
                    this.vRB_Y_A_Check = true;
                }
            } else if (rate.getMainRating().equals("US_TV_Y7")) {
                List<String> subRatings3 = rate.getSubRatings();
                if (subRatings3 != null && subRatings3.size() > 0) {
                    if (subRatings3.contains("US_TV_A")) {
                        this.vRB_Y7_A_Check = true;
                    }
                    if (subRatings3.contains("US_TV_FV")) {
                        this.vRB_Y7_FV_Check = true;
                    }
                }
            } else if (rate.getMainRating().equals("US_TV_G")) {
                List<String> subRatings4 = rate.getSubRatings();
                if (subRatings4 != null && subRatings4.size() > 0 && subRatings4.contains("US_TV_A")) {
                    this.vRB_G_A_Check = true;
                }
            } else if (rate.getMainRating().equals("US_TV_PG")) {
                List<String> subRatings5 = rate.getSubRatings();
                if (subRatings5 != null && subRatings5.size() > 0) {
                    if (subRatings5.contains("US_TV_A")) {
                        this.vRB_PG_A_Check = true;
                    }
                    if (subRatings5.contains("US_TV_D")) {
                        this.vRB_PG_D_Check = true;
                    }
                    if (subRatings5.contains("US_TV_L")) {
                        this.vRB_PG_L_Check = true;
                    }
                    if (subRatings5.contains("US_TV_S")) {
                        this.vRB_PG_S_Check = true;
                    }
                    if (subRatings5.contains("US_TV_V")) {
                        this.vRB_PG_V_Check = true;
                    }
                }
            } else if (rate.getMainRating().equals("US_TV_14")) {
                List<String> subRatings6 = rate.getSubRatings();
                if (subRatings6 != null && subRatings6.size() > 0) {
                    if (subRatings6.contains("US_TV_A")) {
                        this.vRB_14_A_Check = true;
                    }
                    if (subRatings6.contains("US_TV_D")) {
                        this.vRB_14_D_Check = true;
                    }
                    if (subRatings6.contains("US_TV_L")) {
                        this.vRB_14_L_Check = true;
                    }
                    if (subRatings6.contains("US_TV_S")) {
                        this.vRB_14_S_Check = true;
                    }
                    if (subRatings6.contains("US_TV_V")) {
                        this.vRB_14_V_Check = true;
                    }
                }
            } else if (rate.getMainRating().equals("US_TV_MA") && (subRatings = rate.getSubRatings()) != null && subRatings.size() > 0) {
                if (subRatings.contains("US_TV_A")) {
                    this.vRB_MA_A_Check = true;
                }
                if (subRatings.contains("US_TV_L")) {
                    this.vRB_MA_L_Check = true;
                }
                if (subRatings.contains("US_TV_S")) {
                    this.vRB_MA_S_Check = true;
                }
                if (subRatings.contains("US_TV_V")) {
                    this.vRB_MA_V_Check = true;
                }
            }
        }
        initRatingSettingForTIF();
    }

    public void initRatingFromTIFPlus() {
        List<String> subRatings;
        for (TvContentRating rate : this.mTvInputManager.getBlockedRatings()) {
            if (rate.getMainRating().equals("US_TV_Y")) {
                List<String> subRatings2 = rate.getSubRatings();
                if (subRatings2 != null && subRatings2.size() == 1 && subRatings2.get(0).equals("US_TV_A")) {
                    this.vRB_Y_A_Check = true;
                }
            } else if (rate.getMainRating().equals("US_TV_Y7")) {
                List<String> subRatings3 = rate.getSubRatings();
                if (subRatings3 != null && subRatings3.size() > 0) {
                    if (subRatings3.contains("US_TV_A")) {
                        this.vRB_Y7_A_Check = true;
                    }
                    if (subRatings3.contains("US_TV_FV")) {
                        this.vRB_Y7_FV_Check = true;
                    }
                }
            } else if (rate.getMainRating().equals("US_TV_G")) {
                List<String> subRatings4 = rate.getSubRatings();
                if (subRatings4 != null && subRatings4.size() > 0 && subRatings4.contains("US_TV_A")) {
                    this.vRB_G_A_Check = true;
                }
            } else if (rate.getMainRating().equals("US_TV_PG")) {
                List<String> subRatings5 = rate.getSubRatings();
                if (subRatings5 != null && subRatings5.size() > 0) {
                    if (subRatings5.contains("US_TV_A")) {
                        this.vRB_PG_A_Check = true;
                    }
                    if (subRatings5.contains("US_TV_D")) {
                        this.vRB_PG_D_Check = true;
                    }
                    if (subRatings5.contains("US_TV_L")) {
                        this.vRB_PG_L_Check = true;
                    }
                    if (subRatings5.contains("US_TV_S")) {
                        this.vRB_PG_S_Check = true;
                    }
                    if (subRatings5.contains("US_TV_V")) {
                        this.vRB_PG_V_Check = true;
                    }
                }
            } else if (rate.getMainRating().equals("US_TV_14")) {
                List<String> subRatings6 = rate.getSubRatings();
                if (subRatings6 != null && subRatings6.size() > 0) {
                    if (subRatings6.contains("US_TV_A")) {
                        this.vRB_14_A_Check = true;
                    }
                    if (subRatings6.contains("US_TV_D")) {
                        this.vRB_14_D_Check = true;
                    }
                    if (subRatings6.contains("US_TV_L")) {
                        this.vRB_14_L_Check = true;
                    }
                    if (subRatings6.contains("US_TV_S")) {
                        this.vRB_14_S_Check = true;
                    }
                    if (subRatings6.contains("US_TV_V")) {
                        this.vRB_14_V_Check = true;
                    }
                }
            } else if (rate.getMainRating().equals("US_TV_MA") && (subRatings = rate.getSubRatings()) != null && subRatings.size() > 0) {
                if (subRatings.contains("US_TV_A")) {
                    this.vRB_MA_A_Check = true;
                }
                if (subRatings.contains("US_TV_L")) {
                    this.vRB_MA_L_Check = true;
                }
                if (subRatings.contains("US_TV_S")) {
                    this.vRB_MA_S_Check = true;
                }
                if (subRatings.contains("US_TV_V")) {
                    this.vRB_MA_V_Check = true;
                }
            }
        }
        initRatingSettingForTIF();
    }

    public void initRatingSettingForTIF() {
        MtkLog.d("Rating", "initRatingSettingForTIF");
        if (this.vRB_Y_A_Check) {
            this.vRB_Y_A.setChecked(true);
        } else {
            this.vRB_Y_A.setChecked(false);
        }
        if (this.vRB_Y7_A_Check) {
            this.vRB_Y7_A.setChecked(true);
        } else {
            this.vRB_Y7_A.setChecked(false);
        }
        if (this.vRB_G_A_Check) {
            this.vRB_G_A.setChecked(true);
        } else {
            this.vRB_G_A.setChecked(false);
        }
        if (this.vRB_PG_A_Check) {
            this.vRB_PG_A.setChecked(true);
        } else {
            this.vRB_PG_A.setChecked(false);
        }
        if (this.vRB_14_A_Check) {
            this.vRB_14_A.setChecked(true);
        } else {
            this.vRB_14_A.setChecked(false);
        }
        if (this.vRB_MA_A_Check) {
            this.vRB_MA_A.setChecked(true);
        } else {
            this.vRB_MA_A.setChecked(false);
        }
        if (this.vRB_14_D_Check) {
            this.vRB_14_D.setChecked(true);
        } else {
            this.vRB_14_D.setChecked(false);
        }
        if (this.vRB_14_L_Check) {
            this.vRB_14_L.setChecked(true);
        } else {
            this.vRB_14_L.setChecked(false);
        }
        if (this.vRB_14_S_Check) {
            this.vRB_14_S.setChecked(true);
        } else {
            this.vRB_14_S.setChecked(false);
        }
        if (this.vRB_14_V_Check) {
            this.vRB_14_V.setChecked(true);
        } else {
            this.vRB_14_V.setChecked(false);
        }
        if (this.vRB_MA_L_Check) {
            this.vRB_MA_L.setChecked(true);
        } else {
            this.vRB_MA_L.setChecked(false);
        }
        if (this.vRB_MA_S_Check) {
            this.vRB_MA_S.setChecked(true);
        } else {
            this.vRB_MA_S.setChecked(false);
        }
        if (this.vRB_MA_V_Check) {
            this.vRB_MA_V.setChecked(true);
        } else {
            this.vRB_MA_V.setChecked(false);
        }
        if (this.vRB_PG_D_Check) {
            this.vRB_PG_D.setChecked(true);
        } else {
            this.vRB_PG_D.setChecked(false);
        }
        if (this.vRB_PG_L_Check) {
            this.vRB_PG_L.setChecked(true);
        } else {
            this.vRB_PG_L.setChecked(false);
        }
        if (this.vRB_PG_S_Check) {
            this.vRB_PG_S.setChecked(true);
        } else {
            this.vRB_PG_S.setChecked(false);
        }
        if (this.vRB_PG_V_Check) {
            this.vRB_PG_V.setChecked(true);
        } else {
            this.vRB_PG_V.setChecked(false);
        }
        if (this.vRB_Y7_FV_Check) {
            this.vRB_Y7_FV.setChecked(true);
        } else {
            this.vRB_Y7_FV.setChecked(false);
        }
    }

    public void initRatingSetting() {
        MtkLog.d("Rating", "initRatingSetting" + this.mTV.getATSCRating().getUSTvRatingSettingInfo().isUsAgeTvYBlock());
        MtkTvUSTvRatingSettingInfoBase info = this.mTV.getATSCRating().getUSTvRatingSettingInfo();
        if (info.isUsAgeTvYBlock()) {
            this.vRB_Y_A.setChecked(true);
        } else {
            this.vRB_Y_A.setChecked(false);
        }
        if (info.isUsAgeTvY7Block()) {
            this.vRB_Y7_A.setChecked(true);
        } else {
            this.vRB_Y7_A.setChecked(false);
        }
        if (info.isUsAgeTvGBlock()) {
            this.vRB_G_A.setChecked(true);
        } else {
            this.vRB_G_A.setChecked(false);
        }
        if (info.isUsAgeTvPGBlock()) {
            this.vRB_PG_A.setChecked(true);
        } else {
            this.vRB_PG_A.setChecked(false);
        }
        if (info.isUsAgeTv14Block()) {
            this.vRB_14_A.setChecked(true);
        } else {
            this.vRB_14_A.setChecked(false);
        }
        if (info.isUsAgeTvMABlock()) {
            this.vRB_MA_A.setChecked(true);
        } else {
            this.vRB_MA_A.setChecked(false);
        }
        if (info.isUsCntTv14DBlock()) {
            this.vRB_14_D.setChecked(true);
        } else {
            this.vRB_14_D.setChecked(false);
        }
        if (info.isUsCntTv14LBlock()) {
            this.vRB_14_L.setChecked(true);
        } else {
            this.vRB_14_L.setChecked(false);
        }
        if (info.isUsCntTv14SBlock()) {
            this.vRB_14_S.setChecked(true);
        } else {
            this.vRB_14_S.setChecked(false);
        }
        if (info.isUsCntTv14VBlock()) {
            this.vRB_14_V.setChecked(true);
        } else {
            this.vRB_14_V.setChecked(false);
        }
        if (info.isUsCntTvMALBlock()) {
            this.vRB_MA_L.setChecked(true);
        } else {
            this.vRB_MA_L.setChecked(false);
        }
        if (info.isUsCntTvMASBlock()) {
            this.vRB_MA_S.setChecked(true);
        } else {
            this.vRB_MA_S.setChecked(false);
        }
        if (info.isUsCntTvMAVBlock()) {
            this.vRB_MA_V.setChecked(true);
        } else {
            this.vRB_MA_V.setChecked(false);
        }
        if (info.isUsCntTvPGDBlock()) {
            this.vRB_PG_D.setChecked(true);
        } else {
            this.vRB_PG_D.setChecked(false);
        }
        if (info.isUsCntTvPGLBlock()) {
            this.vRB_PG_L.setChecked(true);
        } else {
            this.vRB_PG_L.setChecked(false);
        }
        if (info.isUsCntTvPGSBlock()) {
            this.vRB_PG_S.setChecked(true);
        } else {
            this.vRB_PG_S.setChecked(false);
        }
        if (info.isUsCntTvPGVBlock()) {
            this.vRB_PG_V.setChecked(true);
        } else {
            this.vRB_PG_V.setChecked(false);
        }
        if (info.isUsCntTvY7FVBlock()) {
            this.vRB_Y7_FV.setChecked(true);
        } else {
            this.vRB_Y7_FV.setChecked(false);
        }
    }

    private void generateContentRating() {
        MtkLog.d("RatingView", "US generateContentRating");
        this.mWholeRatingList.clear();
        if (this.vRB_Y_A.isChecked()) {
            this.subRatingsList.clear();
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_Y", RatingConst.US_TV_Y_SUB_RATINGS));
        }
        this.subRatingsList.clear();
        String[] subRatings = RatingConst.US_TV_Y7_SUB_RATINGS;
        int[] indexs = {-1, -1};
        if (this.vRB_Y7_A.isChecked()) {
            indexs[0] = 0;
        }
        if (this.vRB_Y7_FV.isChecked()) {
            indexs[1] = 1;
        }
        for (int subidx : indexs) {
            if (subidx >= 0) {
                this.subRatingsList.add(subRatings[subidx]);
            }
        }
        String[] subRatings2 = (String[]) this.subRatingsList.toArray(new String[0]);
        if (subRatings2 != null && subRatings2.length > 0) {
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_Y7", subRatings2));
        }
        if (this.vRB_G_A.isChecked()) {
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_G", RatingConst.US_TV_G_SUB_RATINGS));
        }
        this.subRatingsList.clear();
        String[] subRatings3 = RatingConst.US_TV_PG_SUB_RATINGS;
        int[] indexs2 = {-1, -1, -1, -1, -1};
        if (this.vRB_PG_A.isChecked()) {
            indexs2[0] = 0;
        }
        if (this.vRB_PG_D.isChecked()) {
            indexs2[1] = 1;
        }
        if (this.vRB_PG_L.isChecked()) {
            indexs2[2] = 2;
        }
        if (this.vRB_PG_S.isChecked()) {
            indexs2[3] = 3;
        }
        if (this.vRB_PG_V.isChecked()) {
            indexs2[4] = 4;
        }
        for (int subidx2 : indexs2) {
            if (subidx2 >= 0) {
                this.subRatingsList.add(subRatings3[subidx2]);
            }
        }
        String[] subRatings4 = (String[]) this.subRatingsList.toArray(new String[0]);
        if (subRatings4 != null && subRatings4.length > 0) {
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_PG", subRatings4));
        }
        this.subRatingsList.clear();
        String[] subRatings5 = RatingConst.US_TV_14_SUB_RATINGS;
        int[] indexs3 = {-1, -1, -1, -1, -1};
        if (this.vRB_14_A.isChecked()) {
            indexs3[0] = 0;
        }
        if (this.vRB_14_D.isChecked()) {
            indexs3[1] = 1;
        }
        if (this.vRB_14_L.isChecked()) {
            indexs3[2] = 2;
        }
        if (this.vRB_14_S.isChecked()) {
            indexs3[3] = 3;
        }
        if (this.vRB_14_V.isChecked()) {
            indexs3[4] = 4;
        }
        for (int subidx3 : indexs3) {
            if (subidx3 >= 0) {
                this.subRatingsList.add(subRatings5[subidx3]);
            }
        }
        String[] subRatings6 = (String[]) this.subRatingsList.toArray(new String[0]);
        if (subRatings6 != null && subRatings6.length > 0) {
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_14", subRatings6));
        }
        this.subRatingsList.clear();
        String[] subRatings7 = RatingConst.US_TV_MA_SUB_RATINGS;
        int[] indexs4 = {-1, -1, -1, -1};
        if (this.vRB_MA_A.isChecked()) {
            indexs4[0] = 0;
        }
        if (this.vRB_MA_L.isChecked()) {
            indexs4[1] = 1;
        }
        if (this.vRB_MA_S.isChecked()) {
            indexs4[2] = 2;
        }
        if (this.vRB_MA_V.isChecked()) {
            indexs4[3] = 3;
        }
        for (int subidx4 : indexs4) {
            if (subidx4 >= 0) {
                this.subRatingsList.add(subRatings7[subidx4]);
            }
        }
        String[] subRatings8 = (String[]) this.subRatingsList.toArray(new String[0]);
        if (subRatings8 != null && subRatings8.length > 0) {
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_MA", subRatings8));
        }
        setAvailableRating();
    }

    /* access modifiers changed from: private */
    public void generateContentRatingPlus() {
        MtkLog.d("RatingView", "US generateContentRating");
        this.mWholeRatingList.clear();
        if (this.vRB_Y_A.isChecked()) {
            this.subRatingsList.clear();
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_Y", RatingConst.US_TV_Y_SUB_RATINGS));
        }
        this.subRatingsList.clear();
        String[] subRatings = RatingConst.US_TV_Y7_SUB_RATINGS;
        int[] indexs = {-1, -1};
        if (this.vRB_Y7_A.isChecked()) {
            indexs[0] = 0;
        }
        if (this.vRB_Y7_FV.isChecked()) {
            indexs[1] = 1;
        }
        for (int subidx : indexs) {
            if (subidx >= 0) {
                this.subRatingsList.add(subRatings[subidx]);
            }
        }
        String[] subRatings2 = (String[]) this.subRatingsList.toArray(new String[0]);
        if (subRatings2 != null) {
            int length = subRatings2.length;
            for (int i = 0; i < length; i++) {
                this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_Y7", new String[]{subRatings2[i]}));
            }
        }
        if (this.vRB_G_A.isChecked()) {
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_G", RatingConst.US_TV_G_SUB_RATINGS));
        }
        this.subRatingsList.clear();
        String[] subRatings3 = RatingConst.US_TV_PG_SUB_RATINGS;
        int[] indexs2 = {-1, -1, -1, -1, -1};
        if (this.vRB_PG_A.isChecked()) {
            indexs2[0] = 0;
        }
        if (this.vRB_PG_D.isChecked()) {
            indexs2[1] = 1;
        }
        if (this.vRB_PG_L.isChecked()) {
            indexs2[2] = 2;
        }
        if (this.vRB_PG_S.isChecked()) {
            indexs2[3] = 3;
        }
        if (this.vRB_PG_V.isChecked()) {
            indexs2[4] = 4;
        }
        for (int subidx2 : indexs2) {
            if (subidx2 >= 0) {
                this.subRatingsList.add(subRatings3[subidx2]);
            }
        }
        String[] subRatings4 = (String[]) this.subRatingsList.toArray(new String[0]);
        if (subRatings4 != null) {
            int length2 = subRatings4.length;
            for (int i2 = 0; i2 < length2; i2++) {
                this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_PG", new String[]{subRatings4[i2]}));
            }
        }
        this.subRatingsList.clear();
        String[] subRatings5 = RatingConst.US_TV_14_SUB_RATINGS;
        int[] indexs3 = {-1, -1, -1, -1, -1};
        if (this.vRB_14_A.isChecked()) {
            indexs3[0] = 0;
        }
        if (this.vRB_14_D.isChecked()) {
            indexs3[1] = 1;
        }
        if (this.vRB_14_L.isChecked()) {
            indexs3[2] = 2;
        }
        if (this.vRB_14_S.isChecked()) {
            indexs3[3] = 3;
        }
        if (this.vRB_14_V.isChecked()) {
            indexs3[4] = 4;
        }
        for (int subidx3 : indexs3) {
            if (subidx3 >= 0) {
                this.subRatingsList.add(subRatings5[subidx3]);
            }
        }
        String[] subRatings6 = (String[]) this.subRatingsList.toArray(new String[0]);
        if (subRatings6 != null) {
            int length3 = subRatings6.length;
            for (int i3 = 0; i3 < length3; i3++) {
                this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_14", new String[]{subRatings6[i3]}));
            }
        }
        this.subRatingsList.clear();
        String[] subRatings7 = RatingConst.US_TV_MA_SUB_RATINGS;
        int[] indexs4 = {-1, -1, -1, -1};
        if (this.vRB_MA_A.isChecked()) {
            indexs4[0] = 0;
        }
        if (this.vRB_MA_L.isChecked()) {
            indexs4[1] = 1;
        }
        if (this.vRB_MA_S.isChecked()) {
            indexs4[2] = 2;
        }
        if (this.vRB_MA_V.isChecked()) {
            indexs4[3] = 3;
        }
        for (int subidx4 : indexs4) {
            if (subidx4 >= 0) {
                this.subRatingsList.add(subRatings7[subidx4]);
            }
        }
        String[] subRatings8 = (String[]) this.subRatingsList.toArray(new String[0]);
        if (subRatings8 != null) {
            int length4 = subRatings8.length;
            for (int i4 = 0; i4 < length4; i4++) {
                this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_MA", new String[]{subRatings8[i4]}));
            }
        }
        setAvailableRatingPlus();
    }

    private void setAvailableRating() {
        List<TvContentRating> currRatings = this.mTvInputManager.getBlockedRatings();
        List<TvContentRating> tmpRatings = new ArrayList<>();
        for (int i = 0; i < this.mWholeRatingList.size(); i++) {
            if (currRatings != null && currRatings.contains(this.mWholeRatingList.get(i))) {
                MtkLog.d("TvContentRating", "contains:" + this.mWholeRatingList.get(i).getMainRating());
                tmpRatings.add(this.mWholeRatingList.get(i));
            }
        }
        this.mWholeRatingList.removeAll(tmpRatings);
        currRatings.removeAll(tmpRatings);
        MtkLog.d("TvContentRating", "currRatings.size ==" + currRatings.size());
        for (TvContentRating tcr : currRatings) {
            this.mTvInputManager.removeBlockedRating(tcr);
        }
        MtkLog.d("TvContentRating", "mWholeRatingList.size ==" + this.mWholeRatingList.size());
        for (TvContentRating tcr2 : this.mWholeRatingList) {
            this.mTvInputManager.addBlockedRating(tcr2);
        }
    }

    private void setAvailableRatingPlus() {
        List<TvContentRating> currRatings = this.mTvInputManager.getBlockedRatings();
        new ArrayList();
        if (currRatings.size() < this.mWholeRatingList.size()) {
            this.mWholeRatingList.removeAll(currRatings);
            MtkLog.d("TvContentRating", "add " + this.mWholeRatingList.size() + " rating");
            for (TvContentRating tcr : this.mWholeRatingList) {
                this.mTvInputManager.addBlockedRating(tcr);
            }
        } else if (currRatings.size() > this.mWholeRatingList.size()) {
            currRatings.removeAll(this.mWholeRatingList);
            MtkLog.d("TvContentRating", "remove " + currRatings.size() + " rating");
            for (TvContentRating tcr2 : currRatings) {
                this.mTvInputManager.removeBlockedRating(tcr2);
            }
        } else {
            MtkLog.e("TvContentRating", "same size something wrong!");
        }
    }

    private void requestUnLockRating(TvContentRating rating) {
        String focusWin = CommonIntegration.getInstance().getCurrentFocus();
        if (!focusWin.equals("main")) {
            focusWin.equals("sub");
        }
    }

    private void generateContentRatingWithoutTV_A() {
        this.mWholeRatingList.clear();
        if (this.vRB_Y_A.isChecked()) {
            this.subRatingsList.clear();
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_Y", RatingConst.US_TV_Y_SUB_RATINGS_N_A));
        }
        this.subRatingsList.clear();
        String[] subRatings = RatingConst.US_TV_Y7_SUB_RATINGS_N_A;
        int[] indexs = {-1};
        if (this.vRB_Y7_FV.isChecked()) {
            indexs[0] = 0;
        }
        for (int subidx : indexs) {
            if (subidx >= 0) {
                this.subRatingsList.add(subRatings[subidx]);
            }
        }
        String[] subRatings2 = (String[]) this.subRatingsList.toArray(new String[0]);
        if (subRatings2 != null && subRatings2.length > 0) {
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_Y7", subRatings2));
        } else if (this.vRB_Y7_A.isChecked()) {
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_Y7", new String[0]));
        }
        if (this.vRB_G_A.isChecked()) {
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_G", RatingConst.US_TV_G_SUB_RATINGS_N_A));
        }
        this.subRatingsList.clear();
        String[] subRatings3 = RatingConst.US_TV_PG_SUB_RATINGS_N_A;
        int[] indexs2 = {-1, -1, -1, -1};
        if (this.vRB_PG_D.isChecked()) {
            indexs2[0] = 0;
        }
        if (this.vRB_PG_L.isChecked()) {
            indexs2[1] = 1;
        }
        if (this.vRB_PG_S.isChecked()) {
            indexs2[2] = 2;
        }
        if (this.vRB_PG_V.isChecked()) {
            indexs2[3] = 3;
        }
        for (int subidx2 : indexs2) {
            if (subidx2 >= 0) {
                this.subRatingsList.add(subRatings3[subidx2]);
            }
        }
        String[] subRatings4 = (String[]) this.subRatingsList.toArray(new String[0]);
        if (subRatings4 != null && subRatings4.length > 0) {
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_PG", subRatings4));
        } else if (this.vRB_PG_A.isChecked()) {
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_PG", new String[0]));
        }
        this.subRatingsList.clear();
        String[] subRatings5 = RatingConst.US_TV_14_SUB_RATINGS_N_A;
        int[] indexs3 = {-1, -1, -1, -1};
        if (this.vRB_14_D.isChecked()) {
            indexs3[0] = 0;
        }
        if (this.vRB_14_L.isChecked()) {
            indexs3[1] = 1;
        }
        if (this.vRB_14_S.isChecked()) {
            indexs3[2] = 2;
        }
        if (this.vRB_14_V.isChecked()) {
            indexs3[3] = 3;
        }
        for (int subidx3 : indexs3) {
            if (subidx3 >= 0) {
                this.subRatingsList.add(subRatings5[subidx3]);
            }
        }
        String[] subRatings6 = (String[]) this.subRatingsList.toArray(new String[0]);
        if (subRatings6 != null && subRatings6.length > 0) {
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_14", subRatings6));
        } else if (this.vRB_14_A.isChecked()) {
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_14", new String[0]));
        }
        this.subRatingsList.clear();
        String[] subRatings7 = RatingConst.US_TV_MA_SUB_RATINGS_N_A;
        int[] indexs4 = {-1, -1, -1};
        if (this.vRB_MA_L.isChecked()) {
            indexs4[0] = 0;
        }
        if (this.vRB_MA_S.isChecked()) {
            indexs4[1] = 1;
        }
        if (this.vRB_MA_V.isChecked()) {
            indexs4[2] = 2;
        }
        for (int subidx4 : indexs4) {
            if (subidx4 >= 0) {
                this.subRatingsList.add(subRatings7[subidx4]);
            }
        }
        String[] subRatings8 = (String[]) this.subRatingsList.toArray(new String[0]);
        if (subRatings8 != null && subRatings8.length > 0) {
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_MA", subRatings8));
        } else if (this.vRB_MA_A.isChecked()) {
            this.mWholeRatingList.add(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_MA", new String[0]));
        }
        setAvailableRating();
    }

    /* access modifiers changed from: private */
    public void setRatingInfo(View v, boolean flag) {
        MtkTvUSTvRatingSettingInfoBase info = this.mTV.getATSCRating().getUSTvRatingSettingInfo();
        int id = ((RadioButton) v).getId();
        if (id == R.id.radioButton1) {
            MtkLog.d("Rating", "setUsAgeTvYBlock flag:" + flag);
            if (flag) {
                MtkLog.d("Rating", "setUsAgeTvYBlock:" + flag);
                info.setUsAgeTvYBlock(flag);
                info.setUsAgeTvY7Block(flag);
                info.setUsAgeTvGBlock(flag);
                info.setUsAgeTvPGBlock(flag);
                info.setUsAgeTv14Block(flag);
                info.setUsAgeTvMABlock(flag);
                info.setUsCntTvY7FVBlock(flag);
                info.setUsCntTvPGDBlock(flag);
                info.setUsCntTvPGLBlock(flag);
                info.setUsCntTvPGSBlock(flag);
                info.setUsCntTvPGVBlock(flag);
                info.setUsCntTv14DBlock(flag);
                info.setUsCntTv14LBlock(flag);
                info.setUsCntTv14SBlock(flag);
                info.setUsCntTv14VBlock(flag);
                info.setUsCntTvMALBlock(flag);
                info.setUsCntTvMASBlock(flag);
                info.setUsCntTvMAVBlock(flag);
                this.vRB_Y_A_Check = true;
                this.vRB_Y7_A_Check = true;
                this.vRB_G_A_Check = true;
                this.vRB_PG_A_Check = true;
                this.vRB_14_A_Check = true;
                this.vRB_MA_A_Check = true;
                this.vRB_PG_D_Check = true;
                this.vRB_14_D_Check = true;
                this.vRB_PG_L_Check = true;
                this.vRB_14_L_Check = true;
                this.vRB_MA_L_Check = true;
                this.vRB_PG_S_Check = true;
                this.vRB_14_S_Check = true;
                this.vRB_MA_S_Check = true;
                this.vRB_PG_V_Check = true;
                this.vRB_14_V_Check = true;
                this.vRB_MA_V_Check = true;
                this.vRB_Y7_FV_Check = true;
            } else {
                info.setUsAgeTvYBlock(flag);
                this.vRB_Y_A_Check = false;
            }
        } else if (id != R.id.radioButton19) {
            if (id != R.id.radioButton31) {
                if (id != R.id.radioButton7) {
                    switch (id) {
                        case R.id.radioButton12 /*2131362721*/:
                            info.setUsCntTvY7FVBlock(flag);
                            this.vRB_Y7_FV_Check = flag;
                            break;
                        case R.id.radioButton13 /*2131362722*/:
                            if (!flag) {
                                info.setUsAgeTvYBlock(flag);
                                info.setUsAgeTvY7Block(flag);
                                info.setUsCntTvY7FVBlock(flag);
                                info.setUsAgeTvGBlock(flag);
                                this.vRB_Y_A_Check = false;
                                this.vRB_Y7_A_Check = false;
                                this.vRB_Y7_FV_Check = false;
                                this.vRB_G_A_Check = false;
                                break;
                            } else {
                                info.setUsAgeTvGBlock(flag);
                                info.setUsAgeTvPGBlock(flag);
                                info.setUsAgeTv14Block(flag);
                                info.setUsAgeTvMABlock(flag);
                                info.setUsCntTvPGDBlock(flag);
                                info.setUsCntTvPGLBlock(flag);
                                info.setUsCntTvPGSBlock(flag);
                                info.setUsCntTvPGVBlock(flag);
                                info.setUsCntTv14DBlock(flag);
                                info.setUsCntTv14LBlock(flag);
                                info.setUsCntTv14SBlock(flag);
                                info.setUsCntTv14VBlock(flag);
                                info.setUsCntTvMALBlock(flag);
                                info.setUsCntTvMASBlock(flag);
                                info.setUsCntTvMAVBlock(flag);
                                this.vRB_G_A_Check = true;
                                this.vRB_PG_A_Check = true;
                                this.vRB_14_A_Check = true;
                                this.vRB_MA_A_Check = true;
                                this.vRB_PG_D_Check = true;
                                this.vRB_14_D_Check = true;
                                this.vRB_PG_L_Check = true;
                                this.vRB_14_L_Check = true;
                                this.vRB_MA_L_Check = true;
                                this.vRB_PG_S_Check = true;
                                this.vRB_14_S_Check = true;
                                this.vRB_MA_S_Check = true;
                                this.vRB_PG_V_Check = true;
                                this.vRB_14_V_Check = true;
                                this.vRB_MA_V_Check = true;
                                break;
                            }
                        default:
                            switch (id) {
                                case R.id.radioButton20 /*2131362730*/:
                                    info.setUsCntTvPGDBlock(flag);
                                    this.vRB_PG_D_Check = flag;
                                    if (flag) {
                                        info.setUsCntTv14DBlock(flag);
                                        this.vRB_14_D_Check = flag;
                                        break;
                                    }
                                    break;
                                case R.id.radioButton21 /*2131362731*/:
                                    info.setUsCntTvPGLBlock(flag);
                                    this.vRB_PG_L_Check = flag;
                                    if (flag) {
                                        info.setUsCntTv14LBlock(flag);
                                        info.setUsCntTvMALBlock(flag);
                                        this.vRB_14_L_Check = flag;
                                        this.vRB_MA_L_Check = flag;
                                        break;
                                    }
                                    break;
                                case R.id.radioButton22 /*2131362732*/:
                                    info.setUsCntTvPGSBlock(flag);
                                    this.vRB_PG_S_Check = flag;
                                    if (flag) {
                                        info.setUsCntTv14SBlock(flag);
                                        info.setUsCntTvMASBlock(flag);
                                        this.vRB_14_S_Check = flag;
                                        this.vRB_MA_S_Check = flag;
                                        break;
                                    }
                                    break;
                                case R.id.radioButton23 /*2131362733*/:
                                    info.setUsCntTvPGVBlock(flag);
                                    this.vRB_PG_V_Check = flag;
                                    if (flag) {
                                        info.setUsCntTv14VBlock(flag);
                                        info.setUsCntTvMAVBlock(flag);
                                        this.vRB_14_V_Check = flag;
                                        this.vRB_MA_V_Check = flag;
                                        break;
                                    }
                                    break;
                                default:
                                    switch (id) {
                                        case R.id.radioButton25 /*2131362735*/:
                                            if (!flag) {
                                                info.setUsAgeTvYBlock(flag);
                                                info.setUsAgeTvY7Block(flag);
                                                info.setUsCntTvY7FVBlock(flag);
                                                info.setUsAgeTvGBlock(flag);
                                                info.setUsAgeTvPGBlock(flag);
                                                info.setUsCntTvPGDBlock(flag);
                                                info.setUsCntTvPGLBlock(flag);
                                                info.setUsCntTvPGSBlock(flag);
                                                info.setUsCntTvPGVBlock(flag);
                                                info.setUsAgeTv14Block(flag);
                                                info.setUsCntTv14DBlock(flag);
                                                info.setUsCntTv14LBlock(flag);
                                                info.setUsCntTv14SBlock(flag);
                                                info.setUsCntTv14VBlock(flag);
                                                this.vRB_Y_A_Check = false;
                                                this.vRB_Y7_A_Check = false;
                                                this.vRB_Y7_FV_Check = false;
                                                this.vRB_G_A_Check = false;
                                                this.vRB_PG_A_Check = false;
                                                this.vRB_PG_D_Check = false;
                                                this.vRB_PG_L_Check = false;
                                                this.vRB_PG_S_Check = false;
                                                this.vRB_PG_V_Check = false;
                                                this.vRB_14_A_Check = false;
                                                this.vRB_14_D_Check = false;
                                                this.vRB_14_L_Check = false;
                                                this.vRB_14_S_Check = false;
                                                this.vRB_14_V_Check = false;
                                                break;
                                            } else {
                                                info.setUsAgeTv14Block(flag);
                                                info.setUsAgeTvMABlock(flag);
                                                info.setUsCntTv14DBlock(flag);
                                                info.setUsCntTv14LBlock(flag);
                                                info.setUsCntTv14SBlock(flag);
                                                info.setUsCntTv14VBlock(flag);
                                                info.setUsCntTvMALBlock(flag);
                                                info.setUsCntTvMASBlock(flag);
                                                info.setUsCntTvMAVBlock(flag);
                                                this.vRB_14_A_Check = true;
                                                this.vRB_MA_A_Check = true;
                                                this.vRB_14_D_Check = true;
                                                this.vRB_14_L_Check = true;
                                                this.vRB_MA_L_Check = true;
                                                this.vRB_14_S_Check = true;
                                                this.vRB_MA_S_Check = true;
                                                this.vRB_14_V_Check = true;
                                                this.vRB_MA_V_Check = true;
                                                break;
                                            }
                                        case R.id.radioButton26 /*2131362736*/:
                                            info.setUsCntTv14DBlock(flag);
                                            this.vRB_14_D_Check = flag;
                                            if (!flag) {
                                                info.setUsCntTvPGDBlock(flag);
                                                this.vRB_PG_D_Check = flag;
                                                break;
                                            }
                                            break;
                                        case R.id.radioButton27 /*2131362737*/:
                                            info.setUsCntTv14LBlock(flag);
                                            this.vRB_14_L_Check = flag;
                                            if (!flag) {
                                                info.setUsCntTvPGLBlock(flag);
                                                this.vRB_PG_L_Check = flag;
                                                break;
                                            } else {
                                                info.setUsCntTvMALBlock(flag);
                                                this.vRB_MA_L_Check = flag;
                                                break;
                                            }
                                        case R.id.radioButton28 /*2131362738*/:
                                            info.setUsCntTv14SBlock(flag);
                                            this.vRB_14_S_Check = flag;
                                            if (!flag) {
                                                info.setUsCntTvPGSBlock(flag);
                                                this.vRB_PG_S_Check = flag;
                                                break;
                                            } else {
                                                info.setUsCntTvMASBlock(flag);
                                                this.vRB_MA_S_Check = flag;
                                                break;
                                            }
                                        case R.id.radioButton29 /*2131362739*/:
                                            info.setUsCntTv14VBlock(flag);
                                            this.vRB_14_V_Check = flag;
                                            if (!flag) {
                                                info.setUsCntTvPGVBlock(flag);
                                                this.vRB_PG_V_Check = flag;
                                                break;
                                            } else {
                                                info.setUsCntTvMAVBlock(flag);
                                                this.vRB_MA_V_Check = flag;
                                                break;
                                            }
                                        default:
                                            switch (id) {
                                                case R.id.radioButton33 /*2131362744*/:
                                                    info.setUsCntTvMALBlock(flag);
                                                    this.vRB_MA_L_Check = flag;
                                                    if (!flag) {
                                                        info.setUsCntTv14LBlock(flag);
                                                        info.setUsCntTvPGLBlock(flag);
                                                        this.vRB_14_L_Check = flag;
                                                        this.vRB_PG_L_Check = flag;
                                                        break;
                                                    }
                                                    break;
                                                case R.id.radioButton34 /*2131362745*/:
                                                    info.setUsCntTvMASBlock(flag);
                                                    this.vRB_MA_S_Check = flag;
                                                    if (!flag) {
                                                        info.setUsCntTv14SBlock(flag);
                                                        info.setUsCntTvPGSBlock(flag);
                                                        this.vRB_14_S_Check = flag;
                                                        this.vRB_PG_S_Check = flag;
                                                        break;
                                                    }
                                                    break;
                                                case R.id.radioButton35 /*2131362746*/:
                                                    info.setUsCntTvMAVBlock(flag);
                                                    this.vRB_MA_V_Check = flag;
                                                    if (!flag) {
                                                        info.setUsCntTv14VBlock(flag);
                                                        info.setUsCntTvPGVBlock(flag);
                                                        this.vRB_14_V_Check = flag;
                                                        this.vRB_PG_V_Check = flag;
                                                        break;
                                                    }
                                                    break;
                                            }
                                    }
                            }
                    }
                } else if (flag) {
                    info.setUsAgeTvY7Block(flag);
                    info.setUsAgeTvGBlock(flag);
                    info.setUsAgeTvPGBlock(flag);
                    info.setUsAgeTv14Block(flag);
                    info.setUsAgeTvMABlock(flag);
                    info.setUsCntTvY7FVBlock(flag);
                    info.setUsCntTvPGDBlock(flag);
                    info.setUsCntTvPGLBlock(flag);
                    info.setUsCntTvPGSBlock(flag);
                    info.setUsCntTvPGVBlock(flag);
                    info.setUsCntTv14DBlock(flag);
                    info.setUsCntTv14LBlock(flag);
                    info.setUsCntTv14SBlock(flag);
                    info.setUsCntTv14VBlock(flag);
                    info.setUsCntTvMALBlock(flag);
                    info.setUsCntTvMASBlock(flag);
                    info.setUsCntTvMAVBlock(flag);
                    this.vRB_Y7_A_Check = true;
                    this.vRB_G_A_Check = true;
                    this.vRB_PG_A_Check = true;
                    this.vRB_14_A_Check = true;
                    this.vRB_MA_A_Check = true;
                    this.vRB_PG_D_Check = true;
                    this.vRB_14_D_Check = true;
                    this.vRB_PG_L_Check = true;
                    this.vRB_14_L_Check = true;
                    this.vRB_MA_L_Check = true;
                    this.vRB_PG_S_Check = true;
                    this.vRB_14_S_Check = true;
                    this.vRB_MA_S_Check = true;
                    this.vRB_PG_V_Check = true;
                    this.vRB_14_V_Check = true;
                    this.vRB_MA_V_Check = true;
                    this.vRB_Y7_FV_Check = true;
                } else {
                    info.setUsAgeTvYBlock(flag);
                    info.setUsAgeTvY7Block(flag);
                    info.setUsCntTvY7FVBlock(flag);
                    this.vRB_Y_A_Check = false;
                    this.vRB_Y7_A_Check = false;
                    this.vRB_Y7_FV_Check = false;
                }
            } else if (flag) {
                info.setUsAgeTvMABlock(flag);
                info.setUsCntTvMALBlock(flag);
                info.setUsCntTvMASBlock(flag);
                info.setUsCntTvMAVBlock(flag);
                this.vRB_MA_A_Check = true;
                this.vRB_MA_L_Check = true;
                this.vRB_MA_S_Check = true;
                this.vRB_MA_V_Check = true;
            } else {
                info.setUsAgeTvYBlock(flag);
                info.setUsAgeTvY7Block(flag);
                info.setUsCntTvY7FVBlock(flag);
                info.setUsAgeTvGBlock(flag);
                info.setUsAgeTvPGBlock(flag);
                info.setUsCntTvPGDBlock(flag);
                info.setUsCntTvPGLBlock(flag);
                info.setUsCntTvPGSBlock(flag);
                info.setUsCntTvPGVBlock(flag);
                info.setUsAgeTv14Block(flag);
                info.setUsCntTv14DBlock(flag);
                info.setUsCntTv14LBlock(flag);
                info.setUsCntTv14SBlock(flag);
                info.setUsCntTv14VBlock(flag);
                info.setUsAgeTvMABlock(flag);
                info.setUsCntTvMALBlock(flag);
                info.setUsCntTvMASBlock(flag);
                info.setUsCntTvMAVBlock(flag);
                this.vRB_Y_A_Check = false;
                this.vRB_Y7_A_Check = false;
                this.vRB_Y7_FV_Check = false;
                this.vRB_G_A_Check = false;
                this.vRB_PG_A_Check = false;
                this.vRB_PG_D_Check = false;
                this.vRB_PG_L_Check = false;
                this.vRB_PG_S_Check = false;
                this.vRB_PG_V_Check = false;
                this.vRB_14_A_Check = false;
                this.vRB_14_D_Check = false;
                this.vRB_14_L_Check = false;
                this.vRB_14_S_Check = false;
                this.vRB_14_V_Check = false;
                this.vRB_MA_A_Check = false;
                this.vRB_MA_L_Check = false;
                this.vRB_MA_S_Check = false;
                this.vRB_MA_V_Check = false;
            }
        } else if (flag) {
            info.setUsAgeTvPGBlock(flag);
            info.setUsAgeTv14Block(flag);
            info.setUsAgeTvMABlock(flag);
            info.setUsCntTvPGDBlock(flag);
            info.setUsCntTvPGLBlock(flag);
            info.setUsCntTvPGSBlock(flag);
            info.setUsCntTvPGVBlock(flag);
            info.setUsCntTv14DBlock(flag);
            info.setUsCntTv14LBlock(flag);
            info.setUsCntTv14SBlock(flag);
            info.setUsCntTv14VBlock(flag);
            info.setUsCntTvMALBlock(flag);
            info.setUsCntTvMASBlock(flag);
            info.setUsCntTvMAVBlock(flag);
            this.vRB_PG_A_Check = true;
            this.vRB_14_A_Check = true;
            this.vRB_MA_A_Check = true;
            this.vRB_PG_D_Check = true;
            this.vRB_14_D_Check = true;
            this.vRB_PG_L_Check = true;
            this.vRB_14_L_Check = true;
            this.vRB_MA_L_Check = true;
            this.vRB_PG_S_Check = true;
            this.vRB_14_S_Check = true;
            this.vRB_MA_S_Check = true;
            this.vRB_PG_V_Check = true;
            this.vRB_14_V_Check = true;
            this.vRB_MA_V_Check = true;
        } else {
            info.setUsAgeTvYBlock(flag);
            info.setUsAgeTvY7Block(flag);
            info.setUsCntTvY7FVBlock(flag);
            info.setUsAgeTvGBlock(flag);
            info.setUsAgeTvPGBlock(flag);
            info.setUsCntTvPGDBlock(flag);
            info.setUsCntTvPGLBlock(flag);
            info.setUsCntTvPGSBlock(flag);
            info.setUsCntTvPGVBlock(flag);
            this.vRB_Y_A_Check = false;
            this.vRB_Y7_A_Check = false;
            this.vRB_Y7_FV_Check = false;
            this.vRB_G_A_Check = false;
            this.vRB_PG_A_Check = false;
            this.vRB_PG_D_Check = false;
            this.vRB_PG_L_Check = false;
            this.vRB_PG_S_Check = false;
            this.vRB_PG_V_Check = false;
        }
        if (!MarketRegionInfo.isFunctionSupport(21)) {
            Map<String, Boolean> map = new HashMap<>();
            map.put("Tv_Y", Boolean.valueOf(info.isUsAgeTvYBlock()));
            map.put("Tv_Y7", Boolean.valueOf(info.isUsAgeTvY7Block()));
            map.put("Tv_G", Boolean.valueOf(info.isUsAgeTvGBlock()));
            map.put("Tv_PG", Boolean.valueOf(info.isUsAgeTvPGBlock()));
            map.put("Tv_14", Boolean.valueOf(info.isUsAgeTv14Block()));
            map.put("Tv_MA", Boolean.valueOf(info.isUsAgeTvMABlock()));
            map.put("Tv_14_D", Boolean.valueOf(info.isUsCntTv14DBlock()));
            map.put("Tv_14_L", Boolean.valueOf(info.isUsCntTv14LBlock()));
            map.put("Tv_14_S", Boolean.valueOf(info.isUsCntTv14SBlock()));
            map.put("Tv_14_V", Boolean.valueOf(info.isUsCntTv14VBlock()));
            map.put("Tv_MA_S", Boolean.valueOf(info.isUsCntTvMASBlock()));
            map.put("Tv_MA_L", Boolean.valueOf(info.isUsCntTvMALBlock()));
            map.put("Tv_MA_V", Boolean.valueOf(info.isUsCntTvMAVBlock()));
            map.put("Tv_PG_D", Boolean.valueOf(info.isUsCntTvPGDBlock()));
            map.put("Tv_PG_L", Boolean.valueOf(info.isUsCntTvPGLBlock()));
            map.put("Tv_PG_S", Boolean.valueOf(info.isUsCntTvPGSBlock()));
            map.put("Tv_PG_V", Boolean.valueOf(info.isUsCntTvPGVBlock()));
            map.put("Tv_Y7_FV", Boolean.valueOf(info.isUsCntTvY7FVBlock()));
            this.mTV.getATSCRating().setUSTvRatingSettingInfo(info);
        }
    }
}
