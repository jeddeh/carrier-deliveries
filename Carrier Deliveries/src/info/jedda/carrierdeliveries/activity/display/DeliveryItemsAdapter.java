package info.jedda.carrierdeliveries.activity.display;

import info.jedda.carrierdeliveries.entity.DeliveryItem;

import java.util.ArrayList;

import info.jedda.carrierdeliveries.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Adapter for the DeliveryItems ListView.
 */
public class DeliveryItemsAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;
	ArrayList<DeliveryItem> deliveryItems;

	public DeliveryItemsAdapter(Context context, ArrayList<DeliveryItem> deliveryItems) {
		layoutInflater = LayoutInflater.from(context);
		this.deliveryItems = deliveryItems;
	}

	@Override
	public int getCount() {
		return deliveryItems.size();
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
			convertView = layoutInflater.inflate(R.layout.listitem_deliveryitem, null);
			holder = new ViewHolder();
			holder.tvJobName = (TextView) convertView.findViewById(R.id.tvJobName);
			holder.tvBreakdown = (TextView) convertView.findViewById(R.id.tvBreakdown);
			holder.tvQuantity = (TextView) convertView.findViewById(R.id.tvQuantity);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvJobName.setText(deliveryItems.get(position).getJobName());

		int bundleSize = deliveryItems.get(position).getBundleSize();
		int bundles = deliveryItems.get(position).getBundles();
		int items = deliveryItems.get(position).getItems();

		if (bundleSize == 0) {
			holder.tvBreakdown.setText("Unknown Bundle Size");
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(bundles).append(bundles == 1 ? " Bundle, " : " Bundles, ").append(items)
					.append(items == 1 ? " Item @ " : " Items @ ").append(bundleSize)
					.append("/Bundle");

			holder.tvBreakdown.setText(sb);
		}

		holder.tvQuantity.setText(Integer.toString(deliveryItems.get(position).getQuantity())
				+ " Total");

		// Grey out the delivery on the ListView if completed.
		if (deliveryItems.get(position).isSelected() == false) {
			convertView.setBackgroundResource(R.drawable.selector_unselected);
		} else {
			convertView.setBackgroundResource(R.drawable.selector_selected);
		}

		return convertView;
	}

	static class ViewHolder {
		TextView tvJobName;
		TextView tvBreakdown;
		TextView tvQuantity;
	}
}
