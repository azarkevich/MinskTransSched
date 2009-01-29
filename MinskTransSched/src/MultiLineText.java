import java.util.Vector;
import javax.microedition.lcdui.*;

public class MultiLineText
{
	public int Top;         //Положение верхнего края текста
	private int x,y,w,h,fsz,fst,fty;    //Размер ограничивающего прямоугольника;
	private int hStr;       //Высота строки
	private int dy;         //Шаг при прокрутке текста
	private int textheight; //Общая высота текста
	private Vector vecLines;    
	private int gx,gy,gw,gh; //Исходная область

	public void  MoveDown()
	{
		if (textheight>h)
		{            
			Top=Top-dy;
			if (h-Top>textheight) {Top=h-textheight;}
		}
	}

	public void MoveUp()
	{
		if (textheight>h)
		{
			Top=Top+dy;
			if (Top>0){Top=0;}
		}

	}

	public void PageUp()
	{
		if (textheight>h)
		{
			Top=Top+h;
			if (Top>0){Top=0;} 
		}

	}

	public void PageDown()
	{
		if (textheight>h)
		{
			Top=Top-h;
			if (h-Top>textheight)
			{
				Top=h-textheight;
			}
		}         
	}

	public void SetTextPar(
			int x, 
			int y,
			int width,
			int height,
			int dy,
			int FontSize,
			int FontStyle,
			int FontType,
			Graphics g,
			String parText
	)
	{
		this.x=x;
		this.y=y;
		this.w=width;
		this.h=height;
		this.dy=dy;
		this.fsz=FontSize;
		this.fst=FontStyle;
		this.fty=FontType;
		gx=g.getClipX();
		gy=g.getClipY();
		gw=g.getClipWidth();
		gh=g.getClipHeight();
		g.setFont(Font.getFont(fty, fst, fsz));
		//Разбиваем строку на массив строк
		vecLines = new Vector(1);
		hStr=g.getFont().getHeight();
		Top=0;

		int parBegin = 0;
		while(true)
		{
			int parEnd = parText.indexOf("\n", parBegin);
			
			if(parEnd == -1)
			{
				SplitString(g, parText.substring(parBegin));
				break;
			}
			
			SplitString(g, parText.substring(parBegin, parEnd));
			parBegin = parEnd + 1;
		}
		
		textheight=vecLines.size()*hStr;
	}
	
	void SplitString(Graphics g, String parText)
	{
		boolean isexit = true;
		int i0=0,i=0,in=0,j,jw=0;   //Смещение от начала строки
		int imax=parText.length();   //Длина строки
		while (isexit)
		{
			i = parText.indexOf(" ", i0 + 1);
			if (i <= i0)
			{
				i=imax;
				isexit=false;
			}

			j=g.getFont().stringWidth(parText.substring(i0,i));
			if (jw+j<w)
			{
				//Слово умещается
				jw=jw+j;
				i0=i;
			}
			else 
			{
				//Слово не умещается
				vecLines.addElement(parText.substring(in,i0));                
				in=i0+1;
				jw=j;
				if (j>w)
				{
					//Слово полностью не умещается в строке
					i=i0;
					while (jw>w)
					{
						j=0;  
						while (j<w)
						{
							i=i+1;
							j=g.getFont().stringWidth(parText.substring(in,i));

						}
						i=i-1;
						j=g.getFont().stringWidth(parText.substring(in,i));
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

	public void DrawMultStr(Graphics g)
	{       
		int y1;       
		g.setClip(x, y, w, h);
		y1=Top;
		g.setFont(Font.getFont(fty, fst, fsz));
		for (int i=0;i<vecLines.size();i++)
		{                
			if ((y1+hStr)>0)
			{
				g.drawString(vecLines.elementAt(i).toString(), x+1, y+y1, Graphics.LEFT|Graphics.TOP);           
			}
			y1=y1+hStr; 
			if (y1>h)
				break;
		}
		g.setClip(gx, gy, gw, gh);
	}
}
