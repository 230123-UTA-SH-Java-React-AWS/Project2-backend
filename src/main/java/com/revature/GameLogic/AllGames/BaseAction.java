package com.revature.GameLogic.AllGames;

public abstract class BaseAction {
    public final String actionVerb;
    public final String additionalInfo;

    public BaseAction(String actionVerb, String additionalInfo) {
        this.actionVerb = actionVerb;
        this.additionalInfo = additionalInfo;
    }
}
