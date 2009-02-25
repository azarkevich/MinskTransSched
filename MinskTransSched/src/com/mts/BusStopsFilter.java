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
		super("Фильтр по остановкам", List.MULTIPLE);
		scheduleBoard = board;
		
		setCommandListener(this);
		addCommand(MinskTransSchedMidlet.cmdSelect);
		addCommand(MinskTransSchedMidlet.cmdBack);
		
		append("Все остановки", null);
//		if(scheduleBoard.filter.busStopsFilter == null)
//			setSelectedIndex(0, true);
		
		// TODO: select if selected only favorites
		append("Избр. остановки", Images.hearts);
		
		for (int i = 0; i < MinskTransSchedMidlet.allBusStopsArray.length; i++)
		{
			BusStop bs = MinskTransSchedMidlet.allBusStopsArray[i]; 
			append(bs.name, bs.favorite ? Images.heart : null);
			if(scheduleBoard.filter.busStopsFilter != null && scheduleBoard.filter.busStopsFilter.containsKey(bs))
				setSelectedIndex(i + 2, true);
		}
	}
	
	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == MinskTransSchedMidlet.cmdSelect || cmd == List.SELECT_COMMAND)
		{
			if(isSelected(0))
			{
				scheduleBoard.setBusStopsFilter(null);
			}
			else
			{
				Vector v = new Vector();
				// add favorites if selected
				if(isSelected(1))
				{
					for (int i = 0; i < MinskTransSchedMidlet.allBusStopsArray.length; i++)
					{
						if(MinskTransSchedMidlet.allBusStopsArray[i].favorite)
							v.addElement(MinskTransSchedMidlet.allBusStopsArray[i]);
					}
				}

				// add selected buses
				for (int i = 2; i < this.size(); i++)
				{
					if(this.isSelected(i))
					{
						v.addElement(MinskTransSchedMidlet.allBusStopsArray[i - 2]);
					}
				}
				// copy to array
				BusStop[] busStops = new BusStop[v.size()];
				v.copyInto(busStops);
				scheduleBoard.setBusStopsFilter(busStops);
			}
		}
		else if(cmd == MinskTransSchedMidlet.cmdBack)
		{
			MinskTransSchedMidlet.display.setCurrent(scheduleBoard);
		}
	}
}
