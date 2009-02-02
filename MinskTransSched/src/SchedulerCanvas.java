import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class SchedulerCanvas extends Canvas
{
	private MultiLineText m_MultiLineText;
	private int m_Top;
	private boolean m_RefreshSchedule;
	private String m_Text;

	private ScheduleBuilder m_ScheduleBuilder;
	
	private int m_CurrentSchedule = 0;
	private ScheduleLoader m_SchedulesLoader;
	
	Timer m_RefreshTimer;
	
	public class RefresherTask extends TimerTask
	{
		public RefresherTask(SchedulerCanvas refreshee)
		{
			m_refreshee = refreshee; 
		}

		SchedulerCanvas m_refreshee;
		public void run()
		{
			m_refreshee.Refresh();
		}
	}

	public SchedulerCanvas()
	{
		m_SchedulesLoader = new ScheduleLoader();
		m_SchedulesLoader.Load();
		
		m_ScheduleBuilder = new ScheduleBuilder();
		m_ScheduleBuilder.Station = m_SchedulesLoader.busStops[m_CurrentSchedule];
		RefreshScheduleText();
		
		m_RefreshTimer = new Timer();
		m_RefreshTimer.schedule(new RefresherTask(this), 10*1000, 10*1000);
	}
	
	public void Refresh()
	{
		RefreshScheduleText(true);
	}
	
	int m_FontSize = Font.SIZE_SMALL;

	public void paint(Graphics g)
	{
		if (m_MultiLineText == null)
		{
			m_MultiLineText = new MultiLineText();
			m_RefreshSchedule = true;
		}

		if(m_RefreshSchedule)
		{
			m_RefreshSchedule = false;
			m_MultiLineText.SetTextPar(0, 0, getWidth(), getHeight(),
					5,
					m_FontSize, Font.STYLE_PLAIN, Font.FACE_PROPORTIONAL,
					g, m_Text);
			m_MultiLineText.Top = m_Top;
		}
		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(0, 255, 0);         
		m_MultiLineText.DrawMultStr(g);
	}
	
	private void SetText(String text)
	{
		m_Top = 0;
		m_Text = text;
		m_RefreshSchedule = true;
		repaint();
	}
	
	private void RefreshScheduleText()
	{
		RefreshScheduleText(false);
	}
	
	private void RefreshScheduleText(boolean savePosition)
	{
		if(savePosition)
			m_Top = m_MultiLineText.Top;
		else
			m_Top = 0;
		SetText(m_ScheduleBuilder.GetScheduleText());
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
			m_CurrentSchedule = (m_CurrentSchedule + m_SchedulesLoader.busStops.length - 1) % m_SchedulesLoader.busStops.length;
			m_ScheduleBuilder.Station = m_SchedulesLoader.busStops[m_CurrentSchedule];
			RefreshScheduleText();
			return;
		case KEY_NUM2:
			m_CurrentSchedule = (m_CurrentSchedule + 1) % m_SchedulesLoader.busStops.length;
			m_ScheduleBuilder.Station = m_SchedulesLoader.busStops[m_CurrentSchedule];
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
			m_ScheduleBuilder.Station = m_SchedulesLoader.busStops[m_CurrentSchedule];
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
			RefreshScheduleText();
			return;
		case KEY_NUM0:
			RefreshScheduleText();
			return;
			
		case KEY_STAR:
			SetText( 
					"Up/Down текст вверх/вниз\n" +
					"Left/Right - текст вверх/вниз построчно\n" +
					"1/2 - предыдущая/следующая остановка\n" +
					"3   - выходной/рабочий день\n" +
					"4/5 - уменьшить/увеличить размер окна\n" +
					"6   - сбросить настройки\n" +
					"7/8 - уменьшить/увеличить сдвиг окна\n" +
					"#   - изменить размер шрифта\n" +
					"*   - помощь\n"
					);
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
			if (keyCode == getKeyCode(UP))
			{
				m_MultiLineText.MoveUp();
				repaint();
			}
			else if (keyCode == getKeyCode(DOWN))
			{
				m_MultiLineText.MoveDown();
				repaint();
			}
			else if (keyCode == getKeyCode(LEFT))
			{
				m_MultiLineText.PageUp();
				repaint();
			}
			else if (keyCode == getKeyCode(RIGHT))
			{
				m_MultiLineText.PageDown();
				repaint();
			}
		}
	}
}
