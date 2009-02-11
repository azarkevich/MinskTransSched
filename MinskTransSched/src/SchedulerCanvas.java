import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.rms.RecordStore;

public class SchedulerCanvas extends Canvas
{
	MultiLineText m_MultiLineText;
	int savedViewportTop;

	ScheduleBuilder m_ScheduleBuilder;
	
	int m_CurrentSchedule = 0;
	
	Timer m_RefreshTimer;
	
	private MinskTransSchedMidlet m_MIDlet;
	
	public void setBusStation(int index)
	{
		m_CurrentSchedule = index;
		m_ScheduleBuilder.Station = m_MIDlet.busStops[index];
		RefreshScheduleText();
	}
	
	public int getBusStation()
	{
		return m_CurrentSchedule;
	}

	public SchedulerCanvas(MinskTransSchedMidlet m)
	{
		m_MIDlet = m;
		
		m_ScheduleBuilder = new ScheduleBuilder();
		m_ScheduleBuilder.Station = m_MIDlet.busStops[m_CurrentSchedule];
		
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
	
	int m_FontSize = Font.SIZE_SMALL;

	public void paint(Graphics g)
	{
		if(m_MultiLineText == null)
			return;

		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(0, 255, 0);
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
				Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, m_FontSize),
				m_ScheduleBuilder.GetScheduleText());
		
		m_MultiLineText.viewportTop = savedViewportTop;

		repaint();
	}

	protected void keyPressed(int keyCode)
	{
		OnKeyEvent(keyCode, false);
	}
	
	protected void keyRepeated(int keyCode)
	{
		OnKeyEvent(keyCode, true);
	}
	
	private void OnKeyEvent(int keyCode, boolean repeated)
	{
		/*
		m_SchedText = "KeyName: " + getKeyName(keyCode) + "\n";
		m_SchedText += "KeyCode: " + keyCode + "\n";
		m_SchedText += "GameAction: " + getGameAction(keyCode) + "\n"; 
		m_MultiLineText = null;
		repaint();
		return;
		*/

		switch (keyCode)
		{
		case KEY_NUM1:
			m_CurrentSchedule = (m_CurrentSchedule + m_MIDlet.busStops.length - 1) % m_MIDlet.busStops.length;
			m_ScheduleBuilder.Station = m_MIDlet.busStops[m_CurrentSchedule];
			RefreshScheduleText();
			return;
		case KEY_NUM2:
			m_CurrentSchedule = (m_CurrentSchedule + 1) % m_MIDlet.busStops.length;
			m_ScheduleBuilder.Station = m_MIDlet.busStops[m_CurrentSchedule];
			RefreshScheduleText();
			return;

		case KEY_NUM3:
			m_ScheduleBuilder.ShiftDayType();
			RefreshScheduleText();
			return;
			
		case KEY_NUM4:
			m_ScheduleBuilder.WindowSize -= 10;
			if(m_ScheduleBuilder.WindowSize < 0)
				m_ScheduleBuilder.WindowSize = 0;
			RefreshScheduleText();
			return;
		case KEY_NUM5:
			m_ScheduleBuilder.WindowSize += 10;
			RefreshScheduleText();
			return;
			
		case KEY_NUM6:
			m_CurrentSchedule = 0;
			m_ScheduleBuilder.Station = m_MIDlet.busStops[m_CurrentSchedule];
			m_ScheduleBuilder.WindowShift = ScheduleBuilder.DEFAULT_WINDOW_SHIFT;
			m_ScheduleBuilder.WindowSize = ScheduleBuilder.DEFAULT_WINDOW_SIZE;
			m_FontSize = Font.SIZE_SMALL;
			m_MultiLineText = null;
			m_ScheduleBuilder.UserDayType = ScheduleBuilder.DAY_AUTO;
			RefreshScheduleText();
			return;
			
		case KEY_NUM7:
			m_ScheduleBuilder.WindowShift -= 10;
			RefreshScheduleText();
			return;
		case KEY_NUM8:
			m_ScheduleBuilder.WindowShift += 10;
			RefreshScheduleText();
			return;
			
		case KEY_NUM9:
			m_ScheduleBuilder.showDescription = !m_ScheduleBuilder.showDescription;
			RefreshScheduleText();
			return;
		case KEY_NUM0:
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
			RefreshScheduleText();
			return;
			
		case KEY_STAR:
			RefreshScheduleText();
			return;

		case KEY_POUND:
			if(m_FontSize == Font.SIZE_SMALL)
				m_FontSize = Font.SIZE_MEDIUM;
			else if(m_FontSize == Font.SIZE_MEDIUM)
				m_FontSize = Font.SIZE_LARGE;
			else if(m_FontSize == Font.SIZE_LARGE)
				m_FontSize = Font.SIZE_SMALL;

			m_MultiLineText = null;
			RefreshScheduleText();
			return;
		}

		if(m_MultiLineText != null)
		{
			if (keyCode == getKeyCode(Canvas.UP))
			{
				m_MultiLineText.MoveUp();
				repaint();
			}
			else if (keyCode == getKeyCode(Canvas.DOWN))
			{
				m_MultiLineText.MoveDown();
				repaint();
			}
			else if (keyCode == getKeyCode(Canvas.LEFT))
			{
				m_MultiLineText.PageUp();
				repaint();
			}
			else if (keyCode == getKeyCode(Canvas.RIGHT))
			{
				m_MultiLineText.PageDown();
				repaint();
			}
		}
	}
}
