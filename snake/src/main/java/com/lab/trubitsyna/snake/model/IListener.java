package com.lab.trubitsyna.snake.model;

import com.lab.trubitsyna.snake.gameException.GameException;

public interface IListener {
    void modelChanged(GameModel model);
    void modelChanged(String message);
    void listen(IModel model) throws GameException;
    void noListen(IModel model);
}
