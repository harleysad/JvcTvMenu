package com.mediatek.twoworlds.tv.model;

public class MtkTvFreqChgParamBase {
    private static final String TAG = "MtkTvFreqSelectParam";
    protected int conType;
    protected int freq;
    protected int freqType;
    protected int satLstId;
    protected int satLstRecId;
    protected int satPol;
    protected int symRate;
    protected int tunerMod;

    public MtkTvFreqChgParamBase() {
        this.conType = 0;
        this.freqType = 7;
        this.freq = 0;
        this.tunerMod = 0;
        this.symRate = 0;
        this.satPol = 0;
        this.satLstId = 0;
        this.satLstRecId = 0;
    }

    public MtkTvFreqChgParamBase(int conType2, int freqType2, int freq2, int tunerMod2, int symRate2) {
        this.conType = conType2;
        this.freqType = freqType2;
        this.freq = freq2;
        this.tunerMod = tunerMod2;
        this.symRate = symRate2;
    }

    public MtkTvFreqChgParamBase(int conType2, int freqType2, int freq2, int tunerMod2, int symRate2, int satPol2, int satLstId2, int satLstRecId2) {
        this.conType = conType2;
        this.freqType = freqType2;
        this.freq = freq2;
        this.tunerMod = tunerMod2;
        this.symRate = symRate2;
        this.satPol = satPol2;
        this.satLstId = satLstId2;
        this.satLstRecId = satLstRecId2;
    }

    public void setConType(int ConType) {
        this.conType = ConType;
    }

    public void setFreqType(int freqType2) {
        this.freqType = freqType2;
    }

    public void setFreq(int freq2) {
        this.freq = freq2;
    }

    public void settunerMod(int tunerMod2) {
        this.tunerMod = tunerMod2;
    }

    public void setsymRate(int symRate2) {
        this.symRate = symRate2;
    }

    public void setSatPol(int Pol) {
        this.satPol = Pol;
    }

    public void setSatLstId(int lstId) {
        this.satLstId = lstId;
    }

    public void setSatLstRecId(int lstRecId) {
        this.satLstRecId = lstRecId;
    }

    public int getConType() {
        return this.conType;
    }

    public int getfreqType() {
        return this.freqType;
    }

    public int getfreq() {
        return this.freq;
    }

    public int gettunerMod() {
        return this.tunerMod;
    }

    public int getsymRate() {
        return this.symRate;
    }

    public int getSatPol() {
        return this.satPol;
    }

    public int getSatLstId() {
        return this.satLstId;
    }

    public int getSatLstRecId() {
        return this.satLstRecId;
    }
}
