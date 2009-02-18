package com.options;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.mts.MinskTransSchedMidlet;

public class ControlPrefs extends List implements CommandListener
{
	static final Command cmdDescription = new Command("Описание", Command.ITEM, 1);
	
	public void commandAction(Command cmd, Displayable d)
	{
		boolean handled = false;
		if(cmd == cmdDescription)
		{
			Alert a = new Alert("Описание");
			a.setString(all[this.getSelectedIndex()].description);
			MinskTransSchedMidlet.display.setCurrent(a);
			handled = true;
		}
		else if(cmd == MinskTransSchedMidlet.cmdSelect)
		{
			handled = true;
		}
		else if(cmd == MinskTransSchedMidlet.cmdOK)
		{
			// save
		}
		
		if(handled == false)
			parentCL.commandAction(cmd, d);
	}

	CommandListener parentCL;
	CmdDef[] all;
	public ControlPrefs(CommandListener clParent)
	{
		super("Настройки управления", List.IMPLICIT);
		
		parentCL = clParent;
		
		all = CmdDef.getAllCommands();
		for (int i = 0; i < all.length; i++)
		{
			append(all[i].name, null);
		}
		
		addCommand(MinskTransSchedMidlet.cmdSelect);
		addCommand(MinskTransSchedMidlet.cmdOK);
		addCommand(MinskTransSchedMidlet.cmdBack);
		addCommand(cmdDescription);
		
		setCommandListener(this);
	}
}
