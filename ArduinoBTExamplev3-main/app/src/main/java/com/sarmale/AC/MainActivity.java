package com.sarmale.AC;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;




import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    // Correct place for nextP method
    public void nextP(View view) {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }

    // Global variables we will use in the
    private static final String TAG = "MyAppTag";
    private static final int REQUEST_ENABLE_BT = 1;
    //We will use a Handler to get the BT Connection statys
    public static Handler handler;
    private final static int ERROR_READ = 0; // used in bluetooth handler to identify message update
    BluetoothDevice arduinoBTModule = null;
    UUID arduinoUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //We declare a default UUID to create the global variable
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intances of BT Manager and BT Adapter needed to work with BT in Android.
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        //Intances of the Android UI elements that will will use during the execution of the APP

        TextView btDevices = findViewById(R.id.btDevices);
        TextView co2TextView = findViewById(R.id.co2TextView);
        TextView humTextView = findViewById(R.id.humTextView);
        TextView tempTextView = findViewById(R.id.tempTextView);

        Button connectToDevice = (Button) findViewById(R.id.connectToDevice);
        Button seachDevices = (Button) findViewById(R.id.seachDevices);
        Button clearValues = (Button) findViewById(R.id.refresh);
        Button openMain2Button = findViewById(R.id.nextP);
        Log.d(TAG, "Begin Execution");


        //Add event listener for openMain2Button
        openMain2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextP(view);
            }
        });


        //Using a handler to update the interface in case of an error connecting to the BT device
        //My idea is to show handler vs RxAndroid
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {

                    case ERROR_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        co2TextView.setText(arduinoMsg);
                        humTextView.setText(arduinoMsg);
                        tempTextView.setText(arduinoMsg);
                        break;
                }
            }
        };



        // Set a listener event on a button to clear the texts
        clearValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btDevices.setText("");
                co2TextView.setText("");
                humTextView.setText("");
                tempTextView.setText("");
            }
        });

        // Create an Observable from RxAndroid
        //The code will be executed when an Observer subscribes to the the Observable
        final Observable<ConnectedThread> connectToBTObservable = Observable.create(emitter -> {
            Log.d(TAG, "Calling connectThread class");
            //Call the constructor of the ConnectThread class
            //Passing the Arguments: an Object that represents the BT device,
            // the UUID and then the handler to update the UI
            ConnectThread connectThread = new ConnectThread(arduinoBTModule, arduinoUUID, handler);
            connectThread.run();
            //Check if Socket connected
            if (connectThread.getMmSocket().isConnected()) {
                Log.d(TAG, "Calling ConnectedThread class");
                //The pass the Open socket as arguments to call the constructor of ConnectedThread
                ConnectedThread connectedThread = new ConnectedThread(connectThread.getMmSocket());
                connectedThread.run();

                    // If we have read a value from the Arduino
                    // we call the onNext() function
                    //This value will be observed by the observer
                emitter.onNext(connectedThread);

                //We just want to stream 1 value, so we close the BT stream
                connectedThread.cancel();
            }
            // SystemClock.sleep(5000); // simulate delay
            //Then we close the socket connection
            connectThread.cancel();
            //We could Override the onComplete function
            emitter.onComplete();

        });
        connectToDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (arduinoBTModule != null) {
                    connectToBTObservable
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(connectedThread -> {
                                // Now, you have the instance of ConnectedThread from which you can get the readings
                                // Retrieve the readings
                                String co2Read = connectedThread.getCo2Read();
                                String humRead = connectedThread.getHumidityRead();
                                String tempRead = connectedThread.getTemperatureRead();

                                // Assuming you have a TextView for each reading in your layout
                                TextView co2TextView = findViewById(R.id.co2TextView);
                                TextView humTextView = findViewById(R.id.humTextView);
                                TextView tempTextView = findViewById(R.id.tempTextView);

                                // Set the text of the TextViews to the readings
                                co2TextView.setText("CO2 Level: " + co2Read + " ppm");
                                humTextView.setText("Humidity: " + humRead + " %");
                                tempTextView.setText("Temperature: " + tempRead + " °C");
                            });
                }
            }
        });

        seachDevices.setOnClickListener(new View.OnClickListener() {
            //Display all the linked BT Devices
            @Override
            public void onClick(View view) {
                //Check if the phone supports BT
                if (bluetoothAdapter == null) {
                    // Device doesn't support Bluetooth
                    Log.d(TAG, "Device doesn't support Bluetooth");
                } else {
                    Log.d(TAG, "Device support Bluetooth");
                    //Check BT enabled. If disabled, we ask the user to enable BT
                    if (!bluetoothAdapter.isEnabled()) {
                        Log.d(TAG, "Bluetooth is disabled");
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            Log.d(TAG, "We don't BT Permissions");
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            Log.d(TAG, "Bluetooth is enabled now");
                        } else {
                            Log.d(TAG, "We have BT Permissions");
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            Log.d(TAG, "Bluetooth is enabled now");
                        }

                    } else {
                        Log.d(TAG, "Bluetooth is enabled");
                    }
                    String btDevicesString="";
                    Set < BluetoothDevice > pairedDevices = bluetoothAdapter.getBondedDevices();

                    if (pairedDevices.size() > 0) {
                        // There are paired devices. Get the name and address of each paired device.
                        for (BluetoothDevice device: pairedDevices) {
                            String deviceName = device.getName();
                            String deviceHardwareAddress = device.getAddress(); // MAC address
                            Log.d(TAG, "deviceName:" + deviceName);
                            Log.d(TAG, "deviceHardwareAddress:" + deviceHardwareAddress);
                            //We append all devices to a String that we will display in the UI
                            btDevicesString=btDevicesString+deviceName+" || "+deviceHardwareAddress+"\n";
                            //If we find the HC 05 device (the Arduino BT module)
                            //We assign the device value to the Global variable BluetoothDevice
                            //We enable the button "Connect to HC 05 device"
                            if (deviceName.equals("HC-05")) {
                                Log.d(TAG, "HC-05 found");
                                arduinoUUID = device.getUuids()[0].getUuid();
                                arduinoBTModule = device;
                                //HC -05 Found, enabling the button to read results
                                connectToDevice.setEnabled(true);
                            }
                            btDevices.setText(btDevicesString);
                        }
                    }
                }
                Log.d(TAG, "Button Pressed");
            }
        });
    }

}
