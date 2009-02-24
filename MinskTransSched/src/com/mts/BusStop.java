package com.mts;

public class BusStop
{
	public short id;
	public String name;
	public String description;
	public Schedule[] schedules;
	public boolean favorite = false;
	public int bookmarkRecord = -1;
}
