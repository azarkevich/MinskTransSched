package com.mts;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.rms.*;

import com.options.*;
import com.resources.Images;


public class MinskTransSchedMidlet extends MIDlet
{
	public static BusStop[] allBusStopsArray;
	public static Bus[] allBusesArray;

	public final static Command cmdSelect = new Command("Выбрать", Command.OK, 1);
	public final static Command cmdHelp = new Command("Помощь", Command.HELP, 1);

	public final static Command cmdOK = new Command("OK", Command.OK, 1);
	public final static Command cmdCancel = new Command("Отменить", Command.BACK, 1);
	public final static Command cmdBack = new Command("Назад", Command.BACK, 1);

	public static Display display;
	public static MinskTransSchedMidlet midlet;
	
	public static OptionsListener[] optionsListeners;

	public MinskTransSchedMidlet()
	{
		midlet = this;
		display = Display.getDisplay(this);
	}

	protected void destroyApp(boolean unconditional)
	{
		notifyDestroyed();
	}

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
					// TODO: use hashtable
					for (int b = 0; b < allBusStopsArray.length; b++)
					{
						if(allBusStopsArray[b].id == id)
						{
							allBusStopsArray[b].favorite = (rec[0] == 1);
							allBusStopsArray[b].bookmarkRecord = recId;
							break;
						}
					}
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
					// TODO: use hashtable
					for (int b = 0; b < allBusesArray.length; b++)
					{
						if(allBusesArray[b].id == id)
						{
							allBusesArray[b].favorite = (rec[0] == 1);
							allBusesArray[b].bookmarkRecord = recId;
							break;
						}
					}
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
		OptionsStoreManager.ReadSettings();
		
	    // load data
		ScheduleLoader loader = new ScheduleLoader();
		loader.Load();

		allBusStopsArray = loader.busStops;
		allBusesArray = loader.buses;

		loadBusStopsFavorites();
		loadBusesFavorites();

		Images.load();
		
		SchedulerCanvas scheduleBoard = new SchedulerCanvas();
		display.setCurrent(scheduleBoard);
		
		optionsListeners = new OptionsListener[] { scheduleBoard };
	}
}
