package com.mts;
import java.util.Stack;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.rms.*;

import com.options.*;
import com.resources.Images;
import com.test.Caps;
import com.test.KeysTest;


public class MinskTransSchedMidlet extends MIDlet implements CommandListener
{
	public BusStop[] busStops;
	
	public final static Command cmdExit = new Command("Exit", Command.EXIT, 2);
	public final static Command cmdShowBookMarks = new Command("Закладки", Command.SCREEN, 1);
	public final static Command cmdShowAllBusStops = new Command("Остановки", Command.SCREEN, 2);
	public final static Command cmdSelect = new Command("Выбрать", Command.OK, 1);
	public final static Command cmdMainHelpPage = new Command("Помощь", Command.HELP, 1);
	public final static Command cmdHelp = new Command("Помощь", Command.HELP, 1);
	public final static Command cmdOptions = new Command("Настройки", Command.SCREEN, 2);

	public final static Command cmdKeysTest = new Command("Тест клавиатуры", Command.ITEM, 10);
	public final static Command cmdCaps = new Command("Возможности", Command.ITEM, 10);
	public final static Command cmdAbout = new Command("О программе", Command.ITEM, 1);

	public final static Command cmdOK = new Command("OK", Command.OK, 1);
	public final static Command cmdCancel = new Command("Отменить", Command.BACK, 1);
	public final static Command cmdBack = new Command("Назад", Command.BACK, 1);
	public final static Command cmdReset = new Command("Сбросить", Command.OK, 2);

	Stack displayableStack = new Stack(); 

	SchedulerCanvas scheduleBoard;
	List allBusStops;
	List settings;
	List bookmarks;

	public static Display display;
	public static MinskTransSchedMidlet midlet;
	
	public static OptionsListener[] optionsListeners;

	public MinskTransSchedMidlet()
	{
		midlet = this;
	}

	public void commandAction(Command cmd, Displayable d)
	{
		// OK, Cancel, Back - restore previous form
		if(cmd == cmdOK || cmd == cmdBack || cmd == cmdCancel)
		{
			if(displayableStack.empty())
			{
				showStartupScreen();
			}
			else
			{
				display.setCurrent((Displayable)displayableStack.pop());
			}
		}
		else if(cmd == cmdShowBookMarks)
		{
			display.setCurrent(getBookmarks());
		}
		else if(cmd == cmdMainHelpPage)
		{
			displayableStack.push(display.getCurrent());
			HelpCanvas helpCanvas = new HelpCanvas(HelpCanvas.mainHelpText);
			helpCanvas.addCommand(cmdExit);
			helpCanvas.addCommand(cmdOptions);
			helpCanvas.addCommand(cmdBack);
			helpCanvas.addCommand(cmdKeysTest);
			helpCanvas.addCommand(cmdCaps);
			helpCanvas.addCommand(cmdAbout);

			helpCanvas.setCommandListener(this);

			display.setCurrent(helpCanvas);
		}
		else if(cmd == cmdExit)
		{
			destroyApp(true);
		}
		else if(cmd == cmdShowAllBusStops)
		{
			allBusStops.setSelectedIndex(scheduleBoard.getBusStation(), true);
			display.setCurrent(getAllBusStopsScreen());
		}
		else if(cmd == List.SELECT_COMMAND || cmd == cmdSelect)
		{
			if(d == bookmarks)
				showBookmarkSchedule(((List)d).getSelectedIndex());
			else if(d == allBusStops)
				showAllBusStopsSchedule(((List)d).getSelectedIndex());
			else if(d == settings)
			{
				if(((List)d).getSelectedIndex() == 0)
					showOptGeneral();
				else
					showOptKeys();
			}
		}
		else if(cmd == cmdOptions)
		{
			displayableStack.push(display.getCurrent());
			
			settings = new List("Настройки", List.IMPLICIT);
			settings.append("Основные", null);
			settings.append("Управление", null);
			
			settings.addCommand(cmdBack);
			settings.addCommand(cmdSelect);
			settings.setCommandListener(this);
			
			display.setCurrent(settings);
		}
		else if(cmd == cmdKeysTest)
		{
			displayableStack.push(display.getCurrent());
			
			KeysTest scr = new KeysTest();
			scr.addCommand(cmdBack);
			scr.setCommandListener(this);

			display.setCurrent(scr);
		}
		else if(cmd == cmdAbout)
		{
			displayableStack.push(display.getCurrent());
			
			About scr = new About();
			scr.addCommand(cmdBack);
			scr.setCommandListener(this);

			display.setCurrent(scr);
		}
		else if(cmd == cmdCaps)
		{
			displayableStack.push(display.getCurrent());
			
			Caps scr = new Caps();
			scr.addCommand(cmdBack);
			scr.setCommandListener(this);

			display.setCurrent(scr);
		}
	}
	
