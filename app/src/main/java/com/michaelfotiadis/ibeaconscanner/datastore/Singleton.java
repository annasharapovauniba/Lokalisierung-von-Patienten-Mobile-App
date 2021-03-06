package com.michaelfotiadis.ibeaconscanner.datastore;

import android.app.Application;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.michaelfotiadis.ibeaconscanner.activities.MainActivity;
import com.michaelfotiadis.ibeaconscanner.utils.Logger;
import com.michaelfotiadis.ibeaconscanner.utils.TimeFormatter;
import com.michaelfotiadis.ibeaconscanner.utils.ToastUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconType;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconUtils;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;

/**
 * Singleton Class
 *
 * @author Michael Fotiadis
 * @since 24/06/2014
 */
@SuppressWarnings("ClassWithOnlyPrivateConstructors")
public class Singleton {
    public static String deviceAddress;

    public BluetoothLeDevice myDevice;

    private static final String TAG = Singleton.class.getSimpleName();
    private static volatile Singleton sInstance = null;

    private final ConcurrentHashMap<String, BluetoothLeDevice> mDeviceMap;
    //Anna 26.11.17
    private final ConcurrentHashMap<String, IBeaconDevice> updatedDetectedDeviceMap;


    private final ConcurrentHashMap<String, BluetoothLeDevice> mAvailableDevicesList;
    private final ConcurrentHashMap<String, BluetoothLeDevice> mUpdatedDevicesList;
    private final ConcurrentHashMap<String, BluetoothLeDevice> mNewDevicesList;
    private final ConcurrentHashMap<String, BluetoothLeDevice> mMovingCloserDevicesList;
    private final ConcurrentHashMap<String, BluetoothLeDevice> mMovingFartherDevicesList;
    private final ConcurrentHashMap<String, BluetoothLeDevice> mDisappearingDevicesList;

    private int mNumberOfScans = 0;
    private long mTimeOfLastUpdate = 0;

    /**
     * Singleton Constructor
     */
    private Singleton() {

        mDeviceMap = new ConcurrentHashMap<>();
        updatedDetectedDeviceMap = new ConcurrentHashMap<>();

        mAvailableDevicesList = new ConcurrentHashMap<>();

        mNewDevicesList = new ConcurrentHashMap<>();
        mUpdatedDevicesList = new ConcurrentHashMap<>();

        mMovingFartherDevicesList = new ConcurrentHashMap<>();
        mMovingCloserDevicesList = new ConcurrentHashMap<>();

        mDisappearingDevicesList = new ConcurrentHashMap<>();

    }

    public int getNumberOfScans() {
        return mNumberOfScans;
    }

    public void setNumberOfScans(final int mNumberOfScans) {
        this.mNumberOfScans = mNumberOfScans;
    }

    public BluetoothLeDevice getBluetoothLeDeviceForAddress(final String address) {

        for (final BluetoothLeDevice device : mDeviceMap.values()) {
            if (device.getAddress().equals(address)) {
                return device;
            }
        }
        return null;
    }
    //Anna 26.11.17
    public BluetoothLeDevice getIBeaconDeviceForMinorNr(final String minor) {

        for (final IBeaconDevice iBeaconDevice : updatedDetectedDeviceMap.values()) {
            if (String.valueOf(iBeaconDevice.getMinor()).equals(minor)) {
                return iBeaconDevice;
            }
        }
        return null;
    }

