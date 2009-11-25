package resources;
import java.io.IOException;

import javax.microedition.lcdui.Image;

public class Images
{
	public static Image heart = null;
	public static Image hearts = null;
	public static Image transport = null;
	public static Image busStop = null;
	public static Image predefFilter = null;
	public static Image monkey = null;

	public static Image fmc_replace = null;
	public static Image fmc_add = null;
	public static Image fmc_remove = null;

	public static Image visible = null;
	public static Image invisible = null;
	
	public static void load()
	{
		try{
			heart = Image.createImage("/heart.png");
			hearts = Image.createImage("/hearts.png");
			transport = Image.createImage("/transport.png");
			busStop = Image.createImage("/busStop.png");
			predefFilter = Image.createImage("/builtin-filter.png");
			monkey = Image.createImage("/monkey.png");
			fmc_replace = Image.createImage("/fmc_replace.png");
			fmc_add = Image.createImage("/fmc_add.png");
			fmc_remove = Image.createImage("/fmc_remove.png");
			
			visible = fmc_add;
			invisible = fmc_remove;
		}
		catch(IOException ex)
		{
		}
	}
}
