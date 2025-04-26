package com.variant.matchup;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class HomeActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    MediaPlayer clickSound;
    Button btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mediaPlayer = MediaPlayer.create(this, R.raw.bg_music_2);
        clickSound = MediaPlayer.create(this, R.raw.click_sound);
        btnPlay = findViewById(R.id.btn_play);
        initialized();
    }

    private void initialized() {
        playBackgroundMusic();
        handlePlay();
    }

    private void playBackgroundMusic() {
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(1f, 1f);
        mediaPlayer.start();
    }

    private void playClick() {
        clickSound.setVolume(1f, 1f);
        if (clickSound.isPlaying()) {
            clickSound.stop();
            clickSound.prepareAsync();
        }
        clickSound.start();
    }

    private void handlePlay() {
        btnPlay.setOnClickListener(v -> {
            playClick();
            Intent i = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(i);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}