package com.options;

import java.util.Hashtable;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public class KeyCommands
{
	static Hashtable key2cmd = new Hashtable();

	public static Integer getKeyHashCode(int keyCode, short actionType)
	{
		if(keyCode < 0)
			return new Integer((-keyCode) | (actionType << 16) | 0x80000000);
		
		return new Integer(keyCode | (actionType << 16));
	}
	
	public static int getKeyCodeFromKeyHashCode(int hash)
	{
		if((hash & 0x80000000) == 0x80000000)
		{
			return -(hash & 0xFFFF);
		}
		
		return hash & 0xFFFF;
	}

	public static CmdDef getCommand(int keyCode, boolean released, boolean repeated)
	{
		Integer hash1;
		Integer hash2;
		if(released == false)
		{
			hash1 = getKeyHashCode(keyCode, KeyActionDef.KEY_ACTION_PRESS);
			if(repeated)
				hash2 = getKeyHashCode(keyCode, KeyActionDef.KEY_ACTION_PRESS_REPEAT);
			else
				hash2 = getKeyHashCode(keyCode, KeyActionDef.KEY_ACTION_PRESS_FIRST);
		}
		else
		{
			hash1 = getKeyHashCode(keyCode, KeyActionDef.KEY_ACTION_RELEASE);
			if(repeated)
				hash2 = getKeyHashCode(keyCode, KeyActionDef.KEY_ACTION_RELEASE_LONG);
			else
				hash2 = getKeyHashCode(keyCode, KeyActionDef.KEY_ACTION_RELEASE_SHORT);
		}

		Object o = key2cmd.get(hash1);
		if(o == null)
			o = key2cmd.get(hash2);

		System.err.println("code:" + keyCode + ", released: " + released + ", repeated: " + repeated +  
				", hash1: " + hash1 + ", hash2: " + hash2 + ", o: " + o);

		return (CmdDef)o;
	}
	
	public static void mapKeyDef2Cmd(int keyDef, CmdDef cmd)
	{
		System.err.println("Add: " + cmd.name + " hash: " + keyDef);
		key2cmd.put(new Integer(keyDef), cmd);
	}

	public static void mapKey2Cmd(int keyCode, short actionType, CmdDef cmd)
	{
		Integer hash = getKeyHashCode(keyCode, actionType);
		System.err.println("Add: " + cmd.name + " hash: " + hash);
		key2cmd.put(hash, cmd);
	}
	
	public static void loadDefaultKeyCommands()
	{
		key2cmd.clear();
		
		Canvas c = new Canvas()
		{
			public void paint(Graphics g)
			{
			}
		};

		mapKey2Cmd(c.getKeyCode(Canvas.UP), KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdScrollUp);
		mapKey2Cmd(c.getKeyCode(Canvas.DOWN), KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdScrollDown);

		mapKey2Cmd('1', KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdBusStopPrev);
		mapKey2Cmd('2', KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdBusStopNext);

		mapKey2Cmd('3', KeyActionDef.KEY_ACTION_PRESS_FIRST, CmdDef.cmdToggleDayType);

		mapKey2Cmd('4', KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdWindowDecrease);
		mapKey2Cmd('5', KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdWindowIncrease);
		
		mapKey2Cmd('6', KeyActionDef.KEY_ACTION_PRESS_FIRST, CmdDef.cmdScheduleReset);

		mapKey2Cmd('7', KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdWindowShiftDecrease);
		mapKey2Cmd('8', KeyActionDef.KEY_ACTION_PRESS, CmdDef.cmdWindowShiftIncrease);
		
		mapKey2Cmd('9', KeyActionDef.KEY_ACTION_PRESS_FIRST, CmdDef.cmdToggleDetailedDescription);
		mapKey2Cmd('0', KeyActionDef.KEY_ACTION_PRESS_FIRST, CmdDef.cmdToggleFavorite);
		mapKey2Cmd('*', KeyActionDef.KEY_ACTION_PRESS_FIRST, CmdDef.cmdToggleFullSchedule);

		mapKey2Cmd(c.getKeyCode(Canvas.FIRE), KeyActionDef.KEY_ACTION_RELEASE_SHORT, CmdDef.cmdShowBookmarks);
		mapKey2Cmd(c.getKeyCode(Canvas.FIRE), KeyActionDef.KEY_ACTION_RELEASE_LONG, CmdDef.cmdShowAllBusStops);

		mapKey2Cmd('#', KeyActionDef.KEY_ACTION_PRESS_FIRST, CmdDef.cmdScheduleFullScreen);
	}
}
