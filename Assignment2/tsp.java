import java.io.*;
import java.util.*;
import java.lang.Math;
class Node{
	Double X;  //coordinate X value
	Double Y; //coordinate Y value
	String name; //node name
	int isVisited = 0;
	double dist = 0; //use by constructing mst
}

class State{
	Node current_city; 
	LinkedList<Node> visited_cities = new LinkedList<Node>();
	double g;//g(n)=path cost
	double h;//h(n)=heuristic
	double f;//f(n)=estimate cost
}

public class tsp {
	ArrayList<Node> nodes = new ArrayList<Node>(); 
	ArrayList<State> states = new ArrayList<State>();
	double cost = 0;
	String path;
	Node startnode = new Node();
	Node endnode = new Node();
	static Queue<Node> output = new LinkedList<Node>(); //used for path output
	static Queue<String> writefile = new LinkedList<String>(); //used for traverse log output
	private PriorityQueue<State> open_Astar = new PriorityQueue<State>(1,new Comparator<State>(){
		@Override
		public int compare(State a, State b)
	     {
	       double dist_A = a.f;
	       double dist_B = b.f;
	       if (dist_B<dist_A)
	            return 1;
	       else if (dist_B>dist_A)
	            return -1;
	       else 
	            return 0; 
	     }
    });
	private double square(double b)
	{
		return b*b;
	}
	
