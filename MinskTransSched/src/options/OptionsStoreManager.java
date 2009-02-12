package options;

import java.io.*;
import javax.microedition.rms.*;

public class OptionsStoreManager
{
	public static void ReadSettings()
	{
		try
		{
			RecordStore sett = RecordStore.openRecordStore("settings", true);

			try
			{
				byte[] rec = sett.getRecord(1);

				ByteArrayInputStream bais = new ByteArrayInputStream(rec);
				DataInputStream dis = new DataInputStream(bais);
				Window.defWindowSize = dis.readShort();
				System.out.println("WindowSize: " + Window.defWindowSize);
				Window.defWindowShift = dis.readShort();
				Window.defWindowSizeStep = dis.readShort();
				Window.defWindowShiftStep = dis.readShort();
			}
			catch(InvalidRecordIDException ex)
			{
				System.out.println(ex.toString());
			}
			catch(IOException ex)
			{
				System.out.println(ex.toString());
			}
			sett.closeRecordStore();
		}
		catch(RecordStoreException ex)
		{
			System.out.println(ex.toString());
		}
	}
	
	public static void SaveSettings()
	{
		try
		{
			RecordStore sett = RecordStore.openRecordStore("settings", true);
			
			try
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
				DataOutputStream dos = new DataOutputStream(baos);

				dos.writeShort(Window.defWindowSize);
				dos.writeShort(Window.defWindowShift);
				dos.writeShort(Window.defWindowSizeStep);
				dos.writeShort(Window.defWindowShiftStep);
				dos.flush();

				byte[] rec = baos.toByteArray();
				
				System.out.println("Out Array length: " + rec.length);
			
				if(sett.getNumRecords() == 0)
					sett.addRecord(rec, 0, rec.length);
				else
					sett.setRecord(1, rec, 0, rec.length);
			}
			catch(InvalidRecordIDException ex)
			{
				System.out.println(ex.toString());
			}

			sett.closeRecordStore();
		}
		catch(Exception ex)
		{
			System.out.println(ex.toString());
		}
	}
}
