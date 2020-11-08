package com.mediatek.wwtv.tvcenter.nav.input;

import android.content.Context;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class InputUtil {
    private static final String TAG = "ISource^InputUtil";
    public static boolean isMultiAVInputs;
    private static final List<AbstractInput> mSourceList = new ArrayList();
    protected static TvInputManager mTvInputManager = null;

    public static TvInputManager getTvInputManager() {
        return mTvInputManager;
    }

    public static synchronized void buildSourceList(Context context) {
        synchronized (InputUtil.class) {
            if (mTvInputManager == null) {
                try {
                    mTvInputManager = (TvInputManager) context.getSystemService("tv_input");
                } catch (Exception e) {
                }
            }
            Iterator<AbstractInput> iterator = mSourceList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().isHDMI()) {
                    iterator.remove();
                }
            }
            AbstractInput absInput = new TvInput();
            if (!absInput.isHidden(context) && getInputByType(absInput.getType()) == null) {
                mSourceList.add(absInput);
            }
            AbstractInput absInput2 = new DtvInput();
            if (!absInput2.isHidden(context) && getInputByType(absInput2.getType()) == null) {
                mSourceList.add(absInput2);
            }
            AbstractInput dtvInput = getInputByType(absInput2.getType());
            if (absInput2.isHidden(context) && dtvInput != null) {
                mSourceList.remove(dtvInput);
            }
            AbstractInput absInput3 = new AtvInput();
            if (!absInput3.isHidden(context) && getInputByType(absInput3.getType()) == null) {
                mSourceList.add(absInput3);
            }
            AbstractInput atvInput = getInputByType(absInput3.getType());
            if (absInput3.isHidden(context) && atvInput != null) {
                mSourceList.remove(atvInput);
            }
            List<TvInputInfo> tvInputList = mTvInputManager.getTvInputList();
            Collections.sort(tvInputList, new Comparator<TvInputInfo>() {
                public int compare(TvInputInfo lhs, TvInputInfo rhs) {
                    return lhs.getHdmiDeviceInfo() == null ? -1 : 1;
                }
            });
            Set<String> avInputIds = new HashSet<>();
            for (TvInputInfo input : tvInputList) {
                MtkLog.d(TAG, "[TIF] " + input.toString());
                switch (input.getType()) {
                    case 1001:
                        AbstractInput absInput4 = null;
                        Iterator<AbstractInput> it = mSourceList.iterator();
                        while (true) {
                            if (it.hasNext()) {
                                AbstractInput abstractInput = it.next();
                                if (input.getId().equals(abstractInput.getId())) {
                                    absInput4 = abstractInput;
                                }
                            }
                        }
                        if (absInput4 == null) {
                            mSourceList.add(new AVInput(input));
                        } else {
                            absInput4.preInit(input, input.getType());
                            absInput4.init(input, input.getType());
                        }
                        avInputIds.add(input.getId());
                        break;
                    case 1003:
                    case 1004:
                    case 1005:
                        AbstractInput absInput5 = getInputByType(input.getType());
                        if (absInput5 != null) {
                            absInput5.preInit(input, input.getType());
                            absInput5.init(input, input.getType());
                            break;
                        } else {
                            mSourceList.add(new AbstractInput(input, input.getType()));
                            break;
                        }
                    case 1007:
                        AbstractInput absInput6 = new HdmiInput(input);
                        MtkLog.d(TAG, "absInput update 1." + input + input.getParentId());
                        StringBuilder sb = new StringBuilder();
                        sb.append("absInput update 2.");
                        sb.append(absInput6.toString(context));
                        MtkLog.d(TAG, sb.toString());
                        if (getInputById(absInput6.getHardwareId()) == null) {
                            MtkLog.d(TAG, "absInput update add.");
                            mSourceList.add(absInput6);
                        } else {
                            MtkLog.d(TAG, "absInput update update.");
                            absInput6.preInit(input, input.getType());
                            absInput6.init(input, input.getType());
                        }
                        Iterator<AbstractInput> iterator2 = mSourceList.iterator();
                        while (true) {
                            if (iterator2.hasNext()) {
                                AbstractInput it2 = iterator2.next();
                                if (it2.getType() == 1007) {
                                    TvInputInfo info = it2.getTvInputInfo();
                                    if (input.getHdmiDeviceInfo() != null && it2.mTvInputInfo.getHdmiDeviceInfo() == null && TextUtils.equals(info.getId(), input.getParentId())) {
                                        iterator2.remove();
                                    }
                                }
                            }
                        }
                        MtkLog.d(TAG, "absInput update 3." + absInput6.toString(context));
                        break;
                }
            }
            Collections.sort(mSourceList);
            boolean z = true;
            if (avInputIds.size() <= 1) {
                z = false;
            }
            isMultiAVInputs = z;
            dump(context, mSourceList);
            dump(context, getSourceList(context));
        }
    }

    public static AbstractInput getInput(int id) {
        for (AbstractInput input : mSourceList) {
            if (id == input.getHardwareId()) {
                return input;
            }
        }
        return null;
    }

    public static AbstractInput getInput(Integer id) {
        if (id == null) {
            return null;
        }
        return getInput(id.intValue());
    }

    public static AbstractInput getInput(String id) {
        if (id == null) {
            return null;
        }
        return getInput(Integer.parseInt(id));
    }

    public static AbstractInput getInputByType(int type) {
        for (AbstractInput input : mSourceList) {
            if (type == input.getType()) {
                return input;
            }
        }
        return null;
    }

    public static AbstractInput getInputById(int hardwardId) {
        return getInput(hardwardId);
    }

    public static List<AbstractInput> getSourceList() {
        return mSourceList;
    }

    public static Map<Integer, String> getSourceList(Context context) {
        Map<Integer, String> nameMap = new ArrayMap<>();
        for (AbstractInput input : mSourceList) {
            String customLabel = input.getCustomSourceName(context);
            if (TextUtils.isEmpty(customLabel) || TextUtils.equals(customLabel, "null")) {
                nameMap.put(Integer.valueOf(input.getHardwareId()), input.getSourceName(context));
            } else {
                nameMap.put(Integer.valueOf(input.getHardwareId()), customLabel);
            }
        }
        return nameMap;
    }

    public static void updateState(String inputId, int state) {
        for (AbstractInput input : mSourceList) {
            if (input.getId().equals(inputId)) {
                input.updateState(state);
                return;
            }
        }
    }

    public static int checkInvalideInput(int id) {
        int result = id;
        if (getInput(id) == null) {
            Iterator<AbstractInput> iterator = mSourceList.iterator();
            while (true) {
                if (!iterator.hasNext()) {
                    break;
                }
                AbstractInput abstractInput = iterator.next();
                if ((id >> 16) == (abstractInput.getHardwareId() >> 16)) {
                    result = abstractInput.getHardwareId();
                    break;
                }
            }
        }
        MtkLog.d(TAG, "checkInvalideInput " + id + " -> " + result);
        return result;
    }

    public static boolean isTunerTypeByInputId(String inputId) {
        TvInputInfo tvInputInfo = mTvInputManager.getTvInputInfo(inputId);
        return tvInputInfo != null && tvInputInfo.getType() == 0;
    }

    public static void dump(Context context, List<AbstractInput> sourceList) {
        MtkLog.d(TAG, "dump start list.");
        for (AbstractInput input : sourceList) {
            MtkLog.d(TAG, input.toString(context));
        }
    }

    public static void dump(Context context, Map<Integer, String> sourceList) {
        if (sourceList != null) {
            MtkLog.d(TAG, "dump start map.");
            for (Map.Entry<Integer, String> entry : sourceList.entrySet()) {
                MtkLog.d(TAG, "key:" + entry.getKey() + "value:" + entry.getValue());
            }
        }
    }
}
