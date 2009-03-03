package com.mts;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.OM.Bus;
import com.OM.BusStop;

import com.options.CmdDef;
import com.options.KeyCommands;
import com.options.Options;
import com.options.OptionsListener;
import com.options.OptionsMenu;
import com.text.MultiLineText;


public class SchedulerCanvas extends Canvas implements OptionsListener, CommandListener
{
	static final Command cmdFilter = new Command("Фильтр", Command.OK, 1); 
	static final Command cmdShowBusStopsList = new Command("Остановки", Command.OK, 2); 
	final static Command cmdOptions = new Command("Настройки", Command.SCREEN, 2);
	final static Command cmdExit = new Command("Выход", Command.EXIT, 2);
	final static Command cmdAbout = new Command("О программе", Command.EXIT, 2);

	BusStop[] busStops = null;
	int currentBusStopIndex = 0;
	
	MultiLineText multiLineText;
	int savedViewportTop;

	ScheduleBuilder scheduleBuilder;
	
	Timer refreshTimer;
	
	boolean fullScreen = false;

	public int foreColor = 255 << 8;
	

	public SchedulerCanvas()
	{
		super();
		
		setCommandListener(this);
		
		if(Options.showExitCommand)
			addCommand(cmdExit);
		if(Options.showHelpCommand)
			addCommand(TransSched.cmdHelp);
		if(Options.showAboutCommand)
			addCommand(cmdAbout);
		
		addCommand(cmdOptions);
		addCommand(cmdShowBusStopsList);
		addCommand(cmdFilter);
		
		fullScreen = Options.fullScreen;
		setFullScreenMode(fullScreen);
		
		TimerTask tt = new TimerTask()
		{
			public void run()
			{
				RefreshScheduleText(true);
			}
		};
		refreshTimer = new Timer();
		refreshTimer.schedule(tt, 10*1000, 10*1000);

		multiLineText = new MultiLineText();

		scheduleBuilder = new ScheduleBuilder(filter);

		// start with favorites
		setFilterToFavorites();
	}

	public void setFilterToFavorites()
	{
		Bus[] favBuses = filter.getFavorites(TransSched.allBusesArray);
		if(favBuses.length > 0)
		{
			filter.setBusesFilter(favBuses);
		}

		BusStop[] favBS = filter.getFavorites(TransSched.allBusStopsArray);
		if(favBS.length > 0)
		{
			filter.setBusStopsFilter(favBS);
		}
		setForeColor();
		
		setBusStops(filter.FilterIt(TransSched.allBusStopsArray), null);
	}

	public void setBusStops(BusStop[] stops, BusStop sel)
	{
		busStops = stops;
		currentBusStopIndex = 0;
		if(sel != null)
		{
			for (int i = 0; i < busStops.length; i++)
			{
				if(busStops[i] == sel)
				{
					currentBusStopIndex = i;
					break;
				}
			}
		}
		RefreshScheduleText();
	}
	
	public void selectBusStop(BusStop sel)
	{
		TransSched.display.setCurrent(this);

		for (int i = 0; i < busStops.length; i++)
		{
			if(busStops[i] == sel)
			{
				currentBusStopIndex = i;
				RefreshScheduleText();
				break;
			}
		}
	}

	BusStop getCurrentBusStop()
	{
		if(currentBusStopIndex >= busStops.length)
			return null;
		return busStops[currentBusStopIndex];
	}
	
	void showCurrBusStopsList()
	{
		BusStopsList list = new BusStopsList("Остановки", busStops, this, getCurrentBusStop()); 
		TransSched.display.setCurrent(list);
	}

	public void showBusesFilter()
	{
		TransSched.display.setCurrent(new BusesFilter(this, false));
	}

