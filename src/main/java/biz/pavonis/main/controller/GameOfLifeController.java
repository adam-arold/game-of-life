package biz.pavonis.main.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import biz.pavonis.main.model.Universe;
import biz.pavonis.main.view.MainPanel;

public class GameOfLifeController {

    private static final long TICK_INTERVAL = 300;
    private TimerTask timerTask = createTimerTask();

    private TimerTask createTimerTask() {
        return new TimerTask() {

            @Override
            public void run() {
                recalculateUniverseState();
                fireLifeTickListeners();
            }
        };
    }

    private final List<LifeTickListener> lifeTickListeners = new ArrayList<>();
    private final Timer timer = new Timer();
    private final Universe universe;

    public GameOfLifeController(final Universe universe, final MainPanel mainPanel) {
        this.universe = universe;
        mainPanel.addStartListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startTimer();
            }
        });
        mainPanel.addStopListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                stopTimer();
            }
        });
        mainPanel.addResetListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                stopTimer();
                universe.generateRandomState();
                mainPanel.getStartButton().setEnabled(true);
                mainPanel.getStopButton().setEnabled(false);
                mainPanel.drawUniverse(universe);
            }
        });
        mainPanel.addClearListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                universe.setNewState(new boolean[Universe.getSize()][Universe.getSize()]);
                mainPanel.drawUniverse(universe);
            }
        });
        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mainPanel.getStartButton().isEnabled() && clickIsOnGrid(e)) {
                    int gridX = (e.getX() - MainPanel.getRectX()) / MainPanel.getCellSize();
                    int gridY = (e.getY() - MainPanel.getRectY()) / MainPanel.getCellSize();
                    boolean newState = !universe.getCellState(gridX, gridY);
                    universe.setCellState(gridX, gridY, newState);
                    mainPanel.drawUniverse(universe);
                }
            }

            private boolean clickIsOnGrid(MouseEvent e) {
                int gridSize = MainPanel.getGridSize();
                int rectX = MainPanel.getRectX();
                int rectY = MainPanel.getRectY();
                return e.getX() > rectX && e.getX() < rectX + gridSize && e.getY() > rectY && e.getY() < rectY + gridSize;
            }
        });
    }

    public void startTimer() {
        stopTimer();
        timer.scheduleAtFixedRate(timerTask, 0, TICK_INTERVAL);
    }

    public void stopTimer() {
        timerTask.cancel();
        timerTask = createTimerTask();
    }

    private void recalculateUniverseState() {
        int size = Universe.getSize();
        boolean[][] oldState = universe.getGrid();
        boolean[][] newState = new boolean[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int liveNeighbors = universe.countLiveNeighbors(x, y);
                boolean isLiving = oldState[y][x];
                if (isLiving && liveNeighbors < 2) {
                    newState[y][x] = false;
                }
                if (isLiving && (liveNeighbors == 2 || liveNeighbors == 3)) {
                    newState[y][x] = true;
                }
                if (isLiving && liveNeighbors > 3) {
                    newState[y][x] = false;
                }
                if (!isLiving && liveNeighbors == 3) {
                    newState[y][x] = true;
                }
            }
        }
        universe.setNewState(newState);
    }

    private void fireLifeTickListeners() {
        for (LifeTickListener listener : lifeTickListeners) {
            listener.tick(universe);
        }
    }

    public boolean addLifeTickListener(LifeTickListener listener) {
        return lifeTickListeners.add(listener);
    }
}
