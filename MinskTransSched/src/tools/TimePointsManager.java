package tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.rms.*;

public class TimePointsManager
{
	public TimePoint[] points = null;
	public StringWithID[] transport = null;
	public StringWithID[] stops = null;
	
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
		transport = new StringWithID[] { new StringWithID(-1, "<none>"), new StringWithID(0, "25") };	
		stops = new StringWithID[] { new StringWithID(-1, "<none>"), new StringWithID(0, "Импульс"),  new StringWithID(0, "С работы")};
		
		try
		{
			RecordStore store = RecordStore.openRecordStore("timepoints", false);
			if(store != null)
			{
				RecordEnumeration en = store.enumerateRecords(null, null, true);
				while(en.hasNextElement())
				{
					byte[] rec = en.nextRecord();

					ByteArrayInputStream bais = new ByteArrayInputStream(rec);
					DataInputStream dis = new DataInputStream(bais);
					
					TimePoint tp = new TimePoint();
					tp.at.setTime(dis.readLong());
					int transp = dis.readInt();
					tp.transp = transport[0];
					for (int i = 0; i < transport.length; i++)
					{
						if(transp == transport[i].id)
						{
							tp.transp = transport[i];
							break;
						}
					}
					
					int stop = dis.readInt();
					tp.stop = stops[0];
					for (int i = 0; i < stops.length; i++)
					{
						if(stop == stops[i].id)
						{
							tp.stop = stops[i];
							break;
						}
					}
					
					System.out.println("added");
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex.toString());
		}
		
		points = new TimePoint[] { new TimePoint(), new TimePoint()};
	}

	public void Add(TimePoint tp)
	{
		tp.stop = stops[0];
		tp.transp = transport[0];
		
		TimePoint[] old = points;
		points = new TimePoint[old.length + 1];
		for (int i = 0; i < old.length; i++)
		{
			points[i] = old[i];
		}
		points[old.length] = tp;

		try
		{
			RecordStore store = RecordStore.openRecordStore("timepoints", true);

			ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
			DataOutputStream dos = new DataOutputStream(baos);
			
			for (int i = 0; i < points.length; i++)
			{
				dos.writeLong(points[i].at.getTime());
				dos.writeInt(points[i].transp.id);
				dos.writeInt(points[i].stop.id);

				dos.flush();

				byte[] rec = baos.toByteArray();

				store.addRecord(rec, 0, rec.length);
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex.toString());
		}
	}
}
