package com.icecondor.nest;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.os.Build;

public class Util {
	public static String DateTimeIso8601Now() {
		return Util.DateTimeIso8601(System.currentTimeMillis());
	}
	
	public static String DateTimeIso8601NowShort() {
		return Util.DateTimeIso8601(System.currentTimeMillis()).substring(0,19);
	}
	
	public static String DateTimeIso8601(long offset) {
		DateFormat datePattern =  new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssZ");
		datePattern.setTimeZone(TimeZone.getTimeZone("GMT"));
		String date = datePattern.format(new Date(offset));
		if (Build.VERSION.SDK_INT == 7) {
			// Android 2.1 timezone bug
			// http://code.google.com/p/android/issues/detail?id=8258
			date = date.substring(0, 19)+"Z";
		}
        return date;
	}
	
	public static Date DateRfc822(String date) {
		 SimpleDateFormat rfc822DateFormats[] = new SimpleDateFormat[] { 
				 new SimpleDateFormat("EEE, d MMM yy HH:mm:ss z"), 
				 new SimpleDateFormat("EEE, d MMM yy HH:mm z"), 
				 new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z"), 
				 new SimpleDateFormat("EEE, d MMM yyyy HH:mm z"), 
				 new SimpleDateFormat("d MMM yy HH:mm z"), 
				 new SimpleDateFormat("d MMM yy HH:mm:ss z"), 
				 new SimpleDateFormat("d MMM yyyy HH:mm z"), 
				 new SimpleDateFormat("d MMM yyyy HH:mm:ss z"),
				 new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz"),
				 new SimpleDateFormat("yyyyMMdd'T'HHmmss"),
				 new SimpleDateFormat("yyyyMMdd")};
		 
		 Date parsed = null;
		 for(int i=0; i < rfc822DateFormats.length; i++) {
			 try {
				parsed = rfc822DateFormats[i].parse(date);
				break;
			} catch (ParseException e) {
				// not this one
			}
		 }
		 return parsed;
	}
	public static String timeAgoInWords(long mark) {
		String ago="none";
		long milliseconds_ago = (Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis() - mark);
		ago = millisecondsToWords(milliseconds_ago)+" ago";
		return ago;
	}

	public static String millisecondsToWords(long duration) {
		long count = duration / 1000;
		String unit = "sec";
		if (count > 60) {
			count = count / 60;
			unit = "min";
		}
		return ""+count+" "+unit;
	}

    /** A name/value pair. */
    public static class Parameter implements Map.Entry<String, String> {

        public Parameter(String key, String value) {
            this.key = key;
            this.value = value;
        }

        private final String key;

        private String value;

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public String setValue(String value) {
            try {
                return this.value;
            } finally {
                this.value = value;
            }
        }

        @Override
        public String toString() {
            return getKey() + '=' + getValue();
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Parameter that = (Parameter) obj;
            if (key == null) {
                if (that.key != null)
                    return false;
            } else if (!key.equals(that.key))
                return false;
            if (value == null) {
                if (that.value != null)
                    return false;
            } else if (!value.equals(that.value))
                return false;
            return true;
        }
    }

	public static String DateToShortDisplay(Date date) {
		String date_string;
		String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		String month_name = months[date.getMonth()-1];
		String ampm;
		int hours = date.getHours();
		if(date.getHours() > 12) {
			ampm="pm";
			hours = hours - 12;
		} else {
			ampm="am";
		}
		date_string = ""+hours+":"+date.getMinutes()+ampm+" "+month_name+" "+date.getDate();
		return date_string;
	}

}
