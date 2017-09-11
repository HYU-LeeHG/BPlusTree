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
		Tree bptree = new Tree();
		switch(args[0]) {//nullpointer exception �߰�

		//CREATION
		case "-c":
			System.out.println("creation");
			makeDat(args[1],args[2]);
			break;
		
		//INSERTION
		case "-i": // -i index.dat input.csv
			System.out.println("insertion");
			makeTree(args[1], bptree);
			System.out.println("!!!!!!!!!!!! :" + bptree.m);
			getInputfile(args[2],bptree);
			saveTree(bptree,args[1]);
			break;
			
			
		//DELETION
		case "-d":
			System.out.println("deletion");;;
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

	private static void saveTree(Tree bptree, String filename) {
		FileWriter out = null;
		try {
		out = new FileWriter("C:/Users/Lee/eclipse-workspace/bptree/"+filename);
		out.write(String.valueOf(bptree.m) + "\n");
		Vector<Node> nextDepth = new Vector<Node>();
		nextDepth.add(bptree.root);
		findNextDepth(out, nextDepth);
	
		} catch(IOException ioe) {
		} finally {
			try {
				out.close();
			}catch(Exception e) {
			}
		}
		
	}

	private static void findNextDepth(FileWriter out, Vector<Node> curDepth) throws IOException {
		//curDepth -> ���� ����
		//nextDepth -> ���� ���� �ڽĵ��� ����
		try {
			int vs = curDepth.size();	//���� ������ ��� ����
			if(curDepth.elementAt(0).isleaf) {
				for(int i=0;i<vs;i++) {
					out.write(curDepth.elementAt(i).size());
					for(int j=0; j<curDepth.elementAt(i).size();j++) {
						System.out.println(" " + String.valueOf(curDepth.elementAt(i).leafkeyarr.elementAt(j).key) + 
								  " " + String.valueOf(curDepth.elementAt(i).leafkeyarr.elementAt(j).value));
						out.write(" " + String.valueOf(curDepth.elementAt(i).leafkeyarr.elementAt(j).key) + 
								  " " + String.valueOf(curDepth.elementAt(i).leafkeyarr.elementAt(j).value));
					}
					if(i != vs-1)
						out.write(",");
				}
				out.write("\n");
			}
			else {
				Vector<Node> nextDepth = new Vector<Node>();
				for(int i=0;i<vs;i++) {
					out.write(curDepth.elementAt(i).size());
					for(int j=0; j<curDepth.elementAt(i).size();j++) {
						System.out.println(String.valueOf(curDepth.elementAt(i).nonleafkeyarr.elementAt(j).key));
						out.write(" " + String.valueOf(curDepth.elementAt(i).nonleafkeyarr.elementAt(j).key));
						nextDepth.add(curDepth.elementAt(i).nonleafkeyarr.elementAt(j).lcNode);
					}
					if(i != vs-1)
						out.write(",");
					nextDepth.add(curDepth.elementAt(i).rightNode);
				}
				out.write("\n");
				findNextDepth(out, nextDepth);
			}
		} catch(NullPointerException npe) {
			
		}
		
	}

	private static void getInputfile(String filename, Tree bptree) {
		//input.csv�� �޾Ƽ� Tree������ �ִ� �޼���
		//file readline -> search -> insertion �ݺ�
		FileReader in = null;
		try {
			in = new FileReader("C:/Users/Lee/eclipse-workspace/bptree/"+filename);
			BufferedReader br = new BufferedReader(in);
			Vector<String> inputpack = new Vector<String>();
			String rltmp;
			 while((rltmp = br.readLine())!=null) {
				 inputpack.add(rltmp);
			 }

			 for(int i=0; i<inputpack.size();i++ ) {
				 Vector<Node> pathNode = new Vector<Node>();
				 String[] kv = inputpack.elementAt(i).split(",");
				 if (bptree.root != null) {
					 findPath(Integer.parseInt(kv[0]),bptree.root, pathNode);
				 }

				 
				 insertion(Integer.parseInt(kv[0]),Integer.parseInt(kv[1]),bptree,pathNode);
				 System.out.println("=======================");
			 }
		} catch(IOException ioe) {
		} finally {
			try {
				in.close();
			}catch(Exception e) {
			}
		}
		
	}

	private static void findPath(int key, Node node, Vector<Node> pathNode) {
		
		pathNode.add(node);				// method�� leaf��忡 �����ϸ� leaf��带 path�� �߰��ϰ� method ����
		
		if (!(node.isleaf))		// �ƴ϶�� ���� path�� ã�� 	
		{	
			
			int ns = node.nonleafkeyarr.size();
			out:
			for(int i=0; i<ns;i++)
			{
				if (key < node.nonleafkeyarr.elementAt(i).key) {			// goto left
					findPath(key, node.nonleafkeyarr.elementAt(i).lcNode,pathNode);
					break out;
				}
				else if(i == node.nonleafkeyarr.size()-1) {
					findPath(key, node.rightNode, pathNode);
				}
			}			
		}
	}
	//86,,siz =2
	private static void insertion(int key, int value, Tree bptree, Vector<Node> pathnode) {
		
		int i=pathnode.size()-1;			// leaf node (path�� ���� ���� �������)
		if ( i== -1) {		//first input
			Node newRoot = new Node(bptree.m);
			newRoot.isleaf = true;
			newRoot.leafkeyarr.add(new leafPair(key,value));
			bptree.root = newRoot;
		}	
		else{
			int ns = pathnode.elementAt(i).leafkeyarr.size();
			out:
			for(int j=0; j<ns;j++) {
				if(key < pathnode.elementAt(i).leafkeyarr.elementAt(j).key) {
					pathnode.elementAt(i).leafkeyarr.add(j,new leafPair(key,value));
					break out;
				}
				else if(j == pathnode.elementAt(i).leafkeyarr.size()-1) {
					pathnode.elementAt(i).leafkeyarr.add(new leafPair(key,value));
				}
			}
			if (pathnode.elementAt(i).leafkeyarr.size() >= bptree.m) {
				int mid = pathnode.elementAt(i).leafkeyarr.elementAt(bptree.m/2).key; //�߰���
				
				System.out.println(key+" needs ofc, mid is ->"+mid);
				overflowCtrl(true, mid,pathnode,i,bptree);
				
				
			}
			
		}
		
		
	}
	private static boolean insertPair(nonleafPair newpair,Node target) {
		int ns = target.nonleafkeyarr.size();
		out:
		for(int i =0; i<ns;i++)
		{
			if (newpair.key < target.nonleafkeyarr.elementAt(i).key) {
				target.nonleafkeyarr.add(i,newpair);
				break out;
				
			}
			else if (i == target.nonleafkeyarr.size()-1) {
				target.nonleafkeyarr.add(newpair);
			}
		}
		if (target.nonleafkeyarr.size()>=target.m)
			return false;			//pair�� �־��� �� �����÷ο�
		else
			return true;			//�����÷ο� ���Ͼ
	}
  
	private static void overflowCtrl(boolean isleaf, int mid, Vector<Node> pathnode, int nodeidx,Tree bptree) {
		int m = bptree.m;
		Node newChild = new Node(m);
		Node newParent = new Node(m,false);
		nonleafPair newPair = new nonleafPair(mid, new Node(m));
		
		System.out.println("mid = " + mid + "  nodeidx= "+nodeidx);
		if (isleaf) {			//leaf������ �����÷ο�
			for(int i = 0; i< m/2;i++) {	//split�� �߰� ���������� ������ ���ο� childnode�� ����
				
				int newKey = pathnode.elementAt(nodeidx).leafkeyarr.elementAt(0).key;
				System.out.println("case1 : "+ newKey);
				int newValue = pathnode.elementAt(nodeidx).leafkeyarr.elementAt(0).value;
				newChild.leafkeyarr.add(new leafPair(newKey,newValue));
				newChild.isleaf = true;
				pathnode.elementAt(nodeidx).leafkeyarr.remove(0);
			}
			newChild.rightNode = pathnode.elementAt(nodeidx);	//�� �������� ���� �������� ����
			if(nodeidx == 0) {		// leaf == root�� ��Ȳ���� �����÷ο�
				System.out.println("case2");
				newParent.nonleafkeyarr.add(new nonleafPair(mid, newChild));
				newParent.rightNode = pathnode.elementAt(nodeidx);
				bptree.root = newParent;
				
			}else {					// leaf != root
				System.out.println("case3");
				newPair.lcNode = newChild;
				if(!(insertPair(newPair, pathnode.elementAt(nodeidx-1)))) {	//pair�� ��忡 �־��µ� �����÷ο��� ���
					int newmid = pathnode.elementAt(nodeidx-1).nonleafkeyarr.elementAt(m/2).key;
					overflowCtrl(false,newmid,pathnode,nodeidx-1,bptree);
				}
			}
			
		}
		else {					//non leaf������ �����÷ο�
			for(int i = 0; i< m/2;i++) {	//split�� �߰� ���������� ������ ���ο� childnode�� ���� + �߰��� ����
				int newKey = pathnode.elementAt(nodeidx).nonleafkeyarr.elementAt(0).key;
				newChild.nonleafkeyarr.add(new nonleafPair(newKey,new Node()));
				pathnode.elementAt(nodeidx).nonleafkeyarr.remove(0);
			}
			pathnode.elementAt(nodeidx).nonleafkeyarr.remove(0);
			if(nodeidx == 0) {		//root�� ��Ȳ���� �����÷ο�
				System.out.println("case4");
				newParent.nonleafkeyarr.add(new nonleafPair(mid, newChild));
				newParent.rightNode = pathnode.elementAt(nodeidx);
				bptree.root = newParent;
			}else {					// nonleaf��尡 root�� �ƴ� ��Ȳ������ �����÷ο�
				newPair.lcNode = newChild;
				System.out.println("case5");
				if(!(insertPair(newPair, pathnode.elementAt(nodeidx-1)))) {	//pair�� ��忡 �־��µ� �����÷ο��� ���
					int newmid = pathnode.elementAt(nodeidx-1).nonleafkeyarr.elementAt(m/2).key;
					overflowCtrl(false,newmid,pathnode,nodeidx-1,bptree);
				}
			}
		}
			
	}

	private static void makeTree(String filename, Tree bptree) {
		String rltmp;//readLineTmp
		FileReader in = null;
		try {
		in = new FileReader("C:/Users/Lee/eclipse-workspace/bptree/"+filename);
		BufferedReader br = new BufferedReader(in);
		bptree.m = Character.getNumericValue(in.read());
		System.out.println("????????: " + bptree.m);
		br.readLine();
		Vector<String> linepack = new Vector<String>();
		
		Node newNode;
		while((rltmp =  br.readLine())!=null) {
			linepack.add(rltmp);
		}
		for(int r=0; r<linepack.size();r++)
		{
			int oldstd =0 ;
			System.out.println(r+"====");
			//=======================One line===================================================
			String[] nodepack = linepack.elementAt(r).split(",");
			for(int c=0; c<nodepack.length;c++)
			{	//===================One Node===================================================
				newNode = new Node(true);
				System.out.println(c);
				String[] eachKey = nodepack[c].split(" ");
				int m = Integer.parseInt(eachKey[0]); //����� Ű �Ǵ� Ű,�� ����� ��
				if (eachKey.length -1 >m) { 
					// =======================leaf node=========================================
					
					for(int n =1; n<m+1 ;n++) {
						newNode.leafkeyarr.add(new leafPair(Integer.parseInt(eachKey[2*n-1])
									  						,Integer.parseInt(eachKey[2*n])));
					}
					if (linepack.size() == 1) { //line = 1 �̸� root=leaf
						bptree.root = newNode;
					
					}else {
						System.out.println("newleaf");
						int std = newNode.leafkeyarr.elementAt(0).key;
						if (c!=0) {						//rightNode Link
							nodeLinkR(bptree.root,oldstd,newNode);
						}
						nodeLink(bptree.root,std,newNode);
						
					}
					oldstd = Integer.parseInt(eachKey[1]);
					System.out.println(oldstd);
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

	private static void nodeLinkR(Node stdnode, int oldstd, Node newnode) {
		if (stdnode.isleaf == true) {
			if (stdnode.leafkeyarr.elementAt(0).key == oldstd)
				stdnode.rightNode = newnode;
		}
		else {
			for(int i=0; i<stdnode.nonleafkeyarr.size();i++)
			{
				if (oldstd < stdnode.nonleafkeyarr.elementAt(i).key) {			// goto left
					nodeLinkR(stdnode.nonleafkeyarr.elementAt(i).lcNode,oldstd,newnode);
				}
				else if(i == stdnode.nonleafkeyarr.size()-1) {
					nodeLinkR(stdnode.rightNode,oldstd,newnode);
				}
			}
		}
		
	}

	private static void nodeLink(Node stdnode, int std, Node newnode) {

		for(int i=0; i<stdnode.nonleafkeyarr.size();i++)
		{
			if (std < stdnode.nonleafkeyarr.elementAt(i).key) {			// goto left
				if (stdnode.nonleafkeyarr.elementAt(i).lcNode ==null) {
					stdnode.nonleafkeyarr.elementAt(i).lcNode = newnode;
					newnode.m = stdnode.m;
				}
				else {
					nodeLink(stdnode.nonleafkeyarr.elementAt(i).lcNode,std,newnode);
					
				}
			}
			else if(i == stdnode.nonleafkeyarr.size()-1) {
				if (stdnode.rightNode == null) {
					stdnode.rightNode= newnode;
					newnode.m = stdnode.m;
				}
				else
					nodeLink(stdnode.rightNode,std,newnode);
			}
		}
	}

	private static void makeDat(String filename, String bnum) {
		FileWriter out = null;
		try {
		out = new FileWriter("C:/Users/Lee/eclipse-workspace/bptree/"+filename);
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
		FileWriter out = null;
		try {
		out = new FileWriter("C:/Users/Lee/eclipse-workspace/bptree/"+filename);;
		out.write(String.valueOf(123415));
		} catch(IOException ioe) {
		} finally {
			try {
				out.close();
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
		root.m = num;
	}
	
}

class Node{
	boolean isleaf; //true = leaf, false= non-leaf
	Vector<leafPair> leafkeyarr;
	Vector<nonleafPair> nonleafkeyarr;
	Node rightNode;	//non-leaf -> right child node ,, leaf ->rightmost node
	int m;
	public Node() {
		leafkeyarr = new Vector<leafPair>();
		nonleafkeyarr = new Vector<nonleafPair>();
		int m;
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
	public Node (int pm) {
		m = pm;
		leafkeyarr = new Vector<leafPair>();
		nonleafkeyarr = new Vector<nonleafPair>();
	}
	public Node (int pm, boolean il) {
		m = pm;
		isleaf = il;
		leafkeyarr = new Vector<leafPair>();
		nonleafkeyarr = new Vector<nonleafPair>();
	}
	public int size() {
		if (isleaf)
			return this.leafkeyarr.size();
		else
			return this.nonleafkeyarr.size();
	}
}
