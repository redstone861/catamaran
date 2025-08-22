package me.cousinss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {

    private final PlayerMeta meta;
    private final ResourceSet hand;
    private final Map<Integer, DevelopmentCard> developmentCards;
    private final List<DevelopmentCard> playedDevelopmentCards;

    public Player(PlayerMeta meta) {
        this.hand = new ResourceSet();
        this.meta = meta;
        this.developmentCards = new HashMap<>();
        this.playedDevelopmentCards = new ArrayList<>();
    }

    public ResourceSet getHand() {
        return this.hand;
    }

    public PlayerMeta getMeta() {
        return this.meta;
    }

    public List<DevelopmentCard> getNewDevelopmentCards(int turn) {
        return developmentCards.entrySet().stream().filter(e -> e.getKey() == turn).map(Map.Entry::getValue).toList();
    }

    public List<DevelopmentCard> getOldDevelopmentCards(int turn) {
        return developmentCards.entrySet().stream().filter(e -> e.getKey() != turn).map(Map.Entry::getValue).toList();
    }

    public int countVictoryCards() {
        return (int) developmentCards.values().stream().filter(d -> d.getBehavior().equals(DevelopmentCard.Behavior.VICTORY)).count();
    }

    public boolean playDevelopmentCard(DevelopmentCard developmentCard) {
        if(!this.developmentCards.containsValue(developmentCard)) return false;
        this.playedDevelopmentCards.add(developmentCard);
        return null != this.developmentCards.remove(developmentCards.entrySet().stream().filter(e -> e.getValue().equals(developmentCard)).findAny().orElseThrow().getKey());
    }

    public int countPlayedKnightCards() {
        return (int) playedDevelopmentCards.stream().filter(c -> c.getBehavior().equals(DevelopmentCard.Behavior.KNIGHT)).count();
    }

    @Override
    public String toString() {
        return this.meta.name() + "#" + this.meta.id() + ":" + this.meta.color().name().charAt(0) + " with hand: " + this.hand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if( !(o instanceof Player p)) return false;
        return this.meta.id() == p.meta.id();
    }

    @Override
    public int hashCode() {
        return this.meta.id();
    }
}
