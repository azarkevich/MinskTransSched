import java.util.Vector;

public class Schedule implements Comparable<Schedule>
{
	public static final int ALLDAY = 0;
	public static final int WORKDAY = 8;
	public static final int HOLIDAY = 9;

	public int compareTo(Schedule o)
	{
		if(busStop > o.busStop)
			return 1;
		else if(busStop < o.busStop)
			return -1;
		else if(bus > o.bus)
			return 1;
		else if(bus < o.bus)
			return -1;
		
		return 0;
	}
	public int bus;
	public int busStop;
	public int day;
	public int schedFrom = SCHED_FROM_UNKNOWN;
	
	public static final int SCHED_FROM_UNKNOWN = 0; 
	public static final int SCHED_FROM_MINSK_TRANS_SITE = 1; 
	public static final byte SCHED_FROM_BUSSTOP = 2; 

	public Vector<Integer> times = new Vector<Integer>(50);
}
