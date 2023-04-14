package org.example;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {

    private static final String LOCATION_FILE_NAME = "interchanges.json";

    private static final double TOLL_RATE = 0.25d;

    public static void main(String[] args) {
        TollCalculator tollCalculator = new TollCalculator();

        try {
            tollCalculator.initialize(LOCATION_FILE_NAME);
        } catch (URISyntaxException e) {
            System.err.println(e.getMessage());
            System.err.println("Failed to locate the initialization file.");
            return;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Failed to read the initialization file.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("please input start and end name in the format of: costOfTrip( 'start name', 'end name' )");
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                System.out.println("input is empty or format error, please input in the format of costOfTrip( 'start name', 'end name' )");
                continue;
            }

            String[] splitInput = input.split("\'");
            if (splitInput.length < 5) {
                System.out.println("input format is not correct, please input in the format of costOfTrip( 'start name', 'end name' )");
                continue;
            }
            String startLocationName = splitInput[1];
            String endLocationName = splitInput[3];

            int startId = 0, endId = 0;
            try {
                startId = tollCalculator.getLocatonId(startLocationName.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                continue;
            }
            try {
                endId = tollCalculator.getLocatonId(endLocationName.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                continue;
            }
            try {
                double[] result = tollCalculator.getTollCost(startId, endId, TOLL_RATE);
                System.out.println("Distance: " + result[0]);
                System.out.println("Cost: " + result[1]);
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                continue;
            }
        }
    }
}