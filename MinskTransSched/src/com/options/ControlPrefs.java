package com.options;

import javax.microedition.lcdui.*;

import com.mts.HelpCanvas;
import com.mts.MinskTransSchedMidlet;

public class ControlPrefs extends List implements CommandListener
{
	static final Command cmdChangeKey = new Command("Изменить", Command.ITEM, 1);
	static final Command cmdDescription = new Command("Описание", Command.ITEM, 3);
	static final Command cmdDelete = new Command("Удалить", Command.ITEM, 4);
	static final Command cmdRestore = new Command("Восстановить", Command.ITEM, 5);
	static final Command cmdRestoreAll = new Command("Восстановить всё", Command.ITEM, 6);
	
	public void commandAction(Command cmd, Displayable d)
	{
		boolean handled = false;
		if(cmd == cmdDescription)
		{
			CmdDef c = all[this.getSelectedIndex()];
			Alert a = new Alert(null);
			a.setString(c.description + "\n" + c.getKeyHashName(true, "<нет>"));
			MinskTransSchedMidlet.display.setCurrent(a);
			handled = true;
		}
		else if(d == actionTypeList)
		{
			if(cmd == MinskTransSchedMidlet.cmdSelect || cmd == List.SELECT_COMMAND)
			{
				CmdDef c = all[this.getSelectedIndex()];

				short actionCode = (short)actionTypeList.getSelectedIndex();
				int hash = c.getKeyHash();
				int keyCode = CmdDef.getKeyCodeFromKeyHash(hash);
				boolean isGameCode = CmdDef.getIsGameCodeFromKeyHash(hash);
				int newHash = CmdDef.getKeyHash(keyCode, isGameCode, actionCode).intValue();
				onKeyActionAssigned(c, newHash);
				
				MinskTransSchedMidlet.display.setCurrent(this);

				handled = true;
			}
			else if(cmd == MinskTransSchedMidlet.cmdCancel)
			{
				MinskTransSchedMidlet.display.setCurrent(this);
				handled = true;
			}
		}
		else if(cmd == cmdChangeKey || cmd == List.SELECT_COMMAND)
		{
			int sel = this.getSelectedIndex();
			CmdDef c = all[sel];

			if(keyDefiner == null)
				keyDefiner = new DefineKey(this);
			
			keyDefiner.setData(c, sel);
			MinskTransSchedMidlet.display.setCurrent(keyDefiner);
			handled = true;
		}
		else if(cmd == MinskTransSchedMidlet.cmdHelp)
		{
			if(helpCanvas == null)
			{
				helpCanvas = new HelpCanvas(HelpCanvas.defineKeyText);
				helpCanvas.addCommand(MinskTransSchedMidlet.cmdBack);
				helpCanvas.addCommand(MinskTransSchedMidlet.cmdExit);
				helpCanvas.setCommandListener(this);
			}
			MinskTransSchedMidlet.display.setCurrent(helpCanvas);
			handled = true;
		}
		else if(cmd == cmdRestore)
		{
			int sel = this.getSelectedIndex();
			all[sel].setDefaultKeyHash();
			set(sel, all[sel].name + ": " + all[sel].getKeyHashName(true, "<нет>"), null);
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

			set(sel, c.name + ": " + c.getKeyHashName(true, "<нет>"), null);
			
			handled = true;
		}
		else if(cmd == MinskTransSchedMidlet.cmdOK)
		{
			OptionsStoreManager.SaveSettings();
		}
		else if(cmd == MinskTransSchedMidlet.cmdCancel)
		{
			OptionsStoreManager.ReadSettings();
		}
		else if(cmd == MinskTransSchedMidlet.cmdBack && d == helpCanvas)
		{
			MinskTransSchedMidlet.display.setCurrent(this);
			handled = true;
		}
		
		if(handled == false)
			parentCL.commandAction(cmd, d);
	}

	List actionTypeList;
	DefineKey keyDefiner;
	CommandListener parentCL;
	HelpCanvas helpCanvas;
	CmdDef[] all;
	public ControlPrefs(CommandListener clParent)
	{
		super("Настройки управления", List.IMPLICIT);
		
		parentCL = clParent;
		
		setFitPolicy(Choice.TEXT_WRAP_ON);
		
		loadItems();
		
		addCommand(MinskTransSchedMidlet.cmdOK);
		addCommand(MinskTransSchedMidlet.cmdCancel);
		addCommand(MinskTransSchedMidlet.cmdHelp);
		addCommand(cmdChangeKey);
		addCommand(cmdDescription);
		addCommand(cmdRestore);
		addCommand(cmdRestoreAll);
		addCommand(cmdDelete);
		
		setCommandListener(this);
	}
	
	public void onKeyAssigned(CmdDef cmd, int index, int newHash)
	{
		cmd.setKeyHash(newHash);
		int actionCode = CmdDef.getActionCodeFromKeyHash(newHash);

		if(actionTypeList == null)
		{
			actionTypeList = new List("Вид реакции", List.IMPLICIT);
			actionTypeList.append("Нажатие с повторами", null);
			actionTypeList.append("Нажатие", null);
			actionTypeList.append("Повторы", null);
			actionTypeList.append("Отжатие", null);
			actionTypeList.append("Отжатие короткое", null);
			actionTypeList.append("Отжатие длинное", null);
			actionTypeList.setFitPolicy(ChoiceGroup.TEXT_WRAP_ON);

			actionTypeList.addCommand(MinskTransSchedMidlet.cmdCancel);
			actionTypeList.addCommand(MinskTransSchedMidlet.cmdSelect);
			actionTypeList.setCommandListener(this);
		}
		
		actionTypeList.setSelectedIndex(actionCode, true);

		MinskTransSchedMidlet.display.setCurrent(actionTypeList);
	}
	
	public void onKeyActionAssigned(CmdDef cmd, int newHash)
	{
		cmd.setKeyHash(newHash);
		set(this.getSelectedIndex(), cmd.name + ": " + cmd.getKeyHashName(true, "<нет>"), null);
	}
	
	void checkConflicts()
	{
//		int keyCode = CmdDef.getKeyCodeFromKeyHash(newHash);
//		short actionType = CmdDef.getActionCodeFromKeyHash(newHash);
//		for (int i = 0; i < all.length; i++)
//		{
//			if(i == index)
//				continue;
//			
//			int otherKeyCode = CmdDef.getKeyCodeFromKeyHash(all[i].getKeyHash());
//			if(otherKeyCode == 0)
//				continue;
//			
//			if(keyCode != otherKeyCode)
//				continue;
//				
//			int otherActionType = CmdDef.getActionCodeFromKeyHash(all[i].getKeyHash());
//
//			if()
//				
//			Alert a = new Alert(null);
//			a.setString("Данная клавиша связана с другой командой: " + all[i].name);
//			MinskTransSchedMidlet.display.setCurrent(a, this);
//			return;
//		}
	}

	void loadItems()
	{
		deleteAll();
		
		all = CmdDef.getAllCommands();
		for (int i = 0; i < all.length; i++)
		{
			append(all[i].name + ": " + all[i].getKeyHashName(true, "<нет>"), null);
		}
	}
}
