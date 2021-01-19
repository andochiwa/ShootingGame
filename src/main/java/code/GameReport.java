package code;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * シューティングゲーム
 * 
 * @author HAN
 * @author 仲嶋
 * @since jdk10
 */
@SuppressWarnings("serial")
public class GameReport extends JFrame {

	/** コンストラクタ */
	public GameReport() {
		setSize(1000, 700);
		setTitle("5班課題");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		this.getContentPane().add(new MyJPanel());

		setVisible(true);
		setResizable(false);

	}

	/***************** main ****************************/
	public static void main(String[] args) {
		new GameReport();
	}

	/**
	 * パネルを作る
	 * 
	 * @author HAN
	 * @author 仲嶋
	 */
	public static class MyJPanel extends JPanel implements ActionListener {

		// ここから（仲嶋）１
		// 仲嶋 弾幕
		int danmaku_wedge = 80;// 弾幕の配置用の定数
		int danmaku_number = 35;// 弾幕の数
		int boss_danmaku_x[] = new int[danmaku_number]; // 弾幕のx座標
		int boss_danmaku_y[] = new int[danmaku_number]; // 弾幕のy座標
		int boss_danmaku_move[] = new int[danmaku_number];
		int boss_danmaku_tama_x[] = new int[danmaku_number];
		int boss_danmaku_tama_y[] = new int[danmaku_number];
		int boss_danmaku_tama_move[] = new int[danmaku_number];
		int boss_danmaku_tama_flag[] = new int[danmaku_number];
		int boss_danmaku_alive[] = new int[danmaku_number];

		int boss_time = 0;// 行動変化用仲介変数
		int boss_alive = 1000;

		int rensha_number = 25;// 連射の数
		int boss_x = 200;
		int boss_y = 0;
		int boss_move = 10;
		int boss_rensha_x;
		int boss_rensha_y[] = new int[rensha_number];
		int random_save[] = new int[rensha_number];
		int boss_tama_move = 10;
		int boss_tama_flag[] = new int[rensha_number];
		// ここまで（仲嶋）１

		Timer timer = new Timer(50, this); // タイマーの初期設定;
		public static Dimension windows_size;

		List<Explode> boom = new LinkedList<>(); // 爆発効果リスト

		List<Goods> goods = new LinkedList<>(); // アイテム

		JButton startButton = new JButton("Start"); // 開始ボタン
		static ButtonStatus buttonStatus = ButtonStatus.RUNNABLE; // ボタン押す判断用

		ImageIcon bg = new ImageIcon(getClass().getResource("/images/bg.jpg")); // 背景
		ImageIcon myPlane_image, enemy_image, boss_image; // 画像

		ImageIcon item = new ImageIcon(getClass().getResource("/images/item.png"));
		int item_width = item.getIconWidth();
		int item_height = item.getIconHeight();
		int pointItem = 0;
		int pointEnemy = 0;

		MyThreadBullet mythread_bullet; // タマ用thread
		MyThreadCreateEnemy1 mythread_enemycreate1;
		MyThreadCreateEnemy2 mythread_enemycreate2;
		MyThreadCreateEnemy3 mythread_enemycreate3;

		/** 移動の制御 0:left, 1:right, 2:upper, 3:lower */
		boolean[] move = new boolean[4];
		boolean bullet = false; // タマの制御

		/** 雑魚リスト */
		final List<Enemy> enemy = Collections.synchronizedList(new LinkedList<>());
		static int enemy_width, enemy_height; // 雑魚の幅と高さ
		int enemy_bullet_size_x = 8, enemy_bullet_size_y = 8; // 雑魚タマの大きさ

		int boss_width, boss_height; // bossの幅と高さ
		int boss_bullet_size_x = 10, boss_bullet_size_y = 20; // bossタマの大きさ

		// 自機
		MyPlaneBulletList myPlane_bullet = new MyPlaneBulletList(); // 自機のたま処理用リスト
		static int myPlane_width, myPlane_height; // 自機の幅と高さ
		static int myPlane_x = 500, myPlane_y = 400; // 自機の座標
		int myPlane_xspeed = 15, myPlane_yspeed = 10; // 自機の移動速度
		int myPlane_hp = 300; // 自機の体力
		int myPlane_bullet_size_x = 8, myPlane_bullet_size_y = 8; // 自機タマの大きさ
		int myPlaneMoveFlag = 2;

