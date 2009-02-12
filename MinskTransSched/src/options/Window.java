package options;

import javax.microedition.lcdui.*;

public class Window extends Form implements OptionsVisualizer
{
	public static int defWindowSize = 30;
	public static int defWindowShift = -5;
	public static int defWindowSizeStep = 10;
	public static int defWindowShiftStep = 10;

	TextField tfDefWindowSize = null;
	TextField tfDefWindowShift = null;
	TextField tfDefWindowSizeStep = null;
	TextField tfDefWindowShiftStep = null;
	
//	ChoiceGroup investmentChoice = null;
	//String choices[] = { "Stock", "Fund" };
	//investmentChoice = new ChoiceGroup("Type", Choice.EXCLUSIVE, choices, null);
	//append(investmentChoice);

	public Window()
	{
		super("Настройки окна расписания");
		
		//lastDisplayable = 
		
		tfDefWindowSize = new TextField("Размер (мин.)", "", 6, TextField.DECIMAL);
		append(tfDefWindowSize);

		tfDefWindowShift = new TextField("Сдвиг (мин.)", "", 6, TextField.DECIMAL);
		append(tfDefWindowShift);
		
		tfDefWindowSizeStep = new TextField("Шаг размера (мин.)", "", 6, TextField.DECIMAL);
		append(tfDefWindowSizeStep);

		tfDefWindowShiftStep = new TextField("Шаг сдвига (мин.)", "", 6, TextField.DECIMAL);
		append(tfDefWindowShiftStep);
		
		ReadSettingToControls();
	}

	public void ReadSettingToControls()
	{
		tfDefWindowSize.setString("" + defWindowSize);
		tfDefWindowSizeStep.setString("" + defWindowSizeStep);
		tfDefWindowShift.setString("" + defWindowShift);
		tfDefWindowShiftStep.setString("" + defWindowShiftStep);
	}

	public void SaveSettingsFromControls()
	{
		defWindowSize = Integer.parseInt(tfDefWindowSize.getString());
		defWindowShift = Integer.parseInt(tfDefWindowShift.getString());
		defWindowSizeStep = Integer.parseInt(tfDefWindowSizeStep.getString());
		defWindowShiftStep = Integer.parseInt(tfDefWindowShiftStep.getString());
	}
}
