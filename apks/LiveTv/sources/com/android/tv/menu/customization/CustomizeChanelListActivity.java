package com.android.tv.menu.customization;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import com.mediatek.wwtv.tvcenter.R;

public class CustomizeChanelListActivity extends Activity {
    private static final int REQUEST_CODE_START_SETUP_ACTIVITY = 1;
    private static final String TAG = "CustomizeChanelListActivity";
    /* access modifiers changed from: private */
    public String SETTINGS_FRAGMENT_TAG = TAG;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customizechannellist);
        if (savedInstanceState == null) {
            final ViewGroup root = (ViewGroup) findViewById(R.id.container);
            root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    root.getViewTreeObserver().removeOnPreDrawListener(this);
                    Scene scene = new Scene(root);
                    scene.setEnterAction(new Runnable() {
                        public void run() {
                            CustomizeChanelListFragment ChanelListFragment = new CustomizeChanelListFragment();
                            if (CustomizeChanelListActivity.this.getFragmentManager().isStateSaved() || CustomizeChanelListActivity.this.getFragmentManager().isDestroyed()) {
                                Log.d(CustomizeChanelListActivity.TAG, "Got torn down before adding fragment");
                            } else {
                                CustomizeChanelListActivity.this.getFragmentManager().beginTransaction().add(R.id.container, ChanelListFragment, CustomizeChanelListActivity.this.SETTINGS_FRAGMENT_TAG).commitNow();
                            }
                        }
                    });
                    Slide slide = new Slide(GravityCompat.END);
                    slide.setSlideFraction(CustomizeChanelListActivity.this.getResources().getDimension(R.dimen.lb_settings_pane_width) / ((float) root.getWidth()));
                    TransitionManager.go(scene, slide);
                    return false;
                }
            });
        }
    }

    public void finish() {
        final Fragment fragment = getFragmentManager().findFragmentByTag(this.SETTINGS_FRAGMENT_TAG);
        if (!isResumed() || fragment == null) {
            super.finish();
            return;
        }
        ViewGroup root = (ViewGroup) findViewById(R.id.container);
        Scene scene = new Scene(root);
        scene.setEnterAction(new Runnable() {
            public void run() {
                CustomizeChanelListActivity.this.getFragmentManager().beginTransaction().remove(fragment).commitNow();
            }
        });
        Slide slide = new Slide(GravityCompat.END);
        slide.setSlideFraction(getResources().getDimension(R.dimen.lb_settings_pane_width) / ((float) root.getWidth()));
        slide.addListener(new Transition.TransitionListener() {
            public void onTransitionStart(Transition transition) {
                CustomizeChanelListActivity.this.getWindow().setDimAmount(0.0f);
            }

            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                CustomizeChanelListActivity.super.finish();
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

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }
}
