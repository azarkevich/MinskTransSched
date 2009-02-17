package options;

import java.util.Hashtable;

import javax.microedition.lcdui.Canvas;

public class KeyCommands
{
	public static final int CMD_NONE = 0;
	public static final int CMD_SCROLL_UP = 1;
	public static final int CMD_SCROLL_DOWN = 2;
	public static final int CMD_SCROLL_PAGE_UP = 3;
	public static final int CMD_SCROLL_PAGE_DOWN = 4;
	
	public static final int CMD_TOGGLE_FAVORITE = 5;
	
	public static final int CMD_RESET_SCHEDULE = 6;
	public static final int CMD_SHIFT_WINDOW_LEFT = 7;
	public static final int CMD_SHIFT_WINDOW_RIGHT = 8;
	public static final int CMD_INCREASE_WINDOW = 9;
	public static final int CMD_DESCREASE_WINDOW = 10;
	
	public static final int CMD_BUSSTOP_PREV = 11;
	public static final int CMD_BUSSTOP_NEXT = 12;
	
	public static final int CMD_TOGGLE_DAY = 13;
	public static final int CMD_TOGGLE_DETAILED_DESCRIPTION = 14;
	public static final int CMD_TOGGLE_FULL_SCHED = 15;

	public static final int CMD_SHOW_BOOKMARKS = 16;
	public static final int CMD_SHOW_BUSSTOPS = 17;

	public static final int CMD_TOGGLE_FULLSCREEN = 18;

	public static Hashtable key2cmd = new Hashtable();

	public static Integer getKeyHashCode(int code, boolean isGameCode, boolean isReleased, boolean isRepeated)
	{
		int hashKey = (short)code;
		if(isGameCode)
			hashKey |= 0x10000;
		
		if(isReleased)
			hashKey |= 0x20000;
		
		if(isRepeated)
			hashKey |= 0x40000;
		
		return new Integer(hashKey);
	}
	
	public static int mapKeyToCommand(int code, boolean isGameCode, boolean isReleased, boolean isRepeated)
	{
		Object o = key2cmd.get(getKeyHashCode(code, isGameCode, isReleased, isRepeated));
		if(o == null)
			return CMD_NONE;
		
		return ((Integer)o).intValue();
	}
	
	static void addPressAndRepeat(int code, boolean isGameCode, int cmd)
	{
		Integer c = new Integer(cmd);
		key2cmd.put(getKeyHashCode(code, isGameCode, false, false), c);
		key2cmd.put(getKeyHashCode(code, isGameCode, false, true), c);
	}

	public static void loadDefaultKeyCommands()
	{
		key2cmd.clear();

		// text scrolling
		addPressAndRepeat(Canvas.UP, true, CMD_SCROLL_UP);
		addPressAndRepeat(Canvas.DOWN, true, CMD_SCROLL_DOWN);
		addPressAndRepeat(Canvas.LEFT, true, CMD_BUSSTOP_PREV);
		addPressAndRepeat(Canvas.RIGHT, true, CMD_BUSSTOP_NEXT);
		
		addPressAndRepeat('1', false, CMD_BUSSTOP_PREV);
		addPressAndRepeat('2', false, CMD_BUSSTOP_NEXT);

		key2cmd.put(getKeyHashCode('3', false, false, false), new Integer(CMD_TOGGLE_DAY));
		
		addPressAndRepeat('4', false, CMD_DESCREASE_WINDOW);
		addPressAndRepeat('5', false, CMD_INCREASE_WINDOW);

		key2cmd.put(getKeyHashCode('6', false, false, false), new Integer(CMD_RESET_SCHEDULE));

		addPressAndRepeat('7', false, CMD_SHIFT_WINDOW_LEFT);
		addPressAndRepeat('8', false, CMD_SHIFT_WINDOW_RIGHT);

		key2cmd.put(getKeyHashCode('9', false, false, false), new Integer(CMD_TOGGLE_DETAILED_DESCRIPTION));
		key2cmd.put(getKeyHashCode('0', false, false, false), new Integer(CMD_TOGGLE_FAVORITE));
		key2cmd.put(getKeyHashCode('*', false, false, false), new Integer(CMD_TOGGLE_FULL_SCHED));

		key2cmd.put(getKeyHashCode(Canvas.FIRE, true, true, false), new Integer(CMD_SHOW_BOOKMARKS));
		key2cmd.put(getKeyHashCode(Canvas.FIRE, true, true, true), new Integer(CMD_SHOW_BUSSTOPS));
		
		key2cmd.put(getKeyHashCode('#', false, true, false), new Integer(CMD_TOGGLE_FULLSCREEN));
	}
}
