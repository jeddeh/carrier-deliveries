package info.jedda.carrierdeliveries.display;

import info.jedda.carrierdeliveries.entity.CarrierDeliveries;
import info.jedda.carrierdeliveries.entity.Delivery;

import java.util.ArrayList;

import info.jedda.carrierdeliveries.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Adapter for the Deliveries ListView.
 */
public class DeliveriesAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private final ArrayList<Delivery> deliveries;

    public DeliveriesAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        deliveries = CarrierDeliveries.getDeliveries();
    }
    
    @Override
    public int getCount() {
        return deliveries.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder;
    	
    	if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listitem_deliveries, null);
            holder = new ViewHolder();
            holder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

    	// Grey out the delivery on the ListView if completed.
        holder.tvAddress.setText(deliveries.get(position).getAddress());
        if (deliveries.get(position).isDelivered() == false) {
        	convertView.setBackgroundResource(R.drawable.selector_enabled);
        } else {
        	convertView.setBackgroundResource(R.drawable.selector_disabled);
        }
        
        return convertView;
    }
	
	static class ViewHolder {
		TextView tvAddress;
	}
}
