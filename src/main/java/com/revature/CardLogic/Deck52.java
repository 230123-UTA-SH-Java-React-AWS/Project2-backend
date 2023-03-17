package com.revature.CardLogic;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class Deck52 {
    protected List<Card52> deck = new ArrayList<>();

    //All decks are generated with 52 cards, one for each combination of rank and suit.
    public Deck52(){
        for(Card52.Suit s : Card52.Suit.values()){
			for(Card52.Rank r : Card52.Rank.values()){
				deck.add(new Card52(s, r));
			}
		}
    }

    //Shuffles all the cards in the deck.
    //This returns this Deck52 instance so that a deck instance can be created by doing [new Deck52.shuffle]
    public Deck52 shuffle(){
        Collections.shuffle(deck);
        return this;
    }

    //Get the number of cards remaining in the deck.
    public int size(){
        return deck.size();
    }

    //Shorthand function that can be used to quickly determine if peek() and pop() are safe to execute.
    public boolean hasNext() {
        return !deck.isEmpty();
    }

    //Show the next card in the deck without removing it. 
    public Card52 peek() throws IndexOutOfBoundsException {
        if(deck.isEmpty()) throw new IndexOutOfBoundsException("There are no more cards left to take.");
        return deck.get(deck.size() - 1); //Index is chosen to avoid many index reassignments each time a card is taken
    }

    //Remove the next card from the deck and return it.
    //Throws IndexOutOfBoundsException if there are no cards left in the deck to deal.
    public Card52 deal() throws IndexOutOfBoundsException {
        if(deck.isEmpty()) throw new IndexOutOfBoundsException("There are no more cards left to take.");
        return deck.remove(deck.size() - 1); //Index is chosen to avoid many index reassignments each time a card is taken
    }

    /**
     * Allows you to deal a set number of cards from the deck at once.
     * @param number The number of cards to deal
     * @return a List containing the cards that were dealt
     * @throws IndexOutOfBoundsException If the number of cards to deal is larger than the amount left in the deck.
     */
    public List<Card52> deal(int number) throws IndexOutOfBoundsException {
        if(number > deck.size()) throw new IndexOutOfBoundsException("There aren't enough cards left to deal " + number + '!');
        List<Card52> result = new ArrayList<>();

        //SonarLint doesn't like the deck.remove right below this because it is inside an ascending for loop.
        //That warning can be safely ignored for this method.
        for(int i = 0; i < number; i++) {
            result.add(deck.remove(deck.size() - 1));
        }

        return result;
    }

    //Used to remove a specific card from the deck. Returns a boolean indicating whether the card was successfully removed.
    //This is intended to be used to pull a specific card out of the deck.
    //May be used before or after the deck is shuffled (or even if it never is).
    public boolean remove(Card52 card){
        return deck.remove(card);
    }

    @Override
    public String toString() {
        return "{" +
            " deck='" + deck + "'" +
            "}";
    }
    
}
