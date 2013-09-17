package net.geeksmind.alacrity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;


public class MyActivity extends Activity {

    // Constants
    private final static String CLEAR_IP = " . . . ";
    private final static String DEFAULT_IP = "192.168.0.1";

    // GUI Widgets
    private Button setAsDefaultButton;
    private Button emitButton;
    private LinearLayout layoutIpAddr;
    private EditText edtTextIpAddrChunk1;
    private EditText edtTextIpAddrChunk2;
    private EditText edtTextIpAddrChunk3;
    private EditText edtTextIpAddrChunk4;
    private RadioGroup cmdOptionsRadioGroup;

    //TODO: remove focus when buttons pushed

    //TODO: make ipChunkList manipulations generic

    //TODO: persistent storage of default ipAddr

    //TODO: JSONObject and web communication

    public void addTextCheckerToIpChunks(EditText... editTexts) {
        for (final EditText editText : editTexts) {
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // remove leading zeros
                    String ipChunkText = editText.getText().toString();
                    if (!editText.isFocused() && ipChunkText.matches("(0{2}\\d|0\\d|0\\d{2})")) { // when editText lose focus and has leading zeros
                        //this setText will invoke focus shifting again, but this time the test will not pass.
                        editText.setText(ipChunkText.replaceFirst("^0+(?!$)", ""));
                        Log.d("FocusChange", editText.getText().toString());
                    }
                }
            });

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Empty
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    // ipChunk local check, show error when the ipChunk is invalid
                    if (!isIPChunkValid(editText.getText().toString())) {
                        editText.setError("between 0 and 255");
                    } else {
                        editText.setError(null);
                        Log.d("TextChange", editText.getText().toString());
                        // shift focus to the next editText which is not the last one
                        int edtViewIndex = layoutIpAddr.indexOfChild(editText);
                        if (editText.getText().toString().length() == 3 && edtViewIndex < layoutIpAddr.getChildCount() - 1 && editText.isFocused()) {
                            layoutIpAddr.getChildAt(edtViewIndex + 2).requestFocus();
                        }
                    }

                    // detect GUI errors, e.g. any IpChunk invalid, then disable buttons
                    if (isExistGUIErrors()) {
                        setButtonsEnable(false);
                    } else {
                        setButtonsEnable(true);
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

        emitButton = (Button) this.findViewById(R.id.buttonEmit);
        layoutIpAddr = (LinearLayout) this.findViewById(R.id.linearLayoutIpChunkList);
        setAsDefaultButton = (Button) this.findViewById(R.id.buttonSetAsDefault);
        edtTextIpAddrChunk1 = (EditText) this.findViewById(R.id.editViewIpAddr1);
        edtTextIpAddrChunk2 = (EditText) this.findViewById(R.id.editViewIpAddr2);
        edtTextIpAddrChunk3 = (EditText) this.findViewById(R.id.editViewIpAddr3);
        edtTextIpAddrChunk4 = (EditText) this.findViewById(R.id.editViewIpAddr4);
        cmdOptionsRadioGroup = (RadioGroup) this.findViewById(R.id.radioGroupOptions);

        // make <light on> as default
        cmdOptionsRadioGroup.check(R.id.radioButtonOn);

        addTextCheckerToIpChunks(edtTextIpAddrChunk1, edtTextIpAddrChunk2, edtTextIpAddrChunk3, edtTextIpAddrChunk4);
    }

    public void setButtonsEnable(boolean enable) {
        setAsDefaultButton.setEnabled(enable);
        emitButton.setEnabled(enable);
    }

    public boolean isExistGUIErrors() {
        String[] ipAddrChunkList = getIPAddrChunkListFromView();
        return !isIPChunkListValid(ipAddrChunkList);
    }

    public void setIPAddrToGUI(String ipAddr) {
        String[] ipChunkList = ipAddr.split("\\.");
        edtTextIpAddrChunk1.setText(ipChunkList[0].trim());
        edtTextIpAddrChunk2.setText(ipChunkList[1].trim());
        edtTextIpAddrChunk3.setText(ipChunkList[2].trim());
        edtTextIpAddrChunk4.setText(ipChunkList[3].trim());  // trim is needed here, because " 1" can not be parsed.
    }

    public void onClearButtonClick(View v) {
        setIPAddrToGUI(CLEAR_IP);
        edtTextIpAddrChunk1.requestFocus();
    }

    public void onResetButtonClick(View v) {
        setIPAddrToGUI(DEFAULT_IP);
    }

    public void onEmitButtonClick(View v) {
        int checkRadioButtonId = cmdOptionsRadioGroup.getCheckedRadioButtonId();
        RadioButton cmdRadioButton = (RadioButton) cmdOptionsRadioGroup.findViewById(checkRadioButtonId);
        int cmdOptionIndex = cmdOptionsRadioGroup.indexOfChild(cmdRadioButton);
        String msg = "Send to : " + getIPAddr() + " , with cmd : " + cmdOptionIndex;

        Toast toast = Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public String makeString(String[] origin, String delimiter) {
        String retVal = "";
        for (int i = 0; i < origin.length - 1; i++) {
            retVal += origin[i] + delimiter;
        }
        return retVal + origin[origin.length - 1];
    }

    public String getIPAddr() {
        String[] ipAddrChunkList = getIPAddrChunkListFromView();
        return makeString(ipAddrChunkList, ".");
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