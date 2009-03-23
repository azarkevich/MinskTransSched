package options;

import java.io.*;
import java.util.Vector;

import javax.microedition.rms.*;

import mts.TransSched;

import om.Bus;
import om.BusStop;
import om.FilterDef;




public class OptionsStoreManager
{
	final static int SETTINGS_SLOT_COMMON = 1;
	final static int SETTINGS_SLOT_KEYS = 2;
	public static void ReadSettings()
	{
		boolean hasErrors = false;
		try
		{
			RecordStore sett = RecordStore.openRecordStore("settings", true);

			try
			{
				byte[] rec = sett.getRecord(SETTINGS_SLOT_COMMON);

				ByteArrayInputStream bais = new ByteArrayInputStream(rec);
				DataInputStream dis = new DataInputStream(bais);
				Options.defWindowSize = dis.readShort();
				Options.defWindowShift = dis.readShort();
				Options.defWindowSizeStep = dis.readShort();
				Options.defWindowShiftStep = dis.readShort();

				// was: startup screen
				dis.readByte();
				
				Options.fontSize = dis.readInt();
				Options.fontFace = dis.readInt();
				Options.fontStyle = dis.readInt();
				
				Options.scrollSize = dis.readInt();
				
				Options.fullScreen = dis.readBoolean();

				Options.showExitCommand = dis.readBoolean();
				Options.showHelpCommand = dis.readBoolean();
				Options.showAboutCommand = dis.readBoolean();
				
				Options.showStopsListOnStartup = dis.readBoolean();
			}
			catch(InvalidRecordIDException ex)
			{
				hasErrors = true;
//				System.out.println("sett:" + ex.toString());
			}
			catch(IOException ex)
			{
				hasErrors = true;
//				System.out.println(ex.toString());
			}

			try
			{
				byte[] rec = sett.getRecord(SETTINGS_SLOT_KEYS);

				ByteArrayInputStream bais = new ByteArrayInputStream(rec);
				DataInputStream dis = new DataInputStream(bais);
				short keysCount = dis.readShort();
				for (int i = 0; i < keysCount; i++)
				{
					int keyHash = dis.readInt();
					int cmdId = dis.readShort();
					
					CmdDef cmd = CmdDef.getCmd(cmdId);
					if(cmd != null)
						cmd.setKeyHash(keyHash);
				}
			}
			catch(Exception ex)
			{
				hasErrors = true;
			}

			sett.closeRecordStore();
		}
		catch(RecordStoreException ex)
		{
//			System.out.println(ex.toString());
			hasErrors = true;
		}
		
		
		if(hasErrors)
			SaveSettings();
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
				
				dos.writeByte(0);

				dos.writeInt(Options.fontSize);
				dos.writeInt(Options.fontFace);
				dos.writeInt(Options.fontStyle);
				
				dos.writeInt(Options.scrollSize);

				dos.writeBoolean(Options.fullScreen);
				dos.writeBoolean(Options.showExitCommand);
				dos.writeBoolean(Options.showHelpCommand);
				dos.writeBoolean(Options.showAboutCommand);

				dos.writeBoolean(Options.showStopsListOnStartup);
				
				dos.flush();

				byte[] rec = baos.toByteArray();
				
				if(sett.getNumRecords() == 0)
					sett.addRecord(rec, 0, rec.length);
				else
					sett.setRecord(SETTINGS_SLOT_COMMON, rec, 0, rec.length);
			}
			catch(InvalidRecordIDException ex)
			{
				//System.out.println(ex.toString());
			}

			// save key settings
			try
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
				DataOutputStream dos = new DataOutputStream(baos);

				CmdDef[] all = CmdDef.getAllCommands();
				dos.writeShort(all.length);

				for (int i = 0; i < all.length; i++)
				{
					dos.writeInt(all[i].getKeyHash());
					dos.writeShort(all[i].id);
				}

				dos.flush();

				byte[] rec = baos.toByteArray();
				
				if(sett.getNumRecords() == 1)
					sett.addRecord(rec, 0, rec.length);
				else
					sett.setRecord(SETTINGS_SLOT_KEYS, rec, 0, rec.length);
			}
			catch(InvalidRecordIDException ex)
			{
				//System.out.println(ex.toString());
			}

			sett.closeRecordStore();
		}
		catch(Exception ex)
		{
			//System.out.println(ex.toString());
		}
	}
	
	public static FilterDef[] LoadCustomFilterDefinitions()
	{
		FilterDef[] ret = new FilterDef[0];
		try{
			RecordStore filters = RecordStore.openRecordStore("filters", true);
	
			Vector v = new Vector(filters.getNumRecords());

			for (int i = 0; i < filters.getNumRecords(); i++)
			{
				try{
					int recId = i + 1;
					byte[] rec = filters.getRecord(recId);
					
					ByteArrayInputStream bais = new ByteArrayInputStream(rec);
					DataInputStream dis = new DataInputStream(bais);

					FilterDef fd = readFilterDef(dis);
					fd.recordId = recId;
					v.addElement(fd);
				}
				catch(Exception e)
				{
					
				}
			}
			ret = new FilterDef[v.size()];
			v.copyInto(ret);
		}
		catch(RecordStoreNotOpenException e)
		{
		}
		catch(RecordStoreException e)
		{
		}
		return ret;
	}
	
	public static FilterDef readFilterDef(DataInput dis) throws IOException
	{
		FilterDef fd = new FilterDef();
		
		fd.name = dis.readUTF();
		int transportCount = dis.readShort();
		if(transportCount > 0)
		{
			fd.transport = new Bus[transportCount];
			for (int j = 0; j < transportCount; j++)
			{
				int id = dis.readShort();
				fd.transport[j] = (Bus)TransSched.id2transport.get(new Integer(id));
			}
		}

		int stopsCount = dis.readShort();
		if(stopsCount > 0)
		{
			fd.stops = new BusStop[stopsCount];
			for (int j = 0; j < stopsCount; j++)
			{
				int id = dis.readShort();
				fd.stops[j] = (BusStop)TransSched.id2stop.get(new Integer(id));
			}
		}
		return fd;
	}
	
	public static void saveCustomFilterDefinitions(FilterDef fd)
	{
		try{
			RecordStore filters = RecordStore.openRecordStore("filters", true);

			ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
			DataOutputStream dos = new DataOutputStream(baos);

			dos.writeUTF(fd.name);
			
			if(fd.transport == null)
			{
				dos.writeShort(0);
			}
			else
			{
				dos.writeShort(fd.transport.length);
				for (int i = 0; i < fd.transport.length; i++)
				{
					dos.writeShort(fd.transport[i].id);
				}
			}

			if(fd.stops == null)
			{
				dos.writeShort(0);
			}
			else
			{
				dos.writeShort(fd.stops.length);
				for (int i = 0; i < fd.stops.length; i++)
				{
					dos.writeShort(fd.stops[i].id);
				}
			}
			
			dos.flush();

			byte[] rec = baos.toByteArray();
			
			if(fd.recordId == -1)
				fd.recordId = filters.addRecord(rec, 0, rec.length);
			else
				filters.setRecord(fd.recordId, rec, 0, rec.length);
		}
		catch(Exception e)
		{
		}
	}
	
	public static void deleteCustomFilterDefinitions(FilterDef fd)
	{
		try{
			RecordStore filters = RecordStore.openRecordStore("filters", true);

			filters.deleteRecord(fd.recordId);
			fd.recordId = -1;
		}
		catch(Exception e)
		{
		}
	}
}
