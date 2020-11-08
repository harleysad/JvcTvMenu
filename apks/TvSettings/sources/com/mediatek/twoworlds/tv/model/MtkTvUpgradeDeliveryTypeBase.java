package com.mediatek.twoworlds.tv.model;

public enum MtkTvUpgradeDeliveryTypeBase {
    USB(0),
    INTERNET(1),
    OAD(2),
    ULI(3),
    UNKNOWN(4);
    
    private int mDeliveryValue;

    private MtkTvUpgradeDeliveryTypeBase(int deliveryValue) {
        this.mDeliveryValue = deliveryValue;
    }

    public int getDeliveryTypeValue() {
        return this.mDeliveryValue;
    }

    public String toString() {
        switch (this.mDeliveryValue) {
            case 0:
                return "USB";
            case 1:
                return "Internet";
            case 2:
                return "OAD";
            case 3:
                return "ULI";
            default:
                return "Unknown";
        }
    }
}
