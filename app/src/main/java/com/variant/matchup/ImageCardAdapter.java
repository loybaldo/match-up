package com.variant.matchup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;


public class ImageCardAdapter extends RecyclerView.Adapter<ImageCardAdapter.ViewHolder> {
    private final Context context;
    private final List<CardItem> cards;
    private final Handler handler = new Handler();
    private final MediaPlayer mediaPlayer;
    private final MediaPlayer matchSound;
    private final MediaPlayer gameCompleteSound;
    MediaPlayer clickSound;
    private Integer totalMatchedImage = 0;
    private int flippedPosition1 = -1;
    private int flippedPosition2 = -1;
    private boolean isBusy = false;
    private OnGameReloadListener reloadListener;

    public interface OnGameReloadListener {
        void onReloadRequested();
    }

    public void setOnGameReloadListener(OnGameReloadListener listener) {
        this.reloadListener = listener;
    }

    public ImageCardAdapter(Context context, List<Integer> imageList) {
        this.context = context;
        this.cards = new ArrayList<>();
        for (Integer imageRes : imageList) {
            cards.add(new CardItem(imageRes));
        }
        mediaPlayer = MediaPlayer.create(context, R.raw.card_sound);
        matchSound = MediaPlayer.create(context, R.raw.correct_sound);
        gameCompleteSound = MediaPlayer.create(context, R.raw.clap_sound);
        clickSound = MediaPlayer.create(context, R.raw.click_sound);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_item_card);
        }
    }

    @NonNull
    @Override
    public ImageCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CardItem card = cards.get(position);
        if (card.isFaceUp || card.isMatched) {
            holder.imageView.setImageResource(card.imageResId);
        } else {
            holder.imageView.setImageResource(R.drawable.img_cover);
        }
        holder.imageView.setOnClickListener((v) -> {
            if (card.isFaceUp || card.isMatched || isBusy) return;
            card.isFaceUp = true;
            notifyItemChanged(position);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.prepareAsync();
            }
            mediaPlayer.start();
            if (flippedPosition1 == -1) {
                flippedPosition1 = position;
            } else if (flippedPosition2 == -1) {
                flippedPosition2 = position;
                isBusy = true;
                handler.postDelayed(this::checkMatch, 800);
            }
        });
    }

    private void checkMatch() {
        CardItem card1 = cards.get(flippedPosition1);
        CardItem card2 = cards.get(flippedPosition2);
        if (card1.imageResId == card2.imageResId) {
            card1.isMatched = true;
            card2.isMatched = true;
            this.totalMatchedImage += 2;
            handlePlaySound();
        } else {
            card1.isFaceUp = false;
            card2.isFaceUp = false;
        }
        notifyItemChanged(flippedPosition1);
        notifyItemChanged(flippedPosition2);
        flippedPosition1 = -1;
        flippedPosition2 = -1;
        isBusy = false;
    }

    private void handlePlaySound() {
        if (this.totalMatchedImage == 12) {
            if (gameCompleteSound.isPlaying()) {
                gameCompleteSound.stop();
                gameCompleteSound.prepareAsync();
            }
            gameCompleteSound.start();
            showGameCompleteDialog();
        } else {
            if (matchSound.isPlaying()) {
                matchSound.stop();
                matchSound.prepareAsync();
            }
            matchSound.start();
        }
    }

    private void showGameCompleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.alert_game_complete, null);
        customView.setBackgroundResource(R.drawable.bg_rounded);
        builder.setView(customView);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        ImageButton btnHome = customView.findViewById(R.id.btn_dialog_home);
        ImageButton btnReload = customView.findViewById(R.id.btn_dialog_reload);
        btnHome.setOnClickListener(v -> {
            playClick();
            dialog.dismiss();
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
            if (context instanceof MainActivity) {
                ((MainActivity) context).finish();
            }
        });
        btnReload.setOnClickListener(v -> {
            playClick();
            dialog.dismiss();
            if (context instanceof MainActivity) {
                ((MainActivity) context).reloadGame();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void playClick() {
        clickSound.setVolume(1f, 1f);
        if (clickSound.isPlaying()) {
            clickSound.stop();
            clickSound.prepareAsync();
        }
        clickSound.start();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void reloadCards(List<Integer> newImages) {
        this.totalMatchedImage = 0;
        cards.clear();
        for (Integer imageRes : newImages) {
            cards.add(new CardItem(imageRes));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}
