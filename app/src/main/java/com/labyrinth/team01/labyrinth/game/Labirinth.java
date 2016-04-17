package com.labyrinth.team01.labyrinth.game;


/**
 * Created by Андрей on 15.02.2016.
 */
public interface Labirinth {
    @Override
    String toString();
    void print();

    int getHeight();
    int getWidth();
    long getSeed();
    Vec2d getStartPosition();
    Vec2d getEndPosition();
    TypeLabirinthsCells getCell(Vec2d vec);
    TypeLabirinthsCells getCell(int x, int y);
}
