package com.mts;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

public class About extends Form implements CommandListener
{
	public void commandAction(Command command, Displayable displayable)
	{
		MinskTransSchedMidlet.display.setCurrent(next);
	}

	Displayable next;
	public About(Displayable next)
	{
		super("О программе");
	
		this.next = next;
	
		addCommand(MinskTransSchedMidlet.cmdOK);
		setCommandListener(this);
		
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
