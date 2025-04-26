package com.variant.matchup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageButton btnHome, btnReload;
    MediaPlayer mediaPlayer;
    MediaPlayer clickSound;
    RecyclerView cardsRecyclerView;
    ImageCardAdapter imageCardAdapter;
    List<Integer> uniqueImages = Arrays.asList(
            R.drawable.img_1,
            R.drawable.img_2,
            R.drawable.img_3,
            R.drawable.img_4,
            R.drawable.img_5,
            R.drawable.img_6
    );
    List<Integer> imagePairs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Handle system insets for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnHome = findViewById(R.id.btn_home);
        btnReload = findViewById(R.id.btn_reload);
        cardsRecyclerView = findViewById(R.id.recyclerViewCards);
        // Initialize sounds
        mediaPlayer = MediaPlayer.create(this, R.raw.bg_music);
        clickSound = MediaPlayer.create(this, R.raw.click_sound);
        // Initial setup
        initialized();
    }

    private void initialized() {
        displayCards();
        playBackgroundMusic();
        handleGoToHome();
        handleReload();
    }

    private void shuffleCards() {
        Collections.shuffle(imagePairs);
    }

    private void displayCards() {
        // Add the image pairs (each image twice)
        for (Integer image : uniqueImages) {
            imagePairs.add(image);
            imagePairs.add(image);
        }
        shuffleCards();
        cardsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        imageCardAdapter = new ImageCardAdapter(this, imagePairs);
        cardsRecyclerView.setAdapter(imageCardAdapter);
    }

    private void playBackgroundMusic() {
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.5f, 0.5f);
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

    public void reloadGame() {
        imagePairs.clear();
        for (Integer image : uniqueImages) {
            imagePairs.add(image);
            imagePairs.add(image);
        }
        shuffleCards();
        imageCardAdapter.reloadCards(imagePairs);
    }

    private void handleGoToHome() {
        btnHome.setOnClickListener(v -> {
            playClick();
            Intent i = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(i);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleReload() {
        btnReload.setOnClickListener(v -> {
            playClick();
            imagePairs.clear();
            for (Integer image : uniqueImages) {
                imagePairs.add(image);
                imagePairs.add(image);
            }
            shuffleCards();
            imageCardAdapter.reloadCards(imagePairs);
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
