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
	public static BusStop[] allBusStopsArray;
	public static Bus[] allBusesArray;

	public final static Command cmdExit = new Command("Exit", Command.EXIT, 2);
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
	List favBuses;
	List allBuses;
	List settings;

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
//		else if(cmd == cmdShowAllBusStops)
//		{
//			showAllBusStopsList();
//		}
//		else if(cmd == cmdShowFavBusStops)
//		{
//			showFavBusStopsList();
//		}
		else if(cmd == List.SELECT_COMMAND || cmd == cmdSelect)
		{
			if(d == settings)
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
	
	protected void destroyApp(boolean unconditional)
	{
		notifyDestroyed();
	}

	protected void pauseApp()
	{
	}
	
	void loadFavorites()
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

	protected void startApp() throws MIDletStateChangeException
	{
		display = Display.getDisplay(this);
		
		OptionsStoreManager.ReadSettings();
		
	    // load data
		ScheduleLoader loader = new ScheduleLoader();
		loader.Load();

		allBusStopsArray = loader.busStops;
		allBusesArray = loader.buses;
		
		Images.load();
		
		scheduleBoard = new SchedulerCanvas(this);
		
		optionsListeners = new OptionsListener[] { scheduleBoard };
		
		loadFavorites();
		
		//showStartupScreen();
		
		display.setCurrent(scheduleBoard);
	}
	
	void showStartupScreen()
	{
//		switch (Options.startupScreen)
//		{
//		default:
//		case Options.BOOKMARK_SCREEN:
//			showFavBusStopsList();
//			break;
//		case Options.BOOKMARKS_SCHED:
//			showFavBusStopsList();
//			break;
//		case Options.ALL_BUSSTOPS_SCREEN:
//			showAllBusStopsList();
//			break;
//		case Options.ALL_BUSSTOPS_SCHED:
//			showAllBusStopsList();
//			break;
//		}
	}
}
