package biz.pavonis.main.view;

import static biz.pavonis.main.util.Utils.cloneArray;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import biz.pavonis.main.model.Universe;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class MainPanel extends JPanel {

    private static final long serialVersionUID = 5360632279489952664L;
    private static final int GRID_SIZE = 800;
    private static final int PANEL_WIDTH = 1280;
    private static final int PANEL_HEIGHT = 960;
    private static final int RECT_X = (PANEL_WIDTH - GRID_SIZE) / 2;
    private static final int RECT_Y = (PANEL_HEIGHT - GRID_SIZE) / 2;
    private static final int CELL_SIZE = Math.round(GRID_SIZE / Universe.getSize());

    private final List<Boolean[][]> patterns = new ArrayList<>();
    private final JButton resetButton;
    private final JButton stopButton;
    private final JButton startButton;
    private boolean[][] currentUniverseState = new boolean[Universe.getSize()][Universe.getSize()];
    private boolean drawGrid = true;
    private JButton clearButton;
    private JLabel lblPreview;
    private String[] patternNames;
    @SuppressWarnings("rawtypes")
    private JComboBox patternsCombo;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MainPanel() {
        patterns.addAll(loadPatterns());
        setBorder(BorderFactory.createLineBorder(Color.black));
        setLayout(null);

        startButton = new JButton("Start");
        startButton.setBounds(12, 13, 97, 25);
        add(startButton);

        stopButton = new JButton("Stop");
        stopButton.setBounds(121, 13, 97, 25);
        add(stopButton);

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                stopButton.setEnabled(true);
                startButton.setEnabled(false);
            }
        });
        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });
        stopButton.setEnabled(false);

        resetButton = new JButton("Random");
        resetButton.setBounds(230, 13, 97, 25);
        add(resetButton);

        clearButton = new JButton("Clear");
        clearButton.setBounds(339, 13, 97, 25);
        add(clearButton);

        final JToggleButton gridToggleButton = new JToggleButton("Toggle grid");
        gridToggleButton.setBounds(448, 13, 137, 25);
        add(gridToggleButton);

        patternsCombo = new JComboBox();
        patternsCombo.setModel(new DefaultComboBoxModel(patternNames));
        patternsCombo.setBounds(12, 89, 206, 25);
        add(patternsCombo);

        JLabel lblNewLabel = new JLabel("Choose pattern");
        lblNewLabel.setBounds(12, 51, 206, 25);
        add(lblNewLabel);

        lblPreview = new JLabel("Preview:");
        lblPreview.setBounds(12, 127, 206, 25);
        add(lblPreview);
        gridToggleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                drawGrid = !drawGrid;
                repaint(RECT_X, RECT_Y, GRID_SIZE, GRID_SIZE);
            }
        });
    }

    private List<Boolean[][]> loadPatterns() {
        List<Boolean[][]> patterns = new ArrayList<>();
        List<String> patternNames = new ArrayList<>();
        URL url = Thread.currentThread().getContextClassLoader().getResource("patterns");
        try {
            File patternsFolder = new File(url.toURI());
            GsonBuilder builder = new GsonBuilder();
            Type collectionType = new TypeToken<Boolean[][]>() {
            }.getType();
            for (File file : patternsFolder.listFiles()) {
                try (Reader reader = new BufferedReader(new FileReader(file))) {
                    patternNames.add(file.getName().replace(".json", ""));
                    Boolean[][] pattern = builder.create().fromJson(reader, collectionType);
                    patterns.add(pattern);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        this.patternNames = patternNames.toArray(new String[patternNames.size()]);
        return patterns;
    }

    public void addResetListener(MouseListener listener) {
        resetButton.addMouseListener(listener);
    }

    public void addStartListener(MouseListener listener) {
        startButton.addMouseListener(listener);
    }

    public void addStopListener(MouseListener listener) {
        stopButton.addMouseListener(listener);
    }

    public void addClearListener(MouseListener listener) {
        clearButton.addMouseListener(listener);
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }

    public JButton getResetButton() {
        return resetButton;
    }

    public Dimension getPreferredSize() {
        return new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
    }

    public void drawUniverse(Universe universe) {
        currentUniverseState = cloneArray(universe.getGrid());
        repaint(RECT_X, RECT_Y, GRID_SIZE, GRID_SIZE);
    }

    public Boolean[][] getSelectedPattern() {
        return patterns.get(patternsCombo.getSelectedIndex());
    }

    public static int getCellSize() {
        return CELL_SIZE;
    }

    public static int getRectX() {
        return RECT_X;
    }

    public static int getRectY() {
        return RECT_Y;
    }

    public static int getGridSize() {
        return GRID_SIZE;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.drawRect(RECT_X, RECT_Y, GRID_SIZE, GRID_SIZE);
        for (int y = 0; y < Universe.getSize(); y++) {
            for (int x = 0; x < Universe.getSize(); x++) {
                if (currentUniverseState[y][x]) {
                    g.fillRect(RECT_X + CELL_SIZE * x, RECT_Y + CELL_SIZE * y, CELL_SIZE, CELL_SIZE);
                } else if (drawGrid) {
                    g.drawRect(RECT_X + CELL_SIZE * x, RECT_Y + CELL_SIZE * y, CELL_SIZE, CELL_SIZE);
                }
            }
        }
        // preview
        Boolean[][] pattern = patterns.get(patternsCombo.getSelectedIndex());
        int factor = 3;
        int startX = patternsCombo.getX();
        int startY = patternsCombo.getY() + 60;
        int innerPatternLength = pattern[0].length;
        for (int y = 0; y < pattern.length; y++) {
            for (int x = 0; x < innerPatternLength; x++) {
                if (pattern[y][x]) {
                    g.fillRect(startX + factor * x, startY + factor * y, factor, factor);
                }
            }
        }
    }

    public void setCurrentUniverseState(boolean[][] currentUniverseState) {
        this.currentUniverseState = currentUniverseState;
    }
}
