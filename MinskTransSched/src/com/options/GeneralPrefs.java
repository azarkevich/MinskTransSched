package com.options;

import javax.microedition.lcdui.*;

import com.mts.TransSched;

public class GeneralPrefs extends Form implements CommandListener
{
	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == TransSched.cmdOK)
		{
			SaveSettings();
			TransSched.display.setCurrent(next);
		}
		else if(cmd == TransSched.cmdCancel)
		{
			TransSched.display.setCurrent(next);
		}
	}

	TextField tfDefWindowSize = null;
	TextField tfDefWindowShift = null;
	TextField tfDefWindowSizeStep = null;
	TextField tfDefWindowShiftStep = null;
	
	ChoiceGroup fontSize = null;
	ChoiceGroup fontFace = null;
	
	FontExample fe = null;

	TextField scrollSize = null;

	ChoiceGroup fullScreenMode = null;
	ChoiceGroup addExitMenuCG = null;

	ChoiceGroup startWithStopsList = null;

	Displayable next;

	public GeneralPrefs(Displayable next)
	{
		super("Настройки");
		
		this.next = next;

		addCommand(TransSched.cmdOK);
		addCommand(TransSched.cmdCancel);

		setCommandListener(this);

		append(new StringItem(null, "Настройки окна расписания (мин.)"));
		
		tfDefWindowSize = new TextField("Размер", "", 6, TextField.DECIMAL);
		append(tfDefWindowSize);
		
		tfDefWindowShift = new TextField("Сдвиг", "", 6, TextField.DECIMAL);
		append(tfDefWindowShift);
		
		tfDefWindowSizeStep = new TextField("Шаг размера", "", 6, TextField.DECIMAL);
		append(tfDefWindowSizeStep);

		tfDefWindowShiftStep = new TextField("Шаг сдвига", "", 6, TextField.DECIMAL);
		append(tfDefWindowShiftStep);
		
		append(new Spacer(0, 5));

		scrollSize = new TextField("Скорость скролирования", "", 3, TextField.DECIMAL);
		append(scrollSize);

		String fullScreen[] = { "Полноэкрнанное", "Обычное" };
		fullScreenMode = new ChoiceGroup("Расписание", Choice.POPUP, fullScreen, null);
		append(fullScreenMode);

		String startup[] = { "Расписание", "Список остановок" };
		startWithStopsList = new ChoiceGroup("Старт", Choice.POPUP, startup, null);
		append(startWithStopsList);
		
		String addExitMenu[] = { "Выход", "Помощь", "О программе" };
		addExitMenuCG = new ChoiceGroup("Добавлять в меню", Choice.MULTIPLE, addExitMenu, null);
		append(addExitMenuCG);
		
		String fontSizes[] = { "Малый", "Средний", "Большой" };
		fontSize = new ChoiceGroup("Размер шрифта", Choice.POPUP, fontSizes, null);
		append(fontSize);

		String fontFaces[] = { "SYSTEM", "MONOSPACE", "PROPORTIONAL" };
		fontFace = new ChoiceGroup("Стиль шрифта", Choice.POPUP, fontFaces, null);
		append(fontFace);

		fe = new FontExample();
		fe.setItemCommandListener(
				new ItemCommandListener()
				{
					public void commandAction(Command command, Item item)
					{
						UpdateFontExample();
					}
				}
			);
		fe.setDefaultCommand(new Command("Обновить", Command.ITEM, 1));
		append(fe);

		lastFontFace = FontExample.fontFace;
		lastFontSize = FontExample.fontSize;
		
		LoadSettings();

		UpdateFontExample();

		setItemStateListener(
				new ItemStateListener()
				{
					public void itemStateChanged(Item item)
					{
						UpdateFontExample();
					}
				}
		);
	}
	
	int lastFontFace;
	int lastFontSize;
	void UpdateFontExample()
	{
		int newFF = GetFontFace();
		int newFS = GetFontSize();
		if(newFF != lastFontFace || lastFontSize != newFS)
		{
			lastFontFace = newFF; 
			lastFontSize = newFS;
			FontExample.fontFace = newFF;
			FontExample.fontSize = newFS;
			fe.update();
		}
	}

	void LoadSettings()
	{
		tfDefWindowSize.setString("" + Options.defWindowSize);
		tfDefWindowSizeStep.setString("" + Options.defWindowSizeStep);
		tfDefWindowShift.setString("" + Options.defWindowShift);
		tfDefWindowShiftStep.setString("" + Options.defWindowShiftStep);
		scrollSize.setString("" + Options.scrollSize);
		
		switch (Options.fontSize)
		{
		default:
		case Font.SIZE_SMALL:
			fontSize.setSelectedIndex(0, true);
			break;
		case Font.SIZE_MEDIUM:
			fontSize.setSelectedIndex(1, true);
			break;
		case Font.SIZE_LARGE:
			fontSize.setSelectedIndex(2, true);
			break;
		}
		
		switch (Options.fontFace)
		{
		default:
		case Font.FACE_SYSTEM:
			fontFace.setSelectedIndex(0, true);
			break;
		case Font.FACE_MONOSPACE:
			fontFace.setSelectedIndex(1, true);
			break;
		case Font.FACE_PROPORTIONAL:
			fontFace.setSelectedIndex(2, true);
			break;
		}
		
		fullScreenMode.setSelectedIndex(Options.fullScreen ? 0 : 1, true);
		if(Options.showExitCommand)
			addExitMenuCG.setSelectedIndex(0, true);
		if(Options.showHelpCommand)
			addExitMenuCG.setSelectedIndex(1, true);
		if(Options.showAboutCommand)
			addExitMenuCG.setSelectedIndex(2, true);
		
		startWithStopsList.setSelectedIndex(0, !Options.showStopsListOnStartup);
		startWithStopsList.setSelectedIndex(1, Options.showStopsListOnStartup);
	}
	
	int GetFontSize()
	{
		switch (fontSize.getSelectedIndex())
		{
		case 1:
			return Font.SIZE_MEDIUM;
		case 2:
			return Font.SIZE_LARGE;
		}
		return Font.SIZE_SMALL;
	}

	int GetFontFace()
	{
		switch (fontFace.getSelectedIndex())
		{
		case 1:
			return Font.FACE_MONOSPACE;
		case 2:
			return Font.FACE_PROPORTIONAL;
		}
		return Font.FACE_SYSTEM;
	}

	void SaveSettings()
	{
		Options.defWindowSize = Short.parseShort(tfDefWindowSize.getString());
		Options.defWindowShift = Short.parseShort(tfDefWindowShift.getString());
		Options.defWindowSizeStep = Short.parseShort(tfDefWindowSizeStep.getString());
		Options.defWindowShiftStep = Short.parseShort(tfDefWindowShiftStep.getString());

		Options.fontSize = GetFontSize();
		Options.fontFace = GetFontFace();

		Options.scrollSize = Integer.parseInt(scrollSize.getString());

		Options.fullScreen = (fullScreenMode.getSelectedIndex() == 0) ? true : false;

		Options.showExitCommand = addExitMenuCG.isSelected(0); 
		Options.showHelpCommand = addExitMenuCG.isSelected(1); 
		Options.showAboutCommand = addExitMenuCG.isSelected(2); 
		
		Options.showStopsListOnStartup = startWithStopsList.isSelected(1); 

		OptionsStoreManager.SaveSettings();
		
		for (int i = 0; i < TransSched.optionsListeners.length; i++)
		{
			TransSched.optionsListeners[i].OptionsUpdated();
		}
	}
}
