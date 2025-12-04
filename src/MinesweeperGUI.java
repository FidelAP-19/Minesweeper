import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.Timer;
import java.awt.Color;

public class MinesweeperGUI extends JFrame {
    private Grid grid;
    private JButton[][] buttons;
    private boolean[][] flagged;
    private JFrame mainFrame;
    private JLabel bombCountLabel;
    private JLabel timerLabel;
    private Timer timer;
    private int safesCellsRevealed;
    private boolean gameOver;
    private int flagsPlaced;
    private int elapsedSeconds;
    private boolean timerStarted;
    private static final int GRID_SIZE = 10;

    public MinesweeperGUI(){
        safesCellsRevealed = 0;
        flagsPlaced = 0;
        mainFrame = new JFrame("Minesweeper");
        mainFrame.setSize(600,600);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        initializeGame();

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }
    public void initializeGame(){
        grid = new Grid();
        gameOver = false;
        buttons = new JButton[GRID_SIZE][GRID_SIZE];
        flagged = new boolean[GRID_SIZE][GRID_SIZE];
        //bomb counter and timer panel
        mainFrame.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setOpaque(false);
        bombCountLabel = new JLabel("\uD83D\uDCA3 Bombs: " + grid.getNumBombs());
        timerLabel = new JLabel(("⏱️ Time: 00:00"));
        topPanel.add(bombCountLabel, BorderLayout.WEST);
        topPanel.add(timerLabel, BorderLayout.EAST);
        mainFrame.add(topPanel, BorderLayout.NORTH);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));
        mainFrame.add(panel, BorderLayout.CENTER);



        for (int i = 0; i < GRID_SIZE;i++){
            for (int j = 0; j < GRID_SIZE;j++){
                JButton button = new JButton("✧");
                button.setFont(new Font("Arial", Font.PLAIN, 16));
                button.setForeground(new Color(0,20,51));
                button.setFocusable(false);
                buttons [i][j] = button;
                final int finalRow = i;
                final int finalColumn = j;
                panel.add(button);
                button.addActionListener(e -> {
                    if(gameOver)return;
                    if(!timerStarted) startTimer();
                    if(flagged[finalRow][finalColumn]) return;


                    if (grid.isBombAtLocation(finalRow,finalColumn)){
                        button.setText("💣");
                        button.setEnabled(false);
                        revealAllBombs();
                        gameOver = true;
                        timer.stop();
                        int choice = JOptionPane.showConfirmDialog(
                                mainFrame,
                                "Game Over! You hit a bomb!\nPlay again?",
                                "Game Over",
                                JOptionPane.YES_NO_OPTION
                        );
                        if (choice == JOptionPane.YES_OPTION){
                            restartGame();
                        }else{
                            System.exit(0);
                        }
                    }else{
                        revealCell(finalRow,finalColumn);
                        if (safesCellsRevealed == (100 - grid.getNumBombs())){
                            gameOver = true;
                            timer.stop();
                            int choice = JOptionPane.showConfirmDialog(
                                     mainFrame,
                                    "You Won! All safe cells revealed!\nPlay again?",
                                    "Winner",
                                    JOptionPane.YES_NO_OPTION);
                            if (choice == JOptionPane.YES_OPTION){
                                restartGame();
                            }else {
                                System.exit(0);
                            }
                        }
                    }

                });
                button.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            if (gameOver || !button.isEnabled()) return;

                            if (flagged[finalRow][finalColumn]) {
                                flagged[finalRow][finalColumn] = false;
                                button.setText("✧");
                                flagsPlaced--;
                            } else {
                                flagged[finalRow][finalColumn] = true;
                                button.setText("\uD83D\uDEA9");
                                flagsPlaced++;
                            }
                            bombCountLabel.setText(" 💣 Bombs: " + (grid.getNumBombs() - flagsPlaced));
                        }
                    }
                }
                );
            }
        }
    }
    /**
     * Reveals all bomb locations on the grid.
     * Updates button text to show bomb emoji for every cell containing a bomb.
     * Called when a player hits a bomb to show where all bombs were located.
     */
    public void revealAllBombs(){
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid.isBombAtLocation(i,j)) {
                    buttons[i][j].setText("💣");
                }
            }
        }
    }
    /**
     * Resets the game to it's initial state for a new round.
     * Stops the timer, creates a new grid, resets all counters, and clears all button states.
     * Called when a player clicks "Play Again" after winning or losing.
     */
    public void restartGame(){
        grid = new Grid();
        safesCellsRevealed = 0;
        flagsPlaced = 0;
        gameOver = false;
        if (timer != null) timer.stop();
        timerStarted = false;
        elapsedSeconds = 0;
        bombCountLabel.setText("💣 Bombs: " + grid.getNumBombs());
        timerLabel.setText("⏱️ Time: 00:00");


        for (int i = 0; i < GRID_SIZE;i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                buttons[i][j].setText("✧");
                buttons[i][j].setEnabled(true);
                flagged[i][j] = false;
            }
        }
    }
    /**
     * Recursively reveals a cell and its neighbors using flood-fill algorithm.
     * If the cell has 0 adjacent bombs, automatically reveals all connected empty cells.
     * Stops at cells with counts > 0 or boundaries.
     * @param row The row index of the cell to reveal
     * @param col The column index of the cell to reveal
     */
    public void revealCell(int row, int col) {
        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE) return;
        if (!buttons[row][col].isEnabled()) return;
        if (flagged[row][col] || gameOver) return;

        int cellCount = grid.getCountAtLocation(row, col);
        String count = String.valueOf(grid.getCountAtLocation(row, col));
        buttons[row][col].setText(count);
        buttons[row][col].setEnabled(false);
        safesCellsRevealed++;

        if (cellCount == 0) {
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (i >= 0 && i < GRID_SIZE && j >= 0 && j < GRID_SIZE) {
                        if (grid.isBombAtLocation(i,j)) {
                            continue;
                        }
                        if (grid.getCountAtLocation(i,j) >= 0){
                            revealCell(i,j);
                        }
                    }
                }
            }
        }
    }
    /**
     * Starts the game timer with a 1-second interval.
     * Uses a Swing Timer that increments elapsedSeconds and updates the display every second.
     * Timer begins when a player clicks their first cell.
     */
    public void startTimer(){
        elapsedSeconds = 0;
        timerStarted = true;
        timer = new Timer(1000, e -> {
            elapsedSeconds++;
            updateTimerDisplay();
        });
        timer.start();
    }
    public void updateTimerDisplay(){
        int minutes = elapsedSeconds/60;
        int seconds = elapsedSeconds % 60;
        timerLabel.setText(String.format("⏱️ Time: %02d:%02d", minutes, seconds));
    }
}
