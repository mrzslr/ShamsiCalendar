
package com.mohammadreza.salari.shcalandar.Utils;

import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.*;
import android.util.*;
import java.text.*;
import android.content.*;


/**
 *
 * <strong> Example </strong>
 * </p>
 * <p>
 * </p>
 * 
 * <pre>
 * {@code
 *       PersianCalendar persianCal = new PersianCalendar();
 *       System.out.println(persianCal.getPersianShortDate());
 *       
 *       persianCal.set(1982, Calendar.MAY, 22);
 *       System.out.println(persianCal.getPersianShortDate());
 *       
 *       persianCal.setDelimiter(" , ");
 *       persianCal.parse("1361 , 03 , 01");
 *       System.out.println(persianCal.getPersianShortDate());
 *       
 *       persianCal.setPersianDate(1361, 3, 1);
 *       System.out.println(persianCal.getPersianLongDate());
 *       System.out.println(persianCal.getTime());
 *       
 *       persianCal.addPersianDate(Calendar.MONTH, 33);
 *       persianCal.addPersianDate(Calendar.YEAR, 5);
 *       persianCal.addPersianDate(Calendar.DATE, 50);
 * 
 * }
 * 
 * <pre>
 * @author MohammadReza Salari  contact: <a href="mailto:frishter.ms@gmail.com">frishter.ms@gmail.com</a>
 * @version 1.1
 */
public class PersianCalendar extends GregorianCalendar {

	public int  persianMonthDays,persianWeekCount,persianMonthLastDayWeekDay,persianMonthFirstDayWeekDay;
	private int persianYear;
	private int persianMonth;
	private int persianDay;
	public int hWeekDay,hAdjust=-1;
	public int hYear,hYear1,hYear2;
	public int hMonth,hMonth1,hMonth2;
	public int gMonth1,gMonth2,gYear1,gYear2;
	public int hDay;
	public int[] persianHDays=new int[32];
	public int[] persianGDays=new int[32];
	public int[] persianHMonths=new int[32];
	public int[] persianGMonths=new int[32];
	public int[] persianHYears=new int[32];
	public int[] persianGYears=new int[32];
	public ResourceUtils eventCalendar;
	//public HashMap<Integer,Integer> hijriMonthDayCorrection;
	public int currentYear,currentMonth,currentDay;
	public int selectedYear,selectedMonth,selectedDay;
	private Date currentDate;
	private long timeCorrection=0;
	//DateTime dtISO,dtIslamic;
	// use to seperate PersianDate's field and also Parse the DateString based
	// on this delimiter
	private String delimiter = "/";

	private long convertToMilis(long julianDate) {
		return PersianCalendarConstants.MILLIS_JULIAN_EPOCH + julianDate * PersianCalendarConstants.MILLIS_OF_A_DAY
			+ PersianCalendarUtils.ceil(getMillis() - PersianCalendarConstants.MILLIS_JULIAN_EPOCH, PersianCalendarConstants.MILLIS_OF_A_DAY);
	}

	/**
	 * default constructor
	 * 
	 * most of the time we don't care about TimeZone when we persisting Date or
	 * doing some calculation on date. <strong> Default TimeZone was set to
	 * "GMT" </strong> in order to make developer to work more convenient with
	 * the library; however you can change the TimeZone as you do in
	 * GregorianCalendar by calling setTimeZone()
	 */
	public PersianCalendar(Context _context,long millis) {
		setTimeInMillis(millis);
		initCalendar(_context);
		
	}

	/**
	 * default constructor
	 * 
	 * most of the time we don't care about TimeZone when we persisting Date or
	 * doing some calculation on date. 
	 * in order to make developer to work more convenient with
	 * the library; however you can change the TimeZone as you do in
	 * GregorianCalendar by calling setTimeZone()
	 */
	 
