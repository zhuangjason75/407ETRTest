package org.example;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.IntStream;

public class TollCalculator {

    private static final String JSON_FIELD_LOCATIONS = "locations";

    //store the sum of distance between first location and current location for forward direction
    private double[] forwardDistanceSum;

    //store the sum of distance between the last location and current location for reverse direction
    private double[] reverseDistanceSum;

    //store the map of name -> id used to get location id from location name
    private Map<String, Integer> nameIdMap;

    //store the map of location id -> location object
    private Map<Integer, Location> locationsMap;

    public TollCalculator() {
        nameIdMap = new HashMap<>();
        locationsMap = new HashMap<>();
    }

    public void initialize(String fileName) throws URISyntaxException, IOException {
        ObjectMapper mapper = new ObjectMapper();

        File locationFile = getFileFromResource(fileName);

        JsonNode jsonNode = mapper.readTree(locationFile);
        if (!jsonNode.has(JSON_FIELD_LOCATIONS)) {
            throw new IllegalArgumentException(JSON_FIELD_LOCATIONS + " field not found in json file " + fileName);
        }
        JsonNode locationsNode = jsonNode.get(JSON_FIELD_LOCATIONS);
        Iterator<Map.Entry<String, JsonNode>> iterator = locationsNode.getFields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            int id = Integer.parseInt(entry.getKey());
            JsonNode locationNode = entry.getValue();
            Location location = mapper.readValue(locationNode, Location.class);
            locationsMap.put(id, location);
        }
        //fill in location->id map
        locationsMap.entrySet().forEach(entry -> {
            nameIdMap.put(entry.getValue().getName().toUpperCase(), entry.getKey());
        });

        forwardDistanceSum = new double[Collections.max(locationsMap.keySet()) + 1];
        forwardDistanceSum[0] = 0d;
        IntStream.range(1, forwardDistanceSum.length - 1).forEach(id -> {
                    if (!locationsMap.containsKey(id)) {
                        //location id is not sequential, set the distance to the missing location to be 0
                        forwardDistanceSum[id] = forwardDistanceSum[id - 1];
                    } else {
                        locationsMap.get(id)
                                .getRoutes()
                                .stream()
                                .filter(route -> route.getToId() > id)
                                .findFirst()
                                .ifPresentOrElse(
                                        route -> {
                                            //fill in sum of distance between location 1 to current location
                                            forwardDistanceSum[id] = forwardDistanceSum[id - 1] + route.getDistance();
                                            //if cannot enter into target route, set forwardEnterEnabled of current location to false
                                            if (!route.isEnter()) {
                                                locationsMap.get(id).setForwardEnterEnabled(false);
                                            }
                                            //if cannot exist from target route, set forwardExitEnabled of target location to false
                                            if (!route.isExit()) {
                                                locationsMap.get(route.getToId()).setForwardExitEnabled(false);
                                            }
                                        },
                                        //set distance to next location to be 0 if next location is invalid
                                        () -> forwardDistanceSum[id] = forwardDistanceSum[id - 1]);
                    }
                }
        );

        //initialize the reverse direction, same as how to initialize the forward direction above
        reverseDistanceSum = new double[forwardDistanceSum.length];
        reverseDistanceSum[0] = 0d;
        IntStream.range(1, reverseDistanceSum.length - 1)
            .map(i -> forwardDistanceSum.length - i)
            .forEach(id -> {
                if (!locationsMap.containsKey(id)) {
                    reverseDistanceSum[forwardDistanceSum.length - id] = reverseDistanceSum[forwardDistanceSum.length - id - 1];
                } else {
                    locationsMap.get(id)
                            .getRoutes()
                            .stream()
                            .filter(route -> route.getToId() < id)
                            .findFirst()
                            .ifPresentOrElse(
                                    route -> {
                                        reverseDistanceSum[reverseDistanceSum.length - id] = reverseDistanceSum[reverseDistanceSum.length - id - 1] + route.getDistance();
                                        if (!route.isEnter()) {
                                            locationsMap.get(id).setReverseEnterEnabled(false);
                                        }
                                        if (!route.isExit()) {
                                            locationsMap.get(route.getToId()).setReverseEnterEnabled(false);
                                        }
                                    },
                                    () -> reverseDistanceSum[reverseDistanceSum.length - id] = reverseDistanceSum[reverseDistanceSum.length - id - 1]);
                }
            }
        );
    }

    private File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }

    }

    public double[] getTollCost(int startId, int endId, double tollRate) throws IllegalArgumentException {
        Location startLocation = locationsMap.get(startId);
        Location endLocation = locationsMap.get(endId);
        boolean isForward = startId < endId;
        if (isForward) {
            if (!startLocation.isForwardEnterEnabled()) {
                throw new IllegalArgumentException("Cannot enter from " + locationsMap.get(startId).getName() + " to " + locationsMap.get(endId).getName());
            }
            if (!endLocation.isForwardExitEnabled()) {
                throw new IllegalArgumentException("Cannot exist from " + locationsMap.get(startId).getName() + " to " + locationsMap.get(endId).getName());
            }
            //forwardDistanceSum[index] contains the sum of distance from location1 to location n,
            // we can use subtraction to get the distance between location m and location n and round to three decimal
            double distance = Math.round((forwardDistanceSum[endId -1] - forwardDistanceSum[startId -1]) * 1000d) / 1000d;
            double cost = Math.round(distance * tollRate * 100d) / 100d;
            return new double[] {distance,  cost};
        }
        else {
            if (!startLocation.isReverseEnterEnabled()) {
                throw new IllegalArgumentException("Cannot enter from " + locationsMap.get(startId).getName() + " to " + locationsMap.get(endId).getName());
            }
            if (!endLocation.isReverseExitEnabled()) {
                throw new IllegalArgumentException("Cannot exist from " + locationsMap.get(startId).getName() + " to " + locationsMap.get(endId).getName());
            }
            double distance = Math.round((reverseDistanceSum[reverseDistanceSum.length - endId - 1] - reverseDistanceSum[reverseDistanceSum.length - startId - 1]) * 1000d) / 1000d;
            double cost = Math.round(distance * tollRate * 100d) / 100d;
            return new double[] {distance, cost};
        }

    }

    public int getLocatonId(String locationName) throws IllegalArgumentException {
        locationName = locationName.trim().toUpperCase();
        if (locationName.isEmpty() || !nameIdMap.containsKey(locationName)) {
            throw new IllegalArgumentException(locationName + " does not exist.");
        }
        return nameIdMap.get(locationName);
    }
}
