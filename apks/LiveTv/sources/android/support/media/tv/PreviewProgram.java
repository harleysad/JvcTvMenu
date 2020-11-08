package android.support.media.tv;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RestrictTo;
import android.support.media.tv.BasePreviewProgram;
import android.support.media.tv.TvContractCompat;
import java.util.Objects;

public final class PreviewProgram extends BasePreviewProgram {
    private static final int INVALID_INT_VALUE = -1;
    private static final long INVALID_LONG_VALUE = -1;
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static final String[] PROJECTION = getProjection();

    PreviewProgram(Builder builder) {
        super(builder);
    }

    public long getChannelId() {
        Long l = this.mValues.getAsLong("channel_id");
        if (l == null) {
            return -1;
        }
        return l.longValue();
    }

    public int getWeight() {
        Integer i = this.mValues.getAsInteger(TvContractCompat.PreviewPrograms.COLUMN_WEIGHT);
        if (i == null) {
            return -1;
        }
        return i.intValue();
    }

    public boolean equals(Object other) {
        if (!(other instanceof PreviewProgram)) {
            return false;
        }
        return this.mValues.equals(((PreviewProgram) other).mValues);
    }

    public boolean hasAnyUpdatedValues(PreviewProgram update) {
        for (String key : update.mValues.keySet()) {
            if (!Objects.deepEquals(update.mValues.get(key), this.mValues.get(key))) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "PreviewProgram{" + this.mValues.toString() + "}";
    }

    public ContentValues toContentValues() {
        return toContentValues(false);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public ContentValues toContentValues(boolean includeProtectedFields) {
        ContentValues values = super.toContentValues(includeProtectedFields);
        if (Build.VERSION.SDK_INT < 26) {
            values.remove("channel_id");
            values.remove(TvContractCompat.PreviewPrograms.COLUMN_WEIGHT);
        }
        return values;
    }

    public static PreviewProgram fromCursor(Cursor cursor) {
        Builder builder = new Builder();
        BasePreviewProgram.setFieldsFromCursor(cursor, builder);
        int columnIndex = cursor.getColumnIndex("channel_id");
        int index = columnIndex;
        if (columnIndex >= 0 && !cursor.isNull(index)) {
            builder.setChannelId(cursor.getLong(index));
        }
        int columnIndex2 = cursor.getColumnIndex(TvContractCompat.PreviewPrograms.COLUMN_WEIGHT);
        int index2 = columnIndex2;
        if (columnIndex2 >= 0 && !cursor.isNull(index2)) {
            builder.setWeight(cursor.getInt(index2));
        }
        return builder.build();
    }

    private static String[] getProjection() {
        String[] oColumns = {"channel_id", TvContractCompat.PreviewPrograms.COLUMN_WEIGHT};
        return (String[]) CollectionUtils.concatAll(BasePreviewProgram.PROJECTION, oColumns);
    }

    public static final class Builder extends BasePreviewProgram.Builder<Builder> {
        public Builder() {
        }

        public Builder(PreviewProgram other) {
            this.mValues = new ContentValues(other.mValues);
        }

        public Builder setChannelId(long channelId) {
            this.mValues.put("channel_id", Long.valueOf(channelId));
            return this;
        }

        public Builder setWeight(int weight) {
            this.mValues.put(TvContractCompat.PreviewPrograms.COLUMN_WEIGHT, Integer.valueOf(weight));
            return this;
        }

        public PreviewProgram build() {
            return new PreviewProgram(this);
        }
    }
}
