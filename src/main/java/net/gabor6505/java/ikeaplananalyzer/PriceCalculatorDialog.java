package net.gabor6505.java.ikeaplananalyzer;

import net.gabor6505.java.ikeaplananalyzer.helper.AtomicDouble;
import net.gabor6505.java.ikeaplananalyzer.helper.GroupIterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.netbeans.swing.outline.Outline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.gabor6505.java.ikeaplananalyzer.Main.getString;

public class PriceCalculatorDialog extends JDialog implements ActionListener, KeyListener {

    private final static String JSON_PATH = "/calculator_properties.json";
    private final static List<String> TYPE_LIST = new ArrayList<>();

    static {
        JSONParser parser = new JSONParser();

        try (InputStreamReader jsonReader = new InputStreamReader(FurnitureCategory.class.getResourceAsStream(JSON_PATH))) {
            JSONObject root = (JSONObject) parser.parse(jsonReader);
            JSONArray dataArray = (JSONArray) root.get("types");

            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject data = (JSONObject) dataArray.get(i);

                String name = (String) data.get("name");
                TYPE_LIST.add(name);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private final static int WIDTH = 300;
    private final static int HEIGHT = 480;

    private final static NumberFormat formatter = NumberFormat.getNumberInstance();

    private Main mainFrame;

    private final JCheckBox calcTypeToggle;
    private final JCheckBox[] types = new JCheckBox[TYPE_LIST.size()];

    private final JTextField unitInput;
    private final JTextField appearanceInput;
    private final JLabel resultLabel;

    public PriceCalculatorDialog(Main mainFrame) {
        this.mainFrame = mainFrame;
        setTitle(getString("price_calculator_window_title"));
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        //setModalityType(DEFAULT_MODALITY_TYPE);

        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setMaximumSize(new Dimension(WIDTH, HEIGHT));
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createLineBorder(centerPanel.getBackground(), 8));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        calcTypeToggle = new JCheckBox(getString("calc_type_toggle"));
        calcTypeToggle.setSelected(true);
        calcTypeToggle.addActionListener(e -> displayResult());
        calcTypeToggle.setBorder(BorderFactory.createMatteBorder(4, 0, 4, 0, calcTypeToggle.getBackground()));
        centerPanel.add(calcTypeToggle);

        centerPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

        JPanel bottomRow = new JPanel(new GridBagLayout());
        bottomRow.setBorder(BorderFactory.createLineBorder(bottomRow.getBackground(), 8));
        mainPanel.add(bottomRow, BorderLayout.SOUTH);

        JButton applyButton = new JButton(getString("apply_button"));
        applyButton.addActionListener(this);
        bottomRow.add(applyButton, new GridBagConstraints());

        for (int i = 0; i < TYPE_LIST.size(); i++) {
            types[i] = new JCheckBox(getString("cat_" + TYPE_LIST.get(i)));
            types[i].addActionListener(e -> displayResult());
            types[i].setBorder(BorderFactory.createMatteBorder(4, 0, 4, 0, types[i].getBackground()));
            centerPanel.add(types[i]);
        }
        centerPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

        JLabel inputLabel = new JLabel(getString("unit_price_input_text"));
        inputLabel.setFont(inputLabel.getFont().deriveFont(Font.PLAIN));
        inputLabel.setBorder(BorderFactory.createMatteBorder(4, 0, 4, 0, inputLabel.getBackground()));
        centerPanel.add(inputLabel);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.setBorder(BorderFactory.createMatteBorder(4, 0, 4, 0, inputPanel.getBackground()));
        centerPanel.add(inputPanel);

        unitInput = new JTextField("0");
        unitInput.setColumns(40);
        unitInput.addKeyListener(this);
        unitInput.setMaximumSize(unitInput.getPreferredSize());
        inputPanel.add(unitInput);
        centerPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

        JLabel inputLabel2 = new JLabel(getString("appearance_input_text"));
        inputLabel2.setFont(inputLabel2.getFont().deriveFont(Font.PLAIN));
        inputLabel2.setBorder(BorderFactory.createMatteBorder(4, 0, 4, 0, inputLabel2.getBackground()));
        centerPanel.add(inputLabel2);

        JPanel inputPanel2 = new JPanel();
        inputPanel2.setLayout(new BoxLayout(inputPanel2, BoxLayout.X_AXIS));
        inputPanel2.setBorder(BorderFactory.createMatteBorder(4, 0, 4, 0, inputPanel2.getBackground()));
        centerPanel.add(inputPanel2);

        appearanceInput = new JTextField("");
        appearanceInput.setColumns(40);
        appearanceInput.setMaximumSize(appearanceInput.getPreferredSize());
        inputPanel2.add(appearanceInput);
        centerPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

        resultLabel = new JLabel(String.format(getString("calculation_result"), "0 Ft"));
        resultLabel.setBorder(BorderFactory.createMatteBorder(4, 0, 4, 0, resultLabel.getBackground()));
        resultLabel.setFont(resultLabel.getFont().deriveFont(14f));
        centerPanel.add(resultLabel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        setVisible(true);
    }

    // Apply Button click
    @Override
    public void actionPerformed(ActionEvent e) {

        boolean onlyEnabledItems = calcTypeToggle.isSelected();
        Pattern pattern = Pattern.compile("^([0-9.]+)x([0-9.]+) cm$");

        AtomicBoolean alreadyWrittenWorktop = new AtomicBoolean();

        List<Item> itemsToRemove = new ArrayList<>();

        for (int i = 0; i < types.length; i++) {
            JCheckBox checkBox = types[i];
            if (!checkBox.isSelected()) continue;

            String type = TYPE_LIST.get(i);

            ItemCategoryGroup.iterate(new GroupIterator() {
                @Override
                public void atItem(ItemGroup group, Item item, int groupIndex, int itemIndex) {
                    if (onlyEnabledItems && !item.isEnabled() && !type.equals("worktop")) return;
                    if (item.getSize() == null) return;

                    if (item.getItemCategory().getName().equals(type)) {
                        Matcher matcher = pattern.matcher(item.getSize());
                        if (matcher.matches()) {

                            if (type.equals("worktop") && alreadyWrittenWorktop.get()) {
                                item.setEnabled(false);
                                item.setQuantity(0);
                                item.setPrice(0);
                                item.setLocked(true);
                                itemsToRemove.add(item);
                                return;
                            }

                            double price = (type.equals("worktop") ? calculateWorktopWidth() : (Integer.parseInt(matcher.group(1)) / 100.0))
                                    * (type.equals("worktop") ? 0.635 : (Integer.parseInt(matcher.group(2)) / 100.0)) * getInputValue();
                            String itemName = getString("custom_" + type);

                            item.setName(itemName);
                            item.setPrice((int) Math.round(price));
                            item.setFamilyName("");
                            item.setAppearance(appearanceInput.getText());
                            item.setId(Item.INVALID_ID);

                            if (type.equals("worktop")) {
                                item.setQuantity(1);
                                item.setSize((int) (calculateWorktopWidth() * 100) + "x" + "63.5 cm");
                                alreadyWrittenWorktop.set(true);
                            }
                        }
                    }
                }
            });
        }

        System.out.println("Items to remove: " + itemsToRemove.size());

        GroupIterator removeIterator = new GroupIterator() {
            @Override
            public void afterGroup(ItemGroup group, int groupIndex) {
                group.getItems().removeAll(itemsToRemove);
            }
        };
        ItemGroup.iterate(removeIterator);
        ItemCategoryGroup.iterate(removeIterator);

        //this.setVisible(false);
        for (Outline o : mainFrame.getOutlines()) {
            if (o != null) o.repaint();
        }
        mainFrame.displayTotalPrice();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        processChange();
    }

    // Unit unitInput text update
    private void processChange() {
        String value = unitInput.getText();

        char decimalSep = DecimalFormatSymbols.getInstance().getDecimalSeparator();

        if (value.isEmpty()) value = "0";
        if (value.matches("^[^0-9]+$")) value = "0";

        value = value.replaceAll("[^0-9" + String.valueOf(decimalSep) + "]", "");

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == decimalSep) {
                if (!result.toString().contains(String.valueOf(decimalSep))) result.append(String.valueOf(decimalSep));
            } else {
                result.append(String.valueOf(value.charAt(i)));
            }
        }
        value = result.toString();

        if (!value.equals(unitInput.getText())) unitInput.setText(value);

        displayResult();
    }

