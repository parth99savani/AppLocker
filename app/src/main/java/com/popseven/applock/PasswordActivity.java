package com.popseven.applock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.popseven.applock.Adapter.CameraFuncation;
import com.popseven.applock.Utils.AppLockConstants;
import com.takwolf.android.lock9.Lock9View;

public class PasswordActivity extends AppCompatActivity {

    private Lock9View lock9View;
    private SharedPreferences sharedPreferences;
    private Context context;
    private Button forgetPassword;
    private TextView textView;
    private static final String TAG = "PasswordActivity";
    private SurfaceView picSurfaceView;
    private CameraFuncation cameraFuncation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context = getApplicationContext();
        setContentView(R.layout.activity_password);

        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        final boolean isPasswordSet = sharedPreferences.getBoolean(AppLockConstants.IS_PASSWORD_SET, false);
        if (!isPasswordSet) {
            Intent i = new Intent(PasswordActivity.this, PasswordSetActivity.class);
            startActivity(i);
            finish();
        }

        forgetPassword = (Button) findViewById(R.id.forgetPassword);
        lock9View = (Lock9View) findViewById(R.id.lock_9_view);
        textView = (TextView) findViewById(R.id.textView);
        picSurfaceView = findViewById(R.id.picSurfaceView);
        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);

        if (sharedPreferences.getBoolean(AppLockConstants.IS_INTRUDER_ON,false)){
            cameraFuncation = new CameraFuncation(this,picSurfaceView);
        }

        lock9View.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {
                if (sharedPreferences.getString(AppLockConstants.PASSWORD, "").matches(password)) {
                    //Toast.makeText(getApplicationContext(), "Success : Pattern Match", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(PasswordActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();

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
                Intent i = new Intent(PasswordActivity.this, PasswordRecoveryActivity.class);
                startActivity(i);

            }
        });
    }

}
