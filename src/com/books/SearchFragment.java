package com.books;



import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import com.books.R;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SearchFragment extends Fragment {
	
	public SearchFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_search, container, false);
			return rootView;
	}
	
	
		@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	
		ImageButton	scannerButton =(ImageButton)getView().findViewById(R.id.searchActivity_scanner);
		scannerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(),
						ZBarScannerActivity.class);
				startActivityForResult(intent, 0);
			}
		});

		ImageButton searchButton = (ImageButton)getView().findViewById(R.id.searchActivity_search);
		searchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText search_keyword = (EditText)getView().findViewById(R.id.searchActivity_input_field);
				String search_phrase = search_keyword.getText().toString();
				if (search_phrase.compareTo("") == 0) {
					Toast toast = Toast.makeText(getActivity(),
							"wrong input", Toast.LENGTH_SHORT);
					toast.setGravity(
							Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
					toast.show();
				} else {
					Intent i = new Intent(getActivity(),
							SearchResultActivity.class);
					i.putExtra("searchfor", search_phrase);
					startActivity(i);
				}

			}
		});
	
		}
		
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == Activity.RESULT_OK) {
				// Scan result is available by making a call to
				// data.getStringExtra(ZBarConstants.SCAN_RESULT)
				// Type of the scan result is available by making a call to
				// data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)
				Toast.makeText(
						getActivity(),
						"Scan Result = "
								+ data.getStringExtra(ZBarConstants.SCAN_RESULT),
						Toast.LENGTH_SHORT).show();
				String returnValue = data.getStringExtra(ZBarConstants.SCAN_RESULT);

				Intent i = new Intent(getActivity(),
						SearchResultActivity.class);
				i.putExtra("searchfor", returnValue);

				startActivity(i);

				// The value of type indicates one of the symbols listed in Advanced
				// Options below.
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Log.e("onActivityResult SearchFragment", "RESULT_CANCELED" + resultCode);
			}

		}

}
