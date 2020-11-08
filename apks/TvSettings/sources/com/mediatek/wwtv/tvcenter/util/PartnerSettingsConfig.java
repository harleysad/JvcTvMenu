package com.mediatek.wwtv.tvcenter.util;

import android.os.SystemProperties;
import android.text.TextUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParserException;

public final class PartnerSettingsConfig {
    public static final String ATTR_ADVANCED_VIDEO = "advanced_video";
    public static final String ATTR_COMMON = "common_misc";
    public static final String ATTR_COUNTRY_EU_COMMON = "eu_common_country";
    public static final String ATTR_COUNTRY_EU_EWS = "eu_ews_country";
    public static final String ATTR_COUNTRY_EU_OCEANIA = "eu_oceania_country";
    public static final String ATTR_COUNTRY_EU_PA = "eu_pa_country";
    public static final String ATTR_DEVICE_ACCESSIBILITY = "accessibility";
    public static final String ATTR_DEVICE_ACC_VISAULLY = "device_acc_visually";
    public static final String ATTR_DEVICE_INPUTS = "device_inputs";
    public static final String ATTR_DEVICE_RETAIL = "device_retail";
    public static final String ATTR_PICTURE_COLOR_TUNE = "picture_color_tune";
    public static final String ATTR_PICTURE_COLOR_TUNE_BRIGHTNESS = "picture_color_tune_brightness";
    public static final String ATTR_PICTURE_COLOR_TUNE_GAIN = "picture_color_tune_gain";
    public static final String ATTR_PICTURE_COLOR_TUNE_HUE = "picture_color_tune_hue";
    public static final String ATTR_PICTURE_COLOR_TUNE_OFFSET = "picture_color_tune_offset";
    public static final String ATTR_PICTURE_COLOR_TUNE_SATURATION = "picture_color_tune_saturation";
    public static final String ATTR_PICTURE_VGA = "picture_vga";
    public static final String ATTR_PICTURE_WHITE_BALANCE11 = "picture_white_balance11";
    public static final String ATTR_SOUND = "sound_effects";
    public static final String ATTR_SOUND_DAP = "sound_dap";
    public static final String ATTR_SOUND_DBX = "sound_dbx";
    public static final String ATTR_SOUND_DTS_STUDIO = "sound_dts_studio";
    public static final String ATTR_SOUND_EQUALIZER_DETAIL = "sound_equalizer_detail";
    public static final String ATTR_SOUND_SONIC_EMOTION_PRE = "sound_sonic_emotion_premium";
    public static final String ATTR_VIDEO = "picture_effects";
    private static final String CONFIG_CONFIG_FILE = "tv_wizard_country_config.xml";
    private static final String CONFIG_FILE = "tv-settings-configs.xml";
    public static final String CUSTOMER_NAME_FUNAI = "funai";
    public static final String CUSTOMER_NAME_MTK = "mtk";
    public static final String CUS_DEVICE_RETAIL = "customized_retail_entry";
    public static final String CUS_INPUTS_HDMI_EDID = "customized_hdmi_edid_entry";
    public static final String CUS_PICTURE_AUTO_BACKLIGHT_ENTRY = "customized_auto_backlight_entry";
    public static final String CUS_PICTURE_AUTO_BACKLIGHT_ENTRY_VALUES = "customized_auto_backlight_entry_values";
    private static final String PATH = "/vendor/etc/";
    private static final String TAG = "PartnerSettingsConfig";
    private static final String VENDOR_NAME = "ro.vendor.odm.name";
    private static final String VENDOR_PATH = "/vendor/tvconfig/apollo/";
    private static final Map<String, CountryConfigEntry> mCountryEntryMap = new HashMap();
    private static String mCustomerName;
    private static String mPath;
    private static final Map<String, ArrayList<String>> mSettingsList = new HashMap();

    static {
        init();
    }

    private static void init() {
        mPath = getFile(CONFIG_FILE);
        MtkLog.d(TAG, "init, mPath==" + mPath);
        if (mPath == null) {
            MtkLog.d(TAG, "Couldn't find or open file path !");
            return;
        }
        String name = SystemProperties.get(VENDOR_NAME);
        if (name == null || name.length() <= 0) {
            mCustomerName = CUSTOMER_NAME_MTK;
        } else {
            mCustomerName = name;
        }
        try {
            parserXml();
            parserCountryConfigFromXml();
        } catch (Exception e) {
            MtkLog.w(TAG, "Got exception parsing favorites.", e);
        }
    }

    private static String getFile(String file) {
        for (String path : new String[]{VENDOR_PATH + file, PATH + file}) {
            if (new File(path).exists()) {
                return path;
            }
        }
        return null;
    }

