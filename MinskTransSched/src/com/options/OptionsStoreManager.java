package com.options;

import java.io.*;
import java.util.Enumeration;

import javax.microedition.rms.*;


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

				Options.startupScreen = dis.readByte();
				
				Options.fontSize = dis.readInt();
				Options.fontFace = dis.readInt();
				Options.fontStyle = dis.readInt();
				
				Options.scrollSize = dis.readInt();
			}
			catch(InvalidRecordIDException ex)
			{
				hasErrors = true;
//				System.out.println("sett:" + ex.toString());
			}
			catch(IOException ex)
			{
				hasErrors = true;
				System.out.println(ex.toString());
			}

			try
			{
				byte[] rec = sett.getRecord(SETTINGS_SLOT_KEYS);

				ByteArrayInputStream bais = new ByteArrayInputStream(rec);
				DataInputStream dis = new DataInputStream(bais);
				short keysCount = dis.readShort();
				for (int i = 0; i < keysCount; i++)
				{
					int keyDef = dis.readInt();
					int cmdId = dis.readShort();
					
					CmdDef cmd = CmdDef.getCmd(cmdId);
					if(cmd == null)
						continue;
					KeyCommands.mapKeyDef2Cmd(keyDef, cmd);
				}
			}
			catch(Exception ex)
			{
				KeyCommands.loadDefaultKeyCommands();
				hasErrors = true;
			}

			sett.closeRecordStore();
		}
		catch(RecordStoreException ex)
		{
			System.out.println(ex.toString());
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
				
				dos.writeByte(Options.startupScreen);

				dos.writeInt(Options.fontSize);
				dos.writeInt(Options.fontFace);
				dos.writeInt(Options.fontStyle);
				
				dos.writeInt(Options.scrollSize);

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

				dos.writeShort(KeyCommands.key2cmd.size());
				
				Enumeration en = KeyCommands.key2cmd.keys();
				while(en.hasMoreElements())
				{
					Integer keyDef = (Integer)en.nextElement();
					CmdDef cmd = (CmdDef)KeyCommands.key2cmd.get(keyDef);
					
					dos.writeInt(keyDef.intValue());
					dos.writeShort(cmd.id);
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
}
