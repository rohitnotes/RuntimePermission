package com.runtime.permission;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import static com.runtime.permission.RuntimePermissionActivity.SETTINGS_PERMISSIONS_REQUEST;

public class PermissionUtils {

    private Activity activity;

    public PermissionUtils(Activity context)
    {
        this.activity = context ;
    }

    public boolean checkPermissions()
    {
        /*
         * If permission is granted then return true, If permission is not granted return false
         */
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED;
    }

    public void askPermission()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, RuntimePermissionActivity.CAMERA_PERMISSIONS_REQUEST);
        }
    }

    public void dialogWhenDenied()
    {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.permission_denied_then_alert_dialog, viewGroup, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button close = dialogView.findViewById(R.id.close_button);
        Button ok = dialogView.findViewById(R.id.ok_button);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                activity.finish();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                askPermission();
            }
        });
    }

    public void dialogWhenNeverAskAgain()
    {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.permission_never_ask_again_alert_dialog, viewGroup, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button close = dialogView.findViewById(R.id.close_button);
        Button settings = dialogView.findViewById(R.id.settings_button);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                activity.finish();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (Build.VERSION.SDK_INT >= 23)
                {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                    activity.startActivityForResult(intent, SETTINGS_PERMISSIONS_REQUEST);

                    /*Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + activity.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    activity.startActivity(intent);*/
                }
            }
        });
    }
}