    private static void parserXml() throws XmlPullParserException, IOException, Exception {
        DocumentBuilder db;
        DocumentBuilderFactory dbf;
        List<String> mList;
        DocumentBuilder db2;
        List<String> mList2 = new ArrayList<>();
        DocumentBuilderFactory dbf2 = DocumentBuilderFactory.newInstance();
        DocumentBuilder db3 = dbf2.newDocumentBuilder();
        NodeList customerNodeList = db3.parse(new File(mPath)).getElementsByTagName("customer");
        MtkLog.d(TAG, "customerNodeList.getLength() : " + customerNodeList.getLength());
        int i = 0;
        int i2 = 0;
        while (i2 < customerNodeList.getLength()) {
            Node customerNode = customerNodeList.item(i2);
            String customer0 = customerNode.getAttributes().item(i).getTextContent();
            MtkLog.d(TAG, "customer0 : " + customer0);
            if (customer0 != null && customer0.equals(mCustomerName)) {
                NodeList itemNodeList = customerNode.getChildNodes();
                int j = i;
                while (j < itemNodeList.getLength()) {
                    Node itemNode = itemNodeList.item(j);
                    if ("item".equalsIgnoreCase(itemNode.getNodeName())) {
                        String itemName = itemNode.getAttributes().item(i).getTextContent();
                        MtkLog.d(TAG, "itemName : " + itemName);
                        ArrayList<String> list = new ArrayList<>();
                        NodeList mNodeList = itemNode.getChildNodes();
                        int k = i;
                        while (k < mNodeList.getLength()) {
                            Node mNode = mNodeList.item(k);
                            List<String> mList3 = mList2;
                            DocumentBuilderFactory dbf3 = dbf2;
                            if ("pref".equalsIgnoreCase(mNode.getNodeName())) {
                                StringBuilder sb = new StringBuilder();
                                db2 = db3;
                                sb.append("mNode.getTextContent()");
                                sb.append(mNode.getTextContent());
                                MtkLog.d(TAG, sb.toString());
                                list.add(mNode.getTextContent());
                            } else {
                                db2 = db3;
                            }
                            k++;
                            mList2 = mList3;
                            dbf2 = dbf3;
                            db3 = db2;
                        }
                        mList = mList2;
                        dbf = dbf2;
                        db = db3;
                        mSettingsList.put(itemName, list);
                    } else {
                        mList = mList2;
                        dbf = dbf2;
                        db = db3;
                    }
                    j++;
                    mList2 = mList;
                    dbf2 = dbf;
                    db3 = db;
                    i = 0;
                }
            }
            i2++;
            mList2 = mList2;
            dbf2 = dbf2;
            db3 = db3;
            i = 0;
        }
        DocumentBuilderFactory documentBuilderFactory = dbf2;
        DocumentBuilder documentBuilder = db3;
    }

    public static String getCustomerName() {
        return mCustomerName;
    }

    public static ArrayList<String> getSettingsList(String key) {
        return mSettingsList.get(key);
    }

    private static boolean isItemDisplay(String id, String type) {
        ArrayList<String> array = getSettingsList(type);
        if (array == null) {
            return false;
        }
        return array.contains(id);
    }

    public static boolean isVideoItemDisplay(String id) {
        return isItemDisplay(id, "picture_effects");
    }

    public static boolean isSoundItemDisplay(String id) {
        return isItemDisplay(id, "sound_effects");
    }

    public static boolean isMiscItemDisplay(String id) {
        return isItemDisplay(id, ATTR_COMMON);
    }

    public static String debug() {
        String info = "PartnerSettingsConfig\n:tv-settings-configs.xml\n";
        for (String key : mSettingsList.keySet()) {
            String info2 = info + "Key:[" + key + "] - value(";
            Iterator it = mSettingsList.get(key).iterator();
            while (it.hasNext()) {
                info2 = info2 + ((String) it.next()) + ",";
            }
            info = info2 + ")\n";
        }
        return info;
    }

    public static Map<String, CountryConfigEntry> getCountryConfigMap() {
        return mCountryEntryMap;
    }

    public static void parserCountryConfigFromXml() throws Exception {
        MtkLog.d("yupeng", "parserCountryConfigFromXml");
        String filePath = getFile(CONFIG_CONFIG_FILE);
        if (TextUtils.isEmpty(filePath)) {
            MtkLog.d(TAG, "Couldn't find or open file country config path !");
        }
        NodeList nList = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath)).getElementsByTagName("country");
        mCountryEntryMap.clear();
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == 1) {
                Element eElement = (Element) nNode;
                CountryConfigEntry countryEntry = new CountryConfigEntry();
                countryEntry.time_zone = getContentByKey(eElement, "time_zone");
                countryEntry.digital_subtitle_lang = getContentByKey(eElement, "digital_subtitle_lang");
                countryEntry.digital_subtitle_lang_2nd = getContentByKey(eElement, "digital_subtitle_lang_2nd");
                countryEntry.audio_lang = getContentByKey(eElement, "audio_lang");
                countryEntry.audio_lang_2nd = getContentByKey(eElement, "audio_lang_2nd");
                try {
                    countryEntry.digital_ttx_Lang = Integer.parseInt(getContentByKey(eElement, "digital_ttx_Lang"));
                    countryEntry.decoding_page_lang = Integer.parseInt(getContentByKey(eElement, "decoding_page_lang"));
                } catch (Exception e) {
                }
                countryEntry.dvbc_operators = getContentByKey(eElement, "dvbc_operator");
                countryEntry.dvbs_operators = getContentByKey(eElement, "dvbs_operator");
                String countryCode = eElement.getAttribute("code");
                MtkLog.d("yupeng", "[+" + countryCode + "]  " + countryEntry.toString());
                mCountryEntryMap.put(countryCode, countryEntry);
            }
        }
    }

    private static String getContentByKey(Element eElement, String key) {
        Node item;
        NodeList list = eElement.getElementsByTagName(key);
        if (list == null || (item = list.item(0)) == null) {
            return "";
        }
        return item.getTextContent();
    }
}
