package com.android.tv.settings.widget;

public final class Lerper {
    public static final float DEFAULT_DIVISOR = 2.0f;
    private float mDivisor = 2.0f;
    private float mMinDelta = 0.5f;

    public void setDivisor(float divisor) {
        if (divisor >= 1.0f) {
            this.mDivisor = divisor;
            this.mMinDelta = 1.0f / divisor;
            return;
        }
        throw new IllegalArgumentException();
    }

    public float getDivisor() {
        return this.mDivisor;
    }

    public float getMinDelta() {
        return this.mMinDelta;
    }

    public int getValue(int currentValue, int targetValue) {
        int delta = targetValue - currentValue;
        if (delta > 0) {
            int delta2 = (int) Math.ceil((double) (((float) delta) / this.mDivisor));
            if (delta2 == 0) {
                delta2 = 1;
            }
            int retValue = currentValue + delta2;
            if (retValue > targetValue) {
                return targetValue;
            }
            return retValue;
        } else if (delta >= 0) {
            return targetValue;
        } else {
            int delta3 = (int) Math.floor((double) (((float) delta) / this.mDivisor));
            if (delta3 == 0) {
                delta3 = -1;
            }
            int retValue2 = currentValue + delta3;
            if (retValue2 < targetValue) {
                return targetValue;
            }
            return retValue2;
        }
    }

    public float getValue(float currentValue, float targetValue) {
        float delta = targetValue - currentValue;
        if (delta > this.mMinDelta) {
            float retValue = currentValue + (delta / this.mDivisor);
            if (retValue > targetValue) {
                return targetValue;
            }
            return retValue;
        } else if (delta >= (-this.mMinDelta)) {
            return targetValue;
        } else {
            float retValue2 = currentValue + (delta / this.mDivisor);
            if (retValue2 < targetValue) {
                return targetValue;
            }
            return retValue2;
        }
    }
}
