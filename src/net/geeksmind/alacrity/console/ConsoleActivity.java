package net.geeksmind.alacrity.console;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import net.geeksmind.alacrity.R;

/**
 * Author: coderh
 * Date: 9/30/13
 * Time: 10:44 PM
 */
public class ConsoleActivity extends Activity {

    // GUI Widgets
    private Button emitButton;
    private TextView statusContent;
    private RadioGroup cmdOptionsRadioGroup;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console);

        emitButton = (Button) this.findViewById(R.id.buttonEmit);
        cmdOptionsRadioGroup = (RadioGroup) this.findViewById(R.id.radioGroupOptions);
        statusContent = (TextView) this.findViewById(R.id.textViewStatusContent);
       /*
        syncIP = guiIpAddr;  // this code will not be reached if any errors occur
        emitButton.setEnabled(true);
        statusContent.setText("Sync to " + syncIP
                + " \nDevice = " + dvc.getName() + "(" + dvc.getType() + ", " + dvc.getPin() + ")");
         */
        emitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int checkRadioButtonId = cmdOptionsRadioGroup.getCheckedRadioButtonId();
//                RadioButton cmdRadioButton = (RadioButton) cmdOptionsRadioGroup.findViewById(checkRadioButtonId);
//                int cmdOptionIndex = cmdOptionsRadioGroup.indexOfChild(cmdRadioButton);
//                String msg = syncIP + "/?pin=" + dvc.getPin() + "&status=" + cmdOptionIndex;
//                showToast(msg);
            }
        });
    }

    public void syncFailAction() {
        statusContent.setText(R.string.default_status);
        emitButton.setEnabled(false);
    }


}