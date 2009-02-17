package com.options;

import javax.microedition.lcdui.*;

public class KeysPrefs extends Form implements OptionsVisualizer
{
	class CommandListener implements ItemCommandListener
	{
		int keyHash;
		CmdDef cmd;
		public CommandListener(int keyHash, CmdDef cmd)
		{
			this.keyHash = keyHash;
			this.cmd = cmd;
		}
		
		public void commandAction(Command command, Item item)
		{
			
		}
	}
	
	//class KeyDef
	public KeysPrefs()
	{
		super("Настройки клавиш");
		
		Canvas c = new Canvas()
		{
			public void paint(Graphics g)
			{
			}
		};

		append(new StringItem(null, "Нажмите SELECT, что-бы сменить назначение клавиши, или выберите в меню соответсвующий пункт."));
		
		java.util.Enumeration en = KeyCommands.key2cmd.keys();
		System.err.println(KeyCommands.key2cmd.size());
		while(en.hasMoreElements())
		{
			Integer keyHash = (Integer)en.nextElement();
			CmdDef cmd = (CmdDef )KeyCommands.key2cmd.get(keyHash);
			if(cmd == null)
				continue;
			String cmdName = cmd.name;

			int keyCode = KeyCommands.getKeyCodeFromKeyHashCode(keyHash.intValue());
			String keyCodeName = c.getKeyName(keyCode);
			if(keyCodeName == null)
				keyCodeName = "#" + keyCode;
			StringItem si = new StringItem(cmdName, keyCodeName); 
			append(si);
			si.setDefaultCommand(new Command("Изменить", Command.ITEM, 1));
			si.setItemCommandListener(new CommandListener(keyHash.intValue(), cmd));
		}

	}
	
	public void ReadSettingToControls()
	{
	}
	
	public void SaveSettingsFromControls()
	{
	}
}
