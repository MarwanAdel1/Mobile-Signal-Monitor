package com.example.android.fireapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ABDO on 15/4/2018.
 */

public class BaseActivity extends Activity {
    public static boolean soundflag = false;
    public static boolean notificationflag = false;

    public static NotificationManager notificationManager;
    public static Notification notification;

    protected static final String TAG = BaseActivity.class.getName();

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sp_ic);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //   Uri sound_notification = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sound1);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.sp_ic);
        mBuilder.setLargeIcon(bitmap);
        mBuilder.setContentTitle("Alert signal");
        mBuilder.setContentText("you will enter a dead zone");
        mBuilder.setSound(soundUri);

        notification = mBuilder.getNotification();
    }

    public static boolean isAppWentToBg = false;

    public static boolean isWindowFocused = false;

    public static boolean isMenuOpened = false;

    public static boolean isBackPressed = false;
    public static boolean flag_dialog = false;
    public static boolean flag_notification = false;
    // public static boolean notification=false;

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart isAppWentToBg " + isAppWentToBg);
        super.onStart();
        applicationWillEnterForeground();
        soundflag = true;
        flag_notification=false;
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop ");
        applicationdidenterbackground();
        notificationflag = true;
        soundflag=false;
    }

    private void applicationWillEnterForeground() {
        if (isAppWentToBg) {
            isAppWentToBg = false;
            Toast.makeText(getApplicationContext(), "App is in foreground",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void applicationdidenterbackground() {
        if (!isWindowFocused) {
            isAppWentToBg = true;


            flag_notification = true;
            ///////////
        }
    }

    @Override
    public void onBackPressed() {

        if (this instanceof MainActivity) {

        } else {
            isBackPressed = true;
        }

        Log.d(TAG,
                "onBackPressed " + isBackPressed + ""
                        + this.getLocalClassName());
        super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        isWindowFocused = hasFocus;

        if (isBackPressed && !hasFocus) {
            isBackPressed = false;
            isWindowFocused = true;
        }

        super.onWindowFocusChanged(hasFocus);
    }

//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                break;
//            case R.id.action_settings:
//                Intent i = new Intent(this, SettingActivity.class);
//                startActivity(i);
//                break;
//        }
//        return true;
//    }

//    @Override
//    public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                break;
//            case R.id.action_settings:
//                Intent i = new Intent(this, SettingActivity.class);
//                startActivity(i);
//                break;
//        }
//        return true;
//    }

}


