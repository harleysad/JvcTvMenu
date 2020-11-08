package com.mediatek.twoworlds.tv.model;

import java.util.HashMap;
import java.util.Map;

public class MtkTvEventConverBroadcastGene {
    private Map<String, String> mEuBroadcastGeneMap = new HashMap();
    private Map<String, String> mSaBroadcastGeneMap = new HashMap();

    private void initEuBroadcastGeneMap() {
        this.mEuBroadcastGeneMap.put(MtkTvRatingConvert2Goo.RATING_STR_10, "movie/drama");
        this.mEuBroadcastGeneMap.put(MtkTvRatingConvert2Goo.RATING_STR_11, "detective/thriller");
        this.mEuBroadcastGeneMap.put(MtkTvRatingConvert2Goo.RATING_STR_12, "adventure/western/war");
        this.mEuBroadcastGeneMap.put("13", "science fiction/fantasy/horror");
        this.mEuBroadcastGeneMap.put("14", "comedy");
        this.mEuBroadcastGeneMap.put(MtkTvRatingConvert2Goo.RATING_STR_15, "soap/melodrama/folkloric");
        this.mEuBroadcastGeneMap.put("16", "romance");
        this.mEuBroadcastGeneMap.put(MtkTvRatingConvert2Goo.RATING_STR_17, "serious/classical/religious/historical movie/drama");
        this.mEuBroadcastGeneMap.put("18", "adult movie/drama");
        this.mEuBroadcastGeneMap.put(MtkTvRatingConvert2Goo.RATING_STR_20, "news/current affairs");
        this.mEuBroadcastGeneMap.put("21", "news/weather report");
        this.mEuBroadcastGeneMap.put("22", "news magazine");
        this.mEuBroadcastGeneMap.put("23", "documentary");
        this.mEuBroadcastGeneMap.put("24", "discussion/interview/debate");
        this.mEuBroadcastGeneMap.put("30", "show/game show");
        this.mEuBroadcastGeneMap.put("31", "game show/quiz/contes");
        this.mEuBroadcastGeneMap.put("32", "variety show");
        this.mEuBroadcastGeneMap.put("33", "talk show");
        this.mEuBroadcastGeneMap.put("40", "sports");
        this.mEuBroadcastGeneMap.put("41", "special events (Olympic Games, World Cup etc.)");
        this.mEuBroadcastGeneMap.put("42", "sports magazines");
        this.mEuBroadcastGeneMap.put("43", "football/soccer");
        this.mEuBroadcastGeneMap.put("44", "tennis/squash");
        this.mEuBroadcastGeneMap.put("45", "team sports (excluding football)");
        this.mEuBroadcastGeneMap.put("46", "athletics");
        this.mEuBroadcastGeneMap.put("47", "motor sport");
        this.mEuBroadcastGeneMap.put("48", "water sport");
        this.mEuBroadcastGeneMap.put("49", "winter sports");
        this.mEuBroadcastGeneMap.put("4a", "equestrian");
        this.mEuBroadcastGeneMap.put("4b", "martial sports");
        this.mEuBroadcastGeneMap.put("50", "children's/youth programmes");
        this.mEuBroadcastGeneMap.put("51", "pre-school children's programmes");
        this.mEuBroadcastGeneMap.put("52", "entertainment programmes for 6 to14");
        this.mEuBroadcastGeneMap.put("53", "entertainment programmes for 10 to 16");
        this.mEuBroadcastGeneMap.put("54", "informational/educational/school programmes");
        this.mEuBroadcastGeneMap.put("55", "cartoons/puppets");
        this.mEuBroadcastGeneMap.put("60", "music/ballet/dance");
        this.mEuBroadcastGeneMap.put("61", "rock/pop");
        this.mEuBroadcastGeneMap.put("62", "serious music/classical music");
        this.mEuBroadcastGeneMap.put("63", "folk/traditional music");
        this.mEuBroadcastGeneMap.put("64", "jazz");
        this.mEuBroadcastGeneMap.put("65", "musical/opera");
        this.mEuBroadcastGeneMap.put("66", "ballet");
        this.mEuBroadcastGeneMap.put("70", "arts/culture");
        this.mEuBroadcastGeneMap.put("71", "performing arts");
        this.mEuBroadcastGeneMap.put("72", "fine arts");
        this.mEuBroadcastGeneMap.put("73", "religion");
        this.mEuBroadcastGeneMap.put("74", "popular culture/traditional arts");
        this.mEuBroadcastGeneMap.put("75", "literature");
        this.mEuBroadcastGeneMap.put("76", "film/cinema");
        this.mEuBroadcastGeneMap.put("77", "experimental film/video");
        this.mEuBroadcastGeneMap.put("78", "broadcasting/press");
        this.mEuBroadcastGeneMap.put("79", "new media");
        this.mEuBroadcastGeneMap.put("7a", "arts/culture magazines");
        this.mEuBroadcastGeneMap.put("7b", "fashion");
        this.mEuBroadcastGeneMap.put("80", "social/political issues/economics");
        this.mEuBroadcastGeneMap.put("81", "magazines/reports/documentary");
        this.mEuBroadcastGeneMap.put("82", "economics/social advisory");
        this.mEuBroadcastGeneMap.put("83", "remarkable people");
        this.mEuBroadcastGeneMap.put("90", "education/science/factual topics");
        this.mEuBroadcastGeneMap.put("91", "nature/animals/environment");
        this.mEuBroadcastGeneMap.put("92", "technology/natural sciences");
        this.mEuBroadcastGeneMap.put("93", "medicine/physiology/psychology");
        this.mEuBroadcastGeneMap.put("94", "foreign countries/expeditions");
        this.mEuBroadcastGeneMap.put("95", "social/spiritual sciences");
        this.mEuBroadcastGeneMap.put("96", "further education");
        this.mEuBroadcastGeneMap.put("97", "languages");
        this.mEuBroadcastGeneMap.put("a0", "leisure hobbies");
        this.mEuBroadcastGeneMap.put("a1", "tourism/travel");
        this.mEuBroadcastGeneMap.put("a2", "handicraft");
        this.mEuBroadcastGeneMap.put("a3", "motoring");
        this.mEuBroadcastGeneMap.put("a4", "fitness &amp; health");
        this.mEuBroadcastGeneMap.put("a5", "cooking");
        this.mEuBroadcastGeneMap.put("a6", "advertisement/shopping");
        this.mEuBroadcastGeneMap.put("a7", "gardening");
        this.mEuBroadcastGeneMap.put("b0", "original language");
        this.mEuBroadcastGeneMap.put("b1", "black &amp; white");
        this.mEuBroadcastGeneMap.put("b2", "unpublished");
        this.mEuBroadcastGeneMap.put("b3", "live broadcast");
    }

