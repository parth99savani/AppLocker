package com.popseven.applock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.popseven.applock.Utils.AppLockConstants;

import java.util.ArrayList;
import java.util.List;

public class PasswordRecoveryActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private EditText answer;
    private Button confirmButton;
    private TextInputLayout inputLayoutName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_recovery_password);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        confirmButton = (Button) findViewById(R.id.confirmButton);
        answer = (EditText) findViewById(R.id.answer);

        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answer.getText().toString().isEmpty()) {
                    if (sharedPreferences.getString(AppLockConstants.ANSWER, "").matches(answer.getText().toString())) {
                        editor.putBoolean(AppLockConstants.IS_PASSWORD_SET, false);
                        editor.commit();
                        editor.putString(AppLockConstants.PASSWORD, "");
                        editor.commit();
                        Intent i = new Intent(PasswordRecoveryActivity.this, PasswordSetActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Your answer didn't matches", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please write an answer", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
