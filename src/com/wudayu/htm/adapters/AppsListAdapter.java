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
import com.wudayu.htm.beans.HtmApp;
import com.wudayu.htm.utils.HtmFormatter;

/**
 * 
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Feb 3, 2013 12:56:31 PM
 * @Description: This is David Wu's property.
 * 
 **/
public class AppsListAdapter extends BaseAdapter {

	private List<HtmApp> appsList;
	private int resource;
	private LayoutInflater inflater;
	private static Map<Integer, Boolean> selMap;

	public static Boolean getSelMapValueByKey(Integer key) {
		return selMap.get(key);
	}

	public static void setSelMapValueByKey(Integer key, Boolean value) {
		selMap.put(key, value);
	}

	public AppsListAdapter(Context context, List<HtmApp> appsList, int resource) {
		this.appsList = appsList;
		this.resource = resource;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		selMap = new HashMap<Integer, Boolean>();
		initSelMap();
	}

	public void initSelMap() {
		for (int i = 0; i < appsList.size(); i++) {
			selMap.put(i, false);
		}
	}

	@Override
	public int getCount() {
		return appsList == null ? 0 : appsList.size();
	}

	@Override
	public Object getItem(int position) {
		return appsList == null ? null : appsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (appsList == null)
			return null;

		ViewCache cache = null;

		if (convertView == null) {
			convertView = inflater.inflate(resource, null);

			cache = new ViewCache();

			cache.chkApp = (CheckBox) convertView
					.findViewById(R.id.chk_app_lv_apps_activity_apps);
			cache.ivApp = (ImageView) convertView
					.findViewById(R.id.iv_app_lv_apps_activity_apps);
			cache.txtAppName = (TextView) convertView
					.findViewById(R.id.txt_appname_lv_apps_activity_apps);
			cache.txtAppMem = (TextView) convertView
					.findViewById(R.id.txt_appmem_lv_apps_activity_apps);

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

		HtmApp app = appsList.get(position);
		cache.chkApp.setChecked(selMap.get(position));
		cache.ivApp.setImageDrawable(app.getIcon());
		cache.txtAppName.setText(app.getName());
		cache.txtAppMem.setText(HtmFormatter.sizeKbToString(app.getSize()));
		cache.pkgName = app.getPkgName();

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
