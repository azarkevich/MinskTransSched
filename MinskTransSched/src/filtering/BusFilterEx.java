package filtering;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import options.Options;

import ObjModel.Bus;

import text.MultiLineText;

public class BusFilterEx extends Canvas
{
	Displayable prev;
	MultiLineText text;
	public BusFilterEx(Bus[] buses, Displayable prev)
	{
		this.prev = prev;
		text = new MultiLineText();
	}
	
	String currentFilter = "";
	protected void keyPressed(int keyCode)
	{
		if(keyCode < 0)
			return;
		
		if((keyCode >= '0' && keyCode <= '9') || keyCode == '*')
		{
			currentFilter += (char)keyCode;
			
			FilterBuses();
		}
	}
	
	public void paint(Graphics g)
	{
		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(0, 255, 0);
		text.Draw(g);
	}
	
	void FilterBuses()
	{
		text.SetTextPar(
				0, 0, getWidth(), getHeight(), 
				Font.getFont(Options.fontFace, Options.fontStyle, Options.fontSize),
				"Filter: " + currentFilter + "\n");
		
		repaint();
	}
}
