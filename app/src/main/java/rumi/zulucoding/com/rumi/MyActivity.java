package rumi.zulucoding.com.rumi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;

import rumi.zulucoding.com.rumi.Rateme.AppRater;
import rumi.zulucoding.com.rumi.TouchFilter.SimpleGestureListener;

public class MyActivity extends Activity implements SimpleGestureListener {

    // UI Elements
    public Boolean fullscreen_webview = AppContent.fullscreen_webview;
    private TextView quoteView;
    private ImageView bg_trans;
    private ImageView bg_main;
    private ImageButton share;

    // Context
    private Context myApp;

    // Random Wallpaper name
    String randomWallpaperName = null;

    // Random font
    Typeface customFont;

    // Random font name
    String randomFontName = null;

    // Logs
    String tag = "appState";

    // Functions
    Functions mFunctions;
    ConnectionDetector mConnectionDetector;

    // Google Play Ads
    private AdView adView;
    private InterstitialAd interstitial;
    String admobBannerID = AppContent.google_play_ads_banner_id;
    String admobInterstitialID = AppContent.google_play_ads_interstitial_id;
    Boolean admobBannerActive = AppContent.google_play_ads_banner_active;
    Boolean admobInterstitialActive = AppContent.google_play_ads_interstitial_active;

    // Rate me
    Boolean rateActive = AppContent.rate_dialog_active;

    // Connection
    String connection_error_message = AppContent.connection_error_message;
    public Boolean isConnected = false;

    // share
    String shareMessage = AppContent.share_message;

    // Animation
    private Animation bounce;

    // Touch Filter
    private TouchFilter detector;

    // Quotes Array
    private String[] quotesAdapter;

    // Quotes Tracker
    private int QTracker = -1;

    // Quotes Length
    private int quotes_length = -1;

    // Shared Pref
    SharedPreferences pref;

    // Pref Editor
    SharedPreferences.Editor pref_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //===>> Logging
        Log.d(tag,"In OnCreate Event()");

        // Hiding the Action Bar for different android versions
        hideActionBar();

        // Set content view
        setContentView(R.layout.activity_my);

        // Passing Context
        myApp = getApplicationContext();

        // Loading UI
        UI();

