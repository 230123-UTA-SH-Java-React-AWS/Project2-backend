package com.revature.GameLogic.Blackjack;

import java.util.Collections;
import java.util.List;

import com.revature.CardLogic.Card52;
import com.revature.CardLogic.Hand52;

public class BlackjackHand extends Hand52 {
    private int handValue = 0;
    private boolean hasBusted = false;

    public BlackjackHand() {

    }

    public BlackjackHand(List<Card52> cards){
        push(cards);
    }

    //Both of the push methods are overridden to ensure that the current value of the hand is always correct.
    @Override
    public List<Card52> push(Card52 card) {
        super.push(card);
        calculateHandValue();
        return cards;
    }

    @Override
    public List<Card52> push(List<Card52> cardList) {
        super.push(cardList);
        calculateHandValue();
        return cards;
    }

    //Updates the current handValue (also returns it immediately for use in functions)
    public int calculateHandValue() {
        handValue = 0;
        Collections.sort(cards); //Places any aces in hand at the front of the hand to ensure that they are checked last.
        for(int i = cards.size() - 1; i >= 0; i--){
            Card52 c = cards.get(i);
            switch (c.getRank()) {
                case ACE:
                    handValue += (handValue > 10) ? 1 : 11;
                    break;
                case KING:
                    handValue += 10;
                    break;
                case QUEEN:
                    handValue += 10;
                    break;
                case JACK:
                    handValue += 10;
                    break;
                case TEN:
                    handValue += 10;
                    break;
                case NINE:
                    handValue += 9;
                    break;
                case EIGHT:
                    handValue += 8;
                    break;
                case SEVEN:
                    handValue += 7;
                    break;
                case SIX:
                    handValue += 6;
                    break;
                case FIVE:
                    handValue += 5;
                    break;
                case FOUR:
                    handValue += 4;
                    break;
                case THREE:
                    handValue += 3;
                    break;
                case TWO:
                    handValue += 2;
                    break;
                //No need to check the default case (i.e. null) because the value of suit and rank is guaranteed non-null
                // by the Card52 class.
            }
        }
        
        if(handValue > 21) hasBusted = true;
        return handValue;
    }

    //handValue is always current with the cards that are in this hand, unless the hand
    // was manually tampered with using getHand(). If that happens, use calculateHandValue() to get the new
    // current value of the hand.
    public int getHandValue() { return handValue; }
    public boolean getHasBusted() { return hasBusted; }
}
