import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
class Move{
	int i = 0;
	int j = 0;
	public Move(int i,int j)
	{
		this.i = i;
		this.j = j;
	}
	public int getI(){
		return i;
	}
	public int getJ(){
		return j;
	}
	public void setMove(int i, int j){
		this.i = i;
		this.j = j;
	}
}

class Player{
	Color color;
	public Player(Color color)
	{
		this.color = color;
	}
	public void switchColor()
	{
		if(this.color == Color.Black)
		{
			this.color = Color.White;
		}
		else
			this.color = Color.Black;
	}
}
enum Color{
	 Black, White, Empty;
}
public class reversi {
	static Color board[][] = new Color[8][8];
	int weight[][] = {
			{99,-8,8,6,6,8,-8,99},
			{-8,-24,-4,-3,-3,-4,-24,-8},
			{8,-4,7,4,4,7,-4,8},
			{6,-3,4,0,0,4,-3,6},
			{6,-3,4,0,0,4,-3,6},
			{8,-4,7,4,4,7,-4,8},
			{-8,-24,-4,-3,-3,-4,-24,-8},
			{99,-8,8,6,6,8,-8,99},
	};
	int maxDepth;
	int select;
	int score = 0;
	int step = 0;
	boolean haveLegalMove = true;
	Player player = new Player(Color.Black);
	Move finalMove = new Move(-1, -1);
	Move lastMove = new Move(-1,-1);
	int parentScore = Integer.MIN_VALUE;
	boolean CUTT_OFF = true;
	HashMap<Integer,ArrayList<Move>> table = new HashMap<Integer, ArrayList<Move>>();
	ArrayList<Color[][]> outputBoard = new ArrayList<Color[][]>();
	ArrayList<String> outputLog = new ArrayList<String>();
	public reversi(int select,int maxDepth){
		this.select = select;
		this.maxDepth = maxDepth;
		if(select == 1)
		{
			outputLog.add("Node,Depth,Value");
			outputLog.add("root 1 -Infinity");
		}
		else
		{
			outputLog.add("Node,Depth,Value,Alpha,Beta");
			outputLog.add("root 1 -Infinity -Infinity Infinity");
		}
		
	}
	