		/** コンストラクタ */
		public MyJPanel() {
			setBackground(Color.black);
			setFocusable(true);

			addKeyListener(new KeyMonitor());

			myPlane_image = new ImageIcon(getClass().getResource("/images/jiki.jpg"));
			myPlane_width = myPlane_image.getIconWidth();
			myPlane_height = myPlane_image.getIconHeight();

			enemy_image = new ImageIcon(getClass().getResource("/images/teki.jpg"));
			enemy_width = enemy_image.getIconWidth();
			enemy_height = enemy_image.getIconHeight();

			boss_image = new ImageIcon(getClass().getResource("/images/boss.jpg"));
			boss_width = boss_image.getIconWidth();
			boss_height = boss_image.getIconHeight();

			add(startButton);
			startButton.addActionListener(this);
			startButton.setPreferredSize(new Dimension(1000, 700));
			startButton.setBorder(BorderFactory.createRaisedBevelBorder());
			startButton.setBackground(Color.white);

			// ここから（仲嶋）２
			// 弾幕及び連射の初期設定
			for (int i = 0; i < danmaku_number; i++) {
				boss_danmaku_x[i] = i * danmaku_wedge % 1000;
				boss_danmaku_y[i] = 0;
			}

			for (int i = 0; i < rensha_number; i++) {
				boss_rensha_y[i] = 0;
			}

			Random random = new Random();
			for (int i = 0; i < danmaku_number; i++) {
				boss_danmaku_alive[i] = 1;
				boss_danmaku_move[i] = -10 * i % 50;
				boss_danmaku_tama_flag[i] = 1;
				boss_danmaku_tama_x[i] = boss_x + random.nextInt(boss_width);
				boss_danmaku_tama_y[i] = boss_y + boss_height / 2;
				boss_danmaku_tama_move[i] = 10 + (i % 8);
			}
			// ここまで（仲嶋）２

		}

		/** 絵画 */
		public void paintComponent(Graphics g) {
			if (buttonStatus == ButtonStatus.RUNNING || buttonStatus == ButtonStatus.OVER) {
				super.paintComponent(g);
				windows_size = getSize();

				// 背景を描く
				g.drawImage(bg.getImage(), 0, 0, windows_size.width, windows_size.height, this);
				
				item(g);

				myPlaneMoveControl();

				myPlaneMove(g);

				myPlaneBullet(g);

				enemyMove(g);
				
				enemyBullet(g);
				
				if(pointEnemy >= 230 && enemy.isEmpty()) bossMove(g);
				
				if(pointEnemy >= 230 && enemy.isEmpty()) bossBullet(g);
				
				healthPoint(g);

				booooom(g);
				if (buttonStatus == ButtonStatus.OVER) {
					g.setColor(Color.white);
					g.drawString("終わる", windows_size.width / 2, windows_size.height / 2);
				}
			} else if (buttonStatus == ButtonStatus.PAUSE) {
				timer.stop();
				add(startButton);
				buttonStatus = ButtonStatus.RUNNABLE;
			}

		}

