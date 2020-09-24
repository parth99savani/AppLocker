package com.popseven.applock.Dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.popseven.applock.R;

import static com.popseven.applock.MainActivity.MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS;

public class AppPermissionDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    public static AppPermissionDialog newInstance() {
        AppPermissionDialog fragment = new AppPermissionDialog();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.app_permission_dialog, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        Button allowButton = (Button) contentView.findViewById(R.id.allowButton);
        allowButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        startActivityForResult(
                new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
        dismiss();
    }
}
