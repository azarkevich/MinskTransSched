package com.mts;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

public class About extends Form
{
	public About()
	{
		super("О программе");
		
		StringItem si;
		si = new StringItem("Автор", "Сергей Азаркевич");
		si.setLayout(Item.LAYOUT_NEWLINE_AFTER);
		append(si);
		try{
			si = new StringItem("Версия", MinskTransSchedMidlet.midlet.getAppProperty("Version"));
			si.setLayout(Item.LAYOUT_NEWLINE_AFTER);
			append(si);
		}
		catch(Exception e)
		{
		}
	}
}
