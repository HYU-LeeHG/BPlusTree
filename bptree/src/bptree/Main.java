package bptree;
import java.io.*;
import java.lang.*;
import java.util.Vector;

import javax.swing.plaf.synth.SynthSplitPaneUI;


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
			getInputfile(args[2],bptree);
			saveTree(bptree,args[1]);
			break;
			
			
		//DELETION
		case "-d":
			System.out.println("deletion");
			makeTree(args[1], bptree);
			getDeletefile(args[2],bptree);
			saveTree(bptree,args[1]);			
		case "-s":
			System.out.println("search");
			break;
		case "-r":
			System.out.println("range search");
			break;
			
		case "-t":
			
			boolean a = false;
			if(!(a))
				System.out.println("asdasd");
			
			break;
		default : 
			System.out.println("Wrong CommandLine");
		}
	
	}

	private static void getDeletefile(String filename, Tree bptree) {
		//delete.csv�� �޾Ƽ� Tree������ �ִ� �޼���
		//file readline -> search -> deletion �ݺ�
		FileReader in = null;
		try {
			in = new FileReader("C:/Users/Lee/eclipse-workspace/bptree/"+filename);
			BufferedReader br = new BufferedReader(in);
			Vector<String> deletepack = new Vector<String>();
			String rltmp;
			 while((rltmp = br.readLine())!=null) {
				 deletepack.add(rltmp);
			 }

			 for(int i=0; i<deletepack.size();i++ ) {
				 Vector<Node> pathNode = new Vector<Node>();
				 int delkey = Integer.parseInt(deletepack.elementAt(i));
				 if (bptree.root != null) {
					 findPath(delkey,bptree.root, pathNode);
				 }						 
				 deletion(delkey,bptree,pathNode);
				 
				 }
		} catch(IOException ioe) {
		} finally {
				try {
					in.close();
				}catch(Exception e) {
			}
		}
		
	}

	private static void deletion(int key, Tree bptree, Vector<Node> pathnode) {

		int i=pathnode.size()-1;			// leaf node (path�� ���� ���� �������)
		
		if ( i== -1) {		//first input
			System.out.println("Tree is empty");
			System.exit(0);
		}	
		else{
			int ns = pathnode.elementAt(i).leafkeyarr.size();
			out:
			for(int j=0; j<ns;j++) {
				if(key == pathnode.elementAt(i).leafkeyarr.elementAt(j).key) {
					pathnode.elementAt(i).leafkeyarr.remove(j);
					break out;
				}
			}
			//i==0�̸� ����=��Ʈ�� Ʈ���Ƿ� ���� �ϳ��̻��̸� �ȴ�.
			//���� 0���� empty tree�� �Ǵ� ���̹Ƿ� underflowCtrl�� ���ʿ�
			if (i!=0){	
				if (pathnode.elementAt(i).leafkeyarr.size() < (bptree.m-1)/2) {
					underflowCtrl(true, key, pathnode,i,bptree);
	
				}
			}
		}
		
		
		
	}

	private static void underflowCtrl(boolean isleaf, int key, Vector<Node> pathnode, int idx, Tree bptree) {
		// underflow�� ��尡 �����϶�
		if(idx == 0) {
			if(key < pathnode.elementAt(idx).nonleafkeyarr.elementAt(0).key)
				bptree.root = pathnode.elementAt(idx).nonleafkeyarr.elementAt(0).lcNode;
			else
				bptree.root = pathnode.elementAt(idx).rightNode;
		}
			
		out:
		if(isleaf) {
			//���� : 	1���ʳ�尡�����������̻��ΰ� -> 2�����ʳ�尡�����������̻��ΰ�-> 
			//		3���ʳ����պ�->4���������պ� ->�θ����Ǻ��ʿ�������-> ����Ű�� ������->
			//		�θ��尡 ����÷��ΰ�? ->���
			out1:
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					if(i==0)
						break out1;
					int lcSize = pathnode.elementAt(idx-1).	// ��������� ������������� ������
							nonleafkeyarr.elementAt(i-1).lcNode.leafkeyarr.size();
					
					//���ʳ���� �� ������ n/2���� ������
					if(lcSize>(bptree.m-1)/2) {
						//���ʳ���� ������ key,value
						int moveKey =pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).
								lcNode.leafkeyarr.elementAt(lcSize-1).key;
						int moveVal = pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).
								lcNode.leafkeyarr.elementAt(lcSize-1).value;
						
						// ���ʳ���� key,value�� ������忡 �߰�
						pathnode.elementAt(idx).leafkeyarr.add(new leafPair(moveKey, moveVal));
						
						//���ʳ���� ������key,value ����
						pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).
						lcNode.leafkeyarr.remove(lcSize-1);
						keyChange(pathnode,idx,key);
						break out;
					}
				}
			}
			
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					
					
					//�ڿ� �� �̻� nonleafkeyPair�� ������ rightNode Ȯ��
					if(i==pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
						if(pathnode.elementAt(idx-1).rightNode.leafkeyarr.size()>(bptree.m-1)/2) {
							int moveKey =pathnode.elementAt(idx-1).rightNode.leafkeyarr.elementAt(0).key;
							int moveVal = pathnode.elementAt(idx-1).rightNode.leafkeyarr.elementAt(0).value;
							
							// �����ʳ���� key,value�� ������忡 �߰�
							pathnode.elementAt(idx).leafkeyarr.add(new leafPair(moveKey, moveVal));
							
							//�����ʳ���� ������key,value ����
							pathnode.elementAt(idx-1).rightNode.leafkeyarr.remove(0);
							keyChange(pathnode,idx,key);
							break out;
						}
					}
					else {
						int rcSize = pathnode.elementAt(idx-1).	// ��������� ����������������� ������
								nonleafkeyarr.elementAt(i+1).lcNode.leafkeyarr.size();
						
						//�����ʳ���� �� ������ n/2���� ������
						if(rcSize>(bptree.m-1)/2) {
							//�����ʳ���� �Ǿ� key,value
							int moveKey =pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i+1).
									lcNode.leafkeyarr.elementAt(0).key;
							int moveVal = pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i+1).
									lcNode.leafkeyarr.elementAt(0).value;
							
							// �����ʳ���� key,value�� ������忡 �߰�
							pathnode.elementAt(idx).leafkeyarr.add(new leafPair(moveKey, moveVal));
							
							//�����ʳ���� ������key,value ����
							pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i+1).
							lcNode.leafkeyarr.remove(0);
							keyChange(pathnode,idx,key);
							break out;
						}
					}
				}
			}
			
			//�޳��� ���� (���ʳ�尡 �����ǰ� ���� �������� �ű�)
			out2:
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					if(i==0)		//������尡 ���� ���ʳ��� �޳�尡 �����Ƿ� Ż��
						break out2;
					int delKey = key;
					int lcSize = pathnode.elementAt(idx-1).	// ��������� ������������� ������
							nonleafkeyarr.elementAt(i-1).lcNode.leafkeyarr.size();
					
					if(pathnode.elementAt(idx).leafkeyarr.size() != 0)
						delKey = pathnode.elementAt(idx).leafkeyarr.elementAt(0).key;
						
					for(int j=0;j<lcSize;j++) {
						int moveKey = pathnode.elementAt(idx-1).	// ��������� ������������� j��° Ű
								nonleafkeyarr.elementAt(i-1).lcNode.leafkeyarr.elementAt(j).key;
						int moveVal = pathnode.elementAt(idx-1).	// ��������� ������������� j��° ��
								nonleafkeyarr.elementAt(i-1).lcNode.leafkeyarr.elementAt(j).value;
						pathnode.elementAt(idx).leafkeyarr.add(j, new leafPair(moveKey,moveVal));
					}
					
					boolean delsuccess = deletePair(delKey,pathnode.elementAt(idx-1));
					keyChange(pathnode,idx,key);
					if(!(delsuccess)) {
						underflowCtrl(false,delKey,pathnode,idx-1,bptree);
					} 
					break out;
				}
			}
			
			//�������� ���� (������尡 �����ǰ� �������� �ű�)
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					int delKey;
					//�ڿ� �� �̻� nonleafkeyPair�� ������ rightNode Ȯ��
					if(i==pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
						delKey = pathnode.elementAt(idx-1).rightNode.leafkeyarr.elementAt(0).key;
						for(int j=0;j<pathnode.elementAt(idx).leafkeyarr.size();j++) {
							int moveKey = pathnode.elementAt(idx).leafkeyarr.elementAt(j).key;
							int moveVal = pathnode.elementAt(idx).leafkeyarr.elementAt(j).value;
							
							pathnode.elementAt(idx-1).rightNode.leafkeyarr.add(j, new leafPair(moveKey, moveVal)); 
						}
					}
					else {
						delKey = pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i+1).
								lcNode.leafkeyarr.elementAt(0).key;
						for(int j=0;j<pathnode.elementAt(idx).leafkeyarr.size();j++) {
							int moveKey = pathnode.elementAt(idx).leafkeyarr.elementAt(j).key;
							int moveVal = pathnode.elementAt(idx).leafkeyarr.elementAt(j).value;
							
							pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i+1)
							.lcNode.leafkeyarr.add(j, new leafPair(moveKey, moveVal)); 
						}
						
					}
					boolean delsuccess = deletePair(delKey,pathnode.elementAt(idx-1));
					keyChange(pathnode,idx,key);
					if(!(delsuccess)) {
						underflowCtrl(false,delKey,pathnode,idx-1,bptree);
					} 
					break out;
				}
			}
		
		}
		
		// underflow�� ��尡 �����϶�
		else {
			// 1���� : ���ʿ��� ��� ��� ( ������������� ���� �� pair�� ������忡 ��� )
			out1:
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					if(i==0)
						break out1;
					int lcSize = pathnode.elementAt(idx-1).	// ��������� ������������� ������
							nonleafkeyarr.elementAt(i-1).lcNode.nonleafkeyarr.size();
					
					//���ʳ���� �� ������ n/2���� ������
					if(lcSize>(bptree.m-1)/2) {
						Node tmpNode = new Node();
						
						//���ʿ��� �� ���� left child = �޳���� rightNode
						//�޳���� rightNode =���ʿ��� �� ���� left child		�� ü���� 
						tmpNode= pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).lcNode.rightNode;
						pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).lcNode.rightNode =
								pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).lcNode
								.nonleafkeyarr.elementAt(lcSize-1).lcNode;
						pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).lcNode
						.nonleafkeyarr.elementAt(lcSize-1).lcNode = tmpNode;
						
						insertPair(pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).lcNode
								.nonleafkeyarr.elementAt(lcSize-1), pathnode.elementAt(idx));
						
						keyChange(pathnode,idx,key);
						break out;
					}
				}
			}
			// 2���� : �����ʿ��� ��� ���
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					
					
					//�ڿ� �� �̻� nonleafkeyPair�� ������ rightNode Ȯ��
					if(i==pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
						if(pathnode.elementAt(idx-1).rightNode.nonleafkeyarr.size()>(bptree.m-1)/2) {
							Node tmpNode= new Node();
							tmpNode = pathnode.elementAt(idx-1).rightNode.nonleafkeyarr.elementAt(0).lcNode;
							pathnode.elementAt(idx-1).rightNode.nonleafkeyarr.elementAt(0).lcNode 
																= pathnode.elementAt(idx).rightNode;
							pathnode.elementAt(idx).rightNode = tmpNode;
							
							insertPair(pathnode.elementAt(idx-1).rightNode.nonleafkeyarr.elementAt(0)
									   ,pathnode.elementAt(idx));
							
							keyChange(pathnode,idx,key);
							break out;
						}
					}
					else {
						int rcSize = pathnode.elementAt(idx-1).	// ��������� ����������������� ������
								nonleafkeyarr.elementAt(i+1).lcNode.nonleafkeyarr.size();
						
						//�����ʳ���� �� ������ n/2���� ������
						if(rcSize>(bptree.m-1)/2) {
							Node tmpNode= new Node();
							tmpNode = pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i+1).lcNode
									.nonleafkeyarr.elementAt(0).lcNode;
							pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i+1).lcNode
							.nonleafkeyarr.elementAt(0).lcNode	= pathnode.elementAt(idx).rightNode;
							pathnode.elementAt(idx).rightNode = tmpNode;
							
							insertPair(pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i+1).lcNode
									   .nonleafkeyarr.elementAt(0), pathnode.elementAt(idx));
							
							keyChange(pathnode,idx,key);
							break out;
						}
					}
				}
			}
			// 3���� : �����̶� ����
			out2:
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					if(i==0)		//������尡 ���� ���ʳ��� �޳�尡 �����Ƿ� Ż��
						break out2;
					int lcSize = pathnode.elementAt(idx-1).	// ��������� ������������� ������
							nonleafkeyarr.elementAt(i-1).lcNode.nonleafkeyarr.size();
					for(int j=0;j<lcSize;j++) {
						pathnode.elementAt(idx).nonleafkeyarr.add(j,
								pathnode.elementAt(idx-1).nonleafkeyarr.
								elementAt(i-1).lcNode.nonleafkeyarr.elementAt(j));
					}
					pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).lcNode = 
							pathnode.elementAt(idx-1).nonleafkeyarr.
							elementAt(i-1).lcNode.rightNode;
					pathnode.elementAt(idx).nonleafkeyarr.add(lcSize,
							pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1));	
					pathnode.elementAt(idx-1).nonleafkeyarr.remove(i-1);
					
					keyChange(pathnode,idx,key);
					if(pathnode.elementAt(idx-1).nonleafkeyarr.size() < (bptree.m-1)/2) {
						int delKey = pathnode.elementAt(idx).nonleafkeyarr.elementAt(0).key;
						underflowCtrl(false,delKey,pathnode,idx-1,bptree);
					} 
					break out;
				}
			}
			
			 
			// 4���� : �������̶� ����
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					int delKey;
					//�ڿ� �� �̻� nonleafkeyPair�� ������ rightNode Ȯ��
					if(i==pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
						int dSize = pathnode.elementAt(idx).nonleafkeyarr.size();	// ��������� ������
						for(int j=0;j<dSize;j++) {
							pathnode.elementAt(idx).rightNode.nonleafkeyarr.add(j,
									pathnode.elementAt(idx-1).nonleafkeyarr.
									elementAt(i-1).lcNode.nonleafkeyarr.elementAt(j));
						}
						pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).lcNode = 
								pathnode.elementAt(idx).rightNode;
						pathnode.elementAt(idx-1).rightNode.nonleafkeyarr.add(dSize,
								pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i));
						pathnode.elementAt(idx-1).nonleafkeyarr.remove(i);
						
						
						 
					}
					else {
						int rcSize = pathnode.elementAt(idx-1).	// ��������� ����������������� ������
								nonleafkeyarr.elementAt(i+1).lcNode.leafkeyarr.size();
						for(int j=0;j<rcSize;j++) {
							pathnode.elementAt(idx).nonleafkeyarr.elementAt(i+1).
							lcNode.nonleafkeyarr.add(j,pathnode.elementAt(idx).nonleafkeyarr.elementAt(j));
						}
						pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).lcNode = 
								pathnode.elementAt(idx).rightNode;
						pathnode.elementAt(idx).nonleafkeyarr.elementAt(i+1).lcNode.nonleafkeyarr.add(rcSize,
								pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i));
						pathnode.elementAt(idx-1).nonleafkeyarr.remove(i);						
					}
					keyChange(pathnode,idx,key);
					if(pathnode.elementAt(idx-1).nonleafkeyarr.size() < (bptree.m-1)/2) {
						delKey = pathnode.elementAt(idx).nonleafkeyarr.elementAt(0).key;
						underflowCtrl(false,delKey,pathnode,idx-1,bptree);
					} 
					break out;
				}
			}
			
			
		}
		
	}
	
	//������ Ű�� �ִ� ���� ���� ��� path�� Ű���� Ÿ������ �˻�
	private static void keyChange(Vector<Node> pathnode, int idx, int delkey) {
		out:
		for(int i=0; i<pathnode.size();i++) {
		//for�� inside = i���� path node (0�� ��Ʈ)
			
			//������忡���� ��ȭ�� underflowCtrl()���� ����
			//���� non leaf���� �Ǻ�
			if(pathnode.elementAt(i).isleaf) {
				break out;
			}
			
			for(int j=0;j<pathnode.elementAt(i).size();j++) {
				if(delkey < pathnode.elementAt(i).nonleafkeyarr.elementAt(j).key) {
					if(j!=0) {	//j==0�̸� ������ ���⶧����
						pathnode.elementAt(i).nonleafkeyarr.elementAt(j-1).key =
								FindRNearest(pathnode.elementAt(i).nonleafkeyarr.elementAt(j).lcNode);
					}
				}
				
				//rightNode�� case	
				else if(j==pathnode.elementAt(i).size()-1) {
					pathnode.elementAt(i).nonleafkeyarr.elementAt(j).key = 
							FindRNearest(pathnode.elementAt(i).rightNode);
				}
			}
			
		}
			
		
	}

	private static int FindRNearest(Node stdnode) {
		if (stdnode.isleaf) {
			//leaf��忡 �����ϸ� ���� ������, �� �迭�� 0��° Ű�� ����
			return stdnode.leafkeyarr.elementAt(0).key;
		}
		
		else {
			//non leaf����� �� ����� ���� ����� ������ ���� ã��(���)
			
			//���س�尡 ������ non leaf Pair�� �ϳ��� ���ٸ� ������忡 ���� ����� �����ʰ��� ����
			if (stdnode.nonleafkeyarr.size() == 0)
				return FindRNearest(stdnode.rightNode);
			
			//size�� 1�̻��̸� ���� ���� Left Child�� ���� ���������ʰ��� ����
			else
				return FindRNearest(stdnode.nonleafkeyarr.elementAt(0).lcNode);
		}
	}

	private static boolean deletePair(int delKey, Node target) {
		for(int i =0; i<target.nonleafkeyarr.size();i++) {
			if(target.nonleafkeyarr.elementAt(i).key == delKey)
				target.nonleafkeyarr.remove(i);
		}
		if(target.nonleafkeyarr.size()<(target.m-1)/2)
			return false;
		else
			return true;
	}

	private static void search(Tree bptree, int num) {	
		searchMethod(bptree.root, num);
		
	}

	private static void searchMethod(Node stdnode, int num) {
		if (stdnode.isleaf == true) {
			System.out.println("leaf!" + stdnode.leafkeyarr.size());
			for(int i = 0 ; i<stdnode.leafkeyarr.size();i++) {
				if (stdnode.leafkeyarr.elementAt(i).key == num)
					System.out.println(num);
				else
					System.out.println(stdnode.leafkeyarr.elementAt(i).key);
			}
		}
		else {
			out:
			for(int i=0; i<stdnode.nonleafkeyarr.size();i++)
			{
				
				if (num < stdnode.nonleafkeyarr.elementAt(i).key) {			// goto left
					System.out.println(stdnode.nonleafkeyarr.elementAt(i).key);
					searchMethod(stdnode.nonleafkeyarr.elementAt(i).lcNode,num);
					break out;
				}
				else if(i == stdnode.nonleafkeyarr.size()-1) {
					System.out.println(stdnode.nonleafkeyarr.elementAt(i).key + " >>R");
					searchMethod(stdnode.rightNode,num);
				}
			}
		}
		
	}

	private static void saveTree(Tree bptree, String filename) {
		PrintWriter out = null;
		try {
		out = new PrintWriter(new FileWriter("C:/Users/Lee/eclipse-workspace/bptree/"+filename));
		out.println(String.valueOf(bptree.m));
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

	private static void findNextDepth(PrintWriter out, Vector<Node> curDepth) throws IOException {
		//curDepth -> ���� ����
		//nextDepth -> ���� ���� �ڽĵ��� ����
		try {
			int vs = curDepth.size();	//���� ������ ��� ����
			if(curDepth.elementAt(0).isleaf) {
				System.out.println("LEAF");
				for(int i=0;i<vs;i++) {
					out.print(curDepth.elementAt(i).size());
					for(int j=0; j<curDepth.elementAt(i).size();j++) {
						out.print(" " + String.valueOf(curDepth.elementAt(i).leafkeyarr.elementAt(j).key) + 
								  " " + String.valueOf(curDepth.elementAt(i).leafkeyarr.elementAt(j).value));
					}
					if(i != vs-1)
						out.print(",");
				}
				out.println();
			}
			else {
				System.out.println("NON-LEAF");
				Vector<Node> nextDepth = new Vector<Node>();
				for(int i=0;i<vs;i++) {
					out.print(curDepth.elementAt(i).size());
					for(int j=0; j<curDepth.elementAt(i).size();j++) {
						out.print(" " + String.valueOf(curDepth.elementAt(i).nonleafkeyarr.elementAt(j).key));
						nextDepth.add(curDepth.elementAt(i).nonleafkeyarr.elementAt(j).lcNode);
					}
					if(i != vs-1) {
						out.print(",");
					}nextDepth.add(curDepth.elementAt(i).rightNode);
				}
				
				out.println();
				System.out.println("size = " + nextDepth.size());
				if (nextDepth.size() == 5)
					System.out.println(nextDepth.elementAt(0).nonleafkeyarr.size());
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
				 if (Integer.parseInt(kv[0]) ==9) {
					 for(int k =0 ; k<pathNode.size();k++) {
						 if(pathNode.elementAt(k).isleaf)
							 System.out.println(pathNode.elementAt(k).leafkeyarr.elementAt(0).key);
						 else
							 System.out.println(pathNode.elementAt(k).nonleafkeyarr.elementAt(0).key);
					 }
				 }

				 
				 insertion(Integer.parseInt(kv[0]),Integer.parseInt(kv[1]),bptree,pathNode);
				 
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
				overflowCtrl(true, mid,pathnode,i,bptree);

			}
			
		}
		
		
	}
	
	private static void overflowCtrl(boolean isleaf, int mid, Vector<Node> pathnode, int nodeidx,Tree bptree) {
		int m = bptree.m;
		int newKey, newValue;
		
		Node newChild = new Node(m);
		Node newParent = new Node(m,false);
		nonleafPair newPair = new nonleafPair(mid, null);
		
		if (isleaf) {			//leaf������ �����÷ο�
			for(int i = 0; i< m/2;i++) {	//split�� �߰� ���������� ������ ���ο� childnode�� ����
				
				newKey = pathnode.elementAt(nodeidx).leafkeyarr.elementAt(0).key;
				newValue = pathnode.elementAt(nodeidx).leafkeyarr.elementAt(0).value;
				newChild.leafkeyarr.add(new leafPair(newKey,newValue));
				newChild.isleaf = true;
				pathnode.elementAt(nodeidx).leafkeyarr.remove(0);
			}
			newChild.rightNode = pathnode.elementAt(nodeidx);	//�� �������� ���� �������� ����
			if(nodeidx == 0) {		// leaf == root�� ��Ȳ���� �����÷ο�
				newParent.nonleafkeyarr.add(new nonleafPair(mid, newChild));
				newParent.rightNode = pathnode.elementAt(nodeidx);
				bptree.root = newParent;
				
			}else {					// leaf != root
				newPair.lcNode = newChild;
				if(!(insertPair(newPair, pathnode.elementAt(nodeidx-1)))) {	//pair�� ��忡 �־��µ� �����÷ο��� ���
					int newmid = pathnode.elementAt(nodeidx-1).nonleafkeyarr.elementAt(m/2).key;
					System.out.println("over & over : " + newmid);
					overflowCtrl(false,newmid,pathnode,nodeidx-1,bptree);
				}
			}
			
		}
		else {					//non leaf������ �����÷ο�
			for(int i = 0; i< m/2;i++) {	//split�� �߰� ���������� ������ ���ο� childnode�� ���� + �߰��� ����
				newKey = pathnode.elementAt(nodeidx).nonleafkeyarr.elementAt(0).key;
				newChild.nonleafkeyarr.add(
						new nonleafPair(newKey,pathnode.elementAt(nodeidx).nonleafkeyarr.elementAt(0).lcNode));
				newChild.isleaf = false;
				pathnode.elementAt(nodeidx).nonleafkeyarr.remove(0);
			}
			newChild.rightNode = pathnode.elementAt(nodeidx).nonleafkeyarr.elementAt(0).lcNode;
			pathnode.elementAt(nodeidx).nonleafkeyarr.remove(0);
			if(mid == 26) {
				System.out.println("t" + newChild.nonleafkeyarr.elementAt(0).lcNode.leafkeyarr.elementAt(0).key);
			}
			
			if(nodeidx == 0) {		//root�� ��Ȳ���� �����÷ο�
				newParent.nonleafkeyarr.add(new nonleafPair(mid, newChild));
				newParent.isleaf = false;
				newParent.rightNode = pathnode.elementAt(nodeidx);
				pathnode.elementAt(nodeidx).isleaf = false;
				bptree.root = newParent;
			}else {					// nonleaf��尡 root�� �ƴ� ��Ȳ������ �����÷ο�
				newPair.lcNode = newChild;
				newChild.isleaf = false;
				if(!(insertPair(newPair, pathnode.elementAt(nodeidx-1)))) {	//pair�� ��忡 �־��µ� �����÷ο��� ���
					int newmid = pathnode.elementAt(nodeidx-1).nonleafkeyarr.elementAt(m/2).key;
					overflowCtrl(false,newmid,pathnode,nodeidx-1,bptree);
				}
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
				target.isleaf =false;
				break out;
				
			}
			else if (i == target.nonleafkeyarr.size()-1) {
				target.nonleafkeyarr.add(newpair);
				target.isleaf =false;
			}
		}
		if (target.nonleafkeyarr.size()>=target.m) 
			return false;			//pair�� �־��� �� �����÷ο�

		else 
			return true;			//�����÷ο� ���Ͼ
	}

	private static void makeTree(String filename, Tree bptree) {
		String rltmp;//readLineTmp
		FileReader in = null;
		try {
		in = new FileReader("C:/Users/Lee/eclipse-workspace/bptree/"+filename);
		BufferedReader br = new BufferedReader(in);
		bptree.m = Character.getNumericValue(in.read());
		br.readLine();
		Vector<String> linepack = new Vector<String>();
		
		Node newNode;
		while((rltmp =  br.readLine())!=null) {
			linepack.add(rltmp);
		}
		for(int r=0; r<linepack.size();r++)
		{
			int oldstd =0 ;
			//=======================One line===================================================
			String[] nodepack = linepack.elementAt(r).split(",");
			for(int c=0; c<nodepack.length;c++)
			{	//===================One Node===================================================
				newNode = new Node(bptree.m, true);
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
						int std = newNode.leafkeyarr.elementAt(0).key;
						if (c!=0) {						//rightNode Link
							nodeLinkR(bptree.root,oldstd,newNode);
						}
						nodeLink(bptree.root,std,newNode);
						
					}
					oldstd = Integer.parseInt(eachKey[1]);
				} else{						
					// ========================non leaf node====================================
					newNode = new Node(bptree.m, false);
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
			out:
			for(int i=0; i<stdnode.nonleafkeyarr.size();i++)
			{
				if (oldstd < stdnode.nonleafkeyarr.elementAt(i).key) {			// goto left
					nodeLinkR(stdnode.nonleafkeyarr.elementAt(i).lcNode,oldstd,newnode);
					break out;
				}
				else if(i == stdnode.nonleafkeyarr.size()-1) {
					nodeLinkR(stdnode.rightNode,oldstd,newnode);
				}
			}
		}
		
	}

	private static void nodeLink(Node stdnode, int std, Node newnode) {
		out:
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
				break out;
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
	// use in CREATION
	// work : �����̸��� m�� ���� �޾Ƽ� .dat file�� �����Ѵ�
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
		isleaf = true;
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
	public Node (int pm) {
		isleaf = true;
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
