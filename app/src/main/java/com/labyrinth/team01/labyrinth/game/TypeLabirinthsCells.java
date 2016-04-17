package com.labyrinth.team01.labyrinth.game;


/**
 * Created by Андрей on 15.02.2016.
 */
public enum TypeLabirinthsCells {
    CORRIDOR,
    WALL,
    ENTRY,
    EXIT,
    UNDEFINED;

    public static char getChar(TypeLabirinthsCells cell){
        if(cell == null){
            return getChar(UNDEFINED);
        }
        switch (cell){
            case CORRIDOR: return ' ';
            case WALL: return '#';
            case UNDEFINED: return '?';
            case ENTRY: return 'E';
            case EXIT: return 'X';
            default: return getChar(UNDEFINED);
        }
    }
}
