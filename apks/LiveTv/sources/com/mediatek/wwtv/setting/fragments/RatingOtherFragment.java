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
import android.widget.TextView;
import com.mediatek.wwtv.setting.preferences.PreferenceUtil;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.List;

public class RatingOtherFragment extends Fragment {
    private boolean isPositionView = false;
    private Action mAction;
    private Context mContext;
    private List<RadioButton> mGroup = new ArrayList();
    private LayoutInflater mInflater;
    String mItemId;
    private ViewGroup mRootView;
    TVContent mTV;
    TvInputManager mTvInputManager;
    private RadioButton vRadioButton1;
    private RadioButton vRadioButton2;
    private RadioButton vRadioButton3;
    private RadioButton vRadioButton4;
    private RadioButton vRadioButton5;
    private RadioButton vRadioButton6;
    private TextView vTextView1;
    private TextView vTextView2;
    private TextView vTextView3;
    private TextView vTextView4;
    private TextView vTextView5;
    private TextView vTextView6;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        PreferenceUtil instance = PreferenceUtil.getInstance(this.mContext);
        this.mTV = TVContent.getInstance(this.mContext);
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.mItemId = bundle.getCharSequence(PreferenceUtil.PARENT_PREFERENCE_ID).toString();
        }
        MtkLog.d("guanglei", "mItemId:" + this.mItemId);
        this.mTvInputManager = (TvInputManager) this.mContext.getSystemService("tv_input");
    }

    public void setAction(Action action) {
        this.mAction = action;
        if (action.mDataType == Action.DataType.POSITIONVIEW) {
            this.isPositionView = true;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mRootView = (ViewGroup) inflater.inflate(R.layout.menu_french_ratings_view, (ViewGroup) null);
        initView();
        init(this.mItemId);
        setListener();
        return this.mRootView;
    }

    private void initView() {
        this.vTextView1 = (TextView) this.mRootView.findViewById(R.id.textview1);
        this.vTextView2 = (TextView) this.mRootView.findViewById(R.id.textview2);
        this.vTextView3 = (TextView) this.mRootView.findViewById(R.id.textview3);
        this.vTextView4 = (TextView) this.mRootView.findViewById(R.id.textview4);
        this.vTextView5 = (TextView) this.mRootView.findViewById(R.id.textview5);
        this.vTextView6 = (TextView) this.mRootView.findViewById(R.id.textview6);
        this.vRadioButton1 = (RadioButton) this.mRootView.findViewById(R.id.radioButton1);
        this.vRadioButton2 = (RadioButton) this.mRootView.findViewById(R.id.radioButton2);
        this.vRadioButton3 = (RadioButton) this.mRootView.findViewById(R.id.radioButton3);
        this.vRadioButton4 = (RadioButton) this.mRootView.findViewById(R.id.radioButton4);
        this.vRadioButton5 = (RadioButton) this.mRootView.findViewById(R.id.radioButton5);
        this.vRadioButton6 = (RadioButton) this.mRootView.findViewById(R.id.radioButton6);
        this.mGroup.add(this.vRadioButton1);
        this.mGroup.add(this.vRadioButton2);
        this.mGroup.add(this.vRadioButton3);
        this.mGroup.add(this.vRadioButton4);
        this.mGroup.add(this.vRadioButton5);
        this.mGroup.add(this.vRadioButton6);
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
                            MtkLog.d("Rating", "setChecked false:");
                            ((RadioButton) v).setChecked(false);
                            RatingOtherFragment.this.setRatingInfo(v, false);
                        } else {
                            MtkLog.d("Rating", "setChecked true:");
                            ((RadioButton) v).setChecked(true);
                            RatingOtherFragment.this.setRatingInfo(v, true);
                        }
                        RatingOtherFragment.this.initRatingSetting(RatingOtherFragment.this.mItemId);
                    }
                    return false;
                }
            });
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!this.vRadioButton1.hasFocus()) {
            this.vRadioButton1.requestFocus();
        }
    }

    /* access modifiers changed from: private */
    public void setRatingInfo(View v, boolean flag) {
        if (this.mItemId.equals(MenuConfigManager.PARENTAL_US_MOVIE_RATINGS)) {
            int id = ((RadioButton) v).getId();
            if (id != R.id.radioButton1) {
                if (id != R.id.radioButton2) {
                    if (id != R.id.radioButton3) {
                        switch (id) {
                            case R.id.radioButton4 /*2131362750*/:
                                if (flag) {
                                    this.mTV.getATSCRating().setUSMovieRatingSettingInfo(3);
                                    return;
                                } else {
                                    this.mTV.getATSCRating().setUSMovieRatingSettingInfo(4);
                                    return;
                                }
                            case R.id.radioButton5 /*2131362751*/:
                                if (flag) {
                                    this.mTV.getATSCRating().setUSMovieRatingSettingInfo(4);
                                    return;
                                } else {
                                    this.mTV.getATSCRating().setUSMovieRatingSettingInfo(5);
                                    return;
                                }
                            case R.id.radioButton6 /*2131362752*/:
                                if (flag) {
                                    this.mTV.getATSCRating().setUSMovieRatingSettingInfo(5);
                                    return;
                                } else {
                                    this.mTV.getATSCRating().setUSMovieRatingSettingInfo(6);
                                    return;
                                }
                            default:
                                return;
                        }
                    } else if (flag) {
                        this.mTV.getATSCRating().setUSMovieRatingSettingInfo(2);
                    } else {
                        this.mTV.getATSCRating().setUSMovieRatingSettingInfo(3);
                    }
                } else if (flag) {
                    this.mTV.getATSCRating().setUSMovieRatingSettingInfo(1);
                } else {
                    this.mTV.getATSCRating().setUSMovieRatingSettingInfo(2);
                }
            } else if (flag) {
                this.mTV.getATSCRating().setUSMovieRatingSettingInfo(0);
            } else {
                this.mTV.getATSCRating().setUSMovieRatingSettingInfo(1);
            }
        } else if (this.mItemId.equals(MenuConfigManager.PARENTAL_CANADIAN_ENGLISH_RATINGS)) {
            int id2 = ((RadioButton) v).getId();
            if (id2 == R.id.radioButton1) {
                MtkLog.d("Rating", "setCANEngRatingSettingInfo flag:" + flag);
                if (flag) {
                    this.mTV.getATSCRating().setCANEngRatingSettingInfo(1);
                } else {
                    this.mTV.getATSCRating().setCANEngRatingSettingInfo(2);
                }
            } else if (id2 != R.id.radioButton2) {
                if (id2 != R.id.radioButton3) {
                    switch (id2) {
                        case R.id.radioButton4 /*2131362750*/:
                            if (flag) {
                                this.mTV.getATSCRating().setCANEngRatingSettingInfo(4);
                                return;
                            } else {
                                this.mTV.getATSCRating().setCANEngRatingSettingInfo(5);
                                return;
                            }
                        case R.id.radioButton5 /*2131362751*/:
                            if (flag) {
                                this.mTV.getATSCRating().setCANEngRatingSettingInfo(5);
                                return;
                            } else {
                                this.mTV.getATSCRating().setCANEngRatingSettingInfo(6);
                                return;
                            }
                        case R.id.radioButton6 /*2131362752*/:
                            if (flag) {
                                this.mTV.getATSCRating().setCANEngRatingSettingInfo(6);
                                return;
                            } else {
                                this.mTV.getATSCRating().setCANEngRatingSettingInfo(7);
                                return;
                            }
                        default:
                            return;
                    }
                } else if (flag) {
                    this.mTV.getATSCRating().setCANEngRatingSettingInfo(3);
                } else {
                    this.mTV.getATSCRating().setCANEngRatingSettingInfo(4);
                }
            } else if (flag) {
                this.mTV.getATSCRating().setCANEngRatingSettingInfo(2);
            } else {
                this.mTV.getATSCRating().setCANEngRatingSettingInfo(3);
            }
        } else {
            int id3 = ((RadioButton) v).getId();
            if (id3 != R.id.radioButton1) {
                if (id3 != R.id.radioButton2) {
                    if (id3 != R.id.radioButton3) {
                        switch (id3) {
                            case R.id.radioButton4 /*2131362750*/:
                                if (flag) {
                                    this.mTV.getATSCRating().setCANFreRatingSettingInfo(4);
                                    return;
                                } else {
                                    this.mTV.getATSCRating().setCANFreRatingSettingInfo(5);
                                    return;
                                }
                            case R.id.radioButton5 /*2131362751*/:
                                if (flag) {
                                    this.mTV.getATSCRating().setCANFreRatingSettingInfo(5);
                                    return;
                                } else {
                                    this.mTV.getATSCRating().setCANFreRatingSettingInfo(6);
                                    return;
                                }
                            case R.id.radioButton6 /*2131362752*/:
                                if (flag) {
                                    this.mTV.getATSCRating().setCANFreRatingSettingInfo(5);
                                    return;
                                } else {
                                    this.mTV.getATSCRating().setCANFreRatingSettingInfo(6);
                                    return;
                                }
                            default:
                                return;
                        }
                    } else if (flag) {
                        this.mTV.getATSCRating().setCANFreRatingSettingInfo(3);
                    } else {
                        this.mTV.getATSCRating().setCANFreRatingSettingInfo(4);
                    }
                } else if (flag) {
                    this.mTV.getATSCRating().setCANFreRatingSettingInfo(2);
                } else {
                    this.mTV.getATSCRating().setCANFreRatingSettingInfo(3);
                }
            } else if (flag) {
                this.mTV.getATSCRating().setCANFreRatingSettingInfo(1);
            } else {
                this.mTV.getATSCRating().setCANFreRatingSettingInfo(2);
            }
        }
    }

    private void setRatingInfoForTIF(View v, boolean flag) {
        MtkLog.d("RatingOtherView", "setRatingInfoForTIF flag:" + flag);
        int id = ((RadioButton) v).getId();
        if (id != R.id.radioButton1) {
            if (id != R.id.radioButton2) {
                if (id != R.id.radioButton3) {
                    switch (id) {
                        case R.id.radioButton4 /*2131362750*/:
                            if (flag) {
                                this.vRadioButton4.setChecked(true);
                                this.vRadioButton5.setChecked(true);
                                this.vRadioButton6.setChecked(true);
                                return;
                            }
                            this.vRadioButton1.setChecked(false);
                            this.vRadioButton2.setChecked(false);
                            this.vRadioButton3.setChecked(false);
                            this.vRadioButton4.setChecked(false);
                            return;
                        case R.id.radioButton5 /*2131362751*/:
                            if (flag) {
                                this.vRadioButton5.setChecked(true);
                                this.vRadioButton6.setChecked(true);
                                return;
                            }
                            this.vRadioButton1.setChecked(false);
                            this.vRadioButton2.setChecked(false);
                            this.vRadioButton3.setChecked(false);
                            this.vRadioButton4.setChecked(false);
                            this.vRadioButton5.setChecked(false);
                            return;
                        case R.id.radioButton6 /*2131362752*/:
                            if (flag) {
                                this.vRadioButton6.setChecked(true);
                                return;
                            }
                            this.vRadioButton1.setChecked(false);
                            this.vRadioButton2.setChecked(false);
                            this.vRadioButton3.setChecked(false);
                            this.vRadioButton4.setChecked(false);
                            this.vRadioButton5.setChecked(false);
                            this.vRadioButton6.setChecked(false);
                            return;
                        default:
                            return;
                    }
                } else if (flag) {
                    this.vRadioButton3.setChecked(true);
                    this.vRadioButton4.setChecked(true);
                    this.vRadioButton5.setChecked(true);
                    this.vRadioButton6.setChecked(true);
                } else {
                    this.vRadioButton1.setChecked(false);
                    this.vRadioButton2.setChecked(false);
                    this.vRadioButton3.setChecked(false);
                }
            } else if (flag) {
                this.vRadioButton2.setChecked(true);
                this.vRadioButton3.setChecked(true);
                this.vRadioButton4.setChecked(true);
                this.vRadioButton5.setChecked(true);
                this.vRadioButton6.setChecked(true);
            } else {
                this.vRadioButton1.setChecked(false);
                this.vRadioButton2.setChecked(false);
            }
        } else if (flag) {
            this.vRadioButton1.setChecked(true);
            this.vRadioButton2.setChecked(true);
            this.vRadioButton3.setChecked(true);
            this.vRadioButton4.setChecked(true);
            this.vRadioButton5.setChecked(true);
            this.vRadioButton6.setChecked(true);
        } else {
            this.vRadioButton1.setChecked(false);
        }
    }

    public void init(String itemID) {
        this.mItemId = itemID;
        this.vTextView6.setVisibility(0);
        this.vRadioButton6.setVisibility(0);
        if (itemID.equals(MenuConfigManager.PARENTAL_US_MOVIE_RATINGS)) {
            this.vTextView1.setText(R.string.menu_rating_movie_g);
            this.vTextView2.setText(R.string.menu_rating_movie_pg);
            this.vTextView3.setText(R.string.menu_rating_movie_pg13);
            this.vTextView4.setText(R.string.menu_rating_movie_r);
            this.vTextView5.setText(R.string.menu_rating_movie_nc17);
            this.vTextView6.setText(R.string.menu_rating_movie_x);
            this.vRadioButton5.setNextFocusDownId(R.id.radioButton6);
            this.vRadioButton6.setNextFocusDownId(R.id.radioButton1);
            this.vRadioButton1.setNextFocusUpId(R.id.radioButton6);
        } else if (itemID.equals(MenuConfigManager.PARENTAL_CANADIAN_ENGLISH_RATINGS)) {
            this.vTextView1.setText(R.string.menu_rating_english_c);
            this.vTextView2.setText(R.string.menu_rating_english_c8);
            this.vTextView3.setText(R.string.menu_rating_english_g);
            this.vTextView4.setText(R.string.menu_rating_english_pg);
            this.vTextView5.setText(R.string.menu_rating_english_14);
            this.vTextView6.setText(R.string.menu_rating_english_18);
            this.vRadioButton5.setNextFocusDownId(R.id.radioButton6);
            this.vRadioButton1.setNextFocusUpId(R.id.radioButton6);
            this.vRadioButton6.setNextFocusDownId(R.id.radioButton1);
        } else {
            this.vTextView1.setText(R.string.menu_rating_french_g);
            this.vTextView2.setText(R.string.menu_rating_french_8ans);
            this.vTextView3.setText(R.string.menu_rating_french_13ans);
            this.vTextView4.setText(R.string.menu_rating_french_16ans);
            this.vTextView5.setText(R.string.menu_rating_french_18ans);
            this.vTextView6.setVisibility(4);
            this.vRadioButton6.setVisibility(4);
            this.vRadioButton1.setNextFocusUpId(R.id.radioButton5);
            this.vRadioButton5.setNextFocusDownId(R.id.radioButton1);
        }
        this.vRadioButton1.setNextFocusLeftId(R.id.radioButton1);
        this.vRadioButton1.setNextFocusRightId(R.id.radioButton1);
        this.vRadioButton2.setNextFocusLeftId(R.id.radioButton2);
        this.vRadioButton2.setNextFocusRightId(R.id.radioButton2);
        this.vRadioButton3.setNextFocusLeftId(R.id.radioButton3);
        this.vRadioButton3.setNextFocusRightId(R.id.radioButton3);
        this.vRadioButton4.setNextFocusLeftId(R.id.radioButton4);
        this.vRadioButton4.setNextFocusRightId(R.id.radioButton4);
        this.vRadioButton5.setNextFocusLeftId(R.id.radioButton5);
        this.vRadioButton5.setNextFocusRightId(R.id.radioButton5);
        this.vRadioButton6.setNextFocusLeftId(R.id.radioButton6);
        this.vRadioButton6.setNextFocusRightId(R.id.radioButton6);
        initRatingSetting(itemID);
    }

    /* access modifiers changed from: private */
    public void initRatingSetting(String itemID) {
        int ratingBlock;
        if (itemID.equals(MenuConfigManager.PARENTAL_US_MOVIE_RATINGS)) {
            ratingBlock = this.mTV.getATSCRating().getUSMovieRatingSettingInfo();
            for (int i = 0; i < this.mGroup.size(); i++) {
                if (i >= ratingBlock) {
                    this.mGroup.get(i).setChecked(true);
                } else {
                    this.mGroup.get(i).setChecked(false);
                }
            }
        } else if (itemID.equals(MenuConfigManager.PARENTAL_CANADIAN_ENGLISH_RATINGS)) {
            ratingBlock = this.mTV.getATSCRating().getCANEngRatingSettingInfo();
            for (int i2 = 0; i2 < this.mGroup.size(); i2++) {
                if (i2 < ratingBlock - 1 || ratingBlock == 0) {
                    this.mGroup.get(i2).setChecked(false);
                } else {
                    this.mGroup.get(i2).setChecked(true);
                }
            }
        } else {
            ratingBlock = this.mTV.getATSCRating().getCANFreRatingSettingInfo();
            for (int i3 = 0; i3 < this.mGroup.size(); i3++) {
                if (i3 < ratingBlock - 1 || ratingBlock == 0) {
                    this.mGroup.get(i3).setChecked(false);
                } else {
                    this.mGroup.get(i3).setChecked(true);
                }
            }
        }
        MtkLog.d("Rating", "ratingBlock:" + ratingBlock);
    }

    private void initRatingSettingPlus(String itemID) {
        MtkLog.d("RatingOtherView", "initRatingSettingPlus:" + itemID);
        List<TvContentRating> currRatings = this.mTvInputManager.getBlockedRatings();
        if (itemID.equals(MenuConfigManager.PARENTAL_US_MOVIE_RATINGS)) {
            List<TvContentRating> mvRatings = new ArrayList<>();
            for (TvContentRating rating : currRatings) {
                if (rating.getRatingSystem().equals("US_MV")) {
                    mvRatings.add(rating);
                }
            }
            for (TvContentRating rating2 : mvRatings) {
                if (rating2.getMainRating().equals("US_MV_G")) {
                    this.vRadioButton1.setChecked(true);
                } else if (rating2.getMainRating().equals("US_MV_PG")) {
                    this.vRadioButton2.setChecked(true);
                } else if (rating2.getMainRating().equals("US_MV_PG13")) {
                    this.vRadioButton3.setChecked(true);
                } else if (rating2.getMainRating().equals("US_MV_R")) {
                    this.vRadioButton4.setChecked(true);
                } else if (rating2.getMainRating().equals("US_MV_NC17")) {
                    this.vRadioButton5.setChecked(true);
                } else if (rating2.getMainRating().equals("US_MV_X")) {
                    this.vRadioButton6.setChecked(true);
                }
            }
        } else if (itemID.equals(MenuConfigManager.PARENTAL_CANADIAN_ENGLISH_RATINGS)) {
            List<TvContentRating> mvRatings2 = new ArrayList<>();
            for (TvContentRating rating3 : currRatings) {
                if (rating3.getRatingSystem().equals("CA_EN_TV")) {
                    mvRatings2.add(rating3);
                }
            }
            for (TvContentRating rating4 : mvRatings2) {
                if (rating4.getMainRating().equals("CA_EN_TV_C")) {
                    this.vRadioButton1.setChecked(true);
                } else if (rating4.getMainRating().equals("CA_EN_TV_C8")) {
                    this.vRadioButton2.setChecked(true);
                } else if (rating4.getMainRating().equals("CA_EN_TV_G")) {
                    this.vRadioButton3.setChecked(true);
                } else if (rating4.getMainRating().equals("CA_EN_TV_PG")) {
                    this.vRadioButton4.setChecked(true);
                } else if (rating4.getMainRating().equals("CA_EN_TV_14")) {
                    this.vRadioButton5.setChecked(true);
                } else if (rating4.getMainRating().equals("CA_EN_TV_18")) {
                    this.vRadioButton6.setChecked(true);
                }
            }
        } else {
            List<TvContentRating> mvRatings3 = new ArrayList<>();
            for (TvContentRating rating5 : currRatings) {
                if (rating5.getRatingSystem().equals("CA_TV")) {
                    mvRatings3.add(rating5);
                }
            }
            for (TvContentRating rating6 : mvRatings3) {
                if (rating6.getMainRating().equals("CA_FR_TV_G")) {
                    this.vRadioButton1.setChecked(true);
                } else if (rating6.getMainRating().equals("CA_FR_TV_8")) {
                    this.vRadioButton2.setChecked(true);
                } else if (rating6.getMainRating().equals("CA_FR_TV_13")) {
                    this.vRadioButton3.setChecked(true);
                } else if (rating6.getMainRating().equals("CA_FR_TV_16")) {
                    this.vRadioButton4.setChecked(true);
                } else if (rating6.getMainRating().equals("CA_FR_TV_18")) {
                    this.vRadioButton5.setChecked(true);
                }
            }
        }
    }

    private void generateRating(String itemID) {
        MtkLog.d("RatingOtherView", "generateRating:" + itemID);
        List<TvContentRating> currRatings = this.mTvInputManager.getBlockedRatings();
        if (itemID.equals(MenuConfigManager.PARENTAL_US_MOVIE_RATINGS)) {
            for (TvContentRating rating : currRatings) {
                if (rating.getRatingSystem().equals("US_MV")) {
                    this.mTvInputManager.removeBlockedRating(rating);
                }
            }
            if (this.vRadioButton1.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "US_MV", "US_MV_G", new String[0]));
            } else if (this.vRadioButton2.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "US_MV", "US_MV_PG", new String[0]));
            } else if (this.vRadioButton3.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "US_MV", "US_MV_PG13", new String[0]));
            } else if (this.vRadioButton4.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "US_MV", "US_MV_R", new String[0]));
            } else if (this.vRadioButton5.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "US_MV", "US_MV_NC17", new String[0]));
            } else if (this.vRadioButton6.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "US_MV", "US_MV_X", new String[0]));
            }
            for (TvContentRating rating2 : currRatings) {
                if (rating2.getRatingSystem().equals("US_MV")) {
                    MtkLog.d("RatingOtherView", "generateRating item:" + rating2.getMainRating());
                }
            }
        } else if (itemID.equals(MenuConfigManager.PARENTAL_CANADIAN_ENGLISH_RATINGS)) {
            for (TvContentRating rating3 : currRatings) {
                if (rating3.getRatingSystem().equals("CA_EN_TV")) {
                    this.mTvInputManager.removeBlockedRating(rating3);
                }
            }
            if (this.vRadioButton1.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "CA_EN_TV", "CA_EN_TV_C", new String[0]));
            } else if (this.vRadioButton2.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "CA_EN_TV", "CA_EN_TV_C8", new String[0]));
            } else if (this.vRadioButton3.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "CA_EN_TV", "CA_EN_TV_G", new String[0]));
            } else if (this.vRadioButton4.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "CA_EN_TV", "CA_EN_TV_PG", new String[0]));
            } else if (this.vRadioButton5.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "CA_EN_TV", "CA_EN_TV_14", new String[0]));
            } else if (this.vRadioButton6.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "CA_EN_TV", "CA_EN_TV_18", new String[0]));
            }
            for (TvContentRating rating4 : currRatings) {
                if (rating4.getRatingSystem().equals("CA_EN_TV")) {
                    MtkLog.d("RatingOtherView", "generateRating item:" + rating4.getMainRating());
                }
            }
        } else {
            for (TvContentRating rating5 : currRatings) {
                if (rating5.getRatingSystem().equals("CA_TV")) {
                    this.mTvInputManager.removeBlockedRating(rating5);
                }
            }
            if (this.vRadioButton1.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "CA_TV", "CA_FR_TV_G", new String[0]));
            } else if (this.vRadioButton2.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "CA_TV", "CA_FR_TV_8", new String[0]));
            } else if (this.vRadioButton3.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "CA_TV", "CA_FR_TV_13", new String[0]));
            } else if (this.vRadioButton4.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "CA_TV", "CA_FR_TV_16", new String[0]));
            } else if (this.vRadioButton5.isChecked()) {
                this.mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "CA_TV", "CA_FR_TV_18", new String[0]));
            }
            for (TvContentRating rating6 : currRatings) {
                if (rating6.getRatingSystem().equals("CA_TV")) {
                    MtkLog.d("RatingOtherView", "generateRating item:" + rating6.getMainRating());
                }
            }
        }
    }
}
