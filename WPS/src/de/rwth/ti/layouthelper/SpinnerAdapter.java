package de.rwth.ti.layouthelper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.rwth.ti.wps.R;

public class SpinnerAdapter extends ArrayAdapter<String> {

	private LayoutInflater inflater;

	public SpinnerAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public SpinnerAdapter(Activity activity, int resource,
			int textViewResourceId) {
		super(activity.getBaseContext(), resource, textViewResourceId);

		inflater = activity.getLayoutInflater();
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, R.layout.spinner_dropdown_item, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// return getCustomView(position, R.layout.spinner_item, parent);
		return super.getView(position, convertView, parent);
	}

	private View getCustomView(int position, int resource, ViewGroup parent) {
		View layout = inflater.inflate(resource, parent, false);
		((TextView) layout.findViewById(R.id.spinner_item_text))
				.setText(getItem(position));
		return layout;
	}
}
