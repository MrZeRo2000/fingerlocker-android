package com.romanpulov.fingerlocker;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "FingerLocker";

    // Interaction with the DevicePolicyManager
    DevicePolicyManager mDPM;
    ComponentName mDeviceAdminSample;

    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;

    /**
     * Helper to determine if we are an active admin
     */
    private boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDeviceAdminSample);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prepare to work with the DPM
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(this, DeviceAdminMainReceiver.class);

        if (!isActiveAdmin()) {
            // Launch the activity to have the user enable our admin.
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.add_admin_extra_app_text));
            ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == REQUEST_CODE_ENABLE_ADMIN) {
                            Toast.makeText(getApplicationContext(), R.string.grant_success_text, Toast.LENGTH_SHORT).show();
                        }
                    });
            startForResult.launch(intent);
            // return false - don't update checkbox until we're really active
        } else {
            //mDPM.removeActiveAdmin(mDeviceAdminSample);
            mDPM.lockNow();
            finish();
        }
        finish();
    }

    public static class DeviceAdminMainReceiver extends DeviceAdminReceiver {
    }
}