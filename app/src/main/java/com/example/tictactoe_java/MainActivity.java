package com.example.tictactoe_java;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private GameBoard gameBoard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameBoard = findViewById(R.id.gameBoard);

        gameBoard.setOnGameEndedListener(new GameBoard.OnGameEndedListener() {
            @Override
            public void onGameEnded(String message) {
                showGameResultDialog(message);
            }
        });

    }
    private void showGameResultDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.game_result_dialog);
        dialog.setCancelable(false);

        TextView gameResultText = dialog.findViewById(R.id.game_result_text);
        gameResultText.setText(message);

        Button resetGameButton = dialog.findViewById(R.id.reset_game_button);
        resetGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameBoard.restartGame();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}