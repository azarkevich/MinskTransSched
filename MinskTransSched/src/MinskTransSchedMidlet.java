import java.util.Stack;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.rms.*;

import options.Options;
import options.OptionsStoreManager;
import options.OptionsVisualizer;
import resources.Images;

public class MinskTransSchedMidlet extends MIDlet implements CommandListener
{
	public BusStop[] busStops;
	
	final static Command cmdExit = new Command("Выход", Command.EXIT, 2);
	final static Command cmdShowBookMarks = new Command("Закладки", Command.SCREEN, 1);
	final static Command cmdShowAllBusStops = new Command("Остановки", Command.SCREEN, 2);
	final static Command cmdSelectBusStop = new Command("Выбрать", Command.OK, 1);
	final static Command cmdSelectBookmark = new Command("Выбрать", Command.OK, 1);
	final static Command cmdMainHelpPage = new Command("Помощь", Command.HELP, 1);
	final static Command cmdOptions = new Command("Настройки", Command.SCREEN, 2);
	
	Stack displayableStack = new Stack(); 
	final static Command cmdOptSaveCommand = new Command("Сохранить", Command.OK, 1);
	final static Command cmdOptCancelCommand = new Command("Отмена", Command.BACK, 1);

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
				helpCanvas.addCommand(cmdOptions);
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
			Display.getDisplay(this).setCurrent(getAllBusStopsScreen());
		}
		else if(cmd == cmdSelectBusStop)
		{
			showAllBusStopsSchedule(((List)d).getSelectedIndex());
		}
		else if(cmd == cmdSelectBookmark)
		{
			showBookmarkSchedule(((List)d).getSelectedIndex());
		}
		else if(cmd == cmdOptions)
		{
			Display display = Display.getDisplay(this);
			displayableStack.push(display.getCurrent());
			
			options.Window opt = new options.Window();
			opt.addCommand(cmdOptSaveCommand);
			opt.addCommand(cmdOptCancelCommand);
			opt.setCommandListener(this);

			display.setCurrent(opt);
		}
		else if(cmd == cmdOptCancelCommand || cmd == cmdOptSaveCommand)
		{
			Display display = Display.getDisplay(this);
			if(displayableStack.empty())
			{
				display.setCurrent(getBookmarks());
			}
			else
			{
				display.setCurrent((Displayable)displayableStack.pop());
			}

			if(cmd == cmdOptSaveCommand)
			{
				((OptionsVisualizer)d).SaveSettingsFromControls(); 
				OptionsStoreManager.SaveSettings();
				
				scheduleBoard.OptionsUpdated();
			}
		}
	}
	
	void showAllBusStopsSchedule(int sel)
	{
		scheduleBoard.setBusStops(busStops);
		scheduleBoard.setBusStation(sel);
		scheduleBoard.setForeColor(255, 255, 255);
		Display.getDisplay(this).setCurrent(scheduleBoard);
	}

	void showBookmarkSchedule(int sel)
	{
		BusStop[] bmBusStops = GetBookmarkedBusStops();
		scheduleBoard.setBusStops(bmBusStops);
		scheduleBoard.setBusStation(sel);
		scheduleBoard.setForeColor(0, 255, 0);
		Display.getDisplay(this).setCurrent(scheduleBoard);
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
		
		for (int i = 0; i < bookmarks.size(); i++)
			bookmarks.setFont(i, Font.getFont(Options.fontFace, Options.fontStyle, Options.fontSize));

		if(bookmarks.size() == 0)
			return allBusStops;
		
		return bookmarks;
	}

	protected void startApp() throws MIDletStateChangeException
	{
		OptionsStoreManager.ReadSettings();

	    // load data
		ScheduleLoader loader = new ScheduleLoader();
		loader.Load();

		busStops = loader.busStops;
		
		Images.load();
		
		scheduleBoard = new SchedulerCanvas(busStops);
		scheduleBoard.setCommandListener(this);
		scheduleBoard.addCommand(cmdExit);
		scheduleBoard.addCommand(cmdShowBookMarks);
		scheduleBoard.addCommand(cmdShowAllBusStops);
		scheduleBoard.addCommand(cmdMainHelpPage);
		scheduleBoard.addCommand(cmdOptions);
		
		allBusStops = new List("Остановки", Choice.IMPLICIT);
		allBusStops.setCommandListener(this);
		allBusStops.setSelectCommand(cmdSelectBusStop);
		allBusStops.addCommand(cmdShowBookMarks);
		allBusStops.addCommand(cmdMainHelpPage);
		allBusStops.addCommand(cmdOptions);
		
		for (int i = 0; i < busStops.length; i++)
		{
			allBusStops.append(busStops[i].name, null);
		}

		for (int i = 0; i < allBusStops.size(); i++)
			allBusStops.setFont(i, Font.getFont(Options.fontFace, Options.fontStyle, Options.fontSize));
		
		bookmarks = new List("Закладки", Choice.IMPLICIT);
		bookmarks.setCommandListener(this);
		bookmarks.setSelectCommand(cmdSelectBookmark);
		bookmarks.addCommand(cmdShowAllBusStops);
		bookmarks.addCommand(cmdMainHelpPage);
		bookmarks.addCommand(cmdOptions);

		loadBookmarks();
		
		switch (Options.startupScreen)
		{
		default:
		case Options.BOOKMARK_SCREEN:
			Display.getDisplay(this).setCurrent(getBookmarks());
			break;
		case Options.BOOKMARKS_SCHED:
			showBookmarkSchedule(0);
			//Display.getDisplay(this).setCurrent(getBookmarks());
			break;
		case Options.ALL_BUSSTOPS_SCREEN:
			Display.getDisplay(this).setCurrent(getAllBusStopsScreen());
			break;
		case Options.ALL_BUSSTOPS_SCHED:
			showAllBusStopsSchedule(0);
			break;
		}
	}
	
	Displayable getAllBusStopsScreen()
	{
		for (int i = 0; i < busStops.length; i++)
		{
			allBusStops.set(i, busStops[i].name, busStops[i].bookmarked ? Images.heart : null);
		}
		return allBusStops;
	}
}
