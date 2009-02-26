package com.options;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.mts.MinskTransSchedMidlet;
import com.mts.SchedulerCanvas;

public class OptionsMenu extends List implements CommandListener
{
	static final Command cmdGeneral = new Command("Основные", Command.OK, 2); 
	static final Command cmdControl = new Command("Управление", Command.OK, 2); 

	SchedulerCanvas schedBoard;

	public OptionsMenu(SchedulerCanvas schedBoard)
	{
		super("Настройки", List.IMPLICIT);

		this.schedBoard = schedBoard;

		setCommandListener(this);
		
		addCommand(MinskTransSchedMidlet.cmdBack);
		addCommand(MinskTransSchedMidlet.cmdSelect);
		addCommand(cmdGeneral);
		addCommand(cmdControl);
		
		append("Основные", null);
		append("Управление", null);
	}

	public void commandAction(Command c, Displayable d)
	{
		if(c == MinskTransSchedMidlet.cmdBack)
		{
			MinskTransSchedMidlet.display.setCurrent(schedBoard);
		}
		else if(c == MinskTransSchedMidlet.cmdSelect || c == List.SELECT_COMMAND)
		{
			if(((List)d).getSelectedIndex() == 0)
				MinskTransSchedMidlet.display.setCurrent(new GeneralPrefs(this));
			else
				MinskTransSchedMidlet.display.setCurrent(new ControlPrefs(this));
		}
		else if(c == cmdGeneral)
		{
			MinskTransSchedMidlet.display.setCurrent(new GeneralPrefs(this));
		}
		else if(c == cmdControl)
		{
			MinskTransSchedMidlet.display.setCurrent(new ControlPrefs(this));
		}
	}
}
