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

public class ItemCategory {

    private final static String JSON_PATH = "/item_categories.json";
    private final static Map<String, ItemCategory> CATEGORY_MAP = new LinkedHashMap<>();

    static {
        JSONParser parser = new JSONParser();

        try (InputStreamReader jsonReader = new InputStreamReader(ItemCategory.class.getResourceAsStream(JSON_PATH), StandardCharsets.UTF_8)) {
            JSONObject root = (JSONObject) parser.parse(jsonReader);
            JSONArray dataArray = (JSONArray) root.get("data");

            for (int i = 0; i < dataArray.size(); i++) {
                Object dataObject = dataArray.get(i);
                JSONObject data = (JSONObject) dataObject;

                String name = (String) data.get("name");
                int displayIndex = (int) (long) data.get("displayOrder");

                JSONArray identifiersArray = (JSONArray) data.get("identifiers");
                String[] identifiers = new String[identifiersArray.size()];
                for (int j = 0; j < identifiersArray.size(); j++) {
                    identifiers[j] = (String) identifiersArray.get(j);
                }

                JSONArray exceptionsArray = (JSONArray) data.get("exceptions");
                String[] exceptions = {};

                if (exceptionsArray != null) {
                    exceptions = new String[exceptionsArray.size()];
                    for (int j = 0; j < exceptionsArray.size(); j++) {
                        exceptions[j] = (String) exceptionsArray.get(j);
                    }
                }

                CATEGORY_MAP.put(name, new ItemCategory(name, i, displayIndex, identifiers, exceptions));
                System.out.println("New ItemCategory: " + name);
                for(String s : identifiers) {
                    System.out.println("+" + s);
                }
                for(String s : exceptions) {
                    System.out.println("-" + s);
                }
                System.out.println();
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public static Collection<ItemCategory> values() {
        return CATEGORY_MAP.values();
    }

    public static ItemCategory get(String name) {
        return CATEGORY_MAP.get(name);
    }

    public static ItemCategory get(int index) {
        for (ItemCategory cat : values()) {
            if (cat.getDisplayIndex() == index) return cat;
        }
        return null;
    }

    public static ItemCategory getCategory(String itemName) {
        for (ItemCategory cat : ItemCategory.values()) {
            for (String identifier : cat.getIdentifiers()) {
                if (itemName.contains(identifier)) {
                    if (cat.getExceptions().length == 0) return cat;
                    boolean isException = false;
                    for (String exception : cat.getExceptions()) {
                        if (itemName.contains(exception)) {
                            isException = true;
                            break;
                        }
                    }
                    if (!isException) return cat;
                }
            }
        }
        System.err.println("No item category match for name \"" + itemName + "\"");
        return CATEGORY_MAP.get("other");
    }

    private final String name;
    private final int index, displayIndex;
    private final String[] identifiers, exceptions;

    public ItemCategory(String name, int index, int displayIndex, String[] identifiers, String[] exceptions) {
        this.name = name;
        this.index = index;
        this.displayIndex = displayIndex;
        this.identifiers = identifiers;
        this.exceptions = exceptions;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public int getDisplayIndex() {
        return displayIndex;
    }

    public String[] getIdentifiers() {
        return identifiers;
    }

    public String[] getExceptions() {
        return exceptions;
    }

    public String getDisplayName() {
        return Main.getString("cat_" + name);
    }
}
