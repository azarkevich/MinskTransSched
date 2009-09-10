package filtering;

import java.util.Vector;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

import resources.Images;

import mts.SchedulerCanvas;
import mts.TransSched;
import ObjModel.Bus;
import ObjModel.BusStop;
import ObjModel.Schedule;

public class TransportOnStop extends List implements CommandListener
{
	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == TransSched.cmdOK)
		{
			BusStop bs = board.getCurrentBusStop();
			
			if(bs != null)
			{
				Vector add = new Vector();
				Vector remove = new Vector();
				for (int i = 0; i < bs.schedules.length; i++)
				{
					Schedule s = bs.schedules[i];
					Bus b = s.bus;
					if(getImage(i) == Images.visible)
						add.addElement(b);
					else
						remove.addElement(b);
				}
				
				Bus[] add_buses = new Bus[add.size()];
				Bus[] rem_buses = new Bus[remove.size()];
				add.copyInto(add_buses);
				remove.copyInto(rem_buses);
				board.filter.changeTransportFilter(SchedulerCanvas.FILTER_CHANGE_MODE_ADD, add_buses);
				board.filter.changeTransportFilter(SchedulerCanvas.FILTER_CHANGE_MODE_REMOVE, rem_buses);
				board.RefreshScheduleText();
			}
			TransSched.display.setCurrent(board);
		}
		else if(cmd == TransSched.cmdCancel)
		{
			TransSched.display.setCurrent(board);
		}
		else if(cmd == List.SELECT_COMMAND)
		{
			int el = getSelectedIndex();
			Image img = getImage(el);
			if(img == Images.invisible)
				img = Images.visible;
			else
				img = Images.invisible;
			
			set(el, getString(el), img);
		}
	}

	SchedulerCanvas board;
	ChoiceGroup busList;
	
	public TransportOnStop(SchedulerCanvas board)
	{
		super("TOS", List.IMPLICIT);
		
		this.board = board;
		
		BusStop bs = board.getCurrentBusStop();
		
		if(bs != null)
		{
			for (int i = 0; i < bs.schedules.length; i++)
			{
				Schedule s =  bs.schedules[i];
				Bus b = s.bus;
				
				Image img = Images.invisible;
				if(board.filter.busesFilter.containsKey(b))
					img = Images.visible;
				append(b.name, img);
			}
		}
		
		addCommand(List.SELECT_COMMAND);
		addCommand(TransSched.cmdOK);
		addCommand(TransSched.cmdCancel);

		setCommandListener(this);
	}

}
