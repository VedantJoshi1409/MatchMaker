import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Host {
    ServerSocket serverSocket;

    String oOneName;
    Socket opponentOne; //true
    ObjectOutputStream oosOne;
    ObjectInputStream oisOne;

    String oTwoName;
    Socket opponentTwo; //false
    ObjectOutputStream oosTwo;
    ObjectInputStream oisTwo;

    int oOneWins;
    int oTwoWins;
    int draws;

    String position;

    public Host(int port) {
        try {
            serverSocket = new ServerSocket(port);

            opponentOne = serverSocket.accept();
            oosOne = new ObjectOutputStream(opponentOne.getOutputStream());
            oisOne = new ObjectInputStream(opponentOne.getInputStream());

            oosOne.writeObject("uci");
            oOneName = (String) oisOne.readObject();
            oosOne.writeObject("isready");
            oosOne.writeObject("ucinewgame");
            System.out.println("Opponent one connected: " + oOneName);

            opponentTwo = serverSocket.accept();
            oosTwo = new ObjectOutputStream(opponentTwo.getOutputStream());
            oisTwo = new ObjectInputStream(opponentTwo.getInputStream());

            oosTwo.writeObject("uci");
            oTwoName = (String) oisTwo.readObject();
            oosTwo.writeObject("isready");
            oosTwo.writeObject("ucinewgame");
            System.out.println("Opponent two connected: " + oTwoName);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void startMatch(Board startBoard, Gui gui, int thinkTime, int matches) {
        Board board;
        boolean player;

        oOneWins = 0;
        oTwoWins = 0;
        draws = 0;

        for (int i = 0; i < matches; i++) {
            board = Openings.getRandomStart((int) (Math.random() * 6) + 1);
            Repetition.positionHistory.clear();
            uciNewGame(oosOne, board.boardToFen());
            uciNewGame(oosTwo, board.boardToFen());

            gui.panel.board = board;
            gui.panel.host = this;
            gui.panel.repaint();

            if (i % 2 == 0) {
                player = true;
                gui.panel.flip = false;
            } else {
                player = false;
                gui.panel.flip = true;
            }

            while (MoveGeneration.getMoves(board).count > 0 && Repetition.getRepetitionAmount(board.zobristKey, Repetition.historyFlag) < 3) {

                if (board.player == player) {
                    board = getOpponentMoveUCI(oosOne, oisOne, board, thinkTime);
                } else {
                    board = getOpponentMoveUCI(oosTwo, oisTwo, board, thinkTime);
                }
                position += " " + MoveList.toStringMove(board.pastMove);

                gui.panel.board = board;
                gui.panel.host = this;
                gui.panel.repaint();
            }

            if (Repetition.getRepetitionAmount(board.zobristKey, Repetition.historyFlag) >= 3) {
                draws++;
            }
            if (MoveGeneration.getMoves(board).count == 0) {
                if ((board.fKing & board.eAttackMask) != 0) {
                    if (board.player == player) {
                        oTwoWins++;
                    } else {
                        oOneWins++;
                    }
                } else {
                    draws++;
                }
            }
        }
    }

    private Board getOpponentMoveUCI(ObjectOutputStream oos, ObjectInputStream ois, Board board, int thinkTime) {
        Board nextBoard = null;
        try {
            oos.writeObject(position);
            oos.writeObject("go movetime " + thinkTime);
            String move = ((String) ois.readObject()).split(" ")[1];

            nextBoard = new Board(board);
            nextBoard.makeMove(MoveGeneration.getMoves(board).getMoveFromString(move));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Repetition.addToHistory(nextBoard.zobristKey, Repetition.historyFlag);
        return nextBoard;
    }

    private void uciNewGame(ObjectOutputStream oos, String startpos) {
        try {
            oos.writeObject("ucinewgame");
            position = "position " + startpos + " moves";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
