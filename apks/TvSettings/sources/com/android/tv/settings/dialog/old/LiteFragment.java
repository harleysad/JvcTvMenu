package com.android.tv.settings.dialog.old;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

public interface LiteFragment {
    Activity getActivity();

    Bundle getArguments();

    Resources getResources();

    View getView();

    boolean isAdded();

    void startActivity(Intent intent);
}
