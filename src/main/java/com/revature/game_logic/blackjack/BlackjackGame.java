package com.revature.game_logic.blackjack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import com.revature.card_logic.Deck52;
import com.revature.card_logic.MultiDeck52;
import com.revature.game_logic.blackjack.BlackjackPlayer.EndGameStates;
import com.revature.game_logic.common.BaseGame;

public class BlackjackGame extends BaseGame<BlackjackPlayer> {
    Random rand = new Random(); //This should be removed later when the player names are refactored.
    Deck52 deck;
    //The dealer always exists and their cards are what gets compared against the players' cards.
    BlackjackPlayer dealer = new BlackjackPlayer("Dealer");
    //This stores the current client game state that is what gets sent to users.
    BlackjackClientGameState gameState = null;
    
    public BlackjackGame(String gameName, boolean isPrivateGame) {
        super(gameName, isPrivateGame, 6);
        gameType = GameType.BLACKJACK;
    }

    public void dealHands(){
        if(isGameStarted) return;
        deck = new MultiDeck52(6);
        dealer = new BlackjackPlayer("Dealer");
        dealer.push(deck.deal());
        admitPlayers();
        for(BlackjackPlayer p : activePlayers){
            p.getHand().getCards().clear();
            p.setTurnEnded(false);
            p.push(deck.deal(2));
            p.setEndGameState(BlackjackPlayer.EndGameStates.STILL_PLAYING);
            p.setDoubledDown(false);
            //Special case: if the player is dealt a natural Blackjack their turn is immediately ended.
            if(p.getHand().getHandValue() == 21){
                p.setTurnEnded(true);
                //I do not set the end game state here because that is handled in onGameStateChange.
            }
        }
        onGameStateChange();
    }

    public void onGameStateChange(){
        updateGameState();

        for(BlackjackPlayer p : activePlayers){
            sendState(p);
        }

        updateWaitingPlayers();
    }
    
    public void sendState(String playerId) {
        BlackjackPlayer p = getActivePlayerByUrlSuffix(playerId);
        if(p == null) return;
        sendState(p);
    }

    public void sendState(BlackjackPlayer p){
        if(gameState == null) return;
        p.setClientGameState(gameState); //Update everyone's game state
        p.sendState(); //Send the new game state to all connected clients
    }

    private void updateGameState() {
        //Create the new game state,
        //For blackjack, we only have to do this once because everyone has the same information.
        List<BlackjackClientGameState.BlackjackPlayerInfo> playerInfo = new ArrayList<>();
        for(BlackjackPlayer p : activePlayers){
            playerInfo.add(new BlackjackClientGameState.BlackjackPlayerInfo(
                p.getEndGameState(),
                //TODO: Instead of a "player name", this should be changed to a unique identifier that allows the frontend to
                // retreive player info from the database.
                p.getPlayerName(),
                p.isTurnEnded(),
                p.getHand().getCards(),
                p.getHand().getHandValue(),
                Objects.equals(p.getPlayerId(), hostPlayer.getPlayerId()),
                p.isDoubledDown())
            );
        }
        gameState = new BlackjackClientGameState(dealer.getHand().getCards(), dealer.getHand().getHandValue(), playerInfo);
    }

