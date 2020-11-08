package com.mediatek.wwtv.tvcenter.nav.util;

import android.text.Layout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class DetailTextReader {
    private static String TAG = "DetailTextReader";
    private static DetailTextReader tr = null;
    private int PAGE_LINE = 4;
    private int curPage = 1;
    private TextReaderPageChangeListener pageChangeListener;
    private TextView tv;

    public interface TextReaderPageChangeListener {
        void onPageChanged(int i);
    }

    private DetailTextReader() {
    }

    public void registerPageChangeListener(TextReaderPageChangeListener textReaderPageChangeListener) {
        this.pageChangeListener = textReaderPageChangeListener;
    }

    public static DetailTextReader getInstance() {
        if (tr == null) {
            MtkLog.i(TAG, "First Create the TextReader Object");
            tr = new DetailTextReader();
        }
        MtkLog.i(TAG, "Get the existed TextReader Object");
        return tr;
    }

    public void autoScroll(int arg0) {
    }

    public void exitSearch() {
    }

    public int getCurPagenum() {
        if (this.tv == null) {
            MtkLog.i(TAG, "No Textview specified, Need to set a Textview");
            return 0;
        } else if (this.tv.getHeight() != 0) {
            return this.curPage;
        } else {
            MtkLog.i(TAG, "Need to refresh the textview for getting the correct pagenum");
            return 0;
        }
    }

    public void resetCurPagenum() {
        this.curPage = 1;
    }

    public int getCurPos() {
        if (this.tv == null) {
            MtkLog.i(TAG, "No Textview specified, Need to set a Textview");
            return 0;
        }
        Layout l = this.tv.getLayout();
        int off = l.getOffsetForHorizontal(l.getLineForVertical(this.tv.getScrollY() + this.tv.getTotalPaddingTop()), (float) (this.tv.getScrollX() + this.tv.getTotalPaddingLeft()));
        String str = TAG;
        MtkLog.i(str, "Get the current Position:" + off);
        return off;
    }

    public String getPreviewBuf(String arg0) {
        return null;
    }

    public int getTotalPage() {
        if (this.tv == null) {
            MtkLog.i(TAG, "No Textview specified, Need to set a Textview");
            return 0;
        } else if (this.tv.getLineCount() == 0) {
            MtkLog.i(TAG, "Need to refresh the textview for getting the correct pagenum");
            return 0;
        } else if (this.tv.getLineCount() % this.PAGE_LINE != 0) {
            return (this.tv.getLineCount() / this.PAGE_LINE) + 1;
        } else {
            return this.tv.getLineCount() / this.PAGE_LINE;
        }
    }

    public void loadPos(int pos) {
        if (this.tv == null) {
            MtkLog.i(TAG, "No Textview specified, Need to set a Textview");
            return;
        }
        Layout l = this.tv.getLayout();
        String str = TAG;
        MtkLog.i(str, "Load the position:" + pos);
        this.tv.scrollTo(0, (int) ((float) l.getLineBottom(l.getLineForOffset(pos))));
    }

    public void pageDown() {
        if (this.tv == null) {
            MtkLog.i(TAG, "No Textview specified, Need to set a Textview");
            return;
        }
        if (this.curPage >= getTotalPage()) {
            MtkLog.i(TAG, "Reach the first page");
            this.tv.scrollTo(0, (this.curPage - 1) * this.PAGE_LINE * this.tv.getLineHeight());
        } else {
            MtkLog.i(TAG, "Page up");
            this.curPage++;
            this.tv.scrollTo(0, (this.curPage - 1) * this.PAGE_LINE * this.tv.getLineHeight());
            MtkLog.d("detailtext", "come in pageDown tv.getLineHeight() == " + this.tv.getLineHeight());
            MtkLog.d("detailtext", "come in pageDown total == " + ((this.curPage + -1) * this.PAGE_LINE * this.tv.getLineHeight()));
        }
        this.pageChangeListener.onPageChanged(getCurPagenum());
    }

    public void pageUp() {
        if (this.tv == null) {
            MtkLog.i(TAG, "No Textview specified, Need to set a Textview");
            return;
        }
        if (this.curPage <= 1) {
            MtkLog.i(TAG, "Reach the first page");
            this.tv.scrollTo(0, (this.curPage - 1) * this.PAGE_LINE * this.tv.getLineHeight());
        } else {
            MtkLog.i(TAG, "Page up");
            this.curPage--;
            this.tv.scrollTo(0, (this.curPage - 1) * this.PAGE_LINE * this.tv.getLineHeight());
        }
        this.pageChangeListener.onPageChanged(getCurPagenum());
    }

    public int playFirst() {
        return 0;
    }

    public int playNext() {
        return 0;
    }

    public int playPrev() {
        return 0;
    }

    public void scrollLnDown() {
    }

    public void scrollLnUp() {
    }

    public void searchNext() {
    }

    public void searchPrev() {
    }

    public void searchText(String arg0, String arg1) {
    }

    public void setBackgroundColor(String arg0) {
    }

    public void setFontColor(String arg0) {
    }

    public void setFontSize(float arg0) {
    }

    public void setFontStyle(String arg0, String arg1) {
    }

    public void setPlayMode(int arg0) {
    }

    public void setScreenHeight(int arg0) {
    }

    public void setScrollView(ScrollView arg0) {
    }

    public void setTextView(TextView textView) {
        this.tv = textView;
        this.tv.setLines(this.PAGE_LINE);
        MtkLog.d("detailtext", "come in DetailTextReader setTextView");
        this.pageChangeListener.onPageChanged(getCurPagenum());
        this.tv.scrollTo(0, (this.curPage - 1) * this.PAGE_LINE * this.tv.getLineHeight());
    }

    public void skipToPage(int arg0) {
    }

    public boolean hasUpPage() {
        if (getCurPagenum() - 1 > 0) {
            return true;
        }
        return false;
    }

    public boolean hasNextPage() {
        if (getTotalPage() - getCurPagenum() >= 1) {
            return true;
        }
        return false;
    }
}