    /**
     * Receives a Device Map and allocates the devices to the appropriate Singleton Device Map
     *
     * @param inputDeviceMap String (ID) - BluetoothLeDevice Map
     */
   //  Original
/*
    public void pruneDeviceList(final Map<String, BluetoothLeDevice> inputDeviceMap) {

        reportMapContents();

        mUpdatedDevicesList.clear();
        mNewDevicesList.clear();
        mMovingFartherDevicesList.clear();
        mMovingCloserDevicesList.clear();
        mDisappearingDevicesList.clear();

        for (final BluetoothLeDevice originalDevice : mDeviceMap.values()) {
            if (!inputDeviceMap.containsKey(originalDevice.getAddress())) {
                Logger.d(TAG, "Device disappeared : " + originalDevice.getAddress());
                mDisappearingDevicesList.put(originalDevice.getAddress(), originalDevice);
            }
        }

        for (final BluetoothLeDevice updatedDevice : inputDeviceMap.values()) {
            mAvailableDevicesList.put(updatedDevice.getAddress(), updatedDevice);

            if (mDeviceMap.containsKey(updatedDevice.getAddress())) {

                Logger.d(TAG, "Device has been updated!");
                Logger.d(TAG, "Device : " + updatedDevice.getAddress());
                Logger.d(TAG, "With average RSSI : " + updatedDevice.getRunningAverageRssi());

                final BluetoothLeDevice originalDevice = mDeviceMap.get(updatedDevice.getAddress());

                if (updatedDevice.getRunningAverageRssi() <= originalDevice.getRunningAverageRssi()) {
                    mMovingCloserDevicesList.put(updatedDevice.getAddress(), updatedDevice);
                } else {
                    mMovingFartherDevicesList.put(updatedDevice.getAddress(), updatedDevice);
                }

                mDeviceMap.put(updatedDevice.getAddress(), updatedDevice);
                mUpdatedDevicesList.put(updatedDevice.getAddress(), updatedDevice);
            } else {
                Logger.d(TAG, "New Device : " + updatedDevice.getAddress());
                Logger.d(TAG, "With average RSSI : " + updatedDevice.getRunningAverageRssi());

                mDeviceMap.put(updatedDevice.getAddress(), updatedDevice);
                mNewDevicesList.put(updatedDevice.getAddress(), updatedDevice);
            }
        }
        reportMapContents();
        mTimeOfLastUpdate = Calendar.getInstance().getTimeInMillis();
    }

*/
    public void pruneDeviceList(final Map<String, BluetoothLeDevice> inputDeviceMap) {

        reportMapContents();

        mUpdatedDevicesList.clear();
        mNewDevicesList.clear();
        mMovingFartherDevicesList.clear();
        mMovingCloserDevicesList.clear();
        mDisappearingDevicesList.clear();

        for (final BluetoothLeDevice originalDevice : mDeviceMap.values()) {
            if (!inputDeviceMap.containsKey(originalDevice.getAddress())) {
                Logger.d(TAG, "Device disappeared : " + originalDevice.getAddress());
                mDisappearingDevicesList.put(originalDevice.getAddress(), originalDevice);
            }
        }

        for (final BluetoothLeDevice updatedDevice : inputDeviceMap.values()) {
            mAvailableDevicesList.put(updatedDevice.getAddress(), updatedDevice);

            if (mDeviceMap.containsKey(updatedDevice.getAddress())) {

                Logger.d(TAG, "Device has been updated!");
                Logger.d(TAG, "Device : " + updatedDevice.getAddress());
                Logger.d(TAG, "With average RSSI : " + updatedDevice.getRunningAverageRssi());

                final BluetoothLeDevice originalDevice = mDeviceMap.get(updatedDevice.getAddress());

                if (updatedDevice.getRunningAverageRssi() <= originalDevice.getRunningAverageRssi()) {
                    mMovingCloserDevicesList.put(updatedDevice.getAddress(), updatedDevice);
                } else {
                    mMovingFartherDevicesList.put(updatedDevice.getAddress(), updatedDevice);
                }

                mDeviceMap.put(updatedDevice.getAddress(), updatedDevice);
                mUpdatedDevicesList.put(updatedDevice.getAddress(), updatedDevice);
            } else {
                Logger.d(TAG, "New Device : " + updatedDevice.getAddress());
                Logger.d(TAG, "With average RSSI : " + updatedDevice.getRunningAverageRssi());

                mDeviceMap.put(updatedDevice.getAddress(), updatedDevice);
                mNewDevicesList.put(updatedDevice.getAddress(), updatedDevice);
            }
        }
        reportMapContents();
        mTimeOfLastUpdate = Calendar.getInstance().getTimeInMillis();
    }


/**
 * @param inputDeviceMap  - detectedDevicesMao from onLeScan
 * updatedDetectedDeviceMap  - storing updated devices (only available devices)
 *
 * */
    public ConcurrentHashMap<String, IBeaconDevice> updateDetectedDeviceMap(final Map<String, IBeaconDevice> inputDeviceMap) {

        reportMapContents();
/*
        mUpdatedDevicesList.clear();
        mNewDevicesList.clear();
        mMovingFartherDevicesList.clear();
        mMovingCloserDevicesList.clear();
        mDisappearingDevicesList.clear();
*/

            Log.d("SCANtask", "Enter else in updateDetectedDeviceMap");
            //removing disapeared devices
            if (!updatedDetectedDeviceMap.isEmpty()) {
                for (final IBeaconDevice originalDevice : updatedDetectedDeviceMap.values()) {
                    if (!inputDeviceMap.containsKey(String.valueOf(originalDevice.getMinor()))) {
                        updatedDetectedDeviceMap.remove(String.valueOf(originalDevice.getMinor()));
                    }
                }
            }
            //if devices already exist in updatedDetetectedDeviceMap, dont add them again
            if(!inputDeviceMap.isEmpty()) {
                for (final IBeaconDevice detectedDevice : inputDeviceMap.values()) {
                    if (updatedDetectedDeviceMap.containsKey(String.valueOf(detectedDevice.getMinor()))) {
                    }
                }

            //add new devices
            for (final IBeaconDevice detectedDevice : inputDeviceMap.values()) {
                if (!updatedDetectedDeviceMap.containsKey(String.valueOf(detectedDevice.getMinor()))) {
                    updatedDetectedDeviceMap.put(String.valueOf(detectedDevice.getMinor()), detectedDevice);
                    Log.d("SCANtask", "Added new Device in updateDetectedDeviceMap");


                }
            }
            }
            reportMapContents();
            mTimeOfLastUpdate = Calendar.getInstance().getTimeInMillis();

            return updatedDetectedDeviceMap;

    }

/*
        for (final IBeaconDevice updatedDevice : inputDeviceMap.values()) {
            mAvailableDevicesList.put(updatedDevice.getAddress(), updatedDevice);

            if (mDeviceMap.containsKey(updatedDevice.getAddress())) {

                Logger.d(TAG, "Device has been updated!");
                Logger.d(TAG, "Device : " + updatedDevice.getAddress());
                Logger.d(TAG, "With average RSSI : " + updatedDevice.getRunningAverageRssi());

                final BluetoothLeDevice originalDevice = mDeviceMap.get(updatedDevice.getAddress());

                if (updatedDevice.getRunningAverageRssi() <= originalDevice.getRunningAverageRssi()) {
                    mMovingCloserDevicesList.put(updatedDevice.getAddress(), updatedDevice);
                } else {
                    mMovingFartherDevicesList.put(updatedDevice.getAddress(), updatedDevice);
                }

                mDeviceMap.put(updatedDevice.getAddress(), updatedDevice);
                mUpdatedDevicesList.put(updatedDevice.getAddress(), updatedDevice);
            } else {
                Logger.d(TAG, "New Device : " + updatedDevice.getAddress());
                Logger.d(TAG, "With average RSSI : " + updatedDevice.getRunningAverageRssi());

                mDeviceMap.put(updatedDevice.getAddress(), updatedDevice);
                mNewDevicesList.put(updatedDevice.getAddress(), updatedDevice);
            }
        }
        reportMapContents();
        mTimeOfLastUpdate = Calendar.getInstance().getTimeInMillis();
    }

*/