	//Readfile input1.txt and initiate every nodes.
	private void Readfile(String filename)
	{
		try{
			
			File file = new File(filename);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String strLine = null;
			while ((strLine = reader.readLine()) != null){
				Node n = new Node();
				String[] arr = strLine.split(",");
				n.X = Double.parseDouble(arr[1]);
				n.Y = Double.parseDouble(arr[2]);
				n.name = arr[0];
				nodes.add(n);
			}
			reader.close();
			
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	//write output_path file
	private void Writefile(String filename, Queue<Node> q)
	{
		try{
			BufferedWriter writer;
			File file = new File(filename);
			writer = new BufferedWriter(new FileWriter(file,true));
			while(!q.isEmpty())
			{
				writer.write(q.poll().name);
				writer.newLine();
			}
			writer.write("Total Tour Cost: "+cost+"\r");
			writer.close();
			
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	//write output_tlog file
	private void WriteLog(String filename, Queue<String> q)
	{
		try{
			BufferedWriter writer;
			File file = new File(filename);
			writer = new BufferedWriter(new FileWriter(file,true));
			while(!q.isEmpty())
			{
				writer.write(q.poll());
			}
			writer.close();
			
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private void Greedy_search(String start)
	{
		double temp = Double.MAX_VALUE;
		Node tempnode = new Node();
		int count = 0; //how many nodes have been visited
		double dist = 0;	
		for(Node n : nodes)
		{
			if(n.isVisited==1)
				count++;
		}
		if(count!=nodes.size())
		{			
			for(Node n : nodes)
			{
				if(n.name.equals(start))
				{
					//endnode constant equals to start but startnode will change with recursive function
					if(count==0)
					{ 						
						endnode = n;
						path = endnode.name;
					}
					startnode = n;				
					n.isVisited = 1;
					output.offer(startnode);				
					break;
				}
			}
			for(Node n : nodes)
			{
				if(n.isVisited==0)
				{
					//calculate the minmum cost between this node and start node
					dist = Math.sqrt(square(startnode.X-n.X)+square(startnode.Y-n.Y));
					if(dist<temp)
					{
						tempnode = n;
						temp = dist;
					}					
				}
			}
			tempnode.isVisited = 1;
			if(count>0)
			{
				path = path+startnode.name;
				writefile.add(path+","+cost+","+"0"+","+cost);
				writefile.add("\n");
			}
			cost = cost + temp;
			//deal with the last visiting node and the end node
			if(count==nodes.size()-1)
			{
				output.offer(tempnode);
				path = path+tempnode.name;			
				temp = Math.sqrt(square(endnode.X-tempnode.X)+square(endnode.Y-tempnode.Y));
				writefile.add(path+","+cost+","+"0"+","+cost);
				writefile.add("\n");
				output.offer(endnode);
				cost = cost +temp;
				path = path+endnode.name;
				writefile.add(path+","+cost+","+"0"+","+cost);
				writefile.add("\n");
			}			
			Greedy_search(tempnode.name); //recursive method
		}
	}
	
	private void A_SLD(String start)
	{
		double dist = 0;
		for(Node n : nodes)
		{
			//initiate start
			if(n.name.equals(start))
			{
				startnode = n;
				State startstate = new State();
				startstate.current_city = startnode;
				startstate.g = 0;
				startstate.h = 0;
				startstate.f = startstate.g + startstate.h;
				open_Astar.offer(startstate);
				break;
			}								
		}
		while(!open_Astar.isEmpty())
		{
			State s = open_Astar.poll();
			cost = s.f;
			for(int i = 0; i<s.visited_cities.size(); i++)
			{
				writefile.add(s.visited_cities.get(i).name);
			}
			writefile.add(s.current_city.name+","+s.g+","+s.h+","+s.f);
			writefile.add("\n");
			//terminal while loop condition
			if(s.visited_cities.size()==nodes.size()-1) 
			{
				for(int i = 0; i<s.visited_cities.size(); i++)
				{
					writefile.add(s.visited_cities.get(i).name);
				}
				writefile.add(s.current_city.name+startnode.name+","+s.g+","+s.h+","+s.f);
				writefile.add("\n");
				while(!s.visited_cities.isEmpty())
				{
					output.offer(s.visited_cities.poll());
				}
				output.offer(s.current_city);
				output.offer(startnode);			
				break;
			}
			for(Node n : nodes)
			{
				if(!n.equals(startnode)&&!n.equals(s.current_city)&&!s.visited_cities.contains(n))
				{
					dist = Math.sqrt(square(s.current_city.X-n.X)+square(s.current_city.Y-n.Y));
					State newstate = new State();
					newstate.g = s.g + dist;
					newstate.h = Math.sqrt(square(startnode.X-n.X)+square(startnode.Y-n.Y));
					newstate.f = newstate.g + newstate.h;
					newstate.current_city = n;
					for(int i=0 ; i<s.visited_cities.size();i++)
					{
						newstate.visited_cities.add(s.visited_cities.get(i));
					}
					newstate.visited_cities.add(s.current_city);
					open_Astar.offer(newstate);
				}
			}		
		}
	}

	private void A_MST(String start)
	{
		double dist = 0;
		for(Node n : nodes)
		{
			if(n.name.equals(start))
			{
				startnode = n;
				startnode.isVisited = 1;
				State startstate = new State();
				startstate.current_city = startnode;
				startstate.g = 0;
				startstate.h = get_MST(nodes);
				startstate.f = startstate.g + startstate.h;
				open_Astar.offer(startstate);
				break;
			}								
		}
		while(!open_Astar.isEmpty())
		{
			State s = open_Astar.poll();
			cost = s.f;
			for(int i = 0; i<s.visited_cities.size(); i++)
			{
				writefile.add(s.visited_cities.get(i).name);
			}
			writefile.add(s.current_city.name+","+s.g+","+s.h+","+s.f);
			writefile.add("\n");
			if(s.visited_cities.size()==nodes.size()-1) 
			{
				for(int i = 0; i<s.visited_cities.size(); i++)
				{
					writefile.add(s.visited_cities.get(i).name);
				}
				writefile.add(s.current_city.name+startnode.name+","+s.f+","+0.0+","+s.f);
				writefile.add("\n");
				while(!s.visited_cities.isEmpty())
				{
					output.offer(s.visited_cities.poll());
				}
				output.offer(s.current_city);
				output.offer(startnode);			
				break;
			}
			for(Node n : nodes)
			{
				if(!n.equals(startnode)&&!n.equals(s.current_city)&&!s.visited_cities.contains(n))
				{
					dist = Math.sqrt(square(s.current_city.X-n.X)+square(s.current_city.Y-n.Y));
					State newstate = new State();
					newstate.current_city = n;
					//deal with visited cities list and update
					for(int i=0 ; i<s.visited_cities.size();i++)
					{
						newstate.visited_cities.add(s.visited_cities.get(i));
					}
					newstate.visited_cities.add(s.current_city);
					newstate.g = s.g + dist;
					//construct new mst using prim algorithm
					ArrayList<Node> tempnodes = new ArrayList<Node>();
					tempnodes.add(startnode);
					for(Node n1 : nodes)
					{
						if(!newstate.visited_cities.contains(n1))
							tempnodes.add(n1);
					}
					newstate.h = get_MST(tempnodes);
					newstate.f = newstate.g + newstate.h;					
					open_Astar.offer(newstate);
				}
			}		
		}
	}
	
	private double get_MST(ArrayList<Node> mst_nodes)
	{
		Node tempnode = new Node();
		for(Node n : mst_nodes)
		{
			n.dist = Math.sqrt(square(startnode.X-n.X)+square(startnode.Y-n.Y));
		}
		double total_cost = 0;
		for(Node n : mst_nodes)
		{
			if(!n.equals(startnode))
			{
				double temp = Double.MAX_VALUE;
				for(Node n1 : mst_nodes)
				{
					if(n1.dist!=0)
					{
						if(temp>n1.dist)
						{
							temp = n1.dist;
							tempnode = n1;
						}
					}
				}
				total_cost = total_cost+tempnode.dist;
				tempnode.dist = 0;
				for(Node n1 : mst_nodes)
				{
					if(n1.dist!=0)
					{
						if(n1.dist>Math.sqrt(square(tempnode.X-n1.X)+square(tempnode.Y-n1.Y)))
						{
							n1.dist = Math.sqrt(square(tempnode.X-n1.X)+square(tempnode.Y-n1.Y));
						}
					}
				}
			}
			
		}
		return total_cost;	
	}
	
	public static void main(String[] args){
		String start = args[3];
		tsp t = new tsp();
		t.Readfile(args[5]);
		if(args[1].equals("1")) t.Greedy_search(start);
		if(args[1].equals("2")) t.A_SLD(start);
		if(args[1].equals("3")) t.A_MST(start);
		t.Writefile(args[7], output);
		t.WriteLog(args[9], writefile);
	}

}
