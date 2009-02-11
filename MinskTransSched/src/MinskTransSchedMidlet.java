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
	final static Command cmdMainHelpPage = new Command("Помощь", Command.HELP, 1);
	
	SchedulerCanvas scheduleBoard;
	HelpCanvas helpCanvas;
	List bookmarks;
	List allBusStops;

	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == cmdShowBookMarks)
		{
			Display.getDisplay(this).setCurrent(getBookmarks());
		}
		else if(cmd == cmdMainHelpPage)
		{
			if(helpCanvas == null)
			{
				helpCanvas = new HelpCanvas();
				helpCanvas.setCommandListener(this);
				helpCanvas.addCommand(cmdShowAllBusStops);
				helpCanvas.addCommand(cmdShowBookMarks);
				helpCanvas.addCommand(cmdExit);
			}
			
			Display.getDisplay(this).setCurrent(helpCanvas);
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
			scheduleBoard.setBusStops(busStops);
			scheduleBoard.setBusStation(((List)d).getSelectedIndex());
			scheduleBoard.setForeColor(255, 255, 255);
			Display.getDisplay(this).setCurrent(scheduleBoard);
		}
		else if(cmd == cmdSelectBookmark)
		{
			BusStop[] bmBusStops = GetBookmarkedBusStops();
			scheduleBoard.setBusStops(bmBusStops);
			scheduleBoard.setBusStation(((List)d).getSelectedIndex());
			scheduleBoard.setForeColor(0, 255, 0);
			Display.getDisplay(this).setCurrent(scheduleBoard);
		}
	}
	
	public BusStop[] GetBookmarkedBusStops()
	{
		BusStop[] ret = null;
		int count = 0;
		for (int i = 0; i < busStops.length; i++)
		{
			if(busStops[i].bookmarked)
				count++;
		}
		ret = new BusStop[count];
		int index = 0;
		for (int i = 0; i < busStops.length; i++)
		{
			if(busStops[i].bookmarked)
				ret[index++] = busStops[i];
		}
		return ret;
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
		bookmarks.deleteAll();
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
		
		scheduleBoard = new SchedulerCanvas(busStops);
		scheduleBoard.setCommandListener(this);
		scheduleBoard.addCommand(cmdExit);
		scheduleBoard.addCommand(cmdShowBookMarks);
		scheduleBoard.addCommand(cmdShowAllBusStops);
		scheduleBoard.addCommand(cmdMainHelpPage);
		
		allBusStops = new List("Остановки", Choice.IMPLICIT);
		allBusStops.setCommandListener(this);
		allBusStops.setSelectCommand(cmdSelectBusStop);
		allBusStops.addCommand(cmdShowBookMarks);
		allBusStops.addCommand(cmdMainHelpPage);
		
		for (int i = 0; i < busStops.length; i++)
		{
			allBusStops.append (busStops[i].name, null);
		}
		
		bookmarks = new List("Закладки", Choice.IMPLICIT);
		bookmarks.setCommandListener(this);
		bookmarks.setSelectCommand(cmdSelectBookmark);
		bookmarks.addCommand(cmdShowAllBusStops);
		bookmarks.addCommand(cmdMainHelpPage);

		loadBookmarks();
		Display.getDisplay(this).setCurrent(getBookmarks());
	}
}
