package mts;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import ObjModel.FilterDef;
import options.OptionsStoreManager;

import resources.Images;


import filtering.FilterEditor;

public class FilterMenu extends List implements CommandListener
{
	SchedulerCanvas schedBoard;
	
	public final static Command cmdNew = new Command("Создать", Command.OK, 1);
	public final static Command cmdUpdate = new Command("Обновить", Command.OK, 2);
	public final static Command cmdDelete = new Command("Удалить", Command.OK, 3);
	public final static Command cmdRename = new Command("Переименовать", Command.OK, 4);
	public final static Command cmdInfo = new Command("Info", Command.OK, 5);

	MainFilterMenu prevScreen;
	FilterDef[] filters;
	public FilterMenu(boolean customFilters, MainFilterMenu prevScreen, SchedulerCanvas schedBoard)
	{
		super(customFilters ? "Мои фильтры" : "Регионы", List.IMPLICIT);

		this.schedBoard = schedBoard;
		this.prevScreen = prevScreen;

		setCommandListener(this);
		
		addCommand(TransSched.cmdBack);
		addCommand(TransSched.cmdSelect);
		if(customFilters)
		{
			addCommand(cmdNew);
			addCommand(cmdUpdate);
			addCommand(cmdDelete);
			addCommand(cmdRename);
		}
		addCommand(cmdInfo);
		
		filters = customFilters ? TransSched.customFilters : TransSched.predefinedFilters;  
		for (int i = 0; i < filters.length; i++)
		{
			append(filters[i].name, customFilters ? Images.monkey : Images.predefFilter);
		}
	}

	public void commandAction(Command c, Displayable d)
	{
		if(c == TransSched.cmdBack)
		{
			TransSched.display.setCurrent(prevScreen);
		}
		else if(c == cmdInfo)
		{
			int sel = getSelectedIndex();
			if(sel == -1)
				return;
			
			TransSched.display.setCurrent(new Help(filters[sel], this));
		}
		else if(c == TransSched.cmdSelect || c == List.SELECT_COMMAND)
		{
			List l = (List)d;
			int sel = l.getSelectedIndex();
			if(sel == -1)
				return;
			
			schedBoard.setFilter(filters[sel]);
			TransSched.display.setCurrent(schedBoard);
		}
		else if(c == cmdNew)
		{
			FilterDef fd = new FilterDef();
			fd.name = "new";
			fd.stops = schedBoard.filter.busStops;
			fd.transport = schedBoard.filter.buses;

			OptionsStoreManager.saveCustomFilterDefinitions(fd);

			// append into list:
			FilterDef[] newFilters = new FilterDef[filters.length + 1];
			System.arraycopy(filters, 0, newFilters, 0, filters.length);
			newFilters[newFilters.length - 1] = fd;

			filters = newFilters;
			TransSched.customFilters = newFilters;
			
			int newSel = append(fd.name, Images.monkey);
			setSelectedIndex(newSel, true);

			TransSched.display.setCurrent(new FilterEditor(fd, this));
		}
		else if(c == cmdUpdate)
		{
			List l = (List)d;
			int sel = l.getSelectedIndex();
			if(sel == -1)
				return;

			FilterDef fd = filters[sel];
			fd.stops = schedBoard.filter.busStops;
			fd.transport = schedBoard.filter.buses;
			OptionsStoreManager.saveCustomFilterDefinitions(fd);
		}
		else if(c == cmdDelete)
		{
			List l = (List)d;
			int sel = l.getSelectedIndex();
			if(sel == -1)
				return;
			
			OptionsStoreManager.deleteCustomFilterDefinitions(filters[sel]);
			delete(sel);
			FilterDef[] newFilters = new FilterDef[filters.length - 1];
			System.arraycopy(filters, 0, newFilters, 0, sel);
			System.arraycopy(filters, sel + 1, newFilters, sel, filters.length - sel - 1);
			
			filters = newFilters;
			TransSched.customFilters = newFilters;
		}
		else if(c == cmdRename)
		{
			List l = (List)d;
			int sel = l.getSelectedIndex();
			if(sel == -1)
				return;
			
			FilterDef fd = filters[sel];
			TransSched.display.setCurrent(new FilterEditor(fd, this));
		}
	}
	
	public void updateFilter(FilterDef fd)
	{
		for (int i = 0; i < filters.length; i++)
		{
			if(fd == filters[i])
			{
				set(i, fd.name, Images.monkey);
				break;
			}
		}
	}
}
