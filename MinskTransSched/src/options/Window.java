package options;

import javax.microedition.lcdui.*;

import resources.Images;

public class Window extends Form implements OptionsVisualizer
{
	TextField tfDefWindowSize = null;
	TextField tfDefWindowShift = null;
	TextField tfDefWindowSizeStep = null;
	TextField tfDefWindowShiftStep = null;
	
	ChoiceGroup startupScreen = null;

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

		String choices[] = { "Список favorites", "Список всех остановок", "Расписание favorites", "Расписание всех остановок" };
		Image[] imgs = {Images.heart, null, null, null}; 
		startupScreen = new ChoiceGroup("Стартовый экран", Choice.EXCLUSIVE, choices, imgs);

		append(startupScreen);

		ReadSettingToControls();
	}

	public void ReadSettingToControls()
	{
		tfDefWindowSize.setString("" + Options.defWindowSize);
		tfDefWindowSizeStep.setString("" + Options.defWindowSizeStep);
		tfDefWindowShift.setString("" + Options.defWindowShift);
		tfDefWindowShiftStep.setString("" + Options.defWindowShiftStep);
		
		startupScreen.setSelectedIndex(Options.startupScreen, true);
	}

	public void SaveSettingsFromControls()
	{
		Options.defWindowSize = Integer.parseInt(tfDefWindowSize.getString());
		Options.defWindowShift = Integer.parseInt(tfDefWindowShift.getString());
		Options.defWindowSizeStep = Integer.parseInt(tfDefWindowSizeStep.getString());
		Options.defWindowShiftStep = Integer.parseInt(tfDefWindowShiftStep.getString());
		Options.startupScreen = (byte)startupScreen.getSelectedIndex();
	}
}
