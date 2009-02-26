package com.options;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.mts.BusStopsFilter;
import com.mts.BusesFilter;
import com.mts.MinskTransSchedMidlet;
import com.mts.SchedulerCanvas;
import com.resources.Images;

public class OptionsMenu extends List implements CommandListener
{
	static final Command cmdGeneral = new Command("Основные", Command.OK, 2); 
	static final Command cmdControl = new Command("Управление", Command.OK, 2); 
	static final Command cmdFavBuses = new Command("Трансп. избранное", Command.OK, 2); 
	static final Command cmdFavBusStops = new Command("Останов. избранное", Command.OK, 2); 

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
		
		append(cmdGeneral.getLabel(), null);
		append(cmdControl.getLabel(), null);

		append(cmdFavBuses.getLabel(), Images.hearts);
		append(cmdFavBusStops.getLabel(), Images.hearts);
	}

	public void commandAction(Command c, Displayable d)
	{
		if(c == MinskTransSchedMidlet.cmdBack)
		{
			MinskTransSchedMidlet.display.setCurrent(schedBoard);
		}
		else if(c == MinskTransSchedMidlet.cmdSelect || c == List.SELECT_COMMAND)
		{
			switch (((List)d).getSelectedIndex())
			{
			case 0:
				MinskTransSchedMidlet.display.setCurrent(new GeneralPrefs(this));
				break;
			case 1:
				MinskTransSchedMidlet.display.setCurrent(new ControlPrefs(this));
				break;
			case 2:
				MinskTransSchedMidlet.display.setCurrent(new BusesFilter(schedBoard, true));
				break;
			case 3:
				MinskTransSchedMidlet.display.setCurrent(new BusStopsFilter(schedBoard, true));
				break;
			}
		}
		else if(c == cmdGeneral)
		{
			MinskTransSchedMidlet.display.setCurrent(new GeneralPrefs(this));
		}
		else if(c == cmdControl)
		{
			MinskTransSchedMidlet.display.setCurrent(new ControlPrefs(this));
		}
		else if(c == cmdFavBuses)
		{
			MinskTransSchedMidlet.display.setCurrent(new BusesFilter(schedBoard, true));
		}
		else if(c == cmdFavBusStops)
		{
			MinskTransSchedMidlet.display.setCurrent(new BusStopsFilter(schedBoard, true));
		}
	}
}
