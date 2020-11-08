package com.mediatek.wwtv.setting.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.InputDeviceCompat;
import android.util.AttributeSet;
import android.widget.TextView;
import com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter;
import com.mediatek.wwtv.setting.base.scan.model.UpdateTime;
import com.mediatek.wwtv.tvcenter.util.SaveValue;

public class DateTimeView extends TextView {
    public static final int DATETYPE = 0;
    public static final int TIMETYPE = 1;
    public boolean flag = true;
    private boolean flagDone = false;
    private final Context mContext;
    private int mCurrentSelectedPosition = -1;
    private String mDate;
    private char[] mDateChars;
    private float mHeight;
    private Paint mPaint;
    private int[] mSplitIndex;
    private int mTextSize = 18;
    public int mType;
    private float mWidth;
    private UpdateTime updateTime;
    private float x;
    private float y;

    public int getCurrentSelectedPosition() {
        return this.mCurrentSelectedPosition;
    }

    public void setCurrentSelectedPosition(int mCurrentSelectedPosition2) {
        this.mCurrentSelectedPosition = mCurrentSelectedPosition2;
    }

    public int getmTextSize() {
        return this.mTextSize;
    }

    public DateTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public DateTimeView(Context context) {
        super(context);
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setDateStr(String mDateStr, UpdateTime updateTime2) {
        this.mDate = mDateStr;
        this.mDateChars = this.mDate.toCharArray();
        setmSplitIndex(this.mDateChars);
        this.updateTime = updateTime2;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDate != null) {
            this.mPaint = new Paint();
            this.mWidth = (float) getWidth();
            this.mHeight = (float) getHeight();
            this.x = (this.mWidth - this.mPaint.measureText(this.mDate)) / 2.0f;
            this.y = (this.mHeight + (this.mPaint.measureText(this.mDateChars, 0, 1) * 1.2f)) / 2.0f;
            this.mPaint.setTextSize((float) this.mTextSize);
            this.mPaint.setFlags(1);
            this.mPaint.setAntiAlias(true);
            this.mPaint.setColor(-1);
            this.mPaint.setAlpha(220);
            this.mPaint.setTextSize((float) this.mTextSize);
            this.mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(this.mDate, this.x, this.y, this.mPaint);
            if (this.mCurrentSelectedPosition >= this.mDate.length()) {
                this.mCurrentSelectedPosition = this.mDate.length() - 1;
            }
            if (this.mCurrentSelectedPosition < 0) {
                this.mCurrentSelectedPosition = 0;
            }
            if (this.mCurrentSelectedPosition != -1) {
                if (this.flag) {
                    this.mPaint.setColor(InputDeviceCompat.SOURCE_ANY);
                } else {
                    this.mPaint.setColor(-1);
                }
                if (this.flagDone) {
                    this.mPaint.setColor(-1);
                }
                if (this.mCurrentSelectedPosition == 0) {
                    canvas.drawText(this.mDate.substring(this.mCurrentSelectedPosition, 1), this.x, this.y, this.mPaint);
                    return;
                }
                this.x += this.mPaint.measureText(this.mDate.substring(0, this.mCurrentSelectedPosition));
                canvas.drawText(this.mDate.substring(this.mCurrentSelectedPosition, this.mCurrentSelectedPosition + 1), this.x, this.y, this.mPaint);
            }
        }
    }

    public void onKeyLeft() {
        this.flagDone = false;
        if (this.mCurrentSelectedPosition != 0) {
            this.mCurrentSelectedPosition--;
        }
        for (int index : this.mSplitIndex) {
            if (index == this.mCurrentSelectedPosition) {
                this.mCurrentSelectedPosition--;
            }
        }
        postInvalidate();
    }

    public void onKeyRight() {
        this.flagDone = false;
        if (this.mCurrentSelectedPosition != this.mDate.length() - 1) {
            this.mCurrentSelectedPosition++;
        }
        for (int index : this.mSplitIndex) {
            if (index == this.mCurrentSelectedPosition) {
                this.mCurrentSelectedPosition++;
            }
        }
        postInvalidate();
    }

    public void input(char ch, SetConfigListViewAdapter.DataItem mDataItem) {
        this.mDateChars = this.mDate.toCharArray();
        if (this.mCurrentSelectedPosition == -1) {
            this.mCurrentSelectedPosition = 0;
        }
        this.mDateChars[this.mCurrentSelectedPosition] = ch;
        String tempStr = String.valueOf(this.mDateChars);
        if (validate(tempStr, this.mCurrentSelectedPosition, ch, this.mType)) {
            this.mDate = tempStr;
            this.updateTime.onTimeModified(this.mDate);
            if (this.mType == 0 && (((this.mCurrentSelectedPosition == 0 || this.mCurrentSelectedPosition == 1) && Integer.valueOf(this.mDate.substring(0, 2)).intValue() > 20) || ((this.mCurrentSelectedPosition == 2 && Integer.valueOf(this.mDate.substring(2, 4)).intValue() > 37) || ((this.mCurrentSelectedPosition == 3 && this.mDateChars[2] == '3' && this.mDateChars[this.mCurrentSelectedPosition] > '7') || ((this.mCurrentSelectedPosition == 2 || this.mCurrentSelectedPosition == 3 || this.mCurrentSelectedPosition == 5 || this.mCurrentSelectedPosition == 6) && Integer.valueOf(this.mDate.substring(0, 4)).intValue() == 2000 && Integer.valueOf(this.mDate.substring(5, 7)).intValue() == 1 && Integer.valueOf(this.mDate.substring(8, 10)).intValue() < 2))))) {
                changeNotValidate();
            }
            if (this.mType == 0 || this.mType == 1) {
                SaveValue.getInstance(this.mContext);
            }
        } else {
            changeNotValidate();
        }
        onKeyRight();
    }

