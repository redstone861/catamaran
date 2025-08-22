package me.cousinss;

import me.cousinss.geometry.*;

import java.util.*;
import java.util.stream.Collectors;

public class Board {

    public static final Map<Resource, Integer> RESOURCE_TILE_COUNT;
    public static final List<Integer> ROLL_VALUES;
    public static final List<TileCoordinate> TILE_SPIRAL;
    static {
        Map<Resource, Integer> mapR = new HashMap<>();
        mapR.put(Resource.WOOD, 4);
        mapR.put(Resource.SHEEP, 4);
        mapR.put(Resource.WHEAT, 4);
        mapR.put(Resource.BRICK, 3);
        mapR.put(Resource.ORE, 3);
        mapR.put(null, 1);
        RESOURCE_TILE_COUNT = Collections.unmodifiableMap(mapR);

        ROLL_VALUES = List.of(5, 2, 6, 3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11);

        List<TileCoordinate> tS = new ArrayList<>();
        for(int i = 2; i >= 0; i--) {
            tS.addAll(TileCoordinate.ring(i));
        }
        TILE_SPIRAL = Collections.unmodifiableList(tS);
    }

    public static int coordinateID(TileCoordinate coordinate) {
        return TILE_SPIRAL.indexOf(coordinate);
    }

    public int getVertexRollDotsSum(VertexCoordinate vertex) {
        return vertex.getEdgeTriad().stream().flatMap(ec -> ec.getAdjacentTiles().stream()).collect(Collectors.toSet()).stream().map(tc -> tileMap.getOrDefault(tc, new Tile(null, 0))).map(Tile::rollValue).map(Dice::getRollDots).reduce(0, Integer::sum);
    }

    private final Map<TileCoordinate, Tile> tileMap;

    public Board() {
        tileMap = new HashMap<>();
    }

    public void initializeBoard(Random random) {
        this.tileMap.clear();
        List<Resource> tileShuffle = new ArrayList<>();
        for(Resource r : Resource.values()) {
            for(int i = 0; i < RESOURCE_TILE_COUNT.get(r); i++) {
                tileShuffle.add(r);
            }
        }
        tileShuffle.add(null);
        Collections.shuffle(tileShuffle, random);
        int spins = random.nextInt(6);
        List<TileCoordinate> tileSpiral = new ArrayList<>(TILE_SPIRAL.stream().map(tc -> tc.rotateAboutOrigin(spins, true)).toList());
        boolean hitDesert = false;
        for(int i = 0; i < tileSpiral.size(); i++) {
            TileCoordinate coordinate = tileSpiral.get(i);
            Resource resource = tileShuffle.get(i);
            int roll;
            if(resource != null) {
                roll = ROLL_VALUES.get(hitDesert ? i - 1 : i);
            } else {
                roll = 0;
                hitDesert = true;
            }
            Tile tile = new Tile(resource, roll);
            tileMap.put(coordinate, tile);
        }
    }

    public boolean hasTile(TileCoordinate coordinate) {
        return tileMap.containsKey(coordinate);
    }

    public Tile get(TileCoordinate coordinate) {
        return tileMap.get(coordinate);
    }

    public void set(TileCoordinate coordinate, Tile tile) {
        tileMap.put(coordinate, tile);
    }

    public Set<Map.Entry<TileCoordinate, Tile>> getTileSet() {
        return this.tileMap.entrySet();
    }

    public Set<VertexCoordinate> getAllVertices() {
        return this.tileMap.keySet().stream().flatMap((tc) -> tc.vertices().stream()).collect(Collectors.toSet());
    }

    public Set<Map.Entry<TileCoordinate, Tile>> getWithRollValue(int roll) {
        return this.tileMap.entrySet().stream().filter(e -> e.getValue().rollValue() == roll).collect(Collectors.toSet());
    }

}
