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

	private DrawerLayout DrawerLayout;
	private ListView DrawerList;
	private ActionBarDrawerToggle DrawerToggle;

	// nav drawer title
	private CharSequence DrawerTitle;

	// used to store app title
	private CharSequence Title;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	// private ArrayList<NavDrawerItem> navDrawerItems;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Title = DrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(
				R.array.navigation_drawer_array);

		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(
				R.array.navigation_drawer_icons);

		DrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		DrawerList = (ListView) findViewById(R.id.list_slidermenu);

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

		DrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		// setting Change List Fragment
		DrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		DrawerToggle = new ActionBarDrawerToggle(this, DrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(Title);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(DrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		DrawerLayout.setDrawerListener(DrawerToggle);
		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
			Log.d("if onCreate", "displayView");
		}

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
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		// Drawer
		if (DrawerToggle.onOptionsItemSelected(item)) {

			return true;
		}
			return super.onOptionsItemSelected(item);
	}

	// // Called when invalidateOptionsMenu() is used
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items

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
			fragment = new MapLocationFragment();
			break;
		case 2:
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
			DrawerList.setItemChecked(position, true);
			DrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			DrawerLayout.closeDrawer(DrawerList);
		} else {
			// error in creating fragment
			Log.d("StartActivity", "Error in creating a fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		Title = title;
		getActionBar().setTitle(Title);
	}

	// using ActionBarDrawerToggle, call it during onPostCreate() and
	// onConfigurationChanged()...

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		DrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		DrawerToggle.onConfigurationChanged(newConfig);
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
