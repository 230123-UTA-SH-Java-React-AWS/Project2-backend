package com.revature.GameLogic.Blackjack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.revature.CardLogic.Deck52;

import com.revature.CardLogic.MultiDeck52;
import com.revature.GameLogic.AllGames.BaseGame;

public class BlackjackGame extends BaseGame<BlackjackPlayer> {
    Random rand = new Random(); //This should be removed later when the player names are refactored.
    Deck52 deck;
    //The dealer always exists and their cards are what gets compared against the players' cards.
    BlackjackPlayer dealer = new BlackjackPlayer();
    
    public BlackjackGame(String gameName, boolean isPrivateGame) {
        super(gameName, isPrivateGame, 6);
        gameType = GameType.BLACKJACK;
    }

    public void dealHands(){
        deck = new MultiDeck52(6);
        dealer.push(deck.deal());
        for(BlackjackPlayer p : activePlayers){
            p.push(deck.deal(2));
        }
    }

    public void onGameStateChange(){
        //Create the new game state,
        //For blackjack, we only have to do this once because everyone has the same information.
        List<BlackjackClientGameState.BlackjackPlayerInfo> playerInfo = new ArrayList<>();
        for(BlackjackPlayer p : activePlayers){
            playerInfo.add(new BlackjackClientGameState.BlackjackPlayerInfo(
                p.getEndGameState(),
                //TODO: Instead of a "player name", this should be changed to a unique identifier that allows the frontend to
                // retreive player info from the database.
                "Jimothy" + rand.nextInt(),
                p.isTurnEnded(),
                p.getHand().getCards()));
        }
        BlackjackClientGameState gameState = new BlackjackClientGameState(dealer.getHand().getCards(), playerInfo);

        for(BlackjackPlayer p : activePlayers){
            p.setClientGameState(gameState); //Update everyone's game state
            p.sendState(); //Send the new game state to all connected clients
        }
    }

    /**
     * This method handles the logic associated with ending the game -- specifically, this means handling the
     *  dealer's turn. THe general flow of this function is as follows:
     *  - Do nothing unless all players have completed their turns
     *  - The dealer can take their cards, updating the players each time they do so
     *  - Winners and losers can be determined.
     */
    public void onPlayerEndsTurn() {
        //If all players have not yet finished their turn, we wait until they do.
        for (BlackjackPlayer blackjackPlayer : activePlayers) {
            if(!blackjackPlayer.isTurnEnded()) return;
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
            if(dealerHand.getHandValue() == 17 && !dealerHand.isSoftHand()) {
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
                    if (dealerHand.isBustedOut()) {
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
            player.setTurnEnded(false);
        }
        //TODO: ASK PLAYERS TO PLACE A BET OR LEAVE THE TABLE NOW. IF THEY LEAVE THE TABLE, ADMIT SOMEONE NEW IN THEIR PLACE IF POSSIBLE.

        //This runs once everyone is connected so that everyone sees the new state of the table.
        onGameStateChange();
    }

    //What happens when a player leaves the game via disconnection?
    // Ideal case: the player withdraws and loses any money the
    public void dropPlayer(String playerId){
        BlackjackPlayer player = getActivePlayerByUrlSuffix(playerId);
        if (player == null) return;
        if(activePlayers.remove(player)) {
            onPlayerEndsTurn(); //A player leaving counts as ending their turn.
        }
    }

    public void onPlayerHit(String playerId){
        BlackjackPlayer player = getActivePlayerByUrlSuffix(playerId);
        if (player == null) return;
        //Deal a card unless the player has blackjack, busted out, or has already opted to stand.
        // This can all be determined with the hasEndedTurn boolean because that is kept current with those actions.
        if(!player.isTurnEnded()){
            player.push(deck.deal());
            onGameStateChange();
        }
        if(player.getHand().getHandValue() >= 21) {
            player.setTurnEnded(true);
            onPlayerEndsTurn();
        }
    }

    public void onPlayerStand(String playerId){
        BlackjackPlayer player = getActivePlayerByUrlSuffix(playerId);
        if (player == null) return;
        //Simply notify that the player is done taking their turn.
        if(!player.isTurnEnded()){
            player.setTurnEnded(true);
            onGameStateChange();
            onPlayerEndsTurn();
        }
    }
}
