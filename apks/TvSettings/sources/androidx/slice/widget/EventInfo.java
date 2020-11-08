package androidx.slice.widget;

import android.support.annotation.RestrictTo;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class EventInfo {
    public static final int ACTION_TYPE_BUTTON = 1;
    public static final int ACTION_TYPE_CONTENT = 3;
    public static final int ACTION_TYPE_SEE_MORE = 4;
    public static final int ACTION_TYPE_SLIDER = 2;
    public static final int ACTION_TYPE_TOGGLE = 0;
    public static final int POSITION_CELL = 2;
    public static final int POSITION_END = 1;
    public static final int POSITION_START = 0;
    public static final int ROW_TYPE_GRID = 1;
    public static final int ROW_TYPE_LIST = 0;
    public static final int ROW_TYPE_MESSAGING = 2;
    public static final int ROW_TYPE_PROGRESS = 5;
    public static final int ROW_TYPE_SHORTCUT = -1;
    public static final int ROW_TYPE_SLIDER = 4;
    public static final int ROW_TYPE_TOGGLE = 3;
    public static final int STATE_OFF = 0;
    public static final int STATE_ON = 1;
    public int actionCount = -1;
    public int actionIndex = -1;
    public int actionPosition = -1;
    public int actionType;
    public int rowIndex;
    public int rowTemplateType;
    public int sliceMode;
    public int state = -1;

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SliceActionType {
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SliceButtonPosition {
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SliceRowType {
    }

    public EventInfo(int sliceMode2, int actionType2, int rowTemplateType2, int rowIndex2) {
        this.sliceMode = sliceMode2;
        this.actionType = actionType2;
        this.rowTemplateType = rowTemplateType2;
        this.rowIndex = rowIndex2;
    }

    public void setPosition(int actionPosition2, int actionIndex2, int actionCount2) {
        this.actionPosition = actionPosition2;
        this.actionIndex = actionIndex2;
        this.actionCount = actionCount2;
    }

    public String toString() {
        return "mode=" + SliceView.modeToString(this.sliceMode) + ", actionType=" + actionToString(this.actionType) + ", rowTemplateType=" + rowTypeToString(this.rowTemplateType) + ", rowIndex=" + this.rowIndex + ", actionPosition=" + positionToString(this.actionPosition) + ", actionIndex=" + this.actionIndex + ", actionCount=" + this.actionCount + ", state=" + this.state;
    }

    private static String positionToString(int position) {
        switch (position) {
            case 0:
                return "START";
            case 1:
                return "END";
            case 2:
                return "CELL";
            default:
                return "unknown position: " + position;
        }
    }

    private static String actionToString(int action) {
        switch (action) {
            case 0:
                return "TOGGLE";
            case 1:
                return "BUTTON";
            case 2:
                return "SLIDER";
            case 3:
                return "CONTENT";
            case 4:
                return "SEE MORE";
            default:
                return "unknown action: " + action;
        }
    }

    private static String rowTypeToString(int type) {
        switch (type) {
            case -1:
                return "SHORTCUT";
            case 0:
                return "LIST";
            case 1:
                return "GRID";
            case 2:
                return "MESSAGING";
            case 3:
                return "TOGGLE";
            case 4:
                return "SLIDER";
            case 5:
                return "PROGRESS";
            default:
                return "unknown row type: " + type;
        }
    }
}
