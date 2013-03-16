package com.wudayu.htm.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wudayu.htm.R;
import com.wudayu.htm.adapters.SysappsListAdapter;
import com.wudayu.htm.beans.HtmSysapp;


/**
 *
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Jan 31, 2013 21:04:40 PM
 * @Description: This is David Wu's property.
 *
 **/

public class SysappsActivity extends Activity {

	/*
	 * these four variables are used to be the arguments for method
	 * getSysappsListInOrder() and refreshListViewInOrder()
	 */
	private static final String ORDER_METHOD_NAME = "name";
	private static final String ORDER_METHOD_MEM = "memory";

	private static final String ORDER_DIRECT_ASC = "asc";
	private static final String ORDER_DIRECT_DESC = "desc";

	/*
	 * used to record the current order direct
	 */
	private String orderCurrDirect;

	/**
	 * Global Variables
	 */
	/*
	 * the current applications list
	 */
	List<HtmSysapp> sysappsList;
	/*
	 * the Adapter that controls the application list
	 */
	SysappsListAdapter sysappsListAdapter;
	/*
	 * ListView for showing running sysapps
	 */
	private ListView lvSysapps;
	/*
	 * three bottom buttons
	 */
	private Button btnRefresh;
	private Button btnKill;
	private Button btnKillAll;
	/*
	 * three top stuff that can be clicked
	 */
	private CheckBox chkAllItem;
	private TextView txtNames;
	private TextView txtMems;
	/*
	 * ProgressDialog is used to show on the top while a lot of work are doing
	 * background
	 */
	ProgressDialog progressDialog;
	/*
	 * handler is used to handle the message
	 */
	Handler handler;
	/*
	 * preferencesExcepList used to store preferences exception list
	 */
	SharedPreferences preferencesExcepList;
	/*
	 * exceptionList used to store the package names from preferences exception
	 * list
	 */
	List<String> exceptionList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sysapps);

		System.out.println("Sysapps onCreate() Called");

		/*
		 * Initialize Variables
		 */
		sysappsList = new ArrayList<HtmSysapp>();
		exceptionList = new ArrayList<String>();
		orderCurrDirect = ORDER_DIRECT_ASC;
		initializeExceptionList();

		/*
		 * initialize widgets
		 */
		chkAllItem = (CheckBox) findViewById(R.id.chk_sysappslist_activity_sysapps);
		lvSysapps = (ListView) findViewById(R.id.lv_sysapps_activity_sysapps);
		btnRefresh = (Button) findViewById(R.id.btn_refresh_activity_sysapps);
		btnKill = (Button) findViewById(R.id.btn_kill_activity_sysapps);
		btnKillAll = (Button) findViewById(R.id.btn_kill_all_activity_sysapps);
		txtNames = (TextView) findViewById(R.id.txt_sysappsnames_activity_sysapps);
		txtMems = (TextView) findViewById(R.id.txt_sysappsmems_activity_sysapps);

		/*
		 * set listener or settings to widgets
		 */
		chkAllItem.setOnCheckedChangeListener(new chkAllItemOnClickListener());
		lvSysapps.setOnItemClickListener(new SysappsListOnItemClickListener());
		lvSysapps.setOnItemLongClickListener(new SysappsListOnItemLongClickListener());
		txtNames.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		txtMems.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		btnRefresh.setOnClickListener(new btnRefreshOnClickListener());
		btnKill.setOnClickListener(new btnKillOnClickListener());
		btnKillAll.setOnClickListener(new btnKillAllOnClickListener());
		txtNames.setOnClickListener(new txtNamesOnClickListener());
		txtMems.setOnClickListener(new txtMemsOnClickListener());

		/*
		 * load the application ListView first
		 */
		refreshListView();
	}

	@Override
	protected void onStart() {
		System.out.println("Sysapps onStart() Called");

		super.onStart();
	}

	/*
	 * the event when user press one item from the listview
	 */
	private class SysappsListOnItemClickListener implements
			AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String pkgName = sysappsList.get(position).getPkgName();
			/*
			 * when app has been pressed, we open the detail form
			 */
			goToAppInfoActivity(pkgName);
		}

	}

	/*
	 * registerForContextMenu when user long press the item from listview
	 */
	private class SysappsListOnItemLongClickListener implements
			AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			registerForContextMenu(parent);

			return false;
		}

	}

	/*
	 * inflate the context menu
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_lv_sysapps_activity_sysapps, menu);
	}

	/*
	 * the event when user press the item from context menu
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		/*
		 * the tag is convertView's tag from SysappsListAdapter or we can say
		 * targetView is the convertView
		 */
		SysappsListAdapter.ViewCache cache = (SysappsListAdapter.ViewCache) info.targetView
				.getTag();
		String pkgName = cache.pkgName;

		switch (item.getItemId()) {
			/*
			 * kill task
			 */
		case R.id.context_item_kill_task_activity_sysapps:
			killApp(pkgName);
			refreshListView();
			Toast.makeText(getApplicationContext(),
					getString(R.string.str_sysapps_have_killed_activity_sysapps),
					Toast.LENGTH_SHORT).show();
			return true;
			/*
			 * storage detail
			 */
		case R.id.context_item_storage_detail_activity_sysapps:
			goToAppInfoActivity(pkgName);
			return true;
			/*
			 * force stop or uninstall
			 */
		case R.id.context_item_forcestop_disable_activity_sysapps:
			goToAppInfoActivity(pkgName);
			return true;
			/*
			 * add to exception list user can use EditExceptionListActivity to
			 * edit exception list
			 */
		case R.id.context_item_add_to_exception_list_activity_sysapps:
			addToExceptionList(pkgName);
			refreshListView();
		default:
			return super.onContextItemSelected(item);
		}
	}

	/**
	 * add application into exception list
	 * @param pkgName package name
	 */
	private void addToExceptionList(String pkgName) {
		SharedPreferences.Editor editor = preferencesExcepList.edit();
		editor.putString(pkgName, pkgName);
		editor.commit();
		exceptionList.add(pkgName);
	}

	/**
	 * open application detail infomation activity
	 * @param pkgName package name
	 */
	public void goToAppInfoActivity(String pkgName) {
		Intent intent = new Intent(
				android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setData(Uri.parse("package:" + pkgName));
		startActivity(intent);
	}

	/*
	 * used to sort by name
	 */
	private class txtNamesOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			/*
			 * if the current order is asc then use desc instead vice versa
			 */
			if (orderCurrDirect == ORDER_DIRECT_ASC) {
				refreshListViewInOrder(ORDER_METHOD_NAME, ORDER_DIRECT_DESC);
				orderCurrDirect = ORDER_DIRECT_DESC;
			} else {
				refreshListViewInOrder(ORDER_METHOD_NAME, ORDER_DIRECT_ASC);
				orderCurrDirect = ORDER_DIRECT_ASC;
			}
		}

	}

	/*
	 * used to sort by memory
	 */
	private class txtMemsOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			/*
			 * if the current order is asc then use desc instead vice versa
			 */
			if (orderCurrDirect == ORDER_DIRECT_ASC) {
				refreshListViewInOrder(ORDER_METHOD_MEM, ORDER_DIRECT_DESC);
				orderCurrDirect = ORDER_DIRECT_DESC;
			} else {
				refreshListViewInOrder(ORDER_METHOD_MEM, ORDER_DIRECT_ASC);
				orderCurrDirect = ORDER_DIRECT_ASC;
			}
		}

	}

	/*
	 * the button 'Kill''s onClickListener
	 */
	private class btnKillOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			/*
			 * this variable is used to make sure that the user has selected any
			 * items
			 */
			boolean nothingChecked = true;
			for (int i = 0; i < sysappsList.size(); ++i) {
				if (SysappsListAdapter.getSelMapValueByKey(i)) {
					killApp(sysappsList.get(i).getPkgName());
					nothingChecked = false;
				}
			}
			/*
			 * if nothing selected, notify the user and quit current function
			 */
			if (nothingChecked) {
				Toast.makeText(SysappsActivity.this,
						R.string.str_nothing_has_checked_activity_sysapps,
						Toast.LENGTH_SHORT).show();
				return;
			}
			/*
			 * reload the list from memory
			 */
			refreshListView();
			Toast.makeText(SysappsActivity.this,
					getString(R.string.str_sysapps_have_killed_activity_sysapps),
					Toast.LENGTH_SHORT).show();
		}

	}

	/*
	 * the button 'Kill All''s onClickListener
	 */
	private class btnKillAllOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			/*
			 * kill application one by one
			 */
			for (HtmSysapp app : sysappsList) {
				killApp(app.getPkgName());
			}
			/*
			 * reload the list from memory
			 */
			refreshListView();
			Toast.makeText(SysappsActivity.this,
					getString(R.string.str_sysapps_have_killed_activity_sysapps),
					Toast.LENGTH_SHORT).show();
		}

	}

	/*
	 * the button 'Refresh''s onClickListener
	 */
	private class btnRefreshOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			/*
			 * is simple just call refreshListView() to reload list from memory
			 */
			refreshListView();
		}

	}

	/*
	 * the checkbox on the left top for select all items from listview
	 * OnCheckedChangeListener
	 */
	private class chkAllItemOnClickListener implements OnCheckedChangeListener {

		/**
		 * onCheckedChanged() is used to check all checkbox on each item of the
		 * listview
		 */
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			for (int i = 0; i < sysappsList.size(); ++i)
				SysappsListAdapter.setSelMapValueByKey(i, isChecked);
			/*
			 * notify the adapter to recall its getView() method to refresh the
			 * listview
			 */
			sysappsListAdapter.notifyDataSetChanged();
		}

	}


	@Override
	protected void onRestart() {
System.out.println("Sysapps onRestart() Called");
		super.onRestart();
	}

	@Override
	protected void onResume() {
System.out.println("Sysapps onResume() Called");
		super.onResume();
	}

	@Override
	protected void onPause() {
System.out.println("Sysapps onPause() Called");
		super.onPause();
	}

	@Override
	protected void onStop() {
System.out.println("Sysapps onStop() Called");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
System.out.println("Sysapps onDestroy() Called");

		/*
		 * important, when the screen rotate
		 * current activity will be closed
		 * finish() will be called
		 * before that, progressDialog must be dismissed
		 */
		if (progressDialog != null)
			progressDialog.dismiss();

		super.onDestroy();
	}

	/**
	 * getSysappsListWithoutOrder() is used to get applications list for type
	 * HtmSysapp but without any order
	 * 
	 */
	public void getSysappsListWithoutOrder() {
		/*
		 * initialize the list sysappsList
		 */
		sysappsList.clear();

		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		/*
		 * get the Running Process list
		 */
		List<ActivityManager.RunningAppProcessInfo> listOfProcesses = activityManager
				.getRunningAppProcesses();

		Iterator<RunningAppProcessInfo> iter = listOfProcesses.iterator();
		/*
		 * we need use PackageManager to get the information of the process
		 */
		PackageManager pm = this.getPackageManager();
		while (iter.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (iter
					.next());
			CharSequence appName = null;
			Drawable appIcon = null;
			int memorySize = 0;
			/*
			 * check if the process is user's or system's
			 */
			if (!info.processName.equals(getPackageName())
					&& !exceptionList.contains(info.processName)
					&& pm.getLaunchIntentForPackage(info.processName) == null) {
				try {
					/*
					 * get the application name, icon and memory size
					 */
					appName = pm.getApplicationLabel(pm.getApplicationInfo(
							info.processName, PackageManager.GET_META_DATA));
					appIcon = pm.getApplicationIcon(pm.getApplicationInfo(
							info.processName, PackageManager.GET_META_DATA));
					memorySize = activityManager
							.getProcessMemoryInfo(new int[] { info.pid })[0]
							.getTotalPss();

					HtmSysapp app = new HtmSysapp(info.pid, appName.toString(),
							info.processName, appIcon, memorySize);
					sysappsList.add(app);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * sortSysappsList() is used to sort the applications list for type HtmSysapp with
	 * order
	 * 
	 * @param sortMethod
	 *            sort by name or memory
	 * @param orderDirect
	 *            asc or desc
	 */
	public void sortSysappsList(String sortMethod, String orderDirect) {
		/*
		 * use different comparator in different arguments
		 */
		if (sortMethod == ORDER_METHOD_NAME) {
			if (orderDirect == ORDER_DIRECT_ASC)
				Collections.sort(sysappsList, new ComparatorHtmSysappNameASC());
			else
				Collections.sort(sysappsList, new ComparatorHtmSysappNameDESC());
		} else if (sortMethod == ORDER_METHOD_MEM) {
			if (orderDirect != ORDER_DIRECT_DESC)
				Collections.sort(sysappsList, new ComparatorHtmSysappMemASC());
			else
				Collections.sort(sysappsList, new ComparatorHtmSysappMemDESC());
		}

	}

	/**
	 * ComparatorHtmSysappNameASC is sort by name in asc
	 * 
	 * @author David
	 * 
	 */
	public class ComparatorHtmSysappNameASC implements Comparator<HtmSysapp> {
		@Override
		public int compare(HtmSysapp lhs, HtmSysapp rhs) {
			return lhs.getName().compareToIgnoreCase(rhs.getName());
		}
	}

	/**
	 * ComparatorHtmSysappNameDESC is sort by name in desc
	 * 
	 * @author David
	 * 
	 */
	public class ComparatorHtmSysappNameDESC implements Comparator<HtmSysapp> {
		@Override
		public int compare(HtmSysapp lhs, HtmSysapp rhs) {
			return rhs.getName().compareToIgnoreCase(lhs.getName());
		}
	}

	/**
	 * ComparatorHtmSysappMemASC is sort by memory in asc
	 * 
	 * @author David
	 * 
	 */
	public class ComparatorHtmSysappMemASC implements Comparator<HtmSysapp> {
		@Override
		public int compare(HtmSysapp lhs, HtmSysapp rhs) {
			return lhs.getSize() - rhs.getSize();
		}
	}

	/**
	 * ComparatorHtmSysappMemDESC is sort by memory in desc
	 * 
	 * @author David
	 * 
	 */
	public class ComparatorHtmSysappMemDESC implements Comparator<HtmSysapp> {
		@Override
		public int compare(HtmSysapp lhs, HtmSysapp rhs) {
			return rhs.getSize() - lhs.getSize();
		}
	}

	/**
	 * refreshListView() is used to refresh the list by reloading the memory,
	 * the procedure is slow so I use a progressDialog to make the user
	 * experience right
	 */
	private void refreshListView() {
		handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				bindSysappsListIntoListView();

				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				chkAllItem.setChecked(false);
			}
		};

		if (progressDialog == null) {
			initializeExceptionList();

			progressDialog = ProgressDialog
			.show(SysappsActivity.this,
					getString(R.string.str_title_refresh_sysapps_list_activity_sysapps),
					getString(R.string.str_content_refresh_sysapps_list_activity_sysapps));

			new Thread() {
				public void run() {
					getSysappsListWithoutOrder();
	
					handler.sendEmptyMessage(0);
				}
			}.start();
		}
	}

	/**
	 * initialize exception list
	 */
	private void initializeExceptionList() {
		exceptionList.clear();
		preferencesExcepList = getSharedPreferences(
				getString(R.string.preferences_exception_list_app_sysapp), 0);
		exceptionList.addAll(preferencesExcepList.getAll().keySet());
	}

	/**
	 * refreshListViewInOrder didn't like refreshListViewWithoutOrder it doesn't
	 * reload the memory, so it's fast just for sort
	 * 
	 * @param sortMethod
	 *            sort by name or memory
	 * @param orderDirect
	 *            asc or desc
	 */
	private void refreshListViewInOrder(String sortMethod, String orderDirect) {
		sortSysappsList(sortMethod, orderDirect);
		bindSysappsListIntoListView();
	}

	/**
	 * bindSysappsListIntoListView() is the Method for setting or resetting the
	 * adapter for listview
	 */
	private void bindSysappsListIntoListView() {
		sysappsListAdapter = new SysappsListAdapter(getApplicationContext(),
				sysappsList, R.layout.lvitem_sysapp_lv_sysapps_activity_sysapps);
		lvSysapps.setAdapter(sysappsListAdapter);
	}

	/**
	 * killApp() is used to kill one app by their package name
	 * 
	 * @param pkgName
	 *            package name
	 */
	private void killApp(String pkgName) {
		ActivityManager actvityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		actvityManager.killBackgroundProcesses(pkgName);
	}

	/**
	 * the code below is trying to close the program with double click back
	 * button
	 * 
	 */
	private Boolean isExit = false;

	/*
	 * I block the first 'pushed back button' action
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitByDoubleClick();
		}

		return false;
	}

	/*
	 * then, I make a Timer for 2 seconds if no 'pushed back button' action came
	 * in, then ignore the first action otherwise, kill myself.
	 */
	private void exitByDoubleClick() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true;
			Toast.makeText(this, R.string.str_exit_app_toast_message,
					Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false;
				}
			}, 2000);
		} else {
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
}
