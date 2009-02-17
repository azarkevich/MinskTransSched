package com.options;

import javax.microedition.lcdui.*;

public class KeysPrefs extends Form implements OptionsVisualizer
{
	//class KeyDef
	public KeysPrefs()
	{
		super("Настройки клавиш");
		
		append(new StringItem(null, "Нажмите SELECT, что-бы сменить назначение клавиши, или выберите в меню соответсвующий пункт."));
		
		java.util.Enumeration en = KeyCommands.key2cmd.keys();
		System.err.println(KeyCommands.key2cmd.size());
		while(en.hasMoreElements())
		{
			Integer keyHash = (Integer)en.nextElement();
			CmdDef cmd = (CmdDef )KeyCommands.key2cmd.get(key);
			String cmdName = (String)KeyCommands.cmd2name.get(cmd);
			if(cmdName == null)
				continue;

			StringItem si = new StringItem(cmdName, "Key: " + key); 
			append(si);
//			si.setDefaultCommand(new Command("Изменить", Command.ITEM, 1));
//			si.setItemCommandListener(new ItemCommandListener()
//				{
//					public void commandAction(Command command, Item item)
//					{
//						Alert a = new Alert(command.getLabel());
//						MinskTransSchedMidlet.display.setCurrent(a);
//					}
//				}
//			);
		}
		*/
	}
	
	public void ReadSettingToControls()
	{
	}
	
	public void SaveSettingsFromControls()
	{
	}
}
