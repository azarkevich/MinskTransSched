package com.options;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import com.mts.MinskTransSchedMidlet;
import com.mts.MultiLineText;

public class DefineKey extends Canvas
{
	ControlPrefs parent;
	MultiLineText multiLineText;
	CmdDef cmd;
	int index;
	public int newHash;
	
	public DefineKey(ControlPrefs parent)
	{
		this.setTitle("Определение клавиши");
		this.parent = parent;
	}
	
	public void setData(CmdDef cmd, int index)
	{
		this.cmd = cmd;
		newHash = cmd.getKeyHash();
		this.index = index;
		multiLineText = null;
		repaint();
	}

	protected void keyPressed(int keyCode)
	{
		newHash = CmdDef.getKeyHash(
				keyCode,
				false,
				CmdDef.getActionCodeFromKeyHash(cmd.getKeyHash())
			).intValue();

		MinskTransSchedMidlet.display.setCurrent(parent);
		
		parent.onKeyAssigned(cmd, index, newHash);
	}

	protected void paint(Graphics g)
	{
		if(multiLineText == null)
		{
			int canvasHeight = getHeight();
			String text = "Выберите клавишу для действия\n\n'" + cmd.name + "'"+ "\n\n" +
				cmd.getKeyHashName(true, "<нет>");
			
			multiLineText = new MultiLineText();
			multiLineText.SetTextPar(0, 0, getWidth(), canvasHeight, g.getFont(), 
					text);
			
			if(multiLineText.textHeight < canvasHeight)
			{
				multiLineText.viewportTop = (canvasHeight - multiLineText.textHeight) / 2; 
			}
			
			multiLineText.justifyCenter = true;
		}
		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(0, 255, 0);
		multiLineText.DrawMultStr(g);
	}
}
