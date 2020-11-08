package com.android.tv.settings.system;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import com.android.tv.settings.R;
import java.util.List;

@Keep
public class InputCustomNameFragment extends GuidedStepFragment {
    private static final String ARG_CURRENT_NAME = "current_name";
    private static final String ARG_DEFAULT_NAME = "default_name";
    private CharSequence mName;

    public interface Callback {
        void onSetCustomName(CharSequence charSequence);
    }

    public static void prepareArgs(@NonNull Bundle args, CharSequence defaultName, CharSequence currentName) {
        args.putCharSequence(ARG_DEFAULT_NAME, defaultName);
        args.putCharSequence(ARG_CURRENT_NAME, currentName);
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mName = getArguments().getCharSequence(ARG_CURRENT_NAME);
        super.onCreate(savedInstanceState);
    }

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.inputs_custom_name), getString(R.string.inputs_custom_name_description_fmt, new Object[]{getArguments().getCharSequence(ARG_DEFAULT_NAME)}), (String) null, getContext().getDrawable(R.drawable.ic_input_132dp));
    }

    public void onCreateButtonActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-4)).build());
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-5)).build());
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).title(this.mName)).editable(true)).build());
    }

    public void onGuidedActionClicked(GuidedAction action) {
        if (action.getId() == -4) {
            ((Callback) getTargetFragment()).onSetCustomName(this.mName);
            getFragmentManager().popBackStack();
        } else if (action.getId() == -5) {
            getFragmentManager().popBackStack();
        }
    }

    public long onGuidedActionEditedAndProceed(GuidedAction action) {
        this.mName = action.getTitle();
        return -4;
    }
}
