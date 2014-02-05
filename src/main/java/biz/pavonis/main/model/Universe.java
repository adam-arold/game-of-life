package biz.pavonis.main.model;

import java.util.Random;

public class Universe {

    private static final int SIZE = 200;
    private boolean[][] grid = new boolean[SIZE][SIZE];

    public boolean[][] getGrid() {
        return grid;
    }

    public static int getSize() {
        return SIZE;
    }

    public void setNewState(boolean[][] newState) {
        this.grid = newState;
    }

    public boolean getCellState(int x, int y) {
        return grid[y][x];
    }

    public void setCellState(int x, int y, boolean state) {
        grid[y][x] = state;
    }

    public void generateRandomState() {
        Random random = new Random();
        boolean[][] state = new boolean[SIZE][SIZE];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                state[y][x] = random.nextBoolean();
            }
        }
        grid = state;
    }

    public int countLiveNeighbors(int x, int y) {
        int result = 0;
        for (CellNeighbor neighbor : CellNeighbor.values()) {
            try {
                boolean isLiving = grid[y + neighbor.getYOffset()][x + neighbor.getXOffset()];
                if (isLiving) {
                    result++;
                }
            } catch (IndexOutOfBoundsException e) {
                // TODO: logging here
            }
        }
        return result;
    }
}
