package com.mts;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.resources.Images;

public class BusStopsFilter extends List implements CommandListener
{
	SchedulerCanvas scheduleBoard;
	public BusStopsFilter(SchedulerCanvas board)
	{
		super("Фильтр по остановкам", List.IMPLICIT);
		scheduleBoard = board;
		
		setCommandListener(this);
		addCommand(MinskTransSchedMidlet.cmdSelect);
		addCommand(MinskTransSchedMidlet.cmdBack);
		
		append("Все остановки", null);
		append("Избр. остановки", null);
		for (int i = 0; i < MinskTransSchedMidlet.allBusStopsArray.length; i++)
		{
			append(MinskTransSchedMidlet.allBusStopsArray[i].name, MinskTransSchedMidlet.allBusStopsArray[i].favorite ? Images.heart : null);
		}
	}
	
	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == MinskTransSchedMidlet.cmdSelect || cmd == List.SELECT_COMMAND)
		{
			int sel = getSelectedIndex();
			if(sel == -1)
			{
				// TODO: error
			}
			else
			{
				if(sel == 0)
				{
					// all buses
					scheduleBoard.setBusStopsFilter(null);
				}
				else if(sel == 1)
				{
					// TODO: here must be favorited buses
					Vector favV = new Vector();
					for (int i = 0; i < MinskTransSchedMidlet.allBusStopsArray.length; i++)
					{
						if(MinskTransSchedMidlet.allBusStopsArray[i].favorite)
							favV.addElement(MinskTransSchedMidlet.allBusStopsArray[i]);
					}
					BusStop[] fav = new BusStop[favV.size()];
					favV.copyInto(fav);
					scheduleBoard.setBusStopsFilter(fav);
				}
				else
				{
					BusStop busStop = MinskTransSchedMidlet.allBusStopsArray[sel - 2];
					scheduleBoard.setBusStopsFilter(new BusStop[] { busStop });
				}
			}
		}
		else if(cmd == MinskTransSchedMidlet.cmdBack)
		{
			MinskTransSchedMidlet.display.setCurrent(scheduleBoard);
		}
	}
}
