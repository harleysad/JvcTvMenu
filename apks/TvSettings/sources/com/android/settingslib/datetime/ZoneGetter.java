package com.android.settingslib.datetime;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.icu.text.TimeZoneFormat;
import android.icu.text.TimeZoneNames;
import android.support.annotation.VisibleForTesting;
import android.support.v4.text.BidiFormatter;
import android.support.v4.text.TextDirectionHeuristicsCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.util.Log;
import com.android.settingslib.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import libcore.util.TimeZoneFinder;
import org.xmlpull.v1.XmlPullParserException;

public class ZoneGetter {
    @Deprecated
    public static final String KEY_DISPLAYNAME = "name";
    public static final String KEY_DISPLAY_LABEL = "display_label";
    @Deprecated
    public static final String KEY_GMT = "gmt";
    public static final String KEY_ID = "id";
    public static final String KEY_OFFSET = "offset";
    public static final String KEY_OFFSET_LABEL = "offset_label";
    private static final String TAG = "ZoneGetter";
    private static final String XMLTAG_TIMEZONE = "timezone";

    public static CharSequence getTimeZoneOffsetAndName(Context context, TimeZone tz, Date now) {
        Locale locale = context.getResources().getConfiguration().locale;
        CharSequence gmtText = getGmtOffsetText(TimeZoneFormat.getInstance(locale), locale, tz, now);
        String zoneNameString = getZoneLongName(TimeZoneNames.getInstance(locale), tz, now);
        if (zoneNameString == null) {
            return gmtText;
        }
        return TextUtils.concat(new CharSequence[]{gmtText, " ", zoneNameString});
    }

    public static List<Map<String, Object>> getZonesList(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        Date now = new Date();
        TimeZoneNames timeZoneNames = TimeZoneNames.getInstance(locale);
        ZoneGetterData data = new ZoneGetterData(context);
        boolean useExemplarLocationForLocalNames = shouldUseExemplarLocationForLocalNames(data, timeZoneNames);
        List<Map<String, Object>> zones = new ArrayList<>();
        for (int i = 0; i < data.zoneCount; i++) {
            TimeZone tz = data.timeZones[i];
            CharSequence gmtOffsetText = data.gmtOffsetTexts[i];
            CharSequence displayName = getTimeZoneDisplayName(data, timeZoneNames, useExemplarLocationForLocalNames, tz, data.olsonIdsToDisplay[i]);
            if (TextUtils.isEmpty(displayName)) {
                displayName = gmtOffsetText;
            }
            zones.add(createDisplayEntry(tz, gmtOffsetText, displayName, tz.getOffset(now.getTime())));
        }
        return zones;
    }

