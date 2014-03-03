package biz.pavonis.main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import biz.pavonis.golservice.api.GameOfLifeService;
import biz.pavonis.golservice.api.GameOfLifeServiceBuilder;
import biz.pavonis.golservice.api.LifeTickListener;
import biz.pavonis.golservice.api.Tick;

public final class Main {

	public static void main(String[] args) {
		Main main = new Main();
		main.initializeLife();
	}

	private void initializeLife() {
		GameOfLifeService service = new GameOfLifeServiceBuilder().setHeight(800).setWidth(800).setTickInterval(50).build();
		final MainPanel mainPanel = new MainPanel(service);
		service.addTickListener(new LifeTickListener() {

			@Override
			public void tick(Tick tick) {
				mainPanel.drawUniverse(tick);
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
