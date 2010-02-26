package tools;

//#ifdef DEVTOOLS
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import mts.TransSched;

public class TimePointsForm extends Form implements CommandListener
{
	static final Command cmdNewPoint = new Command("Создать", Command.OK, 1);
	static final Command cmdDelete = new Command("Удалить", Command.OK, 1);

	Displayable next;
	public TimePointsForm(Displayable next)
	{
		super("Временные отметки");
		
		this.next = next;
		
		addCommand(cmdNewPoint);
		addCommand(TransSched.cmdBack);
		
		setCommandListener(this);
	}

	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == TransSched.cmdBack)
		{
			TransSched.display.setCurrent(next);
		}
		else if(cmd == cmdNewPoint)
		{
			TransSched.display.setCurrent(new TimePointForm(this));
		}
	}
}
//#endif