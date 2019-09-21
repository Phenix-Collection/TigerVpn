package com.tigervpn.free.unblock.fast.vpn.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.tigervpn.free.unblock.fast.vpn.model.Server;
import com.tigervpn.free.unblock.fast.vpn.util.PropertiesService;
import com.tigervpn.free.unblock.fast.vpn.util.Stopwatch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;

public class MySplashsActivity extends ParentActivity {

    private ProgressBar progressBar;
    private TextView commentsText;
    private static boolean loadStatus = false;
    private Handler updateHandler;

    private final int LOAD_ERROR = 0;
    private final int DOWNLOAD_PROGRESS = 1;
    private final int PARSE_PROGRESS = 2;
    private final int LOADING_SUCCESS = 3;
    private final int SWITCH_TO_RESULT = 4;
    private final String BASE_URL = "http://www.vpngate.net/api/iphone/";
    private final String BASE_FILE_NAME = "vpngate.csv";

    private boolean premiumStage = true;

    private int percentDownload = 0;
    private Stopwatch stopwatch;
    private PreferenceManager prefManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        if (checkWifiOnAndConnected() == true) {
            Toasty.success(MySplashsActivity.this, "Wifi Connected!", Toast.LENGTH_SHORT, true).show();

        } else {
            Toasty.error(MySplashsActivity.this, "Sorry there is no Wifi connection", Toast.LENGTH_SHORT, true).show();

            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


        progressBar = findViewById(R.id.spin_kit);
        Sprite foldingCube = new FoldingCube();
        progressBar.setIndeterminateDrawable(foldingCube);
//  progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setMax(200);
        progressBar.setProgress(0);

        Thread thread = new Thread() {

            public void run() {

                try {
                    for (int i = 0; i < 200; i++) {
                        progressBar.setProgress(i);
                        sleep(10);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // InterstitialAdmob();
//                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//                    startActivity(intent);
                    Intent n = new Intent(MySplashsActivity.this, MainActivity.class);
                    startActivity(n);
                    finish();
                }
            }
        };
        thread.start();

    }
    private void parseCSVFile(String fileName) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(getCacheDir().getPath().concat("/").concat(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.arg1 = LOAD_ERROR;
            msg.arg2 = R.string.csv_file_error;
            updateHandler.sendMessage(msg);
        }
        if (reader != null) {
            try {
                int startLine = 2;
                int type = 0;


                dbHelper.clearTable();


                int counter = 0;
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (counter >= startLine) {
                        dbHelper.putLine(line, type);
                    }
                    counter++;

                }

                Message end = new Message();
                end.arg1 = LOADING_SUCCESS;
                updateHandler.sendMessageDelayed(end, 1);


            } catch (Exception e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.arg1 = LOAD_ERROR;
                msg.arg2 = R.string.csv_file_error_parsing;
                updateHandler.sendMessage(msg);
            }
        }
    }


}
