import java.util.Vector;

public class Schedule implements Comparable<Schedule>
{
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
	public String from = "";
	public Vector<Integer> times = new Vector<Integer>(50);
}