    /**
     * This method handles the logic associated with ending the game -- specifically, this means handling the
     *  dealer's turn. The general flow of this function is as follows:
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
         * I am ignoring the rule of "insurance". If the dealer shows an ace in hand, it
         *  is just really unfortunate for the players.
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

        //Ther dealer has taken all of the cards they can, it is now time to determine winners/losers.
        //SonarLint gives this block of code 15 Cognitive Complexity, which suggests that it should be extracted to its
        // own function and called here. However, that allows the check for all players having ended their turns to
        // potentially be bypassed if this code were to be called outside of this function.
        //It simply makes more sense that the same function that checks for whether players have ended their turns should
        // also handle the logic for what happens after all the turns are ended.
        int dealerHandValue = dealerHand.getHandValue();
        for(BlackjackPlayer player : activePlayers){
            int playerHandValue = player.getHand().getHandValue();
            
            //It is acceptible for this to be null here because all possible end game states are checked for below.
            //However, Sonar Lint doesn't like it so I set this to STILL_PLAYING.
            EndGameStates endGameState = EndGameStates.STILL_PLAYING;

            if (playerHandValue > 21) {
                endGameState = EndGameStates.IS_BUSTED;
            } else if (playerHandValue == 21) {
                endGameState = EndGameStates.BLACKJACK;
                if(dealerHandValue == 21) { 
                    endGameState = EndGameStates.TIED_DEALER;
                }
            } else if (playerHandValue < 21){
                if (dealerHandValue <= 21 && playerHandValue > dealerHandValue){
                    endGameState = EndGameStates.BEAT_DEALER;
                } else if (dealerHandValue > 21){
                    endGameState = EndGameStates.DEALER_BUSTED;
                } else if (playerHandValue == dealerHandValue){
                    endGameState = EndGameStates.TIED_DEALER;
                } else {
                    endGameState = EndGameStates.LOST_TO_DEALER;
                }
            }

            player.setEndGameState(endGameState);
        }

        //Now that the winners/losers have been determined, the game is now ended and should be started again
        // either by a player or after some time automatically.
        isGameStarted = false;
        onGameStateChange();
    }

    //What happens when a player leaves the game via disconnection?
    // One solution: the player withdraws and loses any money they had bet that round.
    @Override
    public void dropPlayer(String playerId){
        if(Objects.equals(hostPlayer.getPlayerId(), playerId)) hostPlayer = null;
        //Dropping queued players first.
        super.dropPlayer(playerId);
        //Scanning for any active players that need to be removed
        BlackjackPlayer player = getActivePlayerByUrlSuffix(playerId);
        if (player == null) return;
        if(activePlayers.remove(player)) {
            chooseNewHost();
            onPlayerEndsTurn(); //A player leaving counts as ending their turn.
        }
        chooseNewHost();
    }

    //A player choosing to Hit means they are taking an additional card.
    //If they have Blackjack (their hand value = 21) or have busted out (hand value > 21), they
    // are prevented from taking any more cards.
    public void onPlayerHit(String playerId){
        if(!isGameStarted) return;
        BlackjackPlayer player = getActivePlayerByUrlSuffix(playerId);
        if (player == null) return;
        //Deal a card unless the player has blackjack, busted out, or has already opted to stand.
        // This can all be determined with the hasEndedTurn boolean because that is kept current with those actions.
        if(!player.isTurnEnded()){
            player.push(deck.deal());
            if(player.getHand().getHandValue() >= 21) {
                player.setTurnEnded(true);
                onPlayerEndsTurn();
            }
            onGameStateChange();
        }
    }

    //A player choosing to Stand means they are no longer taking cards.
    // From the perspective of the game state, this simply ends their turn.
    public void onPlayerStand(String playerId){
        if(!isGameStarted) return;
        BlackjackPlayer player = getActivePlayerByUrlSuffix(playerId);
        if (player == null) return;
        //Simply notify that the player is done taking their turn.
        if(!player.isTurnEnded()){
            player.setTurnEnded(true);
            onGameStateChange();
            onPlayerEndsTurn();
        }
    }

    //A player choosing to Double Down means they are taking one more card, doubling their bet
    // (bets are NOT handled by this game), and standing immediately afterwards regardless of hand value.
    public void onPlayerDoubleDown(String playerId){
        if(!isGameStarted) return;
        BlackjackPlayer player = getActivePlayerByUrlSuffix(playerId);
        if (player == null) return;
        //Deal a card unless the player has blackjack, busted out, or has already opted to stand.
        // This can all be determined with the hasEndedTurn boolean because that is kept current with those actions.
        if(!player.isTurnEnded()){
            player.push(deck.deal());
            player.setDoubledDown(true);
            player.setTurnEnded(true);
            onPlayerEndsTurn();
            onGameStateChange();
        }
    }
}
