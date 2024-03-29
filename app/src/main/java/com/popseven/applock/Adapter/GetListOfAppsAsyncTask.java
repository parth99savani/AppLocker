package com.popseven.applock.Adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.popseven.applock.Data.AppInfo;
import com.popseven.applock.MainActivity;
import com.popseven.applock.Prefrence.SharedPreference;
import com.popseven.applock.Utils.AppLockConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GetListOfAppsAsyncTask extends AsyncTask<String, Void, List<AppInfo>> {

    MainActivity container;
    public GetListOfAppsAsyncTask(MainActivity mainActivity){
        container = mainActivity;
    }
    @Override
    protected List<AppInfo> doInBackground(String... strings) {

        String requiredAppsType = strings[0];

        List<AppInfo> list = getListOfInstalledApp(container);

        SharedPreference sharedPreference = new SharedPreference();
        List<AppInfo> lockedFilteredAppList = new ArrayList<AppInfo>();
        List<AppInfo> unlockedFilteredAppList = new ArrayList<AppInfo>();
        boolean flag = true;
        if (requiredAppsType.matches(AppLockConstants.LOCKED) || requiredAppsType.matches(AppLockConstants.UNLOCKED)) {
            for (int i = 0; i < list.size(); i++) {
                flag = true;
                if (sharedPreference.getLocked(container) != null) {
                    for (int j = 0; j < sharedPreference.getLocked(container).size(); j++) {
                        if (list.get(i).getPackageName().matches(sharedPreference.getLocked(container).get(j))) {
                            lockedFilteredAppList.add(list.get(i));
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    unlockedFilteredAppList.add(list.get(i));
                }
            }
            if (requiredAppsType.matches(AppLockConstants.LOCKED)) {
                list.clear();
                list.addAll(lockedFilteredAppList);
            } else if (requiredAppsType.matches(AppLockConstants.UNLOCKED)) {
                list.clear();
                list.addAll(unlockedFilteredAppList);
            }

        }
        return list;
    }

    /**
     * get the list of all installed applications in the device
     *
     * @return ArrayList of installed applications or null
     */
    public static List<AppInfo> getListOfInstalledApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<AppInfo> installedApps = new ArrayList();
        List<PackageInfo> apps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
        final PackageItemInfo.DisplayNameComparator comparator = new PackageItemInfo.DisplayNameComparator(packageManager);
        Collections.sort(apps, new Comparator<PackageInfo>()
        {
            @Override
            public int compare(PackageInfo lhs, PackageInfo rhs)
            {
                return comparator.compare(lhs.applicationInfo, rhs.applicationInfo);
            }
        });
        if (apps != null && !apps.isEmpty()) {

            for (int i = 0; i < apps.size(); i++) {
                PackageInfo p = apps.get(i);
                ApplicationInfo appInfo = null;
                try {
                    if (null != packageManager.getLaunchIntentForPackage(p.packageName)) {
                        // appInfo = packageManager.getApplicationInfo(p.packageName, 0);
                        AppInfo app = new AppInfo();
                        app.setName(p.applicationInfo.loadLabel(packageManager).toString());
                        app.setPackageName(p.packageName);
                        app.setVersionName(p.versionName);
                        app.setVersionCode(p.versionCode);
                        app.setIcon(p.applicationInfo.loadIcon(packageManager));

                        installedApps.add(app);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return installedApps;
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        container.showProgressBar();
    }

    @Override
    protected void onPostExecute(List<AppInfo> appInfos) {
        super.onPostExecute(appInfos);

        if(container!=null && container!=null) {
            container.hideProgressBar();
            container.updateData(appInfos);
        }

    }
}
