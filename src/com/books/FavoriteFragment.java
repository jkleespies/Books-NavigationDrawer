package com.books;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import com.books.adapter.ExtendedSimpleAdapter;
import com.books.adapter.DatenbankManager;
import com.books.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FavoriteFragment extends ListFragment {
	// initialize variables
	private SQLiteDatabase mDatenbank;
	private DatenbankManager mHelper;

	private ArrayList<HashMap<String, Object>> FavoriteList;
	private static final String TAG_ID = "_id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_AUTHOR = "author";
	private static final String TAG_ISBN = "isbn";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_IMAGE = "image";
	private static final String TAG_IMAGE_NAME = "image_name";
	// private String imageName;
	private File path;
	private File file;
	private Bitmap bitmap;

	public FavoriteFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_favorite, container,
				false);
		return rootView;

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mHelper = new DatenbankManager(getActivity());
		mDatenbank = mHelper.getReadableDatabase();

		getBooks();

		// if list:empty
		// button for start SearchActivity
		ImageButton Suche = (ImageButton) getView().findViewById(
				R.id.StartActivity_Suche);
		Suche.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragment fragment = new SearchFragment();
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
				String[] navMenuTitles;
				navMenuTitles = getResources().getStringArray(
						R.array.navigation_drawer_array);
			}
		});

		// button for start MapActivity
		ImageButton Buecherei = (ImageButton) getView().findViewById(
				R.id.StartActivity_Buecherei);
		Buecherei.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragment fragment = new MapLocationFragment();
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
				String[] navMenuTitles;
				navMenuTitles = getResources().getStringArray(
						R.array.navigation_drawer_array);

			}
		});

	}

	// put content to FavoriteList
	// show ListView with content
	public void getBooks() {
		// initialize FavoriteList
		FavoriteList = new ArrayList<HashMap<String, Object>>();
		// cursor for handling DB
		Cursor bookCursor = mDatenbank.query("book", new String[] { "_id",
				"title", "author", "isbn", "description", "image" }, null,
				null, null, null, null);
		// initialize Hashmap for book item s
		HashMap<String, Object> item = new HashMap<String, Object>();
		bookCursor.moveToFirst();

		for (int i = 0; i < bookCursor.getCount(); i++) {
			item = new HashMap<String, Object>();
			String title = bookCursor.getString(1);
			String author = bookCursor.getString(2);
			String isbn = bookCursor.getString(3);
			// String description = bookCursor.getString(4);
			String imageName = bookCursor.getString(5);

			// open file "booksImageDir"
			ContextWrapper cw = new ContextWrapper(getActivity());
			path = cw.getDir("booksImageDir", Context.MODE_PRIVATE);
			file = new File(path, imageName);

			// get bitmap image from file
			try {
				bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

				item.put(TAG_IMAGE, bitmap);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			item.put(TAG_TITLE, title);
			item.put(TAG_AUTHOR, author);
			item.put(TAG_ISBN, isbn);
			// item.put(TAG_DESCRIPTION, description);

			FavoriteList.add(item);

			bookCursor.moveToNext();
		}

		ListAdapter adapter = new ExtendedSimpleAdapter(

				getActivity(), FavoriteList, R.layout.listitem, new String[] {
						TAG_TITLE, TAG_AUTHOR, TAG_ISBN, TAG_IMAGE },
				new int[] { R.id.title, R.id.author, R.id.isbn, R.id.image });

		setListAdapter(adapter);

	}

	// open DB
	public void onResume() {
		super.onResume();
		mDatenbank = mHelper.getReadableDatabase();
		getBooks();
	}

	// close DB
	public void onDestroy() {
		super.onDestroy();
		mDatenbank.close();
	}

	// handle ListView click
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// get values from ListView
		String titel = ((TextView) v.findViewById(R.id.title)).getText()
				.toString();
		String autor = ((TextView) v.findViewById(R.id.author)).getText()
				.toString();
		String isbn = ((TextView) v.findViewById(R.id.isbn)).getText()
				.toString();
		ImageView image = ((ImageView) v.findViewById(R.id.image));

		// save image
		image.buildDrawingCache();
		// initialize Bitmap
		Bitmap imageBitmap = image.getDrawingCache();

		// Cursor for loading information from Database
		Cursor cursor = mDatenbank.query("book", new String[] { "_id", "title",
				"author", "isbn", "description", "image" }, null, null, null,
				null, null);
		if (cursor != null) {
			if (cursor.moveToPosition(position)) {

				// get id from DB
				int column = cursor.getColumnIndex("_id");
				Integer _id = cursor.getInt(column);

				// get description from DB
				column = cursor.getColumnIndex("description");
				String description = cursor.getString(column);

				// get image-Name from DB
				column = cursor.getColumnIndex("image");
				String imageName = cursor.getString(column);

				Intent startFavoriteDetail = new Intent(getActivity(),
						FavoriteDetailActivity.class);
				// insert values into Intent
				startFavoriteDetail.putExtra(TAG_TITLE, titel);
				startFavoriteDetail.putExtra(TAG_AUTHOR, autor);
				startFavoriteDetail.putExtra(TAG_ISBN, isbn);
				startFavoriteDetail.putExtra(TAG_ID, _id);
				startFavoriteDetail.putExtra(TAG_DESCRIPTION, description);
				startFavoriteDetail.putExtra(TAG_IMAGE, imageBitmap);
				startFavoriteDetail.putExtra(TAG_IMAGE_NAME, imageName);
				// start ActivtyFavoriteDetail
				startActivity(startFavoriteDetail);

				Log.d("onListItemClick aufgerufen",
						"Item wurde ausgwählt, es sollte FavoriteDetailActivity starten");

			}
		}

	}

}
