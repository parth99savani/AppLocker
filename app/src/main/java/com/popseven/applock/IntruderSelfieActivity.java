package com.popseven.applock;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.popseven.applock.Adapter.ImageAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class IntruderSelfieActivity extends AppCompatActivity implements ImageAdapter.ImageAdapterListener {

    private ArrayList<String> imageList = new ArrayList<>();
    private RecyclerView recyclerViewSelfie;
    private ImageAdapter adapter;
    private LinearLayout NoIntruder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intruder_selfie);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        recyclerViewSelfie = findViewById(R.id.recyclerViewSelfie);
        NoIntruder = findViewById(R.id.NoIntruder);

        recyclerViewSelfie.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewSelfie.setHasFixedSize(true);

        reloadRecyclerView();
    }

    private void reloadRecyclerView() {
        imageList.clear();
        loadImageFiles(new File(getFilesDir().getAbsolutePath() + "/IntruderSelfie"));

        if (imageList.isEmpty()){
            NoIntruder.setVisibility(View.VISIBLE);
        } else {
            NoIntruder.setVisibility(View.GONE);
        }

        adapter = new ImageAdapter(IntruderSelfieActivity.this, imageList, this);
        recyclerViewSelfie.setAdapter(adapter);
    }

    private void loadImageFiles(File dir) {

        File[] listFile = dir.listFiles();
        ArrayList<String> al_path = new ArrayList<>();
        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    loadImageFiles(listFile[i]);
                } else {
                    String name = listFile[i].getName();
                    if ((name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")) && Pattern.compile("(.*)((\\.(jpg||jpeg||png))$)", 2).matcher(name).matches()) {
                        al_path.add(listFile[i].getAbsolutePath());
                    }
                }
            }
        }
        imageList.addAll(al_path);
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

    @Override
    public void onImageSelected(String image) {
        Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(image));
        Intent openImageIntent = new Intent();
        openImageIntent.setAction(Intent.ACTION_VIEW)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setDataAndType(
                        fileUri,
                        getContentResolver().getType(fileUri));
        startActivity(openImageIntent);
    }

    @Override
    public void onImageLongSelected(final String image) {
        final AlertDialog.Builder alertOptions = new AlertDialog.Builder(IntruderSelfieActivity.this);

        String[] list = {"Delete"};
        alertOptions.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int i) {
                switch (i) {
                    case 0:

                        ViewGroup viewGroup = findViewById(android.R.id.content);
                        View dialogView = LayoutInflater.from(IntruderSelfieActivity.this).inflate(R.layout.dialog_delete, viewGroup, false);
                        AlertDialog.Builder builder = new AlertDialog.Builder(IntruderSelfieActivity.this);
                        builder.setView(dialogView);

                        final AlertDialog deleteDialog = builder.create();

                        Button btnYes = (Button) dialogView.findViewById(R.id.btnYes);
                        Button btnNo = (Button) dialogView.findViewById(R.id.btnNo);

                        btnYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                File file = new File(image);
                                deleteSelfie(file);
                                deleteDialog.dismiss();
                                reloadRecyclerView();
                            }
                        });

                        btnNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteDialog.dismiss();
                            }
                        });

                        deleteDialog.show();

                        dialog.dismiss();
                        break;
                }
            }
        });

        alertOptions.show();
    }

    private void deleteSelfie(File file) {
        file.delete();// delete child file or empty directory
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        } else{
            MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {

                public void onScanCompleted(String path, Uri uri)
                {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });

        }
    }
}