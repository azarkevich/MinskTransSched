import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.rms.*;

public class MinskTransSchedMidlet extends MIDlet implements CommandListener
{
	public BusStop[] busStops;
	
	final static Command cmdExit = new Command("Выход", Command.EXIT, 2);
	final static Command cmdShowBookMarks = new Command("Закладки", Command.SCREEN, 1);
	final static Command cmdShowAllBusStops = new Command("Остановки", Command.SCREEN, 2);
	final static Command cmdSelectBusStop = new Command("Выбрать", Command.OK, 1);
	final static Command cmdSelectBookmark = new Command("Выбрать", Command.OK, 1);
	
	SchedulerCanvas scheduleBoard;
	List bookmarks;
	List allBusStops;
	
	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == cmdShowBookMarks)
		{
			Display.getDisplay(this).setCurrent(getBookmarks());
		}
		else if(cmd == cmdExit)
		{
			destroyApp(true);
		}
		else if(cmd == cmdShowAllBusStops)
		{
			allBusStops.setSelectedIndex(scheduleBoard.getBusStation(), true);
			Display.getDisplay(this).setCurrent(allBusStops);
		}
		else if(cmd == cmdSelectBusStop)
		{
			scheduleBoard.setBusStation(((List)d).getSelectedIndex());
			Display.getDisplay(this).setCurrent(scheduleBoard);
		}
		else if(cmd == cmdSelectBookmark)
		{
			int bookmarkIndex = ((List)d).getSelectedIndex();
			for (int i = 0; i < busStops.length; i++)
			{
				if(busStops[i].bookmarked)
				{
					if(bookmarkIndex == 0)
					{
						scheduleBoard.setBusStation(i);
						Display.getDisplay(this).setCurrent(scheduleBoard);
						break;
					}
					bookmarkIndex--;
				}
			}
		}
	}
	
	public MinskTransSchedMidlet()
	{
	}

	protected void destroyApp(boolean unconditional)
	{
		notifyDestroyed();
	}

	protected void pauseApp()
	{
	}
	
	void loadBookmarks()
	{
		try{
			RecordStore bmBusStops = RecordStore.openRecordStore("bmBusStops", true);
//			System.out.println("RecCount:" + bmBusStops.getNumRecords());
//			System.out.println("NextRecID:" + bmBusStops.getNextRecordID());
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
					for (int b = 0; b < busStops.length; b++)
					{
						if(busStops[b].id == id)
						{
							busStops[b].bookmarked = (rec[0] == 1);
							busStops[b].bookmarkRecord = recId;
							break;
						}
					}
				}
				catch(InvalidRecordIDException ex)
				{
					//System.out.println("Exception:" + ex.toString() + "\nRecordIndex:" + i + ", RecordID:" + recId);
				}
			}
			bmBusStops.closeRecordStore();
		}
		catch(Exception ex)
		{
			//System.out.println("Exception:" + ex.toString());
		}
	}
	
	Displayable getBookmarks()
	{
		for (int i = 0; i < busStops.length; i++)
		{
			if(busStops[i].bookmarked)
				bookmarks.append (busStops[i].name, null);
		}
		if(bookmarks.size() == 0)
			return allBusStops;
		
		return bookmarks;
	}

	protected void startApp() throws MIDletStateChangeException
	{
	    // load data
		ScheduleLoader loader = new ScheduleLoader();
		loader.Load();

		busStops = loader.busStops;
		
		scheduleBoard = new SchedulerCanvas(this);
		scheduleBoard.setCommandListener(this);
		scheduleBoard.addCommand(cmdExit);
		scheduleBoard.addCommand(cmdShowBookMarks);
		scheduleBoard.addCommand(cmdShowAllBusStops);

		allBusStops = new List("Остановки", Choice.IMPLICIT);
		allBusStops.setCommandListener(this);
		allBusStops.setSelectCommand(cmdSelectBusStop);
		allBusStops.addCommand(cmdExit);
		
		for (int i = 0; i < busStops.length; i++)
		{
			allBusStops.append (busStops[i].name, null);
		}
		
		bookmarks = new List("Закладки", Choice.IMPLICIT);
		bookmarks.setCommandListener(this);
		bookmarks.setSelectCommand(cmdSelectBookmark);
		bookmarks.addCommand(cmdExit);

		loadBookmarks();
		Display.getDisplay(this).setCurrent(getBookmarks());
	}
}
