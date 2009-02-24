package com.mts;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

public class BusesFilter extends List implements CommandListener
{
	SchedulerCanvas scheduleBoard;
	public BusesFilter(SchedulerCanvas board)
	{
		super("Автобусы", List.IMPLICIT);
		scheduleBoard = board;
		
		setCommandListener(this);
		addCommand(MinskTransSchedMidlet.cmdSelect);
		addCommand(MinskTransSchedMidlet.cmdBack);
		
		append("Все автобусы", null);
		append("Избр. автобусы", null);
		for (int i = 0; i < MinskTransSchedMidlet.allBusesArray.length; i++)
		{
			append(MinskTransSchedMidlet.allBusesArray[i].name, null);
		}
	}
	
	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == MinskTransSchedMidlet.cmdSelect || cmd == List.SELECT_COMMAND)
		{
			int sel = getSelectedIndex();
			if(sel == -1)
			{
				// TODO: error ??
			}
			else
			{
				if(sel == 0)
				{
					// all buses
					scheduleBoard.setBusesFilter(null);
				}
				else if(sel == 1)
				{
					// TODO: here must be favorited buses
					scheduleBoard.setBusesFilter(null);
				}
				else
				{
					Bus bus = MinskTransSchedMidlet.allBusesArray[sel - 2];
					scheduleBoard.setBusesFilter(new Bus[] { bus });
				}
			}
		}
		else if(cmd == MinskTransSchedMidlet.cmdBack)
		{
			MinskTransSchedMidlet.display.setCurrent(scheduleBoard);
		}
	}
}
