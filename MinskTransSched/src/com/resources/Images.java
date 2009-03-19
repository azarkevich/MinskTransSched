package com.resources;
import java.io.IOException;

import javax.microedition.lcdui.Image;

public class Images
{
	public static Image heart = null;
	public static Image hearts = null;
	public static Image stop = null;
	public static Image undo = null;
	public static Image transport = null;
	public static Image busStop = null;
	public static Image transportGray = null;
	public static Image busStopGray = null;
	public static Image predefFilter = null;
	public static Image monkey = null;

	public static void load()
	{
		try{
			heart = Image.createImage("/heart.png");
			hearts = Image.createImage("/hearts.png");
			stop = Image.createImage("/stop.png");
			undo = Image.createImage("/undo.png");
			transport = Image.createImage("/transport.png");
			busStop = Image.createImage("/busStop.png");
			transportGray = Image.createImage("/transport-gray.png");
			busStopGray = Image.createImage("/busStop-gray.png");
			predefFilter = Image.createImage("/builtin-filter.png");
			monkey = Image.createImage("/monkey.png");
		}
		catch(IOException ex)
		{
		}
	}
}
