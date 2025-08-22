package me.cousinss;

import me.cousinss.geometry.EdgeCoordinate;
import me.cousinss.geometry.TileCoordinate;
import me.cousinss.geometry.VertexCoordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Game {
    private final List<Player> players;
    private final Board board;
    private final Developments developments;
    private Dice dice;
    private Player turnPlayer;
    private int turnNumber;
    private final VictoryCards victoryCards;
    private final List<DevelopmentCard> developmentDeck;
    private final ResourceSet resourceDecks;
    private Random random;

    public Game(PlayerMeta... players) {
        //TODO
        this.players = Arrays.stream(players).map(Player::new).collect(Collectors.toList());
        this.board = new Board();
        this.developments = new Developments();
        this.turnPlayer = this.players.getFirst();
        this.victoryCards = new VictoryCards(this);
        this.turnNumber = 0;
        this.developmentDeck = new ArrayList<>();
        this.resourceDecks = new ResourceSet();
        Arrays.stream(Resource.values()).forEach(resource -> resourceDecks.add(resource, 19));
    }

    public void setup(Random random) {
        board.initializeBoard(random);
        for(DevelopmentCard card : DevelopmentCard.values()) {
            for(int i = 0; i < card.getDeckCount(); i++) {
                developmentDeck.add(card);
            }
        }
        Collections.shuffle(developmentDeck, random);
        this.random = random;
    }

    public Board getBoard() {
        return board;
    }

    public Developments getDevelopments() {
        return developments;
    }

    public Dice getDice() {
        return dice;
    }

    public Dice rollDice() {
        return dice = new Dice(random);
    }

    public VictoryCards getVictoryCards() {
        return victoryCards;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public int advanceTurn() {
        return ++turnNumber;
    }

    public Player playerWithMeta(PlayerMeta meta) {
        return players.stream().filter(p -> p.getMeta().equals(meta)).findFirst().orElse(null);
    }

    public Player getTurnPlayer() {
        return turnPlayer;
    }

    public Player nextTurn() {
        return turnPlayer = players.get((players.indexOf(this.getTurnPlayer()) + 1) % players.size());
    }

    public Player playerById(int id) {
        return players.stream().filter(p -> p.getMeta().id() == id).findFirst().orElse(null);
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public int getScore(Player player) {
        return (int) (developments.getCities(player).size() * 2 + developments.getVillages(player).size()
                        + 2* Arrays.stream(VictoryCards.Card.values()).map(victoryCards::query).filter(player::equals).count());
    }

    public List<EdgeCoordinate> getLongestRoad(Player player) {
        return this.developments.getLongestRoad(player, this.getPlayers().stream().filter(p -> p != player).toList());
    }

    public Set<EdgeCoordinate> getAvailableRoadPlacements(Player player) {
        Set<EdgeCoordinate> placements = new HashSet<>();
        Set<EdgeCoordinate> roads = developments.getRoads(player);
        Set<VertexCoordinate> buildings = developments.getBuildings(player);

        for(VertexCoordinate building : buildings) {
            placements.addAll(building.getEdgeTriad());
        }
        for(EdgeCoordinate road : roads) {
            placements.addAll(road.getVertices().stream().filter(vc -> this.players.stream().noneMatch(pl -> developments.getBuildings(pl).contains(vc))).flatMap(vc -> vc.getEdgeTriad().stream()).collect(Collectors.toSet()));
        }

        Set<VertexCoordinate> vertices = board.getAllVertices();
        placements.removeIf(ec -> !vertices.containsAll(ec.getVertices()));
        players.forEach(p -> placements.removeIf(ec -> developments.getRoads(p).contains(ec)));

        return placements;
    }

    public Set<VertexCoordinate> getUnblockedVillagePlacements() {
        Set<VertexCoordinate> blocked = new HashSet<>();
        for(Player p : players) {
            for (VertexCoordinate building : developments.getBuildings(p)) {
                blocked.addAll(building.getEdgeTriad().stream().flatMap(ec -> ec.getVertices().stream()).collect(Collectors.toSet()));
            }
        }
        return board.getAllVertices().stream().filter(vc -> !blocked.contains(vc)).collect(Collectors.toSet());
    }

    public Set<VertexCoordinate> getLegalVillagePlacements(Player player) {
        Set<VertexCoordinate> roadConnected = developments.getRoads(player).stream().flatMap(ec -> ec.getVertices().stream()).collect(Collectors.toSet());
        roadConnected.retainAll(getUnblockedVillagePlacements());
        return roadConnected;
    }

    public Map<Player, Set<ResourceRollReceipt>> getResourceReceipts(int roll) {
        Map<Player, Set<ResourceRollReceipt>> receipts = new HashMap<>();
        for(Map.Entry<TileCoordinate, Tile> e : board.getWithRollValue(roll)) {
            for(Player player : players) {
                if(!receipts.containsKey(player)) {
                    receipts.put(player, new HashSet<>());
                }
                Set<ResourceRollReceipt> receiptSet = receipts.get(player);
                developments.getAdjacentVillages(e.getKey(), player).forEach(vc -> {
                    receiptSet.add(new ResourceRollReceipt(e.getKey(), vc, e.getValue().resource(), 1));
                });
                developments.getAdjacentCities(e.getKey(), player).forEach(vc -> {
                    receiptSet.add(new ResourceRollReceipt(e.getKey(), vc, e.getValue().resource(), 2));
                });
            }
        }
        return receipts;
    }

    public void addRollResources(Map<Player, Set<ResourceRollReceipt>> receipts) {
        for(Map.Entry<Player, Set<ResourceRollReceipt>> e : receipts.entrySet()) {
            for(ResourceRollReceipt receipt : e.getValue()) {
                e.getKey().getHand().add(receipt.resource(), receipt.count());
            }
        }
    }
}
