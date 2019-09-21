package com.tigervpn.free.unblock.fast.vpn.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.tigervpn.free.unblock.fast.vpn.model.Server;
import com.tigervpn.free.unblock.fast.vpn.util.PropertiesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.blinkt.openvpn.core.OpenVPNService;
import es.dmoral.toasty.Toasty;


public class MainActivity extends ParentActivity implements RewardedVideoAdListener, NavigationView.OnNavigationItemSelectedListener {

    DecoView arcView, arcView2;
    public static final String EXTRA_COUNTRY = "country";
    private PopupWindow popupWindow;
    private RelativeLayout homeContextRL;
    private static OpenVPNService mVPNService;
    Button positive;
    Button neutral;
    Button negative;
    private List<Server> countryList;
    ObjectAnimator textColorAnim, textColorAnim1;
    PreferenceManager prefManager;
    CardView mCardViewShare, mCardViewShare1;
    Intent i;
    public static TextView hello;
    TextView heading;
    public void exit() {

        // Build an AlertDialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom, null);

        // Set the custom layout as alert active_translator view
        builder.setView(dialogView);

        // Get the custom alert active_translator view widgets reference
        positive = dialogView.findViewById(R.id.dialog_positive_btn);
        neutral = dialogView.findViewById(R.id.exit);


        // Create the alert active_translator
        final AlertDialog dialog = builder.create();

        // Set positive/yes button click listener
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                //    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));

            }
        });


        //Neutral Button
        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finishAffinity();
                moveTaskToBack(true);

            }
        });



        // Display the custom alert active_translator on interface
/*

      */
/*  final AdView ad = (AdView) dialogView.findViewById(R.id.larban);
      *//*
  AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
                ad.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
//                super.onAdLoaded();
                ad.setVisibility(View.VISIBLE);
            }
        });
        ad.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                AdRequest adRequest = new AdRequest.Builder().build();
                ad.setVisibility(View.VISIBLE);
                ad.loadAd(adRequest);
            }

            @Override
            public void onAdFailedToLoad(int error) {
                ad.setVisibility(View.GONE);
            }

        });

*/

        dialog.show();

    }

    @Override

    public void onBackPressed() {
        //super.onBackPressed();

        exit();

    }

    private boolean checkWifiOnAndConnected() {

        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if (wifiInfo.getNetworkId() == -1) {
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        } else {
            return false; // Wi-Fi adapter is OFF
        }
    }

    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }
        });
        prefManager = new PreferenceManager(MainActivity.this);
        if (prefManager.isFirstTimeLaunch()) {
            prefManager.setFirstTimeLaunch(false);

        }
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        loadRewardedVideoAd();

        hello = findViewById(R.id.elapse2);
//        heading = findViewById(R.id.heading);
//        textColorAnim1 = ObjectAnimator.ofInt(heading, "textColor", Color.BLACK, Color.TRANSPARENT);
//        textColorAnim1.setDuration(1000);
//        textColorAnim1.setEvaluator(new ArgbEvaluator());
//        textColorAnim1.setRepeatCount(ValueAnimator.INFINITE);
//        textColorAnim1.setRepeatMode(ValueAnimator.REVERSE);
//        textColorAnim1.start();
//        textColorAnim = ObjectAnimator.ofInt(hello, "textColor", Color.BLACK, Color.TRANSPARENT);
//        textColorAnim.setDuration(1000);
//        textColorAnim.setEvaluator(new ArgbEvaluator());
//        textColorAnim.setRepeatCount(ValueAnimator.INFINITE);
//        textColorAnim.setRepeatMode(ValueAnimator.REVERSE);
//        textColorAnim.start();
//
        MobileAds.initialize(this, String.valueOf(R.string.admob_app_id));

        homeContextRL = (RelativeLayout) findViewById(R.id.homeContextRL);
        countryList = dbHelper.getUniqueCountries();

        AdView mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdMobAdView.loadAd(adRequest);
        mAdMobAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdMobAdView.loadAd(adRequest);
