package com.mts;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.rms.RecordStore;

import com.options.CmdDef;
import com.options.KeyCommands;
import com.options.Options;
import com.options.OptionsListener;


public class SchedulerCanvas extends Canvas implements OptionsListener
{
	BusStop[] busStops = null;
	
	MultiLineText m_MultiLineText;
	int savedViewportTop;

	ScheduleBuilder m_ScheduleBuilder;
	
	int m_CurrentSchedule = 0;
	
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
	
	public void setBusStation(int index)
	{
		m_CurrentSchedule = index;
		m_ScheduleBuilder.Station = busStops[index];
		RefreshScheduleText();
	}
	
	public void setBusStops(BusStop[] stops)
	{
		busStops = stops;
		m_CurrentSchedule = 0;
		RefreshScheduleText();
	}
	
	public int getBusStation()
	{
		return m_CurrentSchedule;
	}

	public SchedulerCanvas(BusStop[] stops)
	{
		fullScreen = Options.fullScreen;
		setFullScreenMode(fullScreen);
		
		busStops = stops;
		
		m_ScheduleBuilder = new ScheduleBuilder();
		m_ScheduleBuilder.Station = busStops[m_CurrentSchedule];
		
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
			m_CurrentSchedule = (m_CurrentSchedule + busStops.length - 1) % busStops.length;
			m_ScheduleBuilder.Station = busStops[m_CurrentSchedule];
		}
		else if(cmd == CmdDef.cmdBusStopNext)
		{
			m_CurrentSchedule = (m_CurrentSchedule + 1) % busStops.length;
			m_ScheduleBuilder.Station = busStops[m_CurrentSchedule];
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
					m_ScheduleBuilder.Station.bookmarked = !m_ScheduleBuilder.Station.bookmarked;
	
					RecordStore bmBusStops = RecordStore.openRecordStore("bmBusStops", true);
					byte[] rec = new byte[3];
					rec[0] = (byte)(m_ScheduleBuilder.Station.bookmarked ? 1 : 0);
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
				m_ScheduleBuilder.Station.bookmarked = !m_ScheduleBuilder.Station.bookmarked;
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
			MinskTransSchedMidlet.midlet.commandAction(MinskTransSchedMidlet.cmdShowBookMarks, this);
		}
		else if(cmd == CmdDef.cmdShowAllBusStops)
		{
			MinskTransSchedMidlet.midlet.commandAction(MinskTransSchedMidlet.cmdShowAllBusStops, this);
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
}
