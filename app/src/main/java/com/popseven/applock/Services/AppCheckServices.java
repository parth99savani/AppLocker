package com.popseven.applock.Services;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.popseven.applock.Adapter.CameraFuncation;
import com.popseven.applock.PasswordRecoveryActivity;
import com.popseven.applock.Prefrence.SharedPreference;
import com.popseven.applock.R;
import com.popseven.applock.Utils.AppLockConstants;
import com.takwolf.android.lock9.Lock9View;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class AppCheckServices extends Service {

    public static final String TAG = "AppCheckServices";
    private Context context = null;
    private Timer timer;
    ImageView imageView;
    private WindowManager windowManager;
    private Dialog dialog;
    public static String currentApp = "";
    public static String previousApp = "";
    private SharedPreference sharedPreference;
    private List<String> pakageName;
    private SharedPreferences sharedPreferences;
    private CameraFuncation cameraFuncation;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sharedPreference = new SharedPreference();
        if (sharedPreference != null) {
            pakageName = sharedPreference.getLocked(context);
        }
        timer = new Timer("AppCheckServices");
        timer.schedule(updateTask, 1000L, 1000L);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        imageView = new ImageView(this);
        imageView.setVisibility(View.GONE);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = ((getApplicationContext().getResources().getDisplayMetrics().widthPixels) / 2);
        params.y = ((getApplicationContext().getResources().getDisplayMetrics().heightPixels) / 2);
        windowManager.addView(imageView, params);

    }

    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            if (sharedPreference != null) {
                pakageName = sharedPreference.getLocked(context);
            }
            if (isConcernedAppIsInForeground()) {
                Log.d("isConcernedAppIsInFrgnd", "true");
                if (imageView != null) {
                    imageView.post(new Runnable() {
                        public void run() {
                            if (!currentApp.matches(previousApp)) {

                                showUnlockDialog();
                                previousApp = currentApp;
                            }else {
                                Log.d("AppCheckSErvice", "currentApp matches previous App");
                            }

                        }
                    });
                }
            } else {
                Log.d("isConcernedAppIsInFrgnd", "false");
                if (imageView != null) {
                    imageView.post(new Runnable() {
                        public void run() {
                            hideUnlockDialog();
                        }
                    });
                }
            }
        }
    };

    void showUnlockDialog() {
        showDialog();
    }

    void hideUnlockDialog() {
        previousApp = "";
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showDialog() {
        if (context == null)
            context = getApplicationContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate(R.layout.popup_unlock, null, false);
        Lock9View lock9View = (Lock9View) promptsView.findViewById(R.id.lock_9_view);
        Button forgetPassword = (Button) promptsView.findViewById(R.id.forgetPassword);
        ImageView imgIcon = (ImageView) promptsView.findViewById(R.id.imgIcon);
        final TextView textView = (TextView) promptsView.findViewById(R.id.textView) ;
        LinearLayout llBGTheme = (LinearLayout) promptsView.findViewById(R.id.llBGTheme);
        SurfaceView surfaceView = (SurfaceView) promptsView.findViewById(R.id.picSurfaceView);

        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        llBGTheme.setBackgroundResource(sharedPreferences.getInt(AppLockConstants.THEME,R.drawable.gradient7));

        if (sharedPreferences.getBoolean(AppLockConstants.IS_INTRUDER_ON,false)){
            cameraFuncation = new CameraFuncation(context,surfaceView);
        }

        try {
            Drawable drawable = getPackageManager().getApplicationIcon(currentApp);
            imgIcon.setImageDrawable(drawable);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        lock9View.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {
                if (password.matches(sharedPreference.getPassword(context))) {
                    dialog.dismiss();

                } else {
                    //Toast.makeText(getApplicationContext(), "Wrong Pattern Try Again", Toast.LENGTH_SHORT).show();
                    textView.setText("Try Again");
                    if (sharedPreferences.getBoolean(AppLockConstants.IS_INTRUDER_ON,false)){
                        cameraFuncation.tackPicture();
                    }
                }
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AppCheckServices.this, PasswordRecoveryActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                dialog.dismiss();
            }
        });

        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(promptsView);
        dialog.getWindow().setGravity(Gravity.CENTER);

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
                return true;
            }
        });

        dialog.show();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            onTaskRemoved(intent);
        }
        /* We want this service to continue running until it is explicitly
        * stopped, so return sticky.
        */
        return START_STICKY;
    }

    //Important
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(getApplicationContext(),this.getClass());
        intent.setPackage(getPackageName());
        startService(intent);
        super.onTaskRemoved(rootIntent);
    }

    public boolean isConcernedAppIsInForeground() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(5);
        if (Build.VERSION.SDK_INT <= 20) {
            if (task.size() > 0) {
                ComponentName componentInfo = task.get(0).topActivity;
                for (int i = 0; pakageName != null && i < pakageName.size(); i++) {
                    if (componentInfo.getPackageName().equals(pakageName.get(i))) {
                        currentApp = pakageName.get(i);
                        return true;
                    }
                }
            }
        } else {
            String mpackageName = manager.getRunningAppProcesses().get(0).processName;
            UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, time);
            if (stats != null) {
                SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (runningTask.isEmpty()) {
                    Log.d(TAG,"isEmpty Yes");
                    mpackageName = "";
                }else {
                    mpackageName = runningTask.get(runningTask.lastKey()).getPackageName();
                    Log.d(TAG,"isEmpty No : "+mpackageName);
                }
            }

            for (int i = 0; pakageName != null && i < pakageName.size(); i++) {
                Log.d("AppCheckService", "pakageName Size" + pakageName.size());
                if (mpackageName.equals(pakageName.get(i))) {
                    currentApp = pakageName.get(i);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
        if (imageView != null) {
            windowManager.removeView(imageView);
        }
        /**** added to fix the bug of view not attached to window manager ****/
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
