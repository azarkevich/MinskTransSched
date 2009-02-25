package com.mts;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.resources.Images;

public class BusStopsList extends List implements CommandListener
{
	SchedulerCanvas scheduleBoard;
	BusStop[] busStops;
	public BusStopsList(String title, BusStop[] busStops, SchedulerCanvas board, BusStop sel)
	{
		super(title, List.IMPLICIT);
		scheduleBoard = board;
		this.busStops = busStops;
		
		setCommandListener(this);
		addCommand(MinskTransSchedMidlet.cmdSelect);
		addCommand(MinskTransSchedMidlet.cmdBack);
		
		for (int i = 0; i < busStops.length; i++)
		{
			append(busStops[i].name, busStops[i].favorite ? Images.heart : null);
			if(busStops[i] == sel)
				setSelectedIndex(i, true);
		}
	}
	
	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == MinskTransSchedMidlet.cmdSelect || cmd == List.SELECT_COMMAND)
		{
			int sel = getSelectedIndex();
			if(sel == -1)
			{
				// go back without selection
				MinskTransSchedMidlet.display.setCurrent(scheduleBoard);
			}
			else
			{
				BusStop busStop = busStops[sel];
				scheduleBoard.selectBusStop(busStop);
			}
		}
		else if(cmd == MinskTransSchedMidlet.cmdBack)
		{
			MinskTransSchedMidlet.display.setCurrent(scheduleBoard);
		}
	}
}
