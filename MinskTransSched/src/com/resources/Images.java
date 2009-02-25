package com.resources;
import java.io.IOException;

import javax.microedition.lcdui.Image;

public class Images
{
	public static Image heart = null;
	public static Image hearts = null;

	public static void load()
	{
		try{
			heart = Image.createImage("/heart.png");
			hearts = Image.createImage("/hearts.png");
		}
		catch(IOException ex)
		{
		}
	}
}
