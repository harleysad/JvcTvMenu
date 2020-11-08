package com.mediatek.wwtv.tvcenter.nav.view;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TTXToast extends Toast {
    private boolean isShowing;

    public TTXToast(Context context) {
        super(context);
        this.isShowing = false;
    }

    public TTXToast(Context c, View v) {
        this(c);
        setView(v);
    }

    public void showAlways() {
        try {
            Field mTNField = Toast.class.getDeclaredField("mTN");
            mTNField.setAccessible(true);
            Object mTN = mTNField.get(this);
            try {
                Field mNextViewField = mTN.getClass().getDeclaredField("mNextView");
                mNextViewField.setAccessible(true);
                mNextViewField.set(mTN, getView());
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            try {
                Method showMethod = mTN.getClass().getDeclaredMethod("show", new Class[0]);
                showMethod.setAccessible(true);
                showMethod.invoke(mTN, new Object[0]);
                this.isShowing = true;
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    public void show() {
        super.show();
        this.isShowing = true;
    }

    public void show(long delayMillis) {
        showAlways();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                TTXToast.this.cancel();
            }
        }, delayMillis);
    }

    public void cancel() {
        super.cancel();
        this.isShowing = false;
    }

    public boolean isShowing() {
        return this.isShowing;
    }
}
