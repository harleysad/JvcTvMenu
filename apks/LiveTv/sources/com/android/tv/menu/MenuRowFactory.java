package com.android.tv.menu;

import android.content.Context;
import android.media.tv.TvView;
import android.support.annotation.Nullable;
import com.android.tv.menu.ItemListRowView;
import com.android.tv.menu.customization.CustomAction;
import com.android.tv.menu.customization.TvCustomizationManager;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback;
import java.util.List;

public class MenuRowFactory {
    private final Context mContext;
    private final TvCustomizationManager mTvCustomizationManager;
    private final TvView mTvView;

    public MenuRowFactory(Context context, TvView tvView) {
        this.mContext = context;
        this.mTvView = tvView;
        this.mTvCustomizationManager = new TvCustomizationManager(context);
        this.mTvCustomizationManager.initialize();
    }

    @Nullable
    public MenuRow createMenuRow(Menu menu, Class<?> key) {
        if (ChannelsRow.class.equals(key)) {
            return new ChannelsRow(this.mContext, menu);
        }
        if (TvOptionsRow.class.equals(key)) {
            return new TvOptionsRow(this.mContext, menu, this.mTvCustomizationManager.getCustomActions(TvCustomizationManager.ID_OPTIONS_ROW));
        }
        if (RecordRow.class.equals(key)) {
            return new RecordRow(this.mContext, menu);
        }
        return null;
    }

    public static class TvOptionsRow extends ItemListRow {
        public static final String ID = TvOptionsRow.class.getName();

        private TvOptionsRow(Context context, Menu menu, List<CustomAction> customActions) {
            super(context, menu, (int) R.string.menu_title_options, (int) R.dimen.action_card_height, (ItemListRowView.ItemListAdapter) new TvOptionsRowAdapter(context, customActions));
        }

        public void onStreamInfoChanged() {
            if (getMenu().isActive()) {
                update();
            }
        }

        public String getTitle() {
            return this.mContext.getString(R.string.menu_title_options);
        }
    }

    public static class RecordRow extends ItemListRow {
        public static final String ID = RecordRow.class.getName();

        private RecordRow(Context context, Menu menu) {
            super(context, menu, (int) R.string.menu_arrays_Record, (int) R.dimen.action_card_height, (ItemListRowView.ItemListAdapter) new RecordRowAdapter(context));
        }

        public boolean isVisible() {
            return super.isVisible() && (StateDvrPlayback.getInstance() == null || !StateDvrPlayback.getInstance().isRunning());
        }

        public String getTitle() {
            return this.mContext.getString(R.string.menu_arrays_Record);
        }
    }
}
