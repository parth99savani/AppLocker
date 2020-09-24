package com.popseven.applock;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.popseven.applock.Utils.AppLockConstants;

public class SettingsActivity extends AppCompatActivity {

    private TextView txtChangePattern;
    private SharedPreferences sharedPreferences;
    private Switch switchIntruderSelfie;
    private static final int PERMISSIONS_MULTIPLE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);

        txtChangePattern = findViewById(R.id.txtChangePattern);
        switchIntruderSelfie = findViewById(R.id.switchIntruderSelfie);

        if (sharedPreferences.getBoolean(AppLockConstants.IS_INTRUDER_ON,false)){
            switchIntruderSelfie.setChecked(true);
        } else {
            switchIntruderSelfie.setChecked(false);
        }

        txtChangePattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().putBoolean(AppLockConstants.IS_PASSWORD_SET, false).commit();
                sharedPreferences.edit().putString(AppLockConstants.PASSWORD, "").commit();
                Intent i = new Intent(SettingsActivity.this, PasswordSetActivity.class);
                startActivity(i);
                finish();
            }
        });

        switchIntruderSelfie.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_MULTIPLE_REQUEST);
                        }
                        compoundButton.setChecked(false);
                    } else {
                        sharedPreferences.edit().putBoolean(AppLockConstants.IS_INTRUDER_ON, true).commit();
                    }
                } else {
                    sharedPreferences.edit().putBoolean(AppLockConstants.IS_INTRUDER_ON, false).commit();
                }
            }
        });

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
                        Snackbar.make(SettingsActivity.this.findViewById(android.R.id.content),
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}