package org.jplot2d.axtick;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A calendar start from TAI epoch and count in microseconds.
 * 
 * @author Jingjing Li
 * 
 */
public class TAIMicrosCalendar implements Comparable<TAIMicrosCalendar>, Cloneable {

	public static long TAI_EPOCH_OFFSET = -378691200000L;

	public static int MICROSECOND = Calendar.FIELD_COUNT;

	final Calendar calendar;

	/**
	 * The microsecond part under millisecond
	 */
	int microsecond;

	private TAIMicrosCalendar(Calendar calendar, int microsecond) {
		this.calendar = calendar;
		this.microsecond = microsecond;
	}

	public TAIMicrosCalendar(TimeZone zone, Locale locale) {
		calendar = Calendar.getInstance(zone, locale);
	}

	public long getTimeInMicros() {
		return (calendar.getTimeInMillis() - TAI_EPOCH_OFFSET) * 1000 + microsecond;
	}

	public void setTimeInMicros(long micros) {
		microsecond = (int) (micros % 1000);
		long millis = micros / 1000 + TAI_EPOCH_OFFSET;
		calendar.setTimeInMillis(millis);
	}

	public Object clone() {
		return new TAIMicrosCalendar(calendar, microsecond);
	}

	public int get(int field) {
		if (field == MICROSECOND) {
			return microsecond;
		} else {
			return calendar.get(field);
		}
	}

	public void set(int field, int value) {
		if (field == MICROSECOND) {
			microsecond = value;
		} else {
			calendar.set(field, value);
		}
	}

	public final void set(int year, int month, int date, int hourOfDay, int minute, int second) {
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, date);
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
	}

	public int getMinimum(int field) {
		if (field == MICROSECOND) {
			return 0;
		} else {
			return calendar.getMinimum(field);
		}
	}

	public void add(int field, int amount) {
		if (field == MICROSECOND) {
			microsecond += amount;
			if (microsecond < 0 || microsecond >= 1000) {
				calendar.add(Calendar.MILLISECOND, microsecond / 1000);
				microsecond %= 1000;
				if (microsecond < 0) {
					microsecond += 1000;
					calendar.add(Calendar.MILLISECOND, -1);
				}
			}
		} else {
			calendar.add(field, amount);
		}
	}

	public boolean before(TAIMicrosCalendar when) {
		return compareTo(when) < 0;
	}

	public boolean after(TAIMicrosCalendar when) {
		return compareTo(when) > 0;
	}

	public int compareTo(TAIMicrosCalendar anotherTAI) {
		int compare = calendar.compareTo(anotherTAI.calendar);
		if (compare == 0) {
			return microsecond - anotherTAI.microsecond;
		} else {
			return compare;
		}
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof TAIMicrosCalendar) {
			TAIMicrosCalendar another = (TAIMicrosCalendar) obj;
			return microsecond == another.microsecond && calendar.equals(another.calendar);
		}
		return false;
	}

	public String toString() {
		return "TAI MICRO=" + microsecond + "," + calendar.toString();
	}

}