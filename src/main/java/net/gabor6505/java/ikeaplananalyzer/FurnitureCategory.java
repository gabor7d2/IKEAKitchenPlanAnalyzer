package net.gabor6505.java.ikeaplananalyzer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class FurnitureCategory {

    private final static String JSON_PATH = "/furniture_categories.json";
    private final static Map<String, FurnitureCategory> CATEGORY_MAP = new LinkedHashMap<>();

    static {
        JSONParser parser = new JSONParser();

        try (InputStreamReader jsonReader = new InputStreamReader(FurnitureCategory.class.getResourceAsStream(JSON_PATH), StandardCharsets.UTF_8)) {
            JSONObject root = (JSONObject) parser.parse(jsonReader);
            JSONArray dataArray = (JSONArray) root.get("data");

            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject data = (JSONObject) dataArray.get(i);

                String name = (String) data.get("name");

                JSONArray identifiersArray = (JSONArray) data.get("identifiers");
                String[] identifiers = new String[identifiersArray.size()];
                for (int j = 0; j < identifiersArray.size(); j++) {
                    identifiers[j] = (String) identifiersArray.get(j);
                }

                CATEGORY_MAP.put(name, new FurnitureCategory(name, i, identifiers));
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public static Collection<FurnitureCategory> values() {
        return CATEGORY_MAP.values();
    }

    public static FurnitureCategory get(String name) {
        return CATEGORY_MAP.get(name);
    }

    public static FurnitureCategory get(int index) {
        for (FurnitureCategory cat : values()) {
            if (cat.getIndex() == index) return cat;
        }
        return null;
    }

    public static FurnitureCategory getCategory(String itemName) {
        for (FurnitureCategory cat : FurnitureCategory.values()) {
            for (String identifier : cat.getIdentifiers()) {
                if (itemName.contains(identifier)) return cat;
            }
        }
        System.err.println("No furniture category match for name \"" + itemName + "\"");
        return CATEGORY_MAP.get("other");
    }

    private final String name;
    private final int index;
    private final String[] identifiers;

    public FurnitureCategory(String name, int index, String[] identifiers) {
        this.name = name;
        this.index = index;
        this.identifiers = identifiers;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public String[] getIdentifiers() {
        return identifiers;
    }

    public String getDisplayName() {
        return Main.getString("cat_" + name);
    }
}
