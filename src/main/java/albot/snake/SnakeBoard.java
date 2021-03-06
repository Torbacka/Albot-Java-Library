package albot.snake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import static albot.snake.SnakeConstants.Fields;
import static albot.snake.SnakeBeans.*;
import static albot.Constants.BoardState;

public class SnakeBoard {
    public SnakeBeans.Placement player, enemy;
    private boolean[][] blocked = new boolean[Fields.BOARD_WIDTH][Fields.BOARD_HEIGHT];

    public SnakeBoard(SnakeBoard board, BoardBean boardBean) {
        extractOldBoardInfo(board);
        extractResponseInfo(boardBean);
    }
    public SnakeBoard(BoardBean boardBean) {
        extractResponseInfo(boardBean);
    }

    /**
     * Constructor for SnakeBoard which makes a deep copy of the passed board.
     * @param board Board to be deep copied.
     */
    public SnakeBoard(SnakeBoard board) {
        this.player = new Placement(board.player.x, board.player.y, board.player.dir);
        this.enemy = new Placement(board.enemy.x, board.enemy.y, board.enemy.dir);
        //this.blocked = board.blocked.clone();
        this.blocked = deepCopy(board.blocked);
    }

    private boolean[][] deepCopy(boolean[][] original) {
        if (original == null)
            return null;

        final boolean[][] result = new boolean[original.length][];
        for (int i = 0; i < original.length; i++)
            result[i] = Arrays.copyOf(original[i], original[i].length);

        return result;
    }

    private void extractResponseInfo(BoardBean response) {
        player = response.player;
        enemy = response.enemy;
        /* Does not work well with Evaluate
        if(coordsInBounds(player.x, player.y))
            blocked[player.x][player.y] = true;
        if(coordsInBounds(enemy.x, enemy.y))
            blocked[enemy.x][enemy.y] = true;
        */

        for (Position pos : response.blocked) {
            if(coordsInBounds(pos.x, pos.y))
                blocked[pos.x][pos.y] = true;
        }
    }

    private void extractOldBoardInfo(SnakeBoard board) {
        if (board != null)
            for (int x = 0; x < Fields.BOARD_WIDTH; x++)
                for (int y = 0; y < Fields.BOARD_HEIGHT; y++)
                    blocked[x][y] = board.blocked[x][y];
    }

    /**
     * Checks if cell is occupied.
     * @param x x-coordinate for cell
     * @param y y-coordinate for cell
     * @return True if position is occupied, false if square is empty.
     */
    public boolean cellBlocked(int x, int y) {
        if (! coordsInBounds(x, y)) // Out of bounds
            return true;
        if ((x == player.x && y == player.y) || (x == enemy.x && y == enemy.y))
            return true;
        return blocked[x][y];
    }

    public Position getPlayerPosition() { return new Position(player.x, player.y);}
    public Position getEnemyPosition() { return new Position(enemy.x, enemy.y);}
    public String getPlayerDirection() { return player.dir; }
    public String getEnemyDirection() { return enemy.dir; }

    // Not counting heads as blocked cells!
    private Boolean killed(int x, int y) {
        if(! coordsInBounds(x, y))
            return true;

        return blocked[x][y];
    }

    /**
     * Simulate a time step in the game, where both players have moved.
     * @param playerMove player move to simulate
     * @param enemyMove enemy move to simulate
     * @return the board the moves has been applied
     */
    public SnakeBoard simulateMoves(String playerMove, String enemyMove) {
        SnakeBoard simBoard = new SnakeBoard(this); // Deep copy of current board
        simBoard.playMove(playerMove, true);
        simBoard.playMove(enemyMove, false);
        return simBoard;
    }

    /**
     * Simulate a move where only the player moves.
     * @param move move to simulate
     * @return the board where the move has been applied
     */
    public SnakeBoard simulatePlayerMove(String move) {
        SnakeBoard simBoard = new SnakeBoard(this); // Deep copy of current board
        simBoard.playMove(move, true);
        return simBoard;
    }
    /**
     * Simulate a move where only the enemy moves.
     * @param move move to simulate
     * @return the board where the move has been applied
     */
    public SnakeBoard simulateEnemyMove(String move) {
        SnakeBoard simBoard = new SnakeBoard(this); // Deep copy of current board
        simBoard.playMove(move, false);
        return simBoard;
    }

