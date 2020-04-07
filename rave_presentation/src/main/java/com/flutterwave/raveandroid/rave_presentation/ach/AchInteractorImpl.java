package com.flutterwave.raveandroid.rave_presentation.ach;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.NullFeeCheckListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

class AchInteractorImpl implements AchContract.Interactor {

    private AchPaymentCallback callback;
    private String flwRef;
    private FeeCheckListener feeCheckListener;


    AchInteractorImpl(AchPaymentCallback callback) {
        this.callback = (callback != null) ? callback : new NullAchPaymentCallback();
        this.feeCheckListener = new NullFeeCheckListener();
    }

    @Override
    public void showProgressIndicator(boolean active) {
        callback.showProgressIndicator(active);
    }

    @Override
    public void onTransactionFeeRetrieved(String chargeAmount, Payload payload, String fee) {
        feeCheckListener.onTransactionFeeFetched(chargeAmount, fee);
    }

    @Override
    public void showWebView(String authUrl, String flwRef) {
        this.flwRef = flwRef;
        callback.showAuthenticationWebPage(authUrl);
    }

    @Override
    public void onPaymentSuccessful(String responseAsJSONString) {
        callback.onSuccessful(flwRef);
    }

    @Override
    public void onPaymentFailed(String responseAsJSONString) {
        try {
            Type type = new TypeToken<JsonObject>() {
            }.getType();
            JsonObject responseJson = new Gson().fromJson(responseAsJSONString, type);
            flwRef = responseJson.getAsJsonObject("data").get("flwref").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        callback.onError("Transaction Failed", flwRef);
    }

    @Override
    public void onPaymentError(String errorMessage) {
        callback.onError(errorMessage, null);
    }

    @Override
    public void onFeeFetchError(String errorMessage) {
        feeCheckListener.onFetchFeeError(errorMessage);
    }

    public String getFlwRef() {
        return flwRef;
    }

    public void setFeeCheckListener(FeeCheckListener feeCheckListener) {
        this.feeCheckListener = (feeCheckListener != null) ? feeCheckListener : new NullFeeCheckListener();
    }
}
