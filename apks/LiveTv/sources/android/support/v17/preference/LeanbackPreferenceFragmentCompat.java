package android.support.v17.preference;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class LeanbackPreferenceFragmentCompat extends BaseLeanbackPreferenceFragmentCompat {
    public LeanbackPreferenceFragmentCompat() {
        if (Build.VERSION.SDK_INT >= 21) {
            LeanbackPreferenceFragmentTransitionHelperApi21.addTransitions((Fragment) this);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View innerView = super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(innerView.getContext()).inflate(R.layout.leanback_preference_fragment, container, false);
        ViewGroup innerContainer = (ViewGroup) view.findViewById(R.id.main_frame);
        if (innerView != null) {
            innerContainer.addView(innerView);
        }
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(getPreferenceScreen().getTitle());
    }

    public void setTitle(CharSequence title) {
        TextView decorTitle;
        View view = getView();
        if (view == null) {
            decorTitle = null;
        } else {
            decorTitle = (TextView) view.findViewById(R.id.decor_title);
        }
        if (decorTitle != null) {
            decorTitle.setText(title);
        }
    }
}
