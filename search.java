import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.lang.Integer;
class Node{
	String NodeName = null;
	ArrayList<Node> children = new ArrayList<Node>(); //all the children of this node 
	HashMap<String, Double> cost = new HashMap<String, Double>(); //an edge between child and its cost
	HashMap<String, Integer> reliable = new HashMap<String, Integer>();//an edge between child and its reliability
	double dist = 100000000.0; //current min distance from startnode to this node
	Node parent; //parent node 
	void addParent(Node p)
	{
		this.parent = p;
	}
	Node getParent()
	{
		return parent;
	}
}

public class search {
	
	ArrayList<Node> nodes = new ArrayList<Node>(); //all the nodes 
	Node startnode = new Node();
	Node goalnode = new Node();
	//used for ucs alg
	private PriorityQueue<Node> open_ucs = new PriorityQueue<Node>(1,new Comparator<Node>(){
		@Override
		public int compare(Node a, Node b)
	     {
	       double dist_A = a.dist;
	       double dist_B = b.dist;
	       if (dist_B<dist_A)
	            return 1;
	       else if (dist_B>dist_A)
	            return -1;
	       else 
	            return 0; 
	     }
    });
	Queue<Node> open_bfs = new LinkedList<Node>(); 
	Stack<Node> open_dfs = new Stack<Node>();
	static Stack<Node> output_st = new Stack<Node>(); //store nodes in the path from start to goal node
	