    public void setPaint(Paint mPaint2) {
        this.mPaint = mPaint2;
    }

    public void setDrawDone(boolean flagdone) {
        this.flagDone = flagdone;
    }

    public int[] getmSplitIndex() {
        return this.mSplitIndex;
    }

    public void setmSplitIndex(char[] mDataChar) {
        int[] temp = new int[mDataChar.length];
        int j = 0;
        for (int i = 0; i < mDataChar.length; i++) {
            if (mDataChar[i] < '0' || mDataChar[i] > '9') {
                temp[j] = i;
                j++;
            }
        }
        int i2 = 0;
        while (temp[i2] != 0) {
            i2++;
        }
        this.mSplitIndex = new int[i2];
        for (int j2 = 0; j2 < i2; j2++) {
            this.mSplitIndex[j2] = temp[j2];
        }
    }

    public void setTextSize(int mTextSize2) {
        this.mTextSize = mTextSize2;
    }

    /* access modifiers changed from: protected */
    public boolean isDateValidate(String time, int index, char value) {
        if (value > '9' || value < '0') {
            return false;
        }
        return time.replace(time.charAt(index), value).matches("^((((|[2-9]\\d)\\d{2})/(0?[13578]|1[02])/(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})/(0?[13456789]|1[012])/(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})/0?2/(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))/0?2/29/))$");
    }

    /* access modifiers changed from: protected */
    public boolean isTimeValidate(String time, int index, char value) {
        if (value > '9' || value < '0') {
            return false;
        }
        return time.replace(time.charAt(index), value).matches("(20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$");
    }

    /* access modifiers changed from: protected */
    public boolean validate(String str, int mCurrentSelectIndex, char value, int type) {
        if (type == 1) {
            return isTimeValidate(str, mCurrentSelectIndex, value);
        }
        if (type == 0) {
            return isDateValidate(str, mCurrentSelectIndex, value);
        }
        return false;
    }

    public Paint getmPaint() {
        return this.mPaint;
    }

    public void setmPaint(Paint mPaint2) {
        this.mPaint = mPaint2;
    }

    public String getmDate() {
        return this.mDate;
    }

    public void setmDate(String mDate2) {
        this.mDate = mDate2;
    }

