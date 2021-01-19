package code;

/**
 * 自機リスト
 * 
 * @author HAN
 * @see MyPlaneBulletNode
 * @see GameReport.MyJPanel#myPlane_bullet
 */
public class MyPlaneBulletList {
    public MyPlaneBulletNode dummyhead;
    private int size = 0;

    public MyPlaneBulletList() {
        dummyhead = new MyPlaneBulletNode(-1, -1);
    }

    /**
     * 自機タマを追加し、初期値を決める
     * 
     * @param x     x座標
     * @param y     y座標
     * @param speed タマの速度
     * @param type タマのタイプ
     */
    public void insert(int x, int y, int speed, int type) {
        if (dummyhead.next == null) {
            dummyhead.next = new MyPlaneBulletNode(x, y, speed, type);
        } else {
            MyPlaneBulletNode temp = dummyhead.next;
            dummyhead.next = new MyPlaneBulletNode(x, y, speed, type);
            dummyhead.next.next = temp;
        }
        size++;
    }

    public void insert(int x, int y, int speed, int type, double degree) {
        if(dummyhead.next == null) {
            dummyhead.next = new MyPlaneBulletNode(x, y, speed, type, degree);
        } else {
            MyPlaneBulletNode temp = dummyhead.next;
            dummyhead.next = new MyPlaneBulletNode(x, y, speed, type, degree);
            dummyhead.next.next = temp;
        }
    }

    /**
     * 自機タマを削除する
     * 
     * @param node 削除したいノードの前のノード
     */
    public void delete(MyPlaneBulletNode node) {
        node.next = node.next.next;
        size--;
    }

    /**
     * 現在タマの数
     */
    public int getSize() {
        return size;
    }
}
