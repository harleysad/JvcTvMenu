package com.android.tv.settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.instrumentation.SharedPreferencesLogger;
import com.android.tv.settings.TvSettingsActivity;

public abstract class TvSettingsActivity extends Activity {
    private static final String SETTINGS_FRAGMENT_TAG = "com.android.tv.settings.MainSettings.SETTINGS_FRAGMENT";
    private static final String TAG = "TvSettingsActivity";

    /* access modifiers changed from: protected */
    public abstract Fragment createSettingsFragment();

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            final ViewGroup root = (ViewGroup) findViewById(16908290);
            root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    root.getViewTreeObserver().removeOnPreDrawListener(this);
                    Scene scene = new Scene(root);
                    scene.setEnterAction(new Runnable() {
                        public final void run() {
                            TvSettingsActivity.AnonymousClass1.lambda$onPreDraw$0(TvSettingsActivity.AnonymousClass1.this);
                        }
                    });
                    Slide slide = new Slide(GravityCompat.END);
                    slide.setSlideFraction(TvSettingsActivity.this.getResources().getDimension(R.dimen.lb_settings_pane_width) / ((float) root.getWidth()));
                    TransitionManager.go(scene, slide);
                    return false;
                }

                public static /* synthetic */ void lambda$onPreDraw$0(AnonymousClass1 r4) {
                    if (TvSettingsActivity.this.getFragmentManager().isStateSaved() || TvSettingsActivity.this.getFragmentManager().isDestroyed()) {
                        Log.d(TvSettingsActivity.TAG, "Got torn down before adding fragment");
                        return;
                    }
                    Fragment fragment = TvSettingsActivity.this.createSettingsFragment();
                    if (fragment != null) {
                        TvSettingsActivity.this.getFragmentManager().beginTransaction().add(16908290, fragment, TvSettingsActivity.SETTINGS_FRAGMENT_TAG).commitNow();
                    }
                }
            });
        }
    }

    public void finish() {
        Fragment fragment = getFragmentManager().findFragmentByTag(SETTINGS_FRAGMENT_TAG);
        if (!isResumed() || fragment == null) {
            super.finish();
            return;
        }
        ViewGroup root = (ViewGroup) findViewById(16908290);
        Scene scene = new Scene(root);
        scene.setEnterAction(new Runnable(fragment) {
            private final /* synthetic */ Fragment f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TvSettingsActivity.this.getFragmentManager().beginTransaction().remove(this.f$1).commitNow();
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
                TvSettingsActivity.super.finish();
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

    private String getMetricsTag() {
        String tag = getClass().getName();
        if (tag.startsWith("com.android.tv.settings.")) {
            return tag.replace("com.android.tv.settings.", "");
        }
        return tag;
    }

    public SharedPreferences getSharedPreferences(String name, int mode) {
        if (name.equals(getPackageName() + "_preferences")) {
            return new SharedPreferencesLogger(this, getMetricsTag(), new MetricsFeatureProvider());
        }
        return super.getSharedPreferences(name, mode);
    }
}