    /**
     * @return The time in milliseconds of the last time the singleton was updated
     */
    public long getTimeOfLastUpdate() {
        return mTimeOfLastUpdate;
    }

    /**
     * @return The list of IBeacon devices detected during the last scan
     */
    public ConcurrentHashMap<String, BluetoothLeDevice> getAvailableDevicesList() {
        return mAvailableDevicesList;
    }

    public int getAvailableDeviceListSize() {
        return mAvailableDevicesList.size();
    }

    public int getNewDeviceListSize() {
        return mNewDevicesList.size();
    }

    public int getUpdatedDeviceListSize() {
        return mUpdatedDevicesList.size();
    }

    public int getMovingCloserDeviceListSize() {
        return mMovingCloserDevicesList.size();
    }

    public int getMovingFartherDeviceListSize() {
        return mMovingFartherDevicesList.size();
    }

    public int getDisappearingDeviceListSize() {
        return mDisappearingDevicesList.size();
    }

    public ArrayList<String> getDevicesAvailableAsStringList() {
        final ArrayList<String> list = new ArrayList<>();
     //   for (final BluetoothLeDevice device : mAvailableDevicesList.values()) {
            for (final BluetoothLeDevice device : mAvailableDevicesList.values()) {
            //    list.add(device.getAddress());
            if (BeaconUtils.getBeaconType(device) == BeaconType.IBEACON) {
                final IBeaconDevice iBeacon = new IBeaconDevice(device);
                   this.deviceAddress = Integer.toString(iBeacon.getMinor());
                //   this.deviceAddress = iBeacon.getUUID()+"  "+ iBeacon.getMajor()+"  "+ iBeacon.getMinor();
               // String str =formatTime( iBeacon.getFirstTimestamp());
                // list.add(iBeacon.getUUID()+"  "+ iBeacon.getMajor()+"  "+ iBeacon.getMinor());
                list.add(Integer.toString(iBeacon.getMinor()));
              //  formatTime(iBeacon.getFirstTimestamp())
            }
        }
        return list;
    }


    public ArrayList<String> getDetectedDevicesAsArrayList() {
        final ArrayList<String> detectedDevicesArrayList = new ArrayList<>();
        if(updatedDetectedDeviceMap.isEmpty()){

        }else {
            for (final IBeaconDevice iBeaconDevice : updatedDetectedDeviceMap.values()) {
                detectedDevicesArrayList.add(String.valueOf(iBeaconDevice.getMinor()));
            }
        }
                return detectedDevicesArrayList;
            }


