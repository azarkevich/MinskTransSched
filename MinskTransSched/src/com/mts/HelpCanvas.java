package com.mts;
import javax.microedition.lcdui.*;

import com.options.Options;


public class HelpCanvas extends Canvas
{
	MultiLineText m_MultiLineText;

	int foreColorR = 0;
	int foreColorG = 255;
	int foreColorB = 0;
	
	public void setForeColor(int r, int g, int b)
	{
		foreColorR = r;
		foreColorG = g;
		foreColorB = b;
	}
	
	public final static String mainHelpText = 
		"Расписание автобусов г.Минска\n" +
		"Состоит из 4 основных экранов:\n" +
		" 1. Список избранных остановок\n" +
		" 2. Список всех остановок\n" +
		" 3. Расписание по избранным остановкам\n" +
		" 4. Расписание по всем остановкам\n" +
		"Выбор элемента из 1 и 2 списков переходит к 3 и 4 расписанию соответственно. " +
		"Расписание отображается в некотором временном интервале - 'окне'. " +
		"Начало интервала равно текущее время + сдвиг окна (по умолчанию -5 минут). " +
		"Размер интервала по умолчанию равен 30 минут. Если" +
		"в окно не попадает ни одно время остановки, отображается ближайшее следующее\n\n" +
		"В режиме расписания по умолчанию используются следующие клафиши:\n" +
		" 'Up' и 'Down' Скролинг текста в верх/вниз (на некоторых телефонах не работает, " +
		"попробуйте переопределить в меню настроек)\n" +
		" 'Left' и 'Right' сдвиг расписания на 1 минуту. Долгое удержание - сдвиг на 10 минут\n" +
		" '1' и '2' Предыдущая/следующая остановка\n" +
		" '3' Переключить выходной/рабочий день. Обычно программа сама определяет " +
		"выходной/рабочие дни, но иногода приходится задавать в ручную, например на праздники.\n" +
		" '4' и '5' Уменьшить/увеличить размер окна. Увеличенное окно вмещает больше времён\n" +
		" '6' Сбросить настройки: . Сбрасывает размер окна и сдвиг к значению заданному " +
		"пользователем в настройках. Также переходит в обычный режим.\n" +
		" '7' и '8' Уменьшить/увеличить сдвиг окна: . По умолчанию сдвиг -5 минут, что бы можно " +
		"было видеть прошедшее время. На случай опоздания транспорта.\n" +
		" '9' Переключить режим детального описания: . Дополнительно показываются сведения о автобусах " +
		", остановках, а так же как было получено расписание.\n" +
		" '0' Занести/вынести текущую остановку в избранное\n" +
		" '*' Отобразить расписание польностью, без окна.\n" +
		" 'SELECT' - перейти к списку избранного\n" +
		" 'SELECT(долгое нажате)' - перейти к списку всех остановок\n"
		;

	public String text; 
	public HelpCanvas(String text)
	{
		this.text = text; 
		setFullScreenMode(Options.fullScreen);
	}
	
	public void paint(Graphics g)
	{
		if(m_MultiLineText == null)
		{
			m_MultiLineText = new MultiLineText();
			m_MultiLineText.SetTextPar(0, 0, getWidth(), getHeight(),
					Font.getFont(Options.fontFace, Options.fontStyle, Options.fontSize),
					text);
		}

		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(foreColorR, foreColorG, foreColorB);
		m_MultiLineText.DrawMultStr(g);
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
		if(m_MultiLineText != null)
		{
			if (keyCode == getKeyCode(Canvas.UP))
			{
				m_MultiLineText.MoveUp(Options.scrollSize);
			}
			else if (keyCode == getKeyCode(Canvas.DOWN))
			{
				m_MultiLineText.MoveDown(Options.scrollSize);
			}
			else
			{
				m_MultiLineText.MoveDown(1);
			}
			repaint();
		}
	}
}
