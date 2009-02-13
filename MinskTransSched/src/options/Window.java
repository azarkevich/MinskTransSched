package options;

import javax.microedition.lcdui.*;

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
		
		append(new StringItem("Настройки", "Окно расписания, мин."));
		
		tfDefWindowSize = new TextField("Размер", "", 6, TextField.DECIMAL);
		append(tfDefWindowSize);

		tfDefWindowShift = new TextField("Сдвиг", "", 6, TextField.DECIMAL);
		append(tfDefWindowShift);
		
		tfDefWindowSizeStep = new TextField("Шаг размера", "", 6, TextField.DECIMAL);
		append(tfDefWindowSizeStep);

		tfDefWindowShiftStep = new TextField("Шаг сдвига", "", 6, TextField.DECIMAL);
		append(tfDefWindowShiftStep);
		
		append(new StringItem("Настройки", "Стартовый экран"));

		String choices[] = { "Список favorites", "Список всех остановок", "Расписание favorites", "Расписание всех остановок" };
		startupScreen = new ChoiceGroup(null, Choice.EXCLUSIVE, choices, null);
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
