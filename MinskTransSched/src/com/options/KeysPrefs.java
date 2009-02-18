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
	
	Canvas c;
	
	//class KeyDef
	public KeysPrefs()
	{
		super("Настройки клавиш");
		
		c = new Canvas()
		{
			public void paint(Graphics g)
			{
			}
		};
		
		ReadSettingToControls();
	}
	
	public void ReadSettingToControls()
	{
		this.deleteAll();
		
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

			int keyCode = KeyCommands.getKeyCodeFromKeyHash(keyHash.intValue());
			String keyCodeName = null;
			try{
				if(KeyCommands.getIsGameCodeFromKeyHash(keyHash.intValue()))
					keyCode = c.getKeyCode(keyCode);
				if(keyCode > 32 && keyCode < 127)
					keyCodeName = new String(new char[] { '\'', (char)keyCode , '\''});
				else
					keyCodeName = c.getKeyName(keyCode);
				if(keyCodeName == null)
					keyCodeName = "#" + keyCode;
			}
			catch(Exception ex)
			{
				keyHash = 0;
				keyCodeName = "<none>";
			}
			StringItem si = new StringItem(cmdName, keyCodeName); 
			append(si);
			si.setDefaultCommand(new Command("Изменить", Command.ITEM, 1));
			si.setItemCommandListener(new CommandListener(keyHash.intValue(), cmd));
		}
	}
	
	public void SaveSettingsFromControls()
	{
	}
}
