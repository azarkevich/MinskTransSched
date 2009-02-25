package com.mts;

import javax.microedition.rms.RecordStore;

public class BusStop
{
	public short id;
	public String name;
	public String description;
	public Schedule[] schedules;
	public boolean favorite = false;
	public int bookmarkRecord = -1;
	
	public void toggleFavorite()
	{
		try{
			RecordStore bmBusStops = RecordStore.openRecordStore("bmBusStops", true);
			byte[] rec = new byte[3];
			rec[0] = (byte)(favorite ? 0 : 1);
			rec[1] = (byte)(id / 256);
			rec[2] = (byte)(id % 256);
			if(bookmarkRecord != -1)
				bmBusStops.setRecord(bookmarkRecord, rec, 0, rec.length);
			else
				bookmarkRecord = bmBusStops.addRecord(rec, 0, rec.length);
			
			bmBusStops.closeRecordStore();
			
			favorite = !favorite;
		}
		catch(Exception ex)
		{
		}
	}
}
