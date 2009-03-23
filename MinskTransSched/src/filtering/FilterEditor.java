package filtering;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import mts.FilterMenu;
import mts.TransSched;

import ObjModel.FilterDef;
import options.OptionsStoreManager;



public class FilterEditor extends Form implements CommandListener
{
	TextField tfName;
	FilterMenu prev;
	FilterDef fd;
	
	public FilterEditor(FilterDef fd, FilterMenu prev)
	{
		super("Редактирование фильтра");
		this.prev = prev;
		this.fd = fd;
		
		addCommand(TransSched.cmdBack);
		addCommand(TransSched.cmdOK);
		
		setCommandListener(this);

		tfName = new TextField("Имя", fd.name, 20, TextField.ANY);
		append(tfName);
	}

	public void commandAction(Command c, Displayable d)
	{
		if(c == TransSched.cmdBack)
		{
			TransSched.display.setCurrent(prev);
		}
		else if(c == TransSched.cmdOK)
		{
			fd.name = tfName.getString();
			OptionsStoreManager.saveCustomFilterDefinitions(fd);
			prev.updateFilter(fd);
			TransSched.display.setCurrent(prev);
		}
	}
}
