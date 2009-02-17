package com.options;

import java.util.Hashtable;

public class CmdDef
{
	public CmdDef(int id, String name)
	{
		this.id = id;
		this.name = name;
	}
	public int id;
	public String name;
	
	public static final CmdDef cmdScrollUp = new CmdDef(0, "Скролировать вверх"); 
	public static final CmdDef cmdScrollDown = new CmdDef(1, "Скролировать вниз"); 
	public static final CmdDef cmdScrollUpPage = new CmdDef(2, "Скролировать вверх на страницу"); 
	public static final CmdDef cmdScrollDownPage = new CmdDef(3, "Скролировать вниз на страницу"); 
	public static final CmdDef cmdBusStopPrev = new CmdDef(4, "Предыдущая остановка"); 
	public static final CmdDef cmdBusStopNext = new CmdDef(5, "Следующая остановка"); 

	public static final CmdDef cmdWindowDecrease = new CmdDef(6, "Уменьшить окно"); 
	public static final CmdDef cmdWindowIncrease = new CmdDef(7, "Увеличить окно"); 

	public static final CmdDef cmdWindowShiftDecrease = new CmdDef(8, "Уменьшить сдвиг окна"); 
	public static final CmdDef cmdWindowShiftIncrease = new CmdDef(9, "Увеличить сдвиг окна"); 

	public static final CmdDef cmdToggleDayType = new CmdDef(10, "Переключить тип дня"); 
	public static final CmdDef cmdToggleDetailedDescription = new CmdDef(11, "Детальное описание"); 
	public static final CmdDef cmdToggleFavorite = new CmdDef(12, "Занести в избранное"); 
	public static final CmdDef cmdToggleFullSchedule = new CmdDef(13, "Полное расписание"); 

	public static final CmdDef cmdScheduleReset = new CmdDef(14, "Сбросить настройки расписания"); 

	public static final CmdDef cmdShowBookmarks = new CmdDef(15, "Избранное");
	public static final CmdDef cmdShowAllBusStops = new CmdDef(16, "Все остановки");
	public static final CmdDef cmdScheduleFullScreen = new CmdDef(17, "Полноэкранный режим");

	static Hashtable cmdid2cmd = null;
	public static CmdDef getCmd(int id)
	{
		if(cmdid2cmd == null)
		{
			cmdid2cmd = new Hashtable();

			cmdid2cmd.put(new Integer(cmdScrollUp.id), cmdScrollUp);
			cmdid2cmd.put(new Integer(cmdScrollDown.id), cmdScrollDown);
			cmdid2cmd.put(new Integer(cmdScrollUpPage.id), cmdScrollUpPage);
			cmdid2cmd.put(new Integer(cmdScrollDownPage.id), cmdScrollDownPage);
			cmdid2cmd.put(new Integer(cmdBusStopPrev.id), cmdBusStopPrev);
			cmdid2cmd.put(new Integer(cmdBusStopNext.id), cmdBusStopNext);

			cmdid2cmd.put(new Integer(cmdWindowDecrease.id), cmdWindowDecrease);
			cmdid2cmd.put(new Integer(cmdWindowIncrease.id), cmdWindowIncrease);

			cmdid2cmd.put(new Integer(cmdWindowShiftDecrease.id), cmdWindowShiftDecrease);
			cmdid2cmd.put(new Integer(cmdWindowShiftIncrease.id), cmdWindowShiftIncrease);

			cmdid2cmd.put(new Integer(cmdToggleDayType.id), cmdToggleDayType);
			cmdid2cmd.put(new Integer(cmdToggleDetailedDescription.id), cmdToggleDetailedDescription);
			cmdid2cmd.put(new Integer(cmdToggleFavorite.id), cmdToggleFavorite);
			cmdid2cmd.put(new Integer(cmdToggleFullSchedule.id), cmdToggleFullSchedule);

			cmdid2cmd.put(new Integer(cmdScheduleReset.id), cmdScheduleReset);

			cmdid2cmd.put(new Integer(cmdShowBookmarks.id), cmdShowBookmarks);
			cmdid2cmd.put(new Integer(cmdShowAllBusStops.id), cmdShowAllBusStops);
			cmdid2cmd.put(new Integer(cmdScheduleFullScreen.id), cmdScheduleFullScreen);
		}
		
		return (CmdDef)cmdid2cmd.get(new Integer(id));
	}
}
