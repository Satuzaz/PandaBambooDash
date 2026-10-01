public final class Bamboo extends Item {
    public Bamboo(int x, int y, Assets assets) { super(x, y, 26, 38, assets.get("item/bamboo.png")); }
    @Override protected void onCollect(Player player) { player.collectBamboo(); }
}
