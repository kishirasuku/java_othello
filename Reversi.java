import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

class Stone{
    public final static int black = 1;
    public final static int white = 2;
    public int obverse;

    Stone(){
	obverse = 0;
    }

    void setObverse(int color){
	if(color == black || color == white){
	    obverse = color;
	}else{
	    System.out.println("黒か白でなければいけません");
	}
    }

    void paint(Graphics g,Point p,int rad){
	if(obverse == black){
	    g.setColor(Color.black);
	    g.fillOval(p.x,p.y,rad*2,rad*2);
	}else if(obverse == white){
	    g.setColor(Color.white);
	    g.fillOval(p.x,p.y,rad*2,rad*2);
	}
    }

    int getObverse(){
	return obverse;
    }
}

class Board{
    public Stone[][] stones = new Stone[8][8];
    public int num_grid_black = 0;
    public int num_grid_white = 0;;
    private Point[] direction = new Point[8];
    public int[][] eval_black = new int[8][8];
    public int[][] eval_white = new int[8][8];
    
    Board(){
	for(int i=0;i<8;i++){
	    for(int j=0;j<8;j++){
		stones[i][j] = new Stone();
		if((i==3&&j==3)||(i==4&&j==4)){
		    stones[i][j].setObverse(1);
		}else if((i==4&&j==3)||(i==3&&j==4)){
		    stones[i][j].setObverse(2);
		}
	    }
	}
	direction[0] = new Point(1,0);
	direction[1] = new Point(1,1);
	direction[2] = new Point(0,1);
	direction[3] = new Point(-1,1);
	direction[4] = new Point(-1,0);
	direction[5] = new Point(-1,-1);
	direction[6] = new Point(0,-1);
	direction[7] = new Point(1,-1);

    }

    boolean isOnBoard(int x,int y,int unit_size){
	if(unit_size <= x && x <= unit_size*9 && unit_size <= y && y <=unit_size*9){
	    return true;
	}else{
	    return false;
	}
    }

    boolean isOnBoard2(int x,int y){
	if(x<0||7<x||y<0||7<y){
	    return false;
	}else{
	    return true;
	}
    }

    void setStone(int x,int y,int s){
	stones[x][y].setObverse(s);
    }

    void paint(Graphics g,int unit_size){
	g.setColor(Color.black);
	g.fillRect(0,0,unit_size*10,unit_size*10);

	g.setColor(new Color(0,85,0));
	g.fillRect(unit_size,unit_size,unit_size*8,unit_size*8);

	g.setColor(Color.black);
	for(int i=0;i<9;i++){
	    g.drawLine(unit_size,unit_size*(i+1),unit_size*9,unit_size*(i+1));
	}

	for(int i=0;i<9;i++){
	    g.drawLine(unit_size*(i+1),unit_size,unit_size*(i+1),unit_size*9);
	}

	int mark_size = unit_size/8;
	for(int i=0;i<2;i++){
	    for(int j=0;j<2;j++){
		g.fillRect(unit_size*(3+4*i)-mark_size/2,unit_size*(3+4*j)-mark_size/2,mark_size,mark_size);
	    }
	}

	for(int i=0;i<8;i++){
	    for(int j=0;j<8;j++){
		if(stones[i][j].obverse!=0){
		    Point p = new Point();
		    double x = unit_size*10*3/20+unit_size*i-unit_size/2;
		    double y = unit_size*10*3/20+unit_size*j-unit_size/2;
		    p.setLocation(x,y);
		    stones[i][j].paint(g,p,unit_size/2);
		}
	    }
	}

	
    }

    ArrayList<Integer> getLine(int x,int y,Point d){
	ArrayList<Integer> line = new ArrayList<Integer>();
	int cx = x + d.x;
	int cy = y + d.y;
	
	while(isOnBoard2(cx,cy) && stones[cx][cy].obverse != 0){
	    line.add(stones[cx][cy].obverse);
	    cx += d.x;
	    cy += d.y;
	}

	return line;
    }

    int countReverseStone(int x,int y,int s){
	if(stones[x][y].obverse != 0) return -1;
	int cnt = 0;
	for(int d=0;d<8;d++){
	    ArrayList<Integer> line = new ArrayList<Integer>();
	    line = getLine(x,y,direction[d]);
	    int n=0;
	    while( n < line.size() && line.get(n) != s) n++;
	    if(1 <= n && n < line.size()) cnt += n;
	}
	return cnt;
    }

