package com.akimi808.xo.client;

import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.akimi808.xo.common.*;

/**
 * @author Andrey Larionov
 */
public class XoGame implements UpdateListener {
    private Game game;
    private Lock waitLock = new ReentrantLock();
    private Condition waitCond = waitLock.newCondition();
    private XoClient2 client;

    public static void main(String[] args) {
        new XoGame().run();
    }

    private void run() {
        // Creating client
        String name = "";
        client = new XoClient2(this);
        try {
            // Establishing connection and login
            // in try catch block, cause fail is a fatal error
            client.connect();
        } catch (ConnectionException e) {

        } catch (LoginException e) {

        }

        waitForSession();

        client.login(name);

        waitForGameStarted();

        while (!game.isFinished()) {

            waitForTurn();

            List<Integer> availableMoves = game.getAvailableMoves();
            if (!availableMoves.isEmpty()) {
                Integer selectedMove = availableMoves.get(new Random().nextInt(availableMoves.size()));
                client.makeMove(game.getGameId(), selectedMove);
            }
        }

        waitForGameFinished();

        client.disconnect();
    }

    private void waitForSession() {
        waitLock.lock();
        try {
            while (!client.isSessionInitiated())
                waitCond.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            waitLock.unlock();
        }
    }

    private void waitForTurn() {
        waitLock.lock();
        try {
            while (!game.isMyTurn())
                waitCond.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            waitLock.unlock();
        }
    }

    private void waitForGameFinished() {
        waitLock.lock();
        try {
            while (!game.isFinished())
                waitCond.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            waitLock.unlock();
        }
    }

    private void waitForGameStarted() {
        waitLock.lock();
        try {
            while (!game.isStarted())
                waitCond.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            waitLock.unlock();
        }
    }

    @Override
    public void onUpdate(Update update) {
        waitLock.lock();
        try {
            if (update instanceof UpdateGameStarted) {
                UpdateGameStarted gameStarted = (UpdateGameStarted) update;
                game = new Game(gameStarted.getGameId(), gameStarted.getMark(), gameStarted.getOpponentName());
            } else if (update instanceof UpdateOpponentMadeMove) {
                game.placeMark(((UpdateOpponentMadeMove) update).getMark(), ((UpdateOpponentMadeMove) update).getPlacedPosition());
            } else if (update instanceof UpdateGameFinished) {
                game.setFinished(true);
            } else if (update instanceof UpdateGameTurnChanged) {
                game.setTurnMark(((UpdateGameTurnChanged)update).getTurnMark());
            }
            waitCond.signalAll();
        } finally {
            waitLock.unlock();
        }
    }
}
