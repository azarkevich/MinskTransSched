package com.mts;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.resources.Images;

public class MainFilterMenu extends List implements CommandListener
{
	static final Command cmdBusFilter = new Command("По автобусу фильтр", Command.OK, 2); 
	static final Command cmdBusStopFilter = new Command("По остановке фильтр", Command.OK, 2); 
	static final Command cmdResetFilter = new Command("Сбросить фильтр", Command.OK, 3); 

	SchedulerCanvas schedBoard;

	public MainFilterMenu(SchedulerCanvas schedBoard)
	{
		super("Фильтр", List.IMPLICIT);

		this.schedBoard = schedBoard;

		setCommandListener(this);
		
		addCommand(MinskTransSchedMidlet.cmdBack);
		addCommand(MinskTransSchedMidlet.cmdSelect);
		addCommand(cmdResetFilter);
		addCommand(cmdBusStopFilter);
		addCommand(cmdBusFilter);
		
		append("Сбросить", Images.stop);
		append("По избранным", Images.hearts);
		append("По автобусу", Images.bus);
		append("По остановке", null);
	}

	public void commandAction(Command c, Displayable d)
	{
		if(c == MinskTransSchedMidlet.cmdBack)
		{
			MinskTransSchedMidlet.display.setCurrent(schedBoard);
		}
		else if(c == MinskTransSchedMidlet.cmdSelect || c == List.SELECT_COMMAND)
		{
			List l = (List)d;
			int sel = l.getSelectedIndex();
			if(sel == -1)
				return;
			
			switch (sel)
			{
			case 0:
				schedBoard.resetFilter();
				MinskTransSchedMidlet.display.setCurrent(schedBoard);
				break;
			case 1:
				schedBoard.setFilterToFavorites();
				MinskTransSchedMidlet.display.setCurrent(schedBoard);
				break;
			case 2:
				schedBoard.showBusesFilter();
				break;
			case 3:
				schedBoard.showBusStopsFilter();
				break;
			}
		}
	}
}