    public void displayResult() {
        double result = 0;
        boolean onlyEnabledItems = calcTypeToggle.isSelected();

        for (int i = 0; i < types.length; i++) {
            JCheckBox checkBox = types[i];
            if (!checkBox.isSelected()) continue;
            result += calculateSquareMeters(TYPE_LIST.get(i), onlyEnabledItems) * getInputValue();
        }

        resultLabel.setText(String.format(getString("calculation_result"), formatter.format(result) + " Ft"));
    }

    private double calculateSquareMeters(String type, boolean onlyEnabledItems) {
        AtomicDouble result = new AtomicDouble();
        AtomicBoolean alreadyCalculatedWorktop = new AtomicBoolean();

        switch (type) {
            case "worktop":
            case "front_cover":
            case "drawer_cover":
            case "side_cover":
            case "shelf":
                Pattern pattern = Pattern.compile("^([0-9.]+)x([0-9.]+) cm$");

                ItemCategoryGroup.iterate(new GroupIterator() {
                    @Override
                    public void atItem(ItemGroup group, Item item, int groupIndex, int itemIndex) {

                        if (onlyEnabledItems && !item.isEnabled() && !type.equals("worktop")) return;
                        if (item.getSize() == null) return;

                        if (item.getItemCategory().getName().equals(type)) {
                            Matcher matcher = pattern.matcher(item.getSize());

                            if (type.equals("worktop") && alreadyCalculatedWorktop.get()) return;

                            if (matcher.matches()) {
                                result.addAndGet((type.equals("worktop") ? calculateWorktopWidth() : (Integer.parseInt(matcher.group(1)) / 100.0))
                                        * (type.equals("worktop") ? 0.635 : (Integer.parseInt(matcher.group(2)) / 100.0)) * (type.equals("worktop") ? 1 : item.getQuantity()));
                            }

                            if (type.equals("worktop")) {
                                alreadyCalculatedWorktop.set(true);
                            }
                        }
                    }
                });
                break;
        }
        return result.get();
    }

    private double calculateWorktopWidth() {
        AtomicDouble result = new AtomicDouble();
        Pattern pattern = Pattern.compile("^([0-9.]+)x([0-9.]+)x([0-9.]+) cm$");

        ItemCategoryGroup.iterate(new GroupIterator() {
            @Override
            public void atItem(ItemGroup group, Item item, int groupIndex, int itemIndex) {
                if (item.getSize() == null) return;

                if (item.getItemCategory().getName().equals("floor_casing")) {
                    Matcher matcher = pattern.matcher(item.getSize());
                    if (matcher.matches()) {
                        result.addAndGet(Integer.parseInt(matcher.group(1)));
                    }
                }
            }
        });

        return result.get() / 100.0;
    }

    private double getInputValue() {
        return Double.parseDouble(unitInput.getText().replaceAll("[.,]+", "."));
    }
}
