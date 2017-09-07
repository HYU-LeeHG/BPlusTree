package bptree;
import java.io.*;
import java.lang.*;
import java.util.Vector;


class leafPair {
	int key;
	int value;
	
	public leafPair(int k, int v) {
		key = k;
		value = v;
	}
}
class nonleafPair {
	int key;
	Node lcNode; //left-child-node
	
	public nonleafPair(int k, Node n) {
		key = k;
		lcNode = n;
	}
}

public class Main {

	public final int MAX = 100;
	
	public static void main(String[] args) {
		
		switch(args[0]) {//nullpointer exception 추가

		//CREATION
		case "-c":
			System.out.println("creation");
			makeDat(args[1],args[2]);
			break;
		
		//INSERTION
		case "-i": // -i index.dat input.csv
			System.out.println("insertion");
			
			Tree bptree = new Tree();
			makeTree(args[1], bptree);
			getInputfile(args[2],bptree);
			int c = bptree.root.nonleafkeyarr.elementAt(0).lcNode.nonleafkeyarr.elementAt(0).lcNode.leafkeyarr.elementAt(0).key;
			System.out.println("20 ? ->"+c);
			break;
			
			
		//DELETION
		case "-d":
			System.out.println("deletion");
			break;
		case "-s":
			System.out.println("search");
			break;
		case "-r":
			System.out.println("range search");
			break;
			
		case "-t":
			Test(args[1]);
			break;
		default : 
			System.out.println("Wrong CommandLine");
		}
	
	}

	private static void getInputfile(String filename, Tree bptree) {
		//input.csv를 받아서 Tree구조에 넣는 메서드
		//file readline -> search -> insertion 반복
		FileReader in = null;
		try {
			in = new FileReader(filename);
			BufferedReader br = new BufferedReader(in);
			Vector<String> inputpack = new Vector<String>();
			String rltmp;
			 while((rltmp = br.readLine())!=null) {
				 inputpack.add(rltmp);
			 }
			 for(int i=0; i<inputpack.size();i++ ) {
				 String[] kv = inputpack.elementAt(i).split(",");
				 insertion(Integer.parseInt(kv[0]),Integer.parseInt(kv[1]),bptree);
			 }
		} catch(IOException ioe) {
		} finally {
			try {
				in.close();
			}catch(Exception e) {
			}
		}
		
	}

	private static void insertion(int key, int value,Tree bptree) {
		
	}

	private static void makeTree(String filename, Tree bptree) {
		String rltmp;//readline temp
		FileReader in = null;
		try {
		in = new FileReader(filename);
		BufferedReader br = new BufferedReader(in);
		bptree.m = Character.getNumericValue(in.read());
		br.readLine();
		Vector<String> linepack = new Vector<String>();
		
		Node newNode ;
		while((rltmp =  br.readLine())!=null) {
			linepack.add(rltmp);
		}
		for(int r=0; r<linepack.size();r++)
		{
			System.out.println(r+"====");
			//=======================One line===================================================
			String[] nodepack = linepack.elementAt(r).split(",");
			
			for(int c=0; c<nodepack.length;c++)
			{	//===================One Node===================================================
				System.out.println(c);
				String[] eachKey = nodepack[c].split(" ");
				int m = Integer.parseInt(eachKey[0]); //노드의 키 또는 키,값 페어의 수
				if (eachKey.length -1 >m) { 
					// =======================leaf node=========================================
					newNode = new Node(true);
					for(int n =1; n<m+1 ;n++) {
						newNode.leafkeyarr.add(new leafPair(Integer.parseInt(eachKey[2*n-1])
									  						,Integer.parseInt(eachKey[2*n])));
					}
					if (linepack.size() == 1) { //line = 1 이면 root=leaf
						bptree.root = newNode;
					
					}else {
						System.out.println("newleaf");
						int std = newNode.leafkeyarr.elementAt(0).key;
						nodeLink(bptree.root,std,newNode);
						
					}
				} else{						
					// ========================non leaf node====================================
					newNode = new Node(false);
					for(int n =1;n<m+1;n++) {
						newNode.nonleafkeyarr.add(new nonleafPair(Integer.parseInt(eachKey[n])
																  ,null));
					}
					if (r==0) { //root case
						bptree.root = newNode;
					} else {
						int std = newNode.nonleafkeyarr.elementAt(0).key;
						nodeLink(bptree.root,std,newNode);
					}
						
					
				}
				
			}
		}
		
		
		} catch(IOException ioe) {
		} finally {
			try {
				in.close();
			}catch(Exception e) {
			}
		}
		
		
	}

	private static void nodeLink(Node stdnode, int std, Node newnode) {

		for(int i=0; i<stdnode.nonleafkeyarr.size();i++)
		{
			if (std < stdnode.nonleafkeyarr.elementAt(i).key) {			// goto left
				if (stdnode.nonleafkeyarr.elementAt(i).lcNode ==null) {
					stdnode.nonleafkeyarr.elementAt(i).lcNode = newnode;
				}
				else {
					nodeLink(stdnode.nonleafkeyarr.elementAt(i).lcNode,std,newnode);
					
				}
			}
			else if(i == stdnode.nonleafkeyarr.size()-1) {
				if (stdnode.rightNode == null) {
					stdnode.rightNode= newnode; 
				}
				else
					nodeLink(stdnode.rightNode,std,newnode);
			}
		}
	}

	private static void makeDat(String filename, String bnum) {
		FileWriter out = null;
		try {
		out = new FileWriter(filename);
		out.write(bnum);
		Tree bptree = new Tree();
		} catch(IOException ioe) {
		} finally {
			try {
				out.close();
			}catch(Exception e) {
			}
		}
		
	}
	private static void Test(String filename) {
		FileReader in = null;
		try {
		in = new FileReader(filename);
		BufferedReader br = new BufferedReader(in);
		System.out.println(Character.getNumericValue(in.read()));
		System.out.println(br.readLine());
		System.out.println(br.readLine());
		System.out.println(br.readLine());
		}catch(IOException ioe){
			
		} finally {
			try {
				in.close();
			}catch(Exception e) {
			}
		}
	}
	
	

}


class Tree{
	public int m;
	Node root;
	public Tree() {
	}
	
	public Tree(int num){
		m = num;
	}
	
}

class Node{
	boolean isleaf; //true = leaf, false= non-leaf
	Vector<leafPair> leafkeyarr;
	Vector<nonleafPair> nonleafkeyarr;
	Node rightNode;	//non-leaf -> right child node ,, leaf ->rightmost node
	
	public Node() {
		leafkeyarr = new Vector<leafPair>();
		nonleafkeyarr = new Vector<nonleafPair>();
	}
	public Node(boolean leaftest) {
		isleaf = leaftest;
		if (isleaf) {
			leafkeyarr = new Vector<leafPair>();
		}
		else {
			nonleafkeyarr = new Vector<nonleafPair>();
		}
	}
	public Node(Node node) {
		isleaf = node.isleaf;
		leafkeyarr = node.leafkeyarr;
		nonleafkeyarr = node.nonleafkeyarr ;
		rightNode = node.rightNode;
	}
}
