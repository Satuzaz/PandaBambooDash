public class Level2 extends Level {
    public Level2(Assets assets) {
        super(2, "Deep Forest", assets);
        platform(0, 530, 800, 70);
        platform(120, 440, 120, 20);
        platform(290, 350, 130, 20);
        platform(470, 260, 130, 20);
        platform(640, 350, 130, 20);
        bamboo(160, 398);
        bamboo(340, 308);
        bamboo(520, 218);
        bamboo(695, 308);
        bamboo(740, 488);
        snake(280, 500, 260, 590, 110);
        snake(490, 230, 470, 600, 65);
    }
}
