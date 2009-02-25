package com.mts;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.resources.Images;

public class BusesFilter extends List implements CommandListener
{
	SchedulerCanvas scheduleBoard;
	public BusesFilter(SchedulerCanvas board)
	{
		super("Автобусы", List.MULTIPLE);
		scheduleBoard = board;
		
		setCommandListener(this);
		addCommand(MinskTransSchedMidlet.cmdSelect);
		addCommand(MinskTransSchedMidlet.cmdBack);

		append("Все автобусы", null);
//		if(scheduleBoard.filter.busesFilter == null)
//			setSelectedIndex(0, true);
		
		append("Избр. автобусы", Images.hearts);
		
		for (int i = 0; i < MinskTransSchedMidlet.allBusesArray.length; i++)
		{
			Bus b = MinskTransSchedMidlet.allBusesArray[i];
			append(b.name, null);
			if(scheduleBoard.filter.busesFilter != null && scheduleBoard.filter.busesFilter.containsKey(b))
				setSelectedIndex(i + 2, true);
		}
	}
	
	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == MinskTransSchedMidlet.cmdSelect || cmd == List.SELECT_COMMAND)
		{
			if(isSelected(0))
			{
				scheduleBoard.setBusesFilter(null);
			}
			else
			{
				Vector v = new Vector();
				if(isSelected(1))
				{
					// TODO: add fav buses
				}
				// add selected buses
				for (int i = 2; i < this.size(); i++)
				{
					if(this.isSelected(i))
					{
						v.addElement(MinskTransSchedMidlet.allBusesArray[i - 2]);
					}
				}
				// copy to array
				Bus[] buses = new Bus[v.size()];
				v.copyInto(buses);
				scheduleBoard.setBusesFilter(buses);
			}
		}
		else if(cmd == MinskTransSchedMidlet.cmdBack)
		{
			MinskTransSchedMidlet.display.setCurrent(scheduleBoard);
		}
	}
}
