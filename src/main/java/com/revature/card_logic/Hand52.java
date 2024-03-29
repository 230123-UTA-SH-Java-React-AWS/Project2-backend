package com.revature.card_logic;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public class Hand52 {
    @Getter
    protected List<Card52> cards = new ArrayList<>();

    public Hand52(List<Card52> cards){
        push(cards);
    }

    public List<Card52> push(@NonNull Card52 card) {
        cards.add(card);
        return cards;
    }

    public List<Card52> push(@NonNull List<Card52> cardList){
        for(Card52 card: cardList){
            if(card != null) cards.add(card);
        }
        return cards;
    }

    public int size() { return cards.size(); }
    //Other operations on List<> (such as contains()) can be performed using getCards() and operations provided by List<>.

    @Override
    public String toString() {
        return "{" +
            " cards='" + getCards() + "'" +
            "}";
    }

}
