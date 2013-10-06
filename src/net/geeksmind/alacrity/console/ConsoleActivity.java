package net.geeksmind.alacrity.console;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import net.geeksmind.alacrity.R;
import net.geeksmind.alacrity.component.ArduinoBoard;
import net.geeksmind.alacrity.shieldComm.OnTaskCompleted;
import net.geeksmind.alacrity.shieldComm.ShieldComm;

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console);

        emitButton = (Button) this.findViewById(R.id.buttonEmit);
        statusContent = (TextView) this.findViewById(R.id.textViewStatusContent);
        deviceListView = (ListView) this.findViewById(R.id.deviceListView);

        statusContent.setText("sync to " + ardBd.getIpAddr());

        DevListAdaptor devAdapter = new DevListAdaptor(this, ardBd.getDeviceList());
        deviceListView.setAdapter(devAdapter);

        emitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = ardBd.generateURL();
                Toast toast = Toast.makeText(ConsoleActivity.this.getApplicationContext(), url, Toast.LENGTH_SHORT);
                toast.show();
                ShieldComm.emitArduino(new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(String res) {
                        // TODO
                    }
                }, url);
            }
        });
    }
}