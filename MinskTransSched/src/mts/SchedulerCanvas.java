package mts;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.rms.RecordStore;

import options.KeyCommands;
import options.Options;
import options.OptionsListener;

public class SchedulerCanvas extends Canvas implements OptionsListener
{
	BusStop[] busStops = null;
	
	MultiLineText m_MultiLineText;
	int savedViewportTop;

	ScheduleBuilder m_ScheduleBuilder;
	
	int m_CurrentSchedule = 0;
	
	Timer m_RefreshTimer;

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
		setFullScreenMode(Options.fullScreen);
		
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
		boolean isGameCode = (keyCode < 0);
		if(isGameCode)
			keyCode = getGameAction(keyCode);
		
		int cmd = KeyCommands.mapKeyToCommand(keyCode, isGameCode, released, repeated);
		
		if(cmd != KeyCommands.CMD_NONE)
			handleCmd(cmd);
	}
	
	void handleCmd(int cmd)
	{
		switch(cmd)
		{
		case KeyCommands.CMD_SCROLL_UP:
			m_MultiLineText.MoveUp(Options.scrollSize);
			repaint();
			return;
		case KeyCommands.CMD_SCROLL_DOWN:
			m_MultiLineText.MoveDown(Options.scrollSize);
			repaint();
			return;

		case KeyCommands.CMD_BUSSTOP_PREV:
			m_CurrentSchedule = (m_CurrentSchedule + busStops.length - 1) % busStops.length;
			m_ScheduleBuilder.Station = busStops[m_CurrentSchedule];
			break;

		case KeyCommands.CMD_BUSSTOP_NEXT:
			m_CurrentSchedule = (m_CurrentSchedule + 1) % busStops.length;
			m_ScheduleBuilder.Station = busStops[m_CurrentSchedule];
			break;

		case KeyCommands.CMD_TOGGLE_DAY:
			m_ScheduleBuilder.ShiftDayType();
			break;

		case KeyCommands.CMD_RESET_SCHEDULE:
			m_CurrentSchedule = 0;
			m_ScheduleBuilder.Station = busStops[m_CurrentSchedule];
			m_ScheduleBuilder.WindowShift = Options.defWindowShift;
			m_ScheduleBuilder.WindowSize = Options.defWindowSize;
			m_ScheduleBuilder.UserDayType = ScheduleBuilder.DAY_AUTO;
			m_ScheduleBuilder.showFull = false;
			break;

		case KeyCommands.CMD_DESCREASE_WINDOW:
			m_ScheduleBuilder.WindowSize -= Options.defWindowSizeStep;
			if(m_ScheduleBuilder.WindowSize < 0)
				m_ScheduleBuilder.WindowSize = 0;
			break;
		case KeyCommands.CMD_INCREASE_WINDOW:
			m_ScheduleBuilder.WindowSize += Options.defWindowSizeStep;
			break;
		case KeyCommands.CMD_SHIFT_WINDOW_LEFT:
			m_ScheduleBuilder.WindowShift -= Options.defWindowShiftStep;
			break;
		case KeyCommands.CMD_SHIFT_WINDOW_RIGHT:
			m_ScheduleBuilder.WindowShift += Options.defWindowShiftStep;
			break;
		case KeyCommands.CMD_TOGGLE_DETAILED_DESCRIPTION:
			m_ScheduleBuilder.showDescription = !m_ScheduleBuilder.showDescription;
			break;
			
		case KeyCommands.CMD_TOGGLE_FAVORITE:
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
			break;
			
		case KeyCommands.CMD_TOGGLE_FULL_SCHED:
			m_ScheduleBuilder.showFull = !m_ScheduleBuilder.showFull; 
			break;
			
		case KeyCommands.CMD_SHOW_BOOKMARKS:
			MinskTransSchedMidlet.midlet.commandAction(MinskTransSchedMidlet.cmdShowBookMarks, this);
			break;

		case KeyCommands.CMD_SHOW_BUSSTOPS:
			MinskTransSchedMidlet.midlet.commandAction(MinskTransSchedMidlet.cmdShowAllBusStops, this);
			break;

		case KeyCommands.CMD_TOGGLE_FULLSCREEN:
			Options.fullScreen = !Options.fullScreen; 
			setFullScreenMode(Options.fullScreen);
			break;

		default:
			return;
		}
		RefreshScheduleText();
	}
	
	public void OptionsUpdated()
	{
		m_ScheduleBuilder.WindowShift = Options.defWindowShift;
		m_ScheduleBuilder.WindowSize = Options.defWindowSize;
		RefreshScheduleText(true);
	}
}
