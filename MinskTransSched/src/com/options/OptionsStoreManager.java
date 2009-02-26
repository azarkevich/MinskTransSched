package com.options;

import java.io.*;

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
				
				dos.writeByte(0);

				dos.writeInt(Options.fontSize);
				dos.writeInt(Options.fontFace);
				dos.writeInt(Options.fontStyle);
				
				dos.writeInt(Options.scrollSize);

				dos.writeBoolean(Options.fullScreen);
				dos.writeBoolean(Options.showExitCommand);
				dos.writeBoolean(Options.showHelpCommand);
				dos.writeBoolean(Options.showAboutCommand);

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
}
