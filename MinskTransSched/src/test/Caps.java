package test;


import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.mts.MultiLineText;


import options.Options;

public class Caps extends Canvas
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
	
	String text = "";

	public Caps()
	{
		setFullScreenMode(true);
		
		text = "Canvas:\nDbl Buffer: " + isDoubleBuffered() + 
			"\npointer ev: " + this.hasPointerEvents() + 
			"\nmotion ev: " + this.hasPointerMotionEvents() + 
			"\nrepeat ev: " + this.hasRepeatEvents();
		
	}
	
	public void paint(Graphics g)
	{
		if(m_MultiLineText == null)
		{
			m_MultiLineText = new MultiLineText();
			m_MultiLineText.SetTextPar(0, 0, getWidth(), getHeight(),
					Font.getFont(Options.fontFace, Options.fontStyle, Options.fontSize),
					text);
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
			if (keyCode == getKeyCode(Canvas.UP) || keyCode == '1')
			{
				m_MultiLineText.MoveUp(Options.scrollSize);
				repaint();
			}
			else if (keyCode == getKeyCode(Canvas.DOWN) || keyCode == '4')
			{
				m_MultiLineText.MoveDown(Options.scrollSize);
				repaint();
			}
		}
	}
}
