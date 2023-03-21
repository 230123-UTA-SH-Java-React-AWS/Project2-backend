package com.revature.project2backend.cardlogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.CardLogic.Card52;
import com.revature.CardLogic.Hand52;
import com.revature.CardLogic.Card52.Rank;
import com.revature.CardLogic.Card52.Suit;


public class HandLogicTest {
    Hand52 hand;

    @BeforeEach
    void setUp() {
        hand = new Hand52();
    }
    
    @Test
    void handShouldReturn4SKC() {
        Card52 fourOfSpades = new Card52(Suit.SPADE, Rank.FOUR);
        Card52 kingOfClubs = new Card52(Suit.CLUB, Rank.KING);
        hand.push(fourOfSpades);
        hand.push(kingOfClubs);
        assertEquals(hand.toString(), "{" +
        " cards='" + "[[4S], [KC]]" + "'" +
        "}");
    }

    @Test
    void handSizeShouldReturn0() {
        assertEquals(hand.size(), 0);
    }

    @Test
    void handSizeShouldNotReturn0() {
        Card52 aceOfHearts = new Card52(Suit.HEART, Rank.ACE);
        hand.push(aceOfHearts);
        assertNotEquals(hand.size(), 0);
    }

    @Test
    void handSizeShouldReturn3() {
        Card52 firstCard52 = new Card52(Suit.SPADE, Rank.TWO);
        Card52 secondCard52 = new Card52(Suit.SPADE, Rank.TWO);
        Card52 thirdCard52 = new Card52(Suit.HEART, Rank.ACE);
        hand.push(firstCard52);
        hand.push(secondCard52);
        hand.push(thirdCard52);
        assertEquals(hand.size(), 3);
    }

    @Test
    void pushNullCardShouldReturnError() {
        assertThrows(Exception.class, () -> hand.push(new Card52(null, null)), "Both suit and rank must be defined."
        );
    }
}