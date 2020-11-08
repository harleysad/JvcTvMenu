package com.mediatek.twoworlds.tv.model;

import java.util.ArrayList;

public class MtkTvVideoInfoBase {
    private static final String TAG = "MtkTvVideoInfoBase";
    private int eDataRange;
    private int eHistogramType;
    private ArrayList<int[]> ppu32SatByHueHistogram = new ArrayList<>();
    private int[] pu16HueWinEnd = new int[6];
    private int[] pu16HueWinStart = new int[6];
    private int[] pu32Histogram = new int[32];
    private int u16HistBinNum;
    private int u16HistBinNum_1;
    private int u16HueHistBinNum;
    private int u16LumaHistBinNum;
    private int u16SatByHueHistBinNum;
    private int u16SatByHueWinNum;
    private int u16SatByHueWinNum_1;
    private int u16SatByHueWinSliceNum;
    private int u16SatHistBinNum;
    private int u32AvgPixelLuma;
    private int u32DispCtrlAttrEnable;
    private int u32DispCtrlAttrInfo;
    private int u32DispCtrlAttrOsdDetectd;
    private int u32DispCtrlAttrThreshold;
    private int u32MaxPixelLuma;
    private int u32MinPixelLuma;

    public MtkTvVideoInfoBase() {
        for (int i = 0; i < this.pu16HueWinStart.length; i++) {
            this.pu16HueWinStart[i] = 0;
        }
        for (int i2 = 0; i2 < this.pu16HueWinEnd.length; i2++) {
            this.pu16HueWinEnd[i2] = 0;
        }
        for (int i3 = 0; i3 < this.pu32Histogram.length; i3++) {
            this.pu32Histogram[i3] = 0;
        }
        this.ppu32SatByHueHistogram.clear();
    }

    public int[] getPu16HueWinEnd() {
        return this.pu16HueWinEnd;
    }

    public void setPu16HueWinEnd(int[] hueWinEnd) {
        this.pu16HueWinEnd = hueWinEnd;
    }

    public int[] getPu16HueWinStart() {
        return this.pu16HueWinStart;
    }

    public void setPu16HueWinStart(int[] hueWinStart) {
        this.pu16HueWinStart = hueWinStart;
    }

    public int getU16SatByHueWinNum() {
        return this.u16SatByHueWinNum;
    }

    public void setU16SatByHueWinNum(int u16SatByHueWinNum2) {
        this.u16SatByHueWinNum = u16SatByHueWinNum2;
    }

    public int getU16SatByHueWinNum_1() {
        return this.u16SatByHueWinNum_1;
    }

    public void setU16SatByHueWinNum_1(int u16SatByHueWinNum_12) {
        this.u16SatByHueWinNum_1 = u16SatByHueWinNum_12;
    }

    public int getEHistogramType() {
        return this.eHistogramType;
    }

    public void setEHistogramType(int eHistogramType2) {
        this.eHistogramType = eHistogramType2;
    }

    public int getU16HistBinNum() {
        return this.u16HistBinNum;
    }

    public void setU16HistBinNum(int u16HistBinNum2) {
        this.u16HistBinNum = u16HistBinNum2;
    }

    public int getU16HistBinNum_1() {
        return this.u16HistBinNum_1;
    }

    public void setU16HistBinNum_1(int u16HistBinNum_12) {
        this.u16HistBinNum_1 = u16HistBinNum_12;
    }

    public int[] getPu32Histogram() {
        return this.pu32Histogram;
    }

    public void setPu32Histogram(int[] pu32Histogram2) {
        this.pu32Histogram = pu32Histogram2;
    }

    public int getU16LumaHistBinNum() {
        return this.u16LumaHistBinNum;
    }

    public void setU16LumaHistBinNum(int u16LumaHistBinNum2) {
        this.u16LumaHistBinNum = u16LumaHistBinNum2;
    }

    public int getU16SatHistBinNum() {
        return this.u16SatHistBinNum;
    }

    public void setU16SatHistBinNum(int u16SatHistBinNum2) {
        this.u16SatHistBinNum = u16SatHistBinNum2;
    }

    public int getU16HueHistBinNum() {
        return this.u16HueHistBinNum;
    }

    public void setU16HueHistBinNum(int u16HueHistBinNum2) {
        this.u16HueHistBinNum = u16HueHistBinNum2;
    }

    public int getU16SatByHueHistBinNum() {
        return this.u16SatByHueHistBinNum;
    }

    public void setU16SatByHueHistBinNum(int u16SatByHueHistBinNum2) {
        this.u16SatByHueHistBinNum = u16SatByHueHistBinNum2;
    }

    public int getU16SatByHueWinSliceNum() {
        return this.u16SatByHueWinSliceNum;
    }

    public void setU16SatByHueWinSliceNum(int u16SatByHueWinSliceNum2) {
        this.u16SatByHueWinSliceNum = u16SatByHueWinSliceNum2;
    }

    public int getEDataRange() {
        return this.eDataRange;
    }

    public void setEDataRange(int eDataRange2) {
        this.eDataRange = eDataRange2;
    }

    public int getU32MinPixelLuma() {
        return this.u32MinPixelLuma;
    }

    public void setU32MinPixelLuma(int u32MinPixelLuma2) {
        this.u32MinPixelLuma = u32MinPixelLuma2;
    }

    public int getU32MaxPixelLuma() {
        return this.u32MaxPixelLuma;
    }

    public void setU32MaxPixelLuma(int u32MaxPixelLuma2) {
        this.u32MaxPixelLuma = u32MaxPixelLuma2;
    }

    public int getU32AvgPixelLuma() {
        return this.u32AvgPixelLuma;
    }

    public void setU32AvgPixelLuma(int u32AvgPixelLuma2) {
        this.u32AvgPixelLuma = u32AvgPixelLuma2;
    }

    public ArrayList<int[]> getPpu32SatByHueHistogram() {
        return this.ppu32SatByHueHistogram;
    }

    public void setPpu32SatByHueHistogram(ArrayList<int[]> ppu32SatByHueHistogram2) {
        this.ppu32SatByHueHistogram = (ArrayList) ppu32SatByHueHistogram2.clone();
    }

    public int getU32DispCtrlAttrEnable() {
        return this.u32DispCtrlAttrEnable;
    }

    public void setU32DispCtrlAttrEnable(int u32DispCtrlAttrEnable2) {
        this.u32DispCtrlAttrEnable = u32DispCtrlAttrEnable2;
    }

    public int getU32DispCtrlAttrInfo() {
        return this.u32DispCtrlAttrInfo;
    }

    public void setU32DispCtrlAttrInfo(int u32DispCtrlAttrInfo2) {
        this.u32DispCtrlAttrInfo = u32DispCtrlAttrInfo2;
    }

    public int getU32DispCtrlAttrOsdDetectd() {
        return this.u32DispCtrlAttrOsdDetectd;
    }

    public void setU32DispCtrlAttrOsdDetectd(int u32DispCtrlAttrOsdDetectd2) {
        this.u32DispCtrlAttrOsdDetectd = u32DispCtrlAttrOsdDetectd2;
    }

    public int getU32DispCtrlAttrThreshold() {
        return this.u32DispCtrlAttrThreshold;
    }

    public void setU32DispCtrlAttrThreshold(int u32DispCtrlAttrThreshold2) {
        this.u32DispCtrlAttrThreshold = u32DispCtrlAttrThreshold2;
    }
}
