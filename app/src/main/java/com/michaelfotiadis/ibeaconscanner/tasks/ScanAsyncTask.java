package com.michaelfotiadis.ibeaconscanner.tasks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.michaelfotiadis.ibeaconscanner.containers.CustomConstants;
import com.michaelfotiadis.ibeaconscanner.datastore.Singleton;
import com.michaelfotiadis.ibeaconscanner.utils.Logger;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;

public class ScanAsyncTask extends AsyncTask<Void, Void, Void> {

    private final int scanTime;
    private BluetoothManager mBluetoothManager;
    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private final String TAG = this.toString();

    private ConcurrentHashMap<String, BluetoothLeDevice> mDeviceMap;
    private ConcurrentHashMap<String, IBeaconDevice> detectedDevicesMap;




    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        private final String TAG = "LeScanCallback";


    //Anna 26.11.17
  /*  public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

        final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());
        //new IBeaconDevice(deviceLe);
        IBeaconDevice iBeaconDeviceLe = new IBeaconDevice(deviceLe);
        // Add it to the map if it is an IBeacon
        try {

            if (mDeviceMap.containsKey(deviceLe.getAddress())) {
              //  Logger.d(TAG, "Device " + deviceLe.getAddress() + " updated.");
                //if actual item already exists in the hashmap, it ll be deleted at first, than stored there again
                mDeviceMap.remove(deviceLe.getAddress());

                mDeviceMap.put(deviceLe.getAddress(), deviceLe);
            } else {
              //  Logger.d(TAG, "Device " + deviceLe.getAddress() + " added.");
                //if actual item does not exist in the hash map, it ll be stored there
                mDeviceMap.put(deviceLe.getAddress(), deviceLe);

            }
        } catch (Exception e) {
            // Ignore it otherwise
            Logger.e(TAG, deviceLe.getAddress() + " " + e.getLocalizedMessage());

        }
    }
};
*/
//Original 26.11.17

    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
    Log.d("SCANtask", "Entered onLeScan");
        final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());

        // Add it to the map if it is an IBeacon
        try {
            Log.d("SCANtask", "Entered try");

            final IBeaconDevice iBeaconDeviceLe;
           iBeaconDeviceLe = new IBeaconDevice(deviceLe);
            if (mDeviceMap.containsKey(deviceLe.getAddress())||detectedDevicesMap.containsKey(String.valueOf(iBeaconDeviceLe.getMinor()))) {
                Log.d("SCANtask", "Entered if");

                mDeviceMap.remove(deviceLe.getAddress());
                detectedDevicesMap.remove(String.valueOf(iBeaconDeviceLe.getMinor()));

                mDeviceMap.put(deviceLe.getAddress(), deviceLe);
                detectedDevicesMap.put(String.valueOf(iBeaconDeviceLe.getMinor()), iBeaconDeviceLe);
            } else {
                Log.d("SCANtask", "Entered else");

                mDeviceMap.put(deviceLe.getAddress(), deviceLe);
                detectedDevicesMap.put(String.valueOf(iBeaconDeviceLe.getMinor()), iBeaconDeviceLe);
                Log.d("SCANtask", "After put");

            }
        }
        catch (Exception e)
        {
            // Ignore it otherwise
            Logger.e(TAG, deviceLe.getAddress() + " " + e.getLocalizedMessage());
        }
    }
    };





    /**
     * @param scanTime
     * @param c
     */
    public ScanAsyncTask(int scanTime, Context c) {
        this.scanTime = scanTime;
        this.context = c;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothAdapter.startLeScan(mLeScanCallback);

        try {
            // Let the thread run for the scan time
            Thread.sleep(scanTime);
        } catch (InterruptedException e) {
            this.cancel(true);
            e.printStackTrace();
        }
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        return null;
    }

    @Override
    protected void onPreExecute() {
        Logger.i(TAG, "onPreExecute");
        // Initialise the device map
        mDeviceMap = new ConcurrentHashMap<String, BluetoothLeDevice>();
        //Anna 26.11.17
        detectedDevicesMap = new ConcurrentHashMap<String, IBeaconDevice>();
        super.onPreExecute();
    }



    @Override
    protected void onPostExecute(Void result) {
        Logger.i("SCANtask", " Entered onPostExecute");
        Logger.d(TAG, "Map contains " + mDeviceMap.size() + " unique devices.");

        Singleton.getInstance().pruneDeviceList(mDeviceMap);
        Singleton.getInstance().updateDetectedDeviceMap(detectedDevicesMap);



        Intent broadcastIntent = new Intent(CustomConstants.Broadcasts.BROADCAST_2.getString());
        Logger.i(TAG, "Broadcasting Scanning Status Finished");
        context.sendBroadcast(broadcastIntent);

        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        Logger.i(TAG, "onCancelled");
        super.onCancelled();
    }

    @Override
    protected void onCancelled(Void result) {
        Logger.i(TAG, "onCancelled (with result)");
        super.onCancelled(result);
    }

}
