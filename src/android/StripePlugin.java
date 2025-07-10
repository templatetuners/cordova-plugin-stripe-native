package com.example.stripe;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.activity.ComponentActivity;

import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class StripePlugin extends CordovaPlugin {

    private static final String TAG = "StripePlugin";

    private PaymentSheet paymentSheet;
    private CallbackContext savedCallbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Activity activity = cordova.getActivity();
        if (activity instanceof ComponentActivity) {
            paymentSheet = new PaymentSheet((ComponentActivity) activity, result -> {
                if (savedCallbackContext != null) {
                    handleResult(result, savedCallbackContext);
                }
            });
        } else {
            Log.e(TAG, "Activity is not a ComponentActivity, PaymentSheet won't be initialized.");
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        savedCallbackContext = callbackContext;
        Activity activity = cordova.getActivity();

        switch (action) {
            case "init":
                return initStripe(args, callbackContext, activity);

            case "payWithIntent":
                return presentPaymentSheet(args, callbackContext, activity);

            case "setupPaymentSheet":
                return presentSetupSheet(args, callbackContext, activity);

            default:
                return false;
        }
    }

    private boolean initStripe(JSONArray args, CallbackContext callbackContext, Activity activity) {
        try {
            String publishableKey = args.getString(0);
            PaymentConfiguration.init(activity.getApplicationContext(), publishableKey);
            callbackContext.success("Stripe initialized");
            return true;
        } catch (JSONException e) {
            callbackContext.error("Invalid publishable key");
            return false;
        }
    }

    private boolean presentPaymentSheet(JSONArray args, CallbackContext callbackContext, Activity activity) {
        try {
            String clientSecret = args.getString(0);
            savedCallbackContext = callbackContext;  // actualizează callbackContext aici

            cordova.getActivity().runOnUiThread(() -> {
                if (paymentSheet != null) {
                    PaymentSheet.Configuration config = new PaymentSheet.Configuration("Your Company, Inc.");
                    paymentSheet.presentWithPaymentIntent(clientSecret, config);
                } else {
                    callbackContext.error("PaymentSheet not initialized.");
                }
            });

            return true;
        } catch (JSONException e) {
            callbackContext.error("Missing or invalid client secret");
            return false;
        }
    }

    private boolean presentSetupSheet(JSONArray args, CallbackContext callbackContext, Activity activity) {
        try {
            String clientSecret = args.getString(0);
            savedCallbackContext = callbackContext;

            cordova.getActivity().runOnUiThread(() -> {
                if (paymentSheet != null) {
                    PaymentSheet.Configuration config = new PaymentSheet.Configuration("Your Company, Inc.");
                    paymentSheet.presentWithSetupIntent(clientSecret, config);
                } else {
                    callbackContext.error("PaymentSheet not initialized.");
                }
            });

            return true;
        } catch (JSONException e) {
            callbackContext.error("Missing or invalid setup intent client secret");
            return false;
        }
    }

    private void handleResult(PaymentSheetResult result, CallbackContext callbackContext) {
        if (result instanceof PaymentSheetResult.Completed) {
            callbackContext.success("Success");
        } else if (result instanceof PaymentSheetResult.Canceled) {
            callbackContext.error("Canceled");
        } else if (result instanceof PaymentSheetResult.Failed) {
            PaymentSheetResult.Failed failed = (PaymentSheetResult.Failed) result;
            callbackContext.error("Failed: " + failed.getError().getMessage());
        }
    }
}
