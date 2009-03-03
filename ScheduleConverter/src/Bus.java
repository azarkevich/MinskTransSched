
public class Bus implements Comparable<Bus>
{
	static int nextId = 0;

	int id;
	String name;
	int startRoute = -1;
	int endRoute = -1;

	public Bus() throws Exception
	{
		id = nextId++;
		if(id > Byte.MAX_VALUE)
			throw new Exception("Bus ID exceed store size");
	}

	public int compareTo(Bus o)
	{
		int num1 = Integer.parseInt(name.replaceAll("[^0-9]*", ""));
		int num2 = Integer.parseInt(o.name.replaceAll("[^0-9]*", ""));
		if(num1 < num2)
			return -1;
		else if(num1 > num2)
			return 1;
		else
			return 0;
	}
}
