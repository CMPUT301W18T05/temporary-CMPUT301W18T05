/*
 * Copyright 2018 (c) Andy Li, Colin Choi, James Sun, Jeremy Ng, Micheal Nguyen, Wyatt Praharenka
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.cmput301w18t05.taskzilla;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wyatt on 22/02/18.
 *
 * initial stub
 */

/**
 * @author andy li
 * @version 1
 */

public class EmailAddress {

    private String emailUsername;
    private String emailDomain;

    /**
     * Constructor that takes in a string email address and first checks if its valid.
     * If it is valid, set username and domain accordingly.
     *
     * @param email a email address inserted by the user
     * @see   User
     */

    public EmailAddress(String email) {                         // Constructor used when a string is sent in as a parameter
        if(isValid(email)) {
            String tempArray[] = email.split("@");
            this.emailUsername = tempArray[0];
            this.emailDomain = tempArray[1];
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @return  username of the email
     */

    public String getEmailUsername() {                          // Returns email username
         return this.emailUsername;
    }

    /**
     * @return  domain of the email
     */

    public String getEmailDomain() {                            // Returns email domain
        return this.emailDomain;
    }

    /**
     * @param email new email address specified by the user
     */

    public void setEmail(String email) {                        // Method used to change the email address of the current user
        if(isValid(email)) {
            String tempArray[] = email.split("@");
            this.emailUsername = tempArray[0];
            this.emailDomain = tempArray[1];
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * This checks if the email is valid by checking if it contains exactly one period and one @.
     *
     * @param email email address specified by the user
     * @return      True if valid, else False
     */

    public Boolean isValid(String email) {
        int periodCount = 0;
        int atCount = 0;

        Pattern period = Pattern.compile("([.])");
        Pattern at = Pattern.compile("([@])");

        Matcher periodMatcher = period.matcher(email);
        Matcher atMatcher = at.matcher(email);

        while(periodMatcher.find()) periodCount++;
        while(atMatcher.find()) atCount++;

        if(periodCount == 1 && atCount == 1)
            return true;

        return false;
    }

    /**
     * @return  Full email address
     */

    public String toString() {
        return emailUsername + "@" + emailDomain;
    }
}
