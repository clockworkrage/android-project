package com.labyrinth.team01.labyrinth.game;


import java.util.Stack;

/**
 * Created by Андрей on 19.02.2016.
 */
public class LabirinthImpl implements Labirinth {
    private RandomHelper rnd;
    private int height;
    private int width;
    private TypeLabirinthsCells[][] body;
    private Vec2d startPosition = new Vec2d();
    private Vec2d endPosition = new Vec2d();

    private void repaireSize(){
        if(width%2==0){
            width -=1;
        }
        if(height%2==0){
            height -=1;
        }
    }

    public LabirinthImpl(int width, int height, int seed){
        this.height = height;
        this.width = width;
        repaireSize();
        body = new TypeLabirinthsCells[this.height][this.width];
        rnd = new RandomHelper(seed);
        generate();
    }

    public LabirinthImpl(int width, int height){
        this.height = height;
        this.width = width;
        repaireSize();
        body = new TypeLabirinthsCells[this.height][this.width];
        rnd = new RandomHelper();
        generate();
    }

    @Override
    public Vec2d getStartPosition(){
        return startPosition;
    }

    @Override
    public Vec2d getEndPosition(){
        return endPosition;
    }

    private void generate(){
        int tempHeight = (height-1)/2;
        int tempWidth = (width-1)/2;
        int[][] tempBody = new int[tempHeight][tempWidth];
        boolean[][] borderRight = new boolean[tempHeight][tempWidth];
        boolean[][] borderBottom = new boolean[tempHeight][tempWidth];

        for(int i=0; i<tempHeight; ++i){
            for (int j=0; j<tempWidth; ++j){
                tempBody[i][j]=0;
                borderRight[i][j] = false;
                borderBottom[i][j] = false;
            }
        }

        createNetwork();
        createBorders();
        Vec2d entry = createEntry(tempBody, tempWidth, tempHeight);
        //startPosition = entry;
        concretGenerate(tempBody, tempWidth, tempHeight, entry);
        Vec2d exit = createExit(tempBody, tempWidth, tempHeight);
    }

    private void testPrint(int[][] tempBody, int tempWidth, int tempHeight){
        System.out.print('\n');
        for (int i=0; i<tempHeight; ++i){
            for (int j=0; j<tempWidth; ++j){
                System.out.print(tempBody[i][j]);
                System.out.print("\t");
            }
            System.out.print('\n');
        }
        System.out.print('\n');
    }

    private void concretGenerate(int[][] tempBody, int tempWidth, int tempHeight, Vec2d entry){
        Stack<Cell> cellList = new Stack<>();
        Cell startCell = new Cell();
        startCell.direct = Cell.NONE;
        startCell.x = (int)entry.x;
        startCell.y = (int)entry.y;
        startCell.dist = 1;
        tempBody[startCell.x][startCell.y] = 1;
        cellList.push(startCell);

        while(!cellList.isEmpty()){
            Cell temp = cellList.pop();
            for(int i=0; i<rnd.rand(1, 3); ++i){
                Cell newCell = gen(tempBody, tempWidth, tempHeight, temp);
                if(checkGen(tempBody, tempWidth, tempHeight, newCell)){
                    tempBody[newCell.x][newCell.y] = newCell.dist;
                    setLastCell(newCell);
                    cellList.push(newCell);
                }
            }
        }
    }

    private Cell gen(int[][] tempBody, int tempWidth, int tempHeight, Cell tempCell){
        int flag = rnd.rand(1,4);
        Cell newVec = new Cell();
        newVec.x = -1;
        newVec.y = -1;
        newVec.dist = tempCell.dist + 1;
        int counter = 0;
        while (!checkGen(tempBody, tempWidth, tempHeight, newVec)) {
            switch (flag) {
                case 1: {
                    newVec.x = tempCell.x - 1;
                    newVec.y = tempCell.y;
                    newVec.direct = Cell.TOP;
                } break;
                case 2: {
                    newVec.x = tempCell.x + 1;
                    newVec.y = tempCell.y;
                    newVec.direct = Cell.BOTTOM;
                } break;
                case 3: {
                    newVec.x = tempCell.x;
                    newVec.y = tempCell.y - 1;
                    newVec.direct = Cell.LEFT;
                } break;
                case 4: {
                    newVec.x = tempCell.x;
                    newVec.y = tempCell.y + 1;
                    newVec.direct = Cell.RIGHT;
                } break;
                default: break;
            }
            flag = ++flag>4 ? 1 : flag;
            ++counter;
            if(counter>4){
                break;
            }
        }
        return newVec;
    }

    private boolean checkGen(int[][] tempBody, int tempWidth, int tempHeight, Cell tempCell) {
        return !(tempCell.x < 0 || tempCell.y < 0 || tempCell.x >= tempWidth || tempCell.y >= tempHeight) && tempBody[tempCell.x][tempCell.y] == 0;
    }

