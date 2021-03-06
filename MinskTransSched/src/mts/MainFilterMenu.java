package mts;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import resources.Images;


public class MainFilterMenu extends List implements CommandListener
{
	SchedulerCanvas schedBoard;
	
	int byBus = -1;
	int favorites = -1;
	
	int customFilters = -1;
	int regionFilters = -1;
/*	
	int changeModePos = -1;
	int currentChangeMode = SchedulerCanvas.FILTER_CHANGE_MODE_REPLACE;
	String getChangeModeName(int cm)
	{
		switch(cm)
		{
		case SchedulerCanvas.FILTER_CHANGE_MODE_REPLACE:
			return "Заменить";
		case SchedulerCanvas.FILTER_CHANGE_MODE_ADD:
			return "Добавить";
		case SchedulerCanvas.FILTER_CHANGE_MODE_REMOVE:
			return "Убрать";
		}
		return "?";
	}
	Image getChangeModeImage(int cm)
	{
		switch(cm)
		{
		case SchedulerCanvas.FILTER_CHANGE_MODE_REPLACE:
			return Images.fmc_replace;
		case SchedulerCanvas.FILTER_CHANGE_MODE_ADD:
			return Images.fmc_add;
		case SchedulerCanvas.FILTER_CHANGE_MODE_REMOVE:
			return Images.fmc_remove;
		}
		return null;
	}
*/
	public MainFilterMenu(SchedulerCanvas schedBoard)
	{
		super("Фильтр", List.IMPLICIT);

		this.schedBoard = schedBoard;

		setCommandListener(this);
		
		addCommand(TransSched.cmdBack);
		addCommand(TransSched.cmdSelect);
		
		byBus = append("По транспорту", Images.transport);
		favorites = append("Избранное", Images.hearts);

		customFilters = append("Мои фильтры", Images.monkey);
		regionFilters = append("Регионы", Images.predefFilter);
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
				schedBoard.showBusFilter();
			}
			else if(sel == favorites)
			{
				schedBoard.setFilterToFavorites();
				TransSched.display.setCurrent(schedBoard);
			}
			else if(sel == customFilters)
			{
				TransSched.display.setCurrent(new FilterMenu(true, this, schedBoard));
			}
			else if(sel == regionFilters)
			{
				TransSched.display.setCurrent(new FilterMenu(false, this, schedBoard));
			}
		}
	}
}
