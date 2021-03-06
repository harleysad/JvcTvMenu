package com.android.settingslib.suggestions;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.service.settings.suggestions.ISuggestionService;
import android.service.settings.suggestions.Suggestion;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import java.util.List;

public class SuggestionController {
    private static final boolean DEBUG = false;
    private static final String TAG = "SuggestionController";
    /* access modifiers changed from: private */
    public ServiceConnectionListener mConnectionListener;
    private final Context mContext;
    /* access modifiers changed from: private */
    public ISuggestionService mRemoteService;
    private ServiceConnection mServiceConnection = createServiceConnection();
    private final Intent mServiceIntent;

    public interface ServiceConnectionListener {
        void onServiceConnected();

        void onServiceDisconnected();
    }

    public SuggestionController(Context context, ComponentName service, ServiceConnectionListener listener) {
        this.mContext = context.getApplicationContext();
        this.mConnectionListener = listener;
        this.mServiceIntent = new Intent().setComponent(service);
    }

    public void start() {
        this.mContext.bindServiceAsUser(this.mServiceIntent, this.mServiceConnection, 1, Process.myUserHandle());
    }

    public void stop() {
        if (this.mRemoteService != null) {
            this.mRemoteService = null;
            this.mContext.unbindService(this.mServiceConnection);
        }
    }

    @Nullable
    @WorkerThread
    public List<Suggestion> getSuggestions() {
        if (!isReady()) {
            return null;
        }
        try {
            return this.mRemoteService.getSuggestions();
        } catch (NullPointerException e) {
            Log.w(TAG, "mRemote service detached before able to query", e);
            return null;
        } catch (RemoteException | RuntimeException e2) {
            Log.w(TAG, "Error when calling getSuggestion()", e2);
            return null;
        }
    }

    public void dismissSuggestions(Suggestion suggestion) {
        if (!isReady()) {
            Log.w(TAG, "SuggestionController not ready, cannot dismiss " + suggestion.getId());
            return;
        }
        try {
            this.mRemoteService.dismissSuggestion(suggestion);
        } catch (RemoteException | RuntimeException e) {
            Log.w(TAG, "Error when calling dismissSuggestion()", e);
        }
    }

    public void launchSuggestion(Suggestion suggestion) {
        if (!isReady()) {
            Log.w(TAG, "SuggestionController not ready, cannot launch " + suggestion.getId());
            return;
        }
        try {
            this.mRemoteService.launchSuggestion(suggestion);
        } catch (RemoteException | RuntimeException e) {
            Log.w(TAG, "Error when calling launchSuggestion()", e);
        }
    }

    private boolean isReady() {
        return this.mRemoteService != null;
    }

    private ServiceConnection createServiceConnection() {
        return new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder service) {
                ISuggestionService unused = SuggestionController.this.mRemoteService = ISuggestionService.Stub.asInterface(service);
                if (SuggestionController.this.mConnectionListener != null) {
                    SuggestionController.this.mConnectionListener.onServiceConnected();
                }
            }

            public void onServiceDisconnected(ComponentName name) {
                if (SuggestionController.this.mConnectionListener != null) {
                    ISuggestionService unused = SuggestionController.this.mRemoteService = null;
                    SuggestionController.this.mConnectionListener.onServiceDisconnected();
                }
            }
        };
    }
}
