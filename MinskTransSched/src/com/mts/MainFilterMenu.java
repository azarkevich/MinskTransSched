package com.mts;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.resources.Images;

public class MainFilterMenu extends List implements CommandListener
{
	SchedulerCanvas schedBoard;
	
	int byBus = -1;
	int byBusStop = -1;
	int clearFilter = -1;
	int closeMenu = -1;
	int favorites = -1;
	
	int predefFiltersStart = -1;
	int predefFiltersEnd = -1;
	
	int customFiltersStart = -1;
	int customFiltersEnd = -1;

	public MainFilterMenu(SchedulerCanvas schedBoard)
	{
		super("Фильтр", List.IMPLICIT);

		this.schedBoard = schedBoard;

		setCommandListener(this);
		
		addCommand(TransSched.cmdBack);
		addCommand(TransSched.cmdSelect);
		
		if(schedBoard.filter.isEmpty())
			closeMenu = append("Вернуться", Images.undo);
		else
			clearFilter = append("Сбросить", Images.stop);
		favorites = append("По избранным", Images.hearts);
		byBus = append("По транспорту", (schedBoard.filter.buses == null) ? Images.transportGray : Images.transport);
		byBusStop = append("По остановкам", (schedBoard.filter.busStops == null) ? Images.busStopGray : Images.busStop);
		
		for (int i = 0; i < TransSched.predefinedFilters.length; i++)
		{
			predefFiltersEnd = append(TransSched.predefinedFilters[i].name, Images.predefFilter);
			if(predefFiltersStart == -1)
				predefFiltersStart = predefFiltersEnd;
		}
		
		for (int i = 0; i < TransSched.customFilters.length; i++)
		{
			customFiltersEnd = append(TransSched.customFilters[i].name, Images.customFilter);
			if(customFiltersStart == -1)
				customFiltersStart = customFiltersEnd;
		}
	}

	public void commandAction(Command c, Displayable d)
	{
		if(c == TransSched.cmdBack)
		{
			TransSched.display.setCurrent(schedBoard);
		}
		else if(c == TransSched.cmdSelect || c == List.SELECT_COMMAND)
		{
			List l = (List)d;
			int sel = l.getSelectedIndex();
			if(sel == -1)
				return;
			
			if(sel == byBus)
			{
				schedBoard.showBusesFilter();
			}
			else if(sel == byBusStop)
			{
				schedBoard.showBusStopsFilter();
			}
			else if(sel == favorites)
			{
				schedBoard.setFilterToFavorites();
				TransSched.display.setCurrent(schedBoard);
			}
			else if(sel == clearFilter)
			{
				schedBoard.resetFilter();
				set(clearFilter, "Вернуться", Images.undo);
				closeMenu = clearFilter;
				clearFilter = -1;
				set(byBus, "По транспорту", Images.transportGray);
				set(byBusStop, "По остановкам", Images.busStopGray);
			}
			else if(sel == closeMenu)
			{
				TransSched.display.setCurrent(schedBoard);
			}
			else if(sel >= predefFiltersStart && sel <= predefFiltersEnd)
			{
				int index = sel - predefFiltersStart;
				schedBoard.setFilter(TransSched.predefinedFilters[index]);
				TransSched.display.setCurrent(schedBoard);
			}
			else if(sel >= customFiltersStart && sel <= customFiltersEnd)
			{
				int index = sel - customFiltersStart; 
				schedBoard.setFilter(TransSched.customFilters[index]);
				TransSched.display.setCurrent(schedBoard);
			}
		}
	}
}
