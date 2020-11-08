package com.android.tv.parental;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.tv.TvContentRating;
import android.text.TextUtils;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ContentRatingSystem {
    private static final String DELIMITER = "/";
    public static final Comparator<ContentRatingSystem> DISPLAY_NAME_COMPARATOR = $$Lambda$ContentRatingSystem$yjyLZRXUKMr7p4To7QE869hbi0M.INSTANCE;
    private final List<String> mCountries;
    private final String mDescription;
    private final String mDisplayName;
    private final String mDomain;
    private final boolean mIsCustom;
    private final String mName;
    private final List<Order> mOrders;
    private final List<Rating> mRatings;
    private final List<SubRating> mSubRatings;
    private final String mTitle;

    public String getId() {
        return this.mDomain + DELIMITER + this.mName;
    }

    public String getName() {
        return this.mName;
    }

    public String getDomain() {
        return this.mDomain;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public List<String> getCountries() {
        return this.mCountries;
    }

    public List<Rating> getRatings() {
        return this.mRatings;
    }

    public Rating getRating(String name) {
        for (Rating rating : this.mRatings) {
            if (TextUtils.equals(rating.getName(), name)) {
                return rating;
            }
        }
        return null;
    }

    public List<SubRating> getSubRatings() {
        return this.mSubRatings;
    }

    public List<Order> getOrders() {
        return this.mOrders;
    }

    public String getDisplayName() {
        return this.mDisplayName;
    }

    public boolean isCustom() {
        return this.mIsCustom;
    }

    public boolean ownsRating(TvContentRating rating) {
        return this.mDomain.equals(rating.getDomain()) && this.mName.equals(rating.getRatingSystem());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ContentRatingSystem)) {
            return false;
        }
        ContentRatingSystem other = (ContentRatingSystem) obj;
        if (!this.mName.equals(other.mName) || !this.mDomain.equals(other.mDomain)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (31 * this.mName.hashCode()) + this.mDomain.hashCode();
    }

    private ContentRatingSystem(String name, String domain, String title, String description, List<String> countries, String displayName, List<Rating> ratings, List<SubRating> subRatings, List<Order> orders, boolean isCustom) {
        this.mName = name;
        this.mDomain = domain;
        this.mTitle = title;
        this.mDescription = description;
        this.mCountries = countries;
        this.mDisplayName = displayName;
        this.mRatings = ratings;
        this.mSubRatings = subRatings;
        this.mOrders = orders;
        this.mIsCustom = isCustom;
    }

    public static class Builder {
        private final Context mContext;
        private List<String> mCountries;
        private String mDescription;
        private String mDomain;
        private boolean mIsCustom;
        private String mName;
        private final List<Order.Builder> mOrderBuilders = new ArrayList();
        private final List<Rating.Builder> mRatingBuilders = new ArrayList();
        private final List<SubRating.Builder> mSubRatingBuilders = new ArrayList();
        private String mTitle;

        public Builder(Context context) {
            this.mContext = context;
        }

        public void setName(String name) {
            this.mName = name;
        }

        public void setDomain(String domain) {
            this.mDomain = domain;
        }

        public void setTitle(String title) {
            this.mTitle = title;
        }

        public void setDescription(String description) {
            this.mDescription = description;
        }

        public void addCountry(String country) {
            if (this.mCountries == null) {
                this.mCountries = new ArrayList();
            }
            this.mCountries.add(new Locale("", country).getCountry());
        }

        public void addRatingBuilder(Rating.Builder ratingBuilder) {
            this.mRatingBuilders.add(ratingBuilder);
        }

        public void addSubRatingBuilder(SubRating.Builder subRatingBuilder) {
            this.mSubRatingBuilders.add(subRatingBuilder);
        }

        public void addOrderBuilder(Order.Builder orderBuilder) {
            this.mOrderBuilders.add(orderBuilder);
        }

        public void setIsCustom(boolean isCustom) {
            this.mIsCustom = isCustom;
        }

        public ContentRatingSystem build() {
            if (TextUtils.isEmpty(this.mName)) {
                throw new IllegalArgumentException("Name cannot be empty");
            } else if (!TextUtils.isEmpty(this.mDomain)) {
                StringBuilder sb = new StringBuilder();
                if (this.mCountries != null) {
                    if (this.mCountries.size() == 1) {
                        sb.append(new Locale("", this.mCountries.get(0)).getDisplayCountry());
                    } else if (this.mCountries.size() > 1) {
                        Locale locale = Locale.getDefault();
                        if (!this.mCountries.contains(locale.getCountry())) {
                            sb.append(this.mContext.getString(R.string.other_countries));
                        } else if (!CommonIntegration.isUSRegion() || !"US".equals(locale.getCountry()) || !this.mCountries.contains("CA")) {
                            sb.append(locale.getDisplayCountry());
                        } else {
                            sb.append(new Locale("", this.mCountries.get(this.mCountries.indexOf("CA"))).getDisplayCountry());
                        }
                    }
                }
                if (!TextUtils.isEmpty(this.mTitle)) {
                    sb.append(" (");
                    sb.append(this.mTitle);
                    sb.append(")");
                }
                String displayName = sb.toString();
                List<SubRating> subRatings = new ArrayList<>();
                if (this.mSubRatingBuilders != null) {
                    for (SubRating.Builder builder : this.mSubRatingBuilders) {
                        subRatings.add(builder.build());
                    }
                }
                if (this.mRatingBuilders.size() > 0) {
                    ArrayList arrayList = new ArrayList();
                    for (Rating.Builder builder2 : this.mRatingBuilders) {
                        arrayList.add(builder2.build(subRatings));
                    }
                    for (SubRating subRating : subRatings) {
                        boolean used = false;
                        Iterator it = arrayList.iterator();
                        while (true) {
                            if (it.hasNext()) {
                                if (((Rating) it.next()).getSubRatings().contains(subRating)) {
                                    used = true;
                                    continue;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        if (!used) {
                            throw new IllegalArgumentException("Subrating " + subRating.getName() + " isn't used by any rating");
                        }
                    }
                    ArrayList arrayList2 = new ArrayList();
                    if (this.mOrderBuilders != null) {
                        for (Order.Builder builder3 : this.mOrderBuilders) {
                            arrayList2.add(builder3.build(arrayList));
                        }
                    }
                    ArrayList arrayList3 = arrayList2;
                    ArrayList arrayList4 = arrayList;
                    return new ContentRatingSystem(this.mName, this.mDomain, this.mTitle, this.mDescription, this.mCountries, displayName, arrayList, subRatings, arrayList2, this.mIsCustom);
                }
                throw new IllegalArgumentException("Rating isn't available.");
            } else {
                throw new IllegalArgumentException("Domain cannot be empty");
            }
        }
    }

    public static class Rating implements Comparable<Rating> {
        private final int mContentAgeHint;
        private final String mDescription;
        private final Drawable mIcon;
        private final String mName;
        private final List<SubRating> mSubRatings;
        private final String mTitle;

        public String getName() {
            return this.mName;
        }

        public String getTitle() {
            return this.mTitle;
        }

        public String getDescription() {
            return this.mDescription;
        }

        public Drawable getIcon() {
            return this.mIcon;
        }

        public int getAgeHint() {
            return this.mContentAgeHint;
        }

        public List<SubRating> getSubRatings() {
            return this.mSubRatings;
        }

        public int compareTo(Rating rating) {
            if (this.mContentAgeHint - rating.mContentAgeHint > 0) {
                return -1;
            }
            if (this.mContentAgeHint - rating.mContentAgeHint == 0) {
                return 0;
            }
            return 1;
        }

        private Rating(String name, String title, String description, Drawable icon, int contentAgeHint, List<SubRating> subRatings) {
            this.mName = name;
            this.mTitle = title;
            this.mDescription = description;
            this.mIcon = icon;
            this.mContentAgeHint = contentAgeHint;
            this.mSubRatings = subRatings;
        }

        public static class Builder {
            private int mContentAgeHint = -1;
            private String mDescription;
            private Drawable mIcon;
            private String mName;
            private final List<String> mSubRatingNames = new ArrayList();
            private String mTitle;

            public void setName(String name) {
                this.mName = name;
            }

            public void setTitle(String title) {
                this.mTitle = title;
            }

            public void setDescription(String description) {
                this.mDescription = description;
            }

            public void setIcon(Drawable icon) {
                this.mIcon = icon;
            }

            public void setContentAgeHint(int contentAgeHint) {
                this.mContentAgeHint = contentAgeHint;
            }

            public void addSubRatingName(String subRatingName) {
                this.mSubRatingNames.add(subRatingName);
            }

            /* access modifiers changed from: private */
            public Rating build(List<SubRating> allDefinedSubRatings) {
                if (TextUtils.isEmpty(this.mName)) {
                    throw new IllegalArgumentException("A rating should have non-empty name");
                } else if (allDefinedSubRatings == null && this.mSubRatingNames.size() > 0) {
                    throw new IllegalArgumentException("Invalid subrating for rating " + this.mName);
                } else if (this.mContentAgeHint >= 0) {
                    List<SubRating> subRatings = new ArrayList<>();
                    for (String subRatingId : this.mSubRatingNames) {
                        boolean found = false;
                        Iterator<SubRating> it = allDefinedSubRatings.iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            SubRating subRating = it.next();
                            if (subRatingId.equals(subRating.getName())) {
                                found = true;
                                subRatings.add(subRating);
                                continue;
                                break;
                            }
                        }
                        if (!found) {
                            throw new IllegalArgumentException("Unknown subrating name " + subRatingId + " in rating " + this.mName);
                        }
                    }
                    return new Rating(this.mName, this.mTitle, this.mDescription, this.mIcon, this.mContentAgeHint, subRatings);
                } else {
                    throw new IllegalArgumentException("Rating " + this.mName + " should define non-negative contentAgeHint");
                }
            }
        }
    }

    public static class SubRating {
        private final String mDescription;
        private final Drawable mIcon;
        private final String mName;
        private final String mTitle;

        public String getName() {
            return this.mName;
        }

        public String getTitle() {
            return this.mTitle;
        }

        public String getDescription() {
            return this.mDescription;
        }

        public Drawable getIcon() {
            return this.mIcon;
        }

        private SubRating(String name, String title, String description, Drawable icon) {
            this.mName = name;
            this.mTitle = title;
            this.mDescription = description;
            this.mIcon = icon;
        }

        public boolean equals(Object sub) {
            if (this.mName == null || sub == null || !(sub instanceof SubRating)) {
                return false;
            }
            return this.mName.equals(((SubRating) sub).getName());
        }

        public static class Builder {
            private String mDescription;
            private Drawable mIcon;
            private String mName;
            private String mTitle;

            public void setName(String name) {
                this.mName = name;
            }

            public void setTitle(String title) {
                this.mTitle = title;
            }

            public void setDescription(String description) {
                this.mDescription = description;
            }

            public void setIcon(Drawable icon) {
                this.mIcon = icon;
            }

            /* access modifiers changed from: private */
            public SubRating build() {
                if (!TextUtils.isEmpty(this.mName)) {
                    return new SubRating(this.mName, this.mTitle, this.mDescription, this.mIcon);
                }
                throw new IllegalArgumentException("A subrating should have non-empty name");
            }
        }
    }

    public static class Order {
        private final List<Rating> mRatingOrder;

        public List<Rating> getRatingOrder() {
            return this.mRatingOrder;
        }

        private Order(List<Rating> ratingOrder) {
            this.mRatingOrder = ratingOrder;
        }

        public int getRatingIndex(Rating rating) {
            for (int i = 0; i < this.mRatingOrder.size(); i++) {
                if (this.mRatingOrder.get(i).getName().equals(rating.getName())) {
                    return i;
                }
            }
            return -1;
        }

        public static class Builder {
            private final List<String> mRatingNames = new ArrayList();

            /* access modifiers changed from: private */
            public Order build(List<Rating> ratings) {
                List<Rating> ratingOrder = new ArrayList<>();
                for (String ratingName : this.mRatingNames) {
                    boolean found = false;
                    Iterator<Rating> it = ratings.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        Rating rating = it.next();
                        if (ratingName.equals(rating.getName())) {
                            found = true;
                            ratingOrder.add(rating);
                            continue;
                            break;
                        }
                    }
                    if (!found) {
                        throw new IllegalArgumentException("Unknown rating " + ratingName + " in rating-order tag");
                    }
                }
                return new Order(ratingOrder);
            }

            public void addRatingName(String name) {
                this.mRatingNames.add(name);
            }
        }
    }
}
