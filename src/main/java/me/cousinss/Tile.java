package me.cousinss;

import me.cousinss.geometry.TileCoordinate;

public record Tile(TileCoordinate coordinate, Resource resource, int rollValue) { }
