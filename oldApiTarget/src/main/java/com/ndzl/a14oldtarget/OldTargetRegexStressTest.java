package com.ndzl.a14oldtarget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OldTargetRegexStressTest {

    public static void go() {
/*        Pattern regex = Pattern.compile("(?<name>\\w+) (?<age>\\d+)");
        Matcher match = regex.matcher("John 30");

        if (match.find()) { // Check if a match is found
            String value = match.group("Name"); // Accessing with incorrect capitalization
            System.out.println(value); // Output: null
        } else {
            System.out.println("No match found");
        }*/


/*
        Pattern regex = Pattern.compile("(\\w+) (\\d+)"); // Two capturing groups
        Matcher match = regex.matcher("Alice 25");

        if (match.find()) {
            String value = match.group(3); // Trying to access group 3 (out of bounds)

            System.out.println(value); // Throws IndexOutOfBoundsException
        }
*/

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

