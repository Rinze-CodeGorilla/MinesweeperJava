package minesweeper;

public enum GameState {
    ONGOING,
    WON,
    LOST;

    public boolean isFinished() {
        return this == WON || this == LOST;
    }
}
