package biz.pavonis.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

import biz.pavonis.golservice.api.GameOfLifeService;
import biz.pavonis.golservice.api.Pattern;
import biz.pavonis.golservice.api.PatternOrientation;
import biz.pavonis.golservice.api.Tick;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MainPanel extends JPanel {

    private static final long serialVersionUID = 5360632279489952664L;
    private static final int GRID_SIZE = 800;
    private static final int CELL_SIZE = 1;
    private static final int PANEL_WIDTH = 1280;
    private static final int PANEL_HEIGHT = 960;
    private static final int RECT_X = (PANEL_WIDTH - GRID_SIZE) / 2;
    private static final int RECT_Y = (PANEL_HEIGHT - GRID_SIZE) / 2;

    private final List<boolean[][]> patterns = new ArrayList<>();
    private final JButton stopButton;
    private final JButton startButton;
    private boolean[][] currentUniverseState;
    private JButton clearButton;
    private JLabel lblPreview;
    private String[] patternNames;
    private JComboBox patternsCombo;

    public MainPanel(final GameOfLifeService service) {
        patterns.addAll(loadPatterns());
        setBorder(BorderFactory.createLineBorder(Color.black));
        setLayout(null);

        startButton = new JButton("Start");
        startButton.setBounds(12, 13, 97, 25);
        add(startButton);

        stopButton = new JButton("Stop");
        stopButton.setBounds(121, 13, 97, 25);
        add(stopButton);
        stopButton.setEnabled(false);

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

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	stopButton.setEnabled(true);
                startButton.setEnabled(false);
                service.start();
                repaint(RECT_X, RECT_Y, GRID_SIZE, GRID_SIZE);
            }
        });
        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	startButton.setEnabled(true);
                stopButton.setEnabled(false);
                service.pause();
                repaint(RECT_X, RECT_Y, GRID_SIZE, GRID_SIZE);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (clickIsOnGrid(e)) {
                    int gridX = (e.getX() - RECT_X) / CELL_SIZE;
                    int gridY = (e.getY() - RECT_Y) / CELL_SIZE;
                    service.stampPattern(new Pattern(1, "", getSelectedPattern()), gridX, gridY, PatternOrientation.TOP);
                    repaint(RECT_X, RECT_Y, GRID_SIZE, GRID_SIZE);
                }
            }

            private boolean clickIsOnGrid(MouseEvent e) {
                int gridSize = GRID_SIZE;
                int rectX = RECT_X;
                int rectY = RECT_Y;
                return e.getX() > rectX && e.getX() < rectX + gridSize && e.getY() > rectY && e.getY() < rectY + gridSize;
            }
        });
    }

    private List<boolean[][]> loadPatterns() {
        List<boolean[][]> patterns = new ArrayList<>();
        List<String> patternNames = new ArrayList<>();
        URL url = Thread.currentThread().getContextClassLoader().getResource("patterns");
        try {
            File patternsFolder = new File(url.toURI());
            GsonBuilder builder = new GsonBuilder();
            Type collectionType = new TypeToken<boolean[][]>() {
            }.getType();
            for (File file : patternsFolder.listFiles()) {
                try (Reader reader = new BufferedReader(new FileReader(file))) {
                    patternNames.add(file.getName().replace(".json", ""));
                    boolean[][] pattern = builder.create().fromJson(reader, collectionType);
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

    public Dimension getPreferredSize() {
        return new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
    }

    public void drawUniverse(Tick tick) {
        currentUniverseState = tick.getUniverseState();
        repaint(RECT_X, RECT_Y, GRID_SIZE, GRID_SIZE);
    }

    private boolean[][] getSelectedPattern() {
        return patterns.get(patternsCombo.getSelectedIndex());
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.drawRect(RECT_X, RECT_Y, GRID_SIZE, GRID_SIZE);
        if (currentUniverseState != null) {
            for (int y = 0; y < GRID_SIZE / CELL_SIZE; y++) {
                for (int x = 0; x < GRID_SIZE / CELL_SIZE; x++) {
                    if (currentUniverseState[y][x]) {
                        g.fillRect(RECT_X + CELL_SIZE * x, RECT_Y + CELL_SIZE * y, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }
        // preview
        boolean[][] pattern = patterns.get(patternsCombo.getSelectedIndex());
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

}
