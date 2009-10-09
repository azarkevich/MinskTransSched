import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Vector;


public class TimesDiff
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		int status = new TimesDiff().Convert(args);
		System.exit(status);
	}
	
	void ShowHelp()
	{
		System.out.println("Usage:");
		System.out.println("	TimesDiff <times file 1> <times file 2> ...");
		System.out.println("	Each file contains times. Times blocks separated by '@' sign");
		System.out.println("	First block - base, follow - derived.");
	}

	int Convert(String[] args)
	{
		try{
			if(args.length == 0)
			{
				ShowHelp();
				return -1;
			}

			Vector< Vector<Integer> > allTimes = new Vector< Vector<Integer> >();  
			for (int i = 0; i < args.length; i++)
			{
				LoadTimes(args[i], allTimes);
			}
			
			if(allTimes.size() < 2)
			{
				ShowHelp();
				return -1;
			}
			Vector<Integer> base = allTimes.get(0);
			for (int i = 1; i < allTimes.size(); i++)
			{
				Vector<Integer> deriv = allTimes.get(i);
				
				CalcShift(base, deriv);
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception: " + ex.getMessage());
			return -2;
		}
		return 0;
	}
	
	void CalcShift(Vector<Integer> base, Vector<Integer> deriv)
	{
		if(base.size() != deriv.size())
		{
			System.out.println("!!!BASE=" + base.size() + "; DERIV=" + deriv.size());
		}
		Vector<Integer> Ms = new Vector<Integer>(); 
		Vector<Integer> Disps = new Vector<Integer>();
		
		Integer[] shifts = {0};
		for(int s=0; s<shifts.length; s++)
		{
			int shift = shifts[s];
			
			int bIndex = 0;
			int dIndex = 0;
			Double diffsSum = 0.0;
			int diffsCount = 0;
			Double diffsSquareSum = 0.0;
			while(true)
			{
				int bIndexEff = bIndex + shift;
				int dIndexEff = dIndex;
				
				bIndex++;
				dIndex++;

				if(bIndexEff >= base.size() || dIndexEff >= deriv.size())
					break;

				if(bIndexEff < 0 || dIndexEff < 0)
					continue;

				int btime = base.get(bIndexEff);
				int dtime = deriv.get(dIndexEff);
				int diff = dtime - btime;
				diffsSum += diff;
				diffsSquareSum += diff*diff; 
				diffsCount++;

				System.out.println(FormatTime(btime) + " - " + FormatTime(dtime) + " = " + diff);
			}
			
			Double M = round2(diffsSum / diffsCount);
			
			// negative shift ? no...
			if(M < 0)
				continue;
			
			Double MSquare = round2(diffsSquareSum / diffsCount);
			Double Disp = round2(MSquare - M*M);
			
			System.out.println(shift + "	SHIFT=" + Math.round(M) + "	M=" + M + "	D=" + Disp);
			System.out.println();
		}
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
	}
	
	private String FormatNum(int d)
	{
		return (d < 10 ? "0" : "") + d;
	}

	private String FormatTime(int xTime)
	{
		int hi = (xTime / 60) % 24;
		int lo = xTime % 60;
		return FormatNum(hi) + ":" + FormatNum(lo);
	}
	
	Double round2(Double d)
	{
		return Math.round(d * 100) / 100.0;
	}
	
	void LoadTimes(String file, Vector< Vector<Integer> > allTimes) throws Exception
	{
		Charset c = Charset.forName("UTF-8");
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(file), c));
		String line;
		int previousTime = -1;
		int timeShift = 0;
		Vector<Integer> curTimes = new Vector<Integer>();
		allTimes.add(curTimes);
		while((line = lnr.readLine()) != null)
		{
			try{
				line = line.replaceAll("\\s+", " ").trim();
				if(line.length() == 0)
					continue;

				if(line.startsWith("#"))
					continue;
				
				if(line.equals("@"))
				{
					curTimes = new Vector<Integer>();
					allTimes.add(curTimes);
					timeShift = 0;
					previousTime = 0;
					continue;
				}

				String[] strTimes = line
					.replaceAll("\\s+|,|;|/", " ")
					.replaceAll("\\.", ":")
					.split(" ");

				int hour = 0;
				
				if(strTimes[0].contains(":") == false)
				{
					// if first number in line not has '.' or ':', this is a hour only, not a bus time
					hour = Integer.parseInt(strTimes[0]);
					strTimes = Arrays.copyOfRange(strTimes, 1, strTimes.length);
				}
	
				for (int i = 0; i < strTimes.length; i++)
				{
					String time = strTimes[i];
					
					int minute = 0;
					if(time.contains(":"))
					{
						String[] timePair = time.split(":");
						if(timePair.length != 2)
							throw new Exception("Invalid time: " + time);
						hour = Integer.parseInt(timePair[0]);
						minute = Integer.parseInt(timePair[1]);
					}
					else
					{
						minute = Integer.parseInt(time);
					}
					
					if(hour > 23 || hour < 0 || minute > 59 || minute < 0)
						throw new Exception("Invalid time: " + time);
					
					// fix day border
					int curTime = hour*60 + minute + timeShift;
					if(curTime < previousTime)
					{
						timeShift += 24*60*60;
						curTime = hour*60 + minute + timeShift; 
					}
					previousTime = curTime;
					
					// add time
					curTimes.add(curTime);
				}
			}
			catch(Exception ex)
			{
				int lineNo = lnr.getLineNumber();
				throw new Exception("Error in " + file + ":" + lineNo + "\n" + ex.getMessage(), ex);
			}
		}
	}
}
