package com.mts;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.resources.Images;

public class FilterInFilterMenu extends List implements CommandListener
{
	int byBus = -1;
	int byBusStop = -1;
	int clearFilter = -1;
	int closeMenu = -1;
	int favorites = -1;
	
	public FilterInFilterMenu()
	{
		super("Фильтр", List.IMPLICIT);

		setCommandListener(this);
		
		addCommand(TransSched.cmdBack);
		addCommand(TransSched.cmdSelect);
		
		clearFilter = append("Сбросить", Images.stop);
		favorites = append("Избранные", Images.hearts);
		favorites = append("Текущий фильтр", null);
	}

	public void commandAction(Command c, Displayable d)
	{
		if(c == TransSched.cmdBack)
		{
		}
		else if(c == TransSched.cmdSelect || c == List.SELECT_COMMAND)
		{

		}
	}
}
