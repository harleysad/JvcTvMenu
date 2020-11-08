package androidx.slice.builders.impl;

import android.graphics.drawable.Icon;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceSpec;
import androidx.slice.builders.impl.ListBuilderV1Impl;
import androidx.slice.builders.impl.MessagingBuilder;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public class MessagingListV1Impl extends TemplateBuilderImpl implements MessagingBuilder {
    private final ListBuilderV1Impl mListBuilder;

    public MessagingListV1Impl(Slice.Builder b, SliceSpec spec) {
        super(b, spec);
        this.mListBuilder = new ListBuilderV1Impl(b, spec);
        this.mListBuilder.setTtl(-1);
    }

    public void add(TemplateBuilderImpl builder) {
        this.mListBuilder.addRow(((MessageBuilder) builder).mListBuilder);
    }

    public TemplateBuilderImpl createMessageBuilder() {
        return new MessageBuilder(this);
    }

    public void apply(Slice.Builder builder) {
        this.mListBuilder.apply(builder);
    }

    public static final class MessageBuilder extends TemplateBuilderImpl implements MessagingBuilder.MessageBuilder {
        /* access modifiers changed from: private */
        public final ListBuilderV1Impl.RowBuilderImpl mListBuilder;

        public MessageBuilder(MessagingListV1Impl parent) {
            this(parent.createChildBuilder());
        }

        private MessageBuilder(Slice.Builder builder) {
            super(builder, (SliceSpec) null);
            this.mListBuilder = new ListBuilderV1Impl.RowBuilderImpl(builder);
        }

        @RequiresApi(23)
        public void addSource(Icon source) {
            this.mListBuilder.setTitleItem(IconCompat.createFromIcon(source), 1);
        }

        public void addText(CharSequence text) {
            this.mListBuilder.setSubtitle(text);
        }

        public void addTimestamp(long timestamp) {
            this.mListBuilder.addEndItem(timestamp);
        }

        public void apply(Slice.Builder builder) {
            this.mListBuilder.apply(builder);
        }
    }
}
