package me.cousinss;

public enum DevelopmentCard {

    KNIGHT(14, Behavior.KNIGHT),
    LIBRARY(1, Behavior.VICTORY),
    MARKET(1, Behavior.VICTORY),
    CHAPEL(1, Behavior.VICTORY),
    GREAT_HALL(1, Behavior.VICTORY),
    UNIVERSITY(1, Behavior.VICTORY),
    ROAD_BUILDING(2, Behavior.PROGRESS),
    MONOPOLY(2, Behavior.PROGRESS),
    YEAR_OF_PLENTY(2, Behavior.PROGRESS);

    private final int deckCount;
    private final Behavior behavior;

    DevelopmentCard(int deckCount, Behavior behavior) {
        this.deckCount = deckCount;
        this.behavior = behavior;
    }

    public int getDeckCount() {
        return deckCount;
    }

    public Behavior getBehavior() {
        return behavior;
    }

    public enum Behavior {
        KNIGHT,
        VICTORY,
        PROGRESS
    }
}
