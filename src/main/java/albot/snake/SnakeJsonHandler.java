package albot.snake;

import albot.Constants;
import albot.JsonHandler;
import com.google.gson.*;
import static albot.snake.SnakeConstants.JProtocol;
import static albot.snake.SnakeBeans.*;

class SnakeJsonHandler {
    private static Gson gson = new Gson();

    private static JsonObject serializeBoard(SnakeBoard board) {
        JsonObject jBoard = new JsonObject();
        jBoard.add(JProtocol.player, gson.toJsonTree(board.player));
        jBoard.add(JProtocol.enemy, gson.toJsonTree(board.enemy));
        jBoard.add(JProtocol.blocked, gson.toJsonTree(board.getBlockedList(false)).getAsJsonArray());

        return jBoard;
    }

    static String createCommandPossibleMoves(SnakeBoard board) {
        JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(Constants.Fields.action, Constants.Actions.getPossMoves);
        jsonCommand.addProperty(JProtocol.player, board.getPlayerDirection());
        jsonCommand.addProperty(JProtocol.enemy, board.getEnemyDirection());
        return gson.toJson(jsonCommand);
        //return jsonCommand.toString();
    }


    static String createCommandSimulateBoth(SnakeBoard board, MovesToSimulate simMoves) {
        return createCommandSimulate(board, simMoves, Constants.Actions.simMove);
    }
    static String createCommandSimulatePlayer(SnakeBoard board, MovesToSimulate simMoves) {
        return createCommandSimulate(board, simMoves, JProtocol.simPlayerMove);
    }
    static String createCommandSimulateEnemy(SnakeBoard board, MovesToSimulate simMoves) {
        return createCommandSimulate(board, simMoves, JProtocol.simEnemyMove);
    }

    // Gson seems to ignore null values by default
    private static String createCommandSimulate(SnakeBoard board, MovesToSimulate simMoves, String action) {
        JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(Constants.Fields.action, action);
        if(simMoves.playerMove != null)
            jsonCommand.addProperty(JProtocol.playerMove, simMoves.playerMove);
        if(simMoves.enemyMove != null)
            jsonCommand.addProperty(JProtocol.enemyMove, simMoves.enemyMove);
        jsonCommand.add(JProtocol.board, serializeBoard(board));
        //System.out.println("Create command simulate:  \n" + jsonCommand.toString() + "\n");

        return gson.toJson(jsonCommand);
    }

    static String createCommandEvaluate(SnakeBoard board) {
        JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(Constants.Fields.action, Constants.Actions.evalBoard);
        jsonCommand.add(JProtocol.board, serializeBoard(board));

        return gson.toJson(jsonCommand);
    }

    static BoardBean parseResponseState(JsonObject jState) {
        //System.out.println("Response state: \n" + response + "\n");
        //return gson.fromJson(response, BoardBean.class);
        return JsonHandler.tryParse(jState, BoardBean.class);
    }

    static PossibleMoves parseResponsePossibleMoves(String response) {
        //return gson.fromJson(response, PossibleMoves.class);
        return JsonHandler.tryParse(response, PossibleMoves.class);
    }

    static SnakeBoard parseResponseSimulate(String response) {
        //BoardBean boardBean = gson.fromJson(response, BoardBean.class);
        BoardBean boardBean = JsonHandler.tryParse(response, BoardBean.class);
        return new SnakeBoard(boardBean);
    }

    static Constants.BoardState parseResponseEvaluate(String response) {
        //JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        JsonObject jsonObject = JsonHandler.tryParse(response, JsonObject.class);
        //String boardState = gson.fromJson(jsonObject.get(Constants.Fields.boardState), String.class);
        //String boardState = JsonHandler.tryParse(jsonObject.get(Constants.Fields.boardState), String.class);
        //System.out.println(boardState);
        return JsonHandler.fetchBoardState(jsonObject);
    }
}
