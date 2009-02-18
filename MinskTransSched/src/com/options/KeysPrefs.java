package com.options;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.lcdui.*;

import com.mts.MinskTransSchedMidlet;

public class KeysPrefs extends Form implements CommandListener
{
	CommandListener parentCommandLisener;
	public void commandAction(Command cmd, Displayable d)
	{
		boolean handled = false;
		if(d == defineKeyCanvas)
		{
			if(cmd == MinskTransSchedMidlet.cmdBack)
			{
				MinskTransSchedMidlet.display.setCurrentItem(defineKeyCanvas.item);
				handled = true;
			}
			else if(cmd == MinskTransSchedMidlet.cmdOK)
			{
				setKeyText(defineKeyCanvas.item, defineKeyCanvas.hash);
				StringItemsCommandListener sicl = (StringItemsCommandListener)strItem2CommandListener.get(defineKeyCanvas.item);
				if(sicl != null)
				{
					sicl.keyHash = defineKeyCanvas.hash;
				}
				MinskTransSchedMidlet.display.setCurrentItem(defineKeyCanvas.item);
				handled = true;
			}
		}
		else if(d == this)
		{
			//save
			if(cmd == MinskTransSchedMidlet.cmdOK)
			{
				SaveSettings();
			}
			else if(cmd == MinskTransSchedMidlet.cmdReset)
			{
				KeyCommands.loadDefaultKeyCommands();
				OptionsStoreManager.SaveSettings();
				
				LoadSettings();

				handled = true;
			}
		}
		if(handled == false)
			parentCommandLisener.commandAction(cmd, d);
	}

	class StringItemsCommandListener implements ItemCommandListener
	{
		public int keyHash;
		public CmdDef cmd;
		public StringItemsCommandListener(int keyHash, CmdDef cmd)
		{
			this.keyHash = keyHash;
			this.cmd = cmd;
		}
		
		public void commandAction(Command command, Item item)
		{
			defineKeyCanvas.setData(cmd, keyHash, (StringItem)item);
			MinskTransSchedMidlet.display.setCurrent(defineKeyCanvas);
		}
	}
	
	Canvas c;
	
	DefineKey defineKeyCanvas;
	
	Hashtable strItem2CommandListener = new Hashtable();
	
	//class KeyDef
	public KeysPrefs(CommandListener parent)
	{
		super("Настройки клавиш");
		
		parentCommandLisener = parent;
		
		this.addCommand(MinskTransSchedMidlet.cmdOK);
		this.addCommand(MinskTransSchedMidlet.cmdReset);
		this.addCommand(MinskTransSchedMidlet.cmdBack);
		this.setCommandListener(this);

		c = new Canvas()
		{
			public void paint(Graphics g)
			{
			}
		};

		defineKeyCanvas = new DefineKey(this);
		defineKeyCanvas.addCommand(MinskTransSchedMidlet.cmdBack);
		defineKeyCanvas.addCommand(MinskTransSchedMidlet.cmdOK);
		
		LoadSettings();
	}
	
	void LoadSettings()
	{
		this.deleteAll();
		
		append(new StringItem(null, "Нажмите SELECT, что-бы сменить назначение клавиши, или выберите в меню соответсвующий пункт."));
		
		java.util.Enumeration en = KeyCommands.key2cmd.keys();
		while(en.hasMoreElements())
		{
			Integer keyHash = (Integer)en.nextElement();
			CmdDef cmd = (CmdDef )KeyCommands.key2cmd.get(keyHash);
			if(cmd == null)
				continue;

			StringItem si = new StringItem(cmd.name, null);
			si.setLayout(Item.LAYOUT_NEWLINE_BEFORE);
			append(si);
			si.setDefaultCommand(new Command("Изменить", Command.ITEM, 1));
			StringItemsCommandListener sicl = new StringItemsCommandListener(keyHash.intValue(), cmd); 
			si.setItemCommandListener(sicl);
			
			strItem2CommandListener.put(si, sicl);
			
			setKeyText(si, keyHash.intValue());
		}
	}
	
	void setKeyText(StringItem si, int hash)
	{
		int keyCode = KeyCommands.getKeyCodeFromKeyHash(hash);
		try{
			if(KeyCommands.getIsGameCodeFromKeyHash(hash))
				keyCode = c.getKeyCode(keyCode);

			String keyCodeName = null;
			if(keyCode > 32 && keyCode < 127)
				keyCodeName = new String(new char[] { '\'', (char)keyCode , '\''});
			else
				keyCodeName = c.getKeyName(keyCode);
			if(keyCodeName == null)
				keyCodeName = "#" + keyCode;
			si.setText(keyCodeName);
		}
		catch(Exception ex)
		{
			si.setText("<нет>");
		}
	}
	
	void SaveSettings()
	{
		// save settings
		KeyCommands.key2cmd.clear();
		
		Enumeration en = strItem2CommandListener.elements();
		while(en.hasMoreElements())
		{
			StringItemsCommandListener sicl = (StringItemsCommandListener)en.nextElement();
			KeyCommands.mapKeyHash2Cmd(sicl.keyHash, sicl.cmd);
		}
		OptionsStoreManager.SaveSettings();
	}
}