		/**
		 * Timerと合わせて自動に呼び出されるthread
		 * 
		 * @author HAN
		 * @see #timer
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == startButton) {
				if (buttonStatus == ButtonStatus.RUNNABLE) {
					buttonStatus = ButtonStatus.RUNNING;
					remove(startButton); // ボタンをパネルから削除する

					windows_size = getSize();
					timer.start(); // タイマーの開始
					if (mythread_enemycreate1 == null) {
						mythread_bullet = new MyThreadBullet();
						mythread_enemycreate1 = new MyThreadCreateEnemy1();
						mythread_enemycreate2 = new MyThreadCreateEnemy2();
						mythread_enemycreate3 = new MyThreadCreateEnemy3();
						new Thread(mythread_bullet).start();
						new Thread(mythread_enemycreate1).start();
						new Thread(mythread_enemycreate2).start();
						new Thread(mythread_enemycreate3).start();
					}
					requestFocus(); // 改めて狙いを定める
				}
			}
			repaint();
		}

		/**
		 * 移動の制御
		 * 
		 * @author HAN
		 * @see KeyMonitor
		 */
		private void myPlaneMoveControl() {
			if (buttonStatus == ButtonStatus.RUNNING) {
				if (myPlaneMoveFlag == 2) {
					if (move[0] && move[2]) {
						myPlane_x -= myPlane_xspeed;
						myPlane_y -= myPlane_yspeed;
					} else if (move[0] && move[3]) {
						myPlane_x -= myPlane_xspeed;
						myPlane_y += myPlane_yspeed;
					} else if (move[1] && move[2]) {
						myPlane_x += myPlane_xspeed;
						myPlane_y -= myPlane_yspeed;
					} else if (move[1] && move[3]) {
						myPlane_x += myPlane_xspeed;
						myPlane_y += myPlane_yspeed;
					} else if (move[0]) {
						myPlane_x -= myPlane_xspeed;
					} else if (move[1]) {
						myPlane_x += myPlane_xspeed;
					} else if (move[2]) {
						myPlane_y -= myPlane_yspeed;
					} else if (move[3]) {
						myPlane_y += myPlane_yspeed;
					}
				} else if (myPlaneMoveFlag == 1) {
					if (move[0] && move[2]) {
						myPlane_x -= myPlane_xspeed / 2;
						myPlane_y -= myPlane_yspeed / 2;
					} else if (move[0] && move[3]) {
						myPlane_x -= myPlane_xspeed / 2;
						myPlane_y += myPlane_yspeed / 2;
					} else if (move[1] && move[2]) {
						myPlane_x += myPlane_xspeed / 2;
						myPlane_y -= myPlane_yspeed / 2;
					} else if (move[1] && move[3]) {
						myPlane_x += myPlane_xspeed / 2;
						myPlane_y += myPlane_yspeed / 2;
					} else if (move[0]) {
						myPlane_x -= myPlane_xspeed / 2;
					} else if (move[1]) {
						myPlane_x += myPlane_xspeed / 2;
					} else if (move[2]) {
						myPlane_y -= myPlane_yspeed / 2;
					} else if (move[3]) {
						myPlane_y += myPlane_yspeed / 2;
					}
				} else {
					if (move[0] && move[2]) {
						myPlane_x -= myPlane_xspeed * 2;
						myPlane_y -= myPlane_yspeed * 2;
					} else if (move[0] && move[3]) {
						myPlane_x -= myPlane_xspeed * 2;
						myPlane_y += myPlane_yspeed * 2;
					} else if (move[1] && move[2]) {
						myPlane_x += myPlane_xspeed * 2;
						myPlane_y -= myPlane_yspeed * 2;
					} else if (move[1] && move[3]) {
						myPlane_x += myPlane_xspeed * 2;
						myPlane_y += myPlane_yspeed * 2;
					} else if (move[0]) {
						myPlane_x -= myPlane_xspeed * 2;
					} else if (move[1]) {
						myPlane_x += myPlane_xspeed * 2;
					} else if (move[2]) {
						myPlane_y -= myPlane_yspeed * 2;
					} else if (move[3]) {
						myPlane_y += myPlane_yspeed * 2;
					}
				}
			}
		}

		/**
		 * 自機移動の処理
		 * 
		 * @author HAN
		 * @see KeyMonitor
		 * @see #myPlaneMoveControl
		 */
		private void myPlaneMove(Graphics g) {
			if (myPlane_hp > 0) {
				if (myPlane_x <= 0)
					myPlane_x = 0;
				else if (myPlane_x >= windows_size.width - myPlane_width)
					myPlane_x = windows_size.width - myPlane_width;
				if (myPlane_y <= 0)
					myPlane_y = 0;
				else if (myPlane_y >= windows_size.height - myPlane_height)
					myPlane_y = windows_size.height - myPlane_height;
				g.drawImage(myPlane_image.getImage(), myPlane_x, myPlane_y, this);
			}
		}

