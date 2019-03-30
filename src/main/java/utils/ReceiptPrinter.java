package utils;

import model.OrderItemEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ReceiptPrinter {

    private static final String TOTAL = " TOTAL: ";
    private static final String EXTREME_RESTAURANTE = " EXTREME RESTAURANTE ";
    private static final String EUR = " EUR ";
    private static final String CASH = " CASH: ";
    private static final String OUTLAY = " OUTLAY: ";
    private static final String BEGIN = "*";
    private static final String END = "*\n";
    private static final String THANK_YOU_FOR_VISIT = " THANK YOU FOR VISIT ";

    public static void print(String total, String cash, String outlay, List<OrderItemEntity> orderItems) {
        int lineWidth = lineWidth(longestItemNameSize(orderItems), 25);
        System.out.print(receiptHeader(lineWidth));
        System.out.print(receiptItems(lineWidth, orderItems));
        System.out.print(receiptTotal(lineWidth, total));
        System.out.print(receiptOutlay(lineWidth, cash, outlay));
        System.out.print(receiptFooter(lineWidth));
    }

    public static int lineWidth(int longestItemNameSize, int minWidth) {
        return longestItemNameSize > minWidth ? longestItemNameSize : minWidth;
    }

    public static int longestItemNameSize(List<OrderItemEntity> orderItems) {
        int maxSize = 0;
        for (OrderItemEntity orderItem : orderItems) {
            maxSize = orderItem.getNameLength() > maxSize ? orderItem.getNameLength() : maxSize;
        }
        return maxSize + 10;
    }

    public static String receiptHeader(int lineWidth) {
        String header = "";
        header += fullStarLine(lineWidth);
        header += createReceiptCenterLine(lineWidth, EXTREME_RESTAURANTE);
        header += fullStarLine(lineWidth);
        header += createReceiptCenterLine(lineWidth, currentDateTime());
        header += fullStarLine(lineWidth);
        return header;
    }

    public static String receiptItems(int lineWidth, List<OrderItemEntity> orderItems) {
        String items = "";
        for (OrderItemEntity item : orderItems) {
            items += createReceiptLine(lineWidth, " " + item.getName());
            items += createReceiptLine(lineWidth, " " + item.getQuantity() + " x " + item.getPrice());
            Double price = item.getQuantity() * item.getPrice();
            items += createReceiptRightAlignLine(lineWidth, Double.toString(price) + " ");
        }
        return items;
    }

    public static String receiptTotal(int lineWidth, String total) {
        String totalLine = "";
        totalLine += fullStarLine(lineWidth);
        totalLine += createReceiptCenterLine(lineWidth, TOTAL + total + EUR);
        totalLine += fullStarLine(lineWidth);
        return totalLine;
    }


    public static String receiptOutlay(int lineWidth, String cash, String outlay) {
        String lines = "";
        lines += createReceiptLine(lineWidth, CASH + cash + EUR);
        lines += createReceiptLine(lineWidth, OUTLAY + outlay + EUR);
        lines += fullStarLine(lineWidth);
        return lines;
    }

    public static String receiptFooter(int lineWidth) {
        String footer = "";
        footer += fullStarLine(lineWidth);
        footer += createReceiptCenterLine(lineWidth, THANK_YOU_FOR_VISIT);
        footer += fullStarLine(lineWidth);
        return footer;
    }

    public static String currentDateTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public static String fullStarLine(int lineWidth) {
        if (lineWidth < 0) return "\n";
        String line = String.join("", Collections.nCopies(lineWidth, "*"));
        return line + "\n";
    }

    public static String createReceiptLine(int lineWidth, String content) {
        int numberOfGaps = lineWidth - 2 - content.length();
        return BEGIN + content + gaps(numberOfGaps) + END;
    }

    public static String createReceiptCenterLine(int lineWidth, String content) {
        int numberOfGaps = lineWidth - 2 - content.length();
        int numberOfLeftGaps = numberOfGaps / 2;
        return BEGIN + gaps(numberOfLeftGaps) + content + gaps(numberOfGaps - numberOfLeftGaps) + END;
    }

    public static String createReceiptRightAlignLine(int lineWidth, String content) {
        int numberOfGaps = lineWidth - 2 - content.length();
        return BEGIN + gaps(numberOfGaps) + content + END;
    }

    public static String gaps(int count) {
        if (count < 0) return "";
        return String.join("", Collections.nCopies(count, " "));
    }
}
