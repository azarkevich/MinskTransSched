package options;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class FontExample extends CustomItem
{
	int prefferedWidth = 0;
	int prefferedHeight = 0;
	String text1 = "Пример шрифта";
	String text2 = "SELECT для обновления";
	public FontExample()
	{
		super(null);
		
		int[] faces = {Font.FACE_SYSTEM, Font.FACE_PROPORTIONAL, Font.FACE_SYSTEM };
		for (int i = 0; i < faces.length; i++)
		{
			Font f = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
			int w1 = f.stringWidth(text1);
			int w2 = f.stringWidth(text2);
			if(prefferedWidth < w1)
				prefferedWidth = w1;
			if(prefferedWidth < w2)
				prefferedWidth = w2;
			
			if(f.getHeight()*2 > prefferedHeight)
				prefferedHeight = f.getHeight()*2; 
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
		Font f = Font.getFont(fontFace, fontStyle, fontSize);
		g.setFont(f);
		g.drawString(text1, 2, 2, Graphics.LEFT | Graphics.TOP);
		g.drawString(text2, 2, f.getHeight(), Graphics.LEFT | Graphics.TOP);
	}
}
