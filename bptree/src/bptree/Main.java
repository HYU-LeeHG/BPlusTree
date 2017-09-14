package bptree;
import java.io.*;
import java.lang.*;
import java.util.Vector;

import javax.swing.plaf.synth.SynthSplitPaneUI;


//Leaf ��忡 �� ���
class leafPair {
	
	//Ű�� ������ ����
	int key;
	int value;
	
	public leafPair(int k, int v) {
		key = k;
		value = v;
	}
}

//Non-Leaf ��忡 �� ���
class nonleafPair {
	int key;
	Node lcNode; //left-child-node
	
	public nonleafPair(int k, Node n) {
		key = k;
		lcNode = n;
	}
}

public class Main {
	public static void main(String[] args) {
		
		//Ʈ�� ����
		Tree bptree = new Tree();
		
		//��ɾ ������ 
		if(args[0] == null) {
			System.out.println("Wrong CommandLine");	
			System.exit(0);	//����
		}
		
		//Command Line�� �о �˸��� �޼���� ������ ����
		switch(args[0]) {
			//CREATION
			case "-c":
				System.out.println("creation");
				makeDat(args[1],args[2]);			//dat������ ����.
				break;
			
			//INSERTION
			case "-i": // -i index.dat input.csv
				System.out.println("insertion");
				makeTree(args[1], bptree);			//index.dat�� �а� Ʈ���� ����.
				getInputfile(args[2],bptree);		//input.csv�� �а� Ʈ���� �߰���.
				saveTree(bptree,args[1]);			//Ʈ���� index.dat�� ������
				break;
				
			//DELETION
			case "-d":
				System.out.println("deletion");		
				makeTree(args[1], bptree);			//index.dat�� �а� Ʈ���� ����.
				getDeletefile(args[2],bptree);		//delete.csv�� �а� Ʈ������ ������.
				saveTree(bptree,args[1]);			//Ʈ���� index.dat�� ������
				break;
				
			//SEARCH
			case "-s":
				System.out.println("search");
				makeTree(args[1], bptree);			//index.dat�� �а� Ʈ���� ����.
				//Ŀ�ǵ���� �Է°��� ã�� path�� value��(�Ǵ� NOT FOUND)�� ���
				search(bptree.root, Integer.parseInt(args[2]));	
				break;
				
			//RANGE SEARCH
			case "-r":
				System.out.println("range search");
				makeTree(args[1], bptree);			//index.dat�� �а� Ʈ���� ����.
				////Ŀ�ǵ���� �Է°��� �ּҰ����� �ִ밪 ������ ���� �����
				rangeSearch(bptree.root, Integer.parseInt(args[2]),Integer.parseInt(args[3]));
				break;
				
			//WRONG COMMANDLINE
			default : 
				System.out.println("Wrong CommandLine");
		}
	
	}

	

	
	/* ==================================================================================
	 * 								DELETE ���� �Լ���
	 * ==================================================================================*/
	
	//delete.csv�� �޾Ƽ� Tree������ �ִ� �޼���
	//file readline -> search -> deletion �ݺ�
	private static void getDeletefile(String filename, Tree bptree) {
		
		//file���� ����
		FileReader in = null;
		//����ó�� try-catch��
		try {
			//file������ delete.csv����
			in = new FileReader("C:/Users/Lee/eclipse-workspace/bptree/"+filename);
			//readLine�� ���� ���۸��� ���� �� delete.csv����
			BufferedReader br = new BufferedReader(in);
			//delete.csv�� �� �پ� ������ string vector ����
			Vector<String> deletepack = new Vector<String>();
			//readLine�� ���� �ӽ� String ����
			String rltmp;
			
			//delete.csv�� ���� �� ���� ������ �о� deletepack�̶�� string vector�� ����
			 while((rltmp = br.readLine())!=null) {
				 deletepack.add(rltmp);
			 }

			 //deletepack�� 0���� ������ Tree���� ����
			 for(int i=0; i<deletepack.size();i++ ) {
				 //���� ���� �ִ�(�Ǵ� ���� ���̶�� �����Ǵ�) �������� ���� Path Node���� �����ϴ� ����
				 Vector<Node> pathNode = new Vector<Node>();
				 int delkey = Integer.parseInt(deletepack.elementAt(i));	//���� �� ����
				 //root == null, �� Ʈ���� ������� ��츦 �����ϰ� path�� Ž��
				 if (bptree.root != null) {
					 findPath(delkey,bptree.root, pathNode);
				 }
				 //bptree���� delkey ����
				 deletion(delkey,bptree,pathNode);
				 
				 }
		}
		//����� ����ó��
		catch(IOException ioe) {
		} finally {
				try {
					//file close
					in.close();
				}catch(Exception e) {
			}
		}
		
	}
	
