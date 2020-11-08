package com.mediatek.wwtv.tvcenter.util;

public class CountryConfigEntry {
    public String audio_lang;
    public String audio_lang_2nd;
    public int decoding_page_lang = -1;
    public String digital_subtitle_lang;
    public String digital_subtitle_lang_2nd;
    public int digital_ttx_Lang = -1;
    public String dvbc_operators;
    public String dvbs_operators;
    public String time_zone;

    public String toString() {
        return "CountryConfigEntry [time_zone=" + this.time_zone + ", digital_subtitle_lang=" + this.digital_subtitle_lang + ", digital_subtitle_lang_2nd=" + this.digital_subtitle_lang_2nd + ", audio_lang=" + this.audio_lang + ", audio_lang_2nd=" + this.audio_lang_2nd + ", digital_ttx_Lang=" + this.digital_ttx_Lang + ", decoding_page_lang=" + this.decoding_page_lang + ", dvbc_operators=" + this.dvbc_operators + ", dvbs_operators=" + this.dvbs_operators + "]";
    }
}
