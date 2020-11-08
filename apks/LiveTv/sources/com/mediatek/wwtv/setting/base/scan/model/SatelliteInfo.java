package com.mediatek.wwtv.setting.base.scan.model;

import com.mediatek.twoworlds.tv.model.MtkTvDvbsConfigInfoBase;

public class SatelliteInfo extends MtkTvDvbsConfigInfoBase {
    protected boolean enable = false;
    protected String name = "";
    protected String type = "";

    public SatelliteInfo() {
    }

    public SatelliteInfo(MtkTvDvbsConfigInfoBase base) {
        this.satlRecId = base.getSatlRecId();
        this.mask = base.getMask();
        this.lnbType = base.getLnbType();
        this.lnbLowFreq = base.getLnbLowFreq();
        this.lnbHighFreq = base.getLnbHighFreq();
        this.lnbSwitchFreq = base.getLnbSwitchFreq();
        this.diseqcType = base.getDiseqcType();
        this.port = base.getPort();
        this.m22k = base.getM22k();
        this.toneBurst = base.getToneBurst();
        this.lnbPower = base.getLnbPower();
        this.position = base.getPosition();
        this.userBand = base.getUserBand();
        this.bandFreq = base.getBandFreq();
        this.mduType = base.getMduType();
        this.diseqcTypeEx = base.getDiseqcTypeEx();
        this.portEx = base.getPortEx();
        this.subDiseqcType = base.getSubDiseqcType();
        this.subPort = base.getSubPort();
        this.subM22k = base.getSubM22k();
        this.subToneBurst = base.getSubToneBurst();
        this.subLnbPower = base.getSubLnbPower();
        this.subPosition = base.getSubPosition();
        this.subUserBand = base.getSubUserBand();
        this.subBandFreq = base.getSubBandFreq();
        this.subMduType = base.getSubMduType();
        this.orbPos = base.getOrbPos();
        this.satName = base.getSatName();
        this.motorType = base.getMotorType();
        this.motorPosition = base.getMotorPosition();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type2) {
        this.type = type2;
    }

    public boolean getEnable() {
        return this.enable;
    }

    public void setEnabled(boolean enable2) {
        this.enable = enable2;
    }
}
