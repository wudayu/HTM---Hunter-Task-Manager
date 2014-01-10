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

import com.wudayu.htm.R;
import com.wudayu.htm.beans.HtmSysapp;
import com.wudayu.htm.utils.HtmFormatter;


/**
 *
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Feb 4, 2013 10:01:29 PM
 * @Description: This is David Wu's property.
 *
 **/
public class SysappsListAdapter extends BaseAdapter {

	private List<HtmSysapp> sysappsList;
	private int resource;
	private LayoutInflater inflater;
	private static Map<Integer, Boolean> selMap;

	public static Boolean getSelMapValueByKey(Integer key) {
		return selMap.get(key);
	}

	public static void setSelMapValueByKey(Integer key, Boolean value) {
		selMap.put(key, value);
	}

	public SysappsListAdapter(Context context, List<HtmSysapp> sysappsList, int resource) {
		this.sysappsList = sysappsList;
		this.resource = resource;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		selMap = new HashMap<Integer, Boolean>();
		initSelMap();
	}

	public void initSelMap() {
		for (int i = 0; i < sysappsList.size(); i++) {
			selMap.put(i, false);
		}
	}

	@Override
	public int getCount() {
		return sysappsList == null ? 0 : sysappsList.size();
	}

	@Override
	public Object getItem(int position) {
		return sysappsList == null ? null : sysappsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (sysappsList == null)
			return null;

		ViewCache cache = null;

		if (convertView == null) {
			convertView = inflater.inflate(resource, null);

			cache = new ViewCache();

			cache.chkApp = (CheckBox) convertView
					.findViewById(R.id.chk_sysapp_lv_sysapps_activity_sysapps);
			cache.ivApp = (ImageView) convertView
					.findViewById(R.id.iv_sysapp_lv_sysapps_activity_sysapps);
			cache.txtAppName = (TextView) convertView
					.findViewById(R.id.txt_sysappname_lv_sysapps_activity_sysapps);
			cache.txtAppMem = (TextView) convertView
					.findViewById(R.id.txt_sysappmem_lv_sysapps_activity_sysapps);

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

		HtmSysapp sysapp = sysappsList.get(position);
		cache.chkApp.setChecked(selMap.get(position));
		cache.ivApp.setImageDrawable(sysapp.getIcon());
		cache.txtAppName.setText(sysapp.getName());
		cache.txtAppMem.setText(HtmFormatter.sizeKbToString(sysapp.getSize()));
		cache.pkgName = sysapp.getPkgName();

		if (position % 2 == 0)
			convertView.setBackgroundColor(Color.DKGRAY);
		else
			convertView.setBackgroundColor(Color.BLACK);

		return convertView;
	}

	public final class ViewCache {
		public CheckBox chkApp;
		public ImageView ivApp;
		public TextView txtAppName;
		public TextView txtAppMem;
		public String pkgName;
	}

}
