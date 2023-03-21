package com.revature.project2backend.gamelogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.CardLogic.Card52;
import com.revature.CardLogic.Deck52;



public class BlackjackGameLogicTest {
    Deck52 deck;

    @BeforeEach
    void setUp() {
        deck = new Deck52();
    }

    @Test
    void removeShouldReturnError() {
        Card52 c = deck.peek();
        deck.remove(c);
        assertThrows(Exception.class, () -> deck.remove(c), "Deck does not contain specified card.");
    }

    @Test
    void removeShouldNotReturnError() {
        Card52 card = deck.peek();
        assertEquals(deck.remove(card), true);
    }

    @Test
    void dealShouldNotReturnError() {
        Card52 card = deck.peek();
        assertEquals(deck.deal(), card);
    }

    @Test
    void dealShouldReturnError() {
        assertThrows(Exception.class, () -> deck.deal(53),
                "There are no more cards left to take.");
    }

    @Test
    void dealShouldReturnIndexError() {
        deck.deal(1);
        assertThrows(Exception.class, () -> deck.deal(52),
                "There are no more cards left to take.");
    }
    
    @Test
    void hasNextShouldReturnTrue() {
        deck.shuffle();
        assertEquals(deck.hasNext(), true);
    }

    @Test
    void hasNextShouldReturnFalse() {
        deck.shuffle();
        deck.deal(52);
        assertEquals(deck.hasNext(), false);
    }

    @Test
    void peekShouldNotReturnError() {
        deck.deal(51);
        assertEquals(deck.peek().getClass(), Card52.class);
    }
    
    @Test
    void peekShouldReturnError() {
        while (deck.hasNext()) {
            deck.deal(1);
        }
        assertThrows(Exception.class, () -> deck.peek(),
                "There are no more cards left to take.");
    }
}