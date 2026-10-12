public class Level3 extends Level {
    public Level3(Assets assets) {
        super(3, "Hidden Cave", assets);
        platform(0, 530, 230, 70);
        platform(330, 530, 180, 70);
        platform(620, 530, 180, 70);
        platform(130, 435, 100, 20);
        platform(275, 345, 100, 20);
        platform(430, 255, 100, 20);
        platform(595, 345, 100, 20);
        bamboo(165, 393);
        bamboo(310, 303);
        bamboo(465, 213);
        bamboo(630, 303);
        bamboo(750, 488);
        snake(350, 500, 330, 510, 140);
        snake(640, 500, 620, 800, 155);
        snake(440, 225, 430, 530, 75);
    }
}
