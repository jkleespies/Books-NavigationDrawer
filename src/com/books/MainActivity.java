package com.books;

import java.util.ArrayList;

import com.books.adapter.NavDrawerItem;
import com.books.adapter.NavDrawerListAdapter;
import com.dm.zbar.android.scanner.ZBarConstants;
import com.books.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	// private ArrayList<NavDrawerItem> navDrawerItems;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	
//	handle Map 
	protected static Integer displayViewNumber = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.navigation_drawer_array);

		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.navigation_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// Drawer Icons zu Drawer hinzufügen
		// Search
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
				.getResourceId(0, -1)));
		// Map
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
				.getResourceId(1, -1)));
		// Favorites
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
				.getResourceId(2, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		// setting Change List Fragment
		mDrawerList.setAdapter(adapter);
		
		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		if (savedInstanceState == null && displayViewNumber ==0) {
			// on first time display view for first nav item
			displayView(0);
			Log.d("if onCreate", "displayView");
		}
//		if (displayViewNumber ==1){
//			displayView(1);
//		}
		
	}

	// Slide menu item click listener
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			displayView(position);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title 
		// Drawer
		if (mDrawerToggle.onOptionsItemSelected(item)) {

			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// // Called when invalidateOptionsMenu() is used
	 @Override
	 public boolean onPrepareOptionsMenu(Menu menu) {
	 // if nav drawer is opened, hide the action items
	 boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
	 menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
	 return super.onPrepareOptionsMenu(menu);
	 }

	// displaying different fragments for selected nav drawer items
	public void displayView(int position) {
		// update the fragments when drawer item is selected
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new SearchFragment();
			break;
		case 1:
//			if (displayViewNumber == 0){
//			
//				displayViewNumber =1;
//				Log.d("displayView==0", ""+ displayViewNumber);
//				Intent i = new Intent(getApplicationContext(), MainActivity.class);
//				startActivity(i);
//				break;}
//			if (displayViewNumber ==1){
				fragment = new MapLocationFragment();
//				displayViewNumber= 0;
//				Log.d("displayView==1", " build map fragment");
//				break;}
				break;
			case 2:
//			Intent i = new Intent (getApplicationContext(), FavoriteFragment.class);
//			startActivity(i);
			fragment = new FavoriteFragment();
			 break;
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			// update selected item and close drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.d("StartActivity", "Error in creating a fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

//	  using  ActionBarDrawerToggle,  call it during onPostCreate() and onConfigurationChanged()...

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	// called from fragment SearchFragment
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			// Scan result is available by making a call to
			// data.getStringExtra(ZBarConstants.SCAN_RESULT)
			// Type of the scan result is available by making a call to
			// data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)
			Toast.makeText(
					this,
					"Scan Result = "
							+ data.getStringExtra(ZBarConstants.SCAN_RESULT),
					Toast.LENGTH_SHORT).show();
			String returnValue = data.getStringExtra(ZBarConstants.SCAN_RESULT);

			Intent i = new Intent(getApplicationContext(),
					SearchResultActivity.class);
			i.putExtra("searchfor", returnValue);

			startActivity(i);

			// The value of type indicates one of the symbols listed in Advanced
			// Options below.
		} else if (resultCode == RESULT_CANCELED) {
			Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT)
					.show();
		}

	}
}
