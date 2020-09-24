package com.popseven.applock.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.popseven.applock.Data.AppInfo;
import com.popseven.applock.Prefrence.SharedPreference;
import com.popseven.applock.R;

import java.util.ArrayList;
import java.util.List;

public class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListAdapter.ViewHolder> {

    private List<AppInfo> installedApps = new ArrayList();
    private Context context;
    private SharedPreference sharedPreference;
    private String requiredAppsType;

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView applicationName;
        public CardView cardView;
        public ImageView icon;
        //public Switch switchView;
        public ImageButton btnUnlock;

        public ViewHolder(View v) {
            super(v);
            applicationName = (TextView) v.findViewById(R.id.applicationName);
            cardView = (CardView) v.findViewById(R.id.card_view);
            icon = (ImageView) v.findViewById(R.id.icon);
            //switchView = (Switch) v.findViewById(R.id.switchView);
            btnUnlock = (ImageButton) v.findViewById(R.id.btnUnlock);
        }
    }

    public void add(int position, String item) {
//        mDataset.add(position, item);
//        notifyItemInserted(position);
    }

    public void remove(AppInfo item) {
//        int position = installedApps.indexOf(item);
//        installedApps.remove(position);
//        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ApplicationListAdapter(List<AppInfo> appInfoList, Context context, String requiredAppsType) {
        installedApps = appInfoList;
        this.context = context;
        this.requiredAppsType = requiredAppsType;
        sharedPreference = new SharedPreference();

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ApplicationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final AppInfo appInfo = installedApps.get(position);
        holder.applicationName.setText(appInfo.getName());
        holder.icon.setBackgroundDrawable(appInfo.getIcon());

        //holder.switchView.setOnCheckedChangeListener(null);
        holder.cardView.setOnClickListener(null);
        if (checkLockedItem(appInfo.getPackageName())) {
            //holder.switchView.setChecked(true);
            holder.btnUnlock.setImageResource(R.drawable.ic_baseline_lock_24);
        } else {
            //holder.switchView.setChecked(false);
            holder.btnUnlock.setImageResource(R.drawable.ic_baseline_lock_open_24);
        }

//        holder.switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    //AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Lock Clicked", "lock_clicked", appInfo.getPackageName());
//                    sharedPreference.addLocked(context, appInfo.getPackageName());
//                } else {
//                    //AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Unlock Clicked", "unlock_clicked", appInfo.getPackageName());
//                    sharedPreference.removeLocked(context, appInfo.getPackageName());
//                }
//            }
//        });

        holder.btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkLockedItem(appInfo.getPackageName())) {
                    sharedPreference.removeLocked(context, appInfo.getPackageName());
                    holder.btnUnlock.setImageResource(R.drawable.ic_baseline_lock_open_24);
                } else {
                    sharedPreference.addLocked(context, appInfo.getPackageName());
                    holder.btnUnlock.setImageResource(R.drawable.ic_baseline_lock_24);
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.switchView.performClick();
                holder.btnUnlock.performClick();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return installedApps.size();
    }

    /*Checks whether a particular app exists in SharedPreferences*/
    public boolean checkLockedItem(String checkApp) {
        boolean check = false;
        List<String> locked = sharedPreference.getLocked(context);
        if (locked != null) {
            for (String lock : locked) {
                if (lock.equals(checkApp)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

}