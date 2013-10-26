package com.sssta.ganmaqu;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link ProfileFragment#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class ProfileFragment extends android.support.v4.app.Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	private String ipString;
	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;
	private int count_city;
	private OnFragmentInteractionListener mListener;
	private TextView cityTextView;
	private SharedPreferences userInfo;
	private Connect connect;
	private String city;
	private LocationManager locationManager;
	public String getARG_PARAM1()
	{
		return ARG_PARAM1;
	}
	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment ProfileFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static ProfileFragment newInstance(String param1, String param2) {
		ProfileFragment fragment = new ProfileFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public ProfileFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_profile, container,
				false);
		cityTextView = (TextView) view.findViewById(R.id.textView_currentCity);
		userInfo = view.getContext().getSharedPreferences("userInfo", 0);

		ipString = view.getContext().getResources().getString(R.string.ip);
		city = userInfo.getString("city", "西安市");
		count_city = userInfo.getInt("count_city", 0);
		connect = new Connect(ipString);
		cityTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
//		new AddressRequestTask().execute(String.valueOf(MainActivity.getLat()),
//				String.valueOf(MainActivity.getLng()));
//		Log.i("start in fragment", "start");
//		Log.i("loaction_fragment",
//				String.valueOf(MainActivity.getLat()) + " "
//						+ String.valueOf(MainActivity.getLng()));
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

	public class AddressRequestTask extends AsyncTask<String, integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return connect.GetCurrentAddress(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(String result) {

			if (result == null) {
				cityTextView.setText("地点未知");
			} else {
				try {
					Log.i("ADDRESS REQUEST", result);
					JSONObject jsonObject = new JSONObject(result);
					JSONObject jsonResult = new JSONObject(
							jsonObject.getString("result"));
					// Toast.makeText(getApplicationContext(),
					// jsonResult.getString("formatted_address"),
					// Toast.LENGTH_LONG).show();
//					cityTextView.setText(jsonResult
//							.getString("city"));
					if (count_city == 0) {

						city = null;
						String addressComponent = jsonResult
								.getString("addressComponent");
						JSONObject address = new JSONObject(addressComponent);
						city = new String(address.getString("city"));
						Log.i("city", city);
						cityTextView.setText(city);
						userInfo.edit().putString("city", city).commit();
						count_city++;
						userInfo.edit().putInt("count_city", count_city)
								.commit();

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
}
