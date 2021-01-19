package code;

/**
 * 自機ノード
 * 
 * @author HAN
 * @see MyPlaneBulletList
 * @see GameReport.MyJPanel#myPlane_bullet
 */
public class MyPlaneBulletNode {
    public MyPlaneBulletNode next;
    public int tama_x, tama_y;
    public int tama_speed;
    public int type;
    public double degree;

    /**
     * @param x x座標
     * @param y y座標
     */
    public MyPlaneBulletNode(int x, int y) {
        next = null;
        tama_x = x;
        tama_y = y;
        tama_speed = 20;
    }

    /**
     * @param x x座標
     * @param y y座標
     * @param speed タマのスピード
     */
    public MyPlaneBulletNode(int x, int y, int speed, int type) {
        this(x, y);
        this.type = type;
        tama_speed = speed;
    }

    public MyPlaneBulletNode(int x, int y, int speed, int type, double degree) {
        this(x, y, speed, type);
        this.degree = degree;
    }
}