	public PersianCalendar(Context _context) {
		initCalendar(_context);
		
	}
	public PersianCalendar(Context _context,int hijriAdjust) {
		hAdjust=hijriAdjust;
		initCalendar(_context);

	}
	private void initCalendar(Context _context)
	{

		eventCalendar=new ResourceUtils(_context);
		initCalendar();

	}
    private void initCalendar()
	{
	
		// IRST (Iran Standard Time) UTC/GMT +3:30 hours
		// by @irshst
		set(HOUR_OF_DAY, 3);
		set(MINUTE, 30);
		set(SECOND, 0);
		set(MILLISECOND, 0);
		
		setTimeZone(TimeZone.getTimeZone("GMT+3:30"));

		calculateMonthLastDay();
		currentDay=getPersianDay();
		currentMonth=getPersianMonth();
		currentYear=getPersianYear();
		currentDate=new Date();
		
	}
	public void refresh()
	{
		Date date=new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyyMMdd", Locale.getDefault());

        if (! dateFormat.format(date).equals(dateFormat.format(currentDate))) {
			setTime(date);
			initCalendar();}
	}
	public void setSelectedDate(int persianYear, int persianMonth, int persianDay) {
		this.selectedYear = persianYear;
		this.selectedMonth = persianMonth;
		this.selectedDay = persianDay;
	}
	
	public boolean getHVacation(int day)
	{
		int dayMonth=getHDayMonth(day);
		if (eventCalendar.vacationH.containsKey(dayMonth)) 
			return eventCalendar.vacationH.get(dayMonth);
		return false;

	}
	public boolean getPVacation(int day)
	{
		int dayMonth=getPDayMonth(day);
		if (eventCalendar.vacationP.containsKey(dayMonth)) 
			return eventCalendar.vacationP.get(dayMonth);
		return false;

	}
	public boolean getGVacation(int day)
	{
		int dayMonth=getGDayMonth(day);
		if (eventCalendar.vacationG.containsKey(dayMonth)) 
			return eventCalendar.vacationG.get(dayMonth);
		return false;

	}
	public boolean isVacation(int day)
	{

		return (getPVacation(day) || getHVacation(day) || getGVacation(day)) ; 

	}
	public String getHEvent(int day)
	{
		int dayMonth=getHDayMonth(day);
		if (eventCalendar.eventH.containsKey(dayMonth)) 
			return eventCalendar.eventH.get(dayMonth);
		return "";

	}
	public String getPEvent(int day)
	{
		int dayMonth=getPDayMonth(day);
		if (eventCalendar.eventP.containsKey(dayMonth)) 
			return eventCalendar.eventP.get(dayMonth);
		return "";

	}
	public String getGEvent(int day)
	{
		int dayMonth=getGDayMonth(day);
		if (eventCalendar.eventG.containsKey(dayMonth)) 
			return eventCalendar.eventG.get(dayMonth);
		return "";

	}
	public String getTodayEvent()
	{
		String ret="";
		int day=getPersianDay();
		String pEvent=getPEvent(day);
		String hEvent=getHEvent(day);
		String gEvent=getGEvent(day);
		if (!pEvent.equals("")) ret+=" "+pEvent;
		if (!hEvent.equals("")) ret+=" "+hEvent;
		if (!gEvent.equals("")) ret+=" "+gEvent;
		
		return ret.trim();
	}
	public boolean hasEvent(int day)
	{

		return !(getPEvent(day)+getHEvent(day)+getGEvent(day)).equals("") ; 

	}
	public int getHDayMonth(int i)
	{

		return (persianHMonths[i]+1)*100+persianHDays[i];

	}
	public int getPDayMonth(int i)
	{
		return getPersianMonth()*100+i;

	}
	public int getGDayMonth(int i)
	{
		return (persianGMonths[i]+1)*100+persianGDays[i];

	}
	private long getMillis()
	{
		return getTimeInMillis()+timeCorrection;
	}
	/**
	 * Calculate persian date from current Date and populates the corresponding
	 * fields(persianYear, persianMonth, persianDay)
	 */
	protected void calculatePersianDate() {
		
		
		long julianDate = ((long) Math.floor((getMillis() - PersianCalendarConstants.MILLIS_JULIAN_EPOCH)) / PersianCalendarConstants.MILLIS_OF_A_DAY);
		long PersianRowDate = PersianCalendarUtils.julianToPersian(julianDate);
		long year = PersianRowDate >> 16;
		int month = (int) (PersianRowDate & 0xff00) >> 8;
		int day = (int) (PersianRowDate & 0xff);
		this.persianYear = (int) (year > 0 ? year : year - 1);
		this.persianMonth = month;
		this.persianDay = day;
	//	kuwaiticalendar(true);
	}

