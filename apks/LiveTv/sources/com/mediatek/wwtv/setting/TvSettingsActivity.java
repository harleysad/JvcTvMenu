package com.mediatek.wwtv.setting;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import com.android.tv.dialog.PinDialogFragment;
import com.android.tv.ui.sidepanel.SideFragmentManager;
import com.android.tv.ui.sidepanel.parentalcontrols.ParentalControlsFragment;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.BaseActivity;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public abstract class TvSettingsActivity extends BaseActivity implements PinDialogFragment.OnPinCheckedListener {
    private static final String TAG = "TvSettingsActivity";
    protected String mActionId;
    private final int mContentResId = R.id.main_fragment_container;
    private Fragment mFragment = null;
    private SideFragmentManager mSideFragmentManager;

    /* access modifiers changed from: protected */
    public abstract Fragment createSettingsFragment();

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSideFragmentManager = new SideFragmentManager(this, (Runnable) null, (Runnable) null);
        if (savedInstanceState == null) {
            createFragment();
        }
    }

    public void finish() {
        Fragment fragment = getFragmentManager().findFragmentByTag(TAG);
        if (!isResumed() || fragment == null) {
            super.finish();
        } else {
            hide(1);
        }
    }

    public void hide() {
        if (this.mSideFragmentManager.isActive()) {
            this.mSideFragmentManager.setVisibility(8);
            return;
        }
        this.mFragment = getFragmentManager().findFragmentByTag(TAG);
        hide(0);
    }

    private void hide(final int type) {
        final Fragment fragment = getFragmentManager().findFragmentByTag(TAG);
        if (isResumed() && fragment != null) {
            ViewGroup root = (ViewGroup) findViewById(R.id.main_fragment_container);
            Scene scene = new Scene(root);
            scene.setEnterAction(new Runnable() {
                public void run() {
                    TvSettingsActivity.this.getFragmentManager().beginTransaction().remove(fragment).commitNow();
                }
            });
            Slide slide = new Slide(GravityCompat.END);
            slide.setSlideFraction(getResources().getDimension(R.dimen.lb_settings_pane_width) / ((float) root.getWidth()));
            slide.addListener(new Transition.TransitionListener() {
                public void onTransitionStart(Transition transition) {
                    TvSettingsActivity.this.getWindow().setDimAmount(0.0f);
                }

                public void onTransitionEnd(Transition transition) {
                    transition.removeListener(this);
                    if (type == 1) {
                        TvSettingsActivity.super.finish();
                    }
                }

                public void onTransitionCancel(Transition transition) {
                }

                public void onTransitionPause(Transition transition) {
                }

                public void onTransitionResume(Transition transition) {
                }
            });
            TransitionManager.go(scene, slide);
        }
    }

    public void show() {
        show(this.mFragment);
        this.mFragment = null;
    }

    public void show(final Fragment fragment) {
        ViewGroup root = (ViewGroup) findViewById(R.id.main_fragment_container);
        if (!isResumed() || fragment == null) {
            createFragment();
        } else {
            Scene scene = new Scene(root);
            scene.setEnterAction(new Runnable() {
                public void run() {
                    TvSettingsActivity.this.getFragmentManager().beginTransaction().remove(fragment).commitNow();
                }
            });
            Slide slide = new Slide(GravityCompat.END);
            slide.setSlideFraction(getResources().getDimension(R.dimen.lb_settings_pane_width) / ((float) root.getWidth()));
            slide.addListener(new Transition.TransitionListener() {
                public void onTransitionStart(Transition transition) {
                    TvSettingsActivity.this.getWindow().setDimAmount(0.0f);
                }

                public void onTransitionEnd(Transition transition) {
                    transition.removeListener(this);
                    TvSettingsActivity.this.createFragment();
                }

                public void onTransitionCancel(Transition transition) {
                }

                public void onTransitionPause(Transition transition) {
                }

                public void onTransitionResume(Transition transition) {
                }
            });
            TransitionManager.go(scene, slide);
        }
        root.invalidate();
    }

    /* access modifiers changed from: private */
    public void createFragment() {
        final ViewGroup root = (ViewGroup) findViewById(R.id.main_fragment_container);
        root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                root.getViewTreeObserver().removeOnPreDrawListener(this);
                Scene scene = new Scene(root);
                scene.setEnterAction(new Runnable() {
                    public void run() {
                        Fragment fragment = TvSettingsActivity.this.createSettingsFragment();
                        if (fragment != null) {
                            TvSettingsActivity.this.getFragmentManager().beginTransaction().add(R.id.main_fragment_container, fragment, TvSettingsActivity.TAG).commitNow();
                        }
                    }
                });
                Slide slide = new Slide(GravityCompat.END);
                slide.setSlideFraction(TvSettingsActivity.this.getResources().getDimension(R.dimen.lb_settings_pane_width) / ((float) root.getWidth()));
                TransitionManager.go(scene, slide);
                return false;
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        this.mFragment = null;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        MtkLog.d(TAG, "now onDestroy");
        super.onDestroy();
    }

    public void updateFragment(String packageName, String activityName) {
        if (isPackageExist(packageName)) {
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, activityName));
                startActivity(intent);
                finish();
            } catch (ActivityNotFoundException e) {
                MtkLog.e(TAG, "ActivityNotFoundException = " + e.toString());
                show(getFragmentManager().findFragmentByTag(TAG));
            } catch (Exception e2) {
                e2.printStackTrace();
                MtkLog.e(TAG, "Exception = " + e2.toString());
                show(getFragmentManager().findFragmentByTag(TAG));
            }
        } else {
            show(getFragmentManager().findFragmentByTag(TAG));
        }
    }

    private boolean isPackageExist(String packageName) {
        try {
            ApplicationInfo app = getPackageManager().getApplicationInfo(packageName, 8192);
            MtkLog.d(TAG, "packageInfo = " + app.toString());
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            MtkLog.d(TAG, e.toString());
            return false;
        } catch (Exception e2) {
            return false;
        }
    }

    public void onPinChecked(boolean checked, int type, String rating) {
        if (type == 2) {
            if (checked) {
                this.mSideFragmentManager.show(new ParentalControlsFragment(), true);
            } else {
                show();
            }
        } else if (type == 3) {
            this.mSideFragmentManager.setVisibility(0);
        } else if (type != 7) {
        } else {
            if (checked) {
                show();
                return;
            }
            if (!MenuConfigManager.CHANNEL_CHANNEL_SOURCES.equals(this.mActionId)) {
                show();
            }
            setActionId((String) null);
        }
    }

    public SideFragmentManager getSideFragmentManager() {
        return this.mSideFragmentManager;
    }

    public void setActionId(String actionId) {
        this.mActionId = actionId;
    }
}
