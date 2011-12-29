////////////////////////////////////////////////////////////////
/*
 * Tom George
 * HomemadeParser.java
 * Background class that handles all of the HTML parsing
 */
////////////////////////////////////////////////////////////////

package edu.geneseo.teg3;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class HomemadeParser {
	
	String page;
	Scanner scn;
	int totalResults;
	String returnedHtml="";
	Document doc;
	public HomemadeParser(String page)
	{
		this.page=page;
		
	}
	
	public void initialize()
	{
		try{	
			//Get number of events to send back to ListResults
			HttpClient defaultClient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(page);
			HttpResponse response = defaultClient.execute(httpget);
			HttpEntity result = response.getEntity();
			//Get the input stream
			InputStream stream = result.getContent();
			String returnedHtml = new Scanner(stream).useDelimiter("\\A").next();
			doc = Jsoup.parse(returnedHtml);
			
			String resultNumString = doc.select("p.description, p[style*=line-height]").text();
			System.out.println(resultNumString);
		
			String [] splittedString = resultNumString.split(" ");
			//Needed for no results returned
			if(splittedString.length < 9)
			{
				totalResults = 0;
			}
			else
			{
				String resNum = splittedString[10];
				Integer rslt = new Integer(resNum);
				totalResults = rslt.intValue();
			}
		}
		catch(ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(NumberFormatException e)
		{
			HomeScreen hs = new HomeScreen();
			hs.setVisible(true);
		}
		
	}
	

	public int getTotalResults()
	{
		return totalResults;
	}
	public ArrayList<Event> getResults()
	{
			ArrayList<Event> results = new ArrayList<Event>();
				//Get simple result array, no detailed event information
				Elements links = doc.select("a[href*=event-details]");
	
				for(Element e: links)
				{
					String title = e.text();
					String link = "http://www.cancer.org/involved/participate/app/"+e.attr("href");
					Event theEvent = new Event(title, link);
					results.add(theEvent);
				}
		return results;
	}
}
