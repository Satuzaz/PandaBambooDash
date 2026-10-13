import java.awt.image.BufferedImage;

// แอนิเมชันคือการเปลี่ยนรูปไปเรื่อย ๆ เมื่อครบเวลาที่กำหนด
public class Animation {
    private BufferedImage[] pictures;
    private int pictureNumber = 0;
    private double timer = 0;
    private double secondsPerPicture;
    private boolean repeat;

    public Animation(BufferedImage[] pictures, double secondsPerPicture, boolean repeat) {
        this.pictures = pictures;
        this.secondsPerPicture = secondsPerPicture;
        this.repeat = repeat;
    }

    public void reset() {
        pictureNumber = 0;
        timer = 0;
    }

    public void update(double seconds) {
        timer = timer + seconds;
        if (timer >= secondsPerPicture) {
            timer = timer - secondsPerPicture;
            if (pictureNumber < pictures.length - 1) {
                pictureNumber++;
            } else if (repeat) {
                pictureNumber = 0;
            }
        }
    }

    public BufferedImage getFrame() {
        return pictures[pictureNumber];
    }
}
