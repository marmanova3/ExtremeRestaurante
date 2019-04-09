package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {

    private static final String DOUBLE_PATTERN = "\\d{0,7}([\\.]\\d{0,2})?";


    public static Double getRoundedDecimalNumber(Double number, int places) {
        BigDecimal bd = new BigDecimal(Double.toString(number));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static boolean hasDoubleFormat(String value) {
        return value.matches(DOUBLE_PATTERN);
    }

    public static boolean isValidDoubleFormat(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /*
    returns true if comparingValue is greater or equal than otherValue
     */
    public static boolean isGreaterOrEqual(Double comparingValue, Double otherValue) {
        return otherValue.compareTo(comparingValue) <= 0;
    }
}
