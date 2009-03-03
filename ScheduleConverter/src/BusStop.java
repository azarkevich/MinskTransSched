
public class BusStop implements Comparable<BusStop>
{
	static int nextId = 0;

	int id;
	String name;
	String officialName;
	String description;
	
	public BusStop() throws Exception
	{
		id = nextId++;
		if(id > Byte.MAX_VALUE)
			throw new Exception("BusStop ID exceed store size");
	}

	public int compareTo(BusStop o)
	{
		return name.compareTo(o.name);
	}
}
