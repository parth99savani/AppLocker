package com.popseven.applock.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.popseven.applock.R;
import com.popseven.applock.Utils.AppLockConstants;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.MyViewHolder> {

    private Context context;
    private ThemeAdapter.ThemeAdapterListener listener;
    private ArrayList<Integer> themeList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    public ThemeAdapter(Context context, ArrayList<Integer> themeList, ThemeAdapter.ThemeAdapterListener listener) {
        this.context = context;
        this.themeList = themeList;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rlChecked;
        private RelativeLayout rlTheme;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            rlChecked = itemView.findViewById(R.id.rlChecked);
            rlTheme = itemView.findViewById(R.id.rlTheme);
        }
    }

    @NonNull
    @Override
    public ThemeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_theme, viewGroup, false);

        return new ThemeAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ThemeAdapter.MyViewHolder holder, final int i) {

        sharedPreferences = context.getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);

        holder.rlTheme.setBackgroundResource(themeList.get(i));

        if (themeList.get(i)==sharedPreferences.getInt(AppLockConstants.THEME,R.drawable.gradient7)){
            holder.rlChecked.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().putInt(AppLockConstants.THEME,themeList.get(i)).commit();
                listener.onThemeSelected();
            }
        });

    }

    @Override
    public int getItemCount() {
        return themeList.size();
    }

    public interface ThemeAdapterListener {
        void onThemeSelected();
    }

}
