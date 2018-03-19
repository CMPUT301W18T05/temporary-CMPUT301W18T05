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

/**
 * Created by wyatt on
 */

public class PhoneNumber {

    private String phone;

    public PhoneNumber(String phone) {
        this.phone = phone;
    }

    public PhoneNumber(){
    }

    /**
     * set phone number
     * @param phone
     * @author Micheal-Nguyen
     */
    public void setPhoneNumber(String phone){
        this.phone = phone;
    }

    /**
     * get the phone number and return it
     * @return
     */
    public String getPhoneNumber(){
        return phone;
    }

    /**
     * convert phone number from long to string
     * @return
     * @author Micheal-Nguyen
     */
    public String toString() {
        return phone;
    }
}




