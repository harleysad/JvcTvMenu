package com.mediatek.wwtv.tvcenter.search;

import com.mediatek.wwtv.tvcenter.search.LocalSearchProvider;
import java.util.List;

public interface SearchInterface {
    public static final int ACTION_TYPE_AMBIGUOUS = 1;
    public static final int ACTION_TYPE_SWITCH_CHANNEL = 2;
    public static final int ACTION_TYPE_SWITCH_INPUT = 3;
    public static final String SOURCE_TV_SEARCH = "TvSearch";

    List<LocalSearchProvider.SearchResult> search(String str, int i, int i2);
}
