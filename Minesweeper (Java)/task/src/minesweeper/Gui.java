package minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Gui extends JFrame {
    final Game game;
    public static void main(String[] args) {
        new Gui();
    }

    public Gui() {
        game = new Game(5);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(100 * game.SIZE, 100 * game.SIZE + 15);
        setLayout(new GridLayout(game.SIZE, game.SIZE));
        var clickHandler = new clicky();
        for (int y = 1; y <= game.SIZE; y++)
            for (int x = 1; x <= game.SIZE; x++) {
                var b = new Field(x, y);
                b.visualize();
                b.addMouseListener(clickHandler);
                add(b);
            }
        setVisible(true);
    }

    class Field extends JButton {
        public int x;
        public int y;

        public Field(int x, int y) {
            super();
            this.x = x;
            this.y = y;
            setFocusPainted(false);
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        }

        public void visualize() {
            var symbol = game.PositionToSymbol(x - 1, y - 1);
            setBorderPainted(false);
            setText(switch (symbol) {
                case "X" -> "💥";
                case "*" -> "💣";
                case "/" -> "";
                case "." -> {
                    setBorderPainted(true);
                    yield "🌊";
                }
                default -> symbol;
            });

        }
    }

    class clicky extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Field field = (Field) e.getComponent();
            if (game.state.isFinished()) {
                if (!field.getText().equals("💥")) {
                    new Gui();
                }
                Gui.this.dispose();
                return;
            }
            var command = SwingUtilities.isRightMouseButton(e) ? "mine" : "free";
            game.play(field.x, field.y, command);
            Gui.this.setIgnoreRepaint(true);
            for (var c : field.getParent().getComponents()) {
                if (c instanceof Field f) f.visualize();
            }
            if (game.state.isFinished()) {
                ((JFrame) field.getParent().getParent().getParent().getParent()).setTitle("Finished! You " + (game.state == GameState.WON ? "won!" : "lost :("));
            }
            Gui.this.setIgnoreRepaint(false);
            Gui.this.repaint();

        }
    }
}