    private void initSaBroadcastGeneMap() {
        this.mSaBroadcastGeneMap.put("00", "Regular, general");
        this.mSaBroadcastGeneMap.put("01", "Weather report");
        this.mSaBroadcastGeneMap.put("02", "Special program, documentary");
        this.mSaBroadcastGeneMap.put("03", "Politics, national assembly");
        this.mSaBroadcastGeneMap.put("04", "Economics, market report");
        this.mSaBroadcastGeneMap.put("05", "Overseas, international report");
        this.mSaBroadcastGeneMap.put("06", "News analysis");
        this.mSaBroadcastGeneMap.put("07", "Discussion, conference");
        this.mSaBroadcastGeneMap.put("08", "Special report");
        this.mSaBroadcastGeneMap.put("09", "Local program");
        this.mSaBroadcastGeneMap.put("0a", "Traffic report");
        this.mSaBroadcastGeneMap.put(MtkTvRatingConvert2Goo.RATING_STR_10, "Sports news");
        this.mSaBroadcastGeneMap.put(MtkTvRatingConvert2Goo.RATING_STR_11, "Baseball");
        this.mSaBroadcastGeneMap.put(MtkTvRatingConvert2Goo.RATING_STR_12, "Soccer");
        this.mSaBroadcastGeneMap.put("13", "Golf");
        this.mSaBroadcastGeneMap.put("14", "Other ball games");
        this.mSaBroadcastGeneMap.put(MtkTvRatingConvert2Goo.RATING_STR_15, "Sumo, combative sports");
        this.mSaBroadcastGeneMap.put("16", "Olympic, international games");
        this.mSaBroadcastGeneMap.put(MtkTvRatingConvert2Goo.RATING_STR_17, "Marathon, athletic sports, swimming");
        this.mSaBroadcastGeneMap.put("18", "Motor sports");
        this.mSaBroadcastGeneMap.put(MtkTvRatingConvert2Goo.RATING_STR_19, "Marine sports, winter sports");
        this.mSaBroadcastGeneMap.put("1a", "Horse race, public race");
        this.mSaBroadcastGeneMap.put(MtkTvRatingConvert2Goo.RATING_STR_20, "Gossip/tabloid show");
        this.mSaBroadcastGeneMap.put("21", "Fashion");
        this.mSaBroadcastGeneMap.put("22", "Living, home");
        this.mSaBroadcastGeneMap.put("23", "Health, medical treatment");
        this.mSaBroadcastGeneMap.put("24", "Shopping, mail-order business");
        this.mSaBroadcastGeneMap.put("25", "Gourmet, cocking");
        this.mSaBroadcastGeneMap.put("26", "Events");
        this.mSaBroadcastGeneMap.put("27", "Hobby/Education");
        this.mSaBroadcastGeneMap.put("30", "Japanese dramas");
        this.mSaBroadcastGeneMap.put("31", "Overseas dramas");
        this.mSaBroadcastGeneMap.put("32", "Period dramas");
        this.mSaBroadcastGeneMap.put("40", "Japanese rock, pop music");
        this.mSaBroadcastGeneMap.put("41", "Overseas rock, pop music");
        this.mSaBroadcastGeneMap.put("42", "Classic, opera");
        this.mSaBroadcastGeneMap.put("43", "Jazz, fusion");
        this.mSaBroadcastGeneMap.put("44", "Popular songs, Japanese popular songs (enka songs)");
        this.mSaBroadcastGeneMap.put("45", "Live concernt");
        this.mSaBroadcastGeneMap.put("46", "Rankng, request music");
        this.mSaBroadcastGeneMap.put("47", "Karaoke, amateur singing contests");
        this.mSaBroadcastGeneMap.put("48", "Japanese ballad, Japanese traditional music");
        this.mSaBroadcastGeneMap.put("49", "Children's song");
        this.mSaBroadcastGeneMap.put("4a", "Folk music, world music");
        this.mSaBroadcastGeneMap.put("50", "Quiz");
        this.mSaBroadcastGeneMap.put("51", "Game");
        this.mSaBroadcastGeneMap.put("52", "Talk variety");
        this.mSaBroadcastGeneMap.put("53", "Comedy program");
        this.mSaBroadcastGeneMap.put("54", "Music variety");
        this.mSaBroadcastGeneMap.put("55", "Tour variety");
        this.mSaBroadcastGeneMap.put("56", "Cocking variety");
        this.mSaBroadcastGeneMap.put("60", "Overseas movies");
        this.mSaBroadcastGeneMap.put("61", "Japanese movies");
        this.mSaBroadcastGeneMap.put("62", "Animation");
        this.mSaBroadcastGeneMap.put("70", "Japanese animation");
        this.mSaBroadcastGeneMap.put("71", "Overseas animation");
        this.mSaBroadcastGeneMap.put("72", "Special effects");
        this.mSaBroadcastGeneMap.put("90", "Modern drama, Western-style drama");
        this.mSaBroadcastGeneMap.put("91", "Musical");
        this.mSaBroadcastGeneMap.put("92", "Dance, Ballet");
        this.mSaBroadcastGeneMap.put("93", "Comic story, Entertainment");
        this.mSaBroadcastGeneMap.put("94", "Kabuki, Classical drama");
        this.mSaBroadcastGeneMap.put("a0", "Trip, fishing, outdoor entertainment");
        this.mSaBroadcastGeneMap.put("a1", "Gardening, pet, handicrafts");
        this.mSaBroadcastGeneMap.put("a2", "Music, art, industrial art");
        this.mSaBroadcastGeneMap.put("a3", "Japanese chess (shogi) and \"go\"");
        this.mSaBroadcastGeneMap.put("a4", "Mah-jong, pinball games");
        this.mSaBroadcastGeneMap.put("a6", "Computer, TV games");
        this.mSaBroadcastGeneMap.put("a7", "Conversation, languages");
        this.mSaBroadcastGeneMap.put("a8", "Little children, schoolchildren");
        this.mSaBroadcastGeneMap.put("a9", "Junior high school and high school students");
        this.mSaBroadcastGeneMap.put("aa", "University students, examinations");
        this.mSaBroadcastGeneMap.put("ab", "Lifelong education, qualifications");
        this.mSaBroadcastGeneMap.put("ac", "Educational problem");
    }

    public MtkTvEventConverBroadcastGene() {
        initEuBroadcastGeneMap();
        initSaBroadcastGeneMap();
    }

    public String getEUBroadcastGene(String catogry_value) {
        return this.mEuBroadcastGeneMap.get(catogry_value);
    }

    public String getSABroadcastGene(String catogry_value) {
        return this.mSaBroadcastGeneMap.get(catogry_value);
    }
}
