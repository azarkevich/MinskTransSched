package filtering;

import java.util.Hashtable;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import mts.Help;
import mts.SchedulerCanvas;
import mts.TransSched;

import ObjModel.Bus;

import resources.Images;

public class TransportFavoritesManager extends List implements CommandListener
{
	static final Command cmdToggleFavorite = new Command("Изменить", Command.OK, 1);

	static final Command cmdShowAll = new Command("Все", Command.OK, 2);
	static final Command cmdShowCurrent = new Command("Только текущий", Command.OK, 3);
	static final Command cmdShowFavorites = new Command("Только избранный", Command.OK, 4);

	static final Command cmdShowInfo = new Command("Info", Command.OK, 5);

	SchedulerCanvas scheduleBoard;
	Displayable next;
	Bus[] buses;
	Bus[] lastBusesList;
	public TransportFavoritesManager(SchedulerCanvas board, Displayable next)
	{
		super(null, List.IMPLICIT);
		
		this.next = next;

		setCommandListener(this);
		addCommand(TransSched.cmdBack);
		addCommand(TransSched.cmdHelp);

		addCommand(cmdToggleFavorite);
		addCommand(cmdShowInfo);

		scheduleBoard = board;

		commandAction(cmdShowAll, this);
	}
	
	void createList()
	{
		// save selection
		Hashtable restoreSelectionHT = null;
		if(size() > 0 && lastBusesList != null)
		{
			restoreSelectionHT = new Hashtable();
			for (int i = 0; i < size(); i++)
			{
				if(isSelected(i))
					restoreSelectionHT.put(lastBusesList[i], lastBusesList[i]);
			}
			deleteAll();
		}

		lastBusesList = buses;

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
		for (int i = 0; i < buses.length; i++)
		{
			Bus b = buses[i]; 
			setSelectedIndex(i, scheduleBoard.filter.busesFilter.containsKey(b));
		}
	}
	
	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == cmdShowInfo)
		{
			int sel = getSelectedIndex();
			if(sel == -1)
				return;
			
			TransSched.display.setCurrent(new Help(buses[sel], this));
		}
		else if(cmd == TransSched.cmdBack)
		{
			TransSched.display.setCurrent(next);
		}
		else if(cmd == cmdToggleFavorite || cmd == List.SELECT_COMMAND)
		{
			int sel = getSelectedIndex();
			if(sel == -1)
				return;
			
			Bus b = buses[sel]; 
			b.toggleFavorite();
			set(sel, b.name, b.favorite ? Images.heart : null);
		}
		else if(cmd == cmdShowCurrent)
		{
			this.setTitle("Текущий фильтр");
			
			buses = scheduleBoard.filter.buses != null ? scheduleBoard.filter.buses : TransSched.allBusesArray;
			createList();
		}
		else if(cmd == cmdShowFavorites)
		{
			this.setTitle("Избранный транспорт");
			buses = scheduleBoard.filter.getFavorites(TransSched.allBusesArray);
			createList();
		}
		else if(cmd == cmdShowAll)
		{
			this.setTitle("Все остановки");
			buses = TransSched.allBusesArray;
			createList();
		}
		else if(cmd == TransSched.cmdHelp)
		{
			TransSched.display.setCurrent(new Help(Help.favManagerHelp, this));
		}
	}
}
