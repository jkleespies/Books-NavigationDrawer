package com.books;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.books.adapter.Adapter;
import com.books.adapter.ExtendedSimpleAdapter;
import com.books.R;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SearchResultActivity extends ListActivity {

	private ProgressDialog pDialog;

	// set variables for search
	public String url;
	public String search_phrase;

	// set params
	private static final String TAG_ITEMS = "items";
	private static final String TAG_VOLUMEINFO = "volumeInfo";
	private static final String TAG_TITLE = "title";
	private static final String TAG_AUTHORS = "authors";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_INDUSTRYIDENTIFIER = "industryIdentifiers";
	private static final String TAG_IDENTIFIER = "identifiers";
	private static final String TAG_IMAGELINKS = "imageLinks";
	private static final String TAG_THUMBNAIL = "thumbnail";
	public Bitmap bm;

	// set JSON Array
	JSONArray items = null;

	// Hashmap forListView
	ArrayList<HashMap<String, Object>> bookList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);

		// getSearchPhrase from SearchActivity
		Intent i = getIntent();
		String searchPhrase = i.getExtras().getString("searchfor");

		// if SearchPhrase empty
		if (searchPhrase.equals("")) {
			TextView tv = (TextView) findViewById(R.id.SearchResultActivity_Suche);
			tv.setText(getString(R.string.SearchResultActivity_emptyValue));
		} else {
			TextView tv = (TextView) findViewById(R.id.SearchResultActivity_Suche);
			// underline Searchphrase
			SpannableString content = new SpannableString(searchPhrase);
			content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
			tv.setText(content);

			// fill Hashmap
			bookList = new ArrayList<HashMap<String, Object>>();
			// initialize ListView
			ListView lv = getListView();

			// create SearchString, replace [] by whitespace (%20)
			String searchurl = "https://www.googleapis.com/books/v1/volumes?q="
					+ searchPhrase
					+ "&key=AIzaSyDjL9mTvbGiaHH8BxUDflfpOUJG6Cu2uVE&fields=kind,totalItems,items(volumeInfo(title,authors,description,imageLinks,industryIdentifiers))&maxResults=40";

			url = searchurl.replaceAll("[ ]", "%20");

			// set Click Listerner on ListViewItem -> start next Activity
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// get values from ListView
					String authors = ((TextView) view.findViewById(R.id.author))
							.getText().toString();
					String title = ((TextView) view.findViewById(R.id.title))
							.getText().toString();
					String description = ((TextView) view
							.findViewById(R.id.description)).getText()
							.toString();
					String identifier = ((TextView) view
							.findViewById(R.id.isbn)).getText().toString();
					ImageView image = (ImageView) view.findViewById(R.id.image);
					// save image
					image.buildDrawingCache();
					// initialize Bitmap
					Bitmap imageBitmap = image.getDrawingCache();

					// Starten der Detailansicht
					Intent in = new Intent(getApplicationContext(),
							SearchResultDetailActivity.class);
					in.putExtra(TAG_AUTHORS, authors);
					in.putExtra(TAG_TITLE, title);
					in.putExtra(TAG_DESCRIPTION, description);
					in.putExtra(TAG_IDENTIFIER, identifier);
					in.putExtra(TAG_THUMBNAIL, imageBitmap);
					startActivity(in);
				}
			});

			// call Async task to get JSON
			new GetBooks().execute();
		}
	}

	/**
	 * AsyncTask Class GET HTTP Call for JSON request
	 **/
	private class GetBooks extends AsyncTask<Void, Void, Void> {

		Integer count = 0;
		
		// async method before execute
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			// Loading Screen
			pDialog = new ProgressDialog(SearchResultActivity.this);
			pDialog.setTitle(R.string.wait);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		// async method while execute
		@Override
		protected Void doInBackground(Void... arg0) {
			Adapter ad = new Adapter();
			
			
			// URL request and response from Adapter
			String jsonStr = ad.makeServiceCall(url, Adapter.GET);
			
			
			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					// read JSON Array
					items = jsonObj.getJSONArray(TAG_ITEMS);
//					items = jsonObj.getJSONArray(TAG_VOLUMEINFO);
					
					
					Log.d("Async Task", "" +  jsonObj);
					// get book items from JSONObject
					for (int i = 0; i < 39; i++) {
						JSONObject it = items.getJSONObject(i);

						// volumeInfo conatins content from i item
						JSONObject volumeInfo = it
								.getJSONObject(TAG_VOLUMEINFO);
						// get author from volumeInfo
						String author = volumeInfo.getString("authors")
								.replaceAll("[^a-zA-Z, ]", "");
						String authors = author.replace(",", ", ");
						// get title from volumeInfo
						String title = volumeInfo.getString(TAG_TITLE);
						// get description from volumeInfo
						String description = "";
						if (volumeInfo.has(TAG_DESCRIPTION) == true) {
							description = volumeInfo.getString(TAG_DESCRIPTION);
						} else // if no description available
						{
							description = getString(R.string.no_description_available);
						}

						// identifiers contains different isbn types
						// get types from volumeInfo
						// get ISBN 13 or other from industryIdentifiers
						String identifier = "";
						JSONArray industryIdentifiers = volumeInfo
								.getJSONArray(TAG_INDUSTRYIDENTIFIER);

						for (int j = 0; j < industryIdentifiers.length(); j++) {
							JSONObject industry = industryIdentifiers
									.getJSONObject(j);

							if (industry.get("type").equals("ISBN_13")) {
								identifier = industry.getString("identifier");
							} else if (industry.get("type").equals("OTHER")) {
								identifier = industry.getString("identifier");
							}
						}

						// get imageURL from volumeInfo
						JSONObject imageLinks = volumeInfo
								.getJSONObject(TAG_IMAGELINKS);
						String thumbnailLink = imageLinks
								.getString(TAG_THUMBNAIL);

						// initialize Bitmap and bmOptions
						BitmapFactory.Options bmOptions;
						bmOptions = new BitmapFactory.Options();
						bmOptions.inSampleSize = 1;

						// load Bitmap from method loadBitmap
						bm = loadBitmap(thumbnailLink, bmOptions);

						// initialize hasmap for handling bookitems
						HashMap<String, Object> item = new HashMap<String, Object>();
						
						count ++; 
						// add bookitems to hashmap
						item.put(TAG_AUTHORS, authors);
						item.put(TAG_TITLE, title);
						item.put(TAG_DESCRIPTION, description);
						item.put(TAG_IDENTIFIER, identifier);
						item.put(TAG_THUMBNAIL, bm);

						// add book item to bookList
						bookList.add(item);
					} //endfor
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						R.string.ServiceNotAvailable, Toast.LENGTH_LONG).show();
				Log.e("ServiceHandler",
						"Es konnten keine Bücher abgerufen werden");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (pDialog.isShowing())
				pDialog.dismiss();

			/*** Updaten der JSON Daten in die ListView */
			Log.d("Async Task count", "" + count);
			ListAdapter adapter = new ExtendedSimpleAdapter(
					SearchResultActivity.this, bookList, R.layout.listitem,
					new String[] { TAG_AUTHORS, TAG_TITLE, TAG_DESCRIPTION,
							TAG_IDENTIFIER, TAG_THUMBNAIL }, new int[] {
							R.id.author, R.id.title, R.id.description,
							R.id.isbn, R.id.image });
			setListAdapter(adapter);
		}

		public Bitmap loadBitmap(String URL, BitmapFactory.Options options) {
			Bitmap bitmap = null;
			InputStream in = null;
			Adapter adapter = new Adapter();

			try {
				// open the URL with image
				in = adapter.OpenHttpConnection(URL);

				// URL request and response from Adapter
				// load BitmapImage in bitmap
				bitmap = BitmapFactory.decodeStream(in, null, options);

				// close Input Stream
				in.close();
			} catch (IOException e1) {
			}
			return bitmap;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_result, menu);
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