//                Toast.makeText(MainActivity.this, "Add Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLeftApplication() {
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdMobAdView.loadAd(adRequest);
            }

            @Override
            public void onAdClosed() {
            }
        });

        final InterstitialAd mInterstitial = new InterstitialAd(this);
        mInterstitial.setAdUnitId(getString(R.string.interstitial_ad_unit));
        mInterstitial.loadAd(new AdRequest.Builder().build());
        mInterstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // TODO Auto-generated method stub
                super.onAdLoaded();
                if (mInterstitial.isLoaded()) {
                    mInterstitial.show();
                }
            }
        });

        if (ParentActivity.connectedServer == null) {
            hello.setText("No VPN Connected");
//            heading.setVisibility(View.VISIBLE);

        } else {
            hello.setText("Connected");
            hello.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mVPNService = null;
                }
            });
//            heading.setVisibility(View.GONE);
        }


        long totalServ = dbHelper.getCount();


        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#00000000"))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();

        SeriesItem seriesItem2 = new SeriesItem.Builder(Color.parseColor("#ffffff"))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();

        Random ran2 = new Random();


        mCardViewShare = (CardView) findViewById(R.id.homeBtnChooseCountry1);

        mCardViewShare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finishAffinity();
            }
        });
        ImageView hi = findViewById(R.id.hi);
        hi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Server randomServer = getRandomServer();
                if (randomServer != null) {
                    newConnecting(randomServer, true, true);
                } else {
                    String randomError = String.format(getResources().getString(R.string.error_random_country), PropertiesService.getSelectedCountry());
                    Toasty.error(MainActivity.this, randomError, Toast.LENGTH_LONG, true).show();
                }


            }
        });
        mCardViewShare1 = (CardView) findViewById(R.id.share);

        mCardViewShare1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                i = new Intent();
                i.setAction(Intent.ACTION_SEND);
                i.setType("text/plain");
                final String text = "Check out "
                        + getResources().getString(R.string.app_name)
                        + ", The Fastest Free VPN in all over the world. " + getResources().getString(R.string.app_name) + ". https://play.google.com/store/apps/details?id="
                        + getPackageName();
                i.putExtra(Intent.EXTRA_TEXT, text);
                Intent sender = Intent.createChooser(i, "Share " + getResources().getString(R.string.app_name));
                startActivity(sender);

            }
        });


        CardView button1 = (CardView) findViewById(R.id.homeBtnRandomConnection);
        button1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();

                } else {
                    sendTouchButton("homeBtnRandomConnection");
                    chooseCountry1();
                }

            }
        });

        CardView button2 = (CardView) findViewById(R.id.homeBtnChooseCountry);
        button2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                sendTouchButton("homeBtnChooseCountry");
                chooseCountry();

            }
        });


    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-8894600796730923/8813232748",
                new AdRequest.Builder().build());
        mRewardedVideoAd.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mRewardedVideoAd.resume(this);

        if (ParentActivity.connectedServer == null) {
            TextView hello = findViewById(R.id.elapse2);
            hello.setText("No VPN Connected");
        } else {
            TextView hello = findViewById(R.id.elapse2);
            hello.setText("No VPN Connected");

        }

        invalidateOptionsMenu();


    }

    @Override
    protected void onDestroy() {
        mRewardedVideoAd.destroy(this);

        super.onDestroy();
    }


    @Override
    protected boolean useHomeButton() {
        return true;
    }

    public void homeOnClick(View view) {
        switch (view.getId()) {
            case R.id.homeBtnChooseCountry:
                sendTouchButton("homeBtnChooseCountry");
                chooseCountry();
                break;
            case R.id.homeBtnRandomConnection:
                sendTouchButton("homeBtnRandomConnection");
                Server randomServer = getRandomServer();
                if (randomServer != null) {
                    newConnecting(randomServer, true, true);
                } else {
                    String randomError = String.format(getResources().getString(R.string.error_random_country), PropertiesService.getSelectedCountry());
                    Toasty.error(MainActivity.this, randomError, Toast.LENGTH_LONG, true).show();
                }
                break;
        }

    }

    private void chooseCountry() {
        View view = initPopUp(R.layout.choose_country, 0.6f, 0.8f, 0.8f, 0.7f);

        final List<String> countryListName = new ArrayList<String>();
        for (Server server : countryList) {
            String localeCountryName = localeCountries.get(server.getCountryShort()) != null ?
                    localeCountries.get(server.getCountryShort()) : server.getCountryLong();
            countryListName.add(localeCountryName);
        }

        ListView lvCountry = (ListView) view.findViewById(R.id.homeCountryList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, countryListName);

        lvCountry.setAdapter(adapter);
        lvCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                onSelectCountry(countryList.get(position));
            }
        });

        popupWindow.showAtLocation(homeContextRL, Gravity.CENTER, 0, 0);
    }


    private void chooseCountry1() {

        View view = initPopUp(R.layout.choose_country_vip, 1.0f, 1.0f, 1.0f, 1.0f);
        final int[] listviewImage = new int[]{
                R.drawable.ae};

        final List<String> my = new ArrayList<String>();
        final List<String> countryListName = new ArrayList<String>();
        for (Server server : countryList) {
            String localeCountryName = localeCountries.get(server.getCountryShort()) != null ?
                    localeCountries.get(server.getCountryShort()) : server.getCountryLong();

            countryListName.add(localeCountryName);

        }


        ListView lvCountry = (ListView) view.findViewById(R.id.homeCountryList1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, countryListName);

        lvCountry.setAdapter(adapter);
        lvCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                onSelectCountry(countryList.get(position));
            }
        });

        popupWindow.showAtLocation(homeContextRL, Gravity.CENTER, 0, 0);
    }


    private View initPopUp(int resourse,
                           float landPercentW,
                           float landPercentH,
                           float portraitPercentW,
                           float portraitPercentH) {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resourse, null);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            popupWindow = new PopupWindow(
                    view,
                    (int) (widthWindow * landPercentW),
                    (int) (heightWindow * landPercentH)
            );
        } else {
            popupWindow = new PopupWindow(
                    view,
                    (int) (widthWindow * portraitPercentW),
                    (int) (heightWindow * portraitPercentH)
            );
        }


        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        return view;
    }

    private void onSelectCountry(Server server) {
        Intent intent = new Intent(getApplicationContext(), mytigerListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        intent.putExtra(EXTRA_COUNTRY, server.getCountryShort());
        startActivity(intent);
        finish();
    }


    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }


    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }


    @Override
    public void onRewardedVideoAdLoaded() {
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

        loadRewardedVideoAd();

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {


    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {


    }

    @Override
    public void onRewardedVideoCompleted() {
        Toasty.success(MainActivity.this, "Congratulations You have Rewarded For using This Server!", Toast.LENGTH_LONG, true).show();


    }

    @Override
    protected void onPause() {

        mRewardedVideoAd.pause(this);

        super.onPause();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));


        } else if (id == R.id.nav_rate_us) {
            final com.codemybrainsout.ratingdialog.RatingDialog ratingDialog = new com.codemybrainsout.ratingdialog.RatingDialog.Builder(this)
                    .threshold(4)
                    .ratingBarColor(R.color.yellow)
                    .onRatingBarFormSumbit(new com.codemybrainsout.ratingdialog.RatingDialog.Builder.RatingDialogFormListener() {
                        @Override
                        public void onFormSubmitted(String feedback) {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                    "mailto", "superappsworld512@gmail.com", null));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, feedback);
                            startActivity(Intent.createChooser(emailIntent, "Send Email"));
                        }
                    }).build();

            ratingDialog.show();


        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            String shareBody = "Fast Tiger VPN Free. https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Share App");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

            startActivity(Intent.createChooser(sharingIntent, "Share via"));

            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
}
