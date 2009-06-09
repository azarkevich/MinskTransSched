package mts;

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import ObjModel.Bus;

import resources.Images;

public class BusesFilter extends List implements CommandListener
{
	static final Command cmdShowAll = new Command("Все", Command.OK, 2);
	static final Command cmdShowCurrent = new Command("Только текущий", Command.OK, 3);
	static final Command cmdShowFavorites = new Command("Только избранный", Command.OK, 4);
	
	static final Command cmdSelectCurrent = new Command("Пом. текущий", Command.OK, 5);
	static final Command cmdSelectAll = new Command("Пом. всё", Command.OK, 6);
	static final Command cmdSelectNone = new Command("Пом. ничего", Command.OK, 7);

	SchedulerCanvas scheduleBoard;
	Bus[] buses;
	Bus[] lastBusesList;
	public BusesFilter(SchedulerCanvas board)
	{
		super(null, List.MULTIPLE);
		
		setCommandListener(this);
		addCommand(TransSched.cmdBack);
		addCommand(TransSched.cmdHelp);

		addCommand(TransSched.cmdOK);
		addCommand(cmdShowCurrent);
		addCommand(cmdShowFavorites);
		addCommand(cmdShowAll);
		
		addCommand(cmdSelectCurrent);

		addCommand(cmdSelectNone);
		addCommand(cmdSelectAll);

		scheduleBoard = board;

		commandAction(cmdShowCurrent, this);
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
		if(cmd == TransSched.cmdOK)
		{
			boolean selectAll = isSelected(0);

			// select all == reset busstops filter
			if(selectAll)
			{
				scheduleBoard.setBusesFilter(SchedulerCanvas.FILTER_CHANGE_MODE_REPLACE, null);
				return;
			}
			
			boolean selectFavorites = isSelected(1);

			Vector v = new Vector();
			if(selectFavorites)
			{
				for (int i = 0; i < TransSched.allTransportArray.length; i++)
				{
					if(TransSched.allTransportArray[i].favorite)
						v.addElement(TransSched.allTransportArray[i]);
				}
			}

			// add selected buses
			for (int i = 0; i < this.size(); i++)
			{
				if(this.isSelected(i))
				{
					v.addElement(buses[i]);
				}
			}
			// copy to array
			Bus[] buses = new Bus[v.size()];
			v.copyInto(buses);
			scheduleBoard.setBusesFilter(SchedulerCanvas.FILTER_CHANGE_MODE_REPLACE, buses);
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
		else if(cmd == cmdShowCurrent)
		{
			this.setTitle("Текущий фильтр");
			
			buses = scheduleBoard.filter.buses != null ? scheduleBoard.filter.buses : TransSched.allTransportArray;
			createList();
		}
		else if(cmd == cmdShowFavorites)
		{
			this.setTitle("Избранный транспорт");
			buses = scheduleBoard.filter.getFavorites(TransSched.allTransportArray);
			createList();
		}
		else if(cmd == cmdShowAll)
		{
			this.setTitle("Все остановки");
			buses = TransSched.allTransportArray;
			createList();
		}
		else if(cmd == TransSched.cmdHelp)
		{
			TransSched.display.setCurrent(new Help(Help.transportHelp, this));
		}
	}
}
