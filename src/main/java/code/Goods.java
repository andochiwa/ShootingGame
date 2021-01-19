package code;

/**
 * アイテムクラス
 * 
 * @see GameReport.MyJPanel#goods
 */
public class Goods {
    public double x, y;
    public int move = 5;

	public Goods(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Goods() {
    }
    
    public boolean move() {
        y += move;
        return y >= GameReport.MyJPanel.windows_size.height;
    }

}
