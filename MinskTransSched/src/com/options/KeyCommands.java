package com.options;

import java.util.Hashtable;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public class KeyCommands
{
	static Hashtable key2cmd = new Hashtable();

	public static Integer getKeyHashCode(int keyCode, boolean isGameCode, short actionType)
	{
		int ret = 0;
		if(keyCode < 0)
			ret = (-keyCode) | (actionType << 16) | 0x80000000;
		else
			ret = keyCode | (actionType << 16);
		
		if(isGameCode)
			ret |= 0x40000000;
		
		return new Integer(ret);
	}
	
	public static int getKeyCodeFromKeyHashCode(int hash)
	{
		if((hash & 0x80000000) == 0x80000000)
		{
			return -(hash & 0xFFFF);
		}
		
		return hash & 0xFFFF;
	}

	static Canvas c = null;
	public static CmdDef getCommand(int keyCode, boolean released, boolean repeated)
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

		Object o;
		
		// simple press/release
		short action1 = released ? KeyActionDef.KEY_ACTION_RELEASE : KeyActionDef.KEY_ACTION_PRESS;
		o = key2cmd.get(getKeyHashCode(keyCode, false, action1));
		if(o != null)
		{
			System.out.println("1. #" + keyCode + " -> " + ((CmdDef)o).name);
			return (CmdDef)o;
		}

		// detailed PRESS_FIRST/PRESS_REPEAT or RELEASE_SHORT/RELEASE_LONG
		short action2;
		if(released)
			action2 = repeated ? KeyActionDef.KEY_ACTION_RELEASE_LONG : KeyActionDef.KEY_ACTION_RELEASE_SHORT;
		else
			action2 = repeated ? KeyActionDef.KEY_ACTION_PRESS_REPEAT : KeyActionDef.KEY_ACTION_PRESS_FIRST;

		o = key2cmd.get(getKeyHashCode(keyCode, false, action2));
		if(o != null)
		{
			System.out.println("2. #" + keyCode + " -> " + ((CmdDef)o).name);
			return (CmdDef)o;
		}

		// not found. may be this is stored as game code?
		if(keyCode < 0)
		{
			int gameCode = c.getGameAction(keyCode);
			if(gameCode != 0)
			{
				o = key2cmd.get(getKeyHashCode(gameCode, true, action1));
				if(o != null)
				{
					System.out.println("3. #" + keyCode + " -> " + ((CmdDef)o).name);
					return (CmdDef)o;
				}
	
				o = key2cmd.get(getKeyHashCode(gameCode, true, action2));
				if(o != null)
				{
					System.out.println("4. #" + keyCode + " -> " + ((CmdDef)o).name);
					return (CmdDef)o;
				}
			}
		}
		
		return null;
	}
	
	public static void mapKeyDef2Cmd(int keyDef, CmdDef cmd)
	{
		key2cmd.put(new Integer(keyDef), cmd);
	}

	public static void mapKey2Cmd(int keyCode, boolean isGameCode, short actionType, CmdDef cmd)
	{
		Integer hash = getKeyHashCode(keyCode, isGameCode, actionType);
		key2cmd.put(hash, cmd);
	}
	
	public static void loadDefaultKeyCommands()
	{
		key2cmd.clear();
		
		mapKey2Cmd(Canvas.UP, true, KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdScrollUp);
		mapKey2Cmd(Canvas.DOWN, true, KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdScrollDown);

		mapKey2Cmd('1', false, KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdBusStopPrev);
		mapKey2Cmd('2', false, KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdBusStopNext);

		mapKey2Cmd('3', false, KeyActionDef.KEY_ACTION_PRESS_FIRST, CmdDef.cmdToggleDayType);

		mapKey2Cmd('4', false, KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdWindowDecrease);
		mapKey2Cmd('5', false, KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdWindowIncrease);
		
		mapKey2Cmd('6', false, KeyActionDef.KEY_ACTION_PRESS_FIRST, CmdDef.cmdScheduleReset);

		mapKey2Cmd('7', false, KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdWindowShiftDecrease);
		mapKey2Cmd('8', false, KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdWindowShiftIncrease);
		
		mapKey2Cmd('9', false, KeyActionDef.KEY_ACTION_PRESS_FIRST, CmdDef.cmdToggleDetailedDescription);
		mapKey2Cmd('0', false, KeyActionDef.KEY_ACTION_PRESS_FIRST, CmdDef.cmdToggleFavorite);
		mapKey2Cmd('*', false, KeyActionDef.KEY_ACTION_PRESS_FIRST, CmdDef.cmdToggleFullSchedule);

		mapKey2Cmd(Canvas.FIRE, true, KeyActionDef.KEY_ACTION_RELEASE_SHORT, CmdDef.cmdShowBookmarks);
		mapKey2Cmd(Canvas.FIRE, true, KeyActionDef.KEY_ACTION_RELEASE_LONG, CmdDef.cmdShowAllBusStops);

		mapKey2Cmd('#', false, KeyActionDef.KEY_ACTION_PRESS_FIRST, CmdDef.cmdScheduleFullScreen);
	}
}