	private void UCS_E(String start, String goal, String filename) 
	{
		for(Node n : nodes)
		{
			if(n.NodeName.equals(start))
				startnode = n;
			else if(n.NodeName.equals(goal))
				goalnode = n;
		}
		open_ucs.offer(startnode); //add start node to queue
		startnode.dist = 0.0;
		while (!open_ucs.isEmpty())
		{
			double d;
			Node top = open_ucs.poll(); //get the top node
			File file = new File(filename);
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(file,true)); 
				writer.write(top.NodeName);  // output log
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(top.NodeName+"__"+top.dist);	
			if(top.equals(goalnode)) 
			{ 
				//output path
				while(goalnode.parent != null)
				{
					output_st.push(goalnode);
					goalnode = goalnode.getParent();
				}
				output_st.push(startnode);
				break;
			}			
			for(Node n : top.children )
			{
				if(!n.equals(top.parent)) 					
				{
					//calculate the total cost considering the reliability
					if(top.reliable.get(n.NodeName) == 0)					
						 d = top.dist+top.cost.get(n.NodeName)+0.5;
					else 
						 d = top.dist+top.cost.get(n.NodeName);
					if(d<n.dist) 
					{	
					n.parent = top; //record parent node
					n.dist = d;  //update dist
					if(!open_ucs.contains(n))
					open_ucs.offer(n);	
					}
				}
			}		
		}
	}


	private void UCS(String start, String goal, String filename) 
	{
		for(Node n : nodes)
		{
			if(n.NodeName.equals(start))
				startnode = n;
			else if(n.NodeName.equals(goal))
				goalnode = n;
		}
		open_ucs.offer(startnode);
		startnode.dist = 0.0;
		while (!open_ucs.isEmpty())
		{
			Node top = open_ucs.poll(); //get the top node
			File file = new File(filename);
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(file,true));
				writer.write(top.NodeName);
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(top.NodeName+"__"+top.dist);	
			if(top.equals(goalnode)) 
			{
				while(goalnode.parent != null)
				{
					output_st.push(goalnode);
					goalnode = goalnode.getParent();
				}
				output_st.push(startnode);
				break;
			}			
			for(Node n : top.children )
			{
				if(!n.equals(top.parent)) 					
				{
					double d = top.dist+top.cost.get(n.NodeName);
					if(d<n.dist) 
					{	
						n.parent = top;
						n.dist = d;
						if(!open_ucs.contains(n))
							open_ucs.offer(n);	
					}
				}
			}		
		}
	}


	private void DFS(String start, String goal, String filename) 
	{	
		for(Node n : nodes)
		{
			if(n.NodeName.equals(start))
				startnode = n;
			else if(n.NodeName.equals(goal))
				goalnode = n;
		}
		open_dfs.push(startnode);
		startnode.dist = 0.0;
		while(!open_dfs.isEmpty())
		{
			Node top = open_dfs.pop();
			File file = new File(filename);
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(file,true));
				writer.write(top.NodeName);
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println(top.NodeName+"__"+top.dist);
			if(top.equals(goal)) continue;
			for(Node n : top.children)
			{
				if(!n.equals(top.parent))
				{
					double d = top.dist+top.cost.get(n.NodeName);
					if(d<n.dist)
					{
						n.parent = top;					
						n.dist = d;
						open_dfs.push(n);
					}
				}
			}
		}
		while(goalnode.parent != null)
		{
			output_st.push(goalnode);
			goalnode = goalnode.getParent();
		}
		output_st.push(startnode);
	}


	private void BFS(String start, String goal, String filename) 
	{				
		for(Node n : nodes)
		{
			if(n.NodeName.equals(start))
				startnode = n;
			else if(n.NodeName.equals(goal))
				goalnode = n;
		}
		open_bfs.offer(startnode);
		startnode.dist = 0.0;
		while (!open_bfs.isEmpty())
		{
			Node top = open_bfs.poll(); //get the top node
			File file = new File(filename);
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(file,true));
				writer.write(top.NodeName);
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(top.NodeName+"__"+top.dist);	
			if(top.equals(goalnode)) continue;						
			for(Node n : top.children )
			{
				if(!n.equals(top.parent)) 					
				{
					double d = top.dist+top.cost.get(n.NodeName);
					if(d<n.dist)
					{
						n.parent = top;					
						n.dist = d;
						open_bfs.offer(n);
					}
				}
			}		
		}
		while(goalnode.parent != null)
		{
			output_st.push(goalnode);
			goalnode = goalnode.getParent();
		}
		output_st.push(startnode);
	}

	private void writepathfile(String filename, Stack<Node> st) 
	{
		try{
			BufferedWriter writer;
			File file = new File(filename);
			writer = new BufferedWriter(new FileWriter(file,true));
			while(!st.isEmpty())
			{
				//Node show = st.pop();
				writer.write(st.pop().NodeName);
				writer.newLine();
			}
			
			writer.close();
			
		}catch (IOException e){
			e.printStackTrace();
		}
		
	}

	private void Readfile(String filename) 
	{
		int flag = 0;
		
		try{
			File file = new File(filename);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String strLine = null;
			//int i = 0;
			while ((strLine = reader.readLine()) != null){
				//split every substring line by line in input1.txt using ","
				String[] arr = strLine.split(","); //split line into different string by ","
				int isnew1 = 1;
				int isnew2 = 1;
				if(flag==0)
				{
					Node newnode = new Node();
					Node newnode1 = new Node();
					newnode.NodeName = arr[0];//arr[0] is node name 				
					newnode.children.add(newnode1); //arr[1] is the list of node's children					
					newnode.cost.put(arr[1],Double.parseDouble(arr[2]));//arr[2] is the cost of edge between this node and the child
					newnode.reliable.put(arr[1],Integer.parseInt(arr[3])); //arr[3] is the reliability of edge between this node and the child
					newnode1.NodeName = arr[1];
					newnode1.children.add(newnode);
					newnode1.cost.put(arr[0], Double.parseDouble(arr[2]));
					newnode1.reliable.put(arr[0],Integer.parseInt(arr[3])); 
					nodes.add(newnode);
					nodes.add(newnode1);
					flag = 1;
					continue;
				}
				
				for(Node n : nodes)
				{
					if(n.NodeName.equals(arr[0]))
					{
						isnew1 = 0;
						for(Node n1 : nodes )
						{
							if(n1.NodeName.equals(arr[1]))
							{
								isnew2 = 0;
								n.children.add(n1);
								n.cost.put(arr[1], Double.parseDouble(arr[2]));
								n.reliable.put(arr[1],Integer.parseInt(arr[3]));
								n1.cost.put(arr[0], Double.parseDouble(arr[2]));
								n1.reliable.put(arr[0],Integer.parseInt(arr[3])); 
								n1.children.add(n);
								break;
							}
						}
						if(isnew2 == 1)
						{
							Node newnode1 = new Node();
							newnode1.NodeName = arr[1];
							newnode1.children.add(n);
							newnode1.cost.put(arr[0], Double.parseDouble(arr[2]));
							newnode1.reliable.put(arr[0],Integer.parseInt(arr[3])); 
							nodes.add(newnode1);
							n.children.add(newnode1);
							n.cost.put(arr[1], Double.parseDouble(arr[2]));
							n.reliable.put(arr[1],Integer.parseInt(arr[3]));
							break;
						}
												
					}
				}
				if(isnew1 == 1)
				{
					Node newnode = new Node();
					//newnode.children.add(newnode1); //arr[1] is the list of node's children	
					newnode.NodeName = arr[0];
					newnode.cost.put(arr[1],Double.parseDouble(arr[2]));//arr[2] is the cost of edge between this node and the child
					newnode.reliable.put(arr[1],Integer.parseInt(arr[3])); //arr[3] is the reliability of edge between this node and the child
					nodes.add(newnode);
					for(Node n0 : nodes)
					{
						if(n0.NodeName.equals(arr[1]))
						{
							isnew2 = 0;
							newnode.children.add(n0);
							n0.children.add(newnode);
							n0.cost.put(arr[0],Double.parseDouble(arr[2]));
							n0.reliable.put(arr[0],Integer.parseInt(arr[3]));
							break;
						}
					}
					if(isnew2 == 1)
					{
						
						Node newnode1 = new Node();			
						newnode.children.add(newnode1); //arr[1] is the list of node's children					
						newnode1.NodeName = arr[1];
						newnode1.children.add(newnode);
						newnode1.cost.put(arr[0], Double.parseDouble(arr[2]));
						newnode1.reliable.put(arr[0],Integer.parseInt(arr[3])); 
						nodes.add(newnode1);
					}
				}					
			}			
			reader.close();		
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		String start = args[3];
		String goal = args[5];
		search s = new search();
		s.Readfile(args[7]);
		if(args[1].equals("1")) s.BFS(start,goal,args[11]);
		if(args[1].equals("2")) s.DFS(start,goal,args[11]);
		if(args[1].equals("3")) s.UCS(start,goal,args[11]);
		if(args[1].equals("4")) s.UCS_E(start,goal,args[11]);
		
		s.writepathfile(args[9],output_st);
	}

}