	//key�� tree�� �Է¹޾Ƽ� leaf ��忡�� key�� ����� �Լ�
	//������忡�� ���� 	-> �̻���� -> ��������� key�� �����ִ��� ����üũ(keyChange�޼���)
	//				-> ����÷ο� -> ����÷ο�����޼��� call
	private static void deletion(int key, Tree bptree, Vector<Node> pathnode) {

		int i=pathnode.size()-1;			// leaf node (path�� ���� ���� �������)
		
		//Tree�� ����ٸ� ������� �˸��� ����
		if ( i== -1) {		
			System.out.println("Tree is empty");
			System.exit(0);
		}
		
		//Tree�� ������� �ʴٸ�
		else{
			int ns = pathnode.elementAt(i).leafkeyarr.size();	//�������� �ִٰ� �����Ǵ� leaf node�� ������
			
			
			//ó������ ����������� �������� ���� �� �ִ��� Ȯ�� �� ����
			out:	//Ż������
			for(int j=0; j<ns;j++) {
				if(key == pathnode.elementAt(i).leafkeyarr.elementAt(j).key) {
					pathnode.elementAt(i).leafkeyarr.remove(j);
					break out;
				}
			}
			//i==0�̸� ����=��Ʈ�� Ʈ���Ƿ� ���� �ϳ��̻��̸� �ȴ�.
			//���� 0���� empty tree�� �Ǵ� ���̹Ƿ� underflowCtrl�� ���ʿ�
			if (i!=0){	
				if (pathnode.elementAt(i).leafkeyarr.size() < (bptree.m-1)/2) {		//underflow ����
					underflowCtrl(true, key, pathnode,i,bptree);					//Control underflow
				}
				else
					keyChange(pathnode, i, key);	//�ٲ� Ű���� ������忡 �ִ� �� Ȯ�� �� ����
			}
		}
		
		
		
	}

	/*underflow�� �����ϴ� �Լ�
	 * -������� �õ��غ��� �ȵǸ� ���� ����� ����.
	 * -��� ��찡 �ȵǴ� ���� ����. ( root�� ���� ������ ó�� )
	 *	A. ���ʳ�忡�� �� �ϳ��� �޴´�.
	 *	B. �����ʳ�忡�� �� �ϳ��� �޴´�.
	 *	C. ���ʳ��� �����Ѵ�.
	 *	D. �����ʳ��� �����Ѵ�.
	 *
	 *   �ؼ����� NODE�� ������ �Ͼ ���!!						
	 */

	private static void underflowCtrl(boolean isleaf, int key, Vector<Node> pathnode, int idx, Tree bptree) {
		
		// underflow�� ��尡 ��Ʈ�϶�
		if(idx == 0) {	
			//root�� �ڽ��� �ϳ��� ������ -> �� �ϳ��� �ڽ��� �� root�� ����
			if (!(pathnode.elementAt(idx).isleaf) && pathnode.elementAt(idx).nonleafkeyarr.size() ==0) {
					bptree.root = pathnode.elementAt(idx).rightNode;
			}
		}
		
		//root�� �ƴ� ���� underflow control 
		else {	
		
		//underflow control�� �������� �˸��� Ż������
		out:
			
		//NODE(underflow�� ���, ���ϻ���)�� leaf ��� �϶�
		if(isleaf) {
			
			// A���( ���ʳ�忡�� �� �ޱ� )=========================================================
			// �ش� ���(A)�� ������ ���� �ʾ� ���� ������� �Ѿ�ߵ��� �˸��� Ż������
			out1:
			//�θ��忡�� NODE�� index�� ã�� ����
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				
				//NODE�� ��ġ�� i�϶�
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					//i==0�̸� ���ʳ�尡 �����Ƿ� ���� ������� �̵�
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
						pathnode.elementAt(idx).leafkeyarr.add(0,new leafPair(moveKey, moveVal));
						
						// ���ʳ���� ������key,value ����
						pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).
								lcNode.leafkeyarr.remove(lcSize-1);
						// �� �̵����� ���� non-leaf����� key�� �������
						keyChange(pathnode,idx,key);
						// CTRL(underflow control, ���ϻ���)�� �������� �˸�
						break out;
					}
					//���ʳ���� ���� ���� ���ġ �����Ƿ� ���� ������� �̵�
					else
						break out1;
				}
				
				//NODE�� �θ����� right-child node�� case
				//���� ����� ���� ����(���⼭ i�� ��������� ���ʳ���� �͸� �ٸ�)
				else if(i == pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
					int lcSize = pathnode.elementAt(idx-1).	// ��������� ������������� ������
							nonleafkeyarr.elementAt(i).lcNode.leafkeyarr.size();
					
					//���ʳ���� �� ������ n/2���� ������
					if(lcSize>(bptree.m-1)/2) {
						//���ʳ���� ������ key,value
						int moveKey =pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).
								lcNode.leafkeyarr.elementAt(lcSize-1).key;
						int moveVal = pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).
								lcNode.leafkeyarr.elementAt(lcSize-1).value;
						
						// ���ʳ���� key,value�� ������忡 �߰�
						pathnode.elementAt(idx).leafkeyarr.add(0,new leafPair(moveKey, moveVal));
						
