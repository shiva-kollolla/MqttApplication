package com.example.mqttapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Button button;
    private MqttAsyncClient client;

    private String hostName = "127.0.0.1";
    private String userName = "pub_client";
    private String password = "pub123";
    public static final String TOPIC = "test";

    private TextView subText;
    private Vibrator vibrator;
    private Button connectButton;
    private Button disconnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        button = findViewById(R.id.btn_connect);
        subText = (TextView) findViewById(R.id.text);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        connectButton = findViewById(R.id.conn_btn);
        disconnectButton = findViewById(R.id.disconnect_btn);
        connectButton.setOnClickListener(this);
        disconnectButton.setOnClickListener(this);
    }

    public void publish(View v) {
        Log.d(TAG, "publish() called");
//                String topic = "test";
        String message = "got message";
        try {
            client.publish(TOPIC, message.getBytes(), 0, false);
        } catch (MqttException e) {
            Log.e(TAG, "publish() exception: ", e);
        }
    }

    public void subscription() {
        Log.d(TAG, "subscription() called");
        try {
            client.subscribe(TOPIC, 0);
        } catch (MqttException e) {
            Log.e(TAG, "subscription() exception: ", e);
        }
    }

    public void connect(View v) {
        try {
            Log.d(TAG, "connect() called");
            String clientId = MqttClient.generateClientId();
            client = new MqttAsyncClient("tcp://10.0.2.2:1883", clientId,null);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setUserName(userName);
            connectOptions.setPassword(password.toCharArray());
            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    Log.d(TAG, "connectComplete() called");
                }

                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(TAG, "connectionLost() called");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(TAG, "messageArrived() called");
                    subText.setText(new String(message.getPayload()));
//                        vibrator.vibrate(500);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "deliveryComplete() called");
                }
            });
            IMqttToken token = client.connect(connectOptions);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                      // We are connected
                    Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_SHORT).show();
                    subscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this, "connection failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, "connect() exception: ", e);
        }
    }

    public void disconnect(View view) {
        Log.d(TAG, "connect() called");
        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are Disconnected
                    Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this, "could not disconnect", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            Log.e(TAG, "onCreate() exception: ", e);
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.conn_btn:  connect(view);
            break;

            case R.id.disconnect_btn: disconnect(view);
            break;

            case R.id.publish_btn: publish(view);
            break;

        }

    }
}


