package text;
import java.util.Vector;
import javax.microedition.lcdui.*;

public class MultiLineText
{
	public int viewportTop;
	int regionX;
	int regionY;
	int regionWidth;
	int regionHeight;
	int lineHeight;
	public int textHeight;
	Vector vecLines = new Vector();    

	public void  MoveDown(int lines)
	{
		if (textHeight > regionHeight)
		{            
			viewportTop = viewportTop - lines * lineHeight;
			if (regionHeight - viewportTop > textHeight)
				viewportTop = regionHeight - textHeight;
		}
	}

	public void MoveEnd()
	{
		viewportTop = regionHeight - textHeight;
	}

	public void MoveUp(int lines)
	{
		if (textHeight > regionHeight)
		{
			viewportTop = viewportTop + lines * lineHeight;
			if (viewportTop > 0)
				viewportTop = 0;
		}

	}

	public void PageUp()
	{
		if (textHeight>regionHeight)
		{
			viewportTop=viewportTop+regionHeight;
			if (viewportTop>0){viewportTop=0;} 
		}

	}

	public void PageDown()
	{
		if (textHeight>regionHeight)
		{
			viewportTop=viewportTop-regionHeight;
			if (regionHeight-viewportTop>textHeight)
			{
				viewportTop=regionHeight-textHeight;
			}
		}         
	}

	Font font = null;
	
	public void SetTextPar(int x, int y, int width, int height, Font f, String s)
	{
		regionX=x;
		regionY=y;
		regionWidth=width;
		regionHeight=height;
		font = f;
		vecLines.removeAllElements();
		lineHeight = font.getHeight();
		viewportTop=0;

		int parBegin = 0;
		while(true)
		{
			int parEnd = s.indexOf("\n", parBegin);
			
			if(parEnd == -1)
			{
				SplitString(font, s.substring(parBegin));
				break;
			}
			
			SplitString(font, s.substring(parBegin, parEnd));
			parBegin = parEnd + 1;
		}
		
		textHeight=vecLines.size() * lineHeight;
	}
	
	void SplitString(Font f, String parText)
	{
		boolean isexit = true;
		int i0=0,i=0,in=0,j,jw=0;
		int imax=parText.length();
		while (isexit)
		{
			i = parText.indexOf(" ", i0 + 1);
			if (i <= i0)
			{
				i=imax;
				isexit=false;
			}

			j=f.stringWidth(parText.substring(i0,i));
			if (jw+j<regionWidth)
			{
				jw=jw+j;
				i0=i;
			}
			else 
			{
				vecLines.addElement(parText.substring(in,i0));                
				in=i0+1;
				jw=j;
				if (j>regionWidth)
				{
					i=i0;
					while (jw>regionWidth)
					{
						j=0;  
						while (j<regionWidth)
						{
							i=i+1;
							j=f.stringWidth(parText.substring(in,i));

						}
						i=i-1;
						j=f.stringWidth(parText.substring(in,i));
						vecLines.addElement(parText.substring(in,i));
						jw=jw-j;
						i0=i;                
						in=i;
					}
					jw=0;                    
				}
				else
				{
					i0=i;
				}                
			}            
		}
		vecLines.addElement(parText.substring(in, imax));
	}

	public boolean justifyCenter = false; 
	public void Draw(Graphics g)
	{
		// save clip & font
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
	
		Font f = g.getFont();

		// set new font & clip
		g.setFont(font);
		g.setClip(regionX, regionY, regionWidth, regionHeight);
		
		int y1=viewportTop;
		for (int i=0;i<vecLines.size();i++)
		{                
			if ((y1 + lineHeight)>0)
			{
				if(justifyCenter)
					g.drawString((String)vecLines.elementAt(i), regionX + 1 + (regionWidth - 2)/2, regionY + y1, Graphics.HCENTER | Graphics.TOP);           
				else
					g.drawString((String)vecLines.elementAt(i), regionX + 1, regionY + y1, Graphics.LEFT | Graphics.TOP);           
			}
			y1=y1 + lineHeight; 
			if (y1>regionHeight)
				break;
		}
		
		// restore clip & font
		g.setClip(clipX, clipY, clipWidth, clipHeight);
		g.setFont(f);
	}
}
