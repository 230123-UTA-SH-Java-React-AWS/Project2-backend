package com.revature.CardLogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.card_logic.Card52;
import com.revature.card_logic.Deck52;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;


public class Deck52Test {
    private Deck52 deck;

    @BeforeEach
    public void setUp(){
        deck = new Deck52();
    }

    // Constructor

    @Test
    void aNewDeckShouldHave52Cards() {
        assertEquals(52, deck.size());
    }

    // deal()

    @Test
    void dealShouldReturnACard() {
        Card52 card = deck.deal();
        assertEquals("[AD]", card.toString());
    }

    @Test
    void dealShouldRemoveACardFromDeck() {
        int sizeBeforeDeal = deck.size();
        deck.deal();
        assertEquals(sizeBeforeDeal - 1, deck.size());
    }

    @Test
    void dealShouldThrowExceptionIfDeckIsEmpty() {
        deck.deal(52);
        try {
            deck.deal();
            fail("Should have thrown an exception");
        } catch (Exception e) {
            assertEquals(IndexOutOfBoundsException.class, e.getClass());
        }
    }

    // deal(int number)

    @Test
    void dealMultipleShouldReturnCardList() {
        List<Card52> dealtCards = deck.deal(2);

        assertEquals("[AD]", dealtCards.remove(0).toString());
        assertEquals("[KD]", dealtCards.remove(0).toString());
    }

    @Test
    void dealMultipleShouldRemoveCardsFromDeck() {
        int sizeBeforeDeal = deck.size();
        deck.deal(2);
        assertEquals(sizeBeforeDeal - 2, deck.size());
    }
    
    @Test
    void dealMultipleShouldThrowExceptionIfDeckIsEmpty() {
        try {
            deck.deal(53);
            fail("Should have thrown an exception");
        } catch (Exception e) {
            assertEquals(IndexOutOfBoundsException.class, e.getClass());
        }
    }

    // hasNext

    @Test
    void newDeckShouldHasNext() {
        assertEquals(true, deck.hasNext());
    }

    @Test
    void emptyDeckShouldNotHasNext() {
        deck.deal(52);
        assertEquals(false, deck.hasNext());
    }

    @Test
    void peekShouldReturnCard() {
        assertEquals(Card52.class, deck.peek().getClass());
    }

    @Test
    void peekEmptyDeckShouldThrowException() {
        deck.deal(52);
        try {
            deck.peek();
            fail("Should have thrown an exception");
        } catch (Exception e) {
            assertEquals(IndexOutOfBoundsException.class, e.getClass());
        }
    }

    @Test
    void shuffleShouldBeRandom() {
        Deck52 newDeck = new Deck52();
        assertNotEquals(newDeck.shuffle().deal(), deck.shuffle().deal());
    }

    @Test
    void shuffleShouldNotChangeDeckSize() {
        assertEquals(deck.size(), deck.shuffle().size());
    }

    @Test
    void testToString() {
        deck.deal(51);
        assertEquals("{ deck='[[2S]]'}", deck.toString());
    }
}
