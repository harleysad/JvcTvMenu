package androidx.slice;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import androidx.versionedparcelable.VersionedParcelable;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public final class SliceSpec implements VersionedParcelable {
    int mRevision;
    String mType;

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public SliceSpec() {
    }

    public SliceSpec(@NonNull String type, int revision) {
        this.mType = type;
        this.mRevision = revision;
    }

    public String getType() {
        return this.mType;
    }

    public int getRevision() {
        return this.mRevision;
    }

    public boolean canRender(@NonNull SliceSpec candidate) {
        if (this.mType.equals(candidate.mType) && this.mRevision >= candidate.mRevision) {
            return true;
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SliceSpec)) {
            return false;
        }
        SliceSpec other = (SliceSpec) obj;
        if (!this.mType.equals(other.mType) || this.mRevision != other.mRevision) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.mType.hashCode() + this.mRevision;
    }

    public String toString() {
        return String.format("SliceSpec{%s,%d}", new Object[]{this.mType, Integer.valueOf(this.mRevision)});
    }
}
