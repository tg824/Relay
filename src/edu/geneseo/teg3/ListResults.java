////////////////////////////////////////////////////////////////
/*
 * Tom George
 * ListResults.java
 * The list results of the activity
 */
////////////////////////////////////////////////////////////////

package edu.geneseo.teg3;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.view.*;
import android.view.View.OnKeyListener;

public class ListResults extends Activity {
	
	Event selectedEvent;
	ArrayList<String> URLs = new ArrayList<String>(), eventNames = new ArrayList<String>(),
	links = new ArrayList<String>();
	int totalResults;
	 String page="";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listresults);
        //Get references to UI widgets
        ListView myListView = (ListView)findViewById(R.id.my_list_view);
      
        
        
        
        Bundle extras = getIntent().getExtras();
        Resources res = getResources();
       
        if(extras != null){
        	page = extras.getString("page");
        	URLs.add(extras.getString("URL"));
        }
        else{
        	AlertDialog err = new AlertDialog.Builder(this).create();
        	err.setMessage(res.getString(R.string.page_not_found));
        	err.show();
        	//kill
        	int pid = android.os.Process.myPid();
        	android.os.Process.killProcess(pid);
        }
        //Creates parser for getting the events
        HomemadeParser hmp = new HomemadeParser(URLs.get(0));
        hmp.initialize();
        // Create the array list of items
        final ArrayList<Event> results = hmp.getResults();
        totalResults = hmp.getTotalResults();
        
        //If no results, display a toast and go back
  
        if(totalResults == 0)
        {
        	startActivity(new Intent(ListResults.this, HomeScreen.class));

        	Toast.makeText(getApplicationContext(), "No results found. Please try again or widen your search",
        			Toast.LENGTH_LONG)
        	        .show();
        }
        //Add results array
        for(int i = 0; i < results.size(); i++){
        	eventNames.add(results.get(i).getName());
        	links.add(results.get(i).getLink());
        }
        // Create the array adapter to bind the array to the listview
        final ArrayAdapter<String> aa;
        aa = new ArrayAdapter<String>(this,
        							  android.R.layout.simple_list_item_1,
        							  eventNames);
        
        // Bind the array adapter to the listview
        myListView.setAdapter(aa);
        
        // Add more... option
        if(totalResults > 10)
        {
        	aa.add("More...");
        }
        aa.notifyDataSetChanged();
        myListView.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView _av, View _v, int _index, long arg3)
        	{
        		//Get more events
        		if(aa.getItem(_index).equals("More..."))
        		{
        			
        			URLs.add(URLs.get(0)+"&StartIndex="+aa.getCount());
        			HomemadeParser parser = new HomemadeParser(URLs.get(URLs.size()-1));
        			parser.initialize();
        			
        			aa.remove(aa.getItem(aa.getCount()-1));
        			ArrayList<Event> moreEvents = parser.getResults();
        			for(int i = 0; i < moreEvents.size(); i++){
        	        	eventNames.add(moreEvents.get(i).getName());
        	        	links.add(moreEvents.get(i).getLink());
        	        	results.add(moreEvents.get(i));
        	        	totalResults--;
        	        	
        	        }
        			if(totalResults > 10)
        				
        				aa.add("More...");
        			aa.notifyDataSetChanged();
        			return;
        		
        		}
        		else
        		{
        		//Else go to the event details page
        		selectedEvent = results.get(_index);
        		Intent intent = new Intent(ListResults.this, EventDetails.class);
        		intent.putExtra("eventName", selectedEvent.getName());
        		intent.putExtra("eventLink", selectedEvent.getLink());
        		intent.putExtra("eventAddress", selectedEvent.getAddress());
        		startActivity(intent);
        		}
        	}
        });
        
      }
    @Override
    protected void onStop()
    {
    	super.onStop();
    }
    
    @Override
    protected void onStart()
    {
    	super.onStart();
    }
    
 
    
}