////////////////////////////////////////////////////////////////
/*
 * Tom George
 * Event.java
 * Used to hold Event objects, where all the important information about 
 * events goes.
 */
////////////////////////////////////////////////////////////////

package edu.geneseo.teg3;

public class Event 
{
	String name, address, description,startDate, endDate, link, startTime, endTime,
	timeZone, handicap;
	
	public Event()
	{
		name="";
		address="";
		description="";
		link="";
		startDate="";
		endDate="";
		startTime="";
		endTime="";
		timeZone="";
		handicap="";
		
	}
	
	public Event(String name, String link)
	{
		this.name = name;
		address="";
		description = "";
		this.link = link;
		startDate="";
		endDate="";
		startTime="";
		endTime="";
		timeZone="";
		handicap="";

	}
	
	public Event(String name, String address, String description, String link, String startDate, 
			String endDate, String startTime, String endTime, String timeZone, String handicap)
	{
		this.name = name;
		this.address=address;
		this.description=description;
		this.link = link;
		this.startDate=startDate;
		this.endDate=endDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.timeZone = timeZone;
		this.handicap = handicap;
	}
	
	public String getName() { return name; }
	public String getAddress() { return address; }
	public String getDescription() { return description; }
	public String getLink() { return link; }
	public String getStartDate() { return startDate; }
	public String getEndDate() { return endDate; }
	public String getStartTime() { return startTime; }
	public String getEndTime() { return endTime; }
	public String getTimeZone() { return timeZone; }
	public String getHandicap() { return handicap; }
	
	public String toString()
	{
		return "Event: "+name+"\n\nAddress: "+address+"\n\nDescription: "+description+
		"\n\nStart Date: "+startDate+"\n\nEnd Date: "+endDate+
		"\n\nStart Time: "+startTime+"\n\nEnd Time: "+endTime+"\n\nTime Zone: "+timeZone;
	}
	
	
}
