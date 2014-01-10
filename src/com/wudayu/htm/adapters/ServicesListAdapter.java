package com.wudayu.htm.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.wudayu.htm.R;
import com.wudayu.htm.beans.HtmService;
import com.wudayu.htm.utils.HtmFormatter;


/**
 *
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Feb 5, 2013 10:44:31 AM
 * @Description: This is David Wu's property.
 *
 **/
public class ServicesListAdapter extends BaseAdapter {

	private List<HtmService> servicesList;
	private int resource;
	private LayoutInflater inflater;
	private static Map<Integer, Boolean> selMap;

	public static Boolean getSelMapValueByKey(Integer key) {
		return selMap.get(key);
	}

	public static void setSelMapValueByKey(Integer key, Boolean value) {
		selMap.put(key, value);
	}

	public ServicesListAdapter(Context context, List<HtmService> servicesList, int resource) {
		this.servicesList = servicesList;
		this.resource = resource;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		selMap = new HashMap<Integer, Boolean>();
		initSelMap();
	}

	public void initSelMap() {
		for (int i = 0; i < servicesList.size(); i++) {
			selMap.put(i, false);
		}
	}

	@Override
	public int getCount() {
		return servicesList == null ? 0 : servicesList.size();
	}

	@Override
	public Object getItem(int position) {
		return servicesList == null ? null : servicesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (servicesList == null)
			return null;

		ViewCache cache = null;

		if (convertView == null) {
			convertView = inflater.inflate(resource, null);

			cache = new ViewCache();

			cache.chkApp = (CheckBox) convertView
					.findViewById(R.id.chk_service_lv_services_activity_services);
			cache.ivApp = (ImageView) convertView
					.findViewById(R.id.iv_service_lv_services_activity_services);
			cache.txtShortServiceName = (TextView) convertView
					.findViewById(R.id.txt_servicename_lv_services_activity_services);
			cache.txtAppMem = (TextView) convertView
					.findViewById(R.id.txt_servicemem_lv_services_activity_services);

			convertView.setTag(cache);
		} else {
			cache = (ViewCache) convertView.getTag();
		}

		final int id = position;
		cache.chkApp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selMap.put(id, ((CheckBox)v).isChecked());
			}
		});

		HtmService htmService = servicesList.get(position);
		cache.chkApp.setChecked(selMap.get(position));
		cache.ivApp.setImageDrawable(htmService.getIcon());
		cache.txtShortServiceName.setText(htmService.getServiceName(), BufferType.SPANNABLE);
		cache.txtAppMem.setText(HtmFormatter.sizeKbToString(htmService.getSize()));
		cache.htmService = htmService;

		if (position % 2 == 0)
			convertView.setBackgroundColor(Color.DKGRAY);
		else
			convertView.setBackgroundColor(Color.BLACK);

		return convertView;
	}

	public final class ViewCache {
		public CheckBox chkApp;
		public ImageView ivApp;
		public TextView txtShortServiceName;
		public TextView txtAppMem;
		public HtmService htmService;
	}

}