		/**
		 * 自機タマの処理
		 * 
		 * @author HAN
		 * @see MyThreadBullet
		 * @see #isHit_Enemy(int, int, int, int)
		 * @see #isHit_boss(int, int, int, int)
		 */
		private void myPlaneBullet(Graphics g) {
			for (MyPlaneBulletNode myPlane = myPlane_bullet.dummyhead; myPlane.next != null; myPlane = myPlane.next) {
				boolean flag = false; // 現在のタマが消えたかどうかを判断する
				synchronized(enemy){
					for (Enemy enemy : enemy) {
						if (enemy.alive <= 0)
							continue;
						if (isHit_Enemy((int) enemy.x, (int) enemy.y, myPlane.next.tama_x, myPlane.next.tama_y)) {
							flag = true;
							enemy.alive--;
							if (enemy.alive == 0) {
								pointEnemy++;
								if (pointEnemy >= 20) {
									if (mythread_enemycreate1.getLevel() == 1) {
										mythread_enemycreate1.levelUp();
										mythread_enemycreate2.levelUp();
										mythread_enemycreate3.levelUp();
									} else if (mythread_enemycreate1.getLevel() == 2) {
										if (pointEnemy >= 50) {
											mythread_enemycreate1.levelUp();
											mythread_enemycreate2.levelUp();
											mythread_enemycreate3.levelUp();
										}
									} else if (mythread_enemycreate1.getLevel() == 3) {
										if (pointEnemy >= 95) {
											mythread_enemycreate1.levelUp();
											mythread_enemycreate2.levelUp();
											mythread_enemycreate3.levelUp();
										}
									} else if (mythread_enemycreate1.getLevel() == 4) {
										if (pointEnemy >= 150) {
											mythread_enemycreate1.levelUp();
											mythread_enemycreate2.levelUp();
											mythread_enemycreate3.levelUp();
										}
									} else {
										if (pointEnemy >= 230) {
											mythread_enemycreate1.stop();
											mythread_enemycreate2.stop();
											mythread_enemycreate3.stop();
										}
									}
								}
								if(pointItem <= 90) 
									goods.add(new Goods(enemy.x, enemy.y));
								boom.add(new Explode((int) enemy.x, (int) enemy.y));
							}
							break;
						}
					}
				}

				if(pointEnemy >= 230 && this.enemy.isEmpty()) {
					if(isHit_boss(boss_x, boss_y, myPlane.next.tama_x, myPlane.next.tama_y)) {
						boss_alive--;
						flag = true;
						if(boss_alive == 0) {
							boom.add(new Explode(boss_x, boss_y));
							buttonStatus = ButtonStatus.OVER;
						}
						break;
					}
				}

				if (!flag) { // タマが消えていないなら
					if (myPlane.next.type == 1) {
						myPlane.next.tama_y -= myPlane.next.tama_speed;
					} else if (myPlane.next.type == 2) {
						myPlane.next.tama_x += Math.cos(myPlane.next.degree) * myPlane.next.tama_speed;
						myPlane.next.tama_y += Math.sin(myPlane.next.degree) * myPlane.next.tama_speed;
					}
					g.setColor(Color.magenta);
					g.fillOval(myPlane.next.tama_x, myPlane.next.tama_y, myPlane_bullet_size_x, myPlane_bullet_size_y);
					if (myPlane.next.tama_y < 0 || myPlane.next.tama_x < 0 || myPlane.next.tama_y > windows_size.height) // タマが画面外に出たら
					{
						myPlane_bullet.delete(myPlane);
					}
				} else { // タマが消えたら
					myPlane_bullet.delete(myPlane);
				}
				if (myPlane.next == null)
					break;
			}
		}

		/**
		 * 体力ゲージ
		 * 
		 * @author HAN
		 * @see #myPlane_hp
		 */
		private void healthPoint(Graphics g) {
			int x = windows_size.width - 20;
			int y = 10;
			for (int i = 0; i < myPlane_hp; i++) {
				g.setColor(Color.YELLOW);
				g.drawOval(x, y, 10, 10);
				x -= 20;
			}
		}

		/**
		 * 雑魚移動の処理
		 * 
		 * @author HAN
		 * @see #enemy
		 */
		private void enemyMove(Graphics g) {
			synchronized(enemy){
				for (var it = enemy.iterator(); it.hasNext();) {
					var enemy = it.next();
					if (enemy.alive > 0)
						g.drawImage(enemy_image.getImage(), (int) enemy.x, (int) enemy.y, this);
					if (enemy.move() && enemy.enemyBullet.isEmpty()) {
						it.remove();
					}
				}
			}
		}

		/**
		 * 雑魚弾幕の処理
		 * 
		 * @author HAN
		 * @see #enemy
		 * @see #isHit_myPlane(int, int, int, int)
		 */
		private void enemyBullet(Graphics g) {
			synchronized(enemy){
				for (Enemy enemy : enemy) {
					synchronized(enemy.enemyBullet){
						for (var itBullet = enemy.enemyBullet.iterator(); itBullet.hasNext();) {
							var bullet = itBullet.next();
							g.fillOval((int) bullet.x, (int) bullet.y, enemy_bullet_size_x, enemy_bullet_size_y);
							if (bullet.type == 1) {
								g.setColor(Color.green);
								bullet.y += bullet.speed;
								if (bullet.y <= 0 || bullet.y > windows_size.height) {
									itBullet.remove();
									continue;
								}
							} else if (bullet.type == 2) {
								g.setColor(Color.CYAN);
								bullet.x += Math.cos(bullet.degree) * bullet.speed;
								bullet.y += Math.sin(bullet.degree) * bullet.speed;
								if (bullet.x <= 0 || bullet.x > windows_size.width || bullet.y <= 0
										|| bullet.y > windows_size.height) {
									itBullet.remove();
									continue;
								}
							} else if (bullet.type == 3) {
								g.setColor(Color.red);
								bullet.x += Math.cos(bullet.myPlaneDegree) * bullet.speed;
								bullet.y += Math.sin(bullet.myPlaneDegree) * bullet.speed;
								if (bullet.x <= 0 || bullet.x > windows_size.width || bullet.y <= 0
										|| bullet.y > windows_size.height) {
									itBullet.remove();
									continue;
								}
							}
							if (isHit_myPlane(myPlane_x, myPlane_y, (int) bullet.x, (int) bullet.y)) {
								myPlane_hp--;
								itBullet.remove();
								if (myPlane_hp == 0) {
									mythread_bullet.stop();
									buttonStatus = ButtonStatus.OVER;
									boom.add(new Explode(myPlane_x, myPlane_y));
								}
							}
						}
					}
				}
			}
		}

