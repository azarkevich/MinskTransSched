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
	static final Command cmdToggleFavorite = new Command("Изменить", Command.OK, 1);

	static final Command cmdShowAll = new Command("Все", Command.OK, 2);
	static final Command cmdShowCurrent = new Command("Только текущий", Command.OK, 3);
	static final Command cmdShowFavorites = new Command("Только избранное", Command.OK, 4);
	
	static final Command cmdSelectCurrent = new Command("Пом. текущий", Command.OK, 5);
	static final Command cmdSelectAll = new Command("Пом. всё", Command.OK, 6);
	static final Command cmdSelectNone = new Command("Пом. ничего", Command.OK, 7);

	SchedulerCanvas scheduleBoard;
	Bus[] buses;
	Bus[] lastBusesList;
	boolean favoritesManager;
	int listBase;
	public BusesFilter(SchedulerCanvas board, boolean favoritesManager)
	{
		super(null, List.MULTIPLE);
		
		this.favoritesManager = favoritesManager;
		listBase = favoritesManager ? 0 : 2;

		setCommandListener(this);
		addCommand(MinskTransSchedMidlet.cmdBack);
		addCommand(MinskTransSchedMidlet.cmdHelp);

		if(favoritesManager)
		{
			addCommand(cmdToggleFavorite);
		}
		else
		{
			addCommand(MinskTransSchedMidlet.cmdOK);
			addCommand(cmdShowCurrent);
			addCommand(cmdShowFavorites);
			addCommand(cmdShowAll);
			
			addCommand(cmdSelectCurrent);
		}
		addCommand(cmdSelectNone);
		addCommand(cmdSelectAll);

		scheduleBoard = board;

		if(favoritesManager)
			commandAction(cmdShowAll, this);
		else
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
			if(favoritesManager == false)
			{
				selectAll = isSelected(0);
				selectFavorites = isSelected(1);
			}
			restoreSelectionHT = new Hashtable();
			for (int i = listBase; i < size(); i++)
			{
				if(isSelected(i))
					restoreSelectionHT.put(lastBusesList[i], lastBusesList[i]);
			}
			deleteAll();
		}

		lastBusesList = buses;

		if(favoritesManager == false)
		{
			append("Все", null);
			setSelectedIndex(0, selectAll);
			append("Избранные", Images.hearts);
			setSelectedIndex(1, selectFavorites);
		}
		for (int i = 0; i < buses.length; i++)
		{
			Bus b = buses[i]; 
			int newEl = append(b.name, b.favorite ? Images.heart : null);
			if(restoreSelectionHT != null && restoreSelectionHT.containsKey(b))
				setSelectedIndex(newEl, true);
		}
	}
	
	void selectCurrrent()
	{
		if(favoritesManager == false)
		{
			setSelectedIndex(0, false);
			setSelectedIndex(1, false);
		}

		for (int i = 0; i < buses.length; i++)
		{
			Bus b = buses[i]; 
			setSelectedIndex(listBase + i, scheduleBoard.filter.busesFilter.containsKey(b));
		}
	}
	
	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == MinskTransSchedMidlet.cmdOK)
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
			for (int i = listBase; i < this.size(); i++)
			{
				if(this.isSelected(i))
				{
					v.addElement(buses[i - listBase]);
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
			if(favoritesManager == false)
			{
				setSelectedIndex(0, false);
				setSelectedIndex(1, false);
			}
			for (int i = listBase; i < size(); i++)
			{
				setSelectedIndex(i, true);
			}
		}
		else if(cmd == cmdToggleFavorite)
		{
			for (int i = listBase; i < size(); i++)
			{
				if(isSelected(i))
				{
					Bus b = buses[i - listBase]; 
					b.toggleFavorite();
					set(i, b.name, b.favorite ? Images.heart : null);
				}
			}
		}
		else if(cmd == cmdShowCurrent)
		{
			this.setTitle("Текущий фильтр");
			
			buses = scheduleBoard.filter.buses != null ? scheduleBoard.filter.buses : MinskTransSchedMidlet.allBusesArray;
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
			if(favoritesManager)
				MinskTransSchedMidlet.display.setCurrent(new Help(Help.favManagerHelp, this));
			else
				MinskTransSchedMidlet.display.setCurrent(new Help(Help.transportHelp, this));
		}
	}
}
