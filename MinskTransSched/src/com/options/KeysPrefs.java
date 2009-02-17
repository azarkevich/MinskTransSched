package com.options;

import javax.microedition.lcdui.*;

public class KeysPrefs extends Form implements OptionsVisualizer
{
	TextField tfDefWindowSize = null;
	TextField tfDefWindowShift = null;
	TextField tfDefWindowSizeStep = null;
	TextField tfDefWindowShiftStep = null;
	
	ChoiceGroup startupScreen = null;

	ChoiceGroup fontSize = null;
	ChoiceGroup fontFace = null;
	
	FontExample fe = null;

	TextField scrollSize = null;

	public KeysPrefs()
	{
		super("Настройки клавиш");
		
		append(new StringItem(null, "Нажмите SELECT, что-бы сменить назначение клавиши, или выберите в меню соответсвующий пункт."));
		
		
//		StringItem si = new StringItem(null, "Настройки окна расписания (мин.)"); 
//		append(si);
//		si.setDefaultCommand(new Command("Изменить", Command.ITEM, 1));
//		si.setItemCommandListener(new ItemCommandListener()
//			{
//				public void commandAction(Command command, Item item)
//				{
//					Alert a = new Alert(command.getLabel());
//					MinskTransSchedMidlet.display.setCurrent(a);
//				}
//			}
//		);
	}
	
	public void ReadSettingToControls()
	{
	}
	
	public void SaveSettingsFromControls()
	{
	}
}
