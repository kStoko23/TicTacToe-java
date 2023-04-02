package com.example.tictactoe_java;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
// The GameBoard class is a custom view for the TicTacToe game.
// It extends the View class and is responsible for drawing the game board and handling touch events.
// The class provides methods to draw the game board, player moves, and check for a win or a draw.
// The board is represented as a 3x3 matrix of the Player enum, where each cell can be either X, O, or NONE.
//
// In addition to drawing and touch handling, the GameBoard class provides a listener interface,
// OnGameEndedListener, which is used to notify the activity when the game ends.
// It also includes a restartGame() method that resets the board, game state, and current player.
// The onDraw() method redraws the board and player moves, and the onTouchEvent() method handles user input, updates the board, and checks for game outcomes.
//
//Just a quick little project I made to learn a bit of Android Studio.
public class GameBoard extends View {
    //region Constants
    private final int boardColor;
    //endregion
    //region Variables
    private int cellSize;
    private enum Player { X, O, NONE } //Enum for the players
    private Player[][] board = new Player[3][3];
    private Paint boardPaint = new Paint();
    private Player currentPlayer = Player.X;
    private Paint xPaint = new Paint();
    private Paint oPaint = new Paint();
    private boolean gameEnded = false;
    private OnGameEndedListener onGameEndedListener;
    //endregion
    //region Interface
    interface OnGameEndedListener {
        void onGameEnded(String message);
    }
    public void setOnGameEndedListener(OnGameEndedListener listener) {
        this.onGameEndedListener = listener;
    }
    //endregion
    public GameBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GameBoard,
                0, 0);
        try {
            boardColor = a.getColor(R.styleable.GameBoard_boardColor, 0);
        }
        finally {
            a.recycle();
        }
        initBoard();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // onTouchEvent(): This method is called when the user touches the screen.
        // It checks if the game has ended, and if not, it checks if the user touched a cell that is empty.
        // If so, it places the current player's mark in that cell and checks for a winner or a draw.
        // If there is a winner or a draw, it calls the onGameEndedListener to
        // notify the activity that the game has ended.
        // If not, it switches the current player.
        if (!gameEnded && event.getAction() == MotionEvent.ACTION_DOWN) {
            int row = (int) (event.getY() / cellSize);
            int col = (int) (event.getX() / cellSize);

            if (board[row][col] == Player.NONE) {
                board[row][col] = currentPlayer;
                invalidate(); // Redraw the view

                // Check for a winner or a draw
                if (checkForWin()) {
                    gameEnded = true;
                    if (onGameEndedListener != null) {
                        onGameEndedListener.onGameEnded("Player " + currentPlayer + " wins!");
                    }
                    System.out.println("Player " + currentPlayer + " wins!");
                } else if (checkForDraw()) {
                    gameEnded = true;
                    if (onGameEndedListener != null) {
                        onGameEndedListener.onGameEnded("It's a draw!");
                    }
                    System.out.println("It's a draw!");
                } else {
                    // Switch the current player
                    currentPlayer = (currentPlayer == Player.X) ? Player.O : Player.X;
                }
            }
        }
        return true;
    }
    //region Drawing GameBoard
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // onMeasure(): This method is called when the view is first created.
        // It sets the size of the view to be a square that is as large as possible for the device.
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = Math.min(this.getMeasuredWidth(), this.getMeasuredHeight());
        cellSize = size / 3;
        setMeasuredDimension(size, size);
    }
    @Override
    protected void onDraw(Canvas canvas){
        // onDraw(): This method is called when the view needs to be redrawn.
        // It draws the game board and the player's moves.
        boardPaint.setColor(boardColor);
        boardPaint.setStyle(Paint.Style.STROKE);
        boardPaint.setStrokeWidth(10);
        boardPaint.setAntiAlias(true);

        canvas.drawRect(0, 0, getWidth(), getHeight(), boardPaint);

        drawBoard(canvas);
        drawPlayerMoves(canvas);
    }
    private void drawBoard(Canvas canvas) {
        // drawBoard(): This method draws the lines of the game board.

        for (int i = 0; i < 3; i++) {
            canvas.drawLine(0, cellSize * i, getWidth(), cellSize * i, boardPaint);
            canvas.drawLine(cellSize * i, 0, cellSize * i, getHeight(), boardPaint);
        }
    }
    private void drawPlayerMoves(Canvas canvas) {
        // drawPlayerMoves(): This method draws the player's moves.
        // It loops through the board array and draws an X or an O in each cell, if there are any.

        xPaint.setColor(Color.BLUE);
        xPaint.setStrokeWidth(10);
        xPaint.setAntiAlias(true);

        oPaint.setColor(Color.RED);
        oPaint.setStrokeWidth(10);
        oPaint.setAntiAlias(true);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                float centerX = col * cellSize + cellSize / 2;
                float centerY = row * cellSize + cellSize / 2;
                float offset = cellSize * 0.25f;

                if (board[row][col] == Player.X) {
                    // Draw X
                    canvas.drawLine(centerX - offset,
                            centerY - offset,
                            centerX + offset,
                            centerY + offset,
                            xPaint);
                    canvas.drawLine(centerX - offset,
                            centerY + offset,
                            centerX + offset,
                            centerY - offset,
                            xPaint);

                } else if (board[row][col] == Player.O) {
                    // Draw O
                    canvas.drawCircle(centerX,
                            centerY,
                            cellSize / 2 - offset,
                            oPaint);
                }
            }
        }
    }
    //endregion
    //region Game Logic
    private boolean checkForWin() {
        // checkForWin(): This method checks if there is a winner.
        // It loops through the board array and checks if there are three X's or O's in a row, column, or diagonal.
        // If there is, it returns true - indicating a win. Otherwise, it returns false - indicating that there's no winner.

        for (int i = 0; i < 3; i++) {
            if (board[i][0] != Player.NONE && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return true;
            }
            if (board[0][i] != Player.NONE && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return true;
            }
        }
        if (board[0][0] != Player.NONE && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return true;
        }
        if (board[0][2] != Player.NONE && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return true;
        }
        return false;
    }
    private boolean checkForDraw() {
        // checkForDraw(): This method checks if the game is a draw.
        // It loops through the board array and checks if there are any empty cells.

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == Player.NONE) {
                    return false;
                }
            }
        }
        return true;
    }
    private void initBoard() {
        // initBoard(): This method initializes the board array.
        // It sets all the cells to Player.NONE.

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = Player.NONE;
            }
        }
    }
    public void restartGame() {
        // restartGame(): This method restarts the game.
        // It resets the board array and the gameEnded flag.

        initBoard();
        gameEnded = false;
        currentPlayer = Player.X;
        invalidate();
    }
    //endregion
}
