package com.options;

import javax.microedition.lcdui.*;

import com.mts.MinskTransSchedMidlet;

public class ControlPrefs extends List implements CommandListener
{
	static final Command cmdChangeKey = new Command("Изменить", Command.ITEM, 1);
	static final Command cmdActionType = new Command("Дополнительно", Command.ITEM, 2);
	static final Command cmdDescription = new Command("Описание", Command.ITEM, 3);
	static final Command cmdDelete = new Command("Удалить", Command.ITEM, 4);
	static final Command cmdRestore = new Command("Восстановить", Command.ITEM, 5);
	static final Command cmdRestoreAll = new Command("Восстановить всё", Command.ITEM, 6);
	
	public void commandAction(Command cmd, Displayable d)
	{
		boolean handled = false;
		if(cmd == cmdDescription)
		{
			Alert a = new Alert(null);
			a.setString(all[this.getSelectedIndex()].description);
			MinskTransSchedMidlet.display.setCurrent(a);
			handled = true;
		}
		else if(cmd == cmdChangeKey || cmd == List.SELECT_COMMAND)
		{
			if(keyDefiner == null)
				keyDefiner = new DefineKey(this);
			
			int sel = this.getSelectedIndex();
			keyDefiner.setData(all[sel], sel);
			MinskTransSchedMidlet.display.setCurrent(keyDefiner);
			
			handled = true;
		}
		else if(cmd == cmdActionType)
		{
			// TODO
			Alert a = new Alert(null);
			a.setType(AlertType.INFO);
			a.setString("В разработке");
			MinskTransSchedMidlet.display.setCurrent(a);
			handled = true;
		}
		else if(cmd == MinskTransSchedMidlet.cmdHelp)
		{
			// TODO
			Alert a = new Alert(null);
			a.setString("В разработке");
			a.setType(AlertType.ERROR);
			MinskTransSchedMidlet.display.setCurrent(a);
			handled = true;
		}
		else if(cmd == cmdRestore)
		{
			int sel = this.getSelectedIndex();
			all[sel].setDefaultKeyHash();
			set(sel, all[sel].name + ": " + all[sel].getKeyHashName("<нет>"), null);
			handled = true;
		}
		else if(cmd == cmdRestoreAll)
		{
			int sel = this.getSelectedIndex();
			CmdDef.resetAllKeyHashes();
			loadItems();
			setSelectedIndex(sel, true);

			handled = true;
		}
		else if(cmd == cmdDelete)
		{
			int sel = this.getSelectedIndex();

			CmdDef c = all[sel]; 
			
			c.setKeyHash(0);

			set(sel, c.name + ": " + c.getKeyHashName("<нет>"), null);
			
			handled = true;
		}
		else if(cmd == MinskTransSchedMidlet.cmdOK)
		{
			OptionsStoreManager.SaveSettings();
		}
		
		if(handled == false)
			parentCL.commandAction(cmd, d);
	}

	DefineKey keyDefiner;
	CommandListener parentCL;
	CmdDef[] all;
	public ControlPrefs(CommandListener clParent)
	{
		super("Настройки управления", List.IMPLICIT);
		
		parentCL = clParent;
		
		loadItems();
		
		addCommand(MinskTransSchedMidlet.cmdOK);
		addCommand(MinskTransSchedMidlet.cmdCancel);
		addCommand(MinskTransSchedMidlet.cmdHelp);
		addCommand(cmdChangeKey);
		addCommand(cmdDescription);
		addCommand(cmdActionType);
		addCommand(cmdRestore);
		addCommand(cmdRestoreAll);
		addCommand(cmdDelete);
		
		setCommandListener(this);
	}
	
	public void onKeyAssigned(CmdDef cmd, int index, int newHash)
	{
		int keyCode = CmdDef.getKeyCodeFromKeyHash(newHash);
		for (int i = 0; i < all.length; i++)
		{
			if(i == index)
				continue;
			
			int otherKeyCode = CmdDef.getKeyCodeFromKeyHash(all[i].getKeyHash());
			if(otherKeyCode == 0)
				continue;
			
			if(keyCode == otherKeyCode)
			{
				Alert a = new Alert(null);
				a.setString("Данная клавиша связана с другой командой (" + all[i].name + ")");
				a.setType(AlertType.CONFIRMATION);
				MinskTransSchedMidlet.display.setCurrent(a, this);
				return;
			}
		}
		cmd.setKeyHash(newHash);
		set(index, cmd.name + ": " + cmd.getKeyHashName("<нет>"), null);
	}

	void loadItems()
	{
		deleteAll();
		
		all = CmdDef.getAllCommands();
		for (int i = 0; i < all.length; i++)
		{
			append(all[i].name + ": " + all[i].getKeyHashName("<нет>"), null);
		}
	}
}
