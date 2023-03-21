package com.revature.project2backend.cardlogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.revature.CardLogic.Card52;
import com.revature.CardLogic.Card52.Rank;
import com.revature.CardLogic.Card52.Suit;


public class CardLogicTest {
    
    @Test
    void cardShouldReturnSuitAndRank() {
        Card52 goodCard52 = new Card52(Suit.SPADE, Rank.TWO);
        assertEquals(goodCard52.toString(), "[2S]");
    }

    @Test
    void compareSameCardsShouldReturn0() {
        Card52 firstCard52 = new Card52(Suit.SPADE, Rank.TWO);
        Card52 sameCard52 = new Card52(Suit.SPADE, Rank.TWO);
        assertEquals(firstCard52.compareTo(sameCard52), 0);
    }

    @Test
    void compareSameCardsShouldNotReturn1() {
        Card52 firstCard52 = new Card52(Suit.SPADE, Rank.TWO);
        Card52 sameCard52 = new Card52(Suit.SPADE, Rank.TWO);
        assertNotEquals(firstCard52.compareTo(sameCard52), 1);
    }

    @Test
    void compareCardsShouldReturn1() {
        Card52 firstCard52 = new Card52(Suit.SPADE, Rank.TWO);
        Card52 secondCard52 = new Card52(Suit.SPADE, Rank.THREE);
        assertEquals(firstCard52.compareTo(secondCard52), 1);
    }

    @Test
    void compareSameCardsShouldReturnError() {
        Card52 firstCard52 = new Card52(Suit.SPADE, Rank.TWO);
        assertThrows(Exception.class, () -> firstCard52.compareTo(new Card52(null, null)), "Both suit and rank must be defined."
        );
    }
}