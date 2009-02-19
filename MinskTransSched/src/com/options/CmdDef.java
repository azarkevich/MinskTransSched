package com.options;

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public class CmdDef
{
	public CmdDef(int id, String name, String description)
	{
		this(id, name, description, (short)0, 0, false);
	}

	public CmdDef(int id, String name, String description, short defActionType, int defKeyCode)
	{
		this(id, name, description, defActionType, defKeyCode, false);
	}

	public CmdDef(int id, String name, String description, short defActionType, int defKeyCode, boolean isGameCode)
	{
		this.id = id;
		this.name = name;
		if(description != null)
			this.description = description;
		else
			this.description = name;
		
		defKeyHash = getKeyHash(defKeyCode, isGameCode, defActionType).intValue();
		keyHash = defKeyHash;
		if(v == null)
			v = new Vector();
		v.addElement(this);
		
		if(keyHash2Cmd == null)
			keyHash2Cmd = new Hashtable();
		
		keyHash2Cmd.put(new Integer(keyHash), this);
	}

	public int id;
	public String name;
	public String description;
	private int defKeyHash;
	private int keyHash;
	
	public int getKeyHash()
	{
		return keyHash;
	}
	
	private static Hashtable keyHash2Cmd; 
	public void setKeyHash(int newKeyHash)
	{
		if(keyHash != 0)
			keyHash2Cmd.remove(new Integer(keyHash));
		
		keyHash = newKeyHash;
		
		if(keyHash != 0)
			keyHash2Cmd.put(new Integer(keyHash), this);
	}
	
	public void setDefaultKeyHash()
	{
		setKeyHash(defKeyHash);
	}
	
	public static CmdDef getCommandByKeyHash(int keyHash)
	{
		return (CmdDef)keyHash2Cmd.get(new Integer(keyHash));
	}

	public String getKeyHashName(String noneString)
	{
		return getKeyHashName(keyHash, noneString);
	}

	public static Integer getKeyHash(int keyCode, boolean isGameCode, short actionType)
	{
		int actionMask = ((actionType & 0xF) << 16); 
		int ret = 0;
		if(keyCode < 0)
			ret = (-keyCode) | actionMask | 0x80000000;
		else
			ret = keyCode | actionMask;
		
		if(isGameCode)
			ret |= 0x40000000;
		
		return new Integer(ret);
	}
	
	public static int getKeyCodeFromKeyHash(int hash)
	{
		int keyCode = hash & 0xFFFF;
		if((hash & 0x80000000) == 0x80000000)
			keyCode = -keyCode;
		
		return keyCode;
	}

	public static short getActionCodeFromKeyHash(int hash)
	{
		return (short)((hash & 0x000F0000) >> 16);
	}

	public static boolean getIsGameCodeFromKeyHash(int hash)
	{
		return ((hash & 0x40000000) == 0x40000000);
	}

	private static Canvas c;
	public static Canvas getDummyCanvas()
	{
		if(c == null)
		{
			c = new Canvas()
			{
				public void paint(Graphics g)
				{
				}
			};
		}
		return c;
	}

	public static String getKeyHashName(int keyHash, String noneString)
	{
		int keyCode = getKeyCodeFromKeyHash(keyHash);
		if(keyCode != 0 && getIsGameCodeFromKeyHash(keyHash))
		{
			keyCode = getDummyCanvas().getKeyCode(keyCode);
		}

		if(keyCode == 0)
			return noneString;

		if(keyCode >= 32 && keyCode < 127)
			return new String(new char[] {(char)keyCode});

		return getDummyCanvas().getKeyName(keyCode);
	}

	public static CmdDef cmdScrollUp = new CmdDef(
			0,
			"Вверх скрол.",
			"Скролировать текст в верх на заданное число строк",
			CmdDef.KEY_ACTION_PRESS,
			Canvas.UP, true);
	
	public static CmdDef cmdScrollDown = new CmdDef(
			1,
			"Вниз скрол.",
			"Скролировать текст вниз на заданное число строк",
			CmdDef.KEY_ACTION_PRESS,
			Canvas.DOWN, true);
	
	public static CmdDef cmdScrollUpPage = new CmdDef(
			2,
			"Вверх на страницу",
			"Скролировать текст вверх на 1 экран");
	
	public static CmdDef cmdScrollDownPage = new CmdDef(
			3,
			"Вниз на страницу",
			"Скролировать текст вниз на 1 экран");
	
	public static CmdDef cmdBusStopPrev = new CmdDef(
			4,
			"Пред. остановка",
			"Переключить расписание на предыдущую остановку в списке",
			CmdDef.KEY_ACTION_PRESS,
			'1');
	
	public static CmdDef cmdBusStopNext = new CmdDef(
			5,
			"След. остановка",
			"Переключить расписание на следующую остановку в списке",
			CmdDef.KEY_ACTION_PRESS,
			'2'); 

	public static CmdDef cmdWindowDecrease = new CmdDef(
			6,
			"Уменьш. окно",
			"Уменьшить размер окна расписания",
			CmdDef.KEY_ACTION_PRESS,
			'4');
	
	public static CmdDef cmdWindowIncrease = new CmdDef(
			7,
			"Увелич. окно",
			"Увеличить размер окна расписания",
			CmdDef.KEY_ACTION_PRESS,
			'5'); 

	public static CmdDef cmdWindowShiftDecrease = new CmdDef(
			8,
			"Сдвиг окна в лево",
			"Уменьшить смешение окна. Будет больше видно уже ушедших автобусов",
			CmdDef.KEY_ACTION_PRESS,
			'7');
	
	public static CmdDef cmdWindowShiftIncrease = new CmdDef(
			9,
			"Сдвиг окна в право",
			"Увеличить сдвиг окна.",
			CmdDef.KEY_ACTION_PRESS,
			'8'); 

	public static CmdDef cmdToggleDayType = new CmdDef(
			10,
			"Переключ. день",
			"Переключить тип дня по кругу. День недели -> Рабочий -> Выходной.",
			CmdDef.KEY_ACTION_PRESS_FIRST,
			'3'); 
	
	public static CmdDef cmdToggleDetailedDescription = new CmdDef(
			11,
			"Детальное описания",
			"Вывести более детальную информацию о автобусах, расписаниях и остановках.",
			CmdDef.KEY_ACTION_PRESS_FIRST,
			'9');
	
	public static CmdDef cmdToggleFavorite = new CmdDef(
			12,
			"Занести в избранное",
			"Занести текущую остановку в список избранных остановок (или удалить её оттуда)",
			CmdDef.KEY_ACTION_PRESS_FIRST,
			'0');
	
	public static CmdDef cmdToggleFullSchedule = new CmdDef(
			13,
			"Полное распис.",
			"Отобразить расписание на весь день.",
			CmdDef.KEY_ACTION_PRESS_FIRST,
			'*'); 

	public static CmdDef cmdScheduleReset = new CmdDef(
			14,
			"Сбросить распис.",
			"Сбросить такие настройки расписания, как: тип дня, размер и сдвиг окна, текущая остановка, детальные описания и т.д.",
			CmdDef.KEY_ACTION_PRESS_FIRST,
			'6'); 

	public static CmdDef cmdShowBookmarks = new CmdDef(
			15,
			"Спис. избранное",
			"Пеерход к списку избранных остановок",
			CmdDef.KEY_ACTION_RELEASE_SHORT,
			Canvas.FIRE, true);
	
	public static CmdDef cmdShowAllBusStops = new CmdDef(
			16,
			"Все остановки",
			"Переход к списку всех остановок",
			CmdDef.KEY_ACTION_RELEASE_LONG,
			Canvas.FIRE, true);
	
	public static final CmdDef cmdScheduleFullScreen = new CmdDef(
			17,
			"Полноэкранный режим",
			"Переключить полноэкранный режим");

	public static CmdDef cmdSchedShiftDecrease = new CmdDef(
			18,
			"Сдвиг распис. -1",
			"Уменьшить сдвиг расписания на 1 мин",
			CmdDef.KEY_ACTION_RELEASE_SHORT,
			Canvas.LEFT, true);

	public static CmdDef cmdSchedShiftIncrease = new CmdDef(
			19,
			"Сдвиг распис. +1",
			"Увеличить сдвиг расписания на 1 мин",
			CmdDef.KEY_ACTION_RELEASE_SHORT,
			Canvas.RIGHT, true);

	public static CmdDef cmdSchedShiftDecrease10 = new CmdDef(
			20,
			"Cдвиг распис. -10",
			"Уменьшить сдвиг расписания на 10 мин",
			CmdDef.KEY_ACTION_RELEASE_LONG,
			Canvas.LEFT, true);

	public static CmdDef cmdSchedShiftIncrease10 = new CmdDef(
			21,
			"Сдвиг распис. +10",
			"Увеличить сдвиг расписания на 10 мин",
			CmdDef.KEY_ACTION_RELEASE_LONG,
			Canvas.RIGHT, true);

	public static CmdDef cmdToggleSchedShowTimeDiff = new CmdDef(
			22,
			"Отобр. 'Осталось'",
			"Отображать или нет сколько осталось(или прошло) до автобуса");
	
	// action type
	public static final short KEY_ACTION_PRESS = 0;
	public static final short KEY_ACTION_PRESS_FIRST = 1;
	public static final short KEY_ACTION_PRESS_REPEAT = 2;
	public static final short KEY_ACTION_RELEASE = 3;
	public static final short KEY_ACTION_RELEASE_SHORT = 4;
	public static final short KEY_ACTION_RELEASE_LONG = 5;

	private static Vector v;
	private static CmdDef[] allCommands;
	public static CmdDef[] getAllCommands()
	{
		if(allCommands == null)
		{
			allCommands = new CmdDef[v.size()];
			v.copyInto(allCommands);
			v = null;
		}
		return allCommands;
	}
	
	private static Hashtable cmdid2cmd = null;
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
	
	public static void resetAllKeyHashes()
	{
		CmdDef[] all = CmdDef.getAllCommands();
		for (int i = 0; i < all.length; i++)
		{
			all[i].setDefaultKeyHash();
		}
	}
}
