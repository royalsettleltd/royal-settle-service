package africa.royalsettle.notification.Utils;

import java.util.regex.Pattern;


public class PhoneNumberUtils {
    private PhoneNumberUtils() {}

    public static String format(String phone) {
        String destinationMsisdn = phone.trim(), pre = "234";
        String formattedDest = "invalid";
        try {
            if (destinationMsisdn.length() == 14) {
                if (destinationMsisdn.startsWith("+234") && (Pattern.matches("[+][2][3][4][0-9]*", destinationMsisdn))) {
                    formattedDest = destinationMsisdn;
                }
            } else if (destinationMsisdn.length() == 13) {
                if (destinationMsisdn.startsWith("234") && (Pattern.matches("[2][3][4][0-9]*", destinationMsisdn))) {
                    formattedDest = destinationMsisdn;
                }
            } else if (destinationMsisdn.length() == 11) {
                if (destinationMsisdn.startsWith("0") && (Pattern.matches("[0-9]*", destinationMsisdn))) {
                    formattedDest = destinationMsisdn.replaceFirst("0", "234");
                }
            } else if (destinationMsisdn.length() == 10 && (Pattern.matches("[0-9]*", destinationMsisdn))) {
                formattedDest = pre.concat(destinationMsisdn);
            }
        } catch (NullPointerException e) {
            return "";
        }
        return formattedDest;
    }
}