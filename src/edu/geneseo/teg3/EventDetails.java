////////////////////////////////////////////////////////////////
/*
 * Tom George
 * EventDetails.java
 * Detailed page of the event
 */
////////////////////////////////////////////////////////////////

package edu.geneseo.teg3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.location.Address;
import android.location.Location;
import android.location.Geocoder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class EventDetails extends MapActivity{
	Geocoder geocoder;
	Event theEvent;
	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_details);
		Bundle extras = getIntent().getExtras();
		
		geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
		
		String name = extras.getString("eventName");
		String link = extras.getString("eventLink");
		
		try{
			//Parse the event webpage for detailed information about the event
			Document eventInfo = Jsoup.connect(link).get();
			Elements info = eventInfo.select("div#pnlResults");
			String startDate = info.select("span#lblStartDate").text();
			String endDate = info.select("span#lblEndDate").text();
			String startTime = info.select("span#lblStartTime").text();
			String endTime = info.select("span#lblEndTime").text();
			String timeZone = info.select("span#lblTimeZone").text();
			String description = info.select("span#lblDesc").text();
			String locName = info.select("span#lblLocationName").text();
			String address = info.select("span#lblAddress").text();
			String handicapAccessible = info.select("span#lblHandicapAccessible").text();
			//Build the event
			theEvent = new Event(name, locName+"\n"+address, description, link, startDate,
					endDate, startTime, endTime, timeZone, handicapAccessible);
		}
		catch(MalformedURLException exception)
		{
			exception.printStackTrace();
		}
		catch(IOException exception)
		{
			exception.printStackTrace();
		}
		if(theEvent.getAddress().length() == 1)
		{
			//If it can't find information, it displays a dialog saying that it cannot
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			builder.setTitle("Uh Oh...")
					.setMessage("Some events do not have specific information posted on the "
					+"American Cancer Society. This is one such event.  Detailed information may not"+
					" show up and the map may not be visible.  It is recommended that you visit the event's"
					+ " website for further information.")
					.setCancelable(false)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id)
						{
							dialog.dismiss();
						}
					})
			.create()
			.show();
		}
		//Inflate view
		TextView myTextView = (TextView)findViewById(R.id.event_details_text_view);
		myTextView.setAutoLinkMask(0);
		myTextView.setText(new StringBuilder()
				.append(theEvent.toString()));
		TextView linkTextView = (TextView)findViewById(R.id.event_link);
		linkTextView.setText(new StringBuilder()
			.append("Website: "+ theEvent.getLink()));
		final Button mapButton = (Button)findViewById(R.id.show_map);
		mapButton.setOnClickListener(new OnClickListener(){
		//Go to map
			public void onClick(View v)
			{
				System.out.println(theEvent.getAddress());
				Intent intent = new Intent(EventDetails.this, EventMap.class);
				intent.putExtra("address", theEvent.getAddress());
				startActivity(intent);
			}
		
		});
		
		
	}

}
