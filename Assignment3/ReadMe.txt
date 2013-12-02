///////////////////////////Personal Information//////////////////////////

Name: Zhao Zhao              USCID: 6477616884             E-mail: zhaozhao@usc.edu

//////////////////////////Compile&Execute/////////////////////////////
There are 4 files in the folder which are reverse.java, intput1.txt,input2.txt and Readme.txt.
Compile: Log in linux system then input : javac reversi.java
         Then four more files are added. They are reversi.class,Color.class,Move.class and Player.class
Execute: java reversi -t <task> -d < cutting_off_depth> -i <input_file> -op <output_path> -ol <output_log>
Example:java reversi -t 1 -d 3 -i input1.txt -op output1_moves_minimax.txt -ol output1_tlog_ minimax.txt
Result:  Create two new output files and you can check them to check out the result.
tips: If the output file is existed, the new result will append in the content of it and will not erase the init data.


/////////////////////////Programe Intro//////////////////////////////

There are four classes in programe which are Class Move, Class Player,Class Color and Class reversi.
Class Move describes move structure needed and set method .
Class Player describes player structure and initial data.
Class Color describe the color attribute needed in the program.
Class reversi masters the whole programe running.

The program is aim to use minmax and alphabeta algorithms to find best decision in reversi game.


class Move{
	int i ,j;
	public Move(int i, int j)
	public getI();
	public getJ();
	public setMove(int i,int j)
	}
Class Player{
	public Player();
	public void switchColor;
}
enum Color{
	 Black, White, Empty;
}
public class reversi{
main functions:
	maxDecision();
	minDecision();
	minmax();
	alphabeta();
	main();
}




///////////Similarities and differences between task evaluation function////////////
Task 1 and task 2 use the same evaluation function, which is number of pieces can be computed by E(s) = #black - #white. However in task 3 evaluation function turns to E(s) = Weight_black - Weight_white. Every position in the board is assigned a strategic value. In my point of view, evaluation function of task 3 is better because in the actual game, different position in the board do have different weight to facilitate player to win the game. For example, position in four corner is the best place, because sparing two of them you can turn all the piece's color in the line no matter previous stage is. 



