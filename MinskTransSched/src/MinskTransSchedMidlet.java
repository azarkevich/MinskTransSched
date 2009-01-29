import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class MinskTransSchedMidlet extends MIDlet
{
	public MinskTransSchedMidlet()
	{
		Display.getDisplay(this).setCurrent(new SchedulerCanvas());
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException
	{
	}

	protected void pauseApp()
	{
	}

	protected void startApp() throws MIDletStateChangeException
	{
	}
}
