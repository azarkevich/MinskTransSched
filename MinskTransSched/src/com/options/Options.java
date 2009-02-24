package com.options;

import javax.microedition.lcdui.Font;

public class Options
{
	public static short defWindowSize = 30;
	public static short defWindowShift = -5;
	public static short defWindowSizeStep = 10;
	public static short defWindowShiftStep = 10;
	
	public static final int BOOKMARK_SCREEN = 0;
	public static final int ALL_BUSSTOPS_SCREEN = 1;
	// TODO: now show fav list
	public static final int BOOKMARKS_SCHED = 2;
	// TODO: rename it to unfiltered schedule 
	public static final int ALL_BUSSTOPS_SCHED = 3;
	
	public static byte startupScreen = ALL_BUSSTOPS_SCREEN;
	
	public static int fontSize = Font.SIZE_SMALL;
	public static int fontFace = Font.FACE_SYSTEM;
	public static int fontStyle = Font.STYLE_PLAIN;
	
	public static int scrollSize = 1;
	
	public static boolean fullScreen = true;
}
