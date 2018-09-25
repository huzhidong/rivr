/*
 * Copyright (c) 2013 Nu Echo Inc. All rights reserved.
 */
package com.nuecho.rivr.voicexml.turn.input;

import javax.json.*;

import com.nuecho.rivr.voicexml.turn.output.*;
import com.nuecho.rivr.voicexml.util.json.*;

/**
 * The status of a {@link Transfer}.
 * 
 * @author NuEcho Inc.
 * @see <a
 *      href="https://www.w3.org/TR/voicexml20/#dml2.3.7.2.2">https://www.w3.org/TR/voicexml20/#dml2.3.7.2.2</a>
 */
public class TransferStatus implements JsonSerializable {

    public static final String NO_ANSWER = "noanswer";
    public static final String NEAR_END_DISCONNECT = "near_end_disconnect";
    public static final String FAR_END_DISCONNECT = "far_end_disconnect";
    public static final String NETWORK_DISONNECT = "network_disconnect";
    public static final String MAXTIME_DISCONNECT = "maxtime_disconnect";
    public static final String BUSY = "busy";
    public static final String NETWORK_BUSY = "network_busy";
    public static final String UNKNOWN = "unknown";

    private final String mStatusCode;

    public TransferStatus(String statusCode) {
        mStatusCode = statusCode;
    }

    public String getStatusCode() {
        return mStatusCode;
    }

    @Override
    public JsonValue asJson() {
        return JsonUtils.wrap(mStatusCode);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mStatusCode == null) ? 0 : mStatusCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TransferStatus other = (TransferStatus) obj;
        if (mStatusCode == null) {
            if (other.mStatusCode != null) return false;
        } else if (!mStatusCode.equals(other.mStatusCode)) return false;
        return true;
    }

}