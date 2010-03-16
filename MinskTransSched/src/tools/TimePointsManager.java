package tools;

import java.util.Hashtable;

public class TimePointsManager
{
	public TimePoint[] points = null;
	public StringWithID[] swids = null;
	
	static TimePointsManager _instance = null;
	public static TimePointsManager GetInstance()
	{
		if(_instance == null)
			_instance = new TimePointsManager();
		return _instance;
	}
	
	public TimePointsManager()
	{
		Load();
	}
	
	public void Load()
	{
		swids = StringWithID.LoadAll();
		points = TimePoint.LoadAll(GetSwIdHashTable());
	}
	
	public Hashtable GetSwIdHashTable()
	{
		Hashtable ht = new Hashtable();
		for (int i = 0; i < swids.length; i++)
		{
			ht.put(new Integer(swids[i].id), swids[i]);
		}
		
		return ht; 

	}

	public void Add(TimePoint tp)
	{
		TimePoint[] old = points;
		points = new TimePoint[old.length + 1];
		for (int i = 0; i < old.length; i++)
		{
			points[i] = old[i];
		}
		points[old.length] = tp;
	}
}
