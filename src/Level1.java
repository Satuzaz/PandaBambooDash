public class Level1 extends Level {
    public Level1(Assets assets) {
        super(1, "Bamboo Forest", assets);
        platform(0, 530, 800, 70);
        platform(180, 435, 150, 20);
        platform(410, 345, 150, 20);
        bamboo(110, 488);
        bamboo(240, 393);
        bamboo(470, 303);
        bamboo(630, 488);
        bamboo(740, 488);
        snake(390, 500, 350, 720, 75);
    }
}
