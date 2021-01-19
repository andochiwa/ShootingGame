package code;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 雑魚ノード
 * 
 * @author HAN
 * @see java.util.LinkedList
 * @see GameReport.MyJPanel#enemy
 */
public class Enemy {
    public double x, y; // 敵の位置情報
    public int alive; // 敵のHP
    public int move; // 敵の移動距離
    /**
     * 1, 2: 下に向かう弾幕 3: 円様分散弾幕 4: 自機を狙う弾幕
     */
    public int pattern; // 弾幕のタイプ
    private int pattern1Speed; // 発射間隔
    private int pattern2Speed;
    private int width = GameReport.MyJPanel.enemy_width, height = GameReport.MyJPanel.enemy_height;

    /** 弾幕リスト */
    public final List<EnemyBullet> enemyBullet = Collections.synchronizedList(new LinkedList<>());

    /**
     * 弾幕を作るthread
     */
    public void addShoot() {
        if (pattern == 1 || pattern == 2) {
            Runnable add = () -> {
                for (int i = 0; i < 40;) {
                    if (GameReport.MyJPanel.buttonStatus == ButtonStatus.RUNNING
                            || GameReport.MyJPanel.buttonStatus == ButtonStatus.OVER) {
                        if (alive > 0) {
                            synchronized(enemyBullet){
                                enemyBullet.add(new EnemyBullet(x + width / 2, y + height / 2, 10, 1));
                            }
                            try {
                                Thread.sleep(pattern1Speed);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        i++;
                    }
                }
            };
            new Thread(add).start();
        } else if (pattern == 3) {
            Runnable add = () -> {
                for (int i = 0; i < 18;) {
                    if (GameReport.MyJPanel.buttonStatus == ButtonStatus.RUNNING
                            || GameReport.MyJPanel.buttonStatus == ButtonStatus.OVER) {
                        if (alive > 0) {
                            for (int j = 0; j < 20; j++) {
                                synchronized(enemyBullet){
                                    enemyBullet.add(new EnemyBullet(x + width / 2, y + height / 2, 15, Math.random() * j, 2));
                                }
                            }
                            try {
                                Thread.sleep(pattern2Speed);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        i++;
                    }
                }
            };
            new Thread(add).start();
        } else if (pattern == 4) {
            Runnable add = () -> {
                for (int i = 0; i < 25;) {
                    if (GameReport.MyJPanel.buttonStatus == ButtonStatus.RUNNING
                            || GameReport.MyJPanel.buttonStatus == ButtonStatus.OVER) {
                        if (alive > 0) {
                            double degree = Math.atan2(
                                    GameReport.MyJPanel.myPlane_y - GameReport.MyJPanel.myPlane_width / 2 - y,
                                    GameReport.MyJPanel.myPlane_x - GameReport.MyJPanel.myPlane_height / 2 - x);
                            for(int j = 0; j < 7; j++) {
                                synchronized(enemyBullet){
                                    enemyBullet.add(new EnemyBullet(x + width / 2, y + height / 2, 25, 0, 3, degree * j));
                                }
                            }

                            try {
                                Thread.sleep(pattern1Speed);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        i++;
                    }
                }
            };
            new Thread(add).start();
        }
        
    }

    /**
     * @return true 画面外に出た
     */
    public boolean move() {
        return switch (pattern) {
            case 1 -> movepattern1();
            case 2 -> movepattern2();
            case 3, 4 -> movepattern3();
            default -> false;
        };
    }

    /**
     * 左から右に飛ぶ
     */
    private boolean movepattern1() {
        x += move;
        return x >= GameReport.MyJPanel.windows_size.width || alive <= 0;
    }

    /**
     * 右から左に飛ぶ
     */
    private boolean movepattern2() {
        x -= move;
        return x <= 0 || alive <= 0;
    }

    /**
     * 上から下に飛ぶ
     */
    private boolean movepattern3() {
        y += move;
        return y >= GameReport.MyJPanel.windows_size.height || alive <= 0;
    }


    /**
     * @param x     x座標
     * @param y     y座標
     * @param z     HP
     * @param move  移動距離
     * @param pattern 弾幕のタイプ
     * @param pattern1Speed タイプ1の生成間隔
     * @param pattern2Speed タイプ2の生成間隔
     */                                                     
    public Enemy(double x, double y, int z, int move, int pattern, int pattern1Speed, int pattern2Speed) {
        this.x = x;
        this.y = y;
        this.alive = z;
        this.move = move;
        this.pattern = pattern;
        this.pattern1Speed = pattern1Speed;
        this.pattern2Speed = pattern2Speed;
    }
}