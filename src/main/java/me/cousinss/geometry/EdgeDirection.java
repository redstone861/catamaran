package me.cousinss.geometry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum EdgeDirection implements HexDirection<EdgeDirection> {
    UPRIGHT(1, -1, true),
    RIGHT(1, 0, true),
    DOWNRIGHT(0, 1, false),
    DOWNLEFT(-1, 1, false),
    LEFT(-1, 0, false),
    UPLEFT(0, -1, false);

    private final boolean dominant;
    private final TileCoordinate coordinateVector;
    private static final Map<EdgeDirection, EdgeCoordinate> DOMINANT_ADJUST;

    EdgeDirection(final int q, final int r, boolean dominant) {
        this.dominant = dominant;
        this.coordinateVector = new TileCoordinate(q, r);
    }

    static {
        Map<EdgeDirection, EdgeCoordinate> map = new HashMap<>();
        for(EdgeDirection d : EdgeDirection.values()) {
            map.put(d, new EdgeCoordinate(
                    d.dominant ? TileCoordinate.origin()    : new TileCoordinate(d.coordinateVector.q(), d.coordinateVector.r()),
                    d.dominant ? d                          : d.getOpposing()));
        }
        DOMINANT_ADJUST = Collections.unmodifiableMap(map);
    }

    public TileCoordinate getCoordinateVector() {
        return coordinateVector;
    }

    public EdgeDirection getOpposing() {
        return EdgeDirection.values()[this.ordinal() > 2 ? this.ordinal() - 3 : this.ordinal() + 3];
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }

    @Override
    public EdgeDirection[] valueArray() {
        return EdgeDirection.values();
    }

    public HexCoordinate<EdgeDirection> getDominantAdjust() {
        return DOMINANT_ADJUST.get(this);
    }
}
