
public class BusSchedule
{
	public BusInfo Bus;

	// indexes according to Calendar.SUNDAY ... Calendar.SATURDAY, 0 for holidays, 8 for work days
	public short[][] Times = new short[9][];
	
	public static final int HOLIDAY = 0;
	public static final int WORKDAY = 8;
}