	/**
	 * 
	 * Determines if the given year is a leap year in persian calendar. Returns
	 * true if the given year is a leap year.
	 * 
	 * @return boolean
	 */
	public boolean isPersianLeapYear() {
		// calculatePersianDate();
		return PersianCalendarUtils.isPersianLeapYear(this.persianYear);
	}

	/**
	 * set the persian date it converts PersianDate to the Julian and assigned
	 * equivalent milliseconds to the instance
	 * 
	 * @param persianYear
	 * @param persianMonth
	 * @param persianDay
	 */
	public void setPersianDate(int persianYear, int persianMonth, int persianDay) {
		this.persianYear = persianYear;
		this.persianMonth = persianMonth;
		this.persianDay = persianDay;
		setTimeInMillis(convertToMilis(PersianCalendarUtils.persianToJulian(this.persianYear > 0 ? this.persianYear : this.persianYear + 1, this.persianMonth - 1, this.persianDay)));
	}

	public int getPersianYear() {
		// calculatePersianDate();
		return this.persianYear;
	}

	/**
	 * 
	 * @return int persian month number
	 */
	public int getPersianMonth() {
		// calculatePersianDate();
		return this.persianMonth + 1;
	}

	/**
	 * 
	 * @return String persian month name
	 */
	public String getPersianMonthName() {
		
		return getPersianMonthName(this.persianMonth);
	}
	public String getHMonthName() {
		
		return getHMonthName(this.hMonth);
	}
	public String getPersianMonthName(int month) {
	
		return PersianCalendarConstants.persianMonthNames[month];
	}
	public String getHMonthName(int month) {
	
		return PersianCalendarConstants.iMonthNames[month];
	}
	/**
	 * 
	 * @return int Persian day in month
	 */
	public int getPersianDay() {
		// calculatePersianDate();
		return this.persianDay;
	}

	/**
	 * 
	 * @return String Name of the day in week
	 */

	public int getWeekDayNumber() {
		switch (get(DAY_OF_WEEK)) {
			case SATURDAY:
				return 0;
			case SUNDAY:
				return 1;
			case MONDAY:
				return 2;
			case TUESDAY:
				return 3;
			case WEDNESDAY:
				return 4;
			case THURSDAY:
				return 5;
			default:
				return 6;
		}

	}


	public String getPersianWeekDayName() {
		return getPersianWeekDayName(getWeekDayNumber());
		

	}
	public String getPersianWeekDayName(int n) {
		return PersianCalendarConstants.persianWeekDays[n];


	}


	public String getHWeekDayName() {
		return getHWeekDayName(hWeekDay);


	}
	public String getHWeekDayName(int n) {
		return PersianCalendarConstants.wdNames[n];


	}
	/**
	 * 
	 * @return String of Persian date
	 */
	public String getPersianLongDate() {
		return getPersianWeekDayName() + "  " + PersianCalendarConstants.toArabicNumbers(this.persianDay) + "  " + getPersianMonthName() + "  " + PersianCalendarConstants.toArabicNumbers(this.persianYear);
	}

