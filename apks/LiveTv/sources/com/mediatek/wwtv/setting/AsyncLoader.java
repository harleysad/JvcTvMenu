package com.mediatek.wwtv.setting;

import android.os.AsyncTask;

public class AsyncLoader {
    static AsyncLoader mSelf;
    DataAsyncTask mAsyncTask;
    /* access modifiers changed from: private */
    public DataLoadListener mListener;

    public interface DataLoadListener {
        void loadData();

        void loadFinished();

        void loadStarting();
    }

    public static AsyncLoader getInstance() {
        if (mSelf == null) {
            mSelf = new AsyncLoader();
        }
        return mSelf;
    }

    private AsyncLoader() {
    }

    public void execute(Object params) {
        this.mAsyncTask = new DataAsyncTask();
        this.mAsyncTask.execute(new Object[]{params});
    }

    public void cancelTask() {
        if (this.mAsyncTask != null && this.mAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.mAsyncTask.cancel(true);
        }
    }

    public void bindDataLoadListener(DataLoadListener Listener) {
        this.mListener = Listener;
    }

    public boolean isTaskRunning() {
        if (this.mAsyncTask == null || this.mAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
            return false;
        }
        return true;
    }

    class DataAsyncTask extends AsyncTask<Object, Object, Object> {
        DataAsyncTask() {
        }

        /* access modifiers changed from: protected */
        public Object doInBackground(Object... params) {
            AsyncLoader.this.mListener.loadData();
            return null;
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            super.onCancelled();
        }

        /* access modifiers changed from: protected */
        public void onCancelled(Object result) {
            super.onCancelled(result);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Object result) {
            AsyncLoader.this.mListener.loadFinished();
            super.onPostExecute(result);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            AsyncLoader.this.mListener.loadStarting();
            super.onPreExecute();
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
        }
    }
}
