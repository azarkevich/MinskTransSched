package filtering;

import java.util.Hashtable;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import mts.Help;
import mts.SchedulerCanvas;
import mts.TransSched;

import ObjModel.BusStop;

import resources.Images;

public class StopsFavoritesManager extends List implements CommandListener
{
	static final Command cmdToggleFavorite = new Command("Изменить", Command.OK, 1);

	static final Command cmdShowAll = new Command("Все", Command.OK, 2);
	static final Command cmdShowCurrent = new Command("Только текущие", Command.OK, 3);
	static final Command cmdShowFavorites = new Command("Только избранные", Command.OK, 4);

	SchedulerCanvas scheduleBoard;
	Displayable next;
	BusStop[] busStops;
	BusStop[] lastBusStopsList;
	public StopsFavoritesManager(SchedulerCanvas board, Displayable next)
	{
		super(null, List.IMPLICIT);

		this.next = next;
		
		setCommandListener(this);
		addCommand(TransSched.cmdBack);
		addCommand(TransSched.cmdHelp);

		addCommand(cmdToggleFavorite);

		addCommand(List.SELECT_COMMAND);

		scheduleBoard = board;

		commandAction(cmdShowAll, this);
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
		if(cmd == TransSched.cmdBack)
		{
			TransSched.display.setCurrent(next);
		}
		else if(cmd == cmdToggleFavorite || cmd == List.SELECT_COMMAND)
		{
			int sel = getSelectedIndex();
			if(sel == -1)
				return;
			
			BusStop bs = busStops[sel]; 
			bs.toggleFavorite();
			set(sel, bs.name, bs.favorite ? Images.heart : null);
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
			TransSched.display.setCurrent(new Help(Help.favManagerHelp, this));
		}
	}
}
