package com.android.tv.menu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.tv.util.images.BitmapUtils;
import com.android.tv.util.images.ImageLoader;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;

public class AppLinkCardView extends BaseCardView<ChannelsRowItem> {
    private static final String TAG = MenuView.TAG;
    /* access modifiers changed from: private */
    public TextView mAppInfoView;
    private final int mCardImageHeight;
    private final int mCardImageWidth;
    /* access modifiers changed from: private */
    public TIFChannelInfo mChannel;
    private final Drawable mDefaultDrawable;
    /* access modifiers changed from: private */
    public final int mIconColorFilter;
    /* access modifiers changed from: private */
    public final int mIconHeight;
    private final int mIconPadding;
    /* access modifiers changed from: private */
    public final int mIconWidth;
    private ImageView mImageView;
    /* access modifiers changed from: private */
    public Intent mIntent;
    /* access modifiers changed from: private */
    public View mMetaViewHolder;
    /* access modifiers changed from: private */
    public final PackageManager mPackageManager;

    public AppLinkCardView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppLinkCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppLinkCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCardImageWidth = getResources().getDimensionPixelSize(R.dimen.card_image_layout_width);
        this.mCardImageHeight = getResources().getDimensionPixelSize(R.dimen.card_image_layout_height);
        this.mIconWidth = getResources().getDimensionPixelSize(R.dimen.app_link_card_icon_width);
        this.mIconHeight = getResources().getDimensionPixelSize(R.dimen.app_link_card_icon_height);
        this.mIconPadding = getResources().getDimensionPixelOffset(R.dimen.app_link_card_icon_padding);
        this.mPackageManager = context.getPackageManager();
        this.mIconColorFilter = getResources().getColor(R.color.app_link_card_icon_color_filter, (Resources.Theme) null);
        this.mDefaultDrawable = getResources().getDrawable(R.drawable.ic_recent_thumbnail_default, (Resources.Theme) null);
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public void onBind(ChannelsRowItem item, boolean selected) {
        TIFChannelInfo newChannel = item.getChannel();
        if (this.mChannel != null) {
            String str = this.mChannel.mAppLinkPosterArtUri;
        }
        this.mChannel = newChannel;
        String str2 = TAG;
        Log.d(str2, "onBind(channelName=" + this.mChannel + ", selected=" + selected + ")");
        if (this.mChannel != null) {
            final ApplicationInfo appInfo = TvSingletons.getSingletons().getTvInputManagerHelper().getTvInputAppInfo(this.mChannel.mInputServiceName);
            if (1 != 0) {
                int linkType = this.mChannel.getAppLinkType(getContext());
                this.mIntent = this.mChannel.getAppLinkIntent(getContext());
                switch (linkType) {
                    case 1:
                        setText(this.mChannel.mAppLinkText);
                        this.mAppInfoView.setVisibility(0);
                        this.mAppInfoView.setCompoundDrawablePadding(this.mIconPadding);
                        this.mAppInfoView.setCompoundDrawablesRelative((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
                        CharSequence appLabel = TvSingletons.getSingletons().getTvInputManagerHelper().getTvInputApplicationLabel(this.mChannel.mInputServiceName);
                        if (appLabel != null) {
                            this.mAppInfoView.setText(appLabel);
                        } else {
                            new AsyncTask<Void, Void, CharSequence>() {
                                private final String mLoadTvInputId = AppLinkCardView.this.mChannel.mInputServiceName;

                                /* access modifiers changed from: protected */
                                public CharSequence doInBackground(Void... params) {
                                    if (appInfo != null) {
                                        return AppLinkCardView.this.mPackageManager.getApplicationLabel(appInfo);
                                    }
                                    return null;
                                }

                                /* access modifiers changed from: protected */
                                public void onPostExecute(CharSequence appLabel) {
                                    TvSingletons.getSingletons().getTvInputManagerHelper().setTvInputApplicationLabel(this.mLoadTvInputId, appLabel);
                                    if (!this.mLoadTvInputId.equals(AppLinkCardView.this.mChannel.mInputServiceName) && AppLinkCardView.this.isAttachedToWindow()) {
                                        AppLinkCardView.this.mAppInfoView.setText(appLabel);
                                    }
                                }
                            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                        }
                        if (TextUtils.isEmpty(this.mChannel.mAppLinkIconUri)) {
                            if (!(appInfo == null || appInfo.icon == 0)) {
                                Drawable appIcon = TvSingletons.getSingletons().getTvInputManagerHelper().getTvInputApplicationIcon(this.mChannel.mInputServiceName);
                                if (appIcon == null) {
                                    new AsyncTask<Void, Void, Drawable>() {
                                        private final String mLoadTvInputId = AppLinkCardView.this.mChannel.mInputServiceName;

                                        /* access modifiers changed from: protected */
                                        public Drawable doInBackground(Void... params) {
                                            return AppLinkCardView.this.mPackageManager.getApplicationIcon(appInfo);
                                        }

                                        /* access modifiers changed from: protected */
                                        public void onPostExecute(Drawable appIcon) {
                                            TvSingletons.getSingletons().getTvInputManagerHelper().setTvInputApplicationIcon(this.mLoadTvInputId, appIcon);
                                            if (this.mLoadTvInputId.equals(AppLinkCardView.this.mChannel.mInputServiceName) && AppLinkCardView.this.isAttachedToWindow()) {
                                                BitmapUtils.setColorFilterToDrawable(AppLinkCardView.this.mIconColorFilter, appIcon);
                                                appIcon.setBounds(0, 0, AppLinkCardView.this.mIconWidth, AppLinkCardView.this.mIconHeight);
                                                AppLinkCardView.this.mAppInfoView.setCompoundDrawablesRelative(appIcon, (Drawable) null, (Drawable) null, (Drawable) null);
                                            }
                                        }
                                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                                    break;
                                } else {
                                    BitmapUtils.setColorFilterToDrawable(this.mIconColorFilter, appIcon);
                                    appIcon.setBounds(0, 0, this.mIconWidth, this.mIconHeight);
                                    this.mAppInfoView.setCompoundDrawablesRelative(appIcon, (Drawable) null, (Drawable) null, (Drawable) null);
                                    break;
                                }
                            }
                        } else {
                            ImageLoader.loadBitmap(getContext(), this.mChannel.getImageUriString(2), this.mIconWidth, this.mIconHeight, createChannelLogoCallback(this, this.mChannel, 2));
                            break;
                        }
                    case 2:
                        CharSequence appLabel2 = TvSingletons.getSingletons().getTvInputManagerHelper().getTvInputApplicationLabel(this.mChannel.mInputServiceName);
                        if (appLabel2 != null) {
                            setText(getContext().getString(R.string.channels_item_app_link_app_launcher, new Object[]{appLabel2}));
                        } else {
                            new AsyncTask<Void, Void, CharSequence>() {
                                private final String mLoadTvInputId = AppLinkCardView.this.mChannel.mInputServiceName;

                                /* access modifiers changed from: protected */
                                public CharSequence doInBackground(Void... params) {
                                    if (appInfo != null) {
                                        return AppLinkCardView.this.mPackageManager.getApplicationLabel(appInfo);
                                    }
                                    return null;
                                }

                                /* access modifiers changed from: protected */
                                public void onPostExecute(CharSequence appLabel) {
                                    TvSingletons.getSingletons().getTvInputManagerHelper().setTvInputApplicationLabel(this.mLoadTvInputId, appLabel);
                                    if (this.mLoadTvInputId.equals(AppLinkCardView.this.mChannel.mInputServiceName) && AppLinkCardView.this.isAttachedToWindow()) {
                                        AppLinkCardView.this.setText(AppLinkCardView.this.getContext().getString(R.string.channels_item_app_link_app_launcher, new Object[]{appLabel}));
                                    }
                                }
                            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                        }
                        this.mAppInfoView.setVisibility(8);
                        break;
                    default:
                        this.mAppInfoView.setVisibility(8);
                        Log.d(TAG, "Should not be here.");
                        break;
                }
                if (this.mChannel.mAppLinkColor == 0) {
                    this.mMetaViewHolder.setBackgroundResource(R.color.channel_card_meta_background);
                } else {
                    this.mMetaViewHolder.setBackgroundColor(this.mChannel.mAppLinkColor);
                }
            }
            if (1 != 0) {
                this.mImageView.setImageDrawable(this.mDefaultDrawable);
                this.mImageView.setForeground((Drawable) null);
                if (!TextUtils.isEmpty(this.mChannel.mAppLinkPosterArtUri)) {
                    ImageLoader.loadBitmap(getContext(), this.mChannel.getImageUriString(3), this.mCardImageWidth, this.mCardImageHeight, createChannelLogoCallback(this, this.mChannel, 3));
                } else {
                    setCardImageWithBanner(appInfo);
                }
            }
            super.onBind(item, selected);
        }
    }

    private static ImageLoader.ImageLoaderCallback<AppLinkCardView> createChannelLogoCallback(AppLinkCardView cardView, final TIFChannelInfo channel, final int type) {
        return new ImageLoader.ImageLoaderCallback<AppLinkCardView>(cardView) {
            public void onBitmapLoaded(AppLinkCardView cardView, @Nullable Bitmap bitmap) {
                if (cardView.mChannel.mId == channel.mId) {
                    cardView.updateChannelLogo(bitmap, type);
                }
            }
        };
    }

    /* access modifiers changed from: private */
    public void updateChannelLogo(@Nullable Bitmap bitmap, int type) {
        if (type == 2) {
            BitmapDrawable drawable = null;
            if (bitmap != null) {
                drawable = new BitmapDrawable(getResources(), bitmap);
                if (bitmap.getWidth() > bitmap.getHeight()) {
                    drawable.setBounds(0, 0, this.mIconWidth, (this.mIconWidth * bitmap.getHeight()) / bitmap.getWidth());
                } else {
                    drawable.setBounds(0, 0, (this.mIconHeight * bitmap.getWidth()) / bitmap.getHeight(), this.mIconHeight);
                }
            }
            BitmapUtils.setColorFilterToDrawable(this.mIconColorFilter, drawable);
            this.mAppInfoView.setCompoundDrawablesRelative(drawable, (Drawable) null, (Drawable) null, (Drawable) null);
        } else if (type != 3) {
        } else {
            if (bitmap == null) {
                setCardImageWithBanner(TvSingletons.getSingletons().getTvInputManagerHelper().getTvInputAppInfo(this.mChannel.mInputServiceName));
                return;
            }
            this.mImageView.setImageBitmap(bitmap);
            this.mImageView.setForeground(getContext().getDrawable(R.drawable.card_image_gradient));
            if (this.mChannel.mAppLinkColor == 0) {
                extractAndSetMetaViewBackgroundColor(bitmap);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mImageView = (ImageView) findViewById(R.id.image);
        this.mAppInfoView = (TextView) findViewById(R.id.app_info);
        this.mMetaViewHolder = findViewById(R.id.app_link_text_holder);
    }

    private void setCardImageWithBanner(final ApplicationInfo appInfo) {
        new AsyncTask<Void, Void, Drawable>() {
            private String mLoadTvInputId = AppLinkCardView.this.mChannel.mInputServiceName;

            /* access modifiers changed from: protected */
            public Drawable doInBackground(Void... params) {
                if (AppLinkCardView.this.mIntent == null) {
                    return null;
                }
                try {
                    Drawable banner = AppLinkCardView.this.mPackageManager.getActivityBanner(AppLinkCardView.this.mIntent);
                    if (banner == null) {
                        return AppLinkCardView.this.mPackageManager.getActivityIcon(AppLinkCardView.this.mIntent);
                    }
                    return banner;
                } catch (PackageManager.NameNotFoundException e) {
                    return null;
                }
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Drawable banner) {
                if (!this.mLoadTvInputId.equals(AppLinkCardView.this.mChannel.mInputServiceName) && AppLinkCardView.this.isAttachedToWindow()) {
                    if (banner != null) {
                        AppLinkCardView.this.setCardImageWithBannerInternal(banner);
                    } else {
                        AppLinkCardView.this.setCardImageWithApplicationInfoBanner(appInfo);
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* access modifiers changed from: private */
    public void setCardImageWithApplicationInfoBanner(final ApplicationInfo appInfo) {
        Drawable appBanner = TvSingletons.getSingletons().getTvInputManagerHelper().getTvInputApplicationBanner(this.mChannel.mInputServiceName);
        if (appBanner != null) {
            setCardImageWithBannerInternal(appBanner);
        } else {
            new AsyncTask<Void, Void, Drawable>() {
                private final String mLoadTvInputId = AppLinkCardView.this.mChannel.mInputServiceName;

                /* access modifiers changed from: protected */
                public Drawable doInBackground(Void... params) {
                    Drawable banner = null;
                    if (appInfo == null) {
                        return null;
                    }
                    if (appInfo.banner != 0) {
                        banner = AppLinkCardView.this.mPackageManager.getApplicationBanner(appInfo);
                    }
                    if (banner != null || appInfo.icon == 0) {
                        return banner;
                    }
                    return AppLinkCardView.this.mPackageManager.getApplicationIcon(appInfo);
                }

                /* access modifiers changed from: protected */
                public void onPostExecute(Drawable banner) {
                    TvSingletons.getSingletons().getTvInputManagerHelper().setTvInputApplicationBanner(this.mLoadTvInputId, banner);
                    if (TextUtils.equals(this.mLoadTvInputId, AppLinkCardView.this.mChannel.mInputServiceName) && AppLinkCardView.this.isAttachedToWindow()) {
                        AppLinkCardView.this.setCardImageWithBannerInternal(banner);
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    /* access modifiers changed from: private */
    public void setCardImageWithBannerInternal(Drawable banner) {
        if (banner == null) {
            this.mImageView.setImageDrawable(this.mDefaultDrawable);
            this.mImageView.setBackgroundResource(R.color.channel_card);
            return;
        }
        Bitmap bitmap = Bitmap.createBitmap(this.mCardImageWidth, this.mCardImageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        banner.setBounds(0, 0, this.mCardImageWidth, this.mCardImageHeight);
        banner.draw(canvas);
        this.mImageView.setImageDrawable(banner);
        this.mImageView.setForeground(getContext().getDrawable(R.drawable.card_image_gradient));
        if (this.mChannel.mAppLinkColor == 0) {
            extractAndSetMetaViewBackgroundColor(bitmap);
        }
    }

    private void extractAndSetMetaViewBackgroundColor(Bitmap bitmap) {
        new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                AppLinkCardView.this.mMetaViewHolder.setBackgroundColor(palette.getDarkVibrantColor(AppLinkCardView.this.getResources().getColor(R.color.channel_card_meta_background, (Resources.Theme) null)));
            }
        });
    }
}
