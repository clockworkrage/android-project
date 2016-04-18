package com.labyrinth.team01.labyrinth.game;

/**
 * Created by Андрей on 17.04.2016.
 */
public class Vec2d {
    public double x;
    public double y;

    public Vec2d(){
        this.x = 0;
        this.y = 0;
    }

    public Vec2d(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void set(double x, double y){
        this.x = x;
        this.y = y;
    }
}
