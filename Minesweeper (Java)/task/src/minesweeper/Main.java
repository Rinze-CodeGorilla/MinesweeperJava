package minesweeper;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // write your code here
        System.out.println("How many mines do you want on the field?");
        var s = new Scanner(System.in);
        var mines = s.nextInt();
        var game = new GameState(mines);
        System.out.println(game);
        while(!game.isFinished) {
            String error;
            do {
                System.out.println("Set/unset mines marks or claim a cell as free (x y command):");
                error = game.play(s.nextInt(), s.nextInt(), s.next());
                if (error != null) System.out.println(error);
            } while (error != null);
            System.out.println(game);
        }
        if(game.isWinner) {
            System.out.println("Congratulations! You found all the mines!");
        } else {
            System.out.println("You stepped on a mine and failed!");
        }
    }
}
