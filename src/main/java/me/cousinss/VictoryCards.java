package me.cousinss;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class VictoryCards {
    public enum Card {
        LONGEST_ROAD,
        LARGEST_ARMY;
    }

    private final static Map<Card, Function<Game, Player>> ASSESS_FUNCTIONS;
    static {
        ASSESS_FUNCTIONS = new HashMap<>();
        ASSESS_FUNCTIONS.put(Card.LONGEST_ROAD, (Game g) -> {
            Player holder = g.getVictoryCards().query(Card.LONGEST_ROAD);
            int toBeat = holder == null ? 5 : g.getLongestRoad(holder).size();
            for(Player p : g.getPlayers()) {
                if(p == holder) continue;
                if(g.getLongestRoad(p).size() > toBeat) {
                    toBeat = g.getLongestRoad(p).size();
                    holder = p;
                }
            }
            return holder;
        });
        ASSESS_FUNCTIONS.put(Card.LARGEST_ARMY, (Game g) -> {
            Player holder = g.getVictoryCards().query(Card.LARGEST_ARMY);
            int toBeat = holder == null ? 5 : g.getPlayers().stream().map(Player::countPlayedKnightCards).max(Integer::compareTo).orElse(3);
            for(Player p : g.getPlayers()) {
                if(p == holder) continue;
                if(p.countPlayedKnightCards() > toBeat) {
                    toBeat = p.countPlayedKnightCards();
                    holder = p;
                }
            }
            return holder;
        });

        for(Card card : Card.values()) {
            if(!ASSESS_FUNCTIONS.containsKey(card)) {
                throw new IllegalStateException();
            }
        }
    }

    private final Map<Card, Player> holderMap;
    private final Game game;

    public VictoryCards(Game game) {
        holderMap = new HashMap<>();
        this.game = game;
    }

    public Player assess(Card card) {
        this.holderMap.put(card, ASSESS_FUNCTIONS.get(card).apply(game));
        return this.holderMap.get(card);
    }

    public Player query(Card card) {
        return holderMap.get(card);
    }

}
