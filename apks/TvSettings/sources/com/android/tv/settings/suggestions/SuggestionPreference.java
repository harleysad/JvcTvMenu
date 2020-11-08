package com.android.tv.settings.suggestions;

import android.app.PendingIntent;
import android.content.Context;
import android.service.settings.suggestions.Suggestion;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.suggestions.SuggestionControllerMixin;
import com.android.tv.settings.R;

public class SuggestionPreference extends Preference {
    public static final String SUGGESTION_PREFERENCE_KEY = "suggestion_pref_key";
    private static final String TAG = "SuggestionPreference";
    private Callback mCallback;
    private String mId;
    private final MetricsFeatureProvider mMetricsFeatureProvider = new MetricsFeatureProvider();
    private final Suggestion mSuggestion;
    private final SuggestionControllerMixin mSuggestionControllerMixin;

    public interface Callback {
        void onSuggestionClosed(Preference preference);
    }

    public SuggestionPreference(Suggestion suggestion, Context context, SuggestionControllerMixin suggestionControllerMixin, Callback callback) {
        super(context);
        setLayoutResource(R.layout.suggestion_item);
        this.mSuggestionControllerMixin = suggestionControllerMixin;
        this.mSuggestion = suggestion;
        this.mId = suggestion.getId();
        this.mCallback = callback;
        setKey(SUGGESTION_PREFERENCE_KEY + this.mId);
    }

    public String getId() {
        return this.mId;
    }

    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SuggestionPreference.this.launchSuggestion();
            }
        });
        View containerView = holder.itemView.findViewById(R.id.main_container);
        if (containerView != null) {
            containerView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    SuggestionPreference.this.launchSuggestion();
                }
            });
        }
        View itemContainerView = holder.itemView.findViewById(R.id.item_container);
        if (itemContainerView != null) {
            itemContainerView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    SuggestionPreference.this.launchSuggestion();
                }
            });
        }
        View dismissButton = holder.itemView.findViewById(R.id.dismiss_button);
        if (dismissButton != null) {
            dismissButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    SuggestionPreference.lambda$onBindViewHolder$3(SuggestionPreference.this, view);
                }
            });
        }
        this.mMetricsFeatureProvider.action(getContext(), 384, this.mId, new Pair[0]);
    }

    public static /* synthetic */ void lambda$onBindViewHolder$3(SuggestionPreference suggestionPreference, View v) {
        suggestionPreference.mSuggestionControllerMixin.dismissSuggestion(suggestionPreference.mSuggestion);
        if (suggestionPreference.mCallback != null) {
            suggestionPreference.mCallback.onSuggestionClosed(suggestionPreference);
        }
    }

    /* access modifiers changed from: private */
    public void launchSuggestion() {
        try {
            this.mSuggestion.getPendingIntent().send();
            this.mSuggestionControllerMixin.launchSuggestion(this.mSuggestion);
            this.mMetricsFeatureProvider.action(getContext(), 386, this.mId, new Pair[0]);
        } catch (PendingIntent.CanceledException e) {
            Log.w(TAG, "Failed to start suggestion " + this.mSuggestion.getTitle());
        }
    }
}
