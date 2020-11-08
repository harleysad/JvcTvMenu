package androidx.slice.builders.impl;

import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceSpec;
import androidx.slice.builders.impl.MessagingBuilder;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public class MessagingBasicImpl extends TemplateBuilderImpl implements MessagingBuilder {
    private MessageBuilder mLastMessage;

    public MessagingBasicImpl(Slice.Builder builder, SliceSpec spec) {
        super(builder, spec);
    }

    public void apply(Slice.Builder builder) {
        if (this.mLastMessage != null) {
            if (Build.VERSION.SDK_INT >= 23 && this.mLastMessage.mIcon != null) {
                builder.addIcon(IconCompat.createFromIcon(this.mLastMessage.mIcon), (String) null, new String[0]);
            }
            if (this.mLastMessage.mText != null) {
                builder.addText(this.mLastMessage.mText, (String) null, new String[0]);
            }
        }
    }

    public void add(TemplateBuilderImpl builder) {
        MessageBuilder b = (MessageBuilder) builder;
        if (this.mLastMessage == null || this.mLastMessage.mTimestamp < b.mTimestamp) {
            this.mLastMessage = b;
        }
    }

    public TemplateBuilderImpl createMessageBuilder() {
        return new MessageBuilder(this);
    }

    public static final class MessageBuilder extends TemplateBuilderImpl implements MessagingBuilder.MessageBuilder {
        /* access modifiers changed from: private */
        @RequiresApi(23)
        public Icon mIcon;
        /* access modifiers changed from: private */
        public CharSequence mText;
        /* access modifiers changed from: private */
        public long mTimestamp;

        public MessageBuilder(MessagingBasicImpl parent) {
            this(parent.createChildBuilder());
        }

        private MessageBuilder(Slice.Builder builder) {
            super(builder, (SliceSpec) null);
        }

        @RequiresApi(23)
        public void addSource(Icon source) {
            this.mIcon = source;
        }

        public void addText(CharSequence text) {
            this.mText = text;
        }

        public void addTimestamp(long timestamp) {
            this.mTimestamp = timestamp;
        }

        public void apply(Slice.Builder builder) {
        }
    }
}