	void showOptGeneral()
	{
		displayableStack.push(display.getCurrent());

		display.setCurrent(new GeneralPrefs(this));
	}
	
	void showOptKeys()
	{
		displayableStack.push(display.getCurrent());

		display.setCurrent(new ControlPrefs(this));
	}
	
	void showAllBusStopsSchedule(int sel)
	{
		scheduleBoard.setBusStops(busStops);
		scheduleBoard.setBusStation(sel);
		scheduleBoard.setForeColor(255, 255, 255);
		display.setCurrent(scheduleBoard);
	}

	void showBookmarkSchedule(int sel)
	{
		BusStop[] bmBusStops = GetBookmarkedBusStops();
		scheduleBoard.setBusStops(bmBusStops);
		scheduleBoard.setBusStation(sel);
		scheduleBoard.setForeColor(0, 255, 0);
		display.setCurrent(scheduleBoard);
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
				}
			}
			bmBusStops.closeRecordStore();
		}
		catch(Exception ex)
		{
		}
	}
	
	Displayable getBookmarks()
	{
		// create on demand
		if(bookmarks == null)
		{
			bookmarks = new List("Закладки", Choice.IMPLICIT);
			bookmarks.setCommandListener(this);
			bookmarks.addCommand(cmdSelect);
			bookmarks.addCommand(cmdShowAllBusStops);
			bookmarks.addCommand(cmdMainHelpPage);
			bookmarks.addCommand(cmdOptions);
			bookmarks.addCommand(cmdExit);
		}
		else
		{
			bookmarks.deleteAll();
		}

		for (int i = 0; i < busStops.length; i++)
		{
			if(busStops[i].bookmarked)
				bookmarks.append (busStops[i].name, Images.heart);
		}

		if(bookmarks == null)
			return allBusStops;
		
		return bookmarks;
	}

	protected void startApp() throws MIDletStateChangeException
	{
		display = Display.getDisplay(this);
		
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
		
		optionsListeners = new OptionsListener[] { scheduleBoard };
		
		allBusStops = new List("Остановки", Choice.IMPLICIT);
		allBusStops.setCommandListener(this);
		allBusStops.addCommand(cmdSelect);
		allBusStops.addCommand(cmdShowBookMarks);
		allBusStops.addCommand(cmdMainHelpPage);
		allBusStops.addCommand(cmdOptions);
		allBusStops.addCommand(cmdExit);
		
		for (int i = 0; i < busStops.length; i++)
		{
			allBusStops.append(busStops[i].name, null);
		}

		for (int i = 0; i < allBusStops.size(); i++)
			allBusStops.setFont(i, Font.getFont(Options.fontFace, Options.fontStyle, Options.fontSize));
		
		loadBookmarks();
		
		showStartupScreen();
	}
	
	void showStartupScreen()
	{
		switch (Options.startupScreen)
		{
		default:
		case Options.BOOKMARK_SCREEN:
			display.setCurrent(getBookmarks());
			break;
		case Options.BOOKMARKS_SCHED:
			showBookmarkSchedule(0);
			break;
		case Options.ALL_BUSSTOPS_SCREEN:
			display.setCurrent(getAllBusStopsScreen());
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