	public String getPersianLongDateAndTime() {
		Calendar c = new GregorianCalendar();
		
		return getPersianLongDate() + " " + PersianCalendarConstants.toArabicNumbers(c.get(HOUR_OF_DAY)) + ":" + PersianCalendarConstants.toArabicNumbers(c.get(MINUTE)) + ":" + PersianCalendarConstants.toArabicNumbers(c.get(SECOND));
	}

	/**
	 * 
	 * @return String of persian date formatted by
	 *         'YYYY[delimiter]mm[delimiter]dd' default delimiter is '/'
	 */
	public String getPersianShortDate() {
		// calculatePersianDate();
		return "" + formatToMilitary(this.persianYear) + delimiter + formatToMilitary(getPersianMonth()) + delimiter + formatToMilitary(this.persianDay);
	}

	public String getPersianShortDateTime() {
		return "" + formatToMilitary(this.persianYear) + delimiter + formatToMilitary(getPersianMonth()) + delimiter + formatToMilitary(this.persianDay) + " " + formatToMilitary(this.get(HOUR_OF_DAY)) + ":" + formatToMilitary(get(MINUTE))
				+ ":" + formatToMilitary(get(SECOND));
	}

	private String formatToMilitary(int i) {
		return (i < 9) ? "0" + i : String.valueOf(i);
	}

	/**
	 * add specific amout of fields to the current date for now doesnt handle
	 * before 1 farvardin hejri (before epoch)
	 * 
	 * @param field
	 * @param amount
	 *            <pre>
	 *  Usage:
	 *  {@code
	 *  addPersianDate(Calendar.YEAR, 2);
	 *  addPersianDate(Calendar.MONTH, 3);
	 *  }
	 * </pre>
	 * 
	 *            u can also use Calendar.HOUR_OF_DAY,Calendar.MINUTE,
	 *            Calendar.SECOND, Calendar.MILLISECOND etc
	 */
	//
	
