package biz.pavonis.main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import biz.pavonis.main.controller.GameOfLifeController;
import biz.pavonis.main.controller.LifeTickListener;
import biz.pavonis.main.model.Universe;
import biz.pavonis.main.view.MainPanel;

public final class Main {

    private final MainPanel mainPanel = new MainPanel();
    private final Universe universe = new Universe();
    private final GameOfLifeController calculator = new GameOfLifeController(universe, mainPanel);

    public static void main(String[] args) {
        Main main = new Main();
        main.initializeLife();
    }

    private void initializeLife() {
        mainPanel.setCurrentUniverseState(universe.getGrid());
        calculator.addLifeTickListener(new LifeTickListener() {

            @Override
            public void tick(Universe universe) {
                mainPanel.drawUniverse(universe);
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame("Game of Life");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.add(mainPanel);
                f.pack();
                f.setVisible(true);
            }
        });
    }

}
