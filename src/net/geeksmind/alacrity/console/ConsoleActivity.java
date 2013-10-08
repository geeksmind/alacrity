package net.geeksmind.alacrity.console;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import net.geeksmind.alacrity.R;
import net.geeksmind.alacrity.component.ArduinoBoard;
import net.geeksmind.alacrity.shieldComm.HttpGetTask;
import net.geeksmind.alacrity.shieldComm.OnAsynTaskCallback;

/**
 * Author: coderh
 * Date: 9/30/13
 * Time: 10:44 PM
 */
public class ConsoleActivity extends Activity {

    // GUI Widgets
    private Button emitButton;
    private TextView statusContent;
    private ListView deviceListView;

    // arduino board instance
    private ArduinoBoard ardBd = ArduinoBoard.getInstance();

    public void showToast(String msg) {
        Toast toast = Toast.makeText(ConsoleActivity.this.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console);

        emitButton = (Button) this.findViewById(R.id.buttonEmit);
        statusContent = (TextView) this.findViewById(R.id.textViewStatusContent);
        deviceListView = (ListView) this.findViewById(R.id.deviceListView);

        Bundle extras = getIntent().getExtras();
        String ipSync = extras.getString("ipAddr");

        if (ipSync != null) {
            if (!ipSync.equals(ardBd.getIpAddr())) {
                showToast("IpAddr doesn't match, please check server config");
            }
        }
        statusContent.setText("\u279F" + ipSync);

        DevListAdaptor devAdapter = new DevListAdaptor(this, ardBd.getDeviceList());
        deviceListView.setAdapter(devAdapter);

        emitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = ardBd.generateURL();
                Toast toast = Toast.makeText(ConsoleActivity.this.getApplicationContext(), url, Toast.LENGTH_SHORT);
                toast.show();
                new HttpGetTask(new OnAsynTaskCallback() {
                    @Override
                    public void onTaskCompleted(String res) {
                        // TODO
                    }

                    @Override
                    public void onTaskStarted() {
                        // TODO
                    }
                }).execute(url);
            }
        });
    }
}