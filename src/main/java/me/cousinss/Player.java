package me.cousinss;

public class Player {

    private final PlayerMeta meta;
    private final ResourceSet hand;

    public Player(PlayerMeta meta) {
        this.hand = new ResourceSet();
        this.meta = meta;
    }

    public ResourceSet getHand() {
        return this.hand;
    }

    public PlayerMeta getMeta() {
        return this.meta;
    }
}
