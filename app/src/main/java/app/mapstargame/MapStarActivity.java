package app.mapstargame;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import app.mapstargame.util.IabHelper;
import app.mapstargame.util.IabResult;
import app.mapstargame.util.Inventory;

/**
 * Created by blizz on 8/27/2016.
 */
public class MapStarActivity extends Activity {
    private static String TAG = "com.mapgame.mapstaractivity";
    public static String SKU_PREMIUM = "1003";
    private AdView mAdView;
    IabHelper mHelper;

    public boolean soundsOn = true;
    public int timeOfLevel;
    public boolean mIsPremium;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String base64EncodedPublicKeyBeginning = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjywo9mDXPNOX";
        String base64EncodedPublicKeyMiddle = "6yaR8qzw24H2GsPz4Ksx8B3lrFq6TefeOan01MtAVOnMFml0HhfhCPnUEziZU1vwpJzIwO4/I78sj+aUtl/rgZy9bO+IzyzjHTn2hIwaOFNwk5gD+NzYxMhUHS1dk6PaE+vaZiWiRlsk0Mh9NAa+70ycwYRL8LwoF60+bpwarhilCCAmIKA/4w1";
        String base64EncodedPublicKeyEnd = "ezERQTMx2puXe++DCqArzFt4c0LSvfozubZq7nFAHFwpWFPbHVWm+Xgk+CYD8HYBVPu+/zVWr3IsZqnr44MBLnPZHI7xYHec3FPW0yXq1HR/Mn3xlwZq7d8nomHXZMl/1XgQFyVBsbGypVAouMQIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKeyBeginning +
                base64EncodedPublicKeyMiddle +
                base64EncodedPublicKeyEnd);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                }
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    Log.d(TAG, "Failure querying purchases");
                }
            }
        });

        setContentView(R.layout.mapstar_activity);

        if (!mIsPremium) {
            String android_id = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            String deviceId = md5(android_id).toUpperCase();
            MobileAds.initialize(this, "ca-app-pub-1409082372034038~7018418709");
            mAdView = (AdView) findViewById(R.id.ad_view);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainMenuFragment fragment = new MainMenuFragment();
        fragmentTransaction.add(R.id.mapstar_fragment, fragment, fragment.toString());
        fragmentTransaction.commit();

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setFillEnabled(true);
        animationSet.setInterpolator(new DecelerateInterpolator());

        ImageView cloud1 = (ImageView) findViewById(R.id.cloud1);
        ImageView cloud2 = (ImageView) findViewById(R.id.cloud2);
        ImageView cloud3 = (ImageView) findViewById(R.id.cloud3);
        ImageView cloud4 = (ImageView) findViewById(R.id.cloud4);
        ImageView cloud5 = (ImageView) findViewById(R.id.cloud5);
        ImageView cloud6 = (ImageView) findViewById(R.id.cloud6);

        TranslateAnimation animation1 = new TranslateAnimation(0, 2400, 0, 0);
        animation1.setDuration(40000);
        animation1.setFillAfter(true);
        TranslateAnimation animation2 = new TranslateAnimation(0, 1800, 0, 0);
        animation2.setDuration(40000);
        animation2.setFillAfter(true);
        TranslateAnimation animation3 = new TranslateAnimation(0, 1200, 0, 0);
        animation3.setDuration(40000);
        animation3.setFillAfter(true);
        TranslateAnimation animation4 = new TranslateAnimation(0, 3000, 0, 0);
        animation4.setDuration(40000);
        animation4.setFillAfter(true);

        cloud1.startAnimation(animation2);
        cloud2.startAnimation(animation1);
        cloud3.startAnimation(animation3);
        cloud4.startAnimation(animation3);
        cloud5.startAnimation(animation1);
        cloud6.startAnimation(animation1);
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }

        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.d(TAG, "Problem destroying In App Billing helper onDestroy");
        }
        mHelper = null;

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                // handle error here
            }
            else {
                // does the user have the premium upgrade?
                mIsPremium = inventory.hasPurchase(SKU_PREMIUM);
                if (mIsPremium) {
                    if (mAdView != null) {
                        mAdView.setVisibility(View.GONE);
                        mAdView.destroy();
                    }
                }
            }
        }
    };

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }
}
