package code;

/**
 * 雑魚の弾幕
 * 
 * @author HAN
 * @see Enemy#enemyBullet
 */
public class EnemyBullet {
    public double x, y; // 座標
    public int speed; // 速度
    public double degree; // 角度
    public int type; // 弾幕のタイプ
    public double myPlaneDegree; // 自機を狙うため角度をもらう

    public EnemyBullet() {
    }

    /**
     * @param x     x座標
     * @param y     y座標
     * @param speed 弾幕のスピード
     * @param type  弾幕のタイプ
     */
    public EnemyBullet(double x, double y, int speed, int type) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.type = type;
    }

    /**
     * @param x      x座標
     * @param y      y座標
     * @param speed  弾幕のスピード
     * @param degree 弾幕の角度
     * @param type   弾幕のタイプ
     */
    public EnemyBullet(double x, double y, int speed, double degree, int type) {
        this(x, y, speed, type);
        this.degree = 2 * Math.PI * degree;
    }

    public EnemyBullet(double x, double y, int speed, double degree, int type, double myPlaneDegree) {
        this(x, y, speed, degree, type);
        this.myPlaneDegree = myPlaneDegree;
    }

}
