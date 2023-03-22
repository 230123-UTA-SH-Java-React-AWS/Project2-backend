package com.revature.game_logic.blackjack;

import java.util.Collections;
import java.util.List;

import com.revature.card_logic.Card52;
import com.revature.card_logic.Hand52;

import lombok.Getter;
import lombok.NoArgsConstructor;

public @NoArgsConstructor class BlackjackHand extends Hand52 {
    @Getter
    private int handValue = 0;
    @Getter
    private boolean isBustedOut = false;
    @Getter
    private boolean isSoftHand = false;

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
        isSoftHand = false;
        handValue = 0;
        int aceCount = 0;
        for(int i = cards.size() - 1; i >= 0; i--){
            Card52 c = cards.get(i);
            //This switch statement is here instead of being in its own BlackjackCard class bfor a specific reason.
            //I did not extend the Card52 class because that is not analogous to actual card behavior. The game determines
            // the way that cards act, not the other way around. For instance, a King in blackjack is worth the same value
            // as a queen, but in poker it would be higher. However, it is still the same King card.
            //This way of doing it also ensures compatibility between Deck52, Card52, and any extension of the Hand52 class.
            switch (c.getRank()) {
                case ACE:
                    aceCount++;
                    break;
                case KING:
                case QUEEN:
                case JACK:
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
        for (int i = 0; i < aceCount; i++) {
            if (handValue > 10) {
            handValue += 1;
            } else {
                handValue += 11;
                isSoftHand = true;
                //A "soft" hand is one where an ace is present and is being counted as an 11.
                //A soft hand can never bust by taking one additional card.
            }
        }
        
        
        if(handValue > 21) isBustedOut = true;
        return handValue;
    }
}
