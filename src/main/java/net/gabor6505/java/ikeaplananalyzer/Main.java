package net.gabor6505.java.ikeaplananalyzer;

import net.gabor6505.java.ikeaplananalyzer.helper.AtomicDouble;
import net.gabor6505.java.ikeaplananalyzer.helper.GroupIterator;
import net.gabor6505.java.ikeaplananalyzer.helper.SelectionListener;
import net.gabor6505.java.ikeaplananalyzer.helper.UTF8Control;
import net.gabor6505.java.ikeaplananalyzer.outline.*;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import sun.swing.table.DefaultTableCellHeaderRenderer;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.ResourceBundle;

import static net.gabor6505.java.ikeaplananalyzer.Analyzer.*;

// TODO Expand price calculator, cell editing capabilities etc.
// TODO Expand all and collapse all buttons which expand and collapse all nodes of the currently visible tree
// TODO Kitchen file saving (either pdf, txt or serializable)

public class Main extends JFrame implements ActionListener, SelectionListener {

    private final static ResourceBundle messages;
    private final String[] groupColumnNames;
    private final String[] categoryColumnNames;

    private final static int MIN_WIDTH = 900;
    private final static int MIN_HEIGHT = 250;
    private final static int WIDTH = 1300;
    private final static int HEIGHT = 700;

    private final static int FIRST_COLUMN_PREF_WIDTH = 300;

    private File selectedPdfFile;

    private final JPanel mainPanel;
    private final JScrollPane[] itemTables = new JScrollPane[2];

    private final JTextPane infoText;
    private JLabel totalPrice;

    private PriceCalculatorDialog priceCalcDialog;

    private boolean categoryView = false;

    public static boolean fileIsEnglish = false;
    public static char currencySymbol = ' ';

    static {
        messages = ResourceBundle.getBundle("messages", new UTF8Control());
    }

    public static void main(String[] args) {
        new Main();
    }

    private Main() {
        setupLookAndFeel();
        UIManager.getDefaults().put("TextArea.font", UIManager.getFont("TextField.font"));
        UIManager.getDefaults().put("TextPane.font", UIManager.getFont("TextField.font"));

        setTitle(getString("main_window_title"));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);

        mainPanel = new JPanel(new BorderLayout());

        infoText = new JTextPane();
        infoText.setText(getString("welcome_message"));
        infoText.setEditable(false);
        infoText.setBackground(mainPanel.getBackground());
        infoText.setBorder(BorderFactory.createLineBorder(infoText.getBackground(), 8));
        if (System.getProperty("os.name").toLowerCase().contains("win"))
            infoText.setFont(infoText.getFont().deriveFont(13f));

        StyledDocument doc = infoText.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        mainPanel.add(infoText, BorderLayout.NORTH);
        mainPanel.add(setupBottomRow(), BorderLayout.SOUTH);

        setContentPane(mainPanel);

        groupColumnNames = new String[6];
        groupColumnNames[0] = getString("category_column_name");
        groupColumnNames[1] = getString("size_column_name");
        groupColumnNames[2] = getString("appearance_column_name");
        groupColumnNames[3] = getString("id_column_name");
        groupColumnNames[4] = getString("quantity_column_name");
        groupColumnNames[5] = getString("price_column_name");

