package com.options;

public class KeyCommands
{
	public static CmdDef getCommand(int keyCode, boolean released, boolean repeated)
	{
		CmdDef cmd;
		
		// simple press/release
		short action1 = released ? CmdDef.KEY_ACTION_RELEASE : CmdDef.KEY_ACTION_PRESS;
		cmd = CmdDef.getCommandByKeyHash(CmdDef.getKeyHash(keyCode, false, action1).intValue());
		if(cmd != null)
		{
			//System.out.println("1. #" + keyCode + " -> " + ((CmdDef)cmd).name);
			return cmd;
		}

		// detailed PRESS_FIRST/PRESS_REPEAT or RELEASE_SHORT/RELEASE_LONG
		short action2;
		if(released)
			action2 = repeated ? CmdDef.KEY_ACTION_RELEASE_LONG : CmdDef.KEY_ACTION_RELEASE_SHORT;
		else
			action2 = repeated ? CmdDef.KEY_ACTION_PRESS_REPEAT : CmdDef.KEY_ACTION_PRESS_FIRST;

		cmd = CmdDef.getCommandByKeyHash(CmdDef.getKeyHash(keyCode, false, action2).intValue());
		if(cmd != null)
		{
			//System.out.println("2. #" + keyCode + " -> " + ((CmdDef)cmd).name);
			return cmd;
		}

		// not found. may be this is stored as game code?
		if(keyCode < 0)
		{
			int gameCode = CmdDef.getDummyCanvas().getGameAction(keyCode);
			if(gameCode != 0)
			{
				cmd = CmdDef.getCommandByKeyHash(CmdDef.getKeyHash(gameCode, true, action1).intValue());
				if(cmd != null)
				{
					//System.out.println("3. #" + keyCode + " -> " + ((CmdDef)cmd).name);
					return cmd;
				}
	
				cmd = CmdDef.getCommandByKeyHash(CmdDef.getKeyHash(gameCode, true, action2).intValue());
				if(cmd != null)
				{
					//System.out.println("4. #" + keyCode + " -> " + ((CmdDef)cmd).name);
					return cmd;
				}
			}
		}
		
		return null;
	}
}
