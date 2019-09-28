package com.runtime.permission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class RuntimePermissionActivity extends AppCompatActivity {

    private static final String TAG = RuntimePermissionActivity.class.getSimpleName();
    public static final int CAMERA_PERMISSIONS_REQUEST = 1;
    public static final int SETTINGS_PERMISSIONS_REQUEST = 2;
    private PermissionUtils permissionUtils;
    private Button buttonSingleRuntimePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runtime_permission);
        initView();
        initObject();
        initEvent();
    }

    private void initView() {
        buttonSingleRuntimePermission = findViewById(R.id.single_runtime_permission);
    }

    private void initObject() {
        permissionUtils = new PermissionUtils(RuntimePermissionActivity.this);
    }

    private void initEvent() {

        buttonSingleRuntimePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Marshmallow +
                 * Check whether the app is installed on Android 6.0 or higher.
                 * Check if the build version is greater than or equal to 23.
                 */
                if (Build.VERSION.SDK_INT >= 23)
                {
                    if (permissionUtils.checkPermissions())
                    {
                        Log.e(TAG, "Permission Is Not Granted. Request For Permission");
                        permissionUtils.askPermission();
                    }
                    else
                    {
                        Log.e(TAG, "Permission Already Granted");
                        Intent intent = new Intent(RuntimePermissionActivity.this, PermissionGrantedActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else
                {
                    /*
                     * Pre-Marshmallow
                     * If build version is less than or 23, then all permission is
                     * granted at install time in google play store.
                     */
                    Intent intent = new Intent(RuntimePermissionActivity.this, PermissionGrantedActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == CAMERA_PERMISSIONS_REQUEST)
        {
            Log.i(TAG, "Received Response For Camera Permission Request");

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.i(TAG, "Permission Granted");
                Intent intent = new Intent(RuntimePermissionActivity.this, PermissionGrantedActivity.class);
                startActivity(intent);
            }
            else
            {
                if (Build.VERSION.SDK_INT >= 23)
                {
                    //********************* FIRST WAY ********************
                    /*
                     * showRationale = false If permission is denied (and never ask again is checked), otherwise true
                     * shouldShowRequestPermissionRationale will return false if user clicks Never Ask Again, otherwise true
                     */
                    boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
                    if (showRationale)
                    {
                        Log.i(TAG, "Permission Denied");
                        permissionUtils.dialogWhenDenied();
                    }
                    else
                    {
                        Log.i(TAG, "Never Ask Again With Checked");
                        permissionUtils.dialogWhenNeverAskAgain();
                    }


                    //********************* OR SECOND WAY ********************
                    /*if (ActivityCompat.shouldShowRequestPermissionRationale(RuntimePermissionActivity.this, Manifest.permission.CAMERA))
                    {
                        Log.i(TAG, "Permission Denied");
                        permissionUtils.dialogWhenDenied();
                    }
                    else
                    {
                        Log.i(TAG, "Never Ask Again With Checked");
                        permissionUtils.dialogWhenDenied();
                    }*/

                }
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == SETTINGS_PERMISSIONS_REQUEST)
        {
            if (permissionUtils.checkPermissions())
            {
                Log.i(TAG, "Permission Granted, SETTING");
                Intent intent = new Intent(RuntimePermissionActivity.this, PermissionGrantedActivity.class);
                startActivity(intent);
            }
            else
            {
                Log.i(TAG, "Permissions Not Granted, SETTING");
                permissionUtils.askPermission();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
