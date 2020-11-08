package android.support.v4.content;

import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class ModernAsyncTask<Result> {
    private static final String LOG_TAG = "AsyncTask";
    private static Handler sHandler;
    final AtomicBoolean mCancelled = new AtomicBoolean();
    private final FutureTask<Result> mFuture = new FutureTask<Result>(new Callable<Result>() {
        public Result call() {
            ModernAsyncTask.this.mTaskInvoked.set(true);
            Result result = null;
            try {
                Process.setThreadPriority(10);
                result = ModernAsyncTask.this.doInBackground();
                Binder.flushPendingCommands();
                ModernAsyncTask.this.postResult(result);
                return result;
            } catch (Throwable th) {
                ModernAsyncTask.this.postResult(result);
                throw th;
            }
        }
    }) {
        /* access modifiers changed from: protected */
        public void done() {
            try {
                ModernAsyncTask.this.postResultIfNotInvoked(get());
            } catch (InterruptedException e) {
                Log.w(ModernAsyncTask.LOG_TAG, e);
            } catch (ExecutionException e2) {
                throw new RuntimeException("An error occurred while executing doInBackground()", e2.getCause());
            } catch (CancellationException e3) {
                ModernAsyncTask.this.postResultIfNotInvoked(null);
            } catch (Throwable t) {
                throw new RuntimeException("An error occurred while executing doInBackground()", t);
            }
        }
    };
    private volatile Status mStatus = Status.PENDING;
    final AtomicBoolean mTaskInvoked = new AtomicBoolean();

    public enum Status {
        PENDING,
        RUNNING,
        FINISHED
    }

    /* access modifiers changed from: protected */
    public abstract Result doInBackground();

    private static Handler getHandler() {
        Handler handler;
        synchronized (ModernAsyncTask.class) {
            if (sHandler == null) {
                sHandler = new Handler(Looper.getMainLooper());
            }
            handler = sHandler;
        }
        return handler;
    }

    ModernAsyncTask() {
    }

    /* access modifiers changed from: package-private */
    public void postResultIfNotInvoked(Result result) {
        if (!this.mTaskInvoked.get()) {
            postResult(result);
        }
    }

    /* access modifiers changed from: package-private */
    public void postResult(final Result result) {
        getHandler().post(new Runnable() {
            public void run() {
                ModernAsyncTask.this.finish(result);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(Result result) {
    }

    /* access modifiers changed from: protected */
    public void onCancelled(Result result) {
    }

    public final boolean isCancelled() {
        return this.mCancelled.get();
    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        this.mCancelled.set(true);
        return this.mFuture.cancel(mayInterruptIfRunning);
    }

    public final void executeOnExecutor(@NonNull Executor exec) {
        if (this.mStatus != Status.PENDING) {
            switch (this.mStatus) {
                case RUNNING:
                    throw new IllegalStateException("Cannot execute task: the task is already running.");
                case FINISHED:
                    throw new IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)");
                default:
                    throw new IllegalStateException("We should never reach this state");
            }
        } else {
            this.mStatus = Status.RUNNING;
            exec.execute(this.mFuture);
        }
    }

    /* access modifiers changed from: package-private */
    public void finish(Result result) {
        if (isCancelled()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
        this.mStatus = Status.FINISHED;
    }
}
