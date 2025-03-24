package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.System.*;

public class Main {
    public static void main(String[] args) {
        try {
            String locationsFilePath = "src/main/resources/locations.json";
            String metadataFilePath = "src/main/resources/metadata.json";

            Properties props = new Properties();
            File configFile = new File("src/main/resources/config.properties");

            if (configFile.exists()) {
                try (FileInputStream in = new FileInputStream(configFile)) {
                    props.load(in);

                    String propLocations = props.getProperty("locations.file");
                    if (propLocations != null) {
                        locationsFilePath = propLocations;
                    }

                    String propMetadata = props.getProperty("metadata.file");
                    if (propMetadata != null) {
                        metadataFilePath = propMetadata;
                    }
                }
            }

            out.println("Using locations file: " + locationsFilePath);
            out.println("Using metadata file: " + metadataFilePath);

            processMapData(locationsFilePath, metadataFilePath);

        } catch (Exception e) {
            err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Process map data from the specified files
     */
    public static void processMapData(String locationsFilePath, String metadataFilePath) throws IOException {
        List<Location> locations = loadLocations(locationsFilePath);
        List<Metadata> metadata = loadMetadata(metadataFilePath);

        out.println("Loaded " + locations.size() + " locations");
        out.println("Loaded " + metadata.size() + " metadata entries");

        List<MergedData> mergedData = mergeData(locations, metadata);

        analyzeData(mergedData);
    }

    /**
     * Load location data from a file
     * This method identifies the file format and uses the appropriate parser
     */
    private static List<Location> loadLocations(String filePath) throws IOException {
        String fileContent = readFile(filePath);

        if (filePath.endsWith(".json")) {
            return parseJsonLocations(fileContent);
        } else if (filePath.endsWith(".csv")) {
            return parseCsvLocations(fileContent);
        } else {
            throw new IllegalArgumentException("Unsupported file format for: " + filePath);
        }
    }

    /**
     * Load metadata from a file
     * This method identifies the file format and uses the appropriate parser
     */
    private static List<Metadata> loadMetadata(String filePath) throws IOException {
        String fileContent = readFile(filePath);

        if (filePath.endsWith(".json")) {
            return parseJsonMetadata(fileContent);
        } else if (filePath.endsWith(".csv")) {
            return parseCsvMetadata(fileContent);
        } else {
            throw new IllegalArgumentException("Unsupported file format for: " + filePath);
        }
    }

    /**
     * Read content from a file
     */
    private static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    /**
     * Parse location data from JSON format using Gson
     */
    private static List<Location> parseJsonLocations(String jsonContent) {
        try {
            Gson gson = new Gson();
            Type locationType = new TypeToken<List<Location>>(){}.getType();
            List<Location> locations = gson.fromJson(jsonContent, locationType);
            return locations != null ? locations : new ArrayList<>();
        } catch (Exception e) {
            err.println("Error parsing locations JSON: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Parse metadata from JSON format using Gson
     */
    private static List<Metadata> parseJsonMetadata(String jsonContent) {
        try {
            Gson gson = new Gson();
            Type metadataType = new TypeToken<List<Metadata>>(){}.getType();
            List<Metadata> metadata = gson.fromJson(jsonContent, metadataType);
            return metadata != null ? metadata : new ArrayList<>();
        } catch (Exception e) {
            err.println("Error parsing metadata JSON: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Parse location data from CSV format
     */
    private static List<Location> parseCsvLocations(String csvContent) {
        List<Location> locations = new ArrayList<>();


        String[] lines = csvContent.split("\n");
        if (lines.length <= 1) {
            return locations;
        }

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length >= 3) {
                try {
                    String id = parts[0].trim();
                    if (id.isEmpty()) {
                        err.println("Warning: Skipping CSV location line with empty ID: " + line);
                        continue;
                    }

                    double latitude = Double.parseDouble(parts[1].trim());
                    double longitude = Double.parseDouble(parts[2].trim());

                    locations.add(new Location(id, latitude, longitude));
                } catch (Exception e) {
                    err.println("Error parsing CSV location line: " + line);
                }
            }
        }

        return locations;
    }

    /**
     * Parse metadata from CSV format
     */
    private static List<Metadata> parseCsvMetadata(String csvContent) {
        List<Metadata> metadataList = new ArrayList<>();

        String[] lines = csvContent.split("\n");
        if (lines.length <= 1) {
            return metadataList;
        }

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length >= 4) {
                try {
                    String id = parts[0].trim();
                    if (id.isEmpty()) {
                        err.println("Warning: Skipping CSV metadata line with empty ID: " + line);
                        continue;
                    }

                    String type = parts[1].trim();
                    double rating = Double.parseDouble(parts[2].trim());
                    int reviews = Integer.parseInt(parts[3].trim());

                    metadataList.add(new Metadata(id, type, rating, reviews));
                } catch (Exception e) {
                    err.println("Error parsing CSV metadata line: " + line);
                }
            }
        }

        return metadataList;
    }

    /**
     * Merge location and metadata
     */
    private static List<MergedData> mergeData(List<Location> locations, List<Metadata> metadata) {
        Map<String, Location> locationMap = locations.stream()
                .filter(loc -> loc.getId() != null)
                .collect(Collectors.toMap(
                        Location::getId,
                        Function.identity(),
                        (existing, replacement) -> existing // Keep first occurrence on duplicate
                ));

        Map<String, Metadata> metadataMap = metadata.stream()
                .filter(meta -> meta.getId() != null)
                .collect(Collectors.toMap(
                        Metadata::getId,
                        Function.identity(),
                        (existing, replacement) -> existing // Keep first occurrence on duplicate
                ));

        Set<String> allIds = new HashSet<>();
        allIds.addAll(locationMap.keySet());
        allIds.addAll(metadataMap.keySet());

        List<MergedData> mergedData = new ArrayList<>();
        for (String id : allIds) {
            Location location = locationMap.get(id);
            Metadata meta = metadataMap.get(id);
            mergedData.add(new MergedData(id, location, meta));
        }

        return mergedData;
    }

    /**
     * Analyze merged data
     */
    private static void analyzeData(List<MergedData> mergedData) {
        Map<String, Integer> typeCount = new HashMap<>();

        Map<String, List<Double>> typeRatings = new HashMap<>();

        MergedData mostReviewedLocation = null;
        int maxReviews = -1;

        List<MergedData> incompleteData = new ArrayList<>();

        for (MergedData data : mergedData) {
            if (!data.isComplete()) {
                incompleteData.add(data);
                continue;
            }

            String type = data.getMetadata().getType();

            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);

            if (!typeRatings.containsKey(type)) {
                typeRatings.put(type, new ArrayList<>());
            }
            typeRatings.get(type).add(Double.valueOf(data.getMetadata().getRating()));

            if (data.getMetadata().getReviews() > maxReviews) {
                maxReviews = data.getMetadata().getReviews();
                mostReviewedLocation = data;
            }
        }

        Map<String, Double> avgRatings = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : typeRatings.entrySet()) {
            double sum = 0;
            for (Double rating : entry.getValue()) {
                sum += rating;
            }
            avgRatings.put(entry.getKey(), Double.valueOf(sum / entry.getValue().size()));
        }

        printResults(typeCount, avgRatings, mostReviewedLocation, incompleteData);
    }

    /**
     * Print the analysis results
     */
    private static void printResults(
            Map<String, Integer> typeCount,
            Map<String, Double> avgRatings,
            MergedData mostReviewedLocation,
            List<MergedData> incompleteData) {

        out.println("=== Map Data Analysis Results ===\n");

        out.println("1. Count of Valid Points per Type:");
        for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
            out.println(entry.getKey() + ": " + entry.getValue());
        }
        out.println();

        out.println("2. Average Rating per Type:");
        for (Map.Entry<String, Double> entry : avgRatings.entrySet()) {
            out.printf("%s: %.2f\n", entry.getKey(), entry.getValue());
        }
        out.println();

        out.println("3. Location with Highest Number of Reviews:");
        if (mostReviewedLocation != null) {
            out.printf("ID: %s, Type: %s, Reviews: %d\n",
                    mostReviewedLocation.getId(),
                    mostReviewedLocation.getMetadata().getType(),
                    mostReviewedLocation.getMetadata().getReviews());
        }
        out.println();

        out.println("4. Locations with Incomplete Data:");
        if (incompleteData.isEmpty()) {
            out.println("No incomplete data found.");
        } else {
            for (MergedData data : incompleteData) {
                out.println("ID: " + data.getId() +
                        (data.getLocation() == null ? " (missing location data)" : "") +
                        (data.getMetadata() == null ? " (missing metadata)" : ""));
            }
        }
    }
}