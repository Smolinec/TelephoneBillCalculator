package com.phonecompany.billing;

import com.phonecompany.billing.calculate.TelephoneBillCalculator;
import com.phonecompany.billing.calculate.TelephoneBillCalculatorImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.math.BigDecimal;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String filePath = determineFilePath();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            BigDecimal totalCost = BigDecimal.ZERO;
            TelephoneBillCalculator calculator = new TelephoneBillCalculatorImpl();
            while ((line = br.readLine()) != null) {
                totalCost = totalCost.add(calculator.calculate(line));
            }
            System.out.println("Celková cena hovorů: " + totalCost + " Kč");
        } catch (IOException e) {
            System.out.println("Nepodařilo se načíst soubor: " + filePath);
            e.printStackTrace();
        }
    }

    private static String determineFilePath() {
        String resourcePath = "src/main/resources/callsData.csv";
        String systemPropertyPath = System.getProperty("callsData");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Zadejte cestu k souboru s daty o voláních (nechte prázdné pro výchozí): ");
        String userInputPath = scanner.nextLine();

        if (userInputPath != null && !userInputPath.isEmpty()) {
            return userInputPath;
        } else if (systemPropertyPath != null && !systemPropertyPath.isEmpty()) {
            return systemPropertyPath;
        } else {
            return resourcePath;
        }
    }
}