package com.options;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.StringItem;

import com.mts.MultiLineText;

public class DefineKey extends Canvas implements CommandListener
{
	static final Command cmdEditActionType = new Command("Дополнительно", Command.OK, 1); 
	static final Command cmdClear = new Command("Очистить", Command.OK, 1); 

	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == cmdEditActionType)
		{
			
		}
		else if(cmd == cmdClear)
		{
			hash = 0;
			multiLineText = null;
			repaint();
		}
		else
		{
			parentCL.commandAction(cmd, d);
		}
	}

	public int hash;

	MultiLineText multiLineText;
	CmdDef cmd;
	StringItem item;
	
	CommandListener parentCL;

	public DefineKey(CommandListener parent)
	{
		this.setTitle("Определение клавиши");
		
		this.addCommand(cmdEditActionType);
		this.addCommand(cmdClear);
		this.setCommandListener(this);
		
		parentCL = parent;
	}
	
	public void setData(CmdDef cmd, int hash, StringItem item)
	{
		this.cmd = cmd;
		this.hash = hash;
		this.item = item;
		
		multiLineText = null;
	}

	protected void keyPressed(int keyCode)
	{
		hash = KeyCommands.getKeyHash(
				keyCode,
				false,
				KeyCommands.getActionCodeFromKeyHash(hash)
			).intValue();

		multiLineText = null;
		repaint();
	}

	protected void paint(Graphics g)
	{
		if(multiLineText == null)
		{
			int canvasHeight = getHeight();
			String text = "Выберите клавишу для действия\n\n'" + cmd.name + "'";
			
			int keyCode = KeyCommands.getKeyCodeFromKeyHash(hash);
			if(keyCode !=0 && KeyCommands.getIsGameCodeFromKeyHash(hash))
			{
				keyCode = getKeyCode(keyCode);
			}

			if(keyCode != 0)
			{
				text = text + "\n\n>>" + getKeyName(keyCode) + "<<";
			}
			else
			{
				text = text + "\n\n<нет>";
			}
			
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
