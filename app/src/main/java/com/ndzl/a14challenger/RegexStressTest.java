package com.ndzl.a14challenger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexStressTest {

    public static void two() {

        Pattern regex = Pattern.compile("(\\w+)"); // One capturing group
        Matcher match = regex.matcher("Nik");

        if (match.find()) {
            String value = match.group("foo"); // Trying to access a non-existent named group

            System.out.println(value); // Returns null
        }

    }

    public static void three() {


        Pattern regex = Pattern.compile("(\\w+) (\\d+)"); // Two capturing groups
        Matcher match = regex.matcher("Demi 3");

        if (match.find()) {
            String value = match.group(3); // Trying to access group 3 (out of bounds)

            System.out.println(value); // Throws IndexOutOfBoundsException
        }



    }



}

