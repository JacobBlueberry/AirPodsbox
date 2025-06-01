package com.example.airpodscontroller;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private TextView statusText;
    private TextView batteryText;
    private Button connectButton;
    private Button noiseControlButton;
    private Button transparencyButton;

    // AirPods Pro 特征 UUID
    private static final UUID AIRPODS_SERVICE_UUID = UUID.fromString("74ec2172-0bad-4d01-8f77-997b2be0722a");
    private static final UUID NOISE_CONTROL_CHARACTERISTIC_UUID = UUID.fromString("74ec2172-0bad-4d01-8f77-997b2be0722b");
    private static final UUID TRANSPARENCY_CHARACTERISTIC_UUID = UUID.fromString("74ec2172-0bad-4d01-8f77-997b2be0722c");
    private static final UUID BATTERY_SERVICE_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    private static final UUID BATTERY_CHARACTERISTIC_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        statusText = findViewById(R.id.statusText);
        batteryText = findViewById(R.id.batteryText);
        connectButton = findViewById(R.id.connectButton);
        noiseControlButton = findViewById(R.id.noiseControlButton);
        transparencyButton = findViewById(R.id.transparencyButton);

        // 初始化蓝牙适配器
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // 检查权限
        checkPermissions();

        // 设置按钮点击事件
        connectButton.setOnClickListener(v -> scanForAirPods());
        noiseControlButton.setOnClickListener(v -> toggleNoiseControl());
        transparencyButton.setOnClickListener(v -> toggleTransparency());
    }

    private void checkPermissions() {
        String[] permissions = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        };

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
                break;
            }
        }
    }

    private void scanForAirPods() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        // 开始扫描
        bluetoothAdapter.startDiscovery();
        statusText.setText("正在搜索 AirPods...");
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread(() -> {
                    statusText.setText("已连接到 AirPods");
                    gatt.discoverServices();
                });
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread(() -> {
                    statusText.setText("已断开连接");
                    batteryText.setText("电量: --");
                });
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService batteryService = gatt.getService(BATTERY_SERVICE_UUID);
                if (batteryService != null) {
                    BluetoothGattCharacteristic batteryCharacteristic = 
                        batteryService.getCharacteristic(BATTERY_CHARACTERISTIC_UUID);
                    if (batteryCharacteristic != null) {
                        // 启用通知
                        gatt.setCharacteristicNotification(batteryCharacteristic, true);
                        // 读取电量
                        gatt.readCharacteristic(batteryCharacteristic);
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, 
                                       BluetoothGattCharacteristic characteristic,
                                       int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (BATTERY_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                    final int batteryLevel = characteristic.getIntValue(
                        BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    runOnUiThread(() -> {
                        batteryText.setText(String.format("电量: %d%%", batteryLevel));
                    });
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic) {
            if (BATTERY_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                final int batteryLevel = characteristic.getIntValue(
                    BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                runOnUiThread(() -> {
                    batteryText.setText(String.format("电量: %d%%", batteryLevel));
                });
            }
        }
    };

    private void toggleNoiseControl() {
        if (bluetoothGatt != null) {
            BluetoothGattService service = bluetoothGatt.getService(AIRPODS_SERVICE_UUID);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(NOISE_CONTROL_CHARACTERISTIC_UUID);
                if (characteristic != null) {
                    // 切换降噪模式
                    byte[] value = new byte[]{0x01};
                    characteristic.setValue(value);
                    bluetoothGatt.writeCharacteristic(characteristic);
                }
            }
        }
    }

    private void toggleTransparency() {
        if (bluetoothGatt != null) {
            BluetoothGattService service = bluetoothGatt.getService(AIRPODS_SERVICE_UUID);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(TRANSPARENCY_CHARACTERISTIC_UUID);
                if (characteristic != null) {
                    // 切换通透模式
                    byte[] value = new byte[]{0x01};
                    characteristic.setValue(value);
                    bluetoothGatt.writeCharacteristic(characteristic);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
    }
} 