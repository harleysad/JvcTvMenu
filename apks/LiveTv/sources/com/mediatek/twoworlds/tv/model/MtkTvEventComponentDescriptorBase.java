package com.mediatek.twoworlds.tv.model;

public class MtkTvEventComponentDescriptorBase {
    private String Lang = "";
    private short componentTag = 0;
    private short componentType = 0;
    private short streamContent = 0;
    private short streamContentExt = 0;

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

    public short getStreamContentExt() {
        return this.streamContentExt;
    }

    public void setStreamContentExt(short streamContentExt2) {
        this.streamContentExt = streamContentExt2;
    }

    public String getComponentLang() {
        return this.Lang;
    }

    public void setComponentLang(String Lang2) {
        this.Lang = Lang2;
    }

    public String toString() {
        return "MtkTvEventComponentDescriptor: streamContent=" + this.streamContent + ", componentType=" + this.componentType + ", componentTag=" + this.componentTag + ", streamContentExt =" + this.streamContentExt + ", ComponentLang =" + this.Lang;
    }
}