		/**
		 * @author 仲嶋
		 */
		private void bossMove(Graphics g) {
			if(boss_alive > 0) {
				g.drawImage(boss_image.getImage(), boss_x, boss_y, this);
				boss_x += boss_move;
				if ((boss_x < 0) || (boss_x > windows_size.width - boss_width))
					boss_move = -boss_move;
			}
		}

		/**
		 * bossタマの処理
		 * 
		 * @author 仲嶋
		 * @see #isHit_myPlane_boss(int, int, int, int)
		 */
		private void bossBullet(Graphics g) {

			if (boss_alive > 0) {
				g.drawImage(boss_image.getImage(), boss_x, boss_y, this);
				boss_x += boss_move;
				if (boss_time < 1000)
					boss_time++;
				else
					boss_time = 0;

				if ((boss_x < 0) || (boss_x > windows_size.width - boss_width))
					boss_move = -boss_move;
				// 弾幕の有無
				for (int i = 0; i < danmaku_number; i++) {
					if (boss_danmaku_alive[i] == 1)// 弾幕の分布
					{
						boss_danmaku_x[i] += boss_danmaku_move[i];
						if ((boss_danmaku_x[i] <= 10) || (boss_danmaku_x[i] > windows_size.width))
							boss_danmaku_move[i] = -boss_danmaku_move[i];

					}
				}

				Random random = new Random();
				// 弾幕の処理
				for (int i = 0; i < danmaku_number; i++) {
					if (boss_danmaku_alive[i] == 1) {
						if (boss_danmaku_tama_flag[i] == 0) {
							boss_danmaku_tama_x[i] = boss_x + random.nextInt(boss_width);
							boss_danmaku_tama_y[i] = boss_y + boss_height / 2;
							boss_danmaku_tama_flag[i] = 1;
							g.setColor(Color.red);
							g.fillRect(boss_danmaku_tama_x[i], boss_danmaku_tama_y[i], boss_bullet_size_x,
									boss_bullet_size_y);
						} else {
							boss_danmaku_tama_y[i] += boss_danmaku_tama_move[i];
							g.setColor(Color.red);
							g.fillRect(boss_danmaku_tama_x[i], boss_danmaku_tama_y[i], boss_bullet_size_x,
									boss_bullet_size_y);
							if (boss_danmaku_tama_y[i] >= windows_size.height)
								boss_danmaku_tama_flag[i] = 0;// タマが画面外に出たら
						}

					}
					if (isHit_myPlane_boss(myPlane_x, myPlane_y, boss_danmaku_tama_x[i], boss_danmaku_tama_y[i])) // 敵のたまが自機を当たったら
					{
						myPlane_hp--;
						boss_danmaku_tama_flag[i] = 0;
						if (myPlane_hp == 0) {
							buttonStatus = ButtonStatus.OVER;
							boom.add(new Explode(myPlane_x, myPlane_y));
							mythread_bullet.stop();
						}

					}
				}

				// 連射攻撃
				for (int i = 0; i < rensha_number; i++)// 連射の分布
				{
					if (boss_tama_flag[i] == 0)// 連射の有無
					{
						boss_rensha_x = boss_x;
						boss_rensha_y[i] = 0;
						boss_tama_flag[i] = 1;
						random_save[i] = random.nextInt(windows_size.width);
						g.setColor(Color.white);
						g.fillOval(random_save[i], boss_height, boss_bullet_size_x, boss_bullet_size_y);
						if (isHit_myPlane_boss(myPlane_x, myPlane_y, random_save[i], boss_height)) // 敵のたまが自機を当たったら
						{
							myPlane_hp--;
							if (myPlane_hp == 0) {
								buttonStatus = ButtonStatus.OVER;
								boom.add(new Explode(myPlane_x, myPlane_y));
								mythread_bullet.stop();

							}
							boss_tama_flag[i] = 0;

						}

					} else// 連射の処理
					{
						boss_rensha_y[i] += boss_tama_move;
						g.setColor(Color.white);
						g.fillOval(random_save[i], boss_rensha_y[i] + boss_height, boss_bullet_size_x,
								boss_bullet_size_y);
						if (isHit_myPlane(myPlane_x, myPlane_y, random_save[i], boss_rensha_y[i] + boss_height)) // 敵のたまが自機を当たったら
						{
							myPlane_hp--;
							if (myPlane_hp == 0) {
								buttonStatus = ButtonStatus.OVER;
								boom.add(new Explode(myPlane_x, myPlane_y));
								mythread_bullet.stop();
							}
							boss_tama_flag[i] = 0;

						}
						if (boss_rensha_y[i] > windows_size.height)
							boss_tama_flag[i] = 0;

					}
				}
			}

		}

