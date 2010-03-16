package tools;

//#ifdef DEVTOOLS
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import mts.TransSched;
public class TimePointForm extends Form implements CommandListener
{
	TimePoint tp;
	
	Displayable next;

	ChoiceGroup transport;
	ChoiceGroup stops;

	public TimePointForm(Displayable next)
	{
		super("Отметка");
		
		this.next = next;
		
		//addCommand(cmdNewPoint);
		addCommand(TransSched.cmdCancel);
		addCommand(TransSched.cmdOK);
		
		setCommandListener(this);
		
		tp = new TimePoint();

		append(new StringItem("Время: ", tp.at.toString()));
		
		transport = new ChoiceGroup(null, ChoiceGroup.POPUP);
		transport.append("<none>", null);
		
		stops = new ChoiceGroup(null, ChoiceGroup.POPUP);
		stops.append("<none>", null);

		StringWithID[] arr = TimePointsManager.GetInstance().swids; 
		for(int i=0;i<arr.length;i++)
		{
			if(arr[i].SwIdType == StringWithID.SWID_TYPE_TRANSPORT)
				transport.append(arr[i].str, null);
			else if(arr[i].SwIdType == StringWithID.SWID_TYPE_STOP)
				stops.append(arr[i].str, null);
		}
		transport.setSelectedIndex(0, true);
		append(transport);
		
		stops.setSelectedIndex(0, true);
		append(stops);
	}

	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == TransSched.cmdCancel)
		{
			TransSched.display.setCurrent(next);
		}
		else if(cmd == TransSched.cmdOK)
		{
			tp.Save();
			TimePointsManager.GetInstance().Add(tp);
			TransSched.display.setCurrent(next);
		}
	}
}
//#endif
