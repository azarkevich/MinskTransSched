package tools;

import java.util.Calendar;
import java.util.Date;

public class TimePoint
{
	public TimePoint()
	{
		Calendar cal = Calendar.getInstance();
		at = cal.getTime();
	}
	public Date at;
	public StringWithID transp = null;
	public StringWithID stop = null;
}
