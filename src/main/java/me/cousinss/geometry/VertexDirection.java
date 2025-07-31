package me.cousinss.geometry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum VertexDirection implements HexDirection<VertexDirection> {
    UP(0, 0, true),
    UPRIGHT(0, 0, false),
    DOWNRIGHT(0, 1, true),
    DOWN(-1, 1, true),
    DOWNLEFT(-1, 1, false),
    UPLEFT(-1, 0, false);

    static final Map<VertexDirection, VertexCoordinate> DOMINANT_ADJUST;
    private final TileCoordinate dominantVector;
    private final boolean dominantDirIsUp;

    VertexDirection(int q, int r, boolean dominantDirIsUp) {
        this.dominantVector = new TileCoordinate(q, r);
        this.dominantDirIsUp = dominantDirIsUp;
    }

    static {
        Map<VertexDirection, VertexCoordinate> map = new HashMap<>();
        for(VertexDirection d : VertexDirection.values()) {
            map.put(d, new VertexCoordinate(d.dominantVector, d.dominantDirIsUp ? VertexDirection.UP : VertexDirection.UPRIGHT));
        }
        DOMINANT_ADJUST = Collections.unmodifiableMap(map);
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }

    @Override
    public VertexDirection[] valueArray() {
        return VertexDirection.values();
    }

    public VertexCoordinate getDominantAdjust() {
        return DOMINANT_ADJUST.get(this);
    }

    public boolean isDominant() {
        return DOMINANT_ADJUST.get(this).getTile().isOrigin();
    }
}
