import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Openings {
    static ArrayList<String[]> openings = new ArrayList<>();

    public static void init() {
        try {
            BufferedReader in = new BufferedReader(new FileReader("Games/Openings.txt"));
            String[] opening;
            String lineIn = in.readLine();
            while (lineIn != null) {
                opening = new String[lineIn.length()/5];
                for (int i = 0; i < lineIn.length()-4; i+= 5) {
                    opening[i/5] = lineIn.substring(i, i+4);
                }
                openings.add(opening);
                lineIn = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Board getRandomStart(int movesIn) {
        String[] opening = openings.get((int) (Math.random()*openings.size()));
        Board board = new Board(PosConstants.startPos);
        MoveList moveList;
        for (int i = 0; i < movesIn; i++) {
            moveList = MoveGeneration.getMoves(board);
            board = new Board(board);
            for (long move : moveList.moves) {
                if (MoveList.toStringMove(move).equals(opening[i])) {
                    board.makeMove(move);
                    break;
                }
            }
        }
        return board;
    }
}
