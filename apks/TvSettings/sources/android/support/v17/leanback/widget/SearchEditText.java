package android.support.v17.leanback.widget;

import android.content.Context;
import android.support.v17.leanback.R;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

public class SearchEditText extends StreamingTextView {
    private static final boolean DEBUG = false;
    private static final String TAG = SearchEditText.class.getSimpleName();
    private OnKeyboardDismissListener mKeyboardDismissListener;

    public interface OnKeyboardDismissListener {
        void onKeyboardDismiss();
    }

    public /* bridge */ /* synthetic */ void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
    }

    public /* bridge */ /* synthetic */ void reset() {
        super.reset();
    }

    public /* bridge */ /* synthetic */ void setCustomSelectionActionModeCallback(ActionMode.Callback callback) {
        super.setCustomSelectionActionModeCallback(callback);
    }

    public /* bridge */ /* synthetic */ void setFinalRecognizedText(CharSequence charSequence) {
        super.setFinalRecognizedText(charSequence);
    }

    public /* bridge */ /* synthetic */ void updateRecognizedText(String str, String str2) {
        super.updateRecognizedText(str, str2);
    }

    public /* bridge */ /* synthetic */ void updateRecognizedText(String str, List list) {
        super.updateRecognizedText(str, (List<Float>) list);
    }

    public SearchEditText(Context context) {
        this(context, (AttributeSet) null);
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.TextAppearance_Leanback_SearchTextEdit);
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() != 4) {
            return super.onKeyPreIme(keyCode, event);
        }
        if (this.mKeyboardDismissListener == null) {
            return false;
        }
        this.mKeyboardDismissListener.onKeyboardDismiss();
        return false;
    }

    public void setOnKeyboardDismissListener(OnKeyboardDismissListener listener) {
        this.mKeyboardDismissListener = listener;
    }
}
