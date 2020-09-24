package com.popseven.applock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.popseven.applock.Utils.AppLockConstants;
import com.takwolf.android.lock9.Lock9View;

public class PasswordSetActivity extends AppCompatActivity {

    private Lock9View lock9View;
    //private Button confirmButton;
    private Button retryButton;
    private TextView textView;
    private boolean isEnteringFirstTime = true;
    private boolean isEnteringSecondTime = false;
    private String enteredPassword;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = getApplicationContext();
        setContentView(R.layout.activity_password_set);
        lock9View = (Lock9View) findViewById(R.id.lock_9_view);
        //confirmButton = (Button) findViewById(R.id.confirmButton);
        retryButton = (Button) findViewById(R.id.retryButton);
        textView = (TextView) findViewById(R.id.textView);
        //confirmButton.setEnabled(false);
        retryButton.setEnabled(false);
        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();


//        confirmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editor.putString(AppLockConstants.PASSWORD, enteredPassword);
//                editor.commit();
//
//                Intent i = new Intent(PasswordSetActivity.this, PasswordRecoverSetActivity.class);
//                startActivity(i);
//                finish();
//            }
//        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEnteringFirstTime = true;
                isEnteringSecondTime = false;
                textView.setText("Draw Pattern");
                //confirmButton.setEnabled(false);
                retryButton.setEnabled(false);

            }
        });

        lock9View.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {
                retryButton.setEnabled(true);
                if (isEnteringFirstTime) {
                    enteredPassword = password;
                    isEnteringFirstTime = false;
                    isEnteringSecondTime = true;
                    textView.setText("Re-Draw Pattern");
                } else if (isEnteringSecondTime) {
                    if (enteredPassword.matches(password)) {
                        //confirmButton.setEnabled(true);
                        editor.putString(AppLockConstants.PASSWORD, enteredPassword);
                        editor.commit();

                        Intent i = new Intent(PasswordSetActivity.this, PasswordRecoverSetActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        //Toast.makeText(getApplicationContext(), "Both Pattern did not match - Try again", Toast.LENGTH_SHORT).show();
                        isEnteringFirstTime = true;
                        isEnteringSecondTime = false;
                        textView.setText("Pattern did not match, Try again");
                        retryButton.setEnabled(false);
                    }
                }
            }
        });

    }

}
