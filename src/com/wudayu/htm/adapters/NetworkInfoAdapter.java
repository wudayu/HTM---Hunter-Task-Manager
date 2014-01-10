package com.wudayu.htm.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wudayu.htm.R;

/**
 * 
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Feb 15, 2013 5:45:55 PM
 * @Description: This is David Wu's property.
 * 
 **/
public class NetworkInfoAdapter extends BaseAdapter {

	private List<String> hints;
	private List<String> contents;
	private LayoutInflater inflater;
	private int resource;

	public NetworkInfoAdapter(Context context, List<String> hints,
			List<String> contents, int resource) {
		this.hints = hints;
		this.contents = contents;
		this.resource = resource;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return hints.size();
	}

	@Override
	public Object getItem(int position) {
		return hints.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewCache cache = null;

		if (convertView == null) {
			convertView = inflater.inflate(resource, null);

			cache = new ViewCache();

			cache.txtHint = (TextView) convertView
					.findViewById(R.id.hinttext_lvitem_info_lv_info_activity_networking);
			cache.txtContent = (TextView) convertView
					.findViewById(R.id.contenttext_lvitem_info_lv_info_activity_networking);

			convertView.setTag(cache);
		} else {
			cache = (ViewCache) convertView.getTag();
		}

		cache.txtHint.setText(hints.get(position));
		cache.txtContent.setText(contents.get(position));

		if (position % 2 == 1)
			convertView.setBackgroundColor(Color.DKGRAY);
		else
			convertView.setBackgroundColor(Color.BLACK);
		
		return convertView;
	}

	private final class ViewCache {
		public TextView txtHint;
		public TextView txtContent;
	}

}
