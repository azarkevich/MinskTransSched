package mts;
import javax.microedition.lcdui.*;

import ObjModel.Bus;
import ObjModel.BusStop;
import ObjModel.FilterDef;

import options.Options;

import text.MultiLineText;



public class Help extends Canvas implements CommandListener
{
	public final static String mainHelpText = 
		"Расписание транспорта г.Минска\n" +
		"Расписание отображается в некотором временном интервале - 'окне'. " +
		"Начало интервала равно текущее время + сдвиг окна (по умолчанию -5 минут). " +
		"Размер интервала по умолчанию равен 30 минут. Если" +
		"в окно не попадает ни одно время остановки, отображается ближайшее следующее\n" +
		"\n" +
		"По умолчанию используются следующее управление:\n" +
		" 'Up' и 'Down' Скролинг текста в верх/вниз\n" +
		" 'Left' и 'Right' сдвиг расписания на 1 минуту. Долгое удержание - сдвиг на 10 минут\n" +
		" '1' и '2' Предыдущая/следующая остановка\n" +
		" '3' Переключить выходной/рабочий день. Обычно программа сама определяет " +
		"выходной/рабочие дни, но иногода приходится задавать в ручную, например на праздники.\n" +
		" '4' и '5' Уменьшить/увеличить размер окна. Увеличенное окно вмещает больше времён\n" +
		" '6' Сбросить настройки. Сбрасывает размер окна и сдвиг к значению заданному " +
		"пользователем в настройках. Также переходит в обычный режим.\n" +
		" '7' и '8' Уменьшить/увеличить сдвиг окна. По умолчанию сдвиг -5 минут, что бы можно " +
		"было видеть прошедшее время. На случай опоздания транспорта.\n" +
		" '9' Переключить режим детального описания. Дополнительно показываются сведения о транспорте" +
		", остановках, а так же как было получено расписание.\n" +
		" '0' Занести/вынести текущую остановку в избранное\n" +
		" '*' Отобразить расписание польностью, без окна.\n" +
		" 'SELECT' - отобразить отфильтрованный список остановок\n" +
		" 'SELECT(долгое нажате)' - меню фильтра\n"
		;
	
	public final static String defineKeyText = 
		"Переопределение управления.\n" +
		"Для переопределения управления для выделленой команды, выберите пункт меню 'Изменить'. " +
		"В появившемся окне нажмите желаемую клавишу. Появится экран типа реакции. Доступны следующие виды" +
		" реакции:\n" +
		" 1. Нажатие с повторами. Обозначена [P]. Команда срабатывает на нажатие клавиши, а так же продолжает срабатывать при удержании.\n" +
		" 2. Нажатие. Команда срабатывает только на нажатие. Обозначена [P1].\n" +
		" 3. Повторы. Обозначена [P*]. Команда НЕ срабатывает на первое нажатие, но начинает срабатывать много раз, если удерживать клавишу дальше.\n" +
		" 4. Отжатие. Обозначена [R]. Срабатывание на любое отпускание кнопки.\n" + 
		" 5. Отжатие короткое. Обозначена [R-]. Срабатывание на быстрое отпускание кнопки.\n" + 
		" 6. Отжатие длинное. Обозначена [R+]. Срабатывание после длительного удержание с последующим её отпусканием.\n" + 
		" Из всех вариантов можно использовать:\n" + 
		" 1 - для команд типа скролирования, что бы команда повторялась при удержании.\n" + 
		" 2 или 5 - для команд типа переключения, что бы команда НЕ повторялась при случайном длительном удержании.\n" + 
		" 6 - для совместного использования с типом 5. Например на одну кнопку можно повесить 2 действия, одно на быстрое отжатие, второе на долгое.\n" + 
		" Варианты 3 и 4 по сути бесполезны, но я их оставил :) На любителя извращений.\n" +  
		" Для просмотра дополнительной информации о команде используйте пункт меню 'Описание'\n" + 
		" Для сброса управления текущей команды/всех коамнд предназначены пункты 'Восстановить' и 'Восстановить всё'\n" + 
		" Для удаления связанной с командой клавиши выберите 'Удалить'\n" +
		"\n" +
		" Некоторые кнопки определены через Game коды(помечены '. Например FIRE'[P]), которые одинаковы для всех телефонов. Однако недостаток в том, что на один game код может приходится несколько " +
		"клавиш. Например FIRE это может быть и '5' и клавиша выбора и send(зелёная кнопка), поэтому игровые коды анализируются в последнюю очередь. Более того, " +
		"при переопределении клавиш может возникнуть ситуация, когда разные команды связаны с одной и той-же клавишей(и одна из них определена как game код) и это не будет " +
		"обнаружено системой, как ошибка. Будет срабатывать команда связанная с НЕ game кодом.\n" +
		"Пример: изначально UP/DOWN определяют скролирование текста в верх/вниз и записаны в game кодах (game код для UP одинаков для всех телефонов, в то время как родной код может отличатся для разных моделей) " +
		"Если переопределить какую-нибудь команду(например 'След. остановка') на UP(нажав её на телефоне), то будет записан родной код. При этом система не сможет " +
		"обнаружить, что на одну клавишу привязано 2 команды. Поэтому рекомендуется при переопределинии управления заранее заменить все game коды на родные - просто " +
		"переопределив их теми-же клавишами. (При переназначении всегда используются родные коды)\n" +
		" Удачи вам понять предыдущий абзац и не сломать мозг!" 
		;
	
