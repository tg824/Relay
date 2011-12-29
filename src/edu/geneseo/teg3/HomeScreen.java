////////////////////////////////////////////////////////////////
/*
 * Tom George
 * HomeScreen.java
 * Home Screen of the Relay Application
 */
////////////////////////////////////////////////////////////////

package edu.geneseo.teg3;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;


public class HomeScreen extends Activity{
	
	//Class-level variables
	static final int DATE_DIALOG_ID=0;
	private String returnedHtml;
	private Button mPickStartDate;
	private int mYear, mMonth, mDay;
	private TextView mStartDisplay;
	private Spinner spinner;
	private LocationManager manager;
	private Location loc;
	private Geocoder gc;
	private Criteria criteria = new Criteria();

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Register the listener with the Location Manager to receive location updates
		//Set accuracy of criteria
		manager= (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, myLocationListener);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = manager.getBestProvider(criteria, true);
		loc = manager.getLastKnownLocation(provider);
		gc = new Geocoder(this, Locale.getDefault());
		spinner = (Spinner)findViewById(R.id.spinner);
		//Create array adapter
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.radius_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
		
        
        mPickStartDate = (Button)findViewById(R.id.pick_start);
        mStartDisplay = (TextView)findViewById(R.id.start_date_text_view);
        //Set up Date Pickers
        mPickStartDate.setOnClickListener(new OnClickListener(){
        	public void onClick(View v)
        	{
        		showDialog(DATE_DIALOG_ID);
        	}
        });
        //Set up radio button
        RadioButton rb = (RadioButton)findViewById(R.id.radioButton1);
        rb.setOnClickListener(radioListener);
        
        //Calendar for textView
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
       
        updateDisplay();
        
        //Set up go button
		final Button goButton = (Button)findViewById(R.id.go_button);
		goButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v)
			{
				EditText MyEditText = (EditText)findViewById(R.id.zip_edit_text);
				String ZIP = MyEditText.getText().toString();
			
				if(ZIP.length() != 5)
				{
					Toast.makeText(getApplicationContext(), "Please enter a valid ZIP code", Toast.LENGTH_LONG)
					.show();
					MyEditText.setText("");
					return;
				}
				sendRequest();
			}
		});
	}
	//Updates display based on the date picker
	private void updateDisplay()
	{
		mStartDisplay.setText(
				new StringBuilder()
				.append(mMonth+1).append("-")
				.append(mDay).append("-")
				.append(mYear).append(" ")); 
	}
	private DatePickerDialog.OnDateSetListener mDateSetListener = 
		new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case DATE_DIALOG_ID:
	        return new DatePickerDialog(this,
	                    mDateSetListener,
	                    mYear, mMonth, mDay);
	    }
	    return null;
	}
	//Creates HttpGet and InputStream based on Entered ZIP Code
	public void sendRequest()
	{
			EditText MyEditText = (EditText)findViewById(R.id.zip_edit_text);
			String ZIP = MyEditText.getText().toString();
			TextView startDateTextView = (TextView)findViewById(R.id.start_date_text_view);
			String startDate = startDateTextView.getText().toString();
			String radius = spinner.getSelectedItem().toString();
			
			
			try{
				HttpClient defaultClient = new DefaultHttpClient();				
				//Perform GET request and get InputStream
				Calendar dateNow = Calendar.getInstance();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				String now = formatter.format(dateNow.getTime());
				int year = Integer.parseInt(now.substring(0, 4));
				int month = Integer.parseInt(now.substring(5, 7));
				int day = Integer.parseInt(now.substring(8,10));
				
				//Build URL
				String theURL = "http://www.cancer.org/Involved/Participate/app/event-search.aspx?" +
						"zip="+ZIP+"&city=&state=&local-radius="+radius+"&textsrch=&startdate="+month+"%2F" +
								day+"%2F"+year+"&enddate=&all=1";
	
				HttpGet httpget = new HttpGet(theURL);
				HttpResponse response = defaultClient.execute(httpget);
				HttpEntity result = response.getEntity();
				//Get the input stream
				InputStream stream = result.getContent();
				returnedHtml = new Scanner(stream).useDelimiter("\\A").next();
	
				Intent intent = new Intent(HomeScreen.this, ListResults.class);
				intent.putExtra("page", returnedHtml);
				intent.putExtra("URL", theURL);
				startActivity(intent);
	
				
			}catch (ClientProtocolException e){
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		public String getReturnedHtml()
		{
			return returnedHtml;
		}
	LocationListener myLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		    	
	    }
	   public void onStatusChanged(String provider, int status, Bundle extras) {}

	    public void onProviderEnabled(String provider) {}

       public void onProviderDisabled(String provider) {}
		  
	};

		
		
		private OnClickListener radioListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(loc != null)
				{
					//Get postal code based on current latitude, longitude
					double lat = loc.getLatitude();
					double lon = loc.getLongitude();
				
					List<Address> addresses = null;
					try{
						addresses = gc.getFromLocation(lat, lon, 5);
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
					System.out.println(addresses.get(0).getPostalCode());
					EditText myEditText = (EditText)findViewById(R.id.zip_edit_text);
					myEditText.setText(addresses.get(0).getPostalCode());
				}
				else
					Toast.makeText(getApplicationContext(),"Address not found", Toast.LENGTH_LONG)
					.show();
			}
			
			};
}