    void reverseStone(int x,int y,int s){
	for(int d=0;d<8;d++){
	    int flag = 0;
	    int cx = x + direction[d].x;
	    int cy = y + direction[d].y;

	    if(cx < 0 || cx > 7 || cy < 0 || cy >7){
		continue;
	    }

	    if(stones[cx][cy].obverse==s||stones[cx][cy].obverse == 0){
		continue;
	    }

	    while(isOnBoard2(cx,cy) && stones[cx][cy].obverse != 0){
		if(cx+direction[d].x>=0 && cx+direction[d].x <= 7 && cy+direction[d].y >= 0 && cy+direction[d].y <=7){
		    cx += direction[d].x;
		    cy += direction[d].y;
		}else{
		    break;
		}

		if(stones[cx][cy].obverse == s){
		    flag = 1;
		    cx = x + direction[d].x;
		    cy = y + direction[d].y;

		    break;
		}
	    }

	    while(stones[cx][cy].obverse!=s && flag==1){
		setStone(cx,cy,s);
		cx += direction[d].x;
		cy += direction[d].y;
		
		if(cx+direction[d].x < 0 || cx+direction[d].x > 7 || cy+direction[d].y < 0 && cy+direction[d].y > 7){
		    break;
		}
	    }
	}
    }

    void evaluateBoard(){
	num_grid_black = 0;
	num_grid_white = 0;
	
	for(int i=0;i<8;i++){
	    for(int j=0;j<8;j++){
		eval_black[i][j] = countReverseStone(i,j,1);
		
		eval_white[i][j] = countReverseStone(i,j,2);

		if(stones[i][j].obverse==1){
		    num_grid_black++;
		}else if(stones[i][j].obverse==2){
		    num_grid_white++;
		}
	    }
	}
	
    }

    void printBoard(){
	for(int i=0;i<8;i++){
	    for(int j=0;j<8;j++){
		System.out.printf("%2d",stones[i][j]);
	    }
	    System.out.println("");
	}
    }

    void printEval(){
	System.out.println("Black(1):");
	for(int i=0;i<8;i++){
	    for(int j=0;j<8;j++){
		System.out.printf("%2d",eval_black[i][j]);
	    }
	    System.out.println("");
	}

	System.out.println("White(1):");
	for(int i=0;i<8;i++){
	    for(int j=0;j<8;j++){
		System.out.printf("%2d",eval_white[i][j]);
	    }
	    System.out.println("");
	}
    }

    int countStone(int s){
	this.evaluateBoard();
	if(s == 1){
	    return num_grid_black;
	}else{
	    return num_grid_white;
	}
    }
    
}


public class Reversi extends JPanel{
    public final static int UNIT_SIZE = 60;
    private int x,y;
    static Board board = new Board();
    private int turn = 1;
    static JLabel lb1 = new JLabel();
    static JLabel lb2 = new JLabel();
    static String lb2_text = "[黒:" + 2 + ",白:" + 2 + "]";
    

    public Reversi(){
	setPreferredSize(new Dimension(UNIT_SIZE*10,UNIT_SIZE*10));
	addMouseListener(new MouseProc()); 
    }

    public void paintComponent(Graphics g){
	board.paint(g,UNIT_SIZE);
	
    }

    public static void main(String[] args){
	JFrame f = new JFrame();
	f.getContentPane().setLayout(new FlowLayout());
	f.getContentPane().add(new Reversi());

	lb1.setText("黒の番です");
	lb1.setBackground(Color.WHITE);
	lb1.setForeground(Color.BLACK);
	lb1.setOpaque(true);

	lb2.setText(lb2_text);
	lb2.setBackground(Color.WHITE);
	lb2.setForeground(Color.BLACK);
	lb2.setOpaque(true);

	f.getContentPane().add(lb1,BorderLayout.EAST);
	f.getContentPane().add(lb2,BorderLayout.CENTER);
      
	f.pack();
	f.setResizable(false);
	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	f.setVisible(true);

	
       
    }

