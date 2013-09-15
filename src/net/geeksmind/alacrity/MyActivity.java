package net.geeksmind.alacrity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;


public class MyActivity extends Activity {

    final static private String IPADDR_BAD_FORMAT_CODE = "-1";

    private Button emitButton;
    private EditText edtTextIpAddrChunk1;
    private EditText edtTextIpAddrChunk2;
    private EditText edtTextIpAddrChunk3;
    private EditText edtTextIpAddrChunk4;
    private RadioGroup cmdOptionsRadioGroup;

    //TODO: make ipChunkList manipulations generic

    //TODO: persistent storage of default ipAddr

    //TODO: JSONObject and web communication

    public void addTextCheckerToIpChunks(EditText... editTexts) {
        for (final EditText editText : editTexts) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Empty
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d("BEFORE", editText.getText().toString());
                    if (isExistGUIErrors()) {
                        Log.d("AFTER", editText.getText().toString());
//                        editText.setError("between 0 and 255");
                        emitButton.setEnabled(false);
                    } else {
//                        editText.setError(null);
                        emitButton.setEnabled(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Empty
                }
            });
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        emitButton = ((Button) this.findViewById(R.id.buttonEmit));
        cmdOptionsRadioGroup = (RadioGroup) this.findViewById(R.id.radioGroupOptions);
        edtTextIpAddrChunk1 = ((EditText) this.findViewById(R.id.editViewIpAddr1));
        edtTextIpAddrChunk2 = ((EditText) this.findViewById(R.id.editViewIpAddr2));
        edtTextIpAddrChunk3 = ((EditText) this.findViewById(R.id.editViewIpAddr3));
        edtTextIpAddrChunk4 = ((EditText) this.findViewById(R.id.editViewIpAddr4));

        // make light on as default
        cmdOptionsRadioGroup.check(R.id.radioButtonOn);

        addTextCheckerToIpChunks(edtTextIpAddrChunk1, edtTextIpAddrChunk2, edtTextIpAddrChunk3, edtTextIpAddrChunk4);

    }

    public boolean isExistGUIErrors() {
        return getIPAddr().equals(IPADDR_BAD_FORMAT_CODE);
    }

    public void onClearButtonClick(View v) {
        edtTextIpAddrChunk1.setText("");
        edtTextIpAddrChunk2.setText("");
        edtTextIpAddrChunk3.setText("");
        edtTextIpAddrChunk4.setText("");
        emitButton.setEnabled(false);
    }

    public void onResetButtonClick(View v) {
        edtTextIpAddrChunk1.setText("192");
        edtTextIpAddrChunk2.setText("168");
        edtTextIpAddrChunk3.setText("0");
        edtTextIpAddrChunk4.setText("1");
        emitButton.setEnabled(true);
    }

    public void onEmitButtonClick(View v) {
        int checkRadioButtonId = cmdOptionsRadioGroup.getCheckedRadioButtonId();
        RadioButton cmdRadioButton = (RadioButton) cmdOptionsRadioGroup.findViewById(checkRadioButtonId);
        int cmdOptionIndex = cmdOptionsRadioGroup.indexOfChild(cmdRadioButton);
        String msg = "Send to : " + getIPAddr() + " , with cmd : " + cmdOptionIndex;

        Toast toast = Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public String getIPAddr() {
        String[] ipAddrChunkList = getIPAddrChunkListFromView();
        if (!isIPChunkListValid(ipAddrChunkList)) {
            return IPADDR_BAD_FORMAT_CODE;
        } else {
            return ipAddrChunkList[0] + "." + ipAddrChunkList[1] + "." + ipAddrChunkList[2] + "." + ipAddrChunkList[3];
        }
    }

    public String[] getIPAddrChunkListFromView() {
        return new String[]{
                edtTextIpAddrChunk1.getText().toString(),
                edtTextIpAddrChunk2.getText().toString(),
                edtTextIpAddrChunk3.getText().toString(),
                edtTextIpAddrChunk4.getText().toString()
        };
    }

    public boolean isIPChunkValid(String chunk) {
        if (chunk.trim().equals(""))
            return false;
        int chunkNumber = Integer.parseInt(chunk);
        return chunkNumber <= 255 && chunkNumber >= 0;
    }

    public boolean isIPChunkListValid(String[] chunkList) {
        for (String chunk : chunkList) {
            if (!isIPChunkValid(chunk)) {
                return false;
            }
        }
        return true;
    }
}
