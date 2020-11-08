package com.mediatek.dm;

public class DMNativeDaemonConnectorException extends RuntimeException {
    private String mCmd;
    private int mCode = -1;

    public DMNativeDaemonConnectorException() {
    }

    public DMNativeDaemonConnectorException(String error) {
        super(error);
    }

    public DMNativeDaemonConnectorException(int code, String cmd, String error) {
        super(String.format("Cmd {%s} failed with code %d : {%s}", new Object[]{cmd, Integer.valueOf(code), error}));
        this.mCode = code;
        this.mCmd = cmd;
    }

    public int getCode() {
        return this.mCode;
    }

    public String getCmd() {
        return this.mCmd;
    }
}
