package com.example.android.privatethoughts.utilities;

public class VerificationTransformationUtils {

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


    public static boolean checkEmail(String email) {
        boolean validEmail = true;

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            validEmail = false;
        }

        return validEmail;
    }
}
