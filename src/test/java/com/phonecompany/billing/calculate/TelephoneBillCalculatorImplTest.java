package com.phonecompany.billing.calculate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class TelephoneBillCalculatorImplTest {

    private TelephoneBillCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new TelephoneBillCalculatorImpl();
    }

    @Test
    void testNoValue() {
        String phoneLog = "";
        assertEquals(BigDecimal.ZERO, calculator.calculate(phoneLog));
    }

    @Test
    void testShortCallDuringPeakHours() {
        String phoneLog = "420774577453,13-01-2020 09:10:00,13-01-2020 09:12:00";
        BigDecimal expected = new BigDecimal("2.00");
        assertEquals(expected, calculator.calculate(phoneLog));
    }

    @Test
    void testLongCallDuringPeakHoursWithDiscountedRate() {
        String phoneLog = "420774577453,13-01-2020 09:00:00,13-01-2020 09:07:00";
        BigDecimal expected = new BigDecimal("5.40");
        assertEquals(expected, calculator.calculate(phoneLog));
    }

    @Test
    void testCallOutsidePeakHours() {
        String phoneLog = "420774577453,13-01-2020 17:00:00,13-01-2020 17:03:00";
        BigDecimal expected = new BigDecimal("1.50");
        assertEquals(expected, calculator.calculate(phoneLog));
    }

    @Test
    void testCallSpanningIntoLowerRate() {
        String phoneLog = "420774577453,13-01-2020 15:58:00,13-01-2020 16:02:00";
        BigDecimal expected = new BigDecimal("3.00");
        assertEquals(expected, calculator.calculate(phoneLog));
    }

    @Test
    void testPromotionForMostCalledNumber() {
        String phoneLog =   "420774577453,13-01-2020 09:00:00,13-01-2020 09:03:00\n" +
                            "420774577453,13-01-2020 10:00:00,13-01-2020 10:03:00\n" +
                            "420776562353,13-01-2020 11:00:00,13-01-2020 11:03:00";
        BigDecimal expected = new BigDecimal("3.00");
        assertEquals(expected, calculator.calculate(phoneLog));
    }

    @Test
    public void testCalculateFromCSV() throws Exception {
        TelephoneBillCalculator calculator = new TelephoneBillCalculatorImpl();
        String path = Paths.get("src", "test", "resources", "test_calls.csv").toString();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            BigDecimal totalCost = BigDecimal.ZERO;
            while ((line = br.readLine()) != null) {
                totalCost = totalCost.add(calculator.calculate(line));
            }
            BigDecimal expected = new BigDecimal("9951.60");
            assertEquals(expected, totalCost);
        }
    }
}
