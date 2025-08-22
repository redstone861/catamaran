package me.cousinss.geometry;

public interface HexDirection<T extends HexDirection<T>> {

    int NUM_SIDES = 6;

    int getOrdinal();
    T[] valueArray();
    HexCoordinate<T> getDominantAdjust();

    default int distance(HexDirection<T> d, boolean clockwise) {
        int dist = clockwise ? d.getOrdinal() - this.getOrdinal() : this.getOrdinal() - d.getOrdinal();
        return dist < 0 ? dist + 6 : dist;
    }

    default int distance(HexDirection<T> d) {
        return Math.min(distance(d, true), distance(d, false));
    }

    default T getOpposing() {
        return this.valueArray()[this.getOrdinal() > 2 ? this.getOrdinal() - 3 : this.getOrdinal() + 3];
    }

    default T turn(int turns, boolean clockwise) {
        int newPointId = (clockwise ? this.getOrdinal() + turns : this.getOrdinal() - turns) % NUM_SIDES;
        return valueArray()[newPointId < 0 ? newPointId + NUM_SIDES : newPointId];
    }
}