    /*
        //   for (final BluetoothLeDevice device : mAvailableDevicesList.values()) {
        for (final BluetoothLeDevice device : mAvailableDevicesList.values()) {
            //    list.add(device.getAddress());
            if (BeaconUtils.getBeaconType(device) == BeaconType.IBEACON) {
                final IBeaconDevice iBeacon = new IBeaconDevice(device);
                this.deviceAddress = Integer.toString(iBeacon.getMinor());
                //   this.deviceAddress = iBeacon.getUUID()+"  "+ iBeacon.getMajor()+"  "+ iBeacon.getMinor();
                // String str =formatTime( iBeacon.getFirstTimestamp());
                // list.add(iBeacon.getUUID()+"  "+ iBeacon.getMajor()+"  "+ iBeacon.getMinor());
                list.add(Integer.toString(iBeacon.getMinor()));
                //  formatTime(iBeacon.getFirstTimestamp())
            }
        }
        return list;
    }*/
    /*
    public List<String> getDevicesUpdatedAsStringList() {
        final List<String> list = new ArrayList<>();
        for (final BluetoothLeDevice device : mUpdatedDevicesList.values()) {
            list.add(device.getAddress());
        }
        return list;
    }
*/

    /*
    public ArrayList<String> getDevicesUpdatedAsStringList() {

            final ArrayList<String> list = new ArrayList<>();

            for (final BluetoothLeDevice device : mUpdatedDevicesList.values()) {
                if (BeaconUtils.getBeaconType(device) == BeaconType.IBEACON) {
                    final IBeaconDevice iBeacon = new IBeaconDevice(device);
                    //   this.deviceAddress = iBeacon.getUUID()+"  "+ iBeacon.getMajor()+"  "+ iBeacon.getMinor();
                    // String str =formatTime( iBeacon.getFirstTimestamp());
                    // list.add(iBeacon.getUUID()+"  "+ iBeacon.getMajor()+"  "+ iBeacon.getMinor());
                    list.add(Integer.toString(iBeacon.getMinor()));
                    //  formatTime(iBeacon.getFirstTimestamp())
                }
            }
            return list;
        }

    */

    private static String formatTime(final long time) {
        return TimeFormatter.getIsoDateTime(time);
    }

    public List<String> getDevicesNewAsStringList() {
        final List<String> list = new ArrayList<>();
        for (final BluetoothLeDevice device : mNewDevicesList.values()) {
            list.add(device.getAddress());
        }
        return list;
    }

    public List<String> getDevicesMovingCloserAsStringList() {
        final List<String> list = new ArrayList<>();
        for (final BluetoothLeDevice device : mMovingCloserDevicesList.values()) {
            list.add(device.getAddress());
        }
        return list;
    }

    public List<String> getDevicesMovingFartherAsStringList() {
        final List<String> list = new ArrayList<>();
        for (final BluetoothLeDevice device : mMovingFartherDevicesList.values()) {
            list.add(device.getAddress());
        }
        return list;
    }

    public List<String> getDevicesDisappearingAsStringList() {
        final List<String> list = new ArrayList<>();
        for (final BluetoothLeDevice device : mDisappearingDevicesList.values()) {
            list.add(device.getAddress());
        }
        return list;
    }

    /**
     * Logs the contents of the device hash maps (for debugging purposes)
     */
    private void reportMapContents() {
        Logger.d(TAG, "***Reporting updated devices");
        Logger.d(TAG, "Total devices in memory : " + mDeviceMap.size());
        for (final BluetoothLeDevice originaldevice : mDeviceMap.values()) {
            Logger.d(TAG, "Device : " + originaldevice.getAddress());
            Logger.d(TAG, "With average RSSI : " + originaldevice.getRunningAverageRssi());

            try {
                Logger.d(TAG, "With Accuracy : " + new IBeaconDevice(originaldevice).getAccuracy());
            } catch (final Exception e) {
                Logger.e(TAG, "Failed to cast IBeacon " + originaldevice.getAddress() + " " + e.getLocalizedMessage());
            }

        }
        Logger.d(TAG, "Total devices in memory : " + mDeviceMap.size());
        Logger.d(TAG, "Number of available devices on the last scan : " + mAvailableDevicesList.size());
        Logger.d(TAG, "Number of updated devices on the last scan : " + mUpdatedDevicesList.size());
        Logger.d(TAG, "Number of new devices on the last scan : " + mNewDevicesList.size());
        Logger.d(TAG, "Number of devices moving nearer : " + mMovingCloserDevicesList.size());
        Logger.d(TAG, "Number of devices moving farther away : " + mMovingFartherDevicesList.size());
        Logger.d(TAG, "Number of devices that disappeared : " + mDisappearingDevicesList.size());


    }

    /**
     * @return Instance of the Singleton
     */
    public static Singleton getInstance() {
        if (sInstance == null) {
            synchronized (Singleton.class) {
                if (sInstance == null) {
                    sInstance = new Singleton();
                }
            }
        }
        return sInstance;
    }

}
