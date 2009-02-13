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
				Options.defWindowSize = dis.readShort();
				System.out.println("WindowSize: " + Options.defWindowSize);
				Options.defWindowShift = dis.readShort();
				Options.defWindowSizeStep = dis.readShort();
				Options.defWindowShiftStep = dis.readShort();

				Options.startupScreen = dis.readByte();
				
				Options.fontSize = dis.readInt();
				Options.fontFace = dis.readInt();
				Options.fontStyle = dis.readInt();
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

				dos.writeShort(Options.defWindowSize);
				dos.writeShort(Options.defWindowShift);
				dos.writeShort(Options.defWindowSizeStep);
				dos.writeShort(Options.defWindowShiftStep);
				
				dos.writeByte(Options.startupScreen);

				dos.writeInt(Options.fontSize);
				dos.writeInt(Options.fontFace);
				dos.writeInt(Options.fontStyle);

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
