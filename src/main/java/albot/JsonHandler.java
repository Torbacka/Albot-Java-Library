package albot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

import static albot.Constants.BoardState;

public class JsonHandler {
    private static Gson gson = new Gson();

    public static <T> T tryParse(String response, Type type) {
        try {
            return gson.fromJson(response, type);
        } catch (Exception e) {
            handleCatch(response, e);
        }
        return null; // Unreachable
    }

    public static <T> T tryParse(JsonElement response, Type type) {
        try {
            return gson.fromJson(response, type);
        } catch (Exception e) {
            handleCatch(response.toString(), e);
        }
        return null; // Unreachable
    }

    private static void handleCatch(String response, Exception e) {
        System.out.println("Could not parse response: \n" + response + "\n" + e);
        AlbotConnection.terminate();
    }

    public static BoardState extractBoardState(JsonObject stateResponse) {
        String boardState = JsonHandler.tryParse(stateResponse.get(Constants.Fields.boardState), String.class);
        stateResponse.remove(Constants.Fields.boardState);
        return BoardState.valueOf(boardState);
    }

    public static BoardState fetchBoardState(JsonObject stateResponse) {
        String boardState = JsonHandler.tryParse(stateResponse.get(Constants.Fields.boardState), String.class);
        return BoardState.valueOf(boardState);
    }

}
