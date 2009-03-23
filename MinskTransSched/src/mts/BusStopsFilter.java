package mts;

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import ObjModel.BusStop;

import resources.Images;



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
	public BusStopsFilter(SchedulerCanvas board, boolean favoritesManager)
	{
		super(null, List.MULTIPLE);
		
		this.favoritesManager = favoritesManager;

		setCommandListener(this);
		addCommand(TransSched.cmdBack);
		addCommand(TransSched.cmdHelp);

		if(favoritesManager)
		{
			addCommand(cmdToggleFavorite);
		}
		else
		{
			addCommand(TransSched.cmdOK);

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
		if(size() > 0 && lastBusStopsList != null)
		{
			restoreSelectionVector = new Hashtable();
			for (int i = 0; i < size(); i++)
			{
				if(isSelected(i))
					restoreSelectionVector.put(lastBusStopsList[i], lastBusStopsList[i]);
			}
			deleteAll();
		}

		lastBusStopsList = busStops;

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
		for (int i = 0; i < busStops.length; i++)
		{
			BusStop bs = busStops[i]; 
			setSelectedIndex(i, scheduleBoard.filter.busStopsFilter.containsKey(bs));
		}
	}
	
	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == TransSched.cmdOK)
		{
			Vector v = new Vector();
			// add selected buses
			for (int i = 0; i < this.size(); i++)
			{
				if(this.isSelected(i))
				{
					v.addElement(busStops[i]);
				}
			}
			// copy to array
			BusStop[] busStops = new BusStop[v.size()];
			v.copyInto(busStops);
			scheduleBoard.setBusStopsFilter(busStops);
		}
		else if(cmd == TransSched.cmdBack)
		{
			TransSched.display.setCurrent(scheduleBoard);
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
			for (int i = 0; i < size(); i++)
			{
				setSelectedIndex(i, true);
			}
		}
		else if(cmd == cmdToggleFavorite)
		{
			for (int i = 0; i < size(); i++)
			{
				if(isSelected(i))
				{
					BusStop bs = busStops[i]; 
					bs.toggleFavorite();
					set(i, bs.name, bs.favorite ? Images.heart : null);
				}
			}
		}
		else if(cmd == cmdShowCurrent)
		{
			this.setTitle("Текущие остановки");
			busStops = scheduleBoard.filter.busStops != null ? scheduleBoard.filter.busStops : TransSched.allBusStopsArray;
			createList();
		}
		else if(cmd == cmdShowFavorites)
		{
			this.setTitle("Избранные остановки");
			busStops = scheduleBoard.filter.getFavorites(TransSched.allBusStopsArray);
			createList();
		}
		else if(cmd == cmdShowAll)
		{
			this.setTitle("Все остановки");
			busStops = TransSched.allBusStopsArray;
			createList();
		}
		else if(cmd == TransSched.cmdHelp)
		{
			if(favoritesManager)
				TransSched.display.setCurrent(new Help(Help.favManagerHelp, this));
			else
				TransSched.display.setCurrent(new Help(Help.stopsHelp, this));
		}
	}
}
