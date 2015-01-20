package edu.milton.miltonmobileandroid.campus.doorlock;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import edu.milton.miltonmobileandroid.R;

public class DoorLockActivity extends Activity {
    public boolean isHasBle() {
        return hasBle;
    }


    private boolean hasBle = false;
    private boolean hasMinOs = false;
    private boolean bleEnabled = false;

    private boolean mScanning;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;

    private static final long SCAN_PERIOD = 10000;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campus_doorlock_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //  COPYRIGHT NOTICE
        // ALL CODE In the "DoorLock" package (package edu.milton.miltonmobileandroid.campus.doorlock) and referenced layouts/other assets are (c)
        // Copyright Ravi Rahman, 2015 (except code written by others and cited), as an individual, and are not owned by Milton Academy, nor may these be used for any other purpose or reproduced
        // (in whole, part, or even an idea or method) outside of this application.
        // without Ravi Rahman's explicit approval and permission.
        // Nor may any individual, or the school, profit from these ideas, without Ravi Rahman's explicit approval and permission

        //TODO check the savedInstanceState to see if the doorlocks are already there - so it doesn't have to refresh on screen rotate

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            hasMinOs = true;
        }
        if (!hasMinOs) {
            //TODO display error and break;
            return;
        }

        //from https://developer.android.com/guide/topics/connectivity/bluetooth-le.html
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            hasBle = true;
        }
        if (!hasBle) {
            // TODO display error as alert and break
            return;
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            bleEnabled = true;
        }
        if (!bleEnabled) {
            enableBle();
            return;
        }
        findDoorLocks();
    }

    private void enableBle() {
        //TODO display an alert saying that this app needs BLE enabled; press ok and follow the prompts to enable
        //TODO if no pressed, exit activity
        //TODO if yes pressed, do the following
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //PRocess the devices, add to the list of doorlocks

                            //mLeDeviceListAdapter.addDevice(device);
                            //mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    private void findDoorLocks() {
        boolean enable = true;
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                bleEnabled = true;
                findDoorLocks();
            }
            if (!bleEnabled) {
                enableBle();

            }
        }
    }

    public void findBleDevices() {
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_door_lock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DoorLockFragment extends Fragment {

        public DoorLockFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.campus_doorlock_fragment, container, false);
            return rootView;
        }
    }
}
