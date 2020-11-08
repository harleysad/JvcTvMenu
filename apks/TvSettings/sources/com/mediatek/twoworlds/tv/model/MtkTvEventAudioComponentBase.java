package com.mediatek.twoworlds.tv.model;

public class MtkTvEventAudioComponentBase {
    private short componentTag = 0;
    private short componentType = 0;
    private String language1 = "";
    private String language2 = "";
    private String languageName1 = "";
    private String languageName2 = "";
    private boolean mainComponentFlag = false;
    private boolean multiLingualFlag = false;
    private short qualityIndicator = 0;
    private short sampleRate = 0;
    private short simulcastGrpTag = 0;
    private short streamContent = 0;
    private short streamType = 0;

    public short getStreamContent() {
        return this.streamContent;
    }

    public void setStreamContent(short streamContent2) {
        this.streamContent = streamContent2;
    }

    public short getComponentType() {
        return this.componentType;
    }

    public void setComponentType(short componentType2) {
        this.componentType = componentType2;
    }

    public short getComponentTag() {
        return this.componentTag;
    }

    public void setComponentTag(short componentTag2) {
        this.componentTag = componentTag2;
    }

    public short getStreamType() {
        return this.streamType;
    }

    public void setStreamType(short streamType2) {
        this.streamType = streamType2;
    }

    public short getSimulcastGrpTag() {
        return this.simulcastGrpTag;
    }

    public void setSimulcastGrpTag(short simulcastGrpTag2) {
        this.simulcastGrpTag = simulcastGrpTag2;
    }

    public short getQualityIndicator() {
        return this.qualityIndicator;
    }

    public void setQualityIndicator(short qualityIndicator2) {
        this.qualityIndicator = qualityIndicator2;
    }

    public short getSampleRate() {
        return this.sampleRate;
    }

    public void setSampleRate(short sampleRate2) {
        this.sampleRate = sampleRate2;
    }

    public boolean getMultiLingualFlag() {
        return this.multiLingualFlag;
    }

    public void setMultiLingualFlag(boolean multiLingualFlag2) {
        this.multiLingualFlag = multiLingualFlag2;
    }

    public boolean getMainComponentFlag() {
        return this.mainComponentFlag;
    }

    public void setMainComponentFlag(boolean mainComponentFlag2) {
        this.mainComponentFlag = mainComponentFlag2;
    }

    public String getLanguage1() {
        return this.language1;
    }

    public void setLanguage1(String language12) {
        this.language1 = language12;
    }

    public String getLanguage2() {
        return this.language2;
    }

    public void setLanguage2(String language22) {
        this.language2 = language22;
    }

    public String getLanguageName1() {
        return this.languageName1;
    }

    public void setLanguageName1(String languageName12) {
        this.languageName1 = languageName12;
    }

    public String getLanguageName2() {
        return this.languageName2;
    }

    public void setLanguageName2(String languageName22) {
        this.languageName2 = languageName22;
    }

    public String toString() {
        return "MtkTvEventAudioComponentDescriptor: streamContent=" + this.streamContent + ", componentType=" + this.componentType + ", componentTag=" + this.componentTag + ", streamType =" + this.streamType + ", simulcastGrpTag =" + this.simulcastGrpTag + ", qualityIndicator =" + this.qualityIndicator + ", sampleRate =" + this.sampleRate + ", multiLingualFlag =" + this.multiLingualFlag + ", mainComponentFlag=" + this.mainComponentFlag + ", language1 =" + this.language1 + ", language2" + this.language2 + ", languageName1 =" + this.languageName1 + ", languageName2" + this.languageName2;
    }
}
