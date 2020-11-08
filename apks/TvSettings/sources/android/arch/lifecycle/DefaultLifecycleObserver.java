package android.arch.lifecycle;

import android.support.annotation.NonNull;

public interface DefaultLifecycleObserver extends FullLifecycleObserver {
    void onCreate(@NonNull LifecycleOwner owner) {
    }

    void onStart(@NonNull LifecycleOwner owner) {
    }

    void onResume(@NonNull LifecycleOwner owner) {
    }

    void onPause(@NonNull LifecycleOwner owner) {
    }

    void onStop(@NonNull LifecycleOwner owner) {
    }

    void onDestroy(@NonNull LifecycleOwner owner) {
    }
}
