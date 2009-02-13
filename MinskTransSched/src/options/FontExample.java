package options;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class FontExample extends CustomItem
{
	int prefferedWidth = 0;
	int prefferedHeight = 0;
	String text = "Пример шрифта";
	public FontExample()
	{
		super(null);
		
		int[] faces = {Font.FACE_SYSTEM, Font.FACE_PROPORTIONAL, Font.FACE_SYSTEM };
		for (int i = 0; i < faces.length; i++)
		{
			Font f = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
			int w = f.stringWidth(text);
			if(prefferedWidth < w)
				prefferedWidth = w;
			
			if(f.getHeight() > prefferedHeight)
				prefferedHeight = f.getHeight(); 
		}
		
		prefferedWidth += 4;
		prefferedHeight += 4;
	}
	
	protected int getMinContentHeight()
	{
		return prefferedHeight;
	}

	protected int getMinContentWidth()
	{
		return prefferedWidth;
	}

	protected int getPrefContentHeight(int i)
	{
		return prefferedHeight;
	}

	protected int getPrefContentWidth(int i)
	{
		return prefferedWidth;
	}

	public void update()
	{
		repaint();
	}
	
	public static int fontSize = Font.SIZE_SMALL;
	public static int fontFace = Font.FACE_SYSTEM;
	public static int fontStyle = Font.STYLE_PLAIN;

	protected void paint(Graphics g, int i, int j)
	{
		g.setColor(0, 0, 0);
		g.fillRect(0, 0, prefferedWidth, prefferedHeight);
		g.setColor(0, 255, 0);
		g.setFont(Font.getFont(fontFace, fontStyle, fontSize));
		g.drawString(text, 2, 2, Graphics.LEFT | Graphics.TOP);
	}
}