	//read input file and store them into board array
	private void readFile(String filename)
	{
		try{			
			File file = new File(filename);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String strLine = null;
			int line = 0;
			while ((strLine = reader.readLine()) != null)
			{
				for(int i=0 ; i<8 ;i++)
				{
					char temp = strLine.charAt(i);
					if(temp == '*')
					{
						board[line][i] = Color.Empty;
					}
					else if(temp == 'X')
					{
						board[line][i] = Color.Black;
					}
					else if(temp == 'O')
					{
						board[line][i] = Color.White;
					}				
				}
				line++;
			}
			reader.close();			
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private void writeResult(String filename)
	{
		Player player = new Player(Color.Black);
		outputBoard.add(board);
		try{
			BufferedWriter writer;
			File file = new File(filename);
			writer = new BufferedWriter(new FileWriter(file,true));
			while(!outputBoard.isEmpty())
			{
				Color state[][] = outputBoard.remove(0);
				step++;
				writer.write("STEP = "+step);
				writer.newLine();
				if(player.color == Color.Black)
					writer.write("BLACK");
				else 
					writer.write("WHITE");
				player.switchColor();
				writer.newLine();
				for(int i=0; i<8; i++)
				{
					for(int j=0 ;j<8; j++)
					{
						Color c = state[i][j];
						if(c==Color.Black)
							writer.write("X");
						if(c==Color.White)
							writer.write("O");
						if(c==Color.Empty)
							writer.write("*");
					}
					writer.newLine();
				}
				writer.newLine();
			}
			writer.write("Game End");
			writer.close();
			
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private void writeLog(String filename)
	{		
		try{
			BufferedWriter writer;
			File file = new File(filename);
			writer = new BufferedWriter(new FileWriter(file,true));
			while(!outputLog.isEmpty())
			{
				writer.write(outputLog.remove(0));
				writer.newLine();
			}
			writer.close();
			
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	//calculate piece number of black or white or empty
	private int getPieceNum(Color state[][], Color color)
	{
		int num = 0;
		//int total = 0;
		for(int i=0; i<8; i++)
		{
			for(int j=0; j<8; j++)
			{
				if(state[i][j]==color) 
				{
					if(select == 3)
					{
						num = num + weight[i][j];
					}
					else if(select == 1||select == 2)
					{
						num++;
					}
				}
									
			}
		}
		return num;
	}
	
	//copy board
	private void copy(Color state[][], Color newState[][])
	{
		for(int i=0; i<8; i++)
		{
			for(int j=0; j<8; j++)
			{
				newState[i][j] = state[i][j];
			}
		}
	}
	
	//verify if game ends
	private boolean endCondition(Color state[][])
	{
		int count = 0;
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				if(state[i][j]==Color.Empty)
					count++;
			}
		}
		if(count==0)
			return true;
		else
			return false;
		
	}
	
	//switch square color
	private void switchColor(Color state[][], int i, int j)
	{
		if(state[i][j] == Color.Black)
			state[i][j] = Color.White;
		else
			state[i][j] = Color.Black;
	}
	

	//get all the legal moves in the board
	private ArrayList<Move> getLegalMove(Color state[][], Color color)
	{
		ArrayList<Move> legalMoves = new ArrayList<Move>(); 
		for(int i=0; i<8; i++)
		{
			for(int j=0; j<8; j++)
			{
				if(testLegal(state,i,j,color))
				{
					legalMoves.add(new Move(i,j));
				}
			}
		}
		return legalMoves;
	}
	
	//
	private boolean testLegal(Color state[][], int i, int j, Color color)
	{
		//valid coordinates
		if(i < 0||i >= 8||j < 0||j >= 8)
			return false;
		//squire is empty, that is no black or white
		if(state[i][j] != Color.Empty)
			return false;
		//it produce at least a switch
		if (testSingleDirection(state, i, j, -1, -1, color)) {
            return true;
	    }
	    if (testSingleDirection(state, i, j, -1, 0, color)) {
	        return true;
	    }
	    if (testSingleDirection(state, i, j, -1, +1, color)) {
	        return true;
	    }
	    if (testSingleDirection(state, i, j, 0, -1, color)) {
	        return true;
	    }
	    if (testSingleDirection(state, i, j, 0, +1, color)) {
	        return true;
	    }
	    if (testSingleDirection(state, i, j, +1, -1, color)) {
	        return true;
	    }
	    if (testSingleDirection(state, i, j, +1, 0, color)) {
	        return true;
	    }
	    if (testSingleDirection(state, i, j, +1, +1, color)) {
	        return true;
	    }
	    return false;
	}
	
	private boolean testSingleDirection(Color state[][], int i, int j, int v, int h, Color color)
	{
		Color oppositeColor;
		if(color == Color.Black)
			oppositeColor = Color.White;
		else
			oppositeColor = Color.Black;
		boolean atLeastOne = false;
		while(i+v<8 && i+v>=0 && j+h<8 && j+h>=0 && state[i+v][j+h]==oppositeColor)
		{
			i = i+v;
			j = j+h;
			atLeastOne = true;
		}
		if(i+v<8 &&i+v>=0 && j+h<8 &&j+h>=0 && atLeastOne && state[i+v][j+h]==color)
		{
			return true;
		}
		return false;
	}
	
	private void applyMove(Color state[][], Move move)
	{
		int i = move.getI();
		int j = move.getJ();
		if(i==-1&&j==-1)
			return;
		Color color = player.color;
		state[i][j] = color;
		if (testSingleDirection(state, i, j, -1, -1, color)) {
            switchSingleDirection(state,i, j, -1, -1, color);
	    }
	    if (testSingleDirection(state, i, j, -1, 0, color)) {
	        switchSingleDirection(state, i, j, -1, 0, color );
	    }
	    if (testSingleDirection(state, i, j, -1, +1, color)) {
	        switchSingleDirection(state, i, j, -1, +1, color);
	    }
	    if (testSingleDirection(state, i, j, 0, -1, color)) {
	        switchSingleDirection(state, i, j, 0, -1, color);
	    }
	    if (testSingleDirection(state, i, j, 0, +1, color)) {
	            switchSingleDirection(state, i, j, 0, +1, color);
	    }
	    if (testSingleDirection(state,i, j, +1, -1, color)) {
	            switchSingleDirection(state, i, j, +1, -1, color);
	    }
	    if (testSingleDirection(state, i, j, +1, 0, color)) {
	            switchSingleDirection(state, i, j, +1, 0, color);
	    }
	    if (testSingleDirection(state, i, j, +1, +1, color)) {
	            switchSingleDirection(state, i, j, +1, +1, color);
	    }
	}
	
	private void switchSingleDirection(Color state[][], int i, int j, int v, int h, Color color)
	{
		Color oppositeColor;
		if(color == Color.Black)
			oppositeColor = Color.White;
		else
			oppositeColor = Color.Black;
		while(state[i+v][j+h] == oppositeColor)
		{
			switchColor(state,i+v,j+h);
			i = i+v;
			j = j+h;
		}
	}
	private void show(Move move, int depth, int score)
	{
		String s = addLine(move,depth,score);
		outputLog.add(s);
		//System.out.println(s);
	}
	private void show(Move move,int depth, int score, int alpha,int beta)
	{
		String s = addAlphaBetaLine(move,depth,score,alpha,beta);
		outputLog.add(s);
		//System.out.println(s);
	}
	private void show(Move move, int depth, int score, int alpha, int beta, boolean CUTT_OFF)
	{
		String s = addAlphaBetaLine(move,depth,score,alpha,beta,CUTT_OFF);
		outputLog.add(s);
		//System.out.println(s);
	}
	private String addLine(Move move, int depth, int score)
	{
		String s = null;
		String sc = null;		
		if(score == Integer.MAX_VALUE)
			sc = "Infinity";
		if(score == Integer.MIN_VALUE)
			sc = "-Infinity";
		if(score!=Integer.MAX_VALUE&&score!=Integer.MIN_VALUE){
			sc = String.valueOf((double)score);
		}
		if(move.i==-1)
		{
			s = "root"+" "+String.valueOf(depth)+" "+sc;
		}
		if(move.i != -1)
		{
			char i = (char)('a'+move.j);
			int j = 1+move.i;
			s = String.valueOf(i)+String.valueOf(j)+" "+String.valueOf(depth)+" "+sc;
		}	
		return s;
	}

	private String addAlphaBetaLine(Move move, int depth, int score, int alpha, int beta)
	{
		String s = null;
		String sc = null;
		if(score == Integer.MAX_VALUE)
			sc = "-Infinity";
		if(score == Integer.MIN_VALUE)
			sc = "Infinity";
		if(score!=Integer.MAX_VALUE && score!=Integer.MIN_VALUE){
			sc = String.valueOf((double)score);
		}
		String sa = null;
		if(alpha == Integer.MAX_VALUE)
			sa = "Infinity";
		if(alpha == Integer.MIN_VALUE)
			sa = "-Infinity";
		if(alpha!=Integer.MAX_VALUE&&alpha!=Integer.MIN_VALUE){
			sa = String.valueOf((double)alpha);
		}
		String sb = null;
		if(beta == Integer.MAX_VALUE)
			sb = "Infinity";
		if(beta == Integer.MIN_VALUE)
			sb = "-Infinity";
		if(beta!=Integer.MAX_VALUE&&beta!=Integer.MIN_VALUE){
			sb = String.valueOf((double)beta);
		}
		if(move.i==-1)
		{
			s = "root"+" "+String.valueOf(depth)+" "+sc+" "+sa+" "+sb;
		}
		if(move.i != -1)
		{
			char i = (char)('a'+move.j);
			int j = 1+move.i;
			s = String.valueOf(i)+String.valueOf(j)+" "+String.valueOf(depth)+" "+sc+" "+sa+" "+sb;
		}	
		
		return s;	
	}

	private String addAlphaBetaLine(Move move, int depth, int score, int alpha, int beta, boolean CUTT_OFF)
	{
		String s = null;
		char i = (char)('a'+move.j);
		int j = 1+move.i;
		String sc = null;
		if(score == Integer.MAX_VALUE)
			sc = "-Infinity";
		if(score == Integer.MIN_VALUE)
			sc = "Infinity";
		if(score!=Integer.MAX_VALUE && score!=Integer.MIN_VALUE){
			sc = String.valueOf((double)score);
		}
		String sa = null;
		if(alpha == Integer.MAX_VALUE)
			sa = "Infinity";
		if(alpha == Integer.MIN_VALUE)
			sa = "-Infinity";
		if(alpha!=Integer.MAX_VALUE&&alpha!=Integer.MIN_VALUE){
			sa = String.valueOf((double)alpha);
		}
		String sb = null;
		if(beta == Integer.MAX_VALUE)
			sb = "Infinity";
		if(beta == Integer.MIN_VALUE)
			sb = "-Infinity";
		if(beta!=Integer.MAX_VALUE&&beta!=Integer.MIN_VALUE){
			sb = String.valueOf((double)beta);
		}
		s = String.valueOf(i)+String.valueOf(j)+" "+String.valueOf(depth)+" "+sc+" "+sa+" "+sb+" "+"CUT-OFF";
		return s;
	
	}
	private int maxEvaluation(Color state[][],int depth,Move lastMove,int parentScore)
	{
		int score = 0;
		if(endCondition(state))
		{	
			score = getPieceNum(state,Color.Black) - getPieceNum(state,Color.White);
			player.switchColor();
		}
		else
		{
			ArrayList<Move> legalMove = getLegalMove(state, player.color);
			table.put(depth, legalMove);
			if(legalMove.size() == 0)
			{
				score = getPieceNum(state,Color.Black) - getPieceNum(state,Color.White);
				show(lastMove,depth-1,score);
				player.switchColor();
				finalMove.setMove(-1, -1);
			}
			else
			{
				while(!table.get(depth).isEmpty())
				{
					Color newState[][] = new Color[8][8];
					copy(state,newState);
					Move move = table.get(depth).remove(0);
					applyMove(newState,move);
					score = getPieceNum(newState,Color.Black) - getPieceNum(newState,Color.White);
					if(score>parentScore)
					{
						parentScore = score;
					}
					show(move,depth,score);
					show(lastMove,depth-1,parentScore);					
				}
				score = parentScore;
				player.switchColor();
			}
		}
		return score;
	}
	
	private int maxDecision(int depth,Color state[][],Move lastMove,int parentScore)
	{
		int maxScore = Integer.MIN_VALUE;		
		depth++;
		if(depth>maxDepth)
		{ 
			maxScore = maxEvaluation(state,depth,lastMove,maxScore);
		}
		else 
		{
			ArrayList<Move> legalMove = getLegalMove(state,player.color);
			table.put(depth, legalMove);
			if(legalMove.size() == 0)
			{
				maxScore = maxEvaluation(state,depth,lastMove,parentScore);
			}
			else
			{				
				Move tempMove = new Move(-1,-1);
				while(!table.get(depth).isEmpty())
				{
					Move move = table.get(depth).remove(0);
					int score = Integer.MAX_VALUE;
					Color newState[][] = new Color[8][8];
					copy(state,newState);
					applyMove(newState,move);
					player.switchColor();
					show(move,depth,score);
					parentScore = minDecision(depth,newState,move,0-parentScore);					
					if(parentScore > maxScore)
					{
						maxScore = parentScore;
						tempMove.i = move.i;
						tempMove.j = move.j;										
					}
					show(lastMove,depth-1,maxScore);					
				}
				player.switchColor();
				finalMove.setMove(tempMove.i, tempMove.j);
			}			
		}		
		return maxScore;		
	}

	private int minEvaluation(Color state[][],int depth, Move lastMove, int parentScore)
	{
		int score;
		if(endCondition(state))
		{
			score = getPieceNum(state,Color.Black) - getPieceNum(state,Color.White);
			player.switchColor();
		}
		else
		{
			ArrayList<Move> legalMove = getLegalMove(state, player.color);
			table.put(depth, legalMove);
			if(legalMove.size() == 0)
			{
				score = getPieceNum(state,Color.Black) - getPieceNum(state,Color.White);
				show(lastMove,depth-1,score);
				player.switchColor();
				finalMove.setMove(-1, -1);
			}
			else
			{
				while(!table.get(depth).isEmpty())
				{
					Color newState[][] = new Color[8][8];
					copy(state,newState);
					Move move = table.get(depth).remove(0);
					applyMove(newState,move);
					score = getPieceNum(newState,Color.Black) - getPieceNum(newState,Color.White);
					if(score<parentScore)
					{
						parentScore = score;
					}
					show(move,depth,score);
					show(lastMove,depth-1,parentScore);
				}
				score = parentScore;
				player.switchColor();
			}
	
		}
		return score;
	}

	private int minDecision(int depth, Color state[][],Move lastMove,int parentScore) 
	{
		int minScore = Integer.MAX_VALUE;
		depth++;
		if(depth>maxDepth)
		{
			minScore = minEvaluation(state,depth,lastMove,minScore);
		}
		else
		{
			ArrayList<Move> legalMove = getLegalMove(state, player.color);
			table.put(depth, legalMove);
			if(legalMove.size() == 0)
			{
				minScore = minEvaluation(state,depth,lastMove,parentScore);
			}
			else
			{	
				Move tempMove = new Move(-1,-1);
				while(!table.get(depth).isEmpty())
				{
					Move move = table.get(depth).remove(0);
					int score = Integer.MIN_VALUE;
					Color newState[][] = new Color[8][8];
					copy(state,newState);
					applyMove(newState,move);
					player.switchColor();				
					show(move,depth,score);
					parentScore = maxDecision(depth,newState,move,0-parentScore);					
					if(parentScore<minScore)
					{
						minScore = parentScore;					
						tempMove.i = move.i;
						tempMove.j = move.j;
					}
					show(lastMove,depth-1,minScore);
				}
				player.switchColor();
				finalMove.setMove(tempMove.i, tempMove.j);
			}			
		}
		return minScore;		
	}

	private int maxABEvaluation(Color state[][],int depth,int alpha,int beta,Move lastMove,int parentScore)
	{
		int score = 0;
		if(endCondition(state))
		{
		
			score = getPieceNum(state,Color.Black) - getPieceNum(state,Color.White);
			player.switchColor();
		}
		else
		{
			ArrayList<Move> legalMove = getLegalMove(state, player.color);
			table.put(depth, legalMove);
			if(legalMove.size() == 0)
			{
				score = getPieceNum(state,Color.Black) - getPieceNum(state,Color.White);
				show(lastMove,depth-1,score,alpha,beta);
				player.switchColor();
				finalMove.setMove(-1, -1);
			}
			else
			{
				//int tempScore = Integer.MIN_VALUE;
				while(!table.get(depth).isEmpty())
				{
					Color newState[][] = new Color[8][8];
					copy(state,newState);
					Move move = table.get(depth).remove(0);
					applyMove(newState,move);
					score = getPieceNum(newState,Color.Black) - getPieceNum(newState,Color.White);
					if(score>parentScore)
					{
						parentScore = score;						
					}
					show(move,depth,score,alpha,beta);
					alpha = parentScore;
					if(alpha>=beta)
					{
						show(lastMove,depth-1,parentScore,alpha,beta,CUTT_OFF);
						break;
					}
					else{
						show(lastMove,depth-1,parentScore,alpha,beta);
					}
					
				}
				score = parentScore;
				player.switchColor();
			}
		}
		return score;
	}
	private int maxDecision(int depth, Color state[][], int alpha,int beta,Move lastMove,int parentScore)
	{
		int maxScore = Integer.MIN_VALUE;		
		depth++;
		if(depth>maxDepth)
		{
			maxScore = maxABEvaluation(state,depth,alpha,beta,lastMove,maxScore);
			if(maxScore > alpha)
				alpha = maxScore;
		}
		else 
		{
			ArrayList<Move> legalMove = getLegalMove(state,player.color);
			table.put(depth, legalMove);
			if(legalMove.size() == 0)
			{
				maxScore = maxABEvaluation(state,depth,alpha,beta,lastMove,maxScore);
				if(maxScore > alpha)
					alpha = maxScore;
			}
			else
			{				
				Move tempMove = new Move(-1,-1);
				while(!table.get(depth).isEmpty())
				{
					Move move = table.get(depth).remove(0);
					score = Integer.MIN_VALUE;
					Color newState[][] = new Color[8][8];
					copy(state,newState);
					applyMove(newState,move);
					player.switchColor();
					show(move,depth,score,alpha,beta);
					score = minDecision(depth,newState,alpha,beta,move,0-parentScore);					
					if(score > alpha)
					{
						alpha = score;
						tempMove.i = move.i;
						tempMove.j = move.j;
										
					}
					if(alpha>=beta)
					{
						show(lastMove,depth-1,score,alpha,beta,CUTT_OFF);
						break;
					}
					else
					{
						show(lastMove,depth-1,alpha,alpha,beta);
					}
				}
				player.switchColor();
				finalMove.setMove(tempMove.i, tempMove.j);
			}			
		}		
		return alpha;		
	}

	private int minABEvaluation(Color state[][],int depth,int alpha,int beta,Move lastMove,int parentScore)
	{

		int score = 0;
		if(endCondition(state))
		{
			score = getPieceNum(state,Color.Black) - getPieceNum(state,Color.White);
			player.switchColor();
		}
		else
		{
			ArrayList<Move> legalMove = getLegalMove(state, player.color);
			table.put(depth, legalMove);
			if(legalMove.size() == 0)
			{
				score = getPieceNum(state,Color.Black) - getPieceNum(state,Color.White);
				show(lastMove,depth-1,score,alpha,beta);
				player.switchColor();
				finalMove.setMove(-1, -1);
			}
			else
			{
				//int tempScore = Integer.MAX_VALUE;
				while(!table.get(depth).isEmpty())
				{
					Color newState[][] = new Color[8][8];
					copy(state,newState);
					Move move = table.get(depth).remove(0);
					applyMove(newState,move);
					score = getPieceNum(newState,Color.Black) - getPieceNum(newState,Color.White);
					if(score<parentScore)
					{
						parentScore = score;
					}
					show(move,depth,score,alpha,beta);
					beta = parentScore;
					//show(lastMove,depth-1,parentScore,alpha,beta);
					if(alpha>=beta)
					{
						show(lastMove,depth-1,parentScore,alpha,beta,CUTT_OFF);
						break;
					}
					else{
						show(lastMove,depth-1,parentScore,alpha,beta);
					}
				}
				score = parentScore;
				player.switchColor();
			}

		}
		return score;
	
	}
	private int minDecision(int depth, Color state[][], int alpha, int beta, Move lastMove,int parentScore)
	{
		int minScore = Integer.MAX_VALUE;
		depth++;
		if(depth>maxDepth)
		{
			minScore = minABEvaluation(state,depth,alpha,beta,lastMove,minScore);
			//if(minScore<beta)
				beta = minScore;			
		}
		else
		{
			ArrayList<Move> legalMove = getLegalMove(state, player.color);
			table.put(depth, legalMove);
			if(legalMove.size() == 0)
			{
				minScore = minABEvaluation(state,depth,alpha,beta,lastMove,parentScore);
				if(minScore<beta)
					beta = minScore;
			}
			else
			{	
				Move tempMove = new Move(-1,-1);
				while(!table.get(depth).isEmpty())
				{
					Move move = table.get(depth).remove(0);
					score = Integer.MAX_VALUE;
					Color newState[][] = new Color[8][8];
					copy(state,newState);
					applyMove(newState,move);
					player.switchColor();
					show(move,depth,score,alpha,beta);
					score = maxDecision(depth,newState,alpha,beta,move,0-parentScore);
					if(score<beta)
					{
						beta = score;					
						tempMove.i = move.i;
						tempMove.j = move.j;
					}					
					if(alpha>=beta)
					{
						show(lastMove,depth-1,score,alpha,beta,CUTT_OFF);
						break;
					}
					else
					{
						show(lastMove,depth-1,beta,alpha,beta);
					}
						
				}
				player.switchColor();
				finalMove.setMove(tempMove.i, tempMove.j);
			}			
		}
		return beta;
	}

	private void Minmax()
	{
		while(!endCondition(board))
		{			
			if(player.color == Color.Black)
			{
				int parentScore = Integer.MIN_VALUE;
				Color temp[][] = new Color[8][8];
				copy(board,temp);
				outputBoard.add(temp);
				int f = maxDecision(1,board,lastMove,parentScore);
				player.switchColor();
				applyMove(board,finalMove);
				player.switchColor();	
				//showBoard(board);
				Minmax();
			}
			else
			{
				int parentScore = Integer.MAX_VALUE;
				Color temp[][] = new Color[8][8];
				copy(board,temp);
				outputBoard.add(temp);
				int g = minDecision(1,board,finalMove,parentScore);
				player.switchColor();
				applyMove(board,finalMove);
				player.switchColor();			
			    Move finalMove1 = new Move(-1, -1);
			    show(finalMove1,1,g);
			    //showBoard(board);
				Minmax();
			}				
		}
	}
	
	private void AlphaBeta()
	{
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		while(!endCondition(board))
		{			
			if(player.color == Color.Black)
			{
				int parentScore = Integer.MIN_VALUE;
				Color temp[][] = new Color[8][8];
				copy(board,temp);
				outputBoard.add(temp);
				int f = maxDecision(1,board,alpha,beta,lastMove,parentScore);
				player.switchColor();
				applyMove(board,finalMove);
				player.switchColor();
				//showBoard(board);
				AlphaBeta();
			}
			else
			{
				int parentScore = Integer.MAX_VALUE;
				Color temp[][] = new Color[8][8];
				copy(board,temp);
				outputBoard.add(temp);
				int g = minDecision(1,board,alpha,beta,finalMove,parentScore);
				player.switchColor();
				applyMove(board,finalMove);
			    player.switchColor();
				Move finalMove1 = new Move(-1, -1);
				show(finalMove1,1,g,g,beta);
				parentScore = g;
			    //showBoard(board);
				AlphaBeta();
			}				
		}
	}
	
	private void showBoard(Color board[][])
	{
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				if(board[i][j]==Color.Black)
					System.out.print("X");
				else if(board[i][j]==Color.White)
					System.out.print("O");
				else if(board[i][j]==Color.Empty)
					System.out.print("*");			
			}
			System.out.println("");
		}
		System.out.println("end");
	}
	
	public static void main(String args[])
	{		
		reversi r = new reversi(Integer.parseInt(args[1]),Integer.parseInt(args[3]));
		r.readFile(args[5]);
		if(args[1].equals("1"))
		{
			r.Minmax();
		}
		if(args[1].equals("2")||args[1].equals("3"))
		{
			r.AlphaBeta();
		}		
		r.writeResult(args[7]);
		r.writeLog(args[9]);
		}	
}