        // Adding Animation
        bounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);

        // Declaring mConnection Detector
        mConnectionDetector = new ConnectionDetector(getApplicationContext());

        // Declaring mFunction
        mFunctions = new Functions(getApplicationContext());

        // Declaring TouchFilter Detector
        // Detect touched area
        detector = new TouchFilter(this,this);

        // Check Internet, User and Form State
        isConnected = mConnectionDetector.isConnectingToInternet();

        if(isConnected == false){
            // To do
        }else {
            // To do
        }

        // RateApp
        if (rateActive == true) {
            AppRater.app_rate(this);
        }

        // GooglePlayAds
        if (admobBannerActive) {
            admob_banner_block();
        }

        if (admobInterstitialActive) {
            admob_interstitial_block();
        }

        // Passing XML list to array
        quotesAdapter = getResources().getStringArray(R.array.quotes_array);

        // Checking saved QTracker
        pref = getApplicationContext().getSharedPreferences("QuotesPref", 0); // 0 - for private mode
        QTracker = pref.getInt("last_quote", 0);

        // Display Quote
        //quoteView.setText(mFunctions.upperCaseFirst(quotesAdapter[QTracker]));

        quotes_length = quotesAdapter.length -1;
        randomQuote();
        randomWallpaper();
        randomFont();

        //Share icon
        shareOnClick();
    }

    public void onStart(){
        super.onStart();
        Log.d(tag, "In the onStart() event");
    }

    public void onRestart(){
        super.onRestart();
        Log.d(tag, "In the onRestart() event");
    }

    public void onResume(){
        super.onResume();
        Log.d(tag, "In the onResume() event");

        // Resume Google Play Ads
        if (adView != null) {
            adView.resume();
        }
    }

    public void onPause(){
        super.onPause();
        Log.d(tag, "In the onPause() event");
    }

    public void onStop(){
        super.onStop();
        Log.d(tag, "In the onStop() event");

        // Pause Google Play Ads
        if (adView != null) {
            adView.pause();
        }

        // Saving the last viewed quote number
        pref_editor = pref.edit();
        pref_editor.putInt("QuotesPref", QTracker);
        pref_editor.commit();
    }

    public void onDestroy(){
        super.onDestroy();
        Log.d(tag, "In the onDestroy() event");

        // Destroy Google Play Ads
        if (adView != null) {
            adView.destroy();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_share){
            sharepost();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // UI function
    public void UI(){

       //UI elements decliration
        quoteView = (TextView)findViewById(R.id.quoteView);
        bg_trans = (ImageView)findViewById(R.id.bg_trans);
        bg_main = (ImageView)findViewById(R.id.bg_main);
        share = (ImageButton)findViewById(R.id.share_);

        try {
            bg_trans.setImageDrawable(mFunctions.getAssetImagePng(myApp, "overlay"));
        } catch (IOException e){
            Log.d(tag,"can not load images");
        }

        try {
            bg_main.setImageDrawable(mFunctions.getAssetImageJpg(myApp, "img_1"));
        } catch (IOException e){
            Log.d(tag,"can not load images");
        }

        Log.d(tag, "Finished loading UI");
    }

    // Hide action bar
    @SuppressLint("NewApi")
    public void hideActionBar(){
        if (Build.VERSION.SDK_INT < 16 && fullscreen_webview == true) {
            // Hide the Action Bar on Android 4.0 and Lower
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else if (fullscreen_webview == true) {
            // Hide the Action Bar on Android 4.1 and Higher
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            android.app.ActionBar actionBar = getActionBar();
            actionBar.hide();
        }
    }

    // KeyDown_Back
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Log.d(tag, "Pressed Back");
            exitDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Exit Dialog
    public void exitDialog() {
        Log.d(tag, "In the exitDialog()");
        AlertDialog.Builder exitAlertDialog = new AlertDialog.Builder(MyActivity.this);

        exitAlertDialog.setTitle("Confirm Exit")
                .setMessage("Do you want to quit?")
                .setPositiveButton("Okay", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //===>> Logging
                        Log.d(tag, "Exit Dialog = true");
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //===>> Logging
                        Log.d(tag, "Exit Dialog = false");
                    }
                }).create();

        exitAlertDialog.show();
    }

    // Share Page
    private void sharepost() {
        Intent shareintent = new Intent(Intent.ACTION_SEND);
        shareintent.setType("text/plain");
        String xshare = "link";

            xshare = shareMessage + " \" " + (quotesAdapter[QTracker].toUpperCase()) +  " \" "; //add other condition

        shareintent.putExtra(Intent.EXTRA_TEXT, xshare);
        startActivity(Intent.createChooser(shareintent, "How do you want to share?"));
    }

    // Google Play Ads (banner)
    public void admob_banner_block() {
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(admobBannerID);
        LinearLayout layout = (LinearLayout) findViewById(R.id.banner_ads_layout);
        layout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    // Google Play Ads (interstitial)
    public void admob_interstitial_block() {
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(admobInterstitialID);
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                displayInterstitial();
            }
            public void onAdClosed() {
                // On Ads close
            }
        });
    }

    // Google Play Ads - interstitial (Display).
    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of TouchFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {

        if (direction == TouchFilter.SWIPE_RIGHT){

            if (QTracker == 0) {
                QTracker = quotes_length;
            } else {
                QTracker = QTracker -1;
            }

        } else if (direction == TouchFilter.SWIPE_LEFT){

            if (QTracker == quotes_length) {
                QTracker = 0;
            } else {
                QTracker = QTracker +1;
            }

        } else if (direction == TouchFilter.SWIPE_DOWN){

        } else if (direction == TouchFilter.SWIPE_UP){

        }

        randomQuote();
        randomWallpaper();
        randomFont();
    }

    @Override
    public void onDoubleTap() {
      //  Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

    public void randomQuote(){
        quoteView.setText(quotesAdapter[QTracker].toUpperCase());
        quoteView.startAnimation(bounce);
    }

    public void randomWallpaper(){

        // Generating Random name
        randomWallpaperName = "img_" + String.valueOf((mFunctions.randomizeInRange(9)+1));

        // Changing the wallpaper
        try {
            bg_main.setImageDrawable(mFunctions.getAssetImageJpg(myApp, randomWallpaperName));
        } catch (IOException e){
            Log.d(tag,"can not load images");
        }
    }

    public void randomFont(){
        // Generating random name
        randomFontName = "font/font_" + String.valueOf((mFunctions.randomizeInRange(5)+1) + ".ttf");

        // Changing Font
        customFont = Typeface.createFromAsset(getAssets(),randomFontName);
        quoteView.setTypeface(customFont);
    }

    public void shareOnClick(){

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharepost();
            }
        });


    }

}


