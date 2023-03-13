package com.revature.GameLogic.Blackjack;

import com.revature.CardLogic.Deck52;

import com.revature.CardLogic.MultiDeck52;
import com.revature.GameLogic.AllGames.BaseGame;

public class BlackjackGame extends BaseGame<BlackjackPlayer> {
    Deck52 deck;
    //The dealer always exists and their cards are what gets compared against the players' cards.
    BlackjackPlayer dealer = new BlackjackPlayer("");

    public BlackjackGame() {
        super(6);
    }

    public void dealHands(){
        deck = new MultiDeck52(6);
        dealer.push(deck.deal());
        for(BlackjackPlayer p : activePlayers){
            p.push(deck.deal(2));
        }
    }

    public void onGameStateChange(){
        //Create new game states for each client.


        //Send the new game state to all connected clients
        for(BlackjackPlayer p : activePlayers){
            p.sendState();
        }
    }

    /**
     * This method handles the logic associated with ending the game -- specifically, this means handling the
     *  dealer's turn. THe general flow of this function is as follows:
     *  - Do nothing unless all players have completed their turns
     */
    public void onPlayerEndsTurn() {
        //If all players have not yet finished their turn, we wait until they do.
        for (BlackjackPlayer blackjackPlayer : activePlayers) {
            if(!blackjackPlayer.getHasEndedTurn()) return;
        }
        /*
         * All the players have taken their turn, now it is time for the dealer to have theirs.
         * For now, I am ignoring the rule of "insurance". If the dealer has an ace in hand, it
         *  is really unfortunate for the players.
         * Rules for the dealer:
         *  - Dealer takes a card if they have less than 17 in hand, or if they have a 17 and a soft hand.
         *  - Once they are done taking cards, the winner is determined and payouts are made.
         */
        BlackjackHand dealerHand = dealer.getHand();
        while(dealerHand.getHandValue() <= 17) {
            if(dealerHand.getHandValue() == 17 && !dealerHand.getIsSoftHand()) {
                break; //Dealer stops on a 17 that is not a soft hand.
            }
            dealer.push(deck.deal());
            onGameStateChange();
        }

        //The dealer has taken all the cards they can, now we determine winners/losers
        for (BlackjackPlayer player: activePlayers) {
            BlackjackHand playerHand = player.getHand();
            if (playerHand.getHandValue() > 21) {
                //If the player busts, they automatically lose regardless of what the dealer has
                player.setEndGameState(BlackjackPlayer.EndGameStates.IS_BUSTED);
            } else {
                //If the player and dealer have the same amount (including 21), they tie
                if (playerHand.getHandValue() == dealerHand.getHandValue()) {
                    player.setEndGameState(BlackjackPlayer.EndGameStates.TIED_DEALER);
                } else {
                    //If the dealer busts, the player wins.
                    if (dealerHand.getHasBusted()) {
                        player.setEndGameState(BlackjackPlayer.EndGameStates.DEALER_BUSTED);
                    } else {
                        //If the player has a larger hand than the dealer, they win
                        if (playerHand.getHandValue() > dealerHand.getHandValue()) {
                            if (playerHand.getHandValue() == 21) {
                                //Blackjack!
                                player.setEndGameState(BlackjackPlayer.EndGameStates.BLACKJACK);
                            } else {
                                //Not a blackjack but still winning
                                player.setEndGameState(BlackjackPlayer.EndGameStates.BEAT_DEALER);
                            }
                        } else {
                            //The player has a smaller hand than the dealer, and the dealer did not go over 21. The player loses.
                            player.setEndGameState(BlackjackPlayer.EndGameStates.LOST_TO_DEALER);
                        }
                    }
                }
            }
            onGameStateChange();
        }

        //Now that the winners/losers have been determined, admitPlayers() should run again
        // which begins a new round.
        admitPlayers();
    }

    @Override
    protected void admitPlayers() {
        super.admitPlayers();
        for (BlackjackPlayer player : activePlayers
             ) {
            player.setHasEndedTurn(false);
        }
        //TODO: ASK PLAYERS TO PLACE A BET OR LEAVE THE TABLE NOW. IF THEY LEAVE THE TABLE, ADMIT SOMEONE NEW IN THEIR PLACE IF POSSIBLE.

        //This runs once everyone is connected so that everyone sees the new state of the table.
        onGameStateChange();
    }

    //What happens when a player leaves the game via disconnection?
    // Ideal case: the player withdraws and loses any money the
    public void dropPlayer(BlackjackPlayer player){
        if(activePlayers.remove(player)) {
            onPlayerEndsTurn(); //A player leaving counts as ending their turn.
        }
    }

    public void onPlayerHit(BlackjackPlayer p){
        //Deal a card unless the player has blackjack, busted out, or has already opted to stand.
        // This can all be determined with the hasEndedTurn boolean because that is kept current with those actions.
        if(!p.getHasEndedTurn()){
            p.push(deck.deal());
            onGameStateChange();
        }
        if(p.getHand().getHandValue() >= 21) {
            p.setHasEndedTurn(true);
            onPlayerEndsTurn();
        }
    }

    public void onPlayerStand(BlackjackPlayer p){
        //Simply notify that the player is done taking their turn.
        if(!p.getHasEndedTurn()){
            p.setHasEndedTurn(true);
            onGameStateChange();
            onPlayerEndsTurn();
        }
    }
}
