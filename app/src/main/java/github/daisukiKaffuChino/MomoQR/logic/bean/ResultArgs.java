package github.daisukiKaffuChino.MomoQR.logic.bean;

import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

public class ResultArgs {

    private static final String RECIPIENT_ID = "resultArgsRecipientId";

    private static final String REQUEST_CODE = "ResultArgsRequestCode";

    private static final String BUNDLE = "ResultArgsBundle";

    private final Map<String, Object> mArgsMap = new HashMap<>();

    public ResultArgs(int recipientId, int requestCode) {
        mArgsMap.put(RECIPIENT_ID, recipientId);
        mArgsMap.put(REQUEST_CODE, requestCode);
    }

    public ResultArgs(Bundle bundle) {
        if (null == bundle) {
            return;
        }
        setBusinessArgs(bundle);
        mArgsMap.put(RECIPIENT_ID, bundle.getInt(RECIPIENT_ID));
        mArgsMap.put(REQUEST_CODE, bundle.getInt(REQUEST_CODE));
    }

    public Bundle toBundle() {
        Bundle temp = new Bundle();
        if (null != getBusinessArgs()) {
            temp.putAll(getBusinessArgs());
        }
        temp.putInt(RECIPIENT_ID, getRecipientId());
        temp.putInt(REQUEST_CODE, getRequestCode());
        return temp;
    }

    public int getRecipientId() {
        Object obj = mArgsMap.get(RECIPIENT_ID);
        assert obj != null;
        return (int) obj;
    }

    public int getRequestCode() {
        Object obj = mArgsMap.get(REQUEST_CODE);
        assert obj != null;
        return (int) obj;
    }

    public ResultArgs setBusinessArgs(Bundle businessArgs) {
        mArgsMap.put(BUNDLE, businessArgs);
        return this;
    }

    public Bundle getBusinessArgs() {
        return (Bundle) mArgsMap.get(BUNDLE);
    }

}

