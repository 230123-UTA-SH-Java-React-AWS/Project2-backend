package com.revature.CardLogic;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.revature.CardLogic.Card52.Rank;
import com.revature.CardLogic.Card52.Suit;

public class Hand52Test {
    @Test
    void testPushCard() {
        Card52 aceOfSpades = new Card52(Suit.CLUB, Rank.ACE);
        List<Card52> expectedList = new ArrayList<Card52>();
        expectedList.add(aceOfSpades);

        Hand52 hand52 = new Hand52();
        List<Card52> actualList = hand52.push(aceOfSpades);

        assertArrayEquals(expectedList.toArray(), actualList.toArray());
    }

    @Test
    void testPushCardList() {
        Card52 aceOfSpades = new Card52(Suit.CLUB, Rank.ACE);
        Card52 aceOfHearts = new Card52(Suit.HEART, Rank.ACE);
        List<Card52> aceList = new ArrayList<Card52>();
        aceList.add(aceOfSpades);
        aceList.add(aceOfHearts);

        List<Card52> expectedList = new ArrayList<Card52>();
        expectedList.addAll(aceList);

        Hand52 hand52 = new Hand52();
        List<Card52> actualList = hand52.push(aceList);
    
        assertArrayEquals(expectedList.toArray(), actualList.toArray());
    }
}
