/**
 * Persian Calendar see: http://code.google.com/p/persian-calendar/
   Copyright (C) 2012  Mortezaadi@gmail.com edited by irshst
   PersianCalendarConstants.java
   
   Persian Calendar is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mohammadreza.salari.shlibrary.Utils;


public class PersianCalendarConstants {

	// 00:00:00 UTC (Gregorian) Julian day 0,
	// 0 milliseconds since 1970-01-01
	public static final long MILLIS_JULIAN_EPOCH = -210866803200000L;
	// Milliseconds of a day calculated by 24L(hours) * 60L(minutes) *
	// 60L(seconds) * 1000L(mili);
	public static final long MILLIS_OF_A_DAY = 86400000L;

	/**
	 * The JDN of 1 Farvardin 1; Equivalent to March 19, 622 A.D.
	 */
	public static final long PERSIAN_EPOCH = 1948321;

	public static final String[] persianMonthNames = { "\u0641\u0631\u0648\u0631\u062f\u06cc\u0646", // Farvardin
		"\u0627\u0631\u062F\u06CC\u0628\u0647\u0634\u062A", // Ordibehesht
			"\u062e\u0631\u062f\u0627\u062f", // Khordad
			"\u062a\u06cc\u0631", // Tir
			"\u0645\u0631\u062f\u0627\u062f", // Mordad
			"\u0634\u0647\u0631\u06cc\u0648\u0631", // Shahrivar
			"\u0645\u0647\u0631", // Mehr
			"\u0622\u0628\u0627\u0646", // Aban
			"\u0622\u0630\u0631", // Azar
			"\u062f\u06cc", // Dey
			"\u0628\u0647\u0645\u0646", // Bahman
			"\u0627\u0633\u0641\u0646\u062f" // Esfand
	};

	public static final String[] persianWeekDays = { "شنبه", // Shanbeh
			"یکشنبه", // Yekshanbeh
			"دو شنبه", // Doshanbeh
			"سه شنبه", // Sehshanbeh
			"چهار شنبه", // Chaharshanbeh
			"پنجشنبه", // Panjshanbeh
			"جمعه" // jome
	};
	/*

	public static final String[] persianWeekDays = { "\u0634\u0646\u0628\u0647", // Shanbeh
			"\u06cc\u06a9\u200c\u0634\u0646\u0628\u0647", // Yekshanbeh
			"\u062f\u0648\u0634\u0646\u0628\u0647", // Doshanbeh
			"\u0633\u0647\u200c\u0634\u0646\u0628\u0647", // Sehshanbeh
			"\u0686\u0647\u0627\u0631\u0634\u0646\u0628\u0647", // Chaharshanbeh
			"\u067e\u0646\u062c\u200c\u0634\u0646\u0628\u0647", // Panjshanbeh
			"\u062c\u0645\u0639\u0647" // jome
	};
	 */
	public static final String[] wdNames = { "\u0627\u0644\u0623\u062D\u062F", "\u0627\u0644\u0625\u062B\u0646\u064A\u0646", "\u0627\u0644\u062B\u0644\u0627\u062B\u0627\u0621", "\u0627\u0644\u0623\u0631\u0628\u0639\u0627\u0621", "\u0627\u0644\u062E\u0645\u064A\u0633",
		"\u0627\u0644\u062C\u0645\u0639\u0629", "\u0627\u0644\u0633\u0628\u062A" };
	public static final String[] iMonthNames = { "\u0645\u062D\u0631\u0645", "\u0635\u0641\u0631", "\u0631\u0628\u064A\u0639 \u0627\u0644\u0627\u0648\u0644",
		"\u0631\u0628\u064A\u0639 \u0627\u0644\u062B\u0627\u0646\u064A", "\u062C\u0645\u0627\u062F\u064A \u0627\u0644\u0627\u0648\u0644\u064A", "\u062C\u0645\u0627\u062F\u064A \u0627\u0644\u062B\u0627\u0646\u064A\u0647", "\u0631\u062C\u0628",
		"\u0634\u0639\u0628\u0627\u0646", "\u0631\u0645\u0636\u0627\u0646", "\u0634\u0648\u0627\u0644", "\u0630\u0648\u0627\u0644\u0642\u0639\u062F\u0647", "\u0630\u0648\u0627\u0644\u062D\u062C\u0647" };
    public static final String[] arabicChars ={"\u0660","\u0661","\u0662","\u0663","\u0664","\u0665","\u0666","\u0667","\u0668","\u0669"};
	
	public static final String getShamsiWeekDay(int week)
	{
		if (week>0 && week<6) return toArabicNumbers(week)+persianWeekDays[0];
		return persianWeekDays[week];
   	}
	public static final String toArabicNumbers(int number)
	{
	StringBuilder builder = new StringBuilder();
	String str=Integer.toString(number);
	for(int i =0;i<str.length();i++)
	{
		if(Character.isDigit(str.charAt(i)))
		{
			builder.append(arabicChars[(int)(str.charAt(i))-48]);
		}
		else
		{
			builder.append(str.charAt(i));
		}
	}
	
	return builder.toString();
}
}
