package com.sabbir.andorid.apps.myids.utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sabbir on 7/11/17.
 */

public class PermissionUtils extends AppCompatActivity{
    public interface PermissionCheckListener{
        void permissionStatus(boolean status);
    }
    private PermissionCheckListener permissionCheckListener;
    public Context mContext;
   final int REQUEST_CODE=1;
    private String [] permissions;
    private String [] permissionToAsk;

    private PermissionUtils(Context context,String[] permission){

        mContext=context;
       // permissionCheckListener= (PermissionCheckListener) context;
        this.permissions=permission;
        checkForPermissions();
    }

    private void checkForPermissions() {
        for (String p: permissions){
            if (ContextCompat.checkSelfPermission(mContext,p)!= PackageManager.PERMISSION_GRANTED){
                if (Build.VERSION.SDK_INT>=23)
                requestPermissions(new String[]{p},REQUEST_CODE);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE:{
                
            }


        }
    }
}
