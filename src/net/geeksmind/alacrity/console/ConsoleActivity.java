package net.geeksmind.alacrity.console;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import net.geeksmind.alacrity.R;
import net.geeksmind.alacrity.component.ArduinoBoard;

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

        statusContent.setText("Connected to " + ardBd.getIpAddr());

        Log.d("item", "1");
        DevListAdaptor devAdapter = new DevListAdaptor(this, ardBd.getDeviceList());
        Log.d("item", "2");
        deviceListView.setAdapter(devAdapter);
        Log.d("item", "3");

        emitButton.setEnabled(true);
        emitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(ConsoleActivity.this.getApplicationContext(), ardBd.toString(), Toast.LENGTH_SHORT);
                toast.show();
//                int checkRadioButtonId = cmdOptionsRadioGroup.getCheckedRadioButtonId();
//                RadioButton cmdRadioButton = (RadioButton) cmdOptionsRadioGroup.findViewById(checkRadioButtonId);
//                int cmdOptionIndex = cmdOptionsRadioGroup.indexOfChild(cmdRadioButton);
//                String msg = syncIP + "/?pin=" + dvc.getPin() + "&status=" + cmdOptionIndex;
//                showToast(msg);
            }
        });
        Log.d("item", "4");
    }

    public void syncFailAction() {
        statusContent.setText(R.string.default_status);
        emitButton.setEnabled(false);
    }


}