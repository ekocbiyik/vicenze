package com.meyratech.vicenze;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ekocbiyik on 14.09.2019
 */
public class DateFormatTest {

    public static void main(String[] args) throws Exception {

        String date1 = "21/12/2019";
        String date2 = "12/21/2019";
        String date3 = "21/12/2019 12:12:12";
        String date4 = "12/21/2019 12:12:12";
        System.out.println(validateDate(date1));
        System.out.println(validateDate(date2));
        System.out.println(validateDate(date3));
        System.out.println(validateDate(date4));
    }


    private static LocalDateTime validateDate(String dateString) {
        LocalDateTime date;
        String exceptFormat = (dateString.contains(" ") && dateString.contains(":")) ? "d/M/yyyy HH:mm:ss" : "d/M/yyyy";
        String errorFormat = (dateString.contains(" ") && dateString.contains(":")) ? "M/d/yyyy HH:mm:ss" : "M/d/yyyy";

        try {
            if (exceptFormat.contains(":")) {
                date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(exceptFormat));
            } else {
                date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(exceptFormat)).atStartOfDay();
            }
        } catch (Exception e) {
            if (errorFormat.contains(":")) {
                date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(errorFormat));
            } else {
                date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(errorFormat)).atStartOfDay();
            }
        }
        return date;
    }

}
