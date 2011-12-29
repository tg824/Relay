package edu.geneseo.teg3;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

public class EventMap extends MapActivity{
	
	Geocoder geocoder;
	
	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_map);
		//Set up geocoder
		geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
		
		Bundle extras = getIntent().getExtras();
		String address = extras.getString("address");
		
		try{
			//Get geopoint, addresses based on the address
			double selectedLat=0.0, selectedLon=0.0;
			List<Address> addresses = geocoder.getFromLocationName(address, 5);
			MapView map = (MapView)findViewById(R.id.my_map_view);
			MapController mapController = map.getController();
			
			map.setSatellite(true);
			map.setStreetView(false);
			map.displayZoomControls(false);
			
			mapController.setZoom(17);
			//Get the address and animate to the spot on the map
			for(int i = 0; i < addresses.size(); i++)
			{
				if(addresses.get(i).hasLatitude() && addresses.get(i).hasLongitude() && addresses.get(i)!=null)
				{
					selectedLat = addresses.get(i).getLatitude();
					selectedLon = addresses.get(i).getLongitude();
				}
			}
			
			GeoPoint point = new GeoPoint((int)(selectedLat*1E6), (int)(selectedLon*1E6));
			mapController.animateTo(point);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}
