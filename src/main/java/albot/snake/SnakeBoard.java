package albot.snake;

import java.util.LinkedList;
import java.util.List;
import static albot.snake.SnakeConstants.Fields;
import static albot.snake.SnakeBeans.*;

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
        if (x < 0 || y < 0 || x >= Fields.BOARD_WIDTH || y >= Fields.BOARD_HEIGHT)
            return true; // Out of bounds
        if ((x == player.x && y == player.y) || (x == enemy.x && y == enemy.y))
            return true;
        return blocked[x][y];
    }

    public Position getPlayerPosition() { return new Position(player.x, player.y);}
    public Position getEnemyPosition() { return new Position(enemy.x, enemy.y);}
    public String getPlayerDirection() { return player.dir; }
    public String getEnemyDirection() { return enemy.dir; }

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
