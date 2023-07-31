package minesweeper;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameState {
    final int SIZE = 9;

    final Set<Position> mines;
    final Set<Position> marks;
    final Set<Position> explored;

    public boolean isFinished = false;
    public boolean isWinner = false;
    public boolean isFirstTurn = true;
    Random random = new Random();

    public GameState(int mineCount) {
        marks = new HashSet<>(mineCount);
        mines = new HashSet<>(mineCount);
        explored = new HashSet<>(SIZE * SIZE);
        for (int i = 0; i < mineCount; i++) {
            addMine();
        }
    }

    private void addMine() {
        var success = false;
        while (!success) {
            success = mines.add(new Position(random.nextInt(SIZE), random.nextInt(SIZE)));
        }
    }

    public String toString() {
        var line = "-|%s|\n".formatted("-".repeat(SIZE));
        var row = "|%s|\n".formatted("%s".repeat(SIZE));
        var result = new StringBuilder((SIZE + 3) * (SIZE + 4));
        result.append(" |");
        result.append(IntStream.rangeClosed(1, SIZE).mapToObj(Integer::toString).collect(Collectors.joining()));
        result.append("|\n");
        result.append(line);
        for (int y = 0; y < SIZE; y++) {
            var thisY = y;
            result.append(y + 1);
            result.append(row.formatted(IntStream.range(0, SIZE).mapToObj(x -> PositionToSymbol(x, thisY)).toArray()));
        }
        result.append(line);
        return result.toString();
    }

    private String PositionToSymbol(int x, int y) {
        var pos = new Position(x, y);
        if (isFinished && !isWinner && mines.contains(pos)) {
            return "X";
        }
        if (marks.contains(pos)) {
            return "*";
        }
        if (explored.contains(pos)) {
            var mineCount = countNeighborMines(pos);
            return mineCount == 0 ? "/" : Integer.toString(mineCount);
        }
        return ".";
    }

    private int countNeighborMines(Position pos) {
        var neighborMines = pos.getNeighbors();
        neighborMines.retainAll(mines);
        return neighborMines.size();
    }

    public String play(int x, int y, String command) {
        var pos = new Position(x - 1, y - 1);
        try {
            if (!isValid(pos)) throw new RuntimeException("Invalid position");
            switch (command) {
                case "mine" -> playMark(pos);
                case "free" -> playExplore(pos);
                default -> throw new RuntimeException("Invalid command");
            }
            checkGameEnd();
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        return null;
    }

    private void playMark(Position pos) {
        if (!marks.remove(pos)) {
            if (explored.contains(pos)) {
                throw new RuntimeException("You have already explored this location");
            }
            marks.add(pos);
        }
    }

    private void playExplore(Position pos) {
        if(isFirstTurn) {
            isFirstTurn = false;
            while (mines.remove(pos)) {
                addMine();
            }
        }
        marks.remove(pos);
        var added = explored.add(pos);
        if (mines.contains(pos)) {
            return;
        }
        if (added && countNeighborMines(pos) == 0) {
            pos.getNeighbors().stream().filter(this::isValid).forEach(this::playExplore);
        }
    }

    private void checkGameEnd() {
        if (marks.equals(mines)) {
            isWinner = true;
            isFinished = true;
        } else if (explored.stream().anyMatch(mines::contains)) {
            isWinner = false;
            isFinished = true;
        } else if (explored.size() == SIZE * SIZE - mines.size()) {
            isWinner = true;
            isFinished = true;
        }
    }

    private boolean isValid(Position pos) {
        return pos.x() >= 0 && pos.y() >= 0 && pos.x() < SIZE && pos.y() < SIZE;
    }
}
