package net.geeksmind.alacrity.console;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import net.geeksmind.alacrity.R;
import net.geeksmind.alacrity.component.Device;

import java.util.List;

/**
 * Author: coderh
 * Date: 10/6/13
 * Time: 12:26 AM
 */

class DevListAdaptor extends ArrayAdapter<Device> {

    private Context context;
    private List<Device> deviceList;

    public DevListAdaptor(Context context, List<Device> deviceList) {
        super(context, R.layout.device_item, deviceList);
        this.deviceList = deviceList;
        this.context = context;
    }

    private class ViewHolder {
        TextView name;
        TextView info;
        Switch power;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.device_item, null);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.devName);
            holder.info = (TextView) convertView.findViewById(R.id.devInfo);
            holder.power = (Switch) convertView.findViewById(R.id.devSwitch);
            convertView.setTag(holder);

            holder.power.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    deviceList.get(position).setStatus(isChecked);
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Device dev = deviceList.get(position);
        holder.name.setText(dev.getName());
        holder.info.setText(dev.getType() + " - pin " + dev.getPin());
        holder.power.setChecked(dev.getStatus());
        Log.d("item", dev.toString());

        return convertView;

    }

}