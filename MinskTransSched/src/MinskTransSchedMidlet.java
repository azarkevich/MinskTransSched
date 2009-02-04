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
	public void commandAction(Command command, Displayable displayable)
	{
		Display.getDisplay(this).setCurrent(scheduleBoard);
	}
	
	List favorites;
	SchedulerCanvas scheduleBoard;

	public MinskTransSchedMidlet()
	{
		scheduleBoard = new SchedulerCanvas();
//		favorites = new List ("Favorites", Choice.IMPLICIT);
//
//		favorites.append ("Rotor", null);
//		favorites.append ("Lokomotiv", null);
//		favorites.append ("Zenit", null);
//		favorites.setCommandListener(this);
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException
	{
	}

	protected void pauseApp()
	{
	}

	protected void startApp() throws MIDletStateChangeException
	{
		Display.getDisplay(this).setCurrent(scheduleBoard);
//		Display.getDisplay(this).setCurrent(favorites);
	}
}
