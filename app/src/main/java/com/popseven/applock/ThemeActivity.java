package com.popseven.applock;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.popseven.applock.Adapter.ImageAdapter;
import com.popseven.applock.Adapter.ThemeAdapter;

import java.util.ArrayList;

public class ThemeActivity extends AppCompatActivity implements ThemeAdapter.ThemeAdapterListener{

    private RecyclerView recyclerViewTheme;
    private ArrayList<Integer> themeList;
    private ThemeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        recyclerViewTheme = findViewById(R.id.recyclerViewTheme);

        themeList = new ArrayList<>();

        recyclerViewTheme.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewTheme.setHasFixedSize(true);

        reloadRecyclerView();

    }

    private void reloadRecyclerView() {
        themeList.clear();
        loadTheme();
        adapter = new ThemeAdapter(ThemeActivity.this, themeList, this);
        recyclerViewTheme.setAdapter(adapter);
    }

    private void loadTheme() {
        themeList.add(R.drawable.gradient1);
        themeList.add(R.drawable.gradient2);
        themeList.add(R.drawable.gradient3);
        themeList.add(R.drawable.gradient4);
        themeList.add(R.drawable.gradient5);
        themeList.add(R.drawable.gradient6);
        themeList.add(R.drawable.gradient7);
        themeList.add(R.drawable.gradient8);
        themeList.add(R.drawable.gradient9);
        themeList.add(R.drawable.gradient10);
        themeList.add(R.drawable.gradient11);
        themeList.add(R.drawable.gradient12);
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
    public void onThemeSelected() {
        reloadRecyclerView();
    }
}