	public final static String transportHelp = 
		" Выбор транспорта для фильтрации\n" +
		" Доступны следующие пункты меню:\n" +
		" 'Выбранное в избранное' - неспотря на название, инвертирует 'избранность' отмеченных элементов транспорта.\n" +
		" 'Все' - показать весь транспорт\n" +
		" 'Только текущий' - показать текущий фильтр\n" +
		" 'Только избранный' - показать весь транспорт\n" +
		" 'Выбрать текущий' - выбрать транспорт, который отображается в окне расписания\n" +
		" 'Сбросить выбранный' - сбросить все отметки\n" +
		" 'Помощь' - тут ясно.\n" +
		"\n" +
		" Винмание! Элемент списка 'Все' и 'Избранные' отностится к полному списку, а не к отображаемому в данный момент\n"
		;

	public final static String stopsHelp = 
		" Выбор остановок для фильтрации.\n" +
		" (Текущий фильтр - фильтр, который действует в данный момент.)\n" +
		" Доступны следующие пункты меню:\n" +
		" 'Выбранное в избранное' - неспотря на название, инвертирует 'избранность' отмеченных остановок.\n" +
		" 'Все' - показать все остановки\n" +
		" 'Только текущие' - показать остановки, которые отображаются в окне расписания(отфильтрованные)\n" +
		" 'Только избранные' - показать все избранные остановки\n" +
		" 'Выбрать текущие' - отметить остановки, входящие в текущий фильтр\n" +
		" 'Выбрать все' - выбрать все видимые\n" +
		" 'Сбросить все' - сбросить все отметки\n" +
		" 'Помощь' - тут ясно.\n" +
		"\n" +
		" Винмание! Элемент списка 'Все' и 'Избранные' отностится к полному списку, а не к отображаемому в данный момент\n"
		;

	public final static String favManagerHelp = 
		" Выбор остановок/транспорта как избранное.\n" +
		"\n" +
		" Отметьте элементы, которые вы хотите изменить и выберите пункт меню 'Изменить'. Избранные станут обычными, обычные избранными."
		;
	
	public void commandAction(Command command, Displayable displayable)
	{
		TransSched.display.setCurrent(next);
	}

	MultiLineText multiLineText;

	public String text; 
	Displayable next;
	
	// display filter info
	public Help(FilterDef fd, Displayable next)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Транспорт:");
		if(fd.transport == null)
		{
			sb.append(" весь");
		}
		else
		{
			for(int i=0;i<fd.transport.length;i++)
			{
				Bus b = fd.transport[i];
				sb.append(" ");
				sb.append(b.name);
			}
		}
		sb.append("\n\nОстановки:");
		if(fd.stops == null)
		{
			sb.append(" все");
		}
		else
		{
			for(int i=0;i<fd.stops.length;i++)
			{
				BusStop bs = fd.stops[i];
				sb.append("\n ");
				sb.append(bs.name);
			}
		}

		init(sb.toString(), next, false);
	}

	public Help(BusStop bs, Displayable next)
	{
		String info = bs.name + (bs.favorite ? " (*)" : "") + "\n" + bs.description;
		init(info, next, false);
	}

	public Help(Bus b, Displayable next)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(b.name);
		if(b.favorite)
		{
			sb.append(" (*)");
		}
		if(b.startRoute != null && b.endRoute != null)
		{
			sb.append("\nОт: ");
			sb.append(b.startRoute.name);
			sb.append(" [ ");
			sb.append(b.startRoute.description);
			sb.append(" ]\nДо: ");
			sb.append(b.endRoute.name);
			sb.append(" [ ");
			sb.append(b.endRoute.description);
			sb.append(" ]");
		}
		init(sb.toString(), next, false);
	}

	public Help(String text, Displayable next)
	{
		init(text, next, Options.fullScreen);
	}
	
	void init(String text, Displayable next, boolean fullScreen)
	{
		this.text = text;
		this.next = next;
		addCommand(TransSched.cmdBack);
		setCommandListener(this);
		setFullScreenMode(fullScreen);
	}
	
	public void paint(Graphics g)
	{
		if(multiLineText == null)
		{
			multiLineText = new MultiLineText();
			multiLineText.SetTextPar(0, 0, getWidth(), getHeight(),
					Font.getFont(Options.fontFace, Options.fontStyle, Options.fontSize),
					text);
		}

		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(0, 255, 0);
		multiLineText.Draw(g);
	}

	protected void keyPressed(int keyCode)
	{
		OnKeyEvent(keyCode, false);
	}
	
	protected void keyRepeated(int keyCode)
	{
		OnKeyEvent(keyCode, true);
	}
	
	private void OnKeyEvent(int keyCode, boolean repeated)
	{
		if(multiLineText != null)
		{
			if (keyCode == getKeyCode(Canvas.UP))
			{
				multiLineText.MoveUp(Options.scrollSize);
			}
			else if (keyCode == getKeyCode(Canvas.DOWN))
			{
				multiLineText.MoveDown(Options.scrollSize);
			}
			else
			{
				multiLineText.MoveDown(1);
			}
			repaint();
		}
	}
}
