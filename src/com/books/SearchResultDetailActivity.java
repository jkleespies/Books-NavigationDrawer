package com.books;

import java.io.File;
import java.io.FileOutputStream;

import com.books.adapter.*;
import com.books.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchResultDetailActivity extends Activity {

	// set variables
	private static final String TAG_AUTHORS = "authors";
	private static final String TAG_TITLE = "title";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_IDENTIFIER = "identifiers";
	private static final String TAG_THUMBNAIL = "thumbnail";
	private ImageView add;
	private ImageButton nextLibrary;
	private SQLiteDatabase mDatenbank;
	private DatenbankManager mHelper;

	private String authors = "";
	private String title = "";
	private String description = "";
	private String identifiers = "";
	private String imageName;
	private Bitmap imageBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result_detail);
		mHelper = new DatenbankManager(this);

		// get values from SearchActivity
		Intent in = getIntent();
		authors = in.getStringExtra(TAG_AUTHORS);
		title = in.getStringExtra(TAG_TITLE);
		description = in.getStringExtra(TAG_DESCRIPTION);
		identifiers = in.getStringExtra(TAG_IDENTIFIER);
		imageBitmap = in.getParcelableExtra(TAG_THUMBNAIL);

		// initialize GUI elements
		TextView lblAuthor = (TextView) findViewById(R.id.SearchResultDetailActivity_author);
		TextView lblTitle = (TextView) findViewById(R.id.SearchResultDetailActivity_title);
		TextView lblDescription = (TextView) findViewById(R.id.SearchResultDetailActivity_description);
		lblDescription.setMovementMethod(new ScrollingMovementMethod());
		TextView lblIsbn = (TextView) findViewById(R.id.SearchResultDetailActivity_isbn);
		ImageView lblImage = (ImageView) findViewById(R.id.SearchResultDetailActivity_picture);

		// set content GUI elements
		lblAuthor.setText(authors);
		lblTitle.setText(title);
		lblDescription.setText(description);
		lblIsbn.setText(identifiers);
		lblImage.setImageBitmap(imageBitmap);

		// open MapActivity
		nextLibrary = (ImageButton) findViewById(R.id.SearchResultDetailActivity_library);
		nextLibrary.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//open the activity MainActivity and use Fragment MapFragment
				setContentView(R.layout.activity_main);
				Fragment fragment = new MapLocationFragment();
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
				String[] navMenuTitles;
				navMenuTitles = getResources().getStringArray(
						R.array.navigation_drawer_array);
				setTitle(navMenuTitles[1]);
			}
		});

		// Button for adding book item in DB
		add = (ImageButton) findViewById(R.id.SearchResultDetailActivity_add);
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("hinzufügen", "werte werden in db eingefügt");

				// open file
				ContextWrapper cw = new ContextWrapper(getApplicationContext());
				// file name = "booksImageDir"
				File directory = cw.getDir("booksImageDir",
						Context.MODE_PRIVATE);

				// open Cursor for DB
				Cursor bookCursor = mDatenbank.query("book", new String[] {
						"_id", "title", "author", "isbn", "description",
						"image" }, null, null, null, null, null);
				// request if DB == null or not null
				// create image name
				// if book images exits create image name "image_ + _id + .jpg"
				if (bookCursor != null && bookCursor.moveToFirst()) {
					Log.d("Curor ungleich null", "Button add");
					bookCursor.moveToLast();
					int column = bookCursor.getColumnIndex("_id");
					Integer _id = bookCursor.getInt(column);
					_id++;
					imageName = "image_" + _id + ".jpg";
				} // if book images not exist imagename "image_1.jpg"
				else {
					Log.d("Cursor ist null", "Button add");
					imageName = "image_1.jpg";
				}

				// save image in directory "booksImageDir"
				File mypath = new File(directory, imageName);
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(mypath);
					imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				Log.d("IMAGE_NAME", imageName);

				ContentValues werte = new ContentValues();
				// values for DB
				werte.put("title", title);
				werte.put("author", authors);
				werte.put("isbn", identifiers);
				werte.put("description", description);
				werte.put("image", imageName);

				// insert values in DB
				mDatenbank.insert("book", null, werte);

				Toast.makeText(getApplicationContext(), title + " gespeichert",
						Toast.LENGTH_SHORT).show();
				finish();
			}

		});
	}

	protected void onResume() {
		super.onResume();
		mDatenbank = mHelper.getWritableDatabase();
	}

	// close DB
	protected void onDestroy() {
		super.onDestroy();
		mDatenbank.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_result_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
