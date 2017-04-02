import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * Created by DGVY on 2017/4/2 0002.
 */
public class SnakeFrame extends Frame {
    //方格的宽度和长度
    public static final int BLOCK_WIDTH = 15;
    public static final int BLOCK_HEIGHT = 15;

    //界面方格的行数和列数
    public static final int ROW = 40;
    public static final int COL = 40;

    private Snake snake = new Snake(this);
    private Egg egg = new Egg();

    private int score = 0;

    public int getScore(){
        return  score;
    }

    public void setScore(int score){
        this.score = score;
    }

    private static SnakeFrame snakeframe = null;

    public static void main(String[] args) {
        snakeframe = new SnakeFrame();
        snakeframe.launch();
    }

    public void launch() {
        this.setTitle("Snake");
        this.setSize(ROW * BLOCK_HEIGHT, COL * BLOCK_WIDTH);
        this.setLocation(300, 400);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });

        this.setResizable(false);
        this.setVisible(true);

        //为页面添加监听事件
        this.addKeyListener(new KeyMonitor());

        new Thread(paintThread).start();
    }

    public void displaySomeInfor(Graphics g){
        Color c = g.getColor();
        g.setColor(Color.RED);
        g.drawString("使用说明：空格--暂停，B--继续，F2--重新开始",5*BLOCK_HEIGHT,3*BLOCK_WIDTH);
        g.drawString("得分："+score,5*BLOCK_HEIGHT,5*BLOCK_WIDTH);
        g.setColor(c);
    }

    @Override
    public void paint(Graphics g) {
        Color c = g.getColor();
        g.setColor(Color.GRAY);

        //在界面上画方格
        for (int i = 0; i < ROW; i++) {
            g.drawLine(0, i * BLOCK_HEIGHT, COL * BLOCK_WIDTH, i * BLOCK_HEIGHT);
        }
        for(int i = 0;i<COL;i++){
            g.drawLine(i*BLOCK_WIDTH,0,i*BLOCK_WIDTH,ROW*BLOCK_HEIGHT);
        }
        g.setColor(c);
    }

    private boolean gameOver = false;

    public void setGameOver(){
        gameOver = true;
    }

    private Image offScreenImage = null;

    //重写update方法，使用双缓冲技术防止闪烁
    @Override
    public void update(Graphics g){
        if (offScreenImage == null){
            offScreenImage = this.createImage(ROW*BLOCK_HEIGHT,COL*BLOCK_WIDTH);
        }
        Graphics offg = offScreenImage.getGraphics();

        //将内容画在虚拟画布上
        paint(offg);
        //将虚拟画布上的内容一起画在画布上
        g.drawImage(offScreenImage,0,0,null);

        if(gameOver){
            g.drawString("游戏结束！！！",ROW/2*BLOCK_HEIGHT,COL/2*BLOCK_WIDTH);
            paintThread.dead();
        }

        snake.draw(g);
        boolean b_Success = snake.eatEgg(egg);
        if(b_Success){
           score += 5;
        }
        egg.draw(g);
        displaySomeInfor(g);
    }

    private MyPaintThread paintThread = new MyPaintThread();

    private class MyPaintThread implements Runnable{
        //running 不能改变，否则此线程结束
        private static final boolean running = true;
        private boolean pause = false;

        @Override
        public void run(){
            while(running)
            {
                //若pause为true，则暂停
                if(pause){
                    continue;
                }
                repaint();
                try{
                    Thread.sleep(100);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }

        public void pause(){
            pause = true;
        }

        public void recover(){
            pause = false;
        }

        public void dead(){
            pause  = true;
        }

        public void reStart(){
            snakeframe.gameOver = false;
            this.pause = false;
            snake = null;
            snake = new Snake(snakeframe);
        }
    }

    private class KeyMonitor extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if(key == KeyEvent.VK_SPACE){
                paintThread.pause();
            }
            else if(key == KeyEvent.VK_B){
                paintThread.recover();
            }
            else if(key == KeyEvent.VK_F2){
                paintThread.reStart();
            }
            else{
                snake.keyPressed(e);
            }
        }
    }
}


