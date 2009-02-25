package com.text;

import java.util.Vector;

import javax.microedition.lcdui.Font;

public class TextParser
{
	public int regionWidth;

	int getNextBreakPoint(String text, int start)
	{
		for (int i = start; i < text.length(); i++)
		{
			switch (text.charAt(i))
			{
			// break before
			case '\n':
			case ' ':
				return i;
			}
		}
		return text.length();
	}
	
	int getLineEnd(Font f, String text, int start)
	{
		// empty line?
		if(text.charAt(start) == '\n')
			return start;
		
		int end = start;
		while(true)
		{
			int newEnd = getNextBreakPoint(text, end + 1);

			// next point too far?
			int lineWidth = f.substringWidth(text, start, newEnd - start);
			if(lineWidth > regionWidth)
				break;

			end = newEnd;

			if(end >= text.length() || text.charAt(end) == '\n')
				return end;
		}
		
		if(end == start)
		{
			// word not fit into line. break by chars.
			// return at least 1 char
			end++;
			while(true)
			{
				int newEnd = end + 1;
				if(f.substringWidth(text, start, newEnd - start) > regionWidth)
					break;
				end = newEnd;
			}
		}
		
		return end;
	}
	
	private Vector splitString(Font f, String text)
	{
		Vector lines = new Vector();

		int lineStartIndex = 0;
		
		while(true)
		{
			if(lineStartIndex >= text.length())
				break;

			int lineEndIndex = getLineEnd(f, text, lineStartIndex);
			lines.addElement(text.substring(lineStartIndex, lineEndIndex));
			lineStartIndex = lineEndIndex; 

			if(lineStartIndex >= text.length())
				break;
			
			if(text.charAt(lineStartIndex) == '\n' || text.charAt(lineStartIndex) == ' ')
			{
				// not print ' ' if it was line break point and '\n'
				// shift color indexes:
				for (int i = 0; i < colorIndexes.size(); i++)
				{
					TextChunk tc = (TextChunk)colorIndexes.elementAt(i);
					if(tc.start > lineStartIndex)
						tc.start--;
				}
				lineStartIndex++;
			}
		}
		return lines;
	}
	
	public Vector[] colorInfo = null;
	
	void createColorInfo()
	{
		colorInfo = new Vector[lines.size()];
		int begin = 0;
		int end = 0;
		for (int i = 0; i < lines.size(); i++)
		{
			String line = (String)lines.elementAt(i);
			begin = end;
			end = begin + line.length(); 

			colorInfo[i] = new Vector();
		
			// find overlapping color infos
			for (int j = 0; j < colorIndexes.size(); j++)
			{
				TextChunk tc = (TextChunk)colorIndexes.elementAt(j);
				if(tc.start >= begin && tc.start <= end ||
						tc.end >= begin && tc.end <= end)
				{
					TextChunk tcNew = new TextChunk();
					tcNew.color = tc.color;
					tcNew.start = tc.start - begin;
					tcNew.end = tc.end - begin;
					colorInfo[i].addElement(tcNew);
				}
			}
		}
	}
	
	public Vector lines = null;
	public void parse(Font f, String text)
	{
		text = stripColorInfo(text);
		lines = splitString(f, text);
		// calculate chunk ends:
		for (int j = 0; j < colorIndexes.size(); j++)
		{
			TextChunk tc = (TextChunk)colorIndexes.elementAt(j);
			if(j + 1 != colorIndexes.size())
				tc.end = ((TextChunk)colorIndexes.elementAt(j + 1)).start;
			else
				tc.end = text.length();
		}
		createColorInfo();
	}

	public Vector colorIndexes = null;
	private String stripColorInfo(String text)
	{
		colorIndexes = new Vector();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < text.length(); i++)
		{
			char ch = text.charAt(i); 
			if(ch == '\\' && (i + 1) < text.length())
			{
				char nextCh = text.charAt(i + 1);
				if(nextCh == '\\')
				{
					i++;
					continue;
				}
				if((nextCh == 'c' || nextCh == 'C') && (i + 8) < text.length())
				{
					TextChunk chunk = new TextChunk();

					String r = text.substring(i + 2, i + 4);
					String g = text.substring(i + 4, i + 6);
					String b = text.substring(i + 6, i + 8);
					chunk.color = (Integer.parseInt(r, 16) << 16) | (Integer.parseInt(g, 16) << 8) | Integer.parseInt(b, 16);
					chunk.start = sb.length();
					
					colorIndexes.addElement(chunk);
					
					i+= 7;
					continue;
				}
			}
			sb.append(ch);
		}
		return sb.toString();
	}
}
