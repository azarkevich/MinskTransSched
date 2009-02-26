package com.mts;

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.resources.Images;

public class BusesFilter extends List implements CommandListener
{
	static final Command cmdToggleFavorite = new Command("Выбраный в избранное", Command.OK, 1);
	static final Command cmdShowCurrent = new Command("Только текущий", Command.OK, 1);
	static final Command cmdShowFavorites = new Command("Только избранный", Command.OK, 1);
	static final Command cmdShowAll = new Command("Все", Command.OK, 1);
	static final Command cmdSelectCurrent = new Command("Выбрать текущий", Command.OK, 1);
	static final Command cmdSelectNone = new Command("Сбросить всё", Command.OK, 1);
	static final Command cmdSelectAll = new Command("Отметить всё", Command.OK, 1);

	SchedulerCanvas scheduleBoard;
	Bus[] buses;
	Bus[] lastBusesList;
	public BusesFilter(SchedulerCanvas board)
	{
		super(null, List.MULTIPLE);
		
		setCommandListener(this);
		addCommand(MinskTransSchedMidlet.cmdSelect);
		addCommand(MinskTransSchedMidlet.cmdBack);

		addCommand(cmdShowCurrent);
		addCommand(cmdShowFavorites);
		addCommand(cmdShowAll);
		
		addCommand(cmdToggleFavorite);
		addCommand(cmdSelectCurrent);
		addCommand(cmdSelectNone);
		addCommand(cmdSelectAll);
		
		addCommand(MinskTransSchedMidlet.cmdHelp);

		scheduleBoard = board;

		commandAction(cmdShowCurrent, this);
	}
	
	void createList()
	{
		// save selection
		Hashtable restoreSelectionHT = null;
		boolean selectAll = false;
		boolean selectFavorites = false;
		if(size() > 0 && lastBusesList != null)
		{
			selectAll = isSelected(0);
			selectFavorites = isSelected(1);
			restoreSelectionHT = new Hashtable();
			for (int i = 2; i < size(); i++)
			{
				if(isSelected(i))
					restoreSelectionHT.put(lastBusesList[i], lastBusesList[i]);
			}
			deleteAll();
		}

		lastBusesList = buses;

		append("Все", null);
		setSelectedIndex(0, selectAll);
		append("Избранные", Images.hearts);
		setSelectedIndex(1, selectFavorites);
		for (int i = 0; i < buses.length; i++)
		{
			Bus b = buses[i]; 
			append(b.name, b.favorite ? Images.heart : null);
			if(restoreSelectionHT != null && restoreSelectionHT.containsKey(b))
				setSelectedIndex(i + 2, true);
		}
	}
	
	void selectCurrrent()
	{
		setSelectedIndex(0, false);
		setSelectedIndex(1, false);

		for (int i = 0; i < buses.length; i++)
		{
			Bus b = buses[i]; 
			if(scheduleBoard.filter.busesFilter.containsKey(b))
				setSelectedIndex(i + 2, true);
			else
				setSelectedIndex(i + 2, false);
		}
	}
	
	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == MinskTransSchedMidlet.cmdSelect || cmd == List.SELECT_COMMAND)
		{
			boolean selectAll = isSelected(0);

			// select all == reset busstops filter
			if(selectAll)
			{
				scheduleBoard.setBusesFilter(null);
				return;
			}
			
			boolean selectFavorites = isSelected(1);

			Vector v = new Vector();
			if(selectFavorites)
			{
				for (int i = 0; i < MinskTransSchedMidlet.allBusesArray.length; i++)
				{
					if(MinskTransSchedMidlet.allBusesArray[i].favorite)
						v.addElement(MinskTransSchedMidlet.allBusesArray[i]);
				}
			}

			// add selected buses
			for (int i = 2; i < this.size(); i++)
			{
				if(this.isSelected(i))
				{
					v.addElement(buses[i - 2]);
				}
			}
			// copy to array
			Bus[] buses = new Bus[v.size()];
			v.copyInto(buses);
			scheduleBoard.setBusesFilter(buses);
		}
		else if(cmd == MinskTransSchedMidlet.cmdBack)
		{
			MinskTransSchedMidlet.display.setCurrent(scheduleBoard);
		}
		else if(cmd == cmdSelectCurrent)
		{
			selectCurrrent();
		}
		else if(cmd == cmdSelectNone)
		{
			for (int i = 0; i < size(); i++)
			{
				setSelectedIndex(i, false);
			}
		}
		else if(cmd == cmdSelectAll)
		{
			setSelectedIndex(0, false);
			setSelectedIndex(1, false);
			for (int i = 2; i < size(); i++)
			{
				setSelectedIndex(i, true);
			}
		}
		else if(cmd == cmdToggleFavorite)
		{
			for (int i = 2; i < size(); i++)
			{
				if(isSelected(i))
				{
					Bus b = buses[i - 2]; 
					b.toggleFavorite();
					set(i, b.name, b.favorite ? Images.heart : null);
				}
			}
		}
		else if(cmd == cmdShowCurrent)
		{
			this.setTitle("Текущий фильтр");
			
			buses = scheduleBoard.filter.buses;
			createList();
		}
		else if(cmd == cmdShowFavorites)
		{
			this.setTitle("Избранный транспорт");
			buses = scheduleBoard.filter.getFavorites(MinskTransSchedMidlet.allBusesArray);
			createList();
		}
		else if(cmd == cmdShowAll)
		{
			this.setTitle("Все остановки");
			buses = MinskTransSchedMidlet.allBusesArray;
			createList();
		}
		else if(cmd == MinskTransSchedMidlet.cmdHelp)
		{
			MinskTransSchedMidlet.display.setCurrent(new Help(Help.transportHelp, this));
		}
	}
}
