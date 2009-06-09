package filtering;

public class Matcher
{
	static boolean Match(String targ, int targIndex, String flt, int fltIndex)
	{
		// filter finished. ok
		if(flt.length() <= fltIndex && targ.length() <= targIndex)
			return true;
		
		if(flt.length() <= fltIndex)
			return false;

		// target finished, but filter not
		if(targ.length() <= targIndex)
		{
			for (int i = fltIndex; i < flt.length(); i++)
			{
				if(flt.charAt(i) != '*')
					return false;
			}

			// ok, if filter trail contains only '*'
			return true;
		}

		char fltChar = flt.charAt(fltIndex);
		// consume any chars ?
		if(fltChar == '*')
		{
			for (int i = targIndex; i <= targ.length(); i++)
			{
				if(Match(targ, i, flt, fltIndex + 1))
					return true;
			}
		}
		
		// consume 1 char
		if(fltChar == '#')
			return Match(targ, targIndex + 1, flt, fltIndex + 1);
		
		char targChar = targ.charAt(targIndex);
		if(targChar == fltChar)
			return Match(targ, targIndex + 1, flt, fltIndex + 1);

		return false;
	}
	
	public static boolean Match(String targ, String flt)
	{
		return Match(targ, 0, flt, 0);
	}
}