	public void addPersianDate(int field, int amount) {
		if (amount == 0) {
			return; // Do nothing!
		}

		if (field < 0 || field >= ZONE_OFFSET) {
			throw new IllegalArgumentException();
		}

		if (field == YEAR) {
			setPersianDate(this.persianYear + amount, getPersianMonth(), this.persianDay);
			return;
		} else if (field == MONTH) {
			setPersianDate(this.persianYear + ((getPersianMonth() + amount) / 12), (getPersianMonth() + amount) % 12, this.persianDay);
			return;
		}
		add(field, amount);
		calculatePersianDate();
	}
	public String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }
	/**
	 * Calculate the last day of current month of shamsi date
	 * added by irshst@gmail.com 
	 * telegram.me/shes_ir
	 */
	public void calculateMonthLastDay()
{
	
	int day=getPersianDay();
	int month=getPersianMonth();
	int year=getPersianYear();
	
	setPersianDate(getPersianYear(), getPersianMonth(), 1);
	kuwaiticalendar(hAdjust);
	hYear1=hYear;
	gYear1=get(Calendar.YEAR);
	hMonth1=hMonth;
	gMonth1=get(Calendar.MONTH);
	addPersianDate(MONTH,1 );
    addPersianDate(DATE, -1);
	kuwaiticalendar(hAdjust);
	hYear2=hYear;
	gYear2=get(Calendar.YEAR);
	hMonth2=hMonth;
	gMonth2=get(Calendar.MONTH);
	persianMonthDays=getPersianDay();
	persianMonthLastDayWeekDay=getWeekDayNumber();
	int wDay=persianMonthLastDayWeekDay;
	persianWeekCount=1;
	
	for (int i=persianMonthDays;;i--)
	{
		setPersianDate(getPersianYear(), getPersianMonth(), i);
		kuwaiticalendar(hAdjust);
		persianHYears[i]=hYear;
		persianHMonths[i]=hMonth;
		persianHDays[i]=hDay;
		persianGDays[i]=get(Calendar.DAY_OF_MONTH);
		persianGMonths[i]=get(Calendar.MONTH);
		persianGYears[i]=get(Calendar.YEAR);
		if (i==1) {persianMonthFirstDayWeekDay=wDay;break;}
		if (wDay>0) wDay--; 
		else {
			wDay=6;
		    persianWeekCount++;
		}
		
	}
	setPersianDate(year,month,day);
	kuwaiticalendar(hAdjust);
}
  public void next()
  {
	  addPersianDate(MONTH,1 );
	  calculateMonthLastDay();
  }
	public void prev()
	{
		addPersianDate(MONTH,-1 );
		calculateMonthLastDay();
	}
	public void nextYear()
	{
		addPersianDate(YEAR,1 );
		calculateMonthLastDay();
	}
	public void prevYear()
	{
		addPersianDate(YEAR,-1 );
		calculateMonthLastDay();
	}	
	
    

    	static double gmod(double n, double m) {
    		return ((n % m) + m) % m;
    	}

    	public int[] kuwaiticalendar(int adjust) {
    		
			Calendar  today = getInstance();
		/*	dtISO = new DateTime(today);

// find out what the same instant is using the Islamic Chronology
			dtIslamic = dtISO.withChronology(IslamicChronology.getInstance());
			
			
			hWeekDay=dtIslamic.getDayOfWeek();
			hMonth=dtIslamic.getMonthOfYear();
			hYear=dtIslamic.getYear();
			hDay=dtIslamic.getDayOfMonth();
			if (true) return null;
			
			
			Calendar uCal = new UmmalquraCalendar(TimeZone.getTimeZone("Asia/Tehran"),Locale.getDefault());
			uCal.setTime(getTime());

			hYear=uCal.get(Calendar.YEAR);         
			hMonth=uCal.get(Calendar.MONTH);        
			hDay=uCal.get(Calendar.DAY_OF_MONTH); 
           // hWeekDay=uCal.get(Calendar.DAY_OF_WEEK);
			
			
     
			return null;
			
			*/
			// 2->30 7->29
			
			double day;
    		double month;
    		double year;
    		int adj = adjust;
			
    		
    		if (adjust!=0) {
    			int adjustmili = 1000 * 60 * 60 * 24 * adj;
    			long todaymili = getTimeInMillis() + adjustmili;
    			today.setTimeInMillis(todaymili);
				day = today.get(Calendar.DAY_OF_MONTH);
				month = today.get(Calendar.MONTH);
				year = today.get(Calendar.YEAR);
    		}
			else {
    		day = get(Calendar.DAY_OF_MONTH);
    		month = get(Calendar.MONTH);
    		year = get(Calendar.YEAR);
            }
    		double m = month + 1;
    		double y = year;
    		if (m < 3) {
    			y -= 1;
    			m += 12;
    		}

    		double a = Math.floor(y / 100.);
    		double b = 2 - a + Math.floor(a / 4.);

    		if (y < 1583)
    			b = 0;
    		if (y == 1582) {
    			if (m > 10)
    				b = -10;
    			if (m == 10) {
    				b = 0;
    				if (day > 4)
    					b = -10;
    			}
    		}

    		double jd = Math.floor(365.25 * (y + 4716))
				+ Math.floor(30.6001 * (m + 1)) + day + b - 1524;

    		b = 0;
    		if (jd > 2299160) {
    			a = Math.floor((jd - 1867216.25) / 36524.25);
    			b = 1 + a - Math.floor(a / 4.);
    		}
    		double bb = jd + b + 1524;
    		double cc = Math.floor((bb - 122.1) / 365.25);
    		double dd = Math.floor(365.25 * cc);
    		double ee = Math.floor((bb - dd) / 30.6001);
    		day = (bb - dd) - Math.floor(30.6001 * ee);
    		month = ee - 1;
    		if (ee > 13) {
    			cc += 1;
    			month = ee - 13;
    		}
    		year = cc - 4716;

    		double wd = gmod(jd + 1, 7) + 1;

    		double iyear = 10631. / 30.;
    		double epochastro = 1948084;
    		double epochcivil = 1948085;

    		double shift1 = 8.01 / 60.;

    		double z = jd - epochastro;
    		double cyc = Math.floor(z / 10631.);
    		z = z - 10631 * cyc;
    		double j = Math.floor((z - shift1) / iyear);
    		double iy = 30 * cyc + j;
    		z = z - Math.floor(j * iyear + shift1);
    		double im = Math.floor((z + 28.5001) / 29.5);
    		if (im == 13)
    			im = 12;
    		double id = z - Math.floor(29.5001 * im - 29);

    		int[] myRes = new int[8];

    		myRes[0] = (int)day; // calculated day (CE)
    		myRes[1] = (int)month - 1; // calculated month (CE)
    		myRes[2] = (int)year; // calculated year (CE)
    		myRes[3] = (int)jd - 1; // julian day number
    		myRes[4] = (int)wd - 1; // weekday number
    		myRes[5] = (int)id; // islamic date
    		myRes[6] = (int)im - 1; // islamic month
    		myRes[7] = (int)iy; // islamic year
			hMonth=myRes[6];
			hDay=myRes[5];
		//	hDay=dayCorrection(hMonth,hDay);
		//	if (hDay>30) hMonth++;
			
		//	hWeekDay=myRes[4];
		//	hWeekDay=dayCorrection(hMonth,hWeekDay);
		//	if (hWeekDay>6) hWeekDay=0;
			hWeekDay=getWeekDayNumber()-1;
			if (hWeekDay<0) hWeekDay=6;
			hYear=myRes[7];
			//if (hMonth>12) {hMonth=1;hYear++;}
			
			
			
    		return myRes;
			
    	}
		/*
private int dayCorrection(int month,int day)
{
if (month>1)
	for (int i=1;i<month;i++) day+=hijriMonthDayCorrection.get(i);
	return day;
}*/
    	public  String writeIslamicDate() {
    				// This Value is used to give the correct day +- 1 day
    		
    		kuwaiticalendar(hAdjust);
    		String outputIslamicDate = getHWeekDayName() + ", " + PersianCalendarConstants.toArabicNumbers(hDay)
				+ " " + getHMonthName() + " " + PersianCalendarConstants.toArabicNumbers(hYear) + " ";

    		return outputIslamicDate;
    	}
    
	/**
	 * <pre>
	 *    use <code>{@link PersianDateParser}</code> to parse string 
	 *    and get the Persian Date.
	 * </pre>
	 * 
	 * @see PersianDateParser
	 * @param dateString
	 */
	public void parse(Context context,String dateString) {
		PersianCalendar p = new PersianDateParser(dateString, delimiter).getPersianDate(context);
		setPersianDate(p.getPersianYear(), p.getPersianMonth(), p.getPersianDay());
	}

	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * assign delimiter to use as a separator of date fields.
	 * 
	 * @param delimiter
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public String toString() {
		String str = super.toString();
		return str.substring(0, str.length() - 1) + ",PersianDate=" + getPersianShortDate() + "]";
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);

	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public void set(int field, int value) {
		super.set(field, value);
		calculatePersianDate();
	}

	@Override
	public void setTimeInMillis(long millis) {
		super.setTimeInMillis(millis);
		calculatePersianDate();
	}

	@Override
	public void setTimeZone(TimeZone zone) {
		super.setTimeZone(zone);
		calculatePersianDate();
	}
	public boolean isCurrent(int persianDay)
	{
		return ( currentYear == getPersianYear() &&
			getPersianMonth()== currentMonth &&
			persianDay== currentDay);
	}
	public boolean isSelected(int persianDay)
	{
		return 
			(selectedYear == getPersianYear() &&
			getPersianMonth()== selectedMonth &&
			persianDay == selectedDay);
	}
	public void goToCurrentDate()
	{
		setTime(currentDate);
		calculatePersianDate();
	}
}
