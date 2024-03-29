package com.lab.trubitsyna.snake.controller;

import com.lab.trubitsyna.snake.MyLogger;
import com.lab.trubitsyna.snake.backend.node.INetHandler;
import com.lab.trubitsyna.snake.backend.node.MasterNetNode;
import com.lab.trubitsyna.snake.backend.node.NetNode;
import com.lab.trubitsyna.snake.backend.protoClass.SnakesProto;
import com.lab.trubitsyna.snake.gameException.GameException;
import com.lab.trubitsyna.snake.model.*;
import com.lab.trubitsyna.snake.view.StateSystem;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GameController implements IController{
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    @Getter @FXML
    private TextArea textField;
    //private final Logger logger = LoggerFactory.getLogger("APP");
    @Setter
    private GameModel model;
    @Setter
    private IListenerView gameView;
    //TODO : testing
    private INetHandler node;

    @Setter
    private StateSystem state;

    @Getter @FXML
    private Canvas board;

    @FXML
    private Button exit;

    //for client
    @Setter
    private int serverPort;
    @Setter
    private String serverAddr;
    @Setter
    private SnakesProto.GameConfig serverConfig;


    @FXML
    private void addKeyListener() {
        board.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W, UP -> {
                   // model.changeSnakeDirection(node.getPlayers().get(0), SnakesProto.Direction.UP);
                    var steerMessage = node.getSteerMessage(SnakesProto.Direction.UP);
                    threadPool.submit(()-> {
                        node.sender(null, steerMessage);
                    });

                }
                case A, LEFT-> {
                    var steerMessage = node.getSteerMessage(SnakesProto.Direction.LEFT);
                    threadPool.submit(()-> {
                        node.sender(null, steerMessage);
                    });
                }
                case D, RIGHT -> {
                    var steerMessage = node.getSteerMessage(SnakesProto.Direction.RIGHT);
                    threadPool.submit(()-> {
                        node.sender(null, steerMessage);
                    });
                }
                case S, DOWN -> {
                    var steerMessage = node.getSteerMessage(SnakesProto.Direction.DOWN);
                    threadPool.submit(()-> {
                        node.sender(null, steerMessage);
                    });
                }
            }
        });
    }

    @FXML
    private void mouseListenerExit() {
        exit.setOnMouseClicked(event -> onExitButtonPressed());
    }

    private void onExitButtonPressed() {
        state = StateSystem.MENU;
        gameView.render(state, null);
        node.end();
        threadPool.shutdown();
        gameView.noListen(model);
    }

    public void onExitWindowButtonPressed() {
        node.end();
        gameView.noListen(model);
        threadPool.shutdown();
        Stage stage = (Stage) exit.getScene().getWindow();
        stage.close();
    }

    private void loadGame() {
        gameView.render(StateSystem.LOAD_GAME, "CONNECTING TO THE SERVER");

    }



    @Override
    public void start() throws Exception {
        CustomGameConfig config = new CustomGameConfig();
        addKeyListener();
        config.initConfig();
        switch (state) {
            case JOIN_GAME -> {
                model = new GameModel();
                gameView.listen(model);
                node = new NetNode(gameView, serverConfig, serverAddr, serverPort, config.getLogin(), model);
                loadGame();
                node.openSocket();
                node.start();
            }
            case NEW_GAME -> {
                MyLogger.getLogger().info("Start game on client!");
                model = new GameModel(config);
                gameView.listen(model);
                node = new MasterNetNode(config, model);
                node.openSocket();
                node.start();
            }
        }

    }
}
