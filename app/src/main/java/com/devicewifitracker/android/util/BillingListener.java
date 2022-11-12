package com.devicewifitracker.android.util;

public interface BillingListener {
    void canUseBilling(boolean canUse);

    void isSubscribe(boolean subscribe);

    void subscribeSuccess();

    void subscribeError(int code,String error);

    void launchBillingResponse(boolean success);

    void skuResponse();
}
