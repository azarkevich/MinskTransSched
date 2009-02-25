package com.test;


import javax.microedition.lcdui.*;

import com.options.Options;
import com.text.MultiLineText;

public class KeysTest extends Canvas
{
	MultiLineText m_MultiLineText;

	String text = "";
	
	public KeysTest()
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
					text);
		}

		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(0, 255, 255);
		m_MultiLineText.DrawMultStr(g);
	}

	protected void keyPressed(int keyCode)
	{
		OnKeyEvent(keyCode, true, false);
	}
	
	protected void keyRepeated(int keyCode)
	{
		OnKeyEvent(keyCode, true, true);
	}
	
	protected void keyReleased(int keyCode)
	{
		OnKeyEvent(keyCode, false, false);
	}

	String lookupGameName(int gameCode)
	{
		switch(gameCode)
		{
			case UP:
				return "UP";
			case DOWN:
				return "DOWN";
			case LEFT:
				return "LEFT";
			case RIGHT:
				return "RIGH";
			case FIRE:
				return "FIRE";
			case GAME_A:
				return "GAME_A";
			case GAME_B:
				return "GAME_B";
			case GAME_C:
				return "GAME_C";
			case GAME_D:
				return "GAME_D";
	    }
	    return "<none>";
	}
	
	private void OnKeyEvent(int keyCode, boolean pressed, boolean repeated)
	{
		int gameCode = getGameAction(keyCode); 
		String gameName = lookupGameName(gameCode);
		
		text = text + "\n" + (pressed ? "v" : "^") + (repeated ? "*" : " ") + " Code: " + keyCode + " (" + getKeyName(keyCode) +  ")" +
			" Game: " + gameCode + " (" + gameName + ")";
		m_MultiLineText.SetTextPar(0, 0, getWidth(), getHeight(),
				Font.getFont(Options.fontFace, Options.fontStyle, Options.fontSize),
				text);
		
		m_MultiLineText.MoveEnd(); 
		
		repaint();
	}
}
