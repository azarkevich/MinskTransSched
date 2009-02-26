package com.options;

import javax.microedition.lcdui.*;

import com.mts.HelpCanvas;
import com.mts.HelpCanvasSimple;
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
		if(cmd == cmdDescription)
		{
			CmdDef c = all[this.getSelectedIndex()];
			Alert a = new Alert(null);
			a.setString(c.description + "\n" + c.getKeyHashName(true, "<нет>"));
			MinskTransSchedMidlet.display.setCurrent(a);
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
			}
			else if(cmd == MinskTransSchedMidlet.cmdCancel)
			{
				MinskTransSchedMidlet.display.setCurrent(this);
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
		}
		else if(cmd == MinskTransSchedMidlet.cmdHelp)
		{
			MinskTransSchedMidlet.display.setCurrent(new HelpCanvasSimple(HelpCanvas.defineKeyText, this));
		}
		else if(cmd == cmdRestore)
		{
			int sel = this.getSelectedIndex();
			all[sel].setDefaultKeyHash();
			set(sel, all[sel].name + ": " + all[sel].getKeyHashName(true, "<нет>"), null);
		}
		else if(cmd == cmdRestoreAll)
		{
			int sel = this.getSelectedIndex();
			CmdDef.resetAllKeyHashes();
			loadItems();
			setSelectedIndex(sel, true);
		}
		else if(cmd == cmdDelete)
		{
			int sel = this.getSelectedIndex();

			CmdDef c = all[sel]; 
			
			c.setKeyHash(0);

			set(sel, c.name + ": " + c.getKeyHashName(true, "<нет>"), null);
		}
		else if(cmd == MinskTransSchedMidlet.cmdOK)
		{
			if(checkConflicts())
			{
				OptionsStoreManager.SaveSettings();

				for (int i = 0; i < MinskTransSchedMidlet.optionsListeners.length; i++)
					MinskTransSchedMidlet.optionsListeners[i].OptionsUpdated();
				
				MinskTransSchedMidlet.display.setCurrent(next);
			}
		}
		else if(cmd == MinskTransSchedMidlet.cmdCancel)
		{
			// TODO: READ only control prefs
			OptionsStoreManager.ReadSettings();
			MinskTransSchedMidlet.display.setCurrent(next);
		}
	}

	List actionTypeList;
	DefineKey keyDefiner;
	CmdDef[] all;
	Displayable next;
	public ControlPrefs(Displayable next)
	{
		super("Настройки управления", List.IMPLICIT);
		
		this.next = next;
		
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
			actionTypeList = new List("Вид реакции", List.EXCLUSIVE);
			actionTypeList.append("Нажатие с повторами", null);
			actionTypeList.append("Нажатие", null);
			actionTypeList.append("Повторы", null);
			actionTypeList.append("Отжатие", null);
			actionTypeList.append("Отжатие короткое", null);
			actionTypeList.append("Отжатие длинное", null);
			//actionTypeList.setFitPolicy(ChoiceGroup.TEXT_WRAP_ON);

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
	
	boolean checkConflicts()
	{
		for (int i = 0; i < all.length; i++)
		{
			CmdDef c1 = all[i];

			int keyCode1 = c1.getKeyCode();
			if(keyCode1 == 0)
				continue;
			if(c1.getIsGameCode())
				continue;
			short keyAction1 = c1.getActionCode();

			for (int j = 0; j < all.length; j++)
			{
				if(i == j)
					continue;
				
				CmdDef c2 = all[j];
				
				int keyCode2 = c2.getKeyCode();
				if(keyCode2 == 0)
					continue;
				if(c2.getIsGameCode())
					continue;
				short keyAction2 = c2.getActionCode();
				
				// equal keyCodes ?
				if(keyCode1 == keyCode2)
				{
					// keys not interfere only if ACTION1 = SHORT_RELEASE && ACTION2 == LONG_RELEASE
					if(keyAction1 == CmdDef.KEY_ACTION_RELEASE_SHORT && keyAction2 == CmdDef.KEY_ACTION_RELEASE_LONG ||
							keyAction1 == CmdDef.KEY_ACTION_RELEASE_LONG && keyAction2 == CmdDef.KEY_ACTION_RELEASE_SHORT)
						continue;

					Alert a = new Alert(null);
					a.setString(c1.name + "(" + c1.getKeyHashName(true, "") + ")\n" +
							" конфликтует с " +
							c2.name + "(" + c2.getKeyHashName(true, "") + ")");
					a.setTimeout(Alert.FOREVER);
					a.setType(AlertType.ERROR);
					MinskTransSchedMidlet.display.setCurrent(a, this);
					return false;
				}
			}
		}
		return true;
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