    void MessageDialog(int black,int white){
	String str;
	if(black > white){
	    str = "[黒:" + String.valueOf(black) + ",白:" + String.valueOf(white) + "]で黒の勝ち";
	}else if(white > black){
	    str = "[黒:" + String.valueOf(black) + ",白:" + String.valueOf(white) + "]で白の勝ち";
	}else{
	    str = "[黒:" + String.valueOf(black) + ",白:" + String.valueOf(white) + "]で引き分け";
	}
	JOptionPane.showMessageDialog(this,str,"message",JOptionPane.INFORMATION_MESSAGE);
	System.exit(0);
    }

    void PassDialog(){
	JOptionPane.showMessageDialog(this,"あなたはパスです","情報",JOptionPane.INFORMATION_MESSAGE);
    }

    void change_turn(){
	board.evaluateBoard();
	
	if(turn==1){
	    int flag = 0;
	    for(int i=0;i<8;i++){
		for(int j=0;j<8;j++){
		    if(board.eval_white[i][j] > 0){
			flag = 1;
		    }
		}
	    }

	    if(flag==1){
		turn = 2;
		lb1.setText("白の番です");
	    }else{
		PassDialog();
		System.out.println("pass");
	    }
	}else{
	    int flag = 0;
	    for(int i=0;i<8;i++){
		for(int j=0;j<8;j++){
		    if(board.eval_black[i][j] > 0){
			flag = 1;
		    }
		}
	    }

	    if(flag==1){
		turn = 1;
		lb1.setText("黒の番です");
	    }else{
		PassDialog();
		System.out.println("pass");
	    }
	}

    }
    
    class MouseProc extends MouseAdapter{
    	public void mouseClicked(MouseEvent me){
    	    Point point = me.getPoint();
    	    int btn = me.getButton();

	    x = point.x/UNIT_SIZE - 1;
	    y = point.y/UNIT_SIZE - 1;

	    board.evaluateBoard();
	
    	    if(btn == MouseEvent.BUTTON1 && board.eval_black[x][y]>0 && turn==1){
    		board.setStone(x,y,1);
		board.reverseStone(x,y,1);

		board.evaluateBoard();

		lb2_text = "[黒:" + String.valueOf(board.num_grid_black) + ",白:" + String.valueOf(board.num_grid_white) + "]";
		lb2.setText(lb2_text);

		repaint();
		change_turn();
    	    }else if(btn == MouseEvent.BUTTON3 && board.eval_white[x][y]>0 && turn == 2){
    		board.setStone(x,y,2);
		board.reverseStone(x,y,2);

		board.evaluateBoard();

		lb2_text = "[黒:" + String.valueOf(board.num_grid_black) + ",白:" + String.valueOf(board.num_grid_white) + "]";
		lb2.setText(lb2_text);

		repaint();
		change_turn();
    	    }
	    
	    

	    int black = board.countStone(1);
	    int white = board.countStone(2);
	    if(black + white == 64){
		MessageDialog(black,white);
	    }

	    
	    board.evaluateBoard();

	    int black_flag = 0;
	    int white_flag = 0;
	    
	    for(int i=0;i<8;i++){
		for(int j=0;j<8;j++){
		    if(board.eval_black[i][j] == 1){
			black_flag = 1;
		    }
		    if(board.eval_white[i][j] == 1){
			white_flag = 1;
		    }
		}   
	    }

	    for(int i=0;i<8;i++){
	    	for(int j=0;j<8;j++){
	    	    System.out.printf("%2d",board.stones[j][i].obverse);
	    	}
	    	System.out.println("");
	    }

	    System.out.println("");
	    
	    for(int i=0;i<8;i++){
	    	for(int j=0;j<8;j++){
	    	    System.out.printf("%2d",board.eval_black[j][i]);
	    	}
	    	System.out.println("");
	    }

	    System.out.println("");

	    for(int i=0;i<8;i++){
	    	for(int j=0;j<8;j++){
	    	    System.out.printf("%2d",board.eval_white[j][i]);
	    	}
	    	System.out.println("");
	    }

	    System.out.println("");

	    System.out.printf("%2d %2d\n",board.num_grid_black,board.num_grid_white);

	    if(black_flag == 0 && white_flag == 0){
		black = board.countStone(1);
		white = board.countStone(2);
		MessageDialog(black,white);
	    }
	    
    	}
    }

}
