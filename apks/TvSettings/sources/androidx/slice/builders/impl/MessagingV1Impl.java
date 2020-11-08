package androidx.slice.builders.impl;

import android.graphics.drawable.Icon;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceSpec;
import androidx.slice.builders.impl.MessagingBuilder;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public class MessagingV1Impl extends TemplateBuilderImpl implements MessagingBuilder {
    public MessagingV1Impl(Slice.Builder b, SliceSpec spec) {
        super(b, spec);
    }

    public void add(TemplateBuilderImpl builder) {
        getBuilder().addSubSlice(builder.build(), "message");
    }

    public void apply(Slice.Builder builder) {
    }

    public TemplateBuilderImpl createMessageBuilder() {
        return new MessageBuilder(this);
    }

    public static final class MessageBuilder extends TemplateBuilderImpl implements MessagingBuilder.MessageBuilder {
        public MessageBuilder(MessagingV1Impl parent) {
            super(parent.createChildBuilder(), (SliceSpec) null);
        }

        @RequiresApi(23)
        public void addSource(Icon source) {
            getBuilder().addIcon(IconCompat.createFromIcon(source), "source", new String[0]);
        }

        public void addText(CharSequence text) {
            getBuilder().addText(text, (String) null, new String[0]);
        }

        public void addTimestamp(long timestamp) {
            getBuilder().addTimestamp(timestamp, (String) null, new String[0]);
        }

        public void apply(Slice.Builder builder) {
        }
    }
}
