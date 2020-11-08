package com.android.tv.settings.accessories;

import android.os.Bundle;
import android.view.View;
import com.android.tv.settings.R;
import com.android.tv.settings.dialog.ProgressDialogFragment;

public class AddAccessoryContentFragment extends ProgressDialogFragment {
    public static AddAccessoryContentFragment newInstance() {
        return new AddAccessoryContentFragment();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle((int) R.string.accessories_add_title);
        setIcon((int) R.drawable.ic_bluetooth_searching_128dp);
        setSummary((int) R.string.accessories_add_bluetooth_inst);
        setOperationalRemindText((int) R.string.accessories_operational_remind_msg);
    }
}
