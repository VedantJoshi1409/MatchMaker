import javax.swing.*;

public class Gui extends JFrame {
    Panel panel;

    Gui(Board board, Host host, double scale, boolean flip) {
        panel = new Panel(board, host, scale, flip);
        this.add(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
