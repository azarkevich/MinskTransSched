
public class BusStop implements Comparable<BusStop>
{
	int id;
	String name;
	String officialName;
	String description;

	public int compareTo(BusStop o)
	{
		return name.compareTo(o.name);
	}
}
