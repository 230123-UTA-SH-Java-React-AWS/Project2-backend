package com.revature.CardLogic;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;


@AllArgsConstructor
public class Card52 implements Comparable<Card52> {
    public enum Suit {
        SPADE, HEART, CLUB, DIAMOND
    }

    public enum Rank {
        TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE
    }

    @Getter @NonNull
    protected Suit suit;
    @Getter @NonNull
    protected Rank rank;

    //Interesting stuff happening here. This provides a canonical ordering for all cards and is the implmentation for the
    // Comparable<Card52> interface method. This allows the Collections class to sort a list of cards (literally
    // Collections.sort(List<Card52>).) The order provided is as follows:
    // Ace of Spades, Ace of Hearts, Ace of Clubs, Ace of Diamonds, King of Spades, and so on to the Two of Diamonds.
    @Override
    public int compareTo(Card52 o) {
        int rankDiff = o.rank.ordinal() - this.rank.ordinal();
        if (rankDiff != 0) {
            return rankDiff;
        } else {
            return this.suit.ordinal() - o.suit.ordinal();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Card52)) {
            return false;
        }
        Card52 card52 = (Card52) o;
        return Objects.equals(suit, card52.suit) && Objects.equals(rank, card52.rank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }


    //Provides a clean printout of a card, like "[4D]" for the Four of Diamonds or "[KC]" for the King of Clubs
    @Override
    public String toString() {
        String out = "[";

        switch (rank) {
            case ACE:
                out += 'A';
                break;
            case KING:
                out += 'K';
                break;
            case QUEEN:
                out += 'Q';
                break;
            case JACK:
                out += 'J';
                break;
            case TEN:
                out += 'T';
                break;
            case NINE:
                out += '9';
                break;
            case EIGHT:
                out += '8';
                break;
            case SEVEN:
                out += '7';
                break;
            case SIX:
                out += '6';
                break;
            case FIVE:
                out += '5';
                break;
            case FOUR:
                out += '4';
                break;
            case THREE:
                out += '3';
                break;
            case TWO:
                out += '2';
                break;
        }

        switch (suit) {
            case SPADE:
                out += 'S';
                break;
            case HEART:
                out += 'H';
                break;
            case DIAMOND:
                out += 'D';
                break;
            case CLUB:
                out += 'C';
                break;
        }

        out += ']';
        return out;
    }

}
