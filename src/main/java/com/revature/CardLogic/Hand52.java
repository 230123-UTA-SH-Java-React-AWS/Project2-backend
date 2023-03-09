package com.revature.CardLogic;

import java.util.ArrayList;
import java.util.List;

public class Hand52 {
    protected List<Card52> cards = new ArrayList<>();

    public Hand52(){ }

    public Hand52(List<Card52> cards){
        push(cards);
    }

    public List<Card52> getCards() { return cards; }

    public List<Card52> push(Card52 card) {
        if(card != null) cards.add(card);
        return cards;
    }

    public List<Card52> push(List<Card52> cardList){
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
