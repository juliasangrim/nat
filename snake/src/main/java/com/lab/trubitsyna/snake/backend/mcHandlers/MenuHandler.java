package com.lab.trubitsyna.snake.backend.mcHandlers;

import com.lab.trubitsyna.snake.MyLogger;
import com.lab.trubitsyna.snake.backend.protoClass.SnakesProto;
import javafx.util.Pair;
import lombok.Getter;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

public class MenuHandler {

    public static final int TIMEOUT_MS = 500;
    @Getter
    private final ConcurrentHashMap<Pair<Integer, InetAddress>, Pair<SnakesProto.GameMessage.AnnouncementMsg, Long>> availableGames;
    @Getter
    private boolean isMapChange = true;

    public
    MenuHandler() {
        this.availableGames = new ConcurrentHashMap<>();
    }

    public void checkAliveServers() {
        for (var game : availableGames.keySet()) {
            if (System.currentTimeMillis() - availableGames.get(game).getValue() > TIMEOUT_MS) {
                MyLogger.getLogger().info("Disconnect " + game.getKey() + " " + game.getValue());
                availableGames.remove(game);
                isMapChange = true;
            }
        }
    }

    public void changeListAvailableServer(SnakesProto.GameMessage.AnnouncementMsg server, int port, InetAddress ip) {

        if (!availableGames.containsKey(new Pair<>(port, ip))) {
            availableGames.put(new Pair<>(port, ip), new Pair<>(server, System.currentTimeMillis()));
            MyLogger.getLogger().info("Connect " + port + " " + ip);
            isMapChange = true;
        } else {
            availableGames.put(new Pair<>(port, ip), new Pair<>(server, System.currentTimeMillis()));
            isMapChange = false;
        }
    }
}
