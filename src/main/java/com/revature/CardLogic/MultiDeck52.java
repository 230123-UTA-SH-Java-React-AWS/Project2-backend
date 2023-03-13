package com.revature.CardLogic;

public class MultiDeck52 extends Deck52 {
    public MultiDeck52(int numDecksToShuffleTogether){
        for(int i = 0; i < numDecksToShuffleTogether; i++){
            deck.addAll(new Deck52().deck);
        }
        shuffle();
    }
}
