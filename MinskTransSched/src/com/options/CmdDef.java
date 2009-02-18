package com.options;

import java.util.Hashtable;

public class CmdDef
{
	public CmdDef(int id, String name, String description)
	{
		this.id = id;
		this.name = name;
		this.description = description;
	}
	public CmdDef(int id, String name)
	{
		this.id = id;
		this.name = name;
		this.description = name;
	}
	public int id;
	public String name;
	public String description;
	
	public static final CmdDef cmdScrollUp = new CmdDef(0, "Вверх скрол.", "Скролировать текст в верх на заданное число строк"); 
	public static final CmdDef cmdScrollDown = new CmdDef(1, "Вниз скрол.", "Скролировать текст вниз на заданное число строк"); 
	public static final CmdDef cmdScrollUpPage = new CmdDef(2, "Вверх на страницу", "Скролировать текст вверх на 1 экран"); 
	public static final CmdDef cmdScrollDownPage = new CmdDef(3, "Вниз на страницу", "Скролировать текст вниз на 1 экран"); 
	public static final CmdDef cmdBusStopPrev = new CmdDef(4, "Пред. остановка", "Переключить расписание на следующую остановку в списке"); 
	public static final CmdDef cmdBusStopNext = new CmdDef(5, "След. остановка", "Переключить расписание на предыдущую остановку в списке"); 

	public static final CmdDef cmdWindowDecrease = new CmdDef(6, "Уменьш. окно"); 
	public static final CmdDef cmdWindowIncrease = new CmdDef(7, "Увелич. окно"); 

	public static final CmdDef cmdWindowShiftDecrease = new CmdDef(8, "Уменьш. сдвиг окна"); 
	public static final CmdDef cmdWindowShiftIncrease = new CmdDef(9, "Увелич. сдвиг окна"); 

	public static final CmdDef cmdToggleDayType = new CmdDef(10, "Переключить день"); 
	public static final CmdDef cmdToggleDetailedDescription = new CmdDef(11, "Детальное описание"); 
	public static final CmdDef cmdToggleFavorite = new CmdDef(12, "Занести в избранное"); 
	public static final CmdDef cmdToggleFullSchedule = new CmdDef(13, "Полное расписание"); 

	public static final CmdDef cmdScheduleReset = new CmdDef(14, "Сбросить настройки расписания"); 

	public static final CmdDef cmdShowBookmarks = new CmdDef(15, "Избранное");
	public static final CmdDef cmdShowAllBusStops = new CmdDef(16, "Все остановки");
	public static final CmdDef cmdScheduleFullScreen = new CmdDef(17, "Полноэкранный режим");

	// action type
	public static final short KEY_ACTION_PRESS = 0;
	public static final short KEY_ACTION_PRESS_FIRST = 1;
	public static final short KEY_ACTION_PRESS_REPEAT = 2;
	public static final short KEY_ACTION_RELEASE = 3;
	public static final short KEY_ACTION_RELEASE_SHORT = 4;
	public static final short KEY_ACTION_RELEASE_LONG = 5;

	static Hashtable cmdid2cmd = null;
	
	static CmdDef[] allCommands;
	public static CmdDef[] getAllCommands()
	{
		if(allCommands == null)
		{
			allCommands = new CmdDef[] {
					cmdScrollUp,
					cmdScrollDown,
					cmdScrollUpPage,
					cmdScrollDownPage,
					cmdBusStopPrev,
					cmdBusStopNext,

					cmdWindowDecrease,
					cmdWindowIncrease,

					cmdWindowShiftDecrease,
					cmdWindowShiftIncrease,

					cmdToggleDayType,
					cmdToggleDetailedDescription,
					cmdToggleFavorite,
					cmdToggleFullSchedule,

					cmdScheduleReset,

					cmdShowBookmarks,
					cmdShowAllBusStops,
					cmdScheduleFullScreen
				};
		}
		return allCommands;
	}
	
	public static CmdDef getCmd(int id)
	{
		if(cmdid2cmd == null)
		{
			cmdid2cmd = new Hashtable();
			
			CmdDef[] all = getAllCommands();
			for (int i = 0; i < all.length; i++)
			{
				cmdid2cmd.put(new Integer(all[i].id), all[i]);
			}
		}
		return (CmdDef)cmdid2cmd.get(new Integer(id));
	}
}
