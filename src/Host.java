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

    public Host(int port) {
        ZobristPackage zobristPackage = new ZobristPackage();

        try {
            serverSocket = new ServerSocket(port);

            opponentOne = serverSocket.accept();
            oosOne = new ObjectOutputStream(opponentOne.getOutputStream());
            oisOne = new ObjectInputStream(opponentOne.getInputStream());
            oosOne.writeObject(zobristPackage);

            oOneName = (String) oisOne.readObject();
            System.out.println("Opponent one connected: " + oOneName);

            opponentTwo = serverSocket.accept();
            oosTwo = new ObjectOutputStream(opponentTwo.getOutputStream());
            oisTwo = new ObjectInputStream(opponentTwo.getInputStream());
            oosTwo.writeObject(zobristPackage);

            oTwoName = (String) oisTwo.readObject();
            System.out.println("Opponent two connected: " + oTwoName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void startMatch(Board startBoard, Gui gui, int thinkTime, int matches) {
        try {
            oosOne.writeInt(thinkTime);
            oosTwo.writeInt(thinkTime);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Board board;
        boolean player;

        oOneWins = 0;
        oTwoWins = 0;
        draws = 0;

        for (int i = 0; i < matches; i++) {
            board = new Board(PosConstants.startPos);
            getOpponentMove(oosOne, oisOne, board); //reset TTable
            getOpponentMove(oosTwo, oisTwo, board);

            board = Openings.getRandomStart((int) (Math.random()*6)+1);
            Repetition.positionHistory.clear();

            gui.panel.board = board;
            gui.panel.host = this;
            gui.panel.repaint();

            if (i%2 == 0) {
                player = true;
                gui.panel.flip = false;
            } else {
                player = false;
                gui.panel.flip = true;
            }

            while (MoveGeneration.getMoves(board).count > 0 && Repetition.getRepetitionAmount(board.zobristKey, Repetition.historyFlag) < 3) {

                if (board.player == player) {
                    board = getOpponentMove(oosOne, oisOne, board);
                } else {
                    board = getOpponentMove(oosTwo, oisTwo, board);
                }
                Repetition.addToHistory(board.zobristKey, Repetition.historyFlag);

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

    private Board getOpponentMove(ObjectOutputStream oos, ObjectInputStream ois, Board board) {
        Package out = new Package(board, Repetition.positionHistory);
        Package in = null;
        try {
            oos.writeObject(out);
            in = (Package) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Repetition.positionHistory = in.positionHistory;
        return in.board;
    }
}
