package com.phonecompany.billing;

import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @Test
    public void testMainCase1() {
        String inputString = "src/main/resources/callsData.csv\n";
        System.setIn(new ByteArrayInputStream(inputString.getBytes()));

        PrintStream stdOut = System.out;
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        Main.main(new String[]{});

        String expectedText = "Celková cena hovorů:";
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedText));
        System.setOut(stdOut);
    }

    @Test
    public void testMainCase2() {
        // when
        String inputString = "\n";
        System.setIn(new ByteArrayInputStream(inputString.getBytes()));

        PrintStream stdOut = System.out;
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        Main.main(new String[]{});

        String expectedText = "Celková cena hovorů:";
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedText));
        System.setOut(stdOut);
    }

    @Test
    public void testMainExceptionCase() {
        // when
        String inputString = "not_existing_file_path.txt\n";
        System.setIn(new ByteArrayInputStream(inputString.getBytes()));

        PrintStream stdOut = System.out;
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        Main.main(new String[]{});

        String expectedExceptionText = "Nepodařilo se načíst soubor: not_existing_file_path.txt";
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedExceptionText));
        System.setOut(stdOut);
    }

    @Test
    public void testMainSystemPropertyCase() {
        String inputString = "\n";
        System.setIn(new ByteArrayInputStream(inputString.getBytes()));
        System.setProperty("callsData", "src/main/resources/callsData.csv");

        PrintStream stdOut = System.out;
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        Main.main(new String[]{});

        String expectedText = "Celková cena hovorů:";
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedText));
        System.setOut(stdOut);
    }
}