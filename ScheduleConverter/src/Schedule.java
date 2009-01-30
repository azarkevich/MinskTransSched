import java.util.Vector;

public class Schedule implements Comparable<Schedule>
{
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
	public short bus;
	public short busStop;
	public short days;
	public Vector<Short> times = new Vector<Short>(50);
}
