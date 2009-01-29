import java.util.Vector;

public class BusSchedule
{
	public String bus;
	public String busStop;
	
	public String firstBusStop = "";
	public String lastBusStop = "";

	public Short days;

	// -1 backward, 0 - N/A, 1 - forward
	public short direction = 0;
	
	public Vector<Short> times = new Vector(50);
}
