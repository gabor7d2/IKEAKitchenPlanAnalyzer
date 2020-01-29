package net.gabor6505.java.ikeaplananalyzer;

import net.gabor6505.java.ikeaplananalyzer.helper.GroupIterator;
import net.gabor6505.java.ikeaplananalyzer.helper.SelectionListener;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.*;

class Analyzer {

    static List<String> loadPdfLines(String filePath) {
        String[] lines = null;

        try (PDDocument doc = PDDocument.load(new File(filePath))) {
            String pdfText = new PDFTextStripper().getText(doc);
            lines = pdfText.split("\\r?\\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return removeUnnecessaryLinesAndWhitespace(lines);
    }

    private static List<String> removeUnnecessaryLinesAndWhitespace(String[] lines) {
        List<String> returnLines = new ArrayList<>();
        if (lines == null) return returnLines;

        //String pageSignature1 = lines.length > 0 ? lines[0] : null;
        String pageSignature2 = "kitchenplanner.ikea.com";

        for (String line : lines) {
            line = line.replace('\u00A0', ' ');
            line = line.trim();
            if (line.isEmpty()) continue;
            if (/*line.equals(pageSignature1) || */line.contains(pageSignature2)) continue;
            if (line.equals("Fontos")) return returnLines;
            returnLines.add(line);
        }

        return returnLines;
    }

    static void separateIntoItemGroups(List<String> lines, SelectionListener listener) {
        List<List<String>> groupLines = separateIntoGroups(lines);

        for (int i = 0; i < groupLines.size(); i++) {
            List<String> linesInGroup = groupLines.get(i);
            ItemGroup group = new ItemGroup(i);

            int currentIndex = -1;

            for (String lineInGroup : linesInGroup) {
                if (lineInGroup.length() > 7 && lineInGroup.substring(0, 8).matches("[0-9]+")) {
                    group.addNewItem(listener);
                    currentIndex++;
                }

                if (currentIndex > -1) {
                    group.getItem(currentIndex).addDetail(lineInGroup);
                }
            }
        }

    }

    private static List<List<String>> separateIntoGroups(List<String> lines) {
        List<List<String>> returnList = new ArrayList<>();

        int currentIndex = 1;

        for (String line : lines) {
            //System.out.println(line);
            if (line.matches("[0-9]+") && Integer.parseInt(line) >= currentIndex || line.matches("[A-ZÁ-ÚŰÜŐ&, ]+")) {
                currentIndex++;
                returnList.add(new ArrayList<>());
            } else if (currentIndex > 1) {
                returnList.get(currentIndex - 2).add(line);
            }
        }

        return returnList;
    }

    static void separateIntoCategoryGroups() {
        List<ItemCategory> sortedCategories = new ArrayList<>(ItemCategory.values());
        sortedCategories.sort(Comparator.comparingInt(ItemCategory::getDisplayIndex));

        for (int i = 0; i < sortedCategories.size(); i++) {
            new ItemCategoryGroup(i);
        }

        ItemGroup.iterate(new GroupIterator() {
            @Override
            public void atItem(ItemGroup group, Item currentItem, int groupIndex, int itemIndex) {
                ItemCategoryGroup.ITEM_CATEGORY_GROUPS.get(sortedCategories.indexOf(currentItem.getItemCategory())).addItem(currentItem);
            }
        });

        ItemCategoryGroup.ITEM_CATEGORY_GROUPS.removeIf(group -> group.getItems().size() == 0);

        for (int i = 0; i < ItemCategoryGroup.ITEM_CATEGORY_GROUPS.size(); i++) {
            ItemCategoryGroup.ITEM_CATEGORY_GROUPS.get(i).setId(i);
        }
    }

    /**
     * Probably a useless method, but it was good to tinker with it
     *
     * Bug #1: Because this method changes the quantities of some items and hides others in order to be
     * properly displayed in category view, it causes a price overcalculation
     *
     * Bug #2: Deselecting items won't synchronize across the 2 views so this is why it's not so useful
     */
    private static List<ItemCategoryGroup> deduplicateItemCategories(List<ItemCategoryGroup> itemCategoryGroups) {

        for (ItemCategoryGroup group : itemCategoryGroups) {
            HashMap<String, Integer> map = new HashMap<>();
            List<Item> dedupedItems = new ArrayList<>();
            for (Item item : group.getItems()) {
                if (map.containsKey(item.getId())) {
                    map.put(item.getId(), map.get(item.getId()) + item.getQuantity());
                } else {
                    map.put(item.getId(), item.getQuantity());
                }
            }

            for (Item item : group.getItems()) {
                if (!map.containsKey(item.getId())) continue;
                if (map.get(item.getId()) == item.getQuantity()) {
                    dedupedItems.add(item);
                } else if (map.get(item.getId()) > item.getQuantity()) {
                    item.setQuantity(map.get(item.getId()));
                    dedupedItems.add(item);
                }
                map.remove(item.getId());
            }
            group.setItems(dedupedItems);
        }

        return itemCategoryGroups;
    }

    static void printItems() {
        System.out.println("Length: " + ItemGroup.ITEM_GROUPS.size());
        System.out.println();

        ItemGroup.iterate(new GroupIterator() {
            @Override
            public void beforeGroup(ItemGroup group, int groupIndex) {
                System.out.println("--------------------------------------------------");
                System.out.println("Group #" + group.getId() + " (" + group.getFurnitureCategory().getDisplayName() + ") " + "length: " + group.getItems().size());
            }

            @Override
            public void atItem(ItemGroup group, Item item, int groupIndex, int itemIndex) {
                System.out.println(item.getId());
                System.out.println(item.getFamilyName());
                System.out.println(item.getName());
                System.out.println(item.getAppearance());
                System.out.println(item.getSize());
                System.out.println(item.getPrice());
                System.out.println(item.getQuantity());
                System.out.println(item.getItemCategory().getDisplayName());
                System.out.println();
            }

            @Override
            public void afterGroup(ItemGroup group, int groupIndex) {
                System.out.println();
            }
        });
    }
}
