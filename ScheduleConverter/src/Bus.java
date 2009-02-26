
public class Bus implements Comparable<Bus>
{
	int id;
	String name;
	String route;

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
