C///////////////////////////Personal Information//////////////////////////

Name: Zhao Zhao             E-mail: zhaozhao@usc.edu

//////////////////////////Compile&Execute/////////////////////////////
There are 3 files in the folder, which are bfs_ucs.java, input.txt and output.txt(output file is empty at first).
Compile: Log in linux system then input : javac search.java
         Then there will be a file which is search.class.
Execute: java search -t <task> -s <start_node> -g <goal_node> -i <input_file> -op <output_path> -ol <output_log>
Result:  Create two new output files and you can check them to check out the result.
tips: If the output file is existed, the new result will append in the content of it and will not erase the init data.


/////////////////////////Programe Intro//////////////////////////////

There are two classes in programe which are Class Node and Class search.
Class Node describes data structure needed and initiate variable .
Class search masters the whole programe running.

The program is aim to find the shortest path for start node to goal node.
We use bfs, dfs, ucs and ucs with reliability 4 algorithms to figure out the shortest path.


class Node{
	String NodeName = null;
	ArrayList<Node> children = new ArrayList<Node>(); //all the children of this node 
	HashMap<String, Double> cost = new HashMap<String, Double>(); //an edge between child and its cost
	HashMap<String, Integer> reliable = new HashMap<String, Integer>();//an edge between child and its reliability
	double dist = 100000000.0; //current min distance from startnode to this node
	Node parent; //parent node 
	void addParent(Node p)
	Node getParent()
}


class search:

FUNC:
	void readfile();//read input file, and initial data.
	void bfs(); //execute bfs algro
	void ucs(); //execute dfs algro
        void ucs(); //execute ucs algro
        void ucs_e();//execute ucs with reliability algro
	public static void main(String args[])
	 choose algro;

///////////Similarities and differences between UCS and UCS with reliability////////////
The search method is the same. The differences is once expand a node we should also consider the reliability of the edges between this node and its children. That is the cost from start node to this node increases due to the unreliability. The added edge cost may cause different path from source to goal. 





