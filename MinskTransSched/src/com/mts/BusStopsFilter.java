package com.mts;

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.resources.Images;

public class BusStopsFilter extends List implements CommandListener
{
	static final Command cmdToggleFavorite = new Command("Изменить", Command.OK, 1);

	static final Command cmdShowAll = new Command("Все", Command.OK, 2);
	static final Command cmdShowCurrent = new Command("Только текущие", Command.OK, 3);
	static final Command cmdShowFavorites = new Command("Только избранные", Command.OK, 4);

	static final Command cmdSelectCurrent = new Command("Пом. текущие", Command.OK, 5);
	static final Command cmdSelectAll = new Command("Пом. всё", Command.OK, 6);
	static final Command cmdSelectNone = new Command("Пом. ничего", Command.OK, 7);

	SchedulerCanvas scheduleBoard;
	BusStop[] busStops;
	BusStop[] lastBusStopsList;
	boolean favoritesManager;
	int listBase;
	public BusStopsFilter(SchedulerCanvas board, boolean favoritesManager)
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
		Hashtable restoreSelectionVector = null;
		boolean selectAll = false;
		boolean selectFavorites = false;
		if(size() > 0 && lastBusStopsList != null)
		{
			if(favoritesManager == false)
			{
				selectAll = isSelected(0);
				selectFavorites = isSelected(1);
			}
			restoreSelectionVector = new Hashtable();
			for (int i = listBase; i < size(); i++)
			{
				if(isSelected(i))
					restoreSelectionVector.put(lastBusStopsList[i], lastBusStopsList[i]);
			}
			deleteAll();
		}

		lastBusStopsList = busStops;

		if(favoritesManager == false)
		{
			append("Все", null);
			setSelectedIndex(0, selectAll);
			append("Избранные", Images.hearts);
			setSelectedIndex(1, selectFavorites);
		}
		for (int i = 0; i < busStops.length; i++)
		{
			BusStop bs = busStops[i]; 
			int newEl = append(bs.name, bs.favorite ? Images.heart : null);
			if(restoreSelectionVector != null && restoreSelectionVector.containsKey(bs))
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
		for (int i = 0; i < busStops.length; i++)
		{
			BusStop bs = busStops[i]; 
			setSelectedIndex(listBase + i, scheduleBoard.filter.busStopsFilter.containsKey(bs));
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
				scheduleBoard.setBusStopsFilter(null);
				return;
			}
			
			boolean selectFavorites = isSelected(1);

			Vector v = new Vector();
			if(selectFavorites)
			{
				for (int i = 0; i < MinskTransSchedMidlet.allBusStopsArray.length; i++)
				{
					if(MinskTransSchedMidlet.allBusStopsArray[i].favorite)
						v.addElement(MinskTransSchedMidlet.allBusStopsArray[i]);
				}
			}

			// add selected buses
			for (int i = listBase; i < this.size(); i++)
			{
				if(this.isSelected(i))
				{
					v.addElement(busStops[i - listBase]);
				}
			}
			// copy to array
			BusStop[] busStops = new BusStop[v.size()];
			v.copyInto(busStops);
			scheduleBoard.setBusStopsFilter(busStops);
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
					BusStop bs = busStops[i - listBase]; 
					bs.toggleFavorite();
					set(i, bs.name, bs.favorite ? Images.heart : null);
				}
			}
		}
		else if(cmd == cmdShowCurrent)
		{
			this.setTitle("Текущие остановки");
			busStops = scheduleBoard.filter.busStops != null ? scheduleBoard.filter.busStops : MinskTransSchedMidlet.allBusStopsArray;
			createList();
		}
		else if(cmd == cmdShowFavorites)
		{
			this.setTitle("Избранные остановки");
			busStops = scheduleBoard.filter.getFavorites(MinskTransSchedMidlet.allBusStopsArray);
			createList();
		}
		else if(cmd == cmdShowAll)
		{
			this.setTitle("Все остановки");
			busStops = MinskTransSchedMidlet.allBusStopsArray;
			createList();
		}
		else if(cmd == MinskTransSchedMidlet.cmdHelp)
		{
			if(favoritesManager)
				MinskTransSchedMidlet.display.setCurrent(new Help(Help.favManagerHelp, this));
			else
				MinskTransSchedMidlet.display.setCurrent(new Help(Help.stopsHelp, this));
		}
	}
}
