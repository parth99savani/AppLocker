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

import com.google.android.material.textfield.TextInputEditText;
import com.popseven.applock.Utils.AppLockConstants;

import java.util.ArrayList;
import java.util.List;

public class PasswordRecoverSetActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private EditText answer;
    private Button confirmButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_recover_set_password);

        confirmButton = (Button) findViewById(R.id.confirmButton);
        answer = (EditText) findViewById(R.id.answer);

        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answer.getText().toString().isEmpty()) {
                    editor.putBoolean(AppLockConstants.IS_PASSWORD_SET, true);
                    editor.commit();
                    editor.putString(AppLockConstants.ANSWER, answer.getText().toString());
                    editor.commit();

                    Intent i = new Intent(PasswordRecoverSetActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please write an answer", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(PasswordRecoverSetActivity.this, PasswordSetActivity.class);
        startActivity(i);
        finish();
    }

}
