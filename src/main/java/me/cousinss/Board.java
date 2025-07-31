package me.cousinss;

import me.cousinss.geometry.TileCoordinate;

import java.util.HashMap;
import java.util.Map;

public class Board {
    private final Map<TileCoordinate, Tile> tileMap;

    public Board() {
        tileMap = new HashMap<>();
    }

    public Tile get(TileCoordinate coordinate) {
        return tileMap.get(coordinate);
    }

    public void set(TileCoordinate coordinate, Tile tile) {
        tileMap.put(coordinate, tile);
    }


}
