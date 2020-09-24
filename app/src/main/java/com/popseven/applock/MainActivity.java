package com.popseven.applock;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.popseven.applock.Adapter.ApplicationListAdapter;
import com.popseven.applock.Adapter.GetListOfAppsAsyncTask;
import com.popseven.applock.Data.AppInfo;
import com.popseven.applock.Dialog.AppPermissionDialog;
import com.popseven.applock.Dialog.PermissionDialog;
import com.popseven.applock.Services.AlarmReceiver;
import com.popseven.applock.Services.AppCheckServices;
import com.popseven.applock.Utils.AppLockConstants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerViewApp;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //private ProgressDialog progressDialog;
    private List<AppInfo> list;
    private SharedPreferences sharedPreferences;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    public static int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 12345;
    private Context context;
    private ProgressBar progressBar;
    private ImageButton btnTheme;
    private ImageButton btnIntruder;
    private static final int PERMISSIONS_MULTIPLE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();

        recyclerViewApp = findViewById(R.id.recyclerViewApp);
        progressBar = findViewById(R.id.progressBar);
        btnTheme = findViewById(R.id.btnTheme);
        btnIntruder = findViewById(R.id.btnIntruder);

        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);

        checkPermissions();

//        progressDialog = new ProgressDialog(MainActivity.this);
//        progressDialog.setMessage("Loading Apps ...");

        recyclerViewApp.setHasFixedSize(true);

        list = new ArrayList<>();
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewApp.setLayoutManager(mLayoutManager);
        mAdapter = new ApplicationListAdapter(list, this, AppLockConstants.ALL_APPS);
        recyclerViewApp.setAdapter(mAdapter);

        GetListOfAppsAsyncTask task = new GetListOfAppsAsyncTask(this);
        task.execute(AppLockConstants.ALL_APPS);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        btnTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ThemeActivity.class));
            }
        });

        btnIntruder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean(AppLockConstants.IS_INTRUDER_ON,false)){
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_MULTIPLE_REQUEST);
                        }
                    } else {
                        startActivity(new Intent(MainActivity.this,IntruderSelfieActivity.class));
                    }
                } else {
                    View v = findViewById(android.R.id.content);
                    Snackbar.make(v, "First you have to ON Intruder Selfie.", Snackbar.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        }
                    }, 1500);
                }
            }
        });

    }

    public void showProgressBar() {
        recyclerViewApp.setVisibility(View.INVISIBLE);
//        progressDialog.show();
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
//        if (!MainActivity.this.isFinishing() && progressDialog != null) {
//            progressDialog.dismiss();
//        }
        progressBar.setVisibility(View.GONE);
        recyclerViewApp.setVisibility(View.VISIBLE);
    }

    public void updateData(List<AppInfo> list) {
        this.list.clear();
        this.list.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_option_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                //settings
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_rate) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + getPackageName())));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        } else if (id == R.id.nav_share) {
            Intent a = new Intent(Intent.ACTION_SEND);

            //this is to get the app link in the playstore without launching your app.
            final String appPackageName = getApplicationContext().getPackageName();
            String strAppLink = "";

            try {
                strAppLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
            } catch (ActivityNotFoundException anfe) {
                strAppLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
            }
            // this is the sharing part
            a.setType("text/link");
            String shareBody = "PN Player - AR Video Player\n\nHey! Download this app for free & play video using this player." +
                    "\n" + "" + strAppLink;
            String shareSub = "PN Player - AR Video Player";
            a.putExtra(Intent.EXTRA_SUBJECT, shareSub);
            a.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(a, "Share 'PN Player' Using"));
        } else if (id == R.id.nav_about) {
            //startActivity(new Intent(MainActivity.this,AboutActivity.class));
        } else if (id == R.id.nav_privacy) {
            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.policy))));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {
//                OverlayPermissionDialogFragment dialogFragment = new OverlayPermissionDialogFragment();
//                dialogFragment.show(getSupportFragmentManager(), "Overlay Permission");
                PermissionDialog permissionDialog = PermissionDialog.newInstance();
                permissionDialog.show(getSupportFragmentManager(), "Permission Bottom Sheet Dialog Fragment");
                permissionDialog.setCancelable(false);

            } else if (!hasUsageStatsPermission()) {
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                UsageAcessDialogFragment dialogFragment = new UsageAcessDialogFragment();
//                ft.add(dialogFragment, null);
//                ft.commitAllowingStateLoss();
                AppPermissionDialog appPermissionDialog = AppPermissionDialog.newInstance();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(appPermissionDialog, null);
                ft.commitAllowingStateLoss();
                appPermissionDialog.setCancelable(false);
            } else {
                startService();
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super().
    }

    public void startService() {
        /****************************** too much important don't miss it *****************************/
        startService(new Intent(MainActivity.this, AppCheckServices.class));

        try {
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, 0);
            int interval = (86400 * 1000) / 4;
            if (manager != null) {
                manager.cancel(pendingIntent);
            }
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        final boolean isPasswordSet = sharedPreferences.getBoolean(AppLockConstants.IS_PASSWORD_SET, false);
        if (isPasswordSet) {
//            Intent i = new Intent(MainActivity.this, PasswordActivity.class);
//            startActivity(i);
//            finish();
        } else {
            Intent i = new Intent(MainActivity.this, PasswordSetActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        checkPermissions();
//        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
//            // ** if so check once again if we have permission */
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                Log.d("SplashActivity", "cp 2");
//                if (Settings.canDrawOverlays(this)) {
//                    Log.d("SplashActivity", "cp 3");
//                       checkPermissions();
//                }
//            }
//        }else if(requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS){
//            if (hasUsageStatsPermission()){
//                Log.d("SplashActivity", "cp 4");
//                checkPermissions();
//            }
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(cameraPermission && readExternalFile) {
                        // write your logic here
                    } else {
                        Snackbar.make(MainActivity.this.findViewById(android.R.id.content),
                                "Please Grant Permissions for Intruder Selfie.",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(
                                                    new String[]{Manifest.permission
                                                            .READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                                    PERMISSIONS_MULTIPLE_REQUEST);
                                        }
                                    }
                                }).show();
                    }
                }
                break;
        }
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }


//    public static class OverlayPermissionDialogFragment extends DialogFragment {
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the Builder class for convenient dialog construction
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setMessage(R.string.ovarlay_permission_description)
//                    .setTitle("Overlay Permission")
//                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // FIRE ZE MISSILES!
//                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                                    Uri.parse("package:" + getActivity().getPackageName()));
//                            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
//                        }
//                    });
//
//            // Create the AlertDialog object and return it
//            return builder.create();
//        }
//    }

//    public static class UsageAcessDialogFragment extends DialogFragment {
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the Builder class for convenient dialog construction
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//            builder.setMessage(R.string.usage_data_access_description)
//                    .setTitle("Usage Access Permission")
//                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // FIRE ZE MISSILES!
//                            startActivityForResult(
//                                    new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
//                                    MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
//                        }
//                    });
//
//            // Create the AlertDialog object and return it
//            return builder.create();
//        }
//    }

}