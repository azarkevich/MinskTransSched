import java.util.Calendar;


public class Schedule
{
	public Bus bus;
	public BusStop busStop;

	// indexes according to Calendar.SUNDAY ... Calendar.SATURDAY, 8 for work days, 9 for holidays 
	short[][] m_times = new short[9][];
	
	public short[] GetTimes(int day)
	{
		return m_times[day - 1];
	}
	
	public void setTimes(int day, short[] times)
	{
		m_times[day - 1] = times;
	}
	
	static final int[] workdayIndexes = new int[] { 
		Schedule.WORKDAY - 1,
		Calendar.MONDAY - 1,
		Calendar.TUESDAY - 1,
		Calendar.WEDNESDAY - 1,
		Calendar.THURSDAY - 1,
		Calendar.FRIDAY - 1
		};

	static final int[] holidayIndexes = new int[] { 
			Schedule.HOLIDAY - 1,
			Calendar.SUNDAY - 1,
			Calendar.SATURDAY - 1
		};

	public void NormalizeDays() throws Exception
	{
		// guess workday times
		short[] workTimes = new short[] {};
		for (int i = 0; i < workdayIndexes.length; i++)
		{
			if(m_times[workdayIndexes[i]] != null)
			{
				workTimes = m_times[workdayIndexes[i]];
				break;
			}
		}
		// guess holiday times
		short[] holidayTimes = new short[0];
		for (int i = 0; i < holidayIndexes.length; i++)
		{
			if(m_times[holidayIndexes[i]] != null)
			{
				holidayTimes = m_times[holidayIndexes[i]];
				break;
			}
		}
		
		// fill empty workdays
		for (int i = 0; i < workdayIndexes.length; i++)
		{
			if(m_times[workdayIndexes[i]] == null)
				m_times[workdayIndexes[i]] = workTimes;
		}

		// fill empty holidays
		for (int i = 0; i < holidayIndexes.length; i++)
		{
			if(m_times[holidayIndexes[i]] == null)
				m_times[holidayIndexes[i]] = holidayTimes;
		}
	}
	
	public static final int WORKDAY = 8;
	public static final int HOLIDAY = 9;
}