    // Applies a given move to the board
    private void playMove(String move, boolean player) {
        Placement target = player ? this.player : this.enemy;
        blocked[target.x][target.y] = true;

        switch (move) {
            case "right":
                target.x++; break;
            case "left":
                target.x--; break;
            case "down":
                target.y++; break;
            case "up":
                target.y--; break;
        }
    }

    /**
     * Returns the possible moves for the player, based off direction only.
     * @return List of Strings representing the legal moves
     */
    public List<String> getPossiblePlayerMoves() {
        return dirToPossibleMoves(player.dir);
    }
    /**
     * Returns the possible moves for the enemy, based off direction only.
     * @return List of Strings representing the legal moves
     */
    public List<String> getPossibleEnemyMoves() {
        return dirToPossibleMoves(enemy.dir);
    }
    /**
     * Returns the possible moves for both the player and the enemy, based off directions only.
     * @return class containing a list of possible moves for the player and the enemy
     */
    public PossibleMoves getPossibleMoves() {
        List<String> player = dirToPossibleMoves(this.player.dir);
        List<String> enemy = dirToPossibleMoves(this.enemy.dir);
        return new PossibleMoves(player, enemy);
    }

    private List<String> dirToPossibleMoves(String dir) {
        ArrayList<String> possMoves = new ArrayList<>(3);
        switch (dir) {
            case "right":
                possMoves.add("right");
                possMoves.add("up");
                possMoves.add("down");
                break;
            case "left":
                possMoves.add("left");
                possMoves.add("up");
                possMoves.add("down");
                break;
            case "down":
                possMoves.add("left");
                possMoves.add("right");
                possMoves.add("down");
                break;
            case "up":
                possMoves.add("left");
                possMoves.add("up");
                possMoves.add("right");
                break;
        }
        return possMoves;
    }

    /**
     * Returns the state of the board
     * @return Enum BoardState expressing information about board state. Possible values: ongoing, draw, playerwon, enemywon
     */
    public BoardState evaluateBoardState () {
        if(player.x == enemy.x && player.y == enemy.y)
            return BoardState.draw;

        boolean playerDead = killed(player.x, player.y);
        boolean enemyDead = killed(enemy.x, enemy.y);

        if(playerDead && enemyDead)
            return BoardState.draw;
        if(playerDead)
            return BoardState.enemyWon;
        if(enemyDead)
            return BoardState.playerWon;

        return BoardState.ongoing;
    }

    /**
     * The list of blocked cells.
     * @return Linked list with blocked cells
     */
    public List<Position> getBlockedList() {
        return getBlockedList(true);
    }
    /**
     * The list of blocked cells.
     * @param includePlayerPositions if true, cells containing the head of the snakes are regarded as blocked.
     * @return Linked list with blocked cells
     */
    public List<Position> getBlockedList(boolean includePlayerPositions) {
        List<Position> b = new LinkedList<Position>();
        for (int xb = 0; xb < Fields.BOARD_WIDTH; xb++)
            for (int yb = 0; yb < Fields.BOARD_HEIGHT; yb++)
                if (blocked[xb][yb])
                    b.add(new Position(xb, yb));
        if(includePlayerPositions) {
            b.add(getPlayerPosition());
            b.add(getEnemyPosition());
        }
        return b;
    }

    private boolean coordsInBounds(int x, int y) {
        if (x < 0 || y < 0 || x >= Fields.BOARD_WIDTH || y >= Fields.BOARD_HEIGHT)
            return false;
        return true;
    }

    // Debug, not serialize
    @Override
    public String toString() {
        String s = "";
        for (int y = 0; y < Fields.BOARD_HEIGHT; y++) {
            for (int x = 0; x < Fields.BOARD_WIDTH; x++)
                s += squareInfo(x, y) + " ";
            s += "\n";
        }
        return s;
    }

    /**
     * Prints the board to the console.
     * @param boardTitle Optional title for the printed board.
     */
    public void printBoard(String boardTitle) {
        System.out.println("* * * * * * " + boardTitle + "* * * * * *");
        printBoardInfo();
        System.out.println("* * * * * * * * * * * *");
    }

    /**
     * Prints the board to the console.
     */
    public void printBoard() {
        System.out.println("* * * * * * * * * * * *");
        printBoardInfo();
        System.out.println("* * * * * * * * * * * *");
    }
    private void printBoardInfo() {
        System.out.print(toString());
    }
    private String squareInfo(int x, int y) {
        if (x == player.x && y == player.y)
            return "P";
        if (x == enemy.x && y == enemy.y)
            return "E";
        return blocked[x][y] ? "X" : "0";
    }
}
