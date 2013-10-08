package net.geeksmind.alacrity.sync;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import net.geeksmind.alacrity.R;
import net.geeksmind.alacrity.component.ArduinoBoard;
import net.geeksmind.alacrity.console.ConsoleActivity;
import net.geeksmind.alacrity.shieldComm.HttpGetTask;
import net.geeksmind.alacrity.shieldComm.OnAsynTaskCallback;
import org.json.JSONException;

public class SyncActivity extends Activity {

    // Constants
    private static final String CLEAR_IP = " . . . ";
    private static final String DEFAULT_PREF_IP = "192.168.0.1";
    private static final String PREFS_NAME = "PrefsFile";
    private static final String DEFAULT_IP_ADDR_KEY = "defaultIpAddr";
    private static final String OPENING_IP_ADDR_KEY = "openingIpAddr";
    private static final String JSON_ERROR_MSG = "JSONObject paring error occurs";
    private static final String NETWORK_NOT_AVAILABLE_ERROR_MSG = "No available network";
    private static final String NETWORK_CONNECTION_ERROR_MSG = "Failed to connect";
    private static final String IP_SEGMENT_ERROR = "between 0 and 255";


    // GUI Widgets
    private Button clearButton;
    private Button resetButton;
    private Button setAsDefaultButton;
    private Button syncButton;
    private EditText edtTextIpAddrChunk1;
    private EditText edtTextIpAddrChunk2;
    private EditText edtTextIpAddrChunk3;
    private EditText edtTextIpAddrChunk4;
    private LinearLayout layoutIpAddr;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync);


        syncButton = (Button) this.findViewById(R.id.buttonSync);
        clearButton = (Button) this.findViewById(R.id.buttonClear);
        resetButton = (Button) this.findViewById(R.id.buttonReset);
        setAsDefaultButton = (Button) this.findViewById(R.id.buttonSetAsDefault);
        edtTextIpAddrChunk1 = (EditText) this.findViewById(R.id.editViewIpAddr1);
        edtTextIpAddrChunk2 = (EditText) this.findViewById(R.id.editViewIpAddr2);
        edtTextIpAddrChunk3 = (EditText) this.findViewById(R.id.editViewIpAddr3);
        edtTextIpAddrChunk4 = (EditText) this.findViewById(R.id.editViewIpAddr4);
        layoutIpAddr = (LinearLayout) this.findViewById(R.id.linearLayoutIpChunkList);


        addListenersToIpChunks(edtTextIpAddrChunk1, edtTextIpAddrChunk2, edtTextIpAddrChunk3, edtTextIpAddrChunk4);
        setIPAddrToGUI(getPrefIpAddr(OPENING_IP_ADDR_KEY));

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIPAddrToGUI(CLEAR_IP);
                edtTextIpAddrChunk1.requestFocus();
                // show soft keyboard for first IP seg, when cleanButton pushed
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtTextIpAddrChunk1, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIPAddrToGUI(getPrefIpAddr(DEFAULT_IP_ADDR_KEY));
                edtTextIpAddrChunk1.requestFocus();
                // hide soft keyboard for first IP seg, when resetButton pushed
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtTextIpAddrChunk1.getWindowToken(), 0);
            }
        });

        setAsDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddr = getIPAddrFromGUI();
                setPrefIpAddr(DEFAULT_IP_ADDR_KEY, ipAddr);
                String msg = "Default IPAddr is set to " + ipAddr;
                showToast(msg);
            }
        });

        // This widget is only for sync clickListener
        final ProgressDialog progDailog = new ProgressDialog(this);
        progDailog.setIndeterminate(true);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String guiIpAddr = getIPAddrFromGUI();
                String url = "http://" + guiIpAddr;

                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    final HttpGetTask getTask = new HttpGetTask(new OnAsynTaskCallback() {
                        @Override
                        public void onTaskCompleted(String res) {
                            progDailog.dismiss();
                            if (res.startsWith("ERROR")) {
                                showToast(NETWORK_CONNECTION_ERROR_MSG + " : " + guiIpAddr + "\nERROR CODE = " + res.split(":")[1]);
                            } else {
                                try {
                                    ArduinoBoard.getInstance().init(res);
//                                    showToast(ArduinoBoard.getInstance().toString());
                                    goToConsole(guiIpAddr);
                                } catch (JSONException e) {
                                    showToast(JSON_ERROR_MSG);
                                }
                            }
                        }
                        @Override
                        public void onTaskStarted() {
                            progDailog.show();
                            progDailog.setMessage("Sync to " + guiIpAddr + " ...");
                        }
                    });

                    progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            getTask.cancel(true);
                            showToast("Sync cancelled.");
                        }
                    });

                    getTask.execute(url);
                } else {
                    showToast(NETWORK_NOT_AVAILABLE_ERROR_MSG);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        setPrefIpAddr(OPENING_IP_ADDR_KEY, getIPAddrFromGUI());
    }


    // hacking: only for focus shifting
    EditText currentEditText = null;

    public void addListenersToIpChunks(EditText... editTexts) {

        for (final EditText editText : editTexts) {

            // ip seg focus shifting
            editText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            int edtViewIndex = layoutIpAddr.indexOfChild(editText);
                            // shift focus to the next editText which is not the last one
                            if (edtViewIndex < layoutIpAddr.getChildCount() - 1 && editText == currentEditText) {
                                layoutIpAddr.getChildAt(edtViewIndex + 2).requestFocus();
                            }
                            return true;
                    }
                    return false;
                }
            });

            // remove leading zeros when focus changes
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String ipChunkText = editText.getText().toString();
                    if (!editText.isFocused() && ipChunkText.matches("(0{2}\\d|0\\d|0\\d{2})")) { // when editText lose focus and has leading zeros
                        editText.setText(ipChunkText.replaceFirst("^0+(?!$)", ""));
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
                    currentEditText = editText;
                    // ipChunk local check, show error when the ipChunk is invalid
                    if (!isIPChunkValid(editText.getText().toString())) {
                        editText.setError(IP_SEGMENT_ERROR);
                    } else {
                        editText.setError(null);
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

    public void goToConsole(String ip) {
        Intent itt = new Intent(SyncActivity.this, ConsoleActivity.class);
        itt.putExtra("ipAddr", ip);
        startActivity(itt);
    }

    public void showToast(String msg) {
        Toast toast = Toast.makeText(SyncActivity.this.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public String getPrefIpAddr(String prefKey) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(prefKey, DEFAULT_PREF_IP);
    }

    public void setPrefIpAddr(String prefKey, String ip) {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(prefKey, ip);
        // Commit the edits!
        editor.commit();
    }

    public void setButtonsEnable(boolean enable) {
        setAsDefaultButton.setEnabled(enable);
        syncButton.setEnabled(enable);
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
        edtTextIpAddrChunk4.setText(ipChunkList[3].trim());  // remove spaces for Int parsing
    }

    public String makeString(String[] origin, String delimiter) {
        String retVal = "";
        for (int i = 0; i < origin.length - 1; i++) {
            retVal += origin[i] + delimiter;
        }
        return retVal + origin[origin.length - 1];
    }

    public String getIPAddrFromGUI() {
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