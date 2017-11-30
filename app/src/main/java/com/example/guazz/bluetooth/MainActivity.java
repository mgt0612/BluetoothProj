package com.example.guazz.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.guazz.bluetooth.R.layout.activity_main;

public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Set<BluetoothDevice> devices;
    private ListView mListView;
    private List<Map<String,String>> lesDevices = new ArrayList<Map<String,String>>();
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Map<String,String> n = new HashMap<String, String>();
                n.put("adresse", device.getAddress());
                if (!lesDevices.contains(n.get("Adresse"))){
                    Map<String, String> deviceI = new HashMap<String, String>();
                    deviceI.put("adresse", device.getAddress());
                    deviceI.put("nom", device.getName());
                    lesDevices.add(deviceI);
                    Toast.makeText(MainActivity.this, "New Device = " + device.getAddress(), Toast.LENGTH_SHORT).show();
                    final SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, lesDevices,
                            android.R.layout.simple_list_item_2,
                            new String[] {"adresse", "nom"},
                            new int[] {android.R.id.text1,
                                    android.R.id.text2});
                    mListView.setAdapter(adapter);
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        mListView = (ListView) findViewById(R.id.listView);
        if (bluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "Pas de Bluetooth",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(MainActivity.this, "Avec Bluetooth",
                    Toast.LENGTH_SHORT).show();

        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
        }
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
        devices = bluetoothAdapter.getBondedDevices();
        Map<String,String> kDev = new HashMap<String, String>();
        for (BluetoothDevice blueDevice : devices) {
            kDev.put("adresse",blueDevice.getAddress());
            kDev.put("nom", blueDevice.getName());
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);
        lesDevices.add(kDev);
        final SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, lesDevices,
                android.R.layout.simple_list_item_2,
                new String[] {"adresse", "nom"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
        mListView.setAdapter(adapter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.cancelDiscovery();
        unregisterReceiver(bluetoothReceiver);
    }
    public void discover(View view){
        devices = bluetoothAdapter.getBondedDevices();
        Map<String,String> kDev = new HashMap<String, String>();
        for (BluetoothDevice blueDevice : devices) {
            kDev.put("adresse",blueDevice.getAddress());
            kDev.put("nom", blueDevice.getName());
        }
        lesDevices.add(kDev);
        bluetoothAdapter.startDiscovery();
    }
}