		/**
		 * @see #isHit_item(int, int, int, int)
		 */
		private void item(Graphics g) {
			for (var it = goods.iterator(); it.hasNext();) {
				var node = it.next();
				g.drawImage(item.getImage(), (int) node.x, (int) node.y, this);
				if (isHit_item(myPlane_x, myPlane_y, (int) node.x, (int) node.y)) {
					pointItem++;
					if (pointItem >= 7) {
						if (mythread_bullet.getLevel() == 1) {
							mythread_bullet.levelUp();
						} else if (mythread_bullet.getLevel() == 2) {
							if (pointItem >= 17) {
								mythread_bullet.levelUp();
							}
						} else if (mythread_bullet.getLevel() == 3) {
							if (pointItem >= 50) {
								mythread_bullet.levelUp();
							}
						} else if (mythread_bullet.getLevel() == 4) {
							if (pointItem >= 90) {
								mythread_bullet.levelUp();
							}
						} 
					}
					it.remove();
					continue;
				}
				if (node.move()) {
					it.remove();
				}
			}
		}

		/**
		 * 機体を消滅したときの爆発効果
		 * 
		 * @author HAN
		 * @see #boom
		 */
		private void booooom(Graphics g) {
			for (var it = boom.listIterator(); it.hasNext();) {
				var node = it.next();
				if (node.getCount() < 16) {
					node.draw(g);
				} else {
					it.remove();
				}
			}

		}

		/**
		 * 自機のhit check(雑魚に対する)
		 * 
		 * @author HAN
		 * @param myPlane_x      自機のx座標
		 * @param myPlane_y      自機のy座標
		 * @param enemy_bullet_x 雑魚のタマのx座標
		 * @param enemy_bullet_y 雑魚のタマのy座標
		 * @return {@code true} hit it
		 */
		private boolean isHit_myPlane(int myPlane_x, int myPlane_y, int enemy_bullet_x, int enemy_bullet_y) {
			Rectangle r1 = new Rectangle(myPlane_x + 5, myPlane_y + 5, myPlane_width - 5, myPlane_height - 5);
			Rectangle r2 = new Rectangle(enemy_bullet_x, enemy_bullet_y, enemy_bullet_size_x, enemy_bullet_size_y);
			return r1.intersects(r2);
		}

		/**
		 * 自機のhit check(bossに対する)
		 * 
		 * @author HAN
		 * @param myPlane_x     自機のx座標
		 * @param myPlane_y     自機のy座標
		 * @param boss_bullet_x bossのタマのx座標
		 * @param boss_bullet_y bossのタマのy座標
		 * @return {@code true} hit it
		 */
		private boolean isHit_myPlane_boss(int myPlane_x, int myPlane_y, int boss_bullet_x, int boss_bullet_y) {
			Rectangle r1 = new Rectangle(myPlane_x + 5, myPlane_y + 5, myPlane_width - 5, myPlane_height - 5);
			Rectangle r2 = new Rectangle(boss_bullet_x, boss_bullet_y, boss_bullet_size_x, boss_bullet_size_y);
			return r1.intersects(r2);
		}

		/**
		 * 雑魚のhit check
		 * 
		 * @author HAN
		 * @param enemy_x          雑魚のx座標
		 * @param enemy_y          雑魚のy座標
		 * @param myPlane_bullet_x 自機タマのx座標
		 * @param myPlane_bullet_y 自機タマのy座標
		 * @return {@code true} hit it
		 */
		private boolean isHit_Enemy(int enemy_x, int enemy_y, int myPlane_bullet_x, int myPlane_bullet_y) {
			Rectangle r1 = new Rectangle(enemy_x, enemy_y, enemy_width, enemy_height);
			Rectangle r2 = new Rectangle(myPlane_bullet_x, myPlane_bullet_y, myPlane_bullet_size_x,
					myPlane_bullet_size_y);
			return r1.intersects(r2);
		}

		/**
		 * bossのhit check
		 * 
		 * @author HAN
		 * @param boss_x           bossのx座標
		 * @param boss_y           bossのy座標
		 * @param myPlane_bullet_x 自機タマのx座標
		 * @param myPlane_bullet_y 自機タマのy座標
		 * @return {@code true} hit it
		 */
		private boolean isHit_boss(int boss_x, int boss_y, int myPlane_bullet_x, int myPlane_bullet_y) {
			Rectangle r1 = new Rectangle(boss_x, boss_y, boss_width, boss_height);
			Rectangle r2 = new Rectangle(myPlane_bullet_x, myPlane_bullet_y, myPlane_bullet_size_x,
					myPlane_bullet_size_y);
			return r1.intersects(r2);
		}

