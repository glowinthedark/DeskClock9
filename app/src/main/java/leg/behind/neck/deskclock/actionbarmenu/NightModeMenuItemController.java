/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package leg.behind.neck.deskclock.actionbarmenu;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import leg.behind.neck.deskclock.DeskClock;
import leg.behind.neck.deskclock.R;
import leg.behind.neck.deskclock.ScreensaverActivity;
import leg.behind.neck.deskclock.data.DataModel;
import leg.behind.neck.deskclock.events.Events;

import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static android.support.v4.content.ContextCompat.startActivity;
import static android.view.Menu.NONE;

/**
 * {@link MenuItemController} for controlling night mode display.
 */
public final class NightModeMenuItemController implements MenuItemController {

    private static final int NIGHT_MODE_MENU_RES_ID = R.id.menu_item_night_mode;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_SETTINGS = 8808;

    private final Context mContext;

    public NightModeMenuItemController(Context context) {
        mContext = context;
    }

    @Override
    public int getId() {
        return NIGHT_MODE_MENU_RES_ID;
    }

    @Override
    public void onCreateOptionsItem(Menu menu) {
        menu.add(NONE, NIGHT_MODE_MENU_RES_ID, NONE, R.string.menu_item_night_mode)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    @Override
    public void onPrepareOptionsItem(MenuItem item) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        int originalTimeout;
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (!Settings.System.canWrite(mContext)) {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + mContext.getApplicationContext().getPackageName()));
//                    mContext.getApplicationContext().startActivity(intent);
//                }
//                return false;
//            }
//
////            ActivityCompat.requestPermissions(mContext.getApplicationContext().getAc, new String[]{Manifest.permission.WRITE_SETTINGS}, 1);
//
//            originalTimeout = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
//
//            Log.d(":::LK:::", String.valueOf(originalTimeout));
//            android.provider.Settings.System.putInt(mContext.getContentResolver(),
//                    Settings.System.SCREEN_OFF_TIMEOUT, -1);
//        } catch (Settings.SettingNotFoundException e) {
//            Log.e("::LK:N:::", e.getMessage());
//        }
//        LK: disable screen timeout before starting Screensaver

        boolean canStartScreensaver = false;

        // Here, thisActivity is the current activity
//        https://stackoverflow.com/questions/32266425/android-6-0-permission-denial-requires-permission-android-permission-write-sett
        if (DataModel.getDataModel().isScreensaverAlwaysOn()) {
            if (!Settings.System.canWrite(mContext)) {

                // Permission is not granted
                // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
//                    Manifest.permission.WRITE_SETTINGS)) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//            } else {
//                // No explanation needed; request the permission


                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                ((Activity) mContext).startActivityForResult(intent, MY_PERMISSIONS_REQUEST_WRITE_SETTINGS);

//                ActivityCompat.requestPermissions((Activity) mContext,
//                        new String[]{Manifest.permission.WRITE_SETTINGS},
//                        MY_PERMISSIONS_REQUEST_WRITE_SETTINGS);
//

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
//            }
            } else {
                // Permission has already been granted
                canStartScreensaver = true;
                int originalTimeout = 0;
                try {
                    originalTimeout = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
                } catch (Settings.SettingNotFoundException e) {
                    Log.e(":::LKK:::", e.getMessage());
                }

                Log.d("::LK::", ":::LK::: current autolock timeout=" + originalTimeout);

//            if (originalTimeout != 0) { 6_120_000
                DeskClock.lastSaveAutoLockTimeout = originalTimeout;
//            }
                android.provider.Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 2_147_483_647); // = 24.8 DAYS
//                policyManger = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
            }
        } else {
            canStartScreensaver = true;
        }


        if (canStartScreensaver) {


            mContext.startActivity(new Intent(mContext, ScreensaverActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Events.EXTRA_EVENT_LABEL, R.string.label_deskclock));
        }
        return true;
    }
}
