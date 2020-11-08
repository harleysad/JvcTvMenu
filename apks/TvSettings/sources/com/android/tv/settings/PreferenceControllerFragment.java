package com.android.tv.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.XmlRes;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.util.ArraySet;
import android.util.Log;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PreferenceControllerFragment extends SettingsPreferenceFragment {
    private static final String TAG = "PrefControllerFrag";
    private final Set<AbstractPreferenceController> mPreferenceControllers = new ArraySet();

    /* access modifiers changed from: protected */
    @XmlRes
    public abstract int getPreferenceScreenResId();

    /* access modifiers changed from: protected */
    public abstract List<AbstractPreferenceController> onCreatePreferenceControllers(Context context);

    public void onAttach(Context context) {
        super.onAttach(context);
        List<AbstractPreferenceController> controllers = onCreatePreferenceControllers(context);
        if (controllers == null) {
            controllers = new ArrayList<>();
        }
        this.mPreferenceControllers.addAll(controllers);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setPreferenceComparisonCallback(new PreferenceManager.SimplePreferenceComparisonCallback());
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(getPreferenceScreenResId(), (String) null);
        refreshAllPreferences();
    }

    public void onResume() {
        super.onResume();
        updatePreferenceStates();
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        for (AbstractPreferenceController controller : new ArrayList<>(this.mPreferenceControllers)) {
            if (controller.handlePreferenceTreeClick(preference)) {
                return true;
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    /* access modifiers changed from: protected */
    public <T extends AbstractPreferenceController> T getOnePreferenceController(Class<T> clazz) {
        Stream stream = this.mPreferenceControllers.stream();
        Objects.requireNonNull(clazz);
        List<AbstractPreferenceController> foundControllers = (List) stream.filter(new Predicate(clazz) {
            private final /* synthetic */ Class f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return this.f$0.isInstance((AbstractPreferenceController) obj);
            }
        }).collect(Collectors.toList());
        if (foundControllers.size() > 0) {
            return foundControllers.get(0);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public <T extends AbstractPreferenceController> Collection<T> getPreferenceControllers(Class<T> clazz) {
        Stream stream = this.mPreferenceControllers.stream();
        Objects.requireNonNull(clazz);
        return (Collection) stream.filter(new Predicate(clazz) {
            private final /* synthetic */ Class f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return this.f$0.isInstance((AbstractPreferenceController) obj);
            }
        }).collect(Collectors.toList());
    }

    /* access modifiers changed from: protected */
    public void addPreferenceController(AbstractPreferenceController controller) {
        this.mPreferenceControllers.add(controller);
    }

    /* access modifiers changed from: protected */
    public void updatePreferenceStates() {
        Collection<AbstractPreferenceController> controllers = new ArrayList<>(this.mPreferenceControllers);
        PreferenceScreen screen = getPreferenceScreen();
        for (AbstractPreferenceController controller : controllers) {
            if (controller.isAvailable()) {
                String key = controller.getPreferenceKey();
                Preference preference = screen.findPreference(key);
                if (preference == null) {
                    Log.d(TAG, "Cannot find preference with key " + key + " in Controller " + controller.getClass().getSimpleName());
                } else {
                    controller.updateState(preference);
                }
            }
        }
    }

    private void refreshAllPreferences() {
        PreferenceScreen screen = getPreferenceScreen();
        for (AbstractPreferenceController controller : new ArrayList<>(this.mPreferenceControllers)) {
            controller.displayPreference(screen);
        }
    }
}
