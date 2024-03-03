import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;

public class Panel extends JPanel {
    Image bP, bR, bN, bB, bQ, bK, wP, wR, wN, wB, wQ, wK;
    Color light = new Color(240, 218, 181);
    Color dark = new Color(181, 135, 99);
    Color lightYellow = new Color(255, 255, 51, 63);

    Color blueText = new Color(153, 190, 225);
    Color faintGray = new Color(88, 88, 95);
    Color lighterGray = new Color(152, 153, 160);
    Color background = new Color(14, 14, 22);

    Font header;
    Font name;
    Font description;

    JLabel label = new JLabel();
    Graphics2D g2D;


    final int squareLength;
    final int width;
    final int height;
    final int inverseWidth;
    boolean flip;
    Board board;
    Host host;

    Panel(Board board, Host host, double scale, boolean flip) {
        squareLength = (int) (scale * 100);
        width = squareLength * 12;
        height = squareLength * 8;
        inverseWidth = squareLength * 7;
        this.flip = flip;

        this.board = board;
        this.host = host;
        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(null);
        this.add(label);
        label.setBounds(0, 0, height, height);
        label.setOpaque(false);

        bP = new ImageIcon("Images/bPawn.png").getImage();
        bR = new ImageIcon("Images/bRook.png").getImage();
        bN = new ImageIcon("Images/bKnight.png").getImage();
        bB = new ImageIcon("Images/bBishop.png").getImage();
        bQ = new ImageIcon("Images/bQueen.png").getImage();
        bK = new ImageIcon("Images/bKing.png").getImage();
        wP = new ImageIcon("Images/wPawn.png").getImage();
        wR = new ImageIcon("Images/wRook.png").getImage();
        wN = new ImageIcon("Images/wKnight.png").getImage();
        wB = new ImageIcon("Images/wBishop.png").getImage();
        wQ = new ImageIcon("Images/wQueen.png").getImage();
        wK = new ImageIcon("Images/wKing.png").getImage();

        try {
            header = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts/ZwoWebPro-Semibold W04 Regular.ttf"));
            name = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts/Hind-SemiBold.ttf"));
            description = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts/Squad-SemiBold.ttf"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        createBoard();
        populateBoard();
        highlightSquares();
        setScores();
    }

    private void createBoard() {
        boolean color = true;
        for (int i = 0; i < height; i += squareLength) {
            for (int j = 0; j < height; j += squareLength) {
                if (color) {
                    g2D.setPaint(light);
                } else {
                    g2D.setPaint(dark);
                }
                color = !color;
                g2D.fillRect(i, j, squareLength, squareLength);
            }
            color = !color;
        }
    }

    private void populateBoard() {
        String[][] stringBoard = board.bitsToBoard();
        int x, y;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!stringBoard[j][i].equals(" ")) {
                    if (flip) {
                        x = inverseWidth - i * squareLength;
                        y = inverseWidth - j * squareLength;
                    } else {
                        x = i * squareLength;
                        y = j * squareLength;
                    }
                    switch (stringBoard[j][i]) {
                        case "p" -> g2D.drawImage(bP, x, y, squareLength, squareLength, null);
                        case "r" -> g2D.drawImage(bR, x, y, squareLength, squareLength, null);
                        case "n" -> g2D.drawImage(bN, x, y, squareLength, squareLength, null);
                        case "b" -> g2D.drawImage(bB, x, y, squareLength, squareLength, null);
                        case "q" -> g2D.drawImage(bQ, x, y, squareLength, squareLength, null);
                        case "k" -> g2D.drawImage(bK, x, y, squareLength, squareLength, null);
                        case "P" -> g2D.drawImage(wP, x, y, squareLength, squareLength, null);
                        case "R" -> g2D.drawImage(wR, x, y, squareLength, squareLength, null);
                        case "N" -> g2D.drawImage(wN, x, y, squareLength, squareLength, null);
                        case "B" -> g2D.drawImage(wB, x, y, squareLength, squareLength, null);
                        case "Q" -> g2D.drawImage(wQ, x, y, squareLength, squareLength, null);
                        case "K" -> g2D.drawImage(wK, x, y, squareLength, squareLength, null);
                    }
                }
            }
        }
    }

    private void highlightSquares() {
        g2D.setPaint(lightYellow);

        if (flip) {
            g2D.fillRect(inverseWidth - BitMethods.getLS1B(board.startSquare) % 8 * squareLength, inverseWidth - BitMethods.getLS1B(board.startSquare) / 8 * squareLength, squareLength, squareLength);
            g2D.fillRect(inverseWidth - BitMethods.getLS1B(board.endSquare) % 8 * squareLength, inverseWidth - BitMethods.getLS1B(board.endSquare) / 8 * squareLength, squareLength, squareLength);
        } else {
            g2D.fillRect(BitMethods.getLS1B(board.startSquare) % 8 * squareLength, BitMethods.getLS1B(board.startSquare) / 8 * squareLength, squareLength, squareLength);
            g2D.fillRect(BitMethods.getLS1B(board.endSquare) % 8 * squareLength, BitMethods.getLS1B(board.endSquare) / 8 * squareLength, squareLength, squareLength);
        }
    }

    private void setScores() {
        g2D.setPaint(background);
        g2D.fillRect(squareLength * 8, 0, squareLength * 4, squareLength * 8);

        g2D.setPaint(blueText);
        g2D.setFont(header.deriveFont(Font.BOLD, 60));
        FontMetrics fontMetrics = g2D.getFontMetrics(header.deriveFont(Font.BOLD, 60));

        if (flip) {
            g2D.drawString("White info", (int) (squareLength * 8.2), squareLength / 2);
            g2D.drawString("Black info", (int) (squareLength * 8.2), (int) (squareLength * 4.5));
        } else {
            g2D.drawString("Black info", (int) (squareLength * 8.2), squareLength/2);
            g2D.drawString("White info", (int) (squareLength * 8.2), (int) (squareLength * 4.5));
        }

        g2D.setPaint(faintGray);
        g2D.setFont(name.deriveFont(Font.BOLD, 40));

        g2D.drawString("Name: ", (int) (squareLength * 8.3), (int) (squareLength*0.9));
        g2D.drawString("Wins: ", (int) (squareLength * 8.3), (int) (squareLength*1.3));
        g2D.drawString("Draws: ", (int) (squareLength * 8.3), (int) (squareLength*1.7));

        g2D.drawString("Name: ", (int) (squareLength * 8.3), (int) (squareLength*4.9));
        g2D.drawString("Wins: ", (int) (squareLength * 8.3), (int) (squareLength*5.3));
        g2D.drawString("Draws: ", (int) (squareLength * 8.3), (int) (squareLength*5.7));

        g2D.setPaint(lighterGray);
        g2D.setFont(description.deriveFont(Font.BOLD, 40));
        g2D.drawString(host.oTwoName, (int) (squareLength * 9.4) , (int) (squareLength*0.9));
        g2D.drawString(""+host.oTwoWins, (int) (squareLength * 9.25), (int) (squareLength*1.3));
        g2D.drawString(""+host.draws, (int) (squareLength * 9.45), (int) (squareLength*1.7));

        g2D.drawString(host.oOneName, (int) (squareLength * 9.4), (int) (squareLength*4.9));
        g2D.drawString(""+host.oOneWins, (int) (squareLength * 9.25), (int) (squareLength*5.3));
        g2D.drawString(""+host.draws, (int) (squareLength * 9.45), (int) (squareLength*5.7));
    }
}
