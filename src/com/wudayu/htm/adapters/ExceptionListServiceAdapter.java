package com.wudayu.htm.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wudayu.htm.R;
import com.wudayu.htm.beans.HtmService;


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
public class ExceptionListServiceAdapter extends BaseAdapter {

	private List<HtmService> exceptionList;
	private int resource;
	private LayoutInflater inflater;

	public ExceptionListServiceAdapter(Context context, List<HtmService> exceptionList,
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

			cache.txtServiceItem = (TextView) convertView.findViewById(R.id.txt_lvitem_service_exception_activity_edit_exception_list);

			convertView.setTag(cache);
		} else {
			cache = (ViewCache) convertView.getTag();
		}
		cache.txtServiceItem.setText(exceptionList.get(position).getServiceName());
		exceptionList.get(position).getIcon().setBounds(0, 0, 60, 60);
		cache.txtServiceItem.setCompoundDrawables(exceptionList.get(position).getIcon(), null, null, null);
		cache.pkgName = exceptionList.get(position).getPkgName();
		cache.serviceName = exceptionList.get(position).getServiceName();

		return convertView;
	}

	public final class ViewCache {
		public TextView txtServiceItem;
		public String pkgName;
		public String serviceName;
	}

}
