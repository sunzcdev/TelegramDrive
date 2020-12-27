package com.psaravan.filebrowserview.lib.db;

import java.util.Date;

import androidx.room.TypeConverter;

public class ConversionFactory {

	@TypeConverter
	public static Long fromDateToLong(Date date) {
		return date == null ? null : date.getTime();
	}

	@TypeConverter
	public static Date fromLongToDate(Long value) {
		return value == null ? null : new Date(value);
	}
}
