import javax.microedition.lcdui.*;

import options.Options;

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
	
	final static String helpText = 
		"текст вверх/вниз: Up/Down\n" +
		"текст вверх/вниз постранично: Left/Right\n" +
		"предыдущая/следующая остановка: 1/2\n" +
		"выходной/рабочий день: 3\n" +
		"уменьшить/увеличить размер окна: 4/5\n" +
		"сбросить настройки: 6\n" +
		"уменьшить/увеличить сдвиг окна: 7/8\n" +
		"переключить в режим детального описания: 9\n" +
		"занести/вынести в favorites: 0\n" +
		"показывать всё расписание: *";

	public HelpCanvas()
	{
		setFullScreenMode(true);
	}
	
	public void paint(Graphics g)
	{
		if(m_MultiLineText == null)
		{
			m_MultiLineText = new MultiLineText();
			m_MultiLineText.SetTextPar(0, 0, getWidth(), getHeight(),
					Font.getFont(Options.fontFace, Options.fontStyle, Options.fontSize),
					helpText);
		}

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
				m_MultiLineText.MoveUp(Options.scrollSize);
				repaint();
			}
			else if (keyCode == getKeyCode(Canvas.DOWN))
			{
				m_MultiLineText.MoveDown(Options.scrollSize);
				repaint();
			}
		}
	}
}