    private static Map<String, Object> createDisplayEntry(TimeZone tz, CharSequence gmtOffsetText, CharSequence displayName, int offsetMillis) {
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_ID, tz.getID());
        map.put(KEY_DISPLAYNAME, displayName.toString());
        map.put(KEY_DISPLAY_LABEL, displayName);
        map.put(KEY_GMT, gmtOffsetText.toString());
        map.put(KEY_OFFSET_LABEL, gmtOffsetText);
        map.put(KEY_OFFSET, Integer.valueOf(offsetMillis));
        return map;
    }

    /* access modifiers changed from: private */
    public static List<String> readTimezonesToDisplay(Context context) {
        XmlResourceParser xrp;
        List<String> olsonIds = new ArrayList<>();
        try {
            xrp = context.getResources().getXml(R.xml.timezones);
            while (xrp.next() != 2) {
            }
            xrp.next();
            while (xrp.getEventType() != 3) {
                while (xrp.getEventType() != 2) {
                    if (xrp.getEventType() == 1) {
                        if (xrp != null) {
                            xrp.close();
                        }
                        return olsonIds;
                    }
                    xrp.next();
                }
                if (xrp.getName().equals(XMLTAG_TIMEZONE)) {
                    olsonIds.add(xrp.getAttributeValue(0));
                }
                while (xrp.getEventType() != 3) {
                    xrp.next();
                }
                xrp.next();
            }
            if (xrp != null) {
                xrp.close();
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Ill-formatted timezones.xml file");
        } catch (IOException e2) {
            Log.e(TAG, "Unable to read timezones.xml file");
        } catch (Throwable th) {
            r2.addSuppressed(th);
        }
        return olsonIds;
        throw th;
    }

    private static boolean shouldUseExemplarLocationForLocalNames(ZoneGetterData data, TimeZoneNames timeZoneNames) {
        Set<CharSequence> localZoneNames = new HashSet<>();
        Date now = new Date();
        for (int i = 0; i < data.zoneCount; i++) {
            if (data.localZoneIds.contains(data.olsonIdsToDisplay[i])) {
                CharSequence displayName = getZoneLongName(timeZoneNames, data.timeZones[i], now);
                if (displayName == null) {
                    displayName = data.gmtOffsetTexts[i];
                }
                if (!localZoneNames.add(displayName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static CharSequence getTimeZoneDisplayName(ZoneGetterData data, TimeZoneNames timeZoneNames, boolean useExemplarLocationForLocalNames, TimeZone tz, String olsonId) {
        Date now = new Date();
        if (data.localZoneIds.contains(olsonId) && !useExemplarLocationForLocalNames) {
            return getZoneLongName(timeZoneNames, tz, now);
        }
        String canonicalZoneId = android.icu.util.TimeZone.getCanonicalID(tz.getID());
        if (canonicalZoneId == null) {
            canonicalZoneId = tz.getID();
        }
        String displayName = timeZoneNames.getExemplarLocationName(canonicalZoneId);
        if (displayName == null || displayName.isEmpty()) {
            return getZoneLongName(timeZoneNames, tz, now);
        }
        return displayName;
    }

    private static String getZoneLongName(TimeZoneNames names, TimeZone tz, Date now) {
        TimeZoneNames.NameType nameType;
        if (tz.inDaylightTime(now)) {
            nameType = TimeZoneNames.NameType.LONG_DAYLIGHT;
        } else {
            nameType = TimeZoneNames.NameType.LONG_STANDARD;
        }
        return names.getDisplayName(tz.getID(), nameType, now.getTime());
    }

    private static void appendWithTtsSpan(SpannableStringBuilder builder, CharSequence content, TtsSpan span) {
        int start = builder.length();
        builder.append(content);
        builder.setSpan(span, start, builder.length(), 0);
    }

    private static String formatDigits(int input, int minDigits, String localizedDigits) {
        int tens = input / 10;
        int units = input % 10;
        StringBuilder builder = new StringBuilder(minDigits);
        if (input >= 10 || minDigits == 2) {
            builder.append(localizedDigits.charAt(tens));
        }
        builder.append(localizedDigits.charAt(units));
        return builder.toString();
    }

    public static CharSequence getGmtOffsetText(TimeZoneFormat tzFormatter, Locale locale, TimeZone tz, Date now) {
        String gmtPatternSuffix;
        String gmtPatternPrefix;
        TimeZoneFormat.GMTOffsetPatternType patternType;
        boolean negative;
        int offsetMinutes;
        int placeholderIndex;
        int numDigits;
        String unit;
        int number;
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String gmtPattern = tzFormatter.getGMTPattern();
        int placeholderIndex2 = gmtPattern.indexOf("{0}");
        if (placeholderIndex2 == -1) {
            gmtPatternPrefix = "GMT";
            gmtPatternSuffix = "";
        } else {
            gmtPatternPrefix = gmtPattern.substring(0, placeholderIndex2);
            gmtPatternSuffix = gmtPattern.substring(placeholderIndex2 + 3);
        }
        if (!gmtPatternPrefix.isEmpty()) {
            appendWithTtsSpan(builder, gmtPatternPrefix, new TtsSpan.TextBuilder(gmtPatternPrefix).build());
        }
        int offsetMillis = tz.getOffset(now.getTime());
        boolean negative2 = offsetMillis < 0;
        if (negative2) {
            offsetMillis = -offsetMillis;
            patternType = TimeZoneFormat.GMTOffsetPatternType.NEGATIVE_HM;
        } else {
            patternType = TimeZoneFormat.GMTOffsetPatternType.POSITIVE_HM;
        }
        String gmtOffsetPattern = tzFormatter.getGMTOffsetPattern(patternType);
        String localizedDigits = tzFormatter.getGMTOffsetDigits();
        int offsetHours = (int) (((long) offsetMillis) / 3600000);
        String str = gmtPatternPrefix;
        int offsetMinutes2 = (int) (((long) offsetMillis) / 60000);
        int offsetMinutesRemaining = Math.abs(offsetMinutes2) % 60;
        int i = 0;
        while (i < gmtOffsetPattern.length()) {
            char c = gmtOffsetPattern.charAt(i);
            String gmtPattern2 = gmtPattern;
            if (c == '+' || c == '-') {
                placeholderIndex = placeholderIndex2;
                offsetMinutes = offsetMinutes2;
                negative = negative2;
            } else if (c == 8722) {
                placeholderIndex = placeholderIndex2;
                offsetMinutes = offsetMinutes2;
                negative = negative2;
            } else if (c == 'H' || c == 'm') {
                placeholderIndex = placeholderIndex2;
                if (i + 1 >= gmtOffsetPattern.length() || gmtOffsetPattern.charAt(i + 1) != c) {
                    numDigits = 1;
                } else {
                    numDigits = 2;
                    i++;
                }
                if (c == 'H') {
                    number = offsetHours;
                    unit = "hour";
                } else {
                    number = offsetMinutesRemaining;
                    unit = "minute";
                }
                offsetMinutes = offsetMinutes2;
                int i2 = numDigits;
                negative = negative2;
                appendWithTtsSpan(builder, formatDigits(number, numDigits, localizedDigits), new TtsSpan.MeasureBuilder().setNumber((long) number).setUnit(unit).build());
                i++;
                gmtPattern = gmtPattern2;
                placeholderIndex2 = placeholderIndex;
                offsetMinutes2 = offsetMinutes;
                negative2 = negative;
                TimeZone timeZone = tz;
            } else {
                builder.append(c);
                placeholderIndex = placeholderIndex2;
                offsetMinutes = offsetMinutes2;
                negative = negative2;
                i++;
                gmtPattern = gmtPattern2;
                placeholderIndex2 = placeholderIndex;
                offsetMinutes2 = offsetMinutes;
                negative2 = negative;
                TimeZone timeZone2 = tz;
            }
            String sign = String.valueOf(c);
            appendWithTtsSpan(builder, sign, new TtsSpan.VerbatimBuilder(sign).build());
            i++;
            gmtPattern = gmtPattern2;
            placeholderIndex2 = placeholderIndex;
            offsetMinutes2 = offsetMinutes;
            negative2 = negative;
            TimeZone timeZone22 = tz;
        }
        int i3 = placeholderIndex2;
        int i4 = offsetMinutes2;
        boolean z = negative2;
        if (!gmtPatternSuffix.isEmpty()) {
            appendWithTtsSpan(builder, gmtPatternSuffix, new TtsSpan.TextBuilder(gmtPatternSuffix).build());
        }
        return BidiFormatter.getInstance().unicodeWrap(new SpannableString(builder), TextUtils.getLayoutDirectionFromLocale(locale) == 1 ? TextDirectionHeuristicsCompat.RTL : TextDirectionHeuristicsCompat.LTR);
    }

    @VisibleForTesting
    public static final class ZoneGetterData {
        public final CharSequence[] gmtOffsetTexts = new CharSequence[this.zoneCount];
        public final Set<String> localZoneIds;
        public final String[] olsonIdsToDisplay = new String[this.zoneCount];
        public final TimeZone[] timeZones = new TimeZone[this.zoneCount];
        public final int zoneCount;

        public ZoneGetterData(Context context) {
            Locale locale = context.getResources().getConfiguration().locale;
            TimeZoneFormat tzFormatter = TimeZoneFormat.getInstance(locale);
            Date now = new Date();
            List<String> olsonIdsToDisplayList = ZoneGetter.readTimezonesToDisplay(context);
            this.zoneCount = olsonIdsToDisplayList.size();
            for (int i = 0; i < this.zoneCount; i++) {
                String olsonId = olsonIdsToDisplayList.get(i);
                this.olsonIdsToDisplay[i] = olsonId;
                TimeZone tz = TimeZone.getTimeZone(olsonId);
                this.timeZones[i] = tz;
                this.gmtOffsetTexts[i] = ZoneGetter.getGmtOffsetText(tzFormatter, locale, tz, now);
            }
            this.localZoneIds = new HashSet(lookupTimeZoneIdsByCountry(locale.getCountry()));
        }

        @VisibleForTesting
        public List<String> lookupTimeZoneIdsByCountry(String country) {
            return TimeZoneFinder.getInstance().lookupTimeZoneIdsByCountry(country);
        }
    }
}
