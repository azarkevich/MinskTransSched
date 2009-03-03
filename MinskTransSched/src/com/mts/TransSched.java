package com.mts;
import java.util.Hashtable;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.rms.*;

import com.OM.Bus;
import com.OM.BusStop;
import com.OM.FilterDef;

import com.options.*;
import com.resources.Images;
import com.resources.ScheduleLoader;


public class TransSched extends MIDlet
{
	public static BusStop[] allBusStopsArray;
	public static Hashtable id2stop;
	
	public static Bus[] allBusesArray;
	public static Hashtable id2transport;

	public static FilterDef[] customFilters;
	public static FilterDef[] predefinedFilters;

	public final static Command cmdSelect = new Command("Выбрать", Command.OK, 1);
	public final static Command cmdHelp = new Command("Помощь", Command.HELP, 1);

	public final static Command cmdOK = new Command("OK", Command.OK, 1);
	public final static Command cmdCancel = new Command("Отменить", Command.BACK, 1);
	public final static Command cmdBack = new Command("Назад", Command.BACK, 1);

	public static Display display;
	public static TransSched midlet;
	SchedulerCanvas scheduleBoard;
	
	public static OptionsListener[] optionsListeners;

	protected void pauseApp()
	{
	}
	
	void loadBusStopsFavorites()
	{
		try{
			RecordStore bmBusStops = RecordStore.openRecordStore("bmBusStops", true);
			for(int i=0;i<bmBusStops.getNextRecordID();i++)
			{
				int recId = i + 1;
				try{
					byte[] rec = bmBusStops.getRecord(recId);
					if(rec.length != 3)
					{
						bmBusStops.deleteRecord(recId);
						i--;
						continue;
					}
	
					int id = rec[1] * 256 + rec[2];
					BusStop bs = (BusStop)id2stop.get(new Integer(id));
					bs.favorite = (rec[0] == 1);
					bs.bookmarkRecord = recId;
				}
				catch(InvalidRecordIDException ex)
				{
				}
			}
			bmBusStops.closeRecordStore();
		}
		catch(Exception ex)
		{
		}
	}

	void loadBusesFavorites()
	{
		try{
			RecordStore rsBuses = RecordStore.openRecordStore("bmBuses", true);
			for(int i=0;i<rsBuses.getNextRecordID();i++)
			{
				int recId = i + 1;
				try{
					byte[] rec = rsBuses.getRecord(recId);
					if(rec.length != 3)
					{
						rsBuses.deleteRecord(recId);
						i--;
						continue;
					}
	
					int id = rec[1] * 256 + rec[2];
					Bus b = (Bus)id2transport.get(new Integer(id));
					b.favorite = (rec[0] == 1);
					b.bookmarkRecord = recId;
				}
				catch(InvalidRecordIDException ex)
				{
				}
			}
			rsBuses.closeRecordStore();
		}
		catch(Exception ex)
		{
		}
	}
	
	protected void startApp() throws MIDletStateChangeException
	{
		// Return if MIDlet has already been initialized
		if (midlet != null)
		{
			display.setCurrent(midlet.scheduleBoard);
			return;
		}
		
		midlet = this;
		display = Display.getDisplay(this);

		OptionsStoreManager.ReadSettings();
		
	    // load data
		ScheduleLoader loader = new ScheduleLoader();
		loader.Load();

		allBusStopsArray = loader.busStops;
		id2stop = new Hashtable(allBusStopsArray.length);
		for (int i = 0; i < allBusStopsArray.length; i++)
		{
			id2stop.put(new Integer(allBusStopsArray[i].id), allBusStopsArray[i]);
		}

		allBusesArray = loader.buses;
		id2transport = new Hashtable(allBusesArray.length);
		for (int i = 0; i < allBusesArray.length; i++)
		{
			id2transport.put(new Integer(allBusesArray[i].id), allBusesArray[i]);
		}

		loader.LoadFiltersPub();

		loadBusStopsFavorites();
		loadBusesFavorites();

		predefinedFilters = loader.filters;
		customFilters = OptionsStoreManager.LoadCustomFilterDefinitions();
		
		Images.load();

		scheduleBoard = new SchedulerCanvas();
		display.setCurrent(scheduleBoard);
		
		optionsListeners = new OptionsListener[] { scheduleBoard };
	}

	protected void destroyApp(boolean unconditional)
	{
		display.setCurrent(null);
		notifyDestroyed();
	}
}
