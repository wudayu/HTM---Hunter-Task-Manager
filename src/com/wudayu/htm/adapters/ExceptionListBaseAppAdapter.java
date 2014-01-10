package com.wudayu.htm.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wudayu.htm.R;
import com.wudayu.htm.beans.HtmBaseApp;

/**
 * 
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Feb 4, 2013 2:58:15 PM
 * @Description: This is David Wu's property.
 * 
 **/
public class ExceptionListBaseAppAdapter extends BaseAdapter {

	private List<HtmBaseApp> exceptionList;
	private int resource;
	private LayoutInflater inflater;

	public ExceptionListBaseAppAdapter(Context context, List<HtmBaseApp> exceptionList,
			int resource) {
		this.resource = resource;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.exceptionList = exceptionList;
	}

	@Override
	public int getCount() {
		return exceptionList == null ? 0 : exceptionList.size();
	}

	@Override
	public Object getItem(int position) {
		return exceptionList == null ? null : exceptionList
				.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (exceptionList == null)
			return null;
		ViewCache cache = new ViewCache();

		if (convertView == null) {
			convertView = inflater.inflate(resource, null);

			cache.txtAppItem = (TextView) convertView.findViewById(R.id.txt_lvitem_baseapp_exception_activity_edit_exception_list);

			convertView.setTag(cache);
		} else {
			cache = (ViewCache) convertView.getTag();
		}
		cache.txtAppItem.setText(exceptionList.get(position).getName());
		exceptionList.get(position).getIcon().setBounds(0, 0, 60, 60);
		cache.txtAppItem.setCompoundDrawables(exceptionList.get(position).getIcon(), null, null, null);
		cache.pkgName = exceptionList.get(position).getPkgName();

		return convertView;
	}

	public final class ViewCache {
		public TextView txtAppItem;
		public String pkgName;
	}

}