    private Vec2d createExit(int[][] tempBody, int tempWidth, int tempHeight){
        Vec2d exit = new Vec2d();
        int maxDist = 0;
        for(int i=0; i<tempHeight; ++i){
            for(int j=0; j<tempWidth; ++j){
                if(maxDist < tempBody[i][j]){
                    maxDist = tempBody[i][j];
                    exit.x = i;
                    exit.y = j;
                }
            }
        }
        setCell((int)exit.x*2+1, (int)exit.y*2+1, TypeLabirinthsCells.EXIT);
        return exit;
    }

    private Vec2d createEntry(int[][] tempBody, int tempWidth, int tempHeight){
        switch (rnd.rand(1,4)){
            case 1: {
                tempBody[tempHeight-1][rnd.rand(0, tempWidth-1)] = -1;
            } break;
            case 2: {
                tempBody[0][rnd.rand(0, tempWidth-1)] = -1;
            } break;
            case 3: {
                tempBody[rnd.rand(0, tempHeight-1)][tempWidth-1] = -1;
            } break;
            case 4:{
                tempBody[rnd.rand(0, tempHeight-1)][0] = -1;
            } break;
            default:{
                tempBody[tempHeight-1][rnd.rand(0, tempWidth-1)] = -1;
            } break;
        }

        Vec2d entry = new Vec2d();
        for(int i=0; i<tempHeight; ++i){
            for(int j=0; j<tempWidth; ++j){
                if(tempBody[i][j]==-1){
                    setCell(i*2+1, j*2+1, TypeLabirinthsCells.ENTRY);
                    entry.set(i, j);
                    startPosition.x = i*2+1;
                    startPosition.y = j*2+1;
                }
            }
        }
        return entry;
    }

    private void createNetwork(){
        for(int i=0; i<height; ++i){
            for (int j=0; j<width; ++j){
                if(i%2==0 || j%2==0){
                    setCell(i, j, TypeLabirinthsCells.WALL);
                } else {
                    setCell(i, j, TypeLabirinthsCells.CORRIDOR);
                }
            }
        }
    }

    @SuppressWarnings("all")
    private boolean isCorrectCoord(Vec2d coord){
        return coord.x >= 0 && coord.y >= 0 && coord.x <= height - 1 && coord.y <= width - 1;
    }

    @SuppressWarnings("all")
    private boolean isIntoBorders(Vec2d coord){
        return coord.x >= 1 && coord.y >= 1 && coord.x <= height - 2 && coord.y <= width - 2;
    }

    private void setCell(Vec2d cell, TypeLabirinthsCells type){
        if(!isCorrectCoord(cell)){
            return;
        }
        body[(int)cell.x][(int)cell.y] = type;
    }

    private void setCell(int x, int y, TypeLabirinthsCells type){
        setCell(new Vec2d(x, y), type);
    }


    private void setLastCell(Cell cell){
        switch(cell.direct){
            case Cell.TOP:{
                setCell(1+cell.x*2+1, 1+ cell.y*2, TypeLabirinthsCells.CORRIDOR);
            } break;
            case Cell.BOTTOM:{
                setCell(1+cell.x*2-1, 1+ cell.y*2, TypeLabirinthsCells.CORRIDOR);
            } break;
            case Cell.LEFT:{
                setCell(1+cell.x*2, 1+ cell.y*2+1, TypeLabirinthsCells.CORRIDOR);
            } break;
            case Cell.RIGHT:{
                setCell(1+cell.x*2, 1+ cell.y*2-1, TypeLabirinthsCells.CORRIDOR);
            } break;
            default: break;
        }
    }


    private void createBorders(){
        for(int i = 0; i< height; ++i){
            setCell(new Vec2d(i, width -1), TypeLabirinthsCells.WALL);
            setCell(new Vec2d(i, 0), TypeLabirinthsCells.WALL);
        }
        for(int i = 0; i< width; ++i){
            setCell(new Vec2d(height -1, i), TypeLabirinthsCells.WALL);
            setCell(new Vec2d(0, i), TypeLabirinthsCells.WALL);
        }
    }


    @Override
    public void print() {
        System.out.print(toString() + '\n');
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public long getSeed() {
        return 0;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i< height; ++i){
            for (int j = 0; j< width; ++j){
                stringBuilder.append(TypeLabirinthsCells.getChar(body[i][j]));
                stringBuilder.append(TypeLabirinthsCells.getChar(body[i][j]));
            }
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    @Override
    public TypeLabirinthsCells getCell(int x, int y){
        if(!isCorrectCoord(new Vec2d(x,y))){
            return TypeLabirinthsCells.UNDEFINED;
        }
        return body[x][y];
    }

    @Override
    public TypeLabirinthsCells getCell(Vec2d vec){
        return getCell((int)vec.x, (int)vec.y);
    }

    private static class Cell{
        public static final int TOP = 0;
        public static final int BOTTOM = 1;
        public static final int RIGHT = 2;
        public static final int LEFT = 3;
        public static final int NONE = -1;
        public int x;
        public int y;
        public int direct;
        public int dist;
    }
}
