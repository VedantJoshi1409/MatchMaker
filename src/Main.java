public class Main {
    public static void main(String[] args) {
        init();
        Board board = new Board(PosConstants.startPos);
        Host host = new Host(1409);
        Gui gui = new Gui(board, host, 1, false);
        host.startMatch(board, gui, 50, 100);
    }

    static void init() {
        MoveGeneration.initAttack();
        Zobrist.initKeys();
        Openings.init();
    }
}
