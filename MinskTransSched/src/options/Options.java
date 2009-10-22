package options;

import javax.microedition.lcdui.Font;

public class Options
{
	public static short defWindowSize = 30;
	public static short defWindowShift = -5;
	public static short defWindowSizeStep = 10;
	public static short defWindowShiftStep = 10;
	
	public static int fontSize = Font.SIZE_SMALL;
	public static int fontFace = Font.FACE_SYSTEM;
	public static int fontStyle = Font.STYLE_PLAIN;
	
	public static int scrollSize = 1;
	
	public static boolean fullScreen = true;
	
	public static boolean showExitCommand = true;
	public static boolean showHelpCommand = true;
	public static boolean showAboutCommand = true;
	
	public static boolean showStopsListOnStartup = false;

	public static boolean showFavSymbolInStopList = false;
	
	public static byte lineSpacing = 0;
	
	public static int textColor = 255 << 16 | 255 << 8 | 255;
	public static int favoritesColor = 0 << 16 | 255 << 8 | 0;
}
