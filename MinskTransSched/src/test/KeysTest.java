package test;


import javax.microedition.lcdui.*;

import mts.MultiLineText;

import options.Options;

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
		OnKeyEvent(keyCode, false);
	}
	
	protected void keyRepeated(int keyCode)
	{
		OnKeyEvent(keyCode, true);
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
			case KEY_NUM0:
				return "KEY_NUM0";
			case KEY_NUM1:
				return "KEY_NUM1";
			case KEY_NUM2:
				return "KEY_NUM2";
			case KEY_NUM3:
				return "KEY_NUM3";
			case KEY_NUM4:
				return "KEY_NUM4";
			case KEY_NUM5:
				return "KEY_NUM5";
			case KEY_NUM6:
				return "KEY_NUM6";
			case KEY_NUM7:
				return "KEY_NUM7";
			case KEY_NUM8:
				return "KEY_NUM8";
			case KEY_NUM9:
				return "KEY_NUM9";
			case KEY_STAR:
				return "KEY_STAR";
			case KEY_POUND:
				return "KEY_POUND";
	    }
	    return "<none>";
	}
	
	private void OnKeyEvent(int keyCode, boolean repeated)
	{
		int gameCode = getGameAction(keyCode); 
		String gameName = lookupGameName(gameCode);
		
		text = "#" + keyCode + (repeated ? "*" : "") + ", name: " + getKeyName(keyCode) + ", game: " + gameName + "(" + gameCode +  ")";
		m_MultiLineText.SetTextPar(0, 0, getWidth(), getHeight(),
				Font.getFont(Options.fontFace, Options.fontStyle, Options.fontSize),
				text);
		
		repaint();
	}
}
