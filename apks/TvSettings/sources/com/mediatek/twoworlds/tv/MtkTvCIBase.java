package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvCIMMIEnqBase;
import com.mediatek.twoworlds.tv.model.MtkTvCIMMIMenuBase;

public class MtkTvCIBase {
    public static final String TAG = "MtkTvCIBase";
    private static MtkTvCIMMIEnqBase mMtkTvCIMMIEnq = new MtkTvCIMMIEnqBase();
    private static MtkTvCIMMIMenuBase mMtkTvCIMMIMenuBase = new MtkTvCIMMIMenuBase();
    int slot_id;

    protected MtkTvCIBase(int slotid) {
        this.slot_id = slotid;
    }

    protected MtkTvCIBase() {
        this.slot_id = 0;
    }

    public int getSlotID() {
        return this.slot_id;
    }

    public static int getSlotNum() {
        return TVNativeWrapper.getSlotNum_navtive();
    }

    public static int getMenuListID() {
        return TVNativeWrapper.getMenuListID_navtive();
    }

    public static int getEnqID() {
        return TVNativeWrapper.getEnqID_navtive();
    }

    public String getCamName() {
        return TVNativeWrapper.getCamName_navtive(this.slot_id);
    }

    public String getCamID() {
        String CamID = TVNativeWrapper.getCamID_navtive(this.slot_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.getCamID. CamID =" + CamID + ".\n");
        if (CamID != null) {
            Log.d(TAG, "&&&&&, CamID.length =" + CamID.length() + ".\n");
        } else {
            Log.d(TAG, "&&&&&, return NULL!\n");
        }
        return CamID;
    }

    public boolean getSlotActive() {
        return TVNativeWrapper.getSlotActive_navtive(this.slot_id);
    }

    public int setMenuAnswer(int mmi_id, int answer_item) {
        TVNativeWrapper.setMenuAnswer_navtive(this.slot_id, mmi_id, answer_item);
        return 0;
    }

    public int setEnqAnswer(int mmi_id, int answer, String answer_data) {
        TVNativeWrapper.setEnqAnswer_navtive(this.slot_id, mmi_id, answer, answer_data);
        return 0;
    }

    public void setMMIClose() {
        TVNativeWrapper.setMMIClose_navtive(this.slot_id);
    }

    public void setMMICloseDone() {
        TVNativeWrapper.setMMICloseDone_navtive(this.slot_id);
    }

    public void enterMMI() {
        TVNativeWrapper.enterMMI_navtive(this.slot_id);
    }

    public int startCamScan(boolean b_flag) {
        TVNativeWrapper.startCamScan_navtive(this.slot_id, b_flag);
        return 0;
    }

    public int cancelCamScan() {
        Log.d(TAG, "&&&&&, MtkTvCIBase.cancelCamScan.\n");
        TVNativeWrapper.cancelCamScan_navtive(this.slot_id);
        return 0;
    }

    public int updateCIKey() {
        return TVNativeWrapper.updateCIKey_navtive();
    }

    public int updateCIKeyEx(int cert_type) {
        Log.d(TAG, "&&&&&, MtkTvCIBase.updateCIKeyEx, cert_type:" + cert_type + "\n");
        int ret = TVNativeWrapper.updateCIKeyEx_navtive(cert_type);
        Log.d(TAG, "&&&&&, MtkTvCIBase.updateCIKeyEx, ret:" + ret + "\n");
        return ret;
    }

    public int writeCIKey(byte[] key_buffer, int key_buffer_len) {
        Log.d(TAG, "&&&&&, MtkTvCIBase.writeCIKey.\n");
        int array_len = key_buffer.length;
        Log.d(TAG, "&&&&&, MtkTvCIBase.writeCIKey array_len = " + array_len + ".\n");
        return TVNativeWrapper.writeCIKey_navtive(key_buffer, key_buffer_len);
    }

    public int activateCIKey() {
        return TVNativeWrapper.activateCIKey_navtive();
    }

    public int eraseCIKey() {
        return TVNativeWrapper.eraseCIKey_navtive();
    }

    public String getCIKeyinfo() {
        return TVNativeWrapper.getCIKeyinfo_navtive();
    }

    public boolean getCIKeyStatus() {
        Log.d(TAG, "&&&&&, MtkTvCIBase.getCIKeyStatus.\n");
        return TVNativeWrapper.getCIKeyStatus_navtive();
    }

    public boolean getCIKeyStatusEx(int cert_type) {
        Log.d(TAG, "&&&&&, MtkTvCIBase.getCIKeyStatusEx, cert_type:" + cert_type + "\n");
        return TVNativeWrapper.getCIKeyStatusEx_navtive(cert_type);
    }

    public int updateCIKeyWithPath(String path) {
        Log.d(TAG, "&&&&&, MtkTvCIBase.getCIKeyStatusEx.\n");
        return TVNativeWrapper.updateCIKeyWithPath_navtive(path);
    }

    public int updateCIKeyWithPathEx(String path, int cert_type) {
        Log.d(TAG, "&&&&&, MtkTvCIBase.getCIKeyStatusEx, cert_type:" + cert_type + "\n");
        return TVNativeWrapper.updateCIKeyWithPathEx_navtive(path, cert_type);
    }

    public int getCIHostID(int[] host_id) {
        Log.d(TAG, "&&&&&, MtkTvCIBase.getCIHostID.\n");
        int array_len = host_id.length;
        if (array_len < 8) {
            Log.d(TAG, "&&&&&, MtkTvCIBase.getCIHostID. host_id size = " + array_len + "(< 8) return fail.\n");
            return -1;
        }
        int i_ret = TVNativeWrapper.getCIHostID_navtive(host_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.getCIHostID. return value = " + i_ret + "\n");
        for (int i = 0; i < array_len; i++) {
            Log.d(TAG, "MtkTvCIBase: host_id[" + i + "] = " + host_id[i] + "\n");
        }
        return i_ret;
    }

    public int setCamPinCode(String pin_code) {
        return TVNativeWrapper.setCamPinCode_navtive(this.slot_id, pin_code);
    }

    public String getCamPinCode() {
        String pin_code = TVNativeWrapper.getCamPinCode_navtive(this.slot_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.getCamPinCode, pin_code =" + pin_code + ".\n");
        if (pin_code != null) {
            Log.d(TAG, "&&&&&, pin_code.length() =" + pin_code.length() + ".\n");
        } else {
            Log.d(TAG, "&&&&&, return NULL!\n");
        }
        return pin_code;
    }

    public static int getCamPinCaps() {
        return TVNativeWrapper.getCamPinCaps_navtive();
    }

    public int getCamRatingValue() {
        int cam_rating = TVNativeWrapper.getCamRatingValue_navtive(this.slot_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.getCamRatingValue. cam_rating = " + cam_rating + "\n");
        return cam_rating;
    }

    public int getCaSystemIDMatch() {
        int i_match = TVNativeWrapper.getCaSystemIDMatch_navtive(this.slot_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.getCaSystemIDMatch. match = " + i_match + "\n");
        return i_match;
    }

    public int getProfileValid() {
        int valid = TVNativeWrapper.getProfileValid_navtive(this.slot_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.getProfileValid. valid = " + valid + "\n");
        return valid;
    }

    public int getProfileSupport(int tuner_mode) {
        int support = TVNativeWrapper.getProfileSupport_navtive(this.slot_id, tuner_mode);
        Log.d(TAG, "&&&&&, MtkTvCIBase.getProfileSupport. support = " + support + "\n");
        return support;
    }

    public int getProfileResourceIsOpen() {
        int open = TVNativeWrapper.getProfileResourceIsOpen_navtive(this.slot_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.getProfileResourceIsOpen. open = " + open + "\n");
        return open;
    }

    public String getProfileName() {
        String profileName = TVNativeWrapper.getProfileName_navtive(this.slot_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.getProfileName. Name =" + profileName + ".\n");
        return profileName;
    }

    public String getProfileISO639LangCode() {
        Log.d(TAG, "&&&&&, MtkTvCIBase.getProfileISO639LangCode.\n");
        String langcode = TVNativeWrapper.getProfileISO639LangCode_navtive(this.slot_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.getProfileISO639LangCode. langcode = " + langcode + "\n");
        return langcode;
    }

    public int getSasItvState() {
        int itv_state = TVNativeWrapper.getSasItvState_navtive(this.slot_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.getSasItvState. itv_state = " + itv_state + "\n");
        return itv_state;
    }

    public int setSasForceItvExit() {
        int i4_ret = TVNativeWrapper.setSasForceItvExit_navtive(this.slot_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.setSasForceItvExit. ret = " + i4_ret + "\n");
        return i4_ret;
    }

    public int getSasCbctState() {
        int cbct_state = TVNativeWrapper.getSasCbctState_navtive(this.slot_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.getSasCbctState. cbct_state = " + cbct_state + "\n");
        return cbct_state;
    }

    public int getHostTuneStatus() {
        int tune_status = TVNativeWrapper.getHostTuneStatus_navtive(this.slot_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.getHostTuneStatus. tune_status = " + tune_status + "\n");
        return tune_status;
    }

    public int getHostTuneBrdcstStatus() {
        int tune_brdcst_status = TVNativeWrapper.getHostTuneBrdcstStatus_navtive(this.slot_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.getHostTuneBrdcstStatus. tune_brdcst_status = " + tune_brdcst_status + "\n");
        return tune_brdcst_status;
    }

    public int getHostQuietTuneStatus() {
        int quiet_tune_status = TVNativeWrapper.getHostQuietTuneStatus_navtive(this.slot_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.getHostQuietTuneStatus. quiet_tune_status = " + quiet_tune_status + "\n");
        return quiet_tune_status;
    }

    public int setHDSConfirm(int confirm_value) {
        Log.d(TAG, "&&&&&, MtkTvCIBase.setHDSConfirm value =" + confirm_value + ".\n");
        TVNativeWrapper.setHDSConfirm_navtive(this.slot_id, confirm_value);
        Log.d(TAG, "&&&&&, MtkTvCIBase.setHDSConfirm. i4_ret = " + 0 + "\n");
        return 0;
    }

    public String getVirtualChannelInfo() {
        Log.d(TAG, "&&&&&, MtkTvCIBase.getVirtualChannelInfo.\n");
        String vir_channel = TVNativeWrapper.getVirtualChannelInfo_navtive(this.slot_id);
        Log.d(TAG, " MtkTvCIBase.getVirtualChannelInfo. vir_channel_info =" + vir_channel + ".\n");
        return vir_channel;
    }

    public int setScanComplete() {
        int i4_ret = TVNativeWrapper.setScanComplete_navtive(this.slot_id);
        Log.d(TAG, "&&&&&, MtkTvCIBase.setScanComplete. ret = " + i4_ret + "\n");
        return i4_ret;
    }

    private static MtkTvCIMMIEnqBase getNewMMIEnq(int enq_id, byte anwDataLen, byte bBlindAnswe, String text) {
        Log.d(TAG, "enq_id=" + enq_id + " anwDataLen=" + anwDataLen + " bBlindAnswe=" + bBlindAnswe + " text=" + text + ".\n");
        mMtkTvCIMMIEnq.setMMIId(enq_id);
        mMtkTvCIMMIEnq.setAnsTextLen(anwDataLen);
        mMtkTvCIMMIEnq.setBlindAns(bBlindAnswe);
        mMtkTvCIMMIEnq.setText(text);
        return mMtkTvCIMMIEnq;
    }

    private static MtkTvCIMMIMenuBase getNewMMIMenu(int menuId, byte item_nb, String title, String subTitle, String bottom, String itemlist) {
        Log.d(TAG, " getNewMMIMenu.\n");
        mMtkTvCIMMIMenuBase.setMMIId(menuId);
        mMtkTvCIMMIMenuBase.setItemNum(item_nb);
        mMtkTvCIMMIMenuBase.setTitle(title);
        mMtkTvCIMMIMenuBase.setSubtitle(subTitle);
        mMtkTvCIMMIMenuBase.setBottom(bottom);
        if (item_nb <= 0 || itemlist == null) {
            Log.d(TAG, " item_nb = " + item_nb + " or itemList = null, so set ItemList null.\n");
            mMtkTvCIMMIMenuBase.setItemList((String[]) null);
        } else {
            String[] list = itemlist.split("#\\$#", -1);
            Log.d(TAG, "menuId=" + menuId + " item_nb=" + item_nb + " title=" + title + " subTitle=" + subTitle + " bottom=" + bottom + " itemlist=" + itemlist + "\n");
            StringBuilder sb = new StringBuilder();
            sb.append(" list.size()=");
            sb.append(list.length);
            sb.append(".\n");
            Log.d(TAG, sb.toString());
            for (int i = 0; i < list.length; i++) {
                Log.d(TAG, " list[" + i + "]=" + list[i] + "\n");
            }
            mMtkTvCIMMIMenuBase.setItemList(list);
        }
        return mMtkTvCIMMIMenuBase;
    }
}