		/**
		 * アイテムのhit check
		 * 
		 * @author HAN
		 * @param myPlane_x 自機のx座標
		 * @param myPlane_y 自機のy座標
		 * @param item_x    アイテムのx座標
		 * @param item_y    アイテムのy座標
		 */
		private boolean isHit_item(int myPlane_x, int myPlane_y, int item_x, int item_y) {
			Rectangle r1 = new Rectangle(myPlane_x, myPlane_y, myPlane_width, myPlane_height);
			Rectangle r2 = new Rectangle(item_x, item_y, item_width, item_height);
			return r1.intersects(r2);

		}

		/**
		 * キーボードへの処理
		 * 
		 * @author HAN
		 * @see MyThreadBullet
		 * @see #myPlaneMoveControl
		 */
		public class KeyMonitor extends KeyAdapter {
			/** キーボードを押されたら */
			@Override
			public synchronized void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				switch (key) {
					case KeyEvent.VK_LEFT -> move[0] = true;
					case KeyEvent.VK_RIGHT -> move[1] = true;
					case KeyEvent.VK_UP -> move[2] = true;
					case KeyEvent.VK_DOWN -> move[3] = true;
					case KeyEvent.VK_ESCAPE -> buttonStatus = ButtonStatus.PAUSE;
					case KeyEvent.VK_SHIFT -> myPlaneMoveFlag = 1;
					case KeyEvent.VK_CONTROL -> myPlaneMoveFlag = 3;
					default -> bullet = true;
				}
			}

