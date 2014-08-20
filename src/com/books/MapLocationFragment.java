package com.books;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.books.R;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapLocationFragment extends Fragment implements LocationListener {
	// implements LocationListener {

	// instance variables
	 private int userIcon, shopIcon;
	// private GoogleMap theMap;
	 private LocationManager locMan;
	 private Marker userMarker;
	 private Marker[] placeMarkers;
	 private final int MAX_PLACES = 20;// number of places returned by API
	 private MarkerOptions[] places;
	// protected MapFragment mapFragment;
	 private String placesSearchStr;

	// public View rootview;

	
	
	private MapView mapView;
	private GoogleMap theMap;
	private Bundle bundle;

	public MapLocationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_map, container,
				false);
		MapsInitializer.initialize(getActivity());
		mapView = (MapView) rootView.findViewById(R.id.the_map);
		mapView.onCreate(bundle);
		userIcon = R.drawable.location;
		shopIcon = R.drawable.location_place;
		setUpMapIfNeeded(rootView);
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		bundle = savedInstanceState;
	}



	// rootview = inflater.inflate(R.layout.fragment_map, container, false);
	//
	// Log.d("onCreateView", "set lacout");
	// return rootview;

	private void setUpMapIfNeeded(View rootView) {
		if (theMap == null) {
			theMap = ((MapView) rootView.findViewById(R.id.the_map)).getMap();
			if (theMap != null) {
				setUpMap();
			}
		}

	}

	private void setUpMap() {
		theMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		// create marker array
		placeMarkers = new Marker[MAX_PLACES];
		// update location
		updatePlaces();
		
		
//		
//		theMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title(
//				"Marker"));
//		
//		placeMarkers = new Marker[MAX_PLACES];
//		// update location
//		CameraUpdate cameraPosition = CameraUpdateFactory
//				.newLatLng(new LatLng(40.777777, -73.99999));
//		CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
//
//		theMap.moveCamera(cameraPosition);
//		theMap.animateCamera(zoom);
//		updatePlaces();
		
		
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mapView.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mapView.onPause();
	}

	@Override
	public void onDestroy() {
		mapView.onDestroy();
		super.onDestroy();
	}
	
	public void updatePlaces() {
		Log.d("updatePlaces()", "GetPlaces(), locMan");
		// get location manager
		locMan = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		// get last location
		Location lastLoc = locMan
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		double lat = lastLoc.getLatitude();
		double lng = lastLoc.getLongitude();
		// create LatLng
		LatLng lastLatLng = new LatLng(lat, lng);

		// remove existing marker
		if (userMarker != null)
			userMarker.remove();
		// create and set the marker properties
		userMarker = this.theMap.addMarker(new MarkerOptions()
				.position(lastLatLng).title("You are here")
				.icon(BitmapDescriptorFactory.fromResource(userIcon)));

		// set camera position
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(lastLatLng).zoom(16).bearing(0).tilt(45).build();
		this.theMap.moveCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));

		// define search parameters for places -> type
		String types = "book_store|library";
		try {
			types = URLEncoder.encode(types, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		// create API search string
		placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/"
				+ "json?location="
				+ lat
				+ ","
				+ lng
				+ "&radius=1000&sensor=true"
				+ "&types="
				+ types
				+ "&key=AIzaSyBt4UwId11OPjLA5jjnNuTAvJ-d-1FbAFM";

		// run query
		new GetPlaces().execute();

		// set Update settings (choosen Provider, min time interval between
		// updates, min meters before update, the listener)
		locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000,
				100, this);
	}

	@Override
	public void onLocationChanged(Location location) {
//		updatePlaces();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	// get the places from Google API as JSON and parse
			private class GetPlaces extends AsyncTask<String, Void, String> {
				
				@Override
				protected String doInBackground(String... args0) {
					Log.d("GetPlaces", "doInBackground");

					String url = placesSearchStr.replaceAll("[ ]", "%20");
					Adapter ad = new Adapter();

					// URL request and response from Adapter
					String jsonStr = ad.makeServiceCall(url, Adapter.GET);

					return jsonStr;

				}

				// process data retrieved from doInBackground
				protected void onPostExecute(String result) {
					// parse place data returned from Google Places API
					// remove existing markers
					if (placeMarkers != null) {
						for (int pm = 0; pm < placeMarkers.length; pm++) {
							if (placeMarkers[pm] != null)
								placeMarkers[pm].remove();
						}
					}
					// parse JSON
					try {

						// create JSONObject, pass sting returned from doInBackground
						JSONObject resultObject = new JSONObject(result);
						// get "results" array
						JSONArray placesArray = resultObject.getJSONArray("results");
						// marker options for each place returned
						places = new MarkerOptions[placesArray.length()];
						// loop through places
						for (int p = 0; p < placesArray.length(); p++) {
							// parse each place
							// if any values are missing we won't show the marker
							boolean missingValue = false;
							LatLng placeLL = null;
							String placeName = "";
							String vicinity = "";
							int currIcon = shopIcon;
							try {
								// attempt to retrieve place data values
								missingValue = false;
								// get place at this index
								JSONObject placeObject = placesArray.getJSONObject(p);
								// get location section
								JSONObject loc = placeObject.getJSONObject("geometry")
										.getJSONObject("location");
								// read lat lng
								placeLL = new LatLng(Double.valueOf(loc
										.getString("lat")), Double.valueOf(loc
										.getString("lng")));
								// get types
								JSONArray types = placeObject.getJSONArray("types");
								// loop through types
								for (int t = 0; t < types.length(); t++) {
									// what type is it
									String thisType = types.get(t).toString();
									// check for particular types - set icons
									if (thisType.contains("book_store")) {
										currIcon = shopIcon;
										break;
									} else if (thisType.contains("library")) {
										currIcon = shopIcon;
										break;
									}

								}
								// vicinity
								vicinity = placeObject.getString("vicinity");
								// name
								placeName = placeObject.getString("name");
							} catch (JSONException jse) {
								Log.v("PLACES", "missing value");
								missingValue = true;
								jse.printStackTrace();
							}
							// if values missing we don't display
							if (missingValue)
								places[p] = null;
							else
								places[p] = new MarkerOptions()
										.position(placeLL)
										.title(placeName)
										.icon(BitmapDescriptorFactory
												.fromResource(currIcon))
										.snippet(vicinity);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (places != null && placeMarkers != null) {
						for (int p = 0; p < places.length && p < placeMarkers.length; p++) {
							// will be null if a value was missing
							if (places[p] != null)
								placeMarkers[p] = theMap.addMarker(places[p]);
						}
					}

				}
			}
	}

