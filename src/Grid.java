import java.util.Random;
import javax.swing.*;
import java.util.Arrays;

public class Grid {
    private boolean [][] bombGrid;
    private int [][] countGrid;
    private int numRows;
    private int numColumns;
    private int numBombs;

    public Grid(){
        numRows = 10;
        numColumns = 10;
        numBombs = 25;
        createBombGrid();
        createCountGrid();
    }
    public Grid(int rows, int columns){
        numRows = rows;
        numColumns = columns;
        numBombs = 25;
        createBombGrid();
        createCountGrid();
    }
    public Grid(int rows, int columns, int bombs){
        numRows = rows;
        numColumns = columns;
        numBombs = bombs;
        createBombGrid();
        createCountGrid();
    }
    public int getNumRows(){
        return numRows;
    }
    public int getNumColumns(){
        return numColumns;
    }
    public int getNumBombs(){
        return numBombs;
    }
    public boolean[][] getBombGrid() {
        boolean[][] copy = new boolean[numRows][numColumns];
        for (int i = 0; i < numRows; i++) {
            copy[i] = bombGrid[i].clone();
        }
        return copy;
    }
    public int[][] getCountGrid() {
        int[][] copy = new int[numRows][numColumns];
        for (int i = 0; i < numRows; i++) {
            copy[i] = countGrid[i].clone();
        }
        return copy;
    }
    public boolean isBombAtLocation(int row, int column){
        return bombGrid[row][column];
    }
    public int getCountAtLocation(int row, int column){
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = column - 1; j <= column + 1; j++) {
                if (i >= 0 && i < numRows && j >= 0 && j < numColumns) {
                    if (bombGrid[i][j]) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
    public void createBombGrid(){
        double probabilityTrue = 0.25;
        bombGrid = new boolean[numRows][numColumns];
        Random random = new Random();
        int bombs = 0;
        for(int i = 0; i < numRows;i++){
            for(int j = 0; j < numColumns;j++){
                boolean result;

                if(numBombs > 0){
                    result = random.nextDouble() < probabilityTrue;
                    bombGrid[i][j] = result;
                    if(result) {
                        numBombs--;
                        bombs++;
                    }
                }else{
                    bombGrid[i][j] = false;
                }
            }
        }
        numBombs = bombs;
    }
    public void createCountGrid(){
        countGrid = new int[numRows][numColumns];
        for(int i = 0; i < numRows;i++){
            for(int j = 0; j < numColumns;j++){
                countGrid[i][j] = getCountAtLocation(i,j);
            }
        }
    }

}