        categoryColumnNames = new String[5];
        categoryColumnNames[0] = getString("size_column_name");
        categoryColumnNames[1] = getString("appearance_column_name");
        categoryColumnNames[2] = getString("id_column_name");
        categoryColumnNames[3] = getString("quantity_column_name");
        categoryColumnNames[4] = getString("price_column_name");
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if (info.getName().equals("Nimbus")) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored2) {
            }
        }
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            UIManager.put("Button.font", ((FontUIResource) UIManager.get("Button.font")).deriveFont(13f));
            UIManager.put("Label.font", ((FontUIResource) UIManager.get("Label.font")).deriveFont(13f).deriveFont(Font.BOLD));
        } else {
            UIManager.put("Label.font", ((FontUIResource) UIManager.get("Label.font")).deriveFont(Font.BOLD));
        }
    }

    private JPanel setupBottomRow() {
        JPanel bottomRow = new JPanel(new GridLayout(1, 3));
        bottomRow.setBorder(BorderFactory.createLineBorder(bottomRow.getBackground(), 4));

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel(new BorderLayout());
        bottomRow.add(panel1);
        bottomRow.add(panel2);
        bottomRow.add(panel3);

        JButton selectFileButton = new JButton(getString("select_file_button"));
        selectFileButton.addActionListener(this);
        selectFileButton.setName("select_file");
        panel2.add(selectFileButton);

        totalPrice = new JLabel();
        totalPrice.setBorder(BorderFactory.createMatteBorder(0, 8, 0, 8, totalPrice.getBackground()));
        totalPrice.setHorizontalAlignment(SwingConstants.RIGHT);
        panel3.add(totalPrice);

        /*JButton expandAllButton = new JButton(getString("expand_all_button"));
        expandAllButton.addActionListener(this);
        expandAllButton.setName("expand_all");
        panel1.add(expandAllButton);

        JButton collapseAllButton = new JButton(getString("collapse_all_button"));
        collapseAllButton.addActionListener(this);
        collapseAllButton.setName("collapse_all");
        panel1.add(collapseAllButton);*/

        JButton toggleViewButton = new JButton(getString("category_view_button"));
        toggleViewButton.addActionListener(this);
        toggleViewButton.setName("category_view");
        panel1.add(toggleViewButton);

        JButton priceCalculatorButton = new JButton(getString("price_calculator_button"));
        priceCalculatorButton.addActionListener(this);
        priceCalculatorButton.setName("price_calculator");
        panel1.add(priceCalculatorButton);

        return bottomRow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        switch (source.getName()) {

            case "select_file":
                if (itemTables[0] != null) mainPanel.remove(itemTables[0]);
                if (itemTables[1] != null) mainPanel.remove(itemTables[1]);
                itemTables[0] = null;
                itemTables[1] = null;

                FileDialog dialog = new FileDialog(this, getString("file_chooser_message"), FileDialog.LOAD);
                dialog.setDirectory(System.getProperty("user.home") + "/Downloads/");
                dialog.setFile("*.pdf");
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);

                if (dialog.getFile() == null) return;
                String selectedFilePath = dialog.getDirectory() + File.separator + dialog.getFile();
                System.out.println(selectedFilePath);

                if (new File(selectedFilePath).exists()) {
                    selectedPdfFile = new File(selectedFilePath);
                    infoText.setText(String.format(getString("file_selected_message"), dialog.getFile()));
                    constructItems();
                    displayItems();
                }
                break;

            case "price_calculator":
                if (displayNoFileSelectedError()) break;

                if (priceCalcDialog == null) {
                    priceCalcDialog = new PriceCalculatorDialog(this);
                } else {
                    priceCalcDialog.setVisible(true);
                    priceCalcDialog.displayResult();
                }
                break;

            case "category_view":
                categoryView = true;
                displayItems();

                source.setText(getString("group_view_button"));
                source.setName("group_view");
                break;

            case "group_view":
                categoryView = false;
                displayItems();

                source.setText(getString("category_view_button"));
                source.setName("category_view");
                break;

            /*case "expand_all":
            case "collapse_all":
                JScrollPane itemTable = itemTables[categoryView ? 1 : 0];
                if (itemTable == null) break;
                Outline outline = (Outline) itemTable.getViewport().getComponent(0);
                if (outline == null) break;
                expandAll(new TreePath(outline.getOutlineModel().getRoot()), source.getName().equals("expand_all"), outline);
                break;*/
        }
    }

    private void constructItems() {
        ItemGroup.ITEM_GROUPS.clear();
        ItemCategoryGroup.ITEM_CATEGORY_GROUPS.clear();

        List<String> pdfLines = loadPdfLines(selectedPdfFile.getPath());
        setTitles(pdfLines);

        fileIsEnglish = false;
        for (String line : pdfLines) {
            if (line.contains("$")) {
                fileIsEnglish = true;
                currencySymbol = '$';
                break;
            }
            if (line.contains("£")) {
                fileIsEnglish = true;
                currencySymbol = '£';
                break;
            }
        }

        separateIntoItemGroups(pdfLines, this);
        separateIntoCategoryGroups();
        printItems();
    }

    private void setTitles(List<String> lines) {
        int nameIndex = lines.indexOf("Projekt neve");
        if (nameIndex == -1) nameIndex = lines.indexOf("Project name");
        int idIndex = lines.indexOf("Projekt száma");
        if (idIndex == -1) idIndex = lines.indexOf("Project number");

        String projectName = lines.get(nameIndex + 1);
        String projectId = lines.get(idIndex + 1);
        if (projectName.contains("Mentsd el") || projectName.contains("Please save")) {
            projectName = getString("file_selected_unsaved_design");
            projectId = null;
        }

        setTitle(String.format("%s - %s", getString("main_window_title"), projectName));
        if (projectId == null) {
            infoText.setText(String.format("%s - %s", infoText.getText(), projectName));
        } else infoText.setText(String.format("%s - %s - %s", infoText.getText(), projectName, projectId));
    }

    private void displayItems() {
        if (selectedPdfFile == null) return;

        if (itemTables[0] != null) mainPanel.remove(itemTables[0]);
        if (itemTables[1] != null) mainPanel.remove(itemTables[1]);

        if (!categoryView && itemTables[0] != null) {
            mainPanel.add(itemTables[0], BorderLayout.CENTER);
            return;
        }
        if (categoryView && itemTables[1] != null) {
            mainPanel.add(itemTables[1], BorderLayout.CENTER);
            return;
        }

        Outline outline = new CustomOutline();
        ItemTreeModel treeModel = new ItemTreeModel(categoryView ? ItemCategoryGroup.ITEM_CATEGORY_GROUPS : ItemGroup.ITEM_GROUPS);
        ItemRowModel rowModel = new ItemRowModel(categoryView ? categoryColumnNames : groupColumnNames, categoryView);
        outline.setModel(DefaultOutlineModel.createOutlineModel(treeModel, rowModel, false,
                categoryView ? getString("categories_column_name") : getString("furniture_group_column_name")));

        outline.setRootVisible(false);
        outline.setRenderDataProvider(new ItemRenderer(outline));

        outline.getColumnModel().getColumn(0).setPreferredWidth(FIRST_COLUMN_PREF_WIDTH);
        outline.setGridColor(Color.WHITE);

        TableColumnModel columnModel = outline.getColumnModel();
        TableCellRenderer cellRenderer = new ItemTableCellRenderer(SwingConstants.CENTER);

        for (int i = 1; i < columnModel.getColumnCount() - 1; i++) {
            columnModel.getColumn(i).setCellRenderer(cellRenderer);
        }
        columnModel.getColumn(0).setCellRenderer(new ItemTableCellRenderer(SwingConstants.LEFT));
        columnModel.getColumn(columnModel.getColumnCount() - 1).setCellRenderer(new ItemTableCellRenderer(SwingConstants.RIGHT));

        outline.getTableHeader().setDefaultRenderer(new DefaultTableCellHeaderRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(CENTER);
                return l;
            }
        });

        int idx = categoryView ? 1 : 0;

        itemTables[idx] = new JScrollPane(outline);
        itemTables[idx].setViewportBorder(BorderFactory.createMatteBorder(0, 4, 0, 8, Color.WHITE));
        itemTables[idx].getViewport().setBackground(Color.WHITE);

        mainPanel.add(itemTables[idx], BorderLayout.CENTER);
        displayTotalPrice();
    }

    void displayTotalPrice() {
        AtomicDouble total = new AtomicDouble(0);

        ItemGroup.iterate(new GroupIterator() {
            @Override
            public void atItem(ItemGroup group, Item item, int groupIndex, int itemIndex) {
                if (!item.isEnabled()) return;
                if (item.getPrice() > 0 && item.getQuantity() > 0)
                    total.addAndGet(item.getPrice() * item.getQuantity());
            }
        });

        totalPrice.setText(String.format(getString("total_price_text"), formatPrice(total.get())));
    }

    public static String formatPrice(double value) {
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(2);
        return (fileIsEnglish ? currencySymbol : "") + formatter.format(value) + (fileIsEnglish ? "" : " Ft");
    }

    public static String getString(String key) {
        //return new String(messages.getString(key).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return messages.getString(key);
    }

    @Override
    public void SelectionChanged(Item item, boolean newValue) {
        displayTotalPrice();
        if (priceCalcDialog != null) priceCalcDialog.displayResult();
    }

    private boolean displayNoFileSelectedError() {
        if (selectedPdfFile == null) {
            JOptionPane.showMessageDialog(this, getString("no_file_selected_error_message"),
                    getString("no_file_selected_error_title"), JOptionPane.ERROR_MESSAGE);
            return true;
        } else return false;
    }

    public Outline[] getOutlines() {
        Outline[] outlines = new Outline[itemTables.length];
        for (int i = 0; i < itemTables.length; i++) {
            JScrollPane scrollPane = itemTables[i];
            if (scrollPane != null) outlines[i] = (Outline) scrollPane.getViewport().getComponent(0);
            else outlines[i] = null;
        }
        return outlines;
    }

    /*private void expandAll(TreePath path, boolean expands, Outline treeTable) {
        System.out.println("Starting expansion...");
        TreeNode node = (TreeNode) path.getLastPathComponent();

        if (node.getChildCount() >= 0) {
            Enumeration enumeration = node.children();
            while (enumeration.hasMoreElements()) {
                TreeNode ns = (TreeNode) enumeration.nextElement();
                TreePath ps = path.pathByAddingChild(ns);

                expandAll(ps, expands, treeTable);
            }
        }

        if (expands) {
            treeTable.getOutlineModel().getTreePathSupport().expandPath(path);
        } else {
            treeTable.getOutlineModel().getTreePathSupport().collapsePath(path);
        }
    }*/
}
