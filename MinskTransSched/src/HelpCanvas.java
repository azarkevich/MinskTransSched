import javax.microedition.lcdui.*;

public class HelpCanvas extends Canvas
{
	MultiLineText m_MultiLineText;

	int foreColorR = 0;
	int foreColorG = 255;
	int foreColorB = 0;
	
	public void setForeColor(int r, int g, int b)
	{
		foreColorR = r;
		foreColorG = g;
		foreColorB = b;
	}
	
	final static String helpText = "Up/Down - текст вверх/вниз\n" +
		"Left/Right - текст вверх/вниз построчно\n" +
		"1/2 - предыдущая/следующая остановка\n" +
		"3   - выходной/рабочий день\n" +
		"4/5 - уменьшить/увеличить размер окна\n" +
		"6   - сбросить настройки\n" +
		"7/8 - уменьшить/увеличить сдвиг окна\n" +
		"9   - переключить описания остановок\n" +
		"0   - отметить остановку\n" +
		"#   - изменить размер шрифта\n" +
		"*   - показывать всё расписание";

	public HelpCanvas()
	{
		m_MultiLineText = new MultiLineText();
		m_MultiLineText.SetTextPar(0, 0, getWidth(), getHeight(),
				Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL),
				helpText);
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