			/** キーボードから離れたら */
			@Override
			public synchronized void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				switch (key) {
					case KeyEvent.VK_LEFT -> move[0] = false;
					case KeyEvent.VK_RIGHT -> move[1] = false;
					case KeyEvent.VK_UP -> move[2] = false;
					case KeyEvent.VK_DOWN -> move[3] = false;
					case KeyEvent.VK_ESCAPE -> buttonStatus = ButtonStatus.RUNNING;
					case KeyEvent.VK_SHIFT, KeyEvent.VK_CONTROL -> myPlaneMoveFlag = 2;
					default -> bullet = false;
				}
			}
		}

		/**
		 * MultiThreadによるタマの制御
		 * 
		 * @author HAN
		 * @see KeyMonitor
		 */
		public class MyThreadBullet implements Runnable {
			private boolean flag = true;
			private int bulletSpeed = 30;
			private int bulletSpace = 300;
			private int level = 1;

			@Override
			public void run() {
				while (flag) {
					if (buttonStatus == ButtonStatus.RUNNING || buttonStatus == ButtonStatus.OVER) {
						try {
							if (bullet) {
								if (level == 1) {
									myPlane_bullet.insert(myPlane_x + myPlane_width, myPlane_y, bulletSpeed, 1);
									myPlane_bullet.insert(myPlane_x, myPlane_y, bulletSpeed, 1);
								} else if (level >= 2) {
									myPlane_bullet.insert(myPlane_x - 15, myPlane_y, bulletSpeed, 1);
									myPlane_bullet.insert(myPlane_x, myPlane_y, bulletSpeed, 1);
									myPlane_bullet.insert(myPlane_x + 15, myPlane_y, bulletSpeed, 1);
									myPlane_bullet.insert(myPlane_x + 30, myPlane_y, bulletSpeed, 1);
									if (level >= 3) {
										myPlane_bullet.insert(myPlane_x - 10, myPlane_y, bulletSpeed, 2, 4);
										myPlane_bullet.insert(myPlane_x + 10, myPlane_y, bulletSpeed, 2, 5.4);
										myPlane_bullet.insert(myPlane_x - 5, myPlane_y, bulletSpeed, 2, 4.5);
										myPlane_bullet.insert(myPlane_x + 5, myPlane_y, bulletSpeed, 2, 5);
										if (level >= 4) {
											myPlane_bullet.insert(myPlane_x - 20, myPlane_y, bulletSpeed, 2, 3.7);
											myPlane_bullet.insert(myPlane_x + 20, myPlane_y, bulletSpeed, 2, 6.2);
											myPlane_bullet.insert(myPlane_x - 15, myPlane_y, bulletSpeed, 2, 3.2);
											myPlane_bullet.insert(myPlane_x + 15, myPlane_y, bulletSpeed, 2, 5.8);
										}
									}
								}
								Thread.sleep(bulletSpace); // 発射間隔
							} else {
								Thread.sleep(30);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}

			/** threadを停止しオブジェクトが回収待ちの状態に入る */
			public void stop() {
				this.flag = false;
			}

			public void levelUp() {
				if (level <= 4) {
					level++;
					bulletSpace -= 50;
					bulletSpeed += 10;
				}
			}

			public int getLevel() {
				return level;
			}
		}

		/**
		 * 雑魚1を作るthread
		 * 
		 * @author HAN
		 * @see #enemy
		 */
		public class MyThreadCreateEnemy1 implements Runnable {
			boolean flag = true;
			private int level = 1;
			private int hp = 3;
			private int pattern1Speed = 700;
			private int pattern2Speed = 1000;
			private int CreateSpeed = 1500;

			@Override
			public void run() {
				while (flag) {
					if(buttonStatus == ButtonStatus.RUNNING || buttonStatus == ButtonStatus.OVER) {
						Random random = new Random();
						if (random.nextInt(2) + 1 == 1) {
							int randomx = -80;
							int randomy = random.nextInt(300) + 30;
							int moveSpeed = random.nextInt(5) + 3;
							Enemy enemyTemp = new Enemy(randomx, randomy, hp, moveSpeed, 1, pattern1Speed, pattern2Speed);
							synchronized(enemy) {
								enemy.add(enemyTemp);
							}
							enemyTemp.addShoot();
						} else {
							int randomx = windows_size.width + 80;
							int randomy = random.nextInt(300) + 30;
							int moveSpeed = random.nextInt(4) + 3;
							Enemy enemyTemp = new Enemy(randomx, randomy, hp, moveSpeed, 2, pattern1Speed, pattern2Speed);
							synchronized(enemy) {
								enemy.add(enemyTemp);
							}
							enemyTemp.addShoot();
						}

					}
					try {
						Thread.sleep(CreateSpeed);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			public void stop() {
				this.flag = false;
			}

			public void levelUp() {
				if (level <= 4) {
					level++;
					hp += 2;
					pattern1Speed -= 70;
					pattern2Speed -= 100;
					CreateSpeed -= 150;
				}
			}

			public int getLevel() {
				return level;
			}
		}

		/**
		 * 雑魚2を作るthread
		 * 
		 * @author HAN
		 * @see #enemy
		 */
		public class MyThreadCreateEnemy2 implements Runnable {
			boolean flag = true;
			private int level = 1;
			private int hp = 5;
			private int pattern1Speed = 700;
			private int pattern2Speed = 1000;
			private int CreateSpeed = 8000;

			@Override
			public void run() {
				while (flag) {
					if(buttonStatus == ButtonStatus.RUNNING || buttonStatus == ButtonStatus.OVER) {
						Random random = new Random();
						int randomx = random.nextInt(800) + 100;
						int randomy = -100;
						int moveSpeed = 3;
						Enemy enemyTemp = new Enemy(randomx, randomy, hp, moveSpeed, 3, pattern1Speed, pattern2Speed);
						synchronized(enemy) {
							enemy.add(enemyTemp);
						}
						enemyTemp.addShoot();

					}
					try {
						Thread.sleep(CreateSpeed);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			public void stop() {
				this.flag = false;
			}

			public void levelUp() {
				if (level <= 4) {
					level++;
					hp += 3;
					pattern1Speed -= 70;
					pattern2Speed -= 100;
					CreateSpeed -= 1300;
				}
			}
		}

		/**
		 * 雑魚3を作るthread
		 * 
		 * @author HAN
		 * @see #enemy
		 */
		public class MyThreadCreateEnemy3 implements Runnable {
			boolean flag = true;
			private int level = 1;
			private int hp = 5;
			private int pattern1Speed = 700;
			private int pattern2Speed = 1000;
			private int CreateSpeed = 10000;

			@Override
			public void run() {
				while (flag) {
					if(buttonStatus == ButtonStatus.RUNNING || buttonStatus == ButtonStatus.OVER) {
						Random random = new Random();
						int randomx = random.nextInt(800) + 100;
						int randomy = -80;
						int moveSpeed = 3;
						Enemy enemyTemp = new Enemy(randomx, randomy, hp, moveSpeed, 4, pattern1Speed, pattern2Speed);
						synchronized(enemy) {
							enemy.add(enemyTemp);
						}
						enemyTemp.addShoot();

					}
						try {
							Thread.sleep(CreateSpeed);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
			}

			public void stop() {
				this.flag = false;
			}

			public void levelUp() {
				if (level <= 4) {
					level++;
					hp += 3;
					pattern1Speed -= 70;
					pattern2Speed -= 100;
					CreateSpeed -= 1400;
				}
			}
		}

	}
}