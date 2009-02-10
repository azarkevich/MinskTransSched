import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class MinskTransSchedMidlet extends MIDlet implements CommandListener
{
	public BusStop[] busStops;
	
	final static Command cmdExit = new Command("Выход", Command.EXIT, 2);
	final static Command cmdBookMarks = new Command("Закладки", Command.SCREEN, 1);
	final static Command cmdAllBusStops = new Command("Остановки", Command.SCREEN, 2);
	final static Command cmdSelectBusStop = new Command("Выбрать", Command.OK, 1);
	
	SchedulerCanvas scheduleBoard;
	//List bookmarks;
	List allBusStops;
	
	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd == cmdBookMarks)
		{
//			bookmarks.setSelectedIndex(scheduleBoard.getBusStation(), true);
//			Display.getDisplay(this).setCurrent(bookmarks);
		}
		else if(cmd == cmdExit)
		{
			destroyApp(true);
		}
		else if(cmd == cmdAllBusStops)
		{
			allBusStops.setSelectedIndex(scheduleBoard.getBusStation(), true);
			Display.getDisplay(this).setCurrent(allBusStops);
		}
		else if(cmd == cmdSelectBusStop)
		{
			scheduleBoard.setBusStation(((List)d).getSelectedIndex());
			Display.getDisplay(this).setCurrent(scheduleBoard);
		}
	}
	
	public MinskTransSchedMidlet()
	{
	}

	protected void destroyApp(boolean unconditional)
	{
		notifyDestroyed();
	}

	protected void pauseApp()
	{
	}

	protected void startApp() throws MIDletStateChangeException
	{
	    // load data
		ScheduleLoader loader = new ScheduleLoader();
		loader.Load();

		busStops = loader.busStops;
		
		scheduleBoard = new SchedulerCanvas(this);
		scheduleBoard.setCommandListener(this);
		scheduleBoard.addCommand(cmdExit);
		scheduleBoard.addCommand(cmdBookMarks);
		scheduleBoard.addCommand(cmdAllBusStops);

		allBusStops = new List("Остановки", Choice.IMPLICIT);
		allBusStops.setCommandListener(this);
		allBusStops.setSelectCommand(cmdSelectBusStop);
		allBusStops.addCommand(cmdExit);
		
//		bookmarks = new List("Закладки", Choice.IMPLICIT);
//		bookmarks.setCommandListener(this);
//		bookmarks.setSelectCommand(cmdSelectBusStop);
//		bookmarks.addCommand(cmdExit);
		
		for (int i = 0; i < busStops.length; i++)
		{
//			bookmarks.append (busStops[i].name, null);
			allBusStops.append (busStops[i].name, null);
		}

		Display.getDisplay(this).setCurrent(allBusStops);
//		Display.getDisplay(this).setCurrent(bookmarks);
/*
		Alert helloAlert = new Alert("Testing", "Hello, world!", null, AlertType.INFO);
	    helloAlert.setTimeout(Alert.FOREVER);
	    helloAlert.addCommand(cmdExit);
	    helloAlert.setCommandListener(this);
	    Display.getDisplay(this).setCurrent(helloAlert);
*/	    
	}
}
