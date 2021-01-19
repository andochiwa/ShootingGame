package code;

import java.awt.Graphics;
import javax.swing.ImageIcon;

/**
 * 爆発効果用ノードクラス
 * 
 * @author HAN
 * @see java.util.LinkedList
 * @see GameReport.MyJPanel#boom
 */
public class Explode {
    private int x, y;
    private int count;

    private static ImageIcon[] boom = new ImageIcon[16];

    static {
        for (int i = 0; i < 16; i++) {
            boom[i] = new ImageIcon(GameReport.class.getResource("/images/boom/e" + (i + 1) + ".gif"));
        }
    }

    public Explode(int x, int y) {
        this.count = 0;
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.drawImage(boom[count++].getImage(), this.x, this.y, null);
    }

    public int getCount() {
        return count;
    }
}