						//���ʳ���� ������key,value ����
						pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).
						lcNode.leafkeyarr.remove(lcSize-1);
						keyChange(pathnode,idx,key);
						break out;
					}
					else
						break out1;
				}
			}
			// B���( �����ʳ�忡�� �� �ޱ� )=========================================================
			// �ش� ���(B)�� ������ ���� �ʾ� ���� ������� �Ѿ�ߵ��� �˸��� Ż������
			out2:
			// �θ��忡�� NODE�� index�� ã�� ����
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				//i == index�� ���
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					//���� ��尡 �θ����� right child node�� ��
					if(i==pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
						//������尡 ���� ����ϸ�
						if(pathnode.elementAt(idx-1).rightNode.leafkeyarr.size()>(bptree.m-1)/2) {
							
							//�ű� Ű,�� => ��������� ���� ���� Ű,��
							int moveKey =pathnode.elementAt(idx-1).rightNode.leafkeyarr.elementAt(0).key;
							int moveVal = pathnode.elementAt(idx-1).rightNode.leafkeyarr.elementAt(0).value;
							
							// �����ʳ���� key,value�� ������忡 �߰�
							pathnode.elementAt(idx).leafkeyarr.add(new leafPair(moveKey, moveVal));
							
							//�����ʳ���� ������key,value ����
							pathnode.elementAt(idx-1).rightNode.leafkeyarr.remove(0);
							keyChange(pathnode,idx,key);
							break out;
						}
						else
							break out2;
					}
					// ���� ��尡 NODE�� ���� index(i+1)�� ��ġ�� ����� ��
					else {
						int rcSize = pathnode.elementAt(idx-1).	// ��������� ����������������� ������
								nonleafkeyarr.elementAt(i+1).lcNode.leafkeyarr.size();
						
						//�����ʳ���� �� ������ n/2���� ������
						if(rcSize>(bptree.m-1)/2) {
							if(key == 27)
								System.out.println("case5");
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
						else break out2;
					}
				}
			}

			// C���( ���ʳ��� ���� )=========================================================
			// -�޳��� ������ �� �޳���� ���� NODE�� �ű��.

			// �ش� ���(C)�� ������ ���� �ʾ� ���� ������� �Ѿ�ߵ��� �˸��� Ż������
			out3:
			// �θ��忡�� NODE�� index�� ã�� ����
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				//NODE�� �θ��忡�� i��° index�ϋ�
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					if(i==0)		//������尡 ���� ���ʳ��� �޳�尡 �����Ƿ� Ż��
						break out3;
					// �������� ���� ����� �θ��� non-leaf����� Ű
					int delKey = key;
					int lcSize = pathnode.elementAt(idx-1).	// ��������� ������������� ������
							nonleafkeyarr.elementAt(i-1).lcNode.leafkeyarr.size();
					
					//NODE�� ����Ű���� �ٸ� Ű�� �� ���� ��
					if(pathnode.elementAt(idx).leafkeyarr.size() != 0) {
						//NODE���� ���� ���� ���� delKey�� ����(����Ű ����)
						delKey = pathnode.elementAt(idx).leafkeyarr.elementAt(0).key;
						//���� ���� ���� ����Ű���� �۴ٸ�, delKey ����
						if (delKey > key)
							delKey = key;
					}
					
					//���ʳ���� pair�� ���� NODE�� ����
					for(int j=0;j<lcSize;j++) {
						int moveKey = pathnode.elementAt(idx-1).	// ��������� ������������� j��° Ű
								nonleafkeyarr.elementAt(i-1).lcNode.leafkeyarr.elementAt(j).key;
						int moveVal = pathnode.elementAt(idx-1).	// ��������� ������������� j��° ��
								nonleafkeyarr.elementAt(i-1).lcNode.leafkeyarr.elementAt(j).value;
						pathnode.elementAt(idx).leafkeyarr.add(j, new leafPair(moveKey,moveVal));
					}
					
					// �������� ���� �� ������ �θ��忡�� underflow�� �Ͼ�� ���� ���� boolean
					// ������ ���� -> true, underflow-> false
					boolean delsuccess = deletePair(delKey,pathnode.elementAt(idx-1));
					keyChange(pathnode,idx,key);
					if(!(delsuccess)) {
						//����������� �θ��� underflow control
						underflowCtrl(false,delKey,pathnode,idx-1,bptree);
					}
					
					break out;
				}
				
				//NODE�� �θ����� right-child node�϶� (���γ����� ���� ����)
				else if(i == pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
					int delKey = key;
					int lcSize = pathnode.elementAt(idx-1).	// ��������� ������������� ������
							nonleafkeyarr.elementAt(i).lcNode.leafkeyarr.size();
					
					if(pathnode.elementAt(idx).leafkeyarr.size() != 0) {
						delKey = pathnode.elementAt(idx).leafkeyarr.elementAt(0).key;
						if (key < delKey)
							delKey = key;
					}
					for(int j=0;j<lcSize;j++) {
						int moveKey = pathnode.elementAt(idx-1).	// ��������� ������������� j��° Ű
								nonleafkeyarr.elementAt(i).lcNode.leafkeyarr.elementAt(j).key;
						int moveVal = pathnode.elementAt(idx-1).	// ��������� ������������� j��° ��
								nonleafkeyarr.elementAt(i).lcNode.leafkeyarr.elementAt(j).value;
						pathnode.elementAt(idx).leafkeyarr.add(j, new leafPair(moveKey,moveVal));
					}
					boolean delsuccess = deletePair(delKey,pathnode.elementAt(idx-1));
					keyChange(pathnode,idx,delKey);
					if(!(delsuccess)) {
						underflowCtrl(false,delKey,pathnode,idx-1,bptree);
					}
					
					break out;
				}
			}
			
			// D���( �����ʳ��� ���� )=========================================================
			// -�������� ������ �� NODE�� ���� �������� �ű��.

			// �θ��忡�� NODE�� index�� ã�� ����
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				// i�� NODE�� �θ��忡���� index�϶�
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					//�������� ���� ������ �θ����� ����� Ű
					int delKey;
					//�ڿ� �� �̻� nonleafkeyPair�� ������ rightNode Ȯ��
					if(i==pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
						//delKey�� ��������� ù��°��
						delKey = pathnode.elementAt(idx-1).rightNode.leafkeyarr.elementAt(0).key;
						
						//NODE�� ���� �������� �ű�� ����
						for(int j=0;j<pathnode.elementAt(idx).leafkeyarr.size();j++) {
							//NODE�� ���� ������� �����ִ´�.
							//��������� ������ {(NODE�� ����)(��������)}
							int moveKey = pathnode.elementAt(idx).leafkeyarr.elementAt(j).key;
							int moveVal = pathnode.elementAt(idx).leafkeyarr.elementAt(j).value;
							
							//j��°���� ��������� j��°�� �ִ´�.
							pathnode.elementAt(idx-1).rightNode.leafkeyarr.add(j, new leafPair(moveKey, moveVal)); 
						}
						break out;
					}
					//��������� �θ𿡼��� index�� i+1�϶�( ���λ����� ���� ���� )
					else {
						delKey = pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i+1).
								lcNode.leafkeyarr.elementAt(0).key;
						if (delKey > key)
							delKey = key;
						for(int j=0;j<pathnode.elementAt(idx).leafkeyarr.size();j++) {
							int moveKey = pathnode.elementAt(idx).leafkeyarr.elementAt(j).key;
							int moveVal = pathnode.elementAt(idx).leafkeyarr.elementAt(j).value;
							
							pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i+1)
							.lcNode.leafkeyarr.add(j, new leafPair(moveKey, moveVal)); 
						}
						pathnode.elementAt(idx-1).nonleafkeyarr.remove(i);
						
					}
					
					// C�� ����
					boolean delsuccess = deletePair(delKey,pathnode.elementAt(idx-1));
					keyChange(pathnode,idx,key);
					if(!(delsuccess)) {
						underflowCtrl(false,delKey,pathnode,idx-1,bptree);
					} 
					break out;
				}
			}
		
		}
		
		// NODE�� non-leaf�϶�
		// ���λ����� NODE�� leaf�� ���� ��ġ
		else {
		
			// ��� A : �޳�忡�� ��� �ޱ�
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
						
						//NODE�� ���ʿ��� �� pair ����
						insertPair(pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).lcNode
								.nonleafkeyarr.elementAt(lcSize-1), pathnode.elementAt(idx));
						
						keyChange(pathnode,idx,key);
						break out;
					}
					else 
						break out1;
				}
				else if(i == pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
					int lcSize = pathnode.elementAt(idx-1).	// ��������� ������������� ������
							nonleafkeyarr.elementAt(i).lcNode.nonleafkeyarr.size();
					
					//���ʳ���� �� ������ n/2���� ������
					if(lcSize>(bptree.m-1)/2) {
						Node tmpNode = new Node();
						
						//���ʿ��� �� ���� left child = �޳���� rightNode
						//�޳���� rightNode =���ʿ��� �� ���� left child		�� ü���� 
						tmpNode= pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).lcNode.rightNode;
						pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).lcNode.rightNode =
								pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).lcNode
								.nonleafkeyarr.elementAt(lcSize-1).lcNode;
						pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).lcNode
						.nonleafkeyarr.elementAt(lcSize-1).lcNode = tmpNode;
						
						insertPair(pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).lcNode
								.nonleafkeyarr.elementAt(lcSize-1), pathnode.elementAt(idx));
						
						keyChange(pathnode,idx,key);
						break out;
					}
					else
						break out1;
				}
			}
			// ��� B : ������忡�� ��� �ޱ�
			out2:
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					
					
					//�ڿ� �� �̻� nonleafkeyPair�� ������ rightNode Ȯ��
					if(i==pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
						if(pathnode.elementAt(idx-1).rightNode.nonleafkeyarr.size()>(bptree.m-1)/2) {
							Node tmpNode= new Node();
							
							//NODE�� right-child node -> ������尡 �ִ°�
							//������忡�� �ִ� pair�� left-child node -> NODE�� right-child ���		�� ü����
							tmpNode = pathnode.elementAt(idx-1).rightNode.nonleafkeyarr.elementAt(0).lcNode;
							pathnode.elementAt(idx-1).rightNode.nonleafkeyarr.elementAt(0).lcNode 
																= pathnode.elementAt(idx).rightNode;
							pathnode.elementAt(idx).rightNode = tmpNode;
							
							insertPair(pathnode.elementAt(idx-1).rightNode.nonleafkeyarr.elementAt(0)
									   ,pathnode.elementAt(idx));
							
							keyChange(pathnode,idx,key);
							break out;
						}
						else
							break out2;
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
						else
							break out2;
					}
				}
			}
			// ��� C : �޳��� ����
			out3:
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					if(i==0)		//NODE�� ���� ���ʳ��� �޳�尡 �����Ƿ� Ż��
						break out3;
					int lcSize = pathnode.elementAt(idx-1).	// NODE�� ������������� ������
							nonleafkeyarr.elementAt(i-1).lcNode.nonleafkeyarr.size();
					//���ʳ���� ���� NODE�� �ű�
					for(int j=0;j<lcSize;j++) {
						pathnode.elementAt(idx).nonleafkeyarr.add(j,
								pathnode.elementAt(idx-1).nonleafkeyarr.
								elementAt(i-1).lcNode.nonleafkeyarr.elementAt(j));
					}
					//���յ� �� ���ʳ�带 ����Ű�� �θ����� �� ���� ���յȴ�.
					// {���ʳ��/�θ������/NODE}�� ����Ǿ� �ϳ��� ��尡 �ȴ�.
					
					// �θ������ leftchild -> ���ʳ���� rightchild
					pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).lcNode = 
							pathnode.elementAt(idx-1).nonleafkeyarr.
							elementAt(i-1).lcNode.rightNode;
					//NODE�� �θ���� �߰�(���ʳ�尪�� NODE���� ����)
					pathnode.elementAt(idx).nonleafkeyarr.add(lcSize,
							pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1));	
					// �θ��忡�� �θ���� ����
					pathnode.elementAt(idx-1).nonleafkeyarr.remove(i-1);
					keyChange(pathnode,idx,key);
					
					// �θ��忡�� underflow�Ͼ�� �� Ȯ��
					if(pathnode.elementAt(idx-1).nonleafkeyarr.size() < (bptree.m-1)/2) {
						int delKey = pathnode.elementAt(idx).nonleafkeyarr.elementAt(0).key;
						if (delKey > key)
							key = delKey;
						// �θ����� underflow�� ��������� ó��
						underflowCtrl(false,delKey,pathnode,idx-1,bptree);
						break out;
					} 
					else 
						break out3;
				}
				//NODE�� �θ����� right-child node�� �� ( ���λ����� ���� ���� )
				else if(i == pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
					int lcSize = pathnode.elementAt(idx-1).	// ��������� ������������� ������
							nonleafkeyarr.elementAt(i).lcNode.nonleafkeyarr.size();
					for(int j=0;j<lcSize;j++) {
						pathnode.elementAt(idx).nonleafkeyarr.add(j,
								pathnode.elementAt(idx-1).nonleafkeyarr.
								elementAt(i).lcNode.nonleafkeyarr.elementAt(j));
					}
					pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).lcNode = 
							pathnode.elementAt(idx-1).nonleafkeyarr.
							elementAt(i).lcNode.rightNode;
					pathnode.elementAt(idx).nonleafkeyarr.add(lcSize,
							pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i));	
					pathnode.elementAt(idx-1).nonleafkeyarr.remove(i);
					
					keyChange(pathnode,idx,key);
					if(pathnode.elementAt(idx-1).nonleafkeyarr.size() < (bptree.m-1)/2) {
						int delKey = pathnode.elementAt(idx).nonleafkeyarr.elementAt(0).key;
						if (delKey > key)
							key = delKey;
						underflowCtrl(false,delKey,pathnode,idx-1,bptree);
						break out;
					} 
					break out3;
				}
			}
			
			 
			// ��� D : �����ʳ��� ����
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					int delKey;
					//�ڿ� �� �̻� nonleafkeyPair�� ������ rightNode Ȯ��
					if(i==pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
						int dSize = pathnode.elementAt(idx).nonleafkeyarr.size();	// ��������� ������
						//NODE�� ���� �������� �ű�
						for(int j=0;j<dSize;j++) {
							pathnode.elementAt(idx-1).rightNode.nonleafkeyarr.add(j,
									pathnode.elementAt(idx-1).nonleafkeyarr.
									elementAt(i).lcNode.nonleafkeyarr.elementAt(j));
						}
						
						//���յ� �� NODE�� ����Ű�� �θ����� �� ���� ���յȴ�.
						// {NODE/�θ������/�������}�� ����Ǿ� �ϳ��� ��尡 �ȴ�.
						
						//�θ������ left-child -> NODE�� right-child
						pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).lcNode = 
								pathnode.elementAt(idx).rightNode;
						//������忡 �θ���� ����
						pathnode.elementAt(idx-1).rightNode.nonleafkeyarr.add(dSize,
								pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i));
						//�θ��忡�� �θ���� ����
						pathnode.elementAt(idx-1).nonleafkeyarr.remove(i);
						
						
						 
					}
					//��������� �θ𿡼��� index�� NODE�� index +1 �϶�( ���λ����� ���� ���� )
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
	}
	
	/* ������ Ű�� �ִ� ���� ���� ��� path�� Ű���� Ÿ������ �˻�
	 * pathnode(idx)���� ������ Ű���� �ִ� ���������� ���鼭
	 * Ű���� ���Ұ� �ִ��� Ȯ�� �� �����ϴ� �޼���				*/
	private static void keyChange(Vector<Node> pathnode, int idx, int delkey) {
		out:
		for(int i=0; i<pathnode.size();i++) {
		//for�� inside = i���� path node (0�� ��Ʈ)
			
			//������忡���� ��ȭ�� underflowCtrl()���� ����
			//���� non leaf���� �Ǻ�
			if(pathnode.elementAt(i).isleaf) {
				break out;
			}
			
			//������ Ű�� �ִ� path�� ã��
			for(int j=0;j<pathnode.elementAt(i).size();j++) {
				if(delkey < pathnode.elementAt(i).nonleafkeyarr.elementAt(j).key) {
					if(j!=0) {	//j==0�̸� ������ ���⶧����
						
						//�ٲ� ����� Ű�� = �� ����� �����ʿ� �ִ� ���� ���� �� (FindRNearest�� ��������)
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

	//target��忡 �ִ� delKey�� ���� �� ����
	private static boolean deletePair(int delKey, Node target) {
		
		//target����� index�� ��ȸ�ϴ� ����
		for(int i =0; i<target.nonleafkeyarr.size();i++) {
			//target�� ��� �� delKey�� ���� Ű�� ���� �� �ִٸ� ����
			if(target.nonleafkeyarr.elementAt(i).key == delKey)
				target.nonleafkeyarr.remove(i);
		}
		
		//���� �� underflow�� �Ͼ�� false, ���� �� �����̸� true ����
		if(target.nonleafkeyarr.size()<(target.m-1)/2)
			return false;
		else
			return true;
	}

	
	/*
	 * ==================================================================================
	 * 								SEARCH ���� �Լ���
	 * ==================================================================================*/

	//stdnode���� num�� ã�� ����ϴ� �Լ�
	private static void search(Node stdnode, int num) {
		if (stdnode.isleaf == true) {
			//stdnode�� leaf�� num�� ���� Ű�� �� ã�� �ִٸ� value�� ���ٸ� NOT FOUND�� ���
			for(int i = 0 ; i<stdnode.leafkeyarr.size();i++) {
				if (stdnode.leafkeyarr.elementAt(i).key == num)
					System.out.println(stdnode.leafkeyarr.elementAt(i).value);
				else if(i == stdnode.leafkeyarr.size()-1)
					System.out.println("NOT FOUND");
			}
		}
		else {
			//stdnode�� non-leaf�� 
			//stdnode�� num�� ã�� path node �� �ϳ� �̹Ƿ�
			//stdnode�� ����Ÿ�� ���
			for(int i =0 ; i < stdnode.nonleafkeyarr.size();i++) {
				//stdnode�� ������ ���
				System.out.print(stdnode.nonleafkeyarr.elementAt(i).key);
				//������ ���� �ƴ϶�� ���̻��� �޸��� ���
				if(i != stdnode.nonleafkeyarr.size()-1) 
					System.out.print(",");
				//������ ���̸� ���� ����
				else
					System.out.println();
			}
			
			//����� �ϰ� num�� ���� stdnode�� childnode���� �ٽ� search (���)
			out:
			for(int i=0; i<stdnode.nonleafkeyarr.size();i++)
			{
				
				if (num < stdnode.nonleafkeyarr.elementAt(i).key) {			// goto left
					search(stdnode.nonleafkeyarr.elementAt(i).lcNode,num);
					break out;
				}
				else if(i == stdnode.nonleafkeyarr.size()-1) {				// goto right
					search(stdnode.rightNode,num);
				}
			}
		}	
	}
	
	//�Է¹��� min�̻� max������ key�� ���� ��� ���
	private static void rangeSearch(Node root, int min, int max) {
		
		//min�� �ְų� �Ǵ� ���� ���̶� �����Ǵ� Node�� ã�Ƽ� ����
		Node minIncluded = findMin(root, min);
		
		int i = 0;	//loop index
		out:
		while(true) {
			//Node�� ����� Ű���� min�̻� max���϶�� ��� 
			if(minIncluded.leafkeyarr.elementAt(i).key >= min
					&& minIncluded.leafkeyarr.elementAt(i).key <=max) {
				System.out.println(minIncluded.leafkeyarr.elementAt(i).key + ","+
						minIncluded.leafkeyarr.elementAt(i).value);
			}
			
			//���� max���� ū Ű�� ���� �� ���Դٸ� �� �ڷε� ���� Ŀ���Ƿ� loop break
			else if (minIncluded.leafkeyarr.elementAt(i).key >max)
				break out;
			
			//���� Node�� ���� �����ߴٸ� Node�� rightNode�� �̵�
			if(i==minIncluded.leafkeyarr.size()- 1) {
				if (minIncluded.rightNode != null) {
					minIncluded = minIncluded.rightNode;
					i=0;
					if(minIncluded.size() == 0)
						break out;
				}
				else
					break out;
			}
			//Node�� ���� �ƴ϶�� i�� ����
			else
				i++;
			
			if (i > 100000) break out; 		//���к��� ���ѷ����� ���� ����
		}
		
		
	}

	//stdnode���� min�� �����ϴ� �ڽĳ�带 �����ϴ� �Լ� ( ����ϸ� �ᱹ leaf��尡 ���� )
	private static Node findMin(Node stdnode, int min) {
		//stdnode�� leaf�� ����
		if (stdnode.isleaf)
			return stdnode;
		
		//�ƴ϶�� stdnode�� �ڽ� �� min�� ���ǿ� �´� �ڽĳ��� findMin �ٽ� call
		else {
			for(int i = 0; i<stdnode.size();i++) {
				if(min < stdnode.nonleafkeyarr.elementAt(i).key) {
					return findMin(stdnode.nonleafkeyarr.elementAt(i).lcNode, min);
				}	
			}
			return findMin(stdnode.rightNode, min);
		}
			
	}


	//Tree�� index.dat�� �����ϴ� �޼���
	private static void saveTree(Tree bptree, String filename) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter("C:/Users/Lee/eclipse-workspace/bptree/"+filename));
			//index.dat �ֻ�ܿ� Tree�� m �� ����
			out.println(String.valueOf(bptree.m));
			//Tree�� �� Depth�� ��带 �����ϴ� ����
			Vector<Node> nextDepth = new Vector<Node>();
			//Depth�� ������ root
			nextDepth.add(bptree.root);
			//���� Depth�� ��� �� ���� Depth�� ��������� ã��
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
		try {	//nullptrException : �� �����͸� ������ �� �����Ƿ� (�� ���� ����)
			int vs = curDepth.size();	//���� ������ ��� ����
			if(curDepth.elementAt(0).isleaf) {
				//���� ������ Leaf Depth�� ��� Node�� key,value�� ������������ ����ϰ� �޼��� ����
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
				//���� ������ Non-Leaf Depth��� ��� Node�� key�� ����ϸ� NextDepth�� CurDepth�� �ڽĵ� ����
				//���� ������ ��带 �����ϴ� ����
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
				//���� ������ ������ ���� ������ ��������� �̵�
				findNextDepth(out, nextDepth);
			}
		} catch(NullPointerException npe) {
			
		}
		
	}

	private static void getInputfile(String filename, Tree bptree) {
		//input.csv�� �޾Ƽ� Tree������ �ִ� �޼���
		//file readline -> search -> insertion �ݺ� (getDeletefile�� ����)
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
				 //�о���� ������ �޸��� �ɰ��� ���� ����(���� key, value)
				 String[] kv = inputpack.elementAt(i).split(",");
				 
				
				 if (bptree.root != null) {
					 //tree�� ������� ������ �� key,value�� �� pathã��
					 findPath(Integer.parseInt(kv[0]),bptree.root, pathNode);
				 }
				 //tree�� �� key,value�� ����
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

	//key�� �������� �� ���� �� node�� path�� ã�Ƽ� pathNode�� ����
	private static void findPath(int key, Node node, Vector<Node> pathNode) {
		
		pathNode.add(node);				// method�� leaf��忡 �����ϸ� leaf��带 path�� �߰��ϰ� method ����
		
		if (!(node.isleaf))				// �ƴ϶�� ���� path�� ã�� 	
		{	
			
			int ns = node.nonleafkeyarr.size();
			out:
			for(int i=0; i<ns;i++)
			{
				if (key < node.nonleafkeyarr.elementAt(i).key) {			// goto left
					findPath(key, node.nonleafkeyarr.elementAt(i).lcNode,pathNode);
					break out;
				}
				else if(i == node.nonleafkeyarr.size()-1) {					// goto right
					findPath(key, node.rightNode, pathNode);
				}
			}			
		}
	}
	
	//�� ���� pathnode�� ���� ��(�������)�� ����
	private static void insertion(int key, int value, Tree bptree, Vector<Node> pathnode) {
		
		int i=pathnode.size()-1;			// leaf node (path�� ���� ���� �������)
		
		if ( i== -1) {		//first input
			//ù �Է��̹Ƿ� ��Ʈ�� ����� �� ���� ����
			Node newRoot = new Node(bptree.m);
			newRoot.isleaf = true;
			newRoot.leafkeyarr.add(new leafPair(key,value));
			bptree.root = newRoot;
		}	
		else{				//ù �Է��� �ƴϸ�
			int ns = pathnode.elementAt(i).leafkeyarr.size();
			//�� ���� leaf node�� ���������� �����ǵ��� ����
			out:
			for(int j=0; j<ns;j++) {
				if(key < pathnode.elementAt(i).leafkeyarr.elementAt(j).key) {
					//j��°���� key���� ũ�� j��°�� ����
					pathnode.elementAt(i).leafkeyarr.add(j,new leafPair(key,value));
					break out;
				}
				else if(j == pathnode.elementAt(i).leafkeyarr.size()-1) {
					// key���� ū ���� ������ �������� ����
					pathnode.elementAt(i).leafkeyarr.add(new leafPair(key,value));
				}
			}
			
			if (pathnode.elementAt(i).leafkeyarr.size() >= bptree.m) {
				// ������ overflow�� �ʷ��ϸ� overflowCtrl Call
				int mid = pathnode.elementAt(i).leafkeyarr.elementAt(bptree.m/2).key; //�߰���
				overflowCtrl(true, mid,pathnode,i,bptree);
			}	
		}	
	}
	
	//overflow�� �Ͼ�� ó���ϴ� �Լ�
	//leaf case : �߰����� �ø��� �θ��忡���� �����÷ΰ� �Ͼ�� overflowCtrl recall
	//nonleaf case : �߰����� �ø��� �ش� ���� ����� �θ��忡���� �����÷ΰ� �Ͼ�� overflowCtrl recall
	private static void overflowCtrl(boolean isleaf, int mid, Vector<Node> pathnode, int nodeidx,Tree bptree) {
		int m = bptree.m;
		int newKey, newValue;
		
		Node newChild = new Node(m);
		Node newParent = new Node(m,false);
		nonleafPair newPair = new nonleafPair(mid, null);
		
		if (isleaf) {			//leaf������ �����÷ο�
			for(int i = 0; i< m/2;i++) {	//split�� �߰� ���������� ������ ���ο� childnode�� ����
				//loop�� m/2-1���� �����ϰ� �Ź� 0��° ���鸸 pop�ؼ� ����忡 ����
				newKey = pathnode.elementAt(nodeidx).leafkeyarr.elementAt(0).key;
				newValue = pathnode.elementAt(nodeidx).leafkeyarr.elementAt(0).value;
				newChild.leafkeyarr.add(new leafPair(newKey,newValue));
				newChild.isleaf = true;
				pathnode.elementAt(nodeidx).leafkeyarr.remove(0);
			}
			newChild.rightNode = pathnode.elementAt(nodeidx);	//�� �������� ���� �������� ����
			if(nodeidx == 0) {		// leaf == root�� ��Ȳ���� �����÷ο�
				//���ο� root�� ����
				newParent.nonleafkeyarr.add(new nonleafPair(mid, newChild));
				newParent.rightNode = pathnode.elementAt(nodeidx);
				bptree.root = newParent;
				
			}else {					// leaf != root
				//leftchild�� newChild�� pair�� ����
				newPair.lcNode = newChild;
				if(!(insertPair(newPair, pathnode.elementAt(nodeidx-1)))) {	//pair�� ��忡 �־��µ� �����÷ο��� ���
					int newmid = pathnode.elementAt(nodeidx-1).nonleafkeyarr.elementAt(m/2).key;
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
			//newChild�� right-child�� �ö󰡴� �߰����� ����Ű�� leftchild�� ��
			//(�߰����� leftchild�� newChild�� ��)
			newChild.rightNode = pathnode.elementAt(nodeidx).nonleafkeyarr.elementAt(0).lcNode;
			//���ø��� �߰����� ���� ���������� ����
			pathnode.elementAt(nodeidx).nonleafkeyarr.remove(0);
			
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

	//newpair�� target��忡 �ִ� �޼���
	private static boolean insertPair(nonleafPair newpair,Node target) {
		int ns = target.nonleafkeyarr.size();
		
		out:
		for(int i =0; i<ns;i++)
		{
			//���������� ������ �ʵ��� �´� ��ġ�� ã�Ƽ� ����
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

	
	//index.dat�� �а� Tree�� ����� �޼���
	//�� �پ�(�� ������) -> �� ��徿 -> �� �� ���� ���� �ɰ��� �о����
	private static void makeTree(String filename, Tree bptree) {
		String rltmp;//readLineTmp
		FileReader in = null;
		try {
		in = new FileReader("C:/Users/Lee/eclipse-workspace/bptree/"+filename);
		BufferedReader br = new BufferedReader(in);
		bptree.m = Character.getNumericValue(in.read());
		br.readLine();
		
		//�� ����( �� ���� )�� ����Ǵ� String Vector
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
					
					//�� ��忡 �о���� key, value �� ����
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
						nodeLink(bptree.root,std,newNode);	//������� Tree�� newNode�� ������
						
					}
					
					//������忡�� ��峢�� �����ϱ� ���� ���� ���� �����ص�
					oldstd = Integer.parseInt(eachKey[1]);
				} else{						
					// ========================non leaf node====================================
					newNode = new Node(bptree.m, false);
					//newNode�� �� ���� �־���
					for(int n =1;n<m+1;n++) {
						newNode.nonleafkeyarr.add(new nonleafPair(Integer.parseInt(eachKey[n])
																  ,null));
					}
					if (r==0) { //root case
						bptree.root = newNode;
					} else {
						int std = newNode.nonleafkeyarr.elementAt(0).key;
						nodeLink(bptree.root,std,newNode);		//���� tree�� ��ũ
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

	//newnode�� stdnode�� ���� ���� �����̶� �����ϴ� �޼���
	private static void nodeLink(Node stdnode, int std, Node newnode) {
		out:
		for(int i=0; i<stdnode.nonleafkeyarr.size();i++)
		{
			if (std < stdnode.nonleafkeyarr.elementAt(i).key) {			// goto left
				if (stdnode.nonleafkeyarr.elementAt(i).lcNode ==null) {
					//���� ����ִٸ� newnode�� �� ���� ����
					stdnode.nonleafkeyarr.elementAt(i).lcNode = newnode;
					newnode.m = stdnode.m;
				}
				else {
					// ��������ʴٸ� �ϳ� �� ���� ������ �� ( ������������� ��� )
					nodeLink(stdnode.nonleafkeyarr.elementAt(i).lcNode,std,newnode);
					
				}
				break out;
			}
			else if(i == stdnode.nonleafkeyarr.size()-1) {				// goto right
				if (stdnode.rightNode == null) {
					//����ٸ� ����
					stdnode.rightNode= newnode;
					newnode.m = stdnode.m;
				}
				else {
					//�ƴ϶�� ���� ������...
					nodeLink(stdnode.rightNode,std,newnode);
				}
					
			}
		}
	}
	
	//�������� ������带 �����ϴ� �޼���
	//stdnode���� oldstd�� ã�� �� stdnode.right = newnode�� ����
	//���λ����� nodeLink�� ����
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

	private static void makeDat(String filename, String bnum) {
	// use in CREATION
	// work : �����̸��� m�� ���� �޾Ƽ� .dat file�� �����Ѵ�
		FileWriter out = null;
		try {
		out = new FileWriter("C:/Users/Lee/eclipse-workspace/bptree/"+filename);
		out.write(bnum);
		
		//tree Ȱ��ȭ
		Tree bptree = new Tree();
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
	public int m;		//����
	Node root;			//root ����
	public Tree() {
	}
	
	public Tree(int num){
		m = num;
		root.m = num;
	}
	
}

class Node{
	boolean isleaf; //true = leaf, false= non-leaf
	Vector<leafPair> leafkeyarr;		//leafpair(key,value)�� �����ϴ� ����
	Vector<nonleafPair> nonleafkeyarr;	//nonleafpair(key,leftchild)�� �����ϴ� ����
	Node rightNode;	//non-leaf -> right child node ,, leaf ->rightmost node
	int m;
	
	//�پ��� �����ڵ�...
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
