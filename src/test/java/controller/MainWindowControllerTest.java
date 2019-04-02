package controller;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class MainWindowControllerTest {

    MainWindowController mainWindowController;

    @Before
    public void setUp() {
        mainWindowController = new MainWindowController();
    }

    @Test
    public void shouldReturnTableName() {
        assertEquals("2", mainWindowController.parseTableName("tab2"));
    }

    @Test
    public void shouldReturnEmptyString1() {
        assertEquals("", mainWindowController.parseTableName("tab"));
    }

    @Test
    public void shouldReturnEmptyString2() {
        assertEquals("", mainWindowController.parseTableName(""));
    }

    @Test
    public void tableShouldBeOccupied() {
        List<String> tableNames = Arrays.asList("1", "2", "3");
        assertTrue(mainWindowController.tableIsOccupied("2", tableNames));
    }

    @Test
    public void tableShouldNotBeOccupied() {
        List<String> tableNames = Arrays.asList("1", "2", "3");
        assertFalse(mainWindowController.tableIsOccupied("4", tableNames));
    }


}