    private void changeNotValidate() {
        char c;
        String tempStr = String.valueOf(this.mDateChars);
        if (this.mType == 0) {
            if (this.mCurrentSelectedPosition == 0 || this.mCurrentSelectedPosition == 1 || this.mCurrentSelectedPosition == 2 || this.mCurrentSelectedPosition == 3) {
                if (Integer.valueOf(tempStr.substring(0, 4)).intValue() > 2037) {
                    this.mDateChars[0] = '2';
                    this.mDateChars[1] = '0';
                    this.mDateChars[2] = '3';
                    this.mDateChars[3] = '7';
                } else if (Integer.valueOf(tempStr.substring(0, 4)).intValue() < 2000) {
                    this.mDateChars[0] = '2';
                    this.mDateChars[1] = '0';
                    this.mDateChars[2] = '0';
                    this.mDateChars[3] = '0';
                } else if (Integer.valueOf(tempStr.substring(0, 4)).intValue() == 2000 && Integer.valueOf(tempStr.substring(5, 7)).intValue() == 1 && Integer.valueOf(tempStr.substring(8, 10)).intValue() < 2) {
                    this.mDateChars[8] = '0';
                    this.mDateChars[9] = '2';
                } else if (Integer.valueOf(tempStr.substring(5, 7)).intValue() == 2) {
                    if ((Integer.valueOf(tempStr.substring(0, 4)).intValue() % 4 != 0 || Integer.valueOf(tempStr.substring(0, 4)).intValue() % 100 == 0) && Integer.valueOf(tempStr.substring(0, 4)).intValue() % 400 != 0) {
                        if (Integer.valueOf(tempStr.substring(8, 10)).intValue() > 28) {
                            this.mDateChars[8] = '2';
                            this.mDateChars[9] = '8';
                            this.mDateChars[5] = '0';
                            this.mDateChars[6] = '2';
                        }
                    } else if (Integer.valueOf(tempStr.substring(8, 10)).intValue() > 29) {
                        this.mDateChars[8] = '2';
                        this.mDateChars[9] = '9';
                        this.mDateChars[5] = '0';
                        this.mDateChars[6] = '2';
                    }
                }
            } else if (this.mCurrentSelectedPosition == 5 || this.mCurrentSelectedPosition == 6) {
                if (Integer.valueOf(tempStr.substring(5, 7)).intValue() > 12) {
                    this.mDateChars[5] = '1';
                    this.mDateChars[6] = '2';
                } else if (Integer.valueOf(tempStr.substring(5, 7)).intValue() < 1) {
                    this.mDateChars[5] = '0';
                    this.mDateChars[6] = '1';
                } else if (Integer.valueOf(tempStr.substring(0, 4)).intValue() == 2000 && Integer.valueOf(tempStr.substring(5, 7)).intValue() == 1 && Integer.valueOf(tempStr.substring(8, 10)).intValue() < 2) {
                    this.mDateChars[8] = '0';
                    this.mDateChars[9] = '2';
                } else if (Integer.valueOf(tempStr.substring(5, 7)).intValue() != 2) {
                    int intValue = Integer.valueOf(tempStr.substring(5, 7)).intValue();
                    if (intValue != 3) {
                        if (intValue == 6) {
                            this.mDateChars[5] = '0';
                            this.mDateChars[6] = '6';
                        } else if (intValue == 9) {
                            this.mDateChars[5] = '0';
                            this.mDateChars[6] = '9';
                        } else if (intValue == 11) {
                            this.mDateChars[5] = '1';
                            this.mDateChars[6] = '1';
                        }
                        c = '3';
                    } else {
                        this.mDateChars[5] = '0';
                        c = '3';
                        this.mDateChars[6] = '3';
                    }
                    if (Integer.valueOf(tempStr.substring(8, 10)).intValue() > 30) {
                        this.mDateChars[8] = c;
                        this.mDateChars[9] = '0';
                    }
                } else if ((Integer.valueOf(tempStr.substring(0, 4)).intValue() % 4 != 0 || Integer.valueOf(tempStr.substring(0, 4)).intValue() % 100 == 0) && Integer.valueOf(tempStr.substring(0, 4)).intValue() % 400 != 0) {
                    if (Integer.valueOf(tempStr.substring(8, 10)).intValue() > 28) {
                        this.mDateChars[8] = '2';
                        this.mDateChars[9] = '8';
                        this.mDateChars[5] = '0';
                        this.mDateChars[6] = '2';
                    }
                } else if (Integer.valueOf(tempStr.substring(8, 10)).intValue() > 29) {
                    this.mDateChars[8] = '2';
                    this.mDateChars[9] = '9';
                    this.mDateChars[5] = '0';
                    this.mDateChars[6] = '2';
                }
            } else if (this.mCurrentSelectedPosition == 8 || this.mCurrentSelectedPosition == 9) {
                if (Integer.valueOf(tempStr.substring(0, 4)).intValue() != 2000 || Integer.valueOf(tempStr.substring(5, 7)).intValue() != 1 || Integer.valueOf(tempStr.substring(8, 10)).intValue() >= 2) {
                    if (Integer.valueOf(tempStr.substring(8, 10)).intValue() >= 1) {
                        if (Integer.valueOf(this.mDate.substring(5, 7)).intValue() != 2) {
                            switch (Integer.valueOf(this.mDate.substring(5, 7)).intValue()) {
                                case 1:
                                case 3:
                                case 5:
                                case 7:
                                case 8:
                                case 10:
                                case 12:
                                    this.mDateChars[8] = '3';
                                    this.mDateChars[9] = '1';
                                    break;
                                case 2:
                                case 4:
                                case 6:
                                case 9:
                                case 11:
                                    this.mDateChars[8] = '3';
                                    this.mDateChars[9] = '0';
                                    break;
                            }
                        } else if ((Integer.valueOf(this.mDate.substring(0, 4)).intValue() % 4 != 0 || Integer.valueOf(this.mDate.substring(0, 4)).intValue() % 100 == 0) && Integer.valueOf(this.mDate.substring(0, 4)).intValue() % 400 != 0) {
                            this.mDateChars[8] = '2';
                            this.mDateChars[9] = '8';
                        } else {
                            this.mDateChars[8] = '2';
                            this.mDateChars[9] = '9';
                        }
                    } else {
                        this.mDateChars[8] = '0';
                        this.mDateChars[9] = '1';
                    }
                } else {
                    this.mDateChars[8] = '0';
                    this.mDateChars[9] = '2';
                }
            }
        }
        if (this.mType == 1) {
            if (this.mCurrentSelectedPosition == 0) {
                this.mDateChars[0] = '2';
                this.mDateChars[1] = '3';
            } else if (this.mCurrentSelectedPosition == 1) {
                this.mDateChars[1] = '3';
            } else if (this.mCurrentSelectedPosition == 3) {
                this.mDateChars[3] = '5';
            } else if (this.mCurrentSelectedPosition == 6) {
                this.mDateChars[6] = '5';
            }
        }
        this.mDate = String.valueOf(this.mDateChars);
        this.updateTime.onTimeModified(this.mDate);
        if (this.mType == 0 || this.mType == 1) {
            SaveValue.getInstance(this.mContext);
        }
    }
}
