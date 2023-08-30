package minesweeper;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record Position(int x, int y) {
    public Set<Position> getNeighbors() {
        var result = IntStream.rangeClosed(-1, 1).boxed().flatMap(
                dx -> IntStream.rangeClosed(-1, 1).boxed().map(
                        dy -> new Position(x + dx, y + dy)
                )
        ).collect(Collectors.toSet());
        result.remove(this);
        return result;
    }
}
