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
import com.wudayu.htm.adapters.ServicesListAdapter;
import com.wudayu.htm.beans.HtmService;

/**
 * 
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Jan 31, 2013 21:05:11 PM
 * @Description: This is David Wu's property.
 * 
 **/

public class ServicesActivity extends Activity {

	/*
	 * these four variables are used to be the arguments for method
	 * getServicesListInOrder() and refreshListViewInOrder()
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
	private List<HtmService> servicesList;
	/*
	 * the Adapter that controls the application list
	 */
	private ServicesListAdapter servicesListAdapter;
	/*
	 * ListView for showing running services
	 */
	private ListView lvServices;
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
	private ProgressDialog progressDialog;
	/*
	 * handler is used to handle the message
	 */
	private Handler handler;
	/*
	 * preferencesExcepList used to store preferences exception list
	 */
	private SharedPreferences preferencesExcepList;
	/*
	 * exceptionList used to store the package names from preferences exception
	 * list
	 */
	private List<String> exceptionList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_services);

		System.out.println("Services onCreate() Called");

		/*
		 * Initialize Variables
		 */
		servicesList = new ArrayList<HtmService>();
		exceptionList = new ArrayList<String>();
		orderCurrDirect = ORDER_DIRECT_ASC;
		initializeExceptionList();

		/*
		 * initialize widgets
		 */
		chkAllItem = (CheckBox) findViewById(R.id.chk_serviceslist_activity_services);
		lvServices = (ListView) findViewById(R.id.lv_services_activity_services);
		btnRefresh = (Button) findViewById(R.id.btn_refresh_activity_services);
		btnKill = (Button) findViewById(R.id.btn_kill_activity_services);
		btnKillAll = (Button) findViewById(R.id.btn_kill_all_activity_services);
		txtNames = (TextView) findViewById(R.id.txt_servicesnames_activity_services);
		txtMems = (TextView) findViewById(R.id.txt_servicesmems_activity_services);

		/*
		 * set listener or settings to widgets
		 */
		chkAllItem.setOnCheckedChangeListener(new chkAllItemOnClickListener());
		lvServices
				.setOnItemClickListener(new ServicesListOnItemClickListener());
		lvServices
				.setOnItemLongClickListener(new ServicesListOnItemLongClickListener());
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
		System.out.println("Services onStart() Called");

		super.onStart();
	}

	/*
	 * the event when user press one item from the listview
	 */
	private class ServicesListOnItemClickListener implements
			AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String pkgName = servicesList.get(position).getPkgName();
			/*
			 * when app has been pressed, we open the detail form
			 */
			goToAppInfoActivity(pkgName);
		}

	}

	/*
	 * registerForContextMenu when user long press the item from listview
	 */
	private class ServicesListOnItemLongClickListener implements
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
		inflater.inflate(R.menu.context_menu_lv_services_activity_services,
				menu);
	}

	/*
	 * the event when user press the item from context menu
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		/*
		 * the tag is convertView's tag from ServicesListAdapter or we can say
		 * targetView is the convertView
		 */
		ServicesListAdapter.ViewCache cache = (ServicesListAdapter.ViewCache) info.targetView
				.getTag();
		HtmService htmService = cache.htmService;

		switch (item.getItemId()) {
		/*
		 * kill task
		 */
		case R.id.context_item_kill_service_activity_services:
			boolean killSucceed = killService(htmService);
			refreshListView();
			Toast.makeText(
					getApplicationContext(),
					killSucceed ? getString(R.string.str_services_have_killed_activity_services)
							: getString(R.string.str_service_has_been_denied_activity_services),
					Toast.LENGTH_SHORT).show();
			return true;
			/*
			 * storage detail
			 */
		case R.id.context_item_equivalent_app_storage_detail_activity_services:
			goToAppInfoActivity(htmService.getPkgName());
			return true;
			/*
			 * uninstall
			 */
		case R.id.context_item_uninstall_equivalent_app_activity_services:
			goToAppInfoActivity(htmService.getPkgName());
			return true;
			/*
			 * add to exception list user can use EditExceptionListActivity to
			 * edit exception list
			 */
		case R.id.context_item_add_to_exception_list_activity_services:
			addToExceptionList(htmService);
			refreshListView();
		default:
			return super.onContextItemSelected(item);
		}
	}

	/**
	 * add application into exception list
	 * 
	 * @param pkgName
	 *            package name
	 */
	private void addToExceptionList(HtmService htmService) {
		SharedPreferences.Editor editor = preferencesExcepList.edit();
		editor.putString(htmService.getServiceName(), htmService.getPkgName());
		editor.commit();
		exceptionList.add(htmService.getServiceName());
	}

	/**
	 * open application detail infomation activity
	 * 
	 * @param pkgName
	 *            package name
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
			boolean killSucceed = true;
			boolean nothingChecked = true;
			for (int i = 0; i < servicesList.size(); ++i) {
				if (ServicesListAdapter.getSelMapValueByKey(i)) {
					killSucceed = killService(servicesList.get(i));
					nothingChecked = false;
				}
			}
			/*
			 * if nothing selected, notify the user and quit current function
			 */
			if (nothingChecked) {
				Toast.makeText(ServicesActivity.this,
						R.string.str_nothing_has_checked_activity_services,
						Toast.LENGTH_SHORT).show();
				return;
			}
			/*
			 * reload the list from memory
			 */
			refreshListView();
			Toast.makeText(
					ServicesActivity.this,
					killSucceed ? getString(R.string.str_services_have_killed_activity_services)
							: getString(R.string.str_part_of_service_has_been_denied_activity_services),
					Toast.LENGTH_SHORT).show();
		}

	}

	/*
	 * the button 'Kill All''s onClickListener
	 */
	private class btnKillAllOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			boolean killSucceed = true;
			/*
			 * kill application one by one
			 */
			for (HtmService service : servicesList) {
				killSucceed = killService(service);
			}
			/*
			 * reload the list from memory
			 */
			refreshListView();
			Toast.makeText(
					ServicesActivity.this,
					killSucceed ? getString(R.string.str_services_have_killed_activity_services)
							: getString(R.string.str_part_of_service_has_been_denied_activity_services),
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
			for (int i = 0; i < servicesList.size(); ++i)
				ServicesListAdapter.setSelMapValueByKey(i, isChecked);
			/*
			 * notify the adapter to recall its getView() method to refresh the
			 * listview
			 */
			servicesListAdapter.notifyDataSetChanged();
		}

	}

	@Override
	protected void onRestart() {
		System.out.println("Services onRestart() Called");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		System.out.println("Services onResume() Called");
		super.onResume();
	}

	@Override
	protected void onPause() {
		System.out.println("Services onPause() Called");
		super.onPause();
	}

	@Override
	protected void onStop() {
		System.out.println("Services onStop() Called");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		System.out.println("Services onDestroy() Called");

		/*
		 * important, when the screen rotate current activity will be closed
		 * finish() will be called before that, progressDialog must be dismissed
		 */
		if (progressDialog != null)
			progressDialog.dismiss();

		super.onDestroy();
	}

	/**
	 * getServicesListWithoutOrder() is used to get applications list for type
	 * HtmService but without any order
	 * 
	 */
	public void getServicesListWithoutOrder() {
		/*
		 * initialize the list servicesList
		 */
		servicesList.clear();

		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		/*
		 * get the Running Process list
		 */
		List<ActivityManager.RunningServiceInfo> listOfProcesses = activityManager
				.getRunningServices(0xff);

		Iterator<ActivityManager.RunningServiceInfo> iter = listOfProcesses
				.iterator();
		/*
		 * we need use PackageManager to get the information of the service
		 */
		PackageManager pm = this.getPackageManager();
		while (iter.hasNext()) {
			ActivityManager.RunningServiceInfo info = (ActivityManager.RunningServiceInfo) (iter
					.next());
			CharSequence appName = null;
			Drawable appIcon = null;
			int memorySize = -1;
			String serviceName = null;
			String shortServiceName = null;
			String packageName = null;
			int pid = -1;
			String processName = null;
			Intent serviceIntent = null;
			/*
			 * check if the process is user's or system's
			 */
			if (!info.process.equals(getPackageName())
					&& !exceptionList.contains(info.service.getClassName())) {
				try {
					/*
					 * get the information we need
					 */
					appName = pm.getApplicationLabel(pm.getApplicationInfo(
							info.process, PackageManager.GET_META_DATA));
					appIcon = pm.getApplicationIcon(pm.getApplicationInfo(
							info.process, PackageManager.GET_META_DATA));
					memorySize = activityManager
							.getProcessMemoryInfo(new int[] { info.pid })[0]
							.getTotalPss();
					serviceName = info.service.getClassName();
					shortServiceName = info.service.getShortClassName();
					packageName = info.service.getPackageName();
					pid = info.pid;
					processName = info.process;
					serviceIntent = new Intent();
					serviceIntent.setComponent(info.service);

					HtmService service = new HtmService(pid,
							appName.toString(), packageName, appIcon,
							memorySize, serviceName, shortServiceName,
							serviceIntent, processName);

					servicesList.add(service);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * sortServicesList() is used to sort the applications list for type
	 * HtmService with order
	 * 
	 * @param sortMethod
	 *            sort by name or memory
	 * @param orderDirect
	 *            asc or desc
	 */
	public void sortServicesList(String sortMethod, String orderDirect) {
		/*
		 * use different comparator in different arguments
		 */
		if (sortMethod == ORDER_METHOD_NAME) {
			if (orderDirect == ORDER_DIRECT_ASC)
				Collections.sort(servicesList,
						new ComparatorHtmServiceNameASC());
			else
				Collections.sort(servicesList,
						new ComparatorHtmServiceNameDESC());
		} else if (sortMethod == ORDER_METHOD_MEM) {
			if (orderDirect != ORDER_DIRECT_DESC)
				Collections
						.sort(servicesList, new ComparatorHtmServiceMemASC());
			else
				Collections.sort(servicesList,
						new ComparatorHtmServiceMemDESC());
		}

	}

	/**
	 * ComparatorHtmServiceNameASC is sort by name in asc
	 * 
	 * @author David
	 * 
	 */
	public class ComparatorHtmServiceNameASC implements Comparator<HtmService> {
		@Override
		public int compare(HtmService lhs, HtmService rhs) {
			return lhs.getServiceName().compareToIgnoreCase(rhs.getServiceName());
		}
	}

	/**
	 * ComparatorHtmServiceNameDESC is sort by name in desc
	 * 
	 * @author David
	 * 
	 */
	public class ComparatorHtmServiceNameDESC implements Comparator<HtmService> {
		@Override
		public int compare(HtmService lhs, HtmService rhs) {
			return rhs.getServiceName().compareToIgnoreCase(lhs.getServiceName());
		}
	}

	/**
	 * ComparatorHtmServiceMemASC is sort by memory in asc
	 * 
	 * @author David
	 * 
	 */
	public class ComparatorHtmServiceMemASC implements Comparator<HtmService> {
		@Override
		public int compare(HtmService lhs, HtmService rhs) {
			return lhs.getSize() - rhs.getSize();
		}
	}

	/**
	 * ComparatorHtmServiceMemDESC is sort by memory in desc
	 * 
	 * @author David
	 * 
	 */
	public class ComparatorHtmServiceMemDESC implements Comparator<HtmService> {
		@Override
		public int compare(HtmService lhs, HtmService rhs) {
			return rhs.getSize() - lhs.getSize();
		}
	}

	/**
	 * initialize exception list
	 */
	private void initializeExceptionList() {
		exceptionList.clear();
		preferencesExcepList = getSharedPreferences(
				getString(R.string.preferences_exception_list_service), 0);
		exceptionList.addAll(preferencesExcepList.getAll().keySet());
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

				bindServicesListIntoListView();

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
			.show(ServicesActivity.this,
					getString(R.string.str_title_refresh_services_list_activity_services),
					getString(R.string.str_content_refresh_services_list_activity_services));

			new Thread() {
				public void run() {
					getServicesListWithoutOrder();
	
					handler.sendEmptyMessage(0);
				}
			}.start();
		}

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
		sortServicesList(sortMethod, orderDirect);
		bindServicesListIntoListView();
	}

	/**
	 * bindServicesListIntoListView() is the Method for setting or resetting the
	 * adapter for listview
	 */
	private void bindServicesListIntoListView() {
		servicesListAdapter = new ServicesListAdapter(getApplicationContext(),
				servicesList,
				R.layout.lvitem_service_lv_services_activity_services);
		lvServices.setAdapter(servicesListAdapter);
	}

	/**
	 * killService() is used to kill one service by their HtmService
	 * 
	 * @param htmService
	 * @return is success close or not
	 */
	private boolean killService(HtmService htmService) {
		Intent stopServiceIntent = htmService.getServiceIntent();

		try {
			return stopService(stopServiceIntent);
		} catch (SecurityException e) {
			System.out.println(e.toString());
		}
		
		return false;
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
