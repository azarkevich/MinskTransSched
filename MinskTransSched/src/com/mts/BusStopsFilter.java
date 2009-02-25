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
	static final Command cmdToggleFavorite = new Command("Выбранное в избранное", Command.OK, 1);
	static final Command cmdShowCurrent = new Command("Только текущие", Command.OK, 1);
	static final Command cmdShowFavorites = new Command("Только избранные", Command.OK, 1);
	static final Command cmdShowAll = new Command("Все", Command.OK, 1);
	static final Command cmdSelectCurrent = new Command("Выбрать текущие", Command.OK, 1);
	static final Command cmdSelectNone = new Command("Сбросить выбранные", Command.OK, 1);
	static final Command cmdHelp = new Command("Помощь", Command.HELP, 1);

	SchedulerCanvas scheduleBoard;
	BusStop[] busStops;
	BusStop[] lastBusStopsList;
	boolean showAll = false;
	public BusStopsFilter(SchedulerCanvas board)
	{
		super(null, List.MULTIPLE);

		setCommandListener(this);
		addCommand(MinskTransSchedMidlet.cmdSelect);
		addCommand(MinskTransSchedMidlet.cmdBack);

		// TODO: convert to popup list ?
		addCommand(cmdShowCurrent);
		addCommand(cmdShowFavorites);
		addCommand(cmdShowAll);
		
		addCommand(cmdToggleFavorite);
		addCommand(cmdSelectCurrent);
		addCommand(cmdSelectNone);
		addCommand(cmdHelp);

		scheduleBoard = board;

		// TODO apply filter from options
		commandAction(cmdShowCurrent, this);
	}
	
	void createList()
	{
		Hashtable restoreSelectionVector = null;
		boolean selectAll = false;
		boolean selectFavorites = false;
		if(size() > 0 && lastBusStopsList != null)
		{
			selectAll = isSelected(0);
			selectFavorites = isSelected(1);
			restoreSelectionVector = new Hashtable();
			// TODO: store selection
			for (int i = 2; i < size(); i++)
			{
				if(isSelected(i))
					restoreSelectionVector.put(lastBusStopsList[i], lastBusStopsList[i]);
			}
			deleteAll();
		}

		lastBusStopsList = busStops;

		append("Все", null);
		setSelectedIndex(0, selectAll);
		append("Избранные", Images.hearts);
		setSelectedIndex(1, selectFavorites);
		for (int i = 0; i < busStops.length; i++)
		{
			BusStop bs = busStops[i]; 
			append(bs.name, bs.favorite ? Images.heart : null);
			if(restoreSelectionVector != null && restoreSelectionVector.containsKey(bs))
				setSelectedIndex(i + 2, true);
		}
	}
	
	Hashtable current;
	void selectCurrrent()
	{
		if(scheduleBoard.filter.busStopsFilter == null && scheduleBoard.filter.busesFilter == null)
			setSelectedIndex(0, true);
		// TODO: select if selected only favorites
		
		if(current == null)
		{
			current = new Hashtable();
			for (int i = 0; i < scheduleBoard.busStops.length; i++)
			{
				current.put(scheduleBoard.busStops[i], scheduleBoard.busStops[i]);
			}
		}
		for (int i = 0; i < busStops.length; i++)
		{
			BusStop bs = busStops[i]; 
			if(current.containsKey(bs))
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

			// select all of all == reset busstops filter
			if(selectAll && showAll)
			{
				scheduleBoard.setBusStopsFilter(null);
				return;
			}
			
			boolean selectFavorites = isSelected(1);

			Vector v = new Vector();
			if(selectFavorites)
			{
				for (int i = 0; i < busStops.length; i++)
				{
					if(busStops[i].favorite)
						v.addElement(busStops[i]);
				}
			}

			// add selected buses
			for (int i = 2; i < this.size(); i++)
			{
				if(selectAll || this.isSelected(i))
				{
					v.addElement(busStops[i - 2]);
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
		else if(cmd == cmdToggleFavorite)
		{
			for (int i = 2; i < size(); i++)
			{
				if(isSelected(i))
				{
					BusStop bs = busStops[i - 2]; 
					bs.toggleFavorite();
					set(i, bs.name, bs.favorite ? Images.heart : null);
				}
			}
		}
		else if(cmd == cmdShowCurrent)
		{
			this.setTitle("Текущие остановки");
			showAll = false;
			busStops = scheduleBoard.filter.FilterIt(MinskTransSchedMidlet.allBusStopsArray);
			createList();
		}
		else if(cmd == cmdShowFavorites)
		{
			this.setTitle("Избранные остановки");
			showAll = false;
			busStops = scheduleBoard.filter.getFavorites(MinskTransSchedMidlet.allBusStopsArray);
			createList();
		}
		else if(cmd == cmdShowAll)
		{
			this.setTitle("Все остановки");
			showAll = true;
			busStops = MinskTransSchedMidlet.allBusStopsArray;
			createList();
		}
		else if(cmd == cmdHelp)
		{
			MinskTransSchedMidlet.display.setCurrent(new HelpCanvasSimple(helpText, this));
		}
	}

	public final static String helpText = 
		" Выбор остановок для фильтрации\n" +
		" Доступны следующие пункты меню:\n" +
		" 'Выбранное в избранное' - неспотря на название, инвертирует 'избранность' отмеченных остановок.\n" +
		" 'Все' - показать все остановки\n" +
		" 'Только текущие' - показать остановки, которые отображаются в окне расписания(отфильтрованные)\n" +
		" 'Только избранные' - показать все избранные остановки\n" +
		" 'Выбрать текущие' - выбрать остановки, которые отображаются в окне расписания\n" +
		" 'Сбросить выбранные' - сбросить все отметки\n" +
		" 'Помощь' - тут ясно.\n" +
		"\n" +
		" Внимание! В окне расписания появится только те остановки, которые были выбраны тут и проходят фильтр по транспорту! Т.е. " +
		"выбрав тут ВСЕ и 51 автобус, получим все остановки на которых останавливается 51 автобус. Если фильтр по транспорту не выбран, будут отображены " +
		"выбранные здесь остановки в точности\n" +
		"\n" +
		" Винмание2! Элемент списка 'Все' и 'Избранные' отностится к текущему списку. Выбрав 'Только текущие' в меню и пункт 'Все' в списке " +
		"будут выбраны все текущие. Т.е. ничего не изменится. Выбрав 'Все' в меню и 'Все' в списке всё равно получим не все-все " +
		"остановки при наличии фильтра по транспорту.\n"
		;

}
