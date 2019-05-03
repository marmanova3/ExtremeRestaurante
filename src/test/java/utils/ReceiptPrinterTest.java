package utils;

import model.OrderItemEntity;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReceiptPrinterTest {

    @Test
    public void shouldReturnEmptyString() {
        assertEquals("", ReceiptPrinter.gaps(0));
    }

    @Test
    public void shouldReturnEmptyStringWhenNegativeCount() {
        assertEquals("", ReceiptPrinter.gaps(-2));
    }

    @Test
    public void shouldReturnStringWithTwoGaps() {
        assertEquals("  ", ReceiptPrinter.gaps(2));
    }

    @Test
    public void shouldReturnReceiptLine() {
        String line = "*Ahoj      *\n";
        assertEquals(line, ReceiptPrinter.createReceiptLine(12, "Ahoj"));
    }

    @Test
    public void shouldReturnReceiptCenteredLineWithEvenContentSize() {
        String line = "* Ahoj *\n";
        assertEquals(line, ReceiptPrinter.createReceiptCenterLine(8, "Ahoj"));
    }

    @Test
    public void shouldReturnReceiptCenteredLineWithOddContentSize() {
        String line = "* obsah s neparnou dlzkou  *\n";
        assertEquals(line, ReceiptPrinter.createReceiptCenterLine(28, "obsah s neparnou dlzkou"));
    }

    @Test
    public void shouldReturnReceiptRightAlignLine() {
        String line = "*     zarovnany do prava*\n";
        assertEquals(line, ReceiptPrinter.createReceiptRightAlignLine(25, "zarovnany do prava"));
    }

    @Test
    public void shouldReturnLineWithThreeStars() {
        String line = "***\n";
        assertEquals(line, ReceiptPrinter.fullStarLine(3));
    }

    @Test
    public void shouldReturnNewLine() {
        String line = "\n";
        assertEquals(line, ReceiptPrinter.fullStarLine(0));
    }

    @Test
    public void shouldReturnNewLineWhitNegativeLineWidth() {
        String line = "\n";
        assertEquals(line, ReceiptPrinter.fullStarLine(-1));
    }

    @Test
    public void shouldReturnReceiptFooter() {
        String footer = "";
        footer += "*************************\n";
        footer += "*  THANK YOU FOR VISIT  *\n";
        footer += "*************************\n";
        assertEquals(footer, ReceiptPrinter.receiptFooter(25));
    }

    @Test
    public void shouldReturnReceiptOutlay() {
        String outlay = "";
        outlay += "* CASH: 20.00 EUR     *\n";
        outlay += "* OUTLAY: 0.45 EUR    *\n";
        outlay += "***********************\n";
        assertEquals(outlay, ReceiptPrinter.receiptOutlay(23, "20.00", "0.45"));
    }

    @Test
    public void shouldReturnReceiptTotal() {
        String total = "";
        total += "****************************\n";
        total += "*     TOTAL: 70.00 EUR     *\n";
        total += "*      DISCOUNT: -10%      *\n";
        total += "* PRICE TO PAY: 63.00 EUR  *\n";
        total += "****************************\n";
        assertEquals(total, ReceiptPrinter.receiptTotal(28, "70.00", "63.00", "10"));
    }

    @Test
    public void shouldReturnReceiptHeader() {
        String header = "";
        String dateTime = ReceiptPrinter.currentDateTime();
        header += "******************************\n";
        header += "*    EXTREME RESTAURANTE     *\n";
        header += "******************************\n";
        header += "*    " + dateTime + "     *\n";
        header += "******************************\n";
        assertEquals(header, ReceiptPrinter.receiptHeader(30));
    }

    @Test
    public void shouldReturnReceiptItems() {
        OrderItemEntity soup = new OrderItemEntity();
        soup.setName("Polievka");
        soup.setPrice(5.25);
        soup.setQuantity(3);

        OrderItemEntity cola = new OrderItemEntity();
        cola.setName("Cola");
        cola.setPrice(1.25);
        cola.setQuantity(2);

        List<OrderItemEntity> itemList = new ArrayList<>();
        itemList.add(soup);
        itemList.add(cola);

        String items = "";
        items += "* Polievka                   *\n";
        items += "* 3 x 5.25                   *\n";
        items += "*                      15,75 *\n";
        items += "* Cola                       *\n";
        items += "* 2 x 1.25                   *\n";
        items += "*                       2,50 *\n";
        assertEquals(items, ReceiptPrinter.receiptItems(30, itemList));
    }

}
