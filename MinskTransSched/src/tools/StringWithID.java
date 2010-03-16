package tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

public class StringWithID
{
	public StringWithID(int id, String s)
	{
		this.id = id;
		this.str = s;
	}
	public int record = -1;
	public int id;
	public String str;
	
	public static final int SWID_TYPE_TRANSPORT = 0;
	public static final int SWID_TYPE_STOP = 0;
	
	public int SwIdType = SWID_TYPE_TRANSPORT;

	public void Save()
	{
		try
		{
			RecordStore store = RecordStore.openRecordStore("timepoints-swid", true);

			ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
			DataOutputStream dos = new DataOutputStream(baos);
			
			dos.writeInt(id);
			dos.writeInt(SwIdType);
			dos.writeUTF(str);

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

	public static StringWithID[] LoadAll()
	{
		StringWithID[] ret = null;
		try
		{
			RecordStore store = RecordStore.openRecordStore("timepoints-swid", true);
			
			ret = new StringWithID[store.getNumRecords()];
			int index = 0;
			RecordEnumeration en = store.enumerateRecords(null, null, true);
			while(en.hasNextElement())
			{
				ret[index].record = en.nextRecordId();
				ByteArrayInputStream bais = new ByteArrayInputStream(store.getRecord(ret[index].record));
				DataInputStream dis = new DataInputStream(bais);
				
				ret[index].id = dis.readInt();
				ret[index].SwIdType = dis.readInt();
				ret[index].str = dis.readUTF();
				
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
			RecordStore store = RecordStore.openRecordStore("timepoints-swid", true);

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
