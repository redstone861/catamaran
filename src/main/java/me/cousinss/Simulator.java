package me.cousinss;

import me.cousinss.geometry.EdgeCoordinate;
import me.cousinss.geometry.VertexCoordinate;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Simulator {
    private final Game game;
    private final Random random;

    public Simulator() {
        random = new Random(0);
        game = new Game(
                new PlayerMeta("CJ", me.cousinss.Color.BLUE, 0),
                new PlayerMeta("Zaddy", me.cousinss.Color.GREEN, 1),
                new PlayerMeta("Ishmael", me.cousinss.Color.RED, 2));

        Player cj = game.playerById(0);
        Player zaddy = game.playerById(1);
        Player ishmael = game.playerById(2);

        game.setup(random);

        final Board b = game.getBoard();
        Comparator<VertexCoordinate> vComp = (v1, v2) -> 1000*(b.getVertexRollDotsSum(v1) - b.getVertexRollDotsSum(v2)) + 10 * (v1.getTile().q() - v2.getTile().q()) + (v1.getTile().r() - v2.getTile().r());
        game.getDevelopments().addVillage(cj, game.getUnblockedVillagePlacements().stream().max(vComp).orElse(null));
        game.getDevelopments().addVillage(zaddy, game.getUnblockedVillagePlacements().stream().max(vComp).orElse(null));
        game.getDevelopments().addVillage(ishmael, game.getUnblockedVillagePlacements().stream().max(vComp).orElse(null));
        game.getDevelopments().addVillage(ishmael, game.getUnblockedVillagePlacements().stream().max(vComp).orElse(null));
        game.getDevelopments().addVillage(zaddy, game.getUnblockedVillagePlacements().stream().max(vComp).orElse(null));
        game.getDevelopments().addVillage(cj, game.getUnblockedVillagePlacements().stream().max(vComp).orElse(null));

        for(Player p : game.getPlayers()) {
            for(VertexCoordinate village : game.getDevelopments().getVillages(p)) {
                game.getDevelopments().addRoad(p, random(village.getEdgeTriad().stream().filter(ec -> ec.getAdjacentTiles().stream().anyMatch(game.getBoard()::hasTile)).collect(Collectors.toList())));
            }
        }
    }

    private <E> E random(Collection<E> set) {
        return set.stream().skip(random.nextInt(set.size())).findFirst().orElse(null);
    }

    public Game getGame() {
        return game;
    }

    public void advance() {
        Dice dice = game.rollDice();
        Map<Player, Set<ResourceRollReceipt>> receipts = game.getResourceReceipts(dice.sum());
        game.addRollResources(receipts);
        for(Player p : game.getPlayers()) {
            List<Development> developments = new java.util.ArrayList<>(List.of(Development.values()));
            Collections.reverse(developments);
            boolean built;
            boolean anyBuilt;
            do {
                anyBuilt = false;
                for(Development dev : developments) {
                    built = false;
                    if(dev.canBuild(p.getHand())) {
                        switch(dev) {
                            case CITY:
                                if(!game.getDevelopments().getVillages(p).isEmpty()) {
                                    game.getDevelopments().upgradeToCity(p, random(game.getDevelopments().getVillages(p)));
                                    built = true;
                                }
                                break;
                            case VILLAGE:
                                Set<VertexCoordinate> vPlacements = game.getLegalVillagePlacements(p);
                                if(!vPlacements.isEmpty()) {
                                    game.getDevelopments().addVillage(p, random(vPlacements));
                                    built = true;
                                }
                                break;
                            case ROAD:
                                Set<EdgeCoordinate> rPlacements = game.getAvailableRoadPlacements(p);
                                if(!rPlacements.isEmpty()) {
                                    game.getDevelopments().addRoad(p, random(rPlacements));
                                    game.getVictoryCards().assess(VictoryCards.Card.LONGEST_ROAD);
                                    built = true;
                                }
                                break;
                        }
                        if(built) {
                            p.getHand().remove(dev.cost);
                            System.out.println(p.getMeta().name() + " built a " + dev.name() + ".");
                            anyBuilt = true;
                        }
                    }
                }
            } while(anyBuilt);
        }
    }
}
