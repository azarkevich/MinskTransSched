package options;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import filtering.StopsFavoritesManager;
import filtering.TransportFavoritesManager;

import mts.SchedulerCanvas;
import mts.TransSched;

import resources.Images;


public class OptionsMenu extends List implements CommandListener
{
	static final Command cmdGeneral = new Command("Основные", Command.OK, 2); 
	static final Command cmdControl = new Command("Управление", Command.OK, 2); 
	static final Command cmdFavBuses = new Command("Трансп. избранное", Command.OK, 2); 
	static final Command cmdFavBusStops = new Command("Останов. избранное", Command.OK, 2); 

	SchedulerCanvas schedBoard;

	public OptionsMenu(SchedulerCanvas schedBoard)
	{
		super("Настройки", List.IMPLICIT);

		this.schedBoard = schedBoard;

		setCommandListener(this);
		
		addCommand(TransSched.cmdBack);
		addCommand(TransSched.cmdSelect);
		addCommand(cmdGeneral);
		addCommand(cmdControl);
		
		append(cmdGeneral.getLabel(), null);
		append(cmdControl.getLabel(), null);

		append(cmdFavBuses.getLabel(), Images.hearts);
		append(cmdFavBusStops.getLabel(), Images.hearts);
	}

	public void commandAction(Command c, Displayable d)
	{
		if(c == TransSched.cmdBack)
		{
			TransSched.display.setCurrent(schedBoard);
		}
		else if(c == TransSched.cmdSelect || c == List.SELECT_COMMAND)
		{
			switch (((List)d).getSelectedIndex())
			{
			case 0:
				TransSched.display.setCurrent(new GeneralPrefs(this));
				break;
			case 1:
				TransSched.display.setCurrent(new ControlPrefs(this));
				break;
			case 2:
				TransSched.display.setCurrent(new TransportFavoritesManager(schedBoard, this));
				break;
			case 3:
				TransSched.display.setCurrent(new StopsFavoritesManager(schedBoard, this));
				break;
			}
		}
		else if(c == cmdGeneral)
		{
			TransSched.display.setCurrent(new GeneralPrefs(this));
		}
		else if(c == cmdControl)
		{
			TransSched.display.setCurrent(new ControlPrefs(this));
		}
		else if(c == cmdFavBuses)
		{
			TransSched.display.setCurrent(new TransportFavoritesManager(schedBoard, this));
		}
		else if(c == cmdFavBusStops)
		{
			TransSched.display.setCurrent(new StopsFavoritesManager(schedBoard, this));
		}
	}
}