	public void showBusStopsFilter()
	{
		TransSched.display.setCurrent(new BusStopsFilter(this, false));
	}

	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == cmdFilter)
		{
			TransSched.display.setCurrent(new MainFilterMenu(this));
		}
		else if(cmd == cmdShowBusStopsList)
		{
			showCurrBusStopsList();
		}
		else if(cmd == cmdOptions)
		{
			TransSched.display.setCurrent(new OptionsMenu(this));
		}
		else if(cmd == TransSched.cmdHelp)
		{
			TransSched.display.setCurrent(new Help(Help.mainHelpText, this));
		}
		else if(cmd == cmdExit)
		{
			TransSched.midlet.notifyDestroyed();
		}
		else if(cmd == cmdAbout)
		{
			TransSched.display.setCurrent(new About(this));
		}
	}

	public void resetFilter()
	{
		filter.setBusesFilter(null);
		filter.setBusStopsFilter(null);
		setForeColor();
		setBusStops(TransSched.allBusStopsArray, getCurrentBusStop());
	}

	public void paint(Graphics g)
	{
		if(multiLineText == null)
			return;

		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(foreColor);
		multiLineText.Draw(g);
	}
	
	private void RefreshScheduleText()
	{
		RefreshScheduleText(false);
	}
	
	private void RefreshScheduleText(boolean savePosition)
	{
		if(savePosition && multiLineText != null)
			savedViewportTop = multiLineText.viewportTop;
		else
			savedViewportTop = 0;

		multiLineText.SetTextPar(0, 0, getWidth(), getHeight(),
				Font.getFont(Options.fontFace, Options.fontStyle, Options.fontSize),
				scheduleBuilder.GetScheduleText(getCurrentBusStop()));
		
		multiLineText.viewportTop = savedViewportTop;

		repaint();
	}

	boolean keyRepeated = false;
	protected void keyPressed(int keyCode)
	{
		keyRepeated = false;
		handleKeyEvent(keyCode, false, false);
	}
	
	protected void keyRepeated(int keyCode)
	{
		keyRepeated = true;
		handleKeyEvent(keyCode, false, true);
	}

	protected void keyReleased(int keyCode)
	{
		handleKeyEvent(keyCode, true, keyRepeated);
		keyRepeated = false;
	}

	void handleKeyEvent(int keyCode, boolean released, boolean repeated)
	{
		CmdDef cmd = KeyCommands.getCommand(keyCode, released, repeated);
		if(cmd == null)
			return;
		
		if(cmd == CmdDef.cmdScrollUp)
		{
			multiLineText.MoveUp(Options.scrollSize);
			repaint();
			return;
		}
		else if(cmd == CmdDef.cmdScrollDown)
		{
			multiLineText.MoveDown(Options.scrollSize);
			repaint();
			return;
		}
		else if(cmd == CmdDef.cmdScrollUpPage)
		{
			multiLineText.PageUp();
			repaint();
			return;
		}
		else if(cmd == CmdDef.cmdScrollDownPage)
		{
			multiLineText.PageDown();
			repaint();
			return;
		}
		else if(cmd == CmdDef.cmdBusStopPrev)
		{
			if(busStops.length != 0)
				currentBusStopIndex = (currentBusStopIndex + busStops.length - 1) % busStops.length;
		}
		else if(cmd == CmdDef.cmdBusStopNext)
		{
			if(busStops.length != 0)
				currentBusStopIndex = (currentBusStopIndex + 1) % busStops.length;
		}
		else if(cmd == CmdDef.cmdWindowDecrease)
		{
			scheduleBuilder.WindowSize -= Options.defWindowSizeStep;
			if(scheduleBuilder.WindowSize < 0)
				scheduleBuilder.WindowSize = 0;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdWindowIncrease)
		{
			scheduleBuilder.WindowSize += Options.defWindowSizeStep;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdWindowShiftDecrease)
		{
			scheduleBuilder.WindowShift -= Options.defWindowShiftStep;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdWindowShiftIncrease)
		{
			scheduleBuilder.WindowShift += Options.defWindowShiftStep;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdToggleDayType)
		{
			scheduleBuilder.ShiftDayType();
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdToggleDetailedDescription)
		{
			scheduleBuilder.showDescription = !scheduleBuilder.showDescription;
		}
		else if(cmd == CmdDef.cmdToggleFavorite)
		{
			BusStop bs = getCurrentBusStop();
			if(bs != null)
				bs.toggleFavorite();
		}
		else if(cmd == CmdDef.cmdToggleFullSchedule)
		{
			scheduleBuilder.showFull = !scheduleBuilder.showFull;  
		}
		else if(cmd == CmdDef.cmdScheduleReset)
		{
			scheduleBuilder.WindowShift = Options.defWindowShift;
			scheduleBuilder.WindowSize = Options.defWindowSize;
			scheduleBuilder.UserDayType = ScheduleBuilder.DAY_AUTO;
			scheduleBuilder.schedShift = 0;
			scheduleBuilder.showFull = false;
			scheduleBuilder.showTimeDiff = true;
			scheduleBuilder.showDescription = false;
		}
		else if(cmd == CmdDef.cmdShowCurrentBusStops)
		{
			showCurrBusStopsList();
		}
		else if(cmd == CmdDef.cmdShowFilterMenu)
		{
			TransSched.display.setCurrent(new MainFilterMenu(this));
		}
		else if(cmd == CmdDef.cmdScheduleFullScreen)
		{
			fullScreen = !fullScreen; 
			setFullScreenMode(fullScreen);
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdSchedShiftDecrease)
		{
			scheduleBuilder.schedShift--;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdSchedShiftIncrease)
		{
			scheduleBuilder.schedShift++;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdSchedShiftDecrease10)
		{
			scheduleBuilder.schedShift -= 10;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdSchedShiftIncrease10)
		{
			scheduleBuilder.schedShift+=10;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdToggleSchedShowTimeDiff)
		{
			scheduleBuilder.showTimeDiff = !scheduleBuilder.showTimeDiff;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdToggleBusFlow)
		{
			scheduleBuilder.showBusFlow = !scheduleBuilder.showBusFlow;
		}

		RefreshScheduleText();
	}
	
	public void OptionsUpdated()
	{
		scheduleBuilder.WindowShift = Options.defWindowShift;
		scheduleBuilder.WindowSize = Options.defWindowSize;
		fullScreen = Options.fullScreen;
		setFullScreenMode(fullScreen);

		try{
			removeCommand(cmdExit);
			removeCommand(TransSched.cmdHelp);
			removeCommand(cmdAbout);
			
			if(Options.showExitCommand)
				addCommand(cmdExit);
			if(Options.showHelpCommand)
				addCommand(TransSched.cmdHelp);
			if(Options.showAboutCommand)
				addCommand(cmdAbout);
		}
		catch(Exception e)
		{
		}

		RefreshScheduleText(true);
	}

	public Filter filter = new Filter();
	public void setBusesFilter(Bus[] f)
	{
		filter.setBusesFilter(f);
		setBusStops(filter.FilterIt(TransSched.allBusStopsArray), getCurrentBusStop());
		setForeColor();
		TransSched.display.setCurrent(this);
	}

	public void setBusStopsFilter(BusStop[] f)
	{
		filter.setBusStopsFilter(f);
		setBusStops(filter.FilterIt(TransSched.allBusStopsArray), getCurrentBusStop());
		setForeColor();
		TransSched.display.setCurrent(this);
	}
	
	void setForeColor()
	{
		if(filter.busesFilter == null && filter.busStopsFilter == null)
			foreColor = 255 << 16 | 255 << 8 | 255;
		else
			foreColor = 0 << 16 | 255 << 8 | 0;
	}
}
