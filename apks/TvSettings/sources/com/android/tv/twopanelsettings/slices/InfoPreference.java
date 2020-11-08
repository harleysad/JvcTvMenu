package com.android.tv.twopanelsettings.slices;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.tv.twopanelsettings.R;
import java.util.List;

public class InfoPreference extends Preference {
    private List<Pair<CharSequence, CharSequence>> mInfoList;

    public InfoPreference(Context context, List<Pair<CharSequence, CharSequence>> infoList) {
        super(context);
        this.mInfoList = infoList;
        setLayoutResource(R.layout.info_preference);
        setEnabled(false);
    }

    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        ViewGroup container = (ViewGroup) holder.findViewById(R.id.item_container);
        container.removeAllViews();
        for (Pair<CharSequence, CharSequence> info : this.mInfoList) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.info_preference_item, container, false);
            ((TextView) view.findViewById(R.id.info_item_title)).setText((CharSequence) info.first);
            ((TextView) view.findViewById(R.id.info_item_summary)).setText((CharSequence) info.second);
            container.addView(view);
        }
    }
}
