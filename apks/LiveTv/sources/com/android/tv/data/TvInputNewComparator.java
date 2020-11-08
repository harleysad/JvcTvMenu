package com.android.tv.data;

import android.media.tv.TvInputInfo;
import com.android.tv.util.SetupUtils;
import com.android.tv.util.TvInputManagerHelper;
import java.util.Comparator;

public class TvInputNewComparator implements Comparator<TvInputInfo> {
    private final TvInputManagerHelper mInputManager;
    private final SetupUtils mSetupUtils;

    public TvInputNewComparator(SetupUtils setupUtils, TvInputManagerHelper inputManager) {
        this.mSetupUtils = setupUtils;
        this.mInputManager = inputManager;
    }

    public int compare(TvInputInfo lhs, TvInputInfo rhs) {
        boolean lhsSetupDone;
        boolean lhsIsNewInput = this.mSetupUtils.isNewInput(lhs.getId());
        if (lhsIsNewInput != this.mSetupUtils.isNewInput(rhs.getId())) {
            if (lhsIsNewInput) {
                return -1;
            }
            return 1;
        } else if (lhsIsNewInput || (lhsSetupDone = this.mSetupUtils.isSetupDone(lhs.getId())) == this.mSetupUtils.isSetupDone(rhs.getId())) {
            return this.mInputManager.getDefaultTvInputInfoComparator().compare(lhs, rhs);
        } else {
            if (lhsSetupDone) {
                return 1;
            }
            return -1;
        }
    }
}
