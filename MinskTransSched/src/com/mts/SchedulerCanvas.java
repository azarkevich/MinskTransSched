package com.mts;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.rms.RecordStore;

import com.options.CmdDef;
import com.options.KeyCommands;
import com.options.Options;
import com.options.OptionsListener;


public class SchedulerCanvas extends Canvas implements OptionsListener, CommandListener
{
	static final Command cmdShowBusStopsList = new Command("Остановки", Command.OK, 1); 
	static final Command cmdResetFilter = new Command("Сбросить фильтр", Command.OK, 1); 
	static final Command cmdAddBusFilter = new Command("По автобусу фильтр", Command.OK, 1); 
	static final Command cmdBusStopFilter = new Command("По остановке фильтр", Command.OK, 1); 
	
	BusStop[] busStops = null;
	int currentBusStopIndex = 0;
	
	MultiLineText m_MultiLineText;
	int savedViewportTop;

	ScheduleBuilder m_ScheduleBuilder;
	
	Timer m_RefreshTimer;
	
	boolean fullScreen = false;

	int foreColorR = 0;
	int foreColorG = 255;
	int foreColorB = 0;
	
	public void setForeColor(int r, int g, int b)
	{
		foreColorR = r;
		foreColorG = g;
		foreColorB = b;
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
		m_ScheduleBuilder.Station = busStops[currentBusStopIndex];
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
	
	void showCurrBusStopsList()
	{
		// TODO: select current busstop
		BusStopsList list = new BusStopsList("Остановки", busStops, this); 
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

		fullScreen = Options.fullScreen;
		setFullScreenMode(fullScreen);
		
		busStops = MinskTransSchedMidlet.allBusStopsArray;
		
		m_ScheduleBuilder = new ScheduleBuilder(filter);
		m_ScheduleBuilder.Station = busStops[currentBusStopIndex];
		
		TimerTask tt = new TimerTask()
		{
			public void run()
			{
				RefreshScheduleText(true);
			}
		};
		m_RefreshTimer = new Timer();
		m_RefreshTimer.schedule(tt, 10*1000, 10*1000);

		m_MultiLineText = new MultiLineText();

		RefreshScheduleText();
	}

	public void paint(Graphics g)
	{
		if(m_MultiLineText == null)
			return;

		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(foreColorR, foreColorG, foreColorB);
		m_MultiLineText.DrawMultStr(g);
	}
	
	private void RefreshScheduleText()
	{
		RefreshScheduleText(false);
	}
	
	private void RefreshScheduleText(boolean savePosition)
	{
		if(savePosition && m_MultiLineText != null)
			savedViewportTop = m_MultiLineText.viewportTop;
		else
			savedViewportTop = 0;

		m_MultiLineText.SetTextPar(0, 0, getWidth(), getHeight(),
				Font.getFont(Options.fontFace, Options.fontStyle, Options.fontSize),
				m_ScheduleBuilder.GetScheduleText());
		
		m_MultiLineText.viewportTop = savedViewportTop;

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
			m_MultiLineText.MoveUp(Options.scrollSize);
			repaint();
			return;
		}
		else if(cmd == CmdDef.cmdScrollDown)
		{
			m_MultiLineText.MoveDown(Options.scrollSize);
			repaint();
			return;
		}
		else if(cmd == CmdDef.cmdScrollUpPage)
		{
			m_MultiLineText.PageUp();
			repaint();
			return;
		}
		else if(cmd == CmdDef.cmdScrollDownPage)
		{
			m_MultiLineText.PageDown();
			repaint();
			return;
		}
		else if(cmd == CmdDef.cmdBusStopPrev)
		{
			currentBusStopIndex = (currentBusStopIndex + busStops.length - 1) % busStops.length;
			m_ScheduleBuilder.Station = busStops[currentBusStopIndex];
		}
		else if(cmd == CmdDef.cmdBusStopNext)
		{
			currentBusStopIndex = (currentBusStopIndex + 1) % busStops.length;
			m_ScheduleBuilder.Station = busStops[currentBusStopIndex];
		}
		else if(cmd == CmdDef.cmdWindowDecrease)
		{
			m_ScheduleBuilder.WindowSize -= Options.defWindowSizeStep;
			if(m_ScheduleBuilder.WindowSize < 0)
				m_ScheduleBuilder.WindowSize = 0;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdWindowIncrease)
		{
			m_ScheduleBuilder.WindowSize += Options.defWindowSizeStep;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdWindowShiftDecrease)
		{
			m_ScheduleBuilder.WindowShift -= Options.defWindowShiftStep;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdWindowShiftIncrease)
		{
			m_ScheduleBuilder.WindowShift += Options.defWindowShiftStep;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdToggleDayType)
		{
			m_ScheduleBuilder.ShiftDayType();
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdToggleDetailedDescription)
		{
			m_ScheduleBuilder.showDescription = !m_ScheduleBuilder.showDescription;
		}
		else if(cmd == CmdDef.cmdToggleFavorite)
		{
			try{
				if(m_ScheduleBuilder.Station != null)
				{
					m_ScheduleBuilder.Station.favorite = !m_ScheduleBuilder.Station.favorite;
	
					RecordStore bmBusStops = RecordStore.openRecordStore("bmBusStops", true);
					byte[] rec = new byte[3];
					rec[0] = (byte)(m_ScheduleBuilder.Station.favorite ? 1 : 0);
					rec[1] = (byte)(m_ScheduleBuilder.Station.id / 256);
					rec[2] = (byte)(m_ScheduleBuilder.Station.id % 256);
					if(m_ScheduleBuilder.Station.bookmarkRecord != -1)
						bmBusStops.setRecord(m_ScheduleBuilder.Station.bookmarkRecord, rec, 0, rec.length);
					else
						m_ScheduleBuilder.Station.bookmarkRecord = bmBusStops.addRecord(rec, 0, rec.length);
					
					bmBusStops.closeRecordStore();
				}
			}
			catch(Exception ex)
			{
				// restore
				m_ScheduleBuilder.Station.favorite = !m_ScheduleBuilder.Station.favorite;
			}
		}
		else if(cmd == CmdDef.cmdToggleFullSchedule)
		{
			m_ScheduleBuilder.toggleFullSched(); 
		}
		else if(cmd == CmdDef.cmdScheduleReset)
		{
			m_ScheduleBuilder.WindowShift = Options.defWindowShift;
			m_ScheduleBuilder.WindowSize = Options.defWindowSize;
			m_ScheduleBuilder.UserDayType = ScheduleBuilder.DAY_AUTO;
			m_ScheduleBuilder.schedShift = 0;
			m_ScheduleBuilder.showFull = ScheduleBuilder.SCHED_FULL_NONE;
			m_ScheduleBuilder.showTimeDiff = true;
			m_ScheduleBuilder.showDescription = false;
		}
		else if(cmd == CmdDef.cmdShowBookmarks)
		{
			showCurrBusStopsList();
//			MinskTransSchedMidlet.midlet.commandAction(MinskTransSchedMidlet.cmdShowFavBusStops, this);
		}
		else if(cmd == CmdDef.cmdShowAllBusStops)
		{
//			MinskTransSchedMidlet.midlet.commandAction(MinskTransSchedMidlet.cmdShowAllBusStops, this);
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
			m_ScheduleBuilder.schedShift--;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdSchedShiftIncrease)
		{
			m_ScheduleBuilder.schedShift++;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdSchedShiftDecrease10)
		{
			m_ScheduleBuilder.schedShift -= 10;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdSchedShiftIncrease10)
		{
			m_ScheduleBuilder.schedShift+=10;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdToggleSchedShowTimeDiff)
		{
			m_ScheduleBuilder.showTimeDiff = !m_ScheduleBuilder.showTimeDiff;
			RefreshScheduleText(true);
			return;
		}
		else if(cmd == CmdDef.cmdToggleBusFlow)
		{
			m_ScheduleBuilder.showBusFlow = !m_ScheduleBuilder.showBusFlow;
		}

		RefreshScheduleText();
	}
	
	public void OptionsUpdated()
	{
		m_ScheduleBuilder.WindowShift = Options.defWindowShift;
		m_ScheduleBuilder.WindowSize = Options.defWindowSize;
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
		MinskTransSchedMidlet.display.setCurrent(this);
	}

	public void setBusStopsFilter(BusStop[] f)
	{
		filter.setBusStopsFilter(f);
		BusStop cur = busStops[currentBusStopIndex];
		setBusStops(filter.FilterIt(MinskTransSchedMidlet.allBusStopsArray), cur);
		MinskTransSchedMidlet.display.setCurrent(this);
	}
}
