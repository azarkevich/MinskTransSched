package options;

import javax.microedition.lcdui.*;

import com.mts.MinskTransSchedMidlet;


import resources.Images;

public class Window extends Form implements OptionsVisualizer, ItemStateListener
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

	public Window()
	{
		super("Настройки");
		
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

		String choices[] = { "Список fav.", "Список всех", "Расписание fav.", "Расписание всех" };
		Image[] imgs = {Images.heart, null, null, null}; 
		startupScreen = new ChoiceGroup("Стартовый экран", Choice.POPUP, choices, imgs);

		append(startupScreen);
		
		scrollSize = new TextField("Скорость скролирования", "", 3, TextField.DECIMAL);
		append(scrollSize);

		String fontSizes[] = { "Малый", "Средний", "Большой" };
		fontSize = new ChoiceGroup("Размер шрифта", Choice.POPUP, fontSizes, null);
		append(fontSize);

		String fontFaces[] = { "SYSTEM", "MONOSPACE", "PROPORTIONAL" };
		fontFace = new ChoiceGroup("Стиль шрифта", Choice.POPUP, fontFaces, null);
		
		append(fontFace);

		fe = new FontExample();
		append(fe);

		lastFontFace = FontExample.fontFace;
		lastFontSize = FontExample.fontSize;
		
		UpdateFontExample();

		ReadSettingToControls();
		
		setItemStateListener(this);
	}
	
	public void itemStateChanged(Item item)
	{
		Alert a = new Alert("aaa");
		MinskTransSchedMidlet.display.setCurrent(a);
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

	public void ReadSettingToControls()
	{
		tfDefWindowSize.setString("" + Options.defWindowSize);
		tfDefWindowSizeStep.setString("" + Options.defWindowSizeStep);
		tfDefWindowShift.setString("" + Options.defWindowShift);
		tfDefWindowShiftStep.setString("" + Options.defWindowShiftStep);
		scrollSize.setString("" + Options.scrollSize);
		
		startupScreen.setSelectedIndex(Options.startupScreen, true);
		
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

	public void SaveSettingsFromControls()
	{
		Options.defWindowSize = Integer.parseInt(tfDefWindowSize.getString());
		Options.defWindowShift = Integer.parseInt(tfDefWindowShift.getString());
		Options.defWindowSizeStep = Integer.parseInt(tfDefWindowSizeStep.getString());
		Options.defWindowShiftStep = Integer.parseInt(tfDefWindowShiftStep.getString());
		Options.startupScreen = (byte)startupScreen.getSelectedIndex();

		Options.fontSize = GetFontSize();
		Options.fontFace = GetFontFace();

		Options.scrollSize = Integer.parseInt(scrollSize.getString());
	}
}
