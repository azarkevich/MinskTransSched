package com.mts;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.options.CmdDef;
import com.options.KeyCommands;
import com.options.Options;
import com.options.OptionsListener;
import com.text.MultiLineText;


public class SchedulerCanvas extends Canvas implements OptionsListener, CommandListener
{
	static final Command cmdShowBusStopsList = new Command("Остановки", Command.OK, 1); 
	static final Command cmdAddBusFilter = new Command("По автобусу фильтр", Command.OK, 2); 
	static final Command cmdBusStopFilter = new Command("По остановке фильтр", Command.OK, 2); 
	static final Command cmdResetFilter = new Command("Сбросить фильтр", Command.OK, 3); 
	static final Command cmdOther = new Command("Другое", Command.OK, 100);
	
	BusStop[] busStops = null;
	int currentBusStopIndex = 0;
	
	MultiLineText multiLineText;
	int savedViewportTop;

	ScheduleBuilder scheduleBuilder;
	
	Timer refreshTimer;
	
	boolean fullScreen = false;

	public int foreColor = 255 << 8;
	

	public SchedulerCanvas(CommandListener parent)
	{
		parentCL = parent;
		
		this.setCommandListener(this);
		addCommand(MinskTransSchedMidlet.cmdExit);
		addCommand(MinskTransSchedMidlet.cmdMainHelpPage);
		addCommand(MinskTransSchedMidlet.cmdOptions);
		addCommand(cmdShowBusStopsList);
		addCommand(cmdResetFilter);
		addCommand(cmdAddBusFilter);
		addCommand(cmdBusStopFilter);
		addCommand(cmdOther);

		fullScreen = Options.fullScreen;
		setFullScreenMode(fullScreen);
		
		// start with favorites
		filter.setBusesFilter(filter.getFavorites(MinskTransSchedMidlet.allBusesArray));
		filter.setBusStopsFilter(filter.getFavorites(MinskTransSchedMidlet.allBusStopsArray));
		busStops = filter.FilterIt(MinskTransSchedMidlet.allBusStopsArray);
		
		scheduleBuilder = new ScheduleBuilder(filter);
		
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

		setForeColor();

		RefreshScheduleText();
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
		MinskTransSchedMidlet.display.setCurrent(this);

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
	
	public int getBusStation()
	{
		return currentBusStopIndex;
	}

	CommandListener parentCL;
	
	BusStop getCurrentBusStop()
	{
		if(currentBusStopIndex >= busStops.length)
			return null;
		return busStops[currentBusStopIndex];
	}
	
	void showCurrBusStopsList()
	{
		BusStopsList list = new BusStopsList("Остановки", busStops, this, getCurrentBusStop()); 
		MinskTransSchedMidlet.display.setCurrent(list);
	}

	void showBusesFilterList()
	{
		MinskTransSchedMidlet.display.setCurrent(new BusesFilter(this));
	}

	void showBusStopsFilterList()
	{
		MinskTransSchedMidlet.display.setCurrent(new BusStopsFilter(this));
	}

	public void commandAction(Command cmd, Displayable d)
	{
		boolean handled = false;
		
		if(d == this)
		{
			if(cmd == cmdShowBusStopsList)
			{
				showCurrBusStopsList();
				handled = true;
			}
			else if(cmd == cmdResetFilter)
			{
				BusStop cur = busStops[currentBusStopIndex];
				setBusStops(MinskTransSchedMidlet.allBusStopsArray, cur);
				filter.setBusesFilter(null);
				filter.setBusStopsFilter(null);
				setForeColor();
				RefreshScheduleText();
				handled = true;
			}
			else if(cmd == cmdAddBusFilter)
			{
				showBusesFilterList();
			}
			else if(cmd == cmdBusStopFilter)
			{
				showBusStopsFilterList();
			}
		}
		if(handled == false)
			parentCL.commandAction(cmd, d);
	}

	public void paint(Graphics g)
	{
		if(multiLineText == null)
			return;

		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(foreColor);
		multiLineText.DrawMultStr(g);
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
			scheduleBuilder.toggleFullSched(); 
		}
		else if(cmd == CmdDef.cmdScheduleReset)
		{
			scheduleBuilder.WindowShift = Options.defWindowShift;
			scheduleBuilder.WindowSize = Options.defWindowSize;
			scheduleBuilder.UserDayType = ScheduleBuilder.DAY_AUTO;
			scheduleBuilder.schedShift = 0;
			scheduleBuilder.showFull = ScheduleBuilder.SCHED_FULL_NONE;
			scheduleBuilder.showTimeDiff = true;
			scheduleBuilder.showDescription = false;
		}
		else if(cmd == CmdDef.cmdShowCurrentBusStops)
		{
			showCurrBusStopsList();
		}
		else if(cmd == CmdDef.cmdShowBusesFilter)
		{
			showBusesFilterList();
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
		RefreshScheduleText(true);
	}

	public Filter filter = new Filter();
	public void setBusesFilter(Bus[] f)
	{
		filter.setBusesFilter(f);
		BusStop cur = busStops[currentBusStopIndex];
		setBusStops(filter.FilterIt(MinskTransSchedMidlet.allBusStopsArray), cur);
		setForeColor();
		MinskTransSchedMidlet.display.setCurrent(this);
	}

	public void setBusStopsFilter(BusStop[] f)
	{
		filter.setBusStopsFilter(f);
		BusStop cur = busStops[currentBusStopIndex];
		setBusStops(filter.FilterIt(MinskTransSchedMidlet.allBusStopsArray), cur);
		setForeColor();
		MinskTransSchedMidlet.display.setCurrent(this);
	}
	
	void setForeColor()
	{
		if(filter.busesFilter == null && filter.busStopsFilter == null)
			foreColor = 255 << 16 | 255 << 8 | 255;
		else
			foreColor = 0 << 16 | 255 << 8 | 0;
	}
}
