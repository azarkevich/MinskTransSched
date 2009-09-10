package filtering;

import java.util.Vector;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import com.sun.midp.midlet.Selector;

import resources.Images;

import mts.Filter;
import mts.SchedulerCanvas;
import mts.TransSched;

import ObjModel.Bus;

public class TransportFilterForm extends Form implements CommandListener, Runnable
{
	boolean stop = false;
	public void run()
	{
		String lastValue = null;
		int lastFCM = -1;
		while(stop == false)
		{
            try
            {
    			Thread.sleep(500);
    			
    			boolean forceFiltering = false;
    			
    			int fcm = mode.getSelectedIndex();
    			if(fcm != lastFCM)
    			{
    				lastFCM = fcm;
    				forceFiltering = true;
    				
    				if(lastFCM == SchedulerCanvas.FILTER_CHANGE_MODE_REPLACE)
    				{
    					baseItems = TransSched.allTransportArray;
    				}
    				else if(lastFCM == SchedulerCanvas.FILTER_CHANGE_MODE_ADD)
    				{
    					// TODO: remove from base list transport, which in current filter
    					baseItems = TransSched.allTransportArray;
    				}
    				else if(lastFCM == SchedulerCanvas.FILTER_CHANGE_MODE_REMOVE)
    				{
    					baseItems = board.filter.buses;
    					if(baseItems == null)
        					baseItems = TransSched.allTransportArray;
    				}
    			}
    			
    			String newValue = tfFilter.getString();
    			if(forceFiltering || lastValue == null || newValue.compareTo(lastValue) != 0)
    			{
    				// filter:
    				lastValue = newValue;

    				current = Filter.FilterByName(baseItems, lastValue);

    				if(busListIndex != -1)
    				{
    					delete(busListIndex);
    				}
    				
    				busList = new ChoiceGroup(null, ChoiceGroup.BUTTON);
    				for (int i = 0; i < current.length; i++)
    				{
    					busList.append(current[i].name, null);
    					busList.setSelectedIndex(i, true);
    				}
    				busListIndex = append(busList);
    			}
            }
            catch( InterruptedException e )
            {
            }
		}
	}
	
	void stopThread()
	{
        try
        {
			stop = true;
			thread.join();
        }
        catch( InterruptedException e )
        {
        }
	}

	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == TransSched.cmdOK)
		{
			stopThread();
			
			Vector vec = new Vector();
			for (int i = 0; i < current.length; i++)
			{
				if(busList.isSelected(i))
				{
					vec.addElement(current[i]);
				}
			}

			current = new Bus[vec.size()];
			vec.copyInto(current);
			
			int fcm = mode.getSelectedIndex();

			board.setBusesFilter(fcm, current);
		}
		else if(cmd == TransSched.cmdCancel)
		{
			TransSched.display.setCurrent(board);
			stopThread();
		}
	}

	SchedulerCanvas board;
	Bus[] baseItems;
	Bus[] current;
	
	TextField tfFilter = null;
	ChoiceGroup busList;
	ChoiceGroup mode;
	int busListIndex = -1;
	
	Thread thread;
	
	public TransportFilterForm(SchedulerCanvas board)
	{
		super("TFilter");
		
		this.board = board;
		this.baseItems = TransSched.allTransportArray;

		addCommand(TransSched.cmdOK);
		addCommand(TransSched.cmdCancel);

		setCommandListener(this);

		mode = new ChoiceGroup(null, ChoiceGroup.POPUP);
		mode.append("Replace", Images.fmc_replace);
		mode.append("Add", Images.fmc_add);
		mode.append("Remove", Images.fmc_remove);
		mode.setSelectedIndex(0, true);
		append(mode);
		
		tfFilter = new TextField("Фильтр", "", 6, TextField.PHONENUMBER);
		append(tfFilter);
		
		thread = new Thread(this);
		thread.start();
	}
}
