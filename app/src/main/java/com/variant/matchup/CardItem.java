package com.variant.matchup;

public class CardItem {
    public int imageResId;
    public boolean isFaceUp = false;
    public boolean isMatched = false;

    public CardItem(int imageResId) {
        this.imageResId = imageResId;
    }
}
