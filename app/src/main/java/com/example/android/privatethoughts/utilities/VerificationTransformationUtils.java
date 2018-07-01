/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.privatethoughts.utilities;

/**
 * Class for performing verifications and transformations
 */
public class VerificationTransformationUtils {

    /**
     * Creates and string from and email by removing "@" and "."
     * @param emailAccount proper email address
     * @return string that can be added to URI
     */
    public static String getEmailString(String emailAccount) {
        if (emailAccount.contains(".")) {
            char[] emailAccountChar = emailAccount.toCharArray();

            if (emailAccountChar.length > 0) {
                emailAccount = "";

                for (char c : emailAccountChar) {
                    if (c != '.') {
                        emailAccount = emailAccount.concat(Character.toString(c));
                    }
                }
            }
        }

        if (emailAccount.contains("@")) {
            String[] emailAccountArray = emailAccount.split("@");

            if (emailAccountArray.length > 0) {
                emailAccount = "";

                for (String s : emailAccountArray) {
                    emailAccount = emailAccount.concat(s.trim());
                }
            }
        }

        return emailAccount;
    }


    /**
     * Validates email string structure
     * @param email email string to be verified
     * @return true if string is email, false otherwise
     */
    public static boolean checkEmail(String email) {
        boolean validEmail = true;

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            validEmail = false;
        }

        return validEmail;
    }
}