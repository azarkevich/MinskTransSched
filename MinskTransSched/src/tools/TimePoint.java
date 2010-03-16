package tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

public class TimePoint
{
	public TimePoint()
	{
		Calendar cal = Calendar.getInstance();
		at = cal.getTime();
	}
	public int record = -1;
	public Date at;
	public StringWithID transp = null;
	public StringWithID stop = null;

	public void Save()
	{
		try
		{
			RecordStore store = RecordStore.openRecordStore("timepoints", true);

			ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
			DataOutputStream dos = new DataOutputStream(baos);
			
			dos.writeLong(at.getTime());
			
			if(transp == null)
				dos.writeInt(-1);
			else
				dos.writeInt(transp.id);

			if(stop == null)
				dos.writeInt(-1);
			else
				dos.writeInt(stop.id);

			dos.flush();

			byte[] rec = baos.toByteArray();

			if(record == -1)
				record = store.addRecord(rec, 0, rec.length);
			else
				store.setRecord(record, rec, 0, rec.length);
		}
		catch(Exception ex)
		{
			//#ifdef DEBUG
			System.out.println(ex.toString());
			//#endif
		}
	}

	public static TimePoint[] LoadAll(Hashtable ht)
	{
		TimePoint[] ret = null;
		try
		{
			RecordStore store = RecordStore.openRecordStore("timepoints", true);
			
			ret = new TimePoint[store.getNumRecords()];
			int index = 0;
			RecordEnumeration en = store.enumerateRecords(null, null, true);
			while(en.hasNextElement())
			{
				ret[index].record = en.nextRecordId();
				ByteArrayInputStream bais = new ByteArrayInputStream(store.getRecord(ret[index].record));
				DataInputStream dis = new DataInputStream(bais);

				ret[index].at.setTime(dis.readLong());
				Integer trid = new Integer(dis.readInt());
				if(ht.containsKey(trid))
					ret[index].transp = (StringWithID)ht.get(trid);
				
				Integer stopid = new Integer(dis.readInt());
				if(ht.containsKey(stopid))
					ret[index].stop = (StringWithID)ht.get(stopid);
				
				index++;
			}
		}
		catch(Exception ex)
		{
			//#ifdef DEBUG
			System.out.println(ex.toString());
			//#endif
		}
		return ret;
	}
	
	public void Remove()
	{
		if(record == -1)
			return;

		try
		{
			RecordStore store = RecordStore.openRecordStore("timepoints", true);

			store.deleteRecord(record);
			
			record = -1;
		}
		catch(Exception ex)
		{
			//#ifdef DEBUG
			System.out.println(ex.toString());
			//#endif
		}
	}
}
