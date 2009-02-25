package com.mts;
import javax.microedition.lcdui.*;

import com.options.Options;
import com.text.MultiLineText;


public class HelpCanvasSimple extends Canvas implements CommandListener
{
	public void commandAction(Command command, Displayable displayable)
	{
		MinskTransSchedMidlet.display.setCurrent(next);
	}

	MultiLineText multiLineText;

	public String text; 
	Displayable next;
	public HelpCanvasSimple(String text, Displayable next)
	{
		this.text = text;
		this.next = next;
		addCommand(MinskTransSchedMidlet.cmdBack);
		setCommandListener(this);
		setFullScreenMode(Options.fullScreen);
	}
	
	public void paint(Graphics g)
	{
		if(multiLineText == null)
		{
			multiLineText = new MultiLineText();
			multiLineText.SetTextPar(0, 0, getWidth(), getHeight(),
					Font.getFont(Options.fontFace, Options.fontStyle, Options.fontSize),
					text);
		}

		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(0, 255, 0);
		multiLineText.DrawMultStr(g);
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
		if(multiLineText != null)
		{
			if (keyCode == getKeyCode(Canvas.UP))
			{
				multiLineText.MoveUp(Options.scrollSize);
			}
			else if (keyCode == getKeyCode(Canvas.DOWN))
			{
				multiLineText.MoveDown(Options.scrollSize);
			}
			else
			{
				multiLineText.MoveDown(1);
			}
			repaint();
		}
	}
}
