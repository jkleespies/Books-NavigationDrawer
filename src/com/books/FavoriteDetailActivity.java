package com.books;

import java.io.File;

import com.books.adapter.DatenbankManager;
import com.books.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FavoriteDetailActivity extends Activity {

	// initialize varialbe
	private TextView title;
	private TextView author;
	private TextView isbn;
	private TextView description;
	private ImageView image;

	private SQLiteDatabase mDatenbank;
	private DatenbankManager mHelper;

	private String readTitle;
	private String readAuthor;
	private String readISBN;
	private Integer readID;
	private String _id;
	private String readDescription;
	private Bitmap readImageBitmap;
	private String readImageName;
	private File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite_detail);
		mHelper = new DatenbankManager(this);

		// get values from FavoriteActivity
		Intent startFavoriteDetail = getIntent();
		readTitle = startFavoriteDetail.getExtras().getString("title");
		readAuthor = startFavoriteDetail.getExtras().getString("author");
		readISBN = startFavoriteDetail.getExtras().getString("isbn");
		// id is need if data will be delete
		readID = startFavoriteDetail.getExtras().getInt("_id");
		readDescription = startFavoriteDetail.getExtras().getString(
				"description");
		readImageBitmap = startFavoriteDetail.getParcelableExtra("image");
		readImageName = startFavoriteDetail.getExtras().getString("image_name");

		// initialize GUI elements
		title = (TextView) findViewById(R.id.FavoriteDetailActivity_title);
		author = (TextView) findViewById(R.id.FavoriteDetailActivity_author);
		isbn = (TextView) findViewById(R.id.FavoriteDetailActivity_isbn);
		description = (TextView) findViewById(R.id.FavoriteDetailActivity_description);
		image = (ImageView) findViewById(R.id.FavoriteDetailActivity_image);

		// set GUI elements
		title.setText(readTitle);
		author.setText(readAuthor);
		isbn.setText(readISBN);
		description.setText(readDescription);
		// description element should be scrollable
		description.setMovementMethod(new ScrollingMovementMethod());

		image.setImageBitmap(readImageBitmap);

		// get Filesystem "booksImageDir"
		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		File path = cw.getDir("booksImageDir", Context.MODE_PRIVATE);
		file = new File(path, readImageName);

		// delete button for delete book item form DB
		ImageButton delete = (ImageButton) findViewById(R.id.FavoriteDetailActivity_delete);
		delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// delete book item
				_id = (Integer.toString(readID));
				String where = "_id = ?";
				String[] whereArgs = { _id };

				mDatenbank.delete("book", where, whereArgs);
				Toast.makeText(getApplicationContext(),
						"Eintrag aus Favoriten gelöscht", Toast.LENGTH_SHORT)
						.show();
				// delete image from file "booksImgDir"

				file.delete();
				// close activity
				finish();

			}
		});

		// start Activity Map
		ImageButton buecherei = (ImageButton) findViewById(R.id.FavoriteDetailActivity_library);
		buecherei.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
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
	}

	public void onResume() {
		super.onResume();
		mDatenbank = mHelper.getWritableDatabase();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.favorite_detail, menu);
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
