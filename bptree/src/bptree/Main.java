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
		switch(args[0]) {//nullpointer exception 추가

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
		//delete.csv를 받아서 Tree구조에 넣는 메서드
		//file readline -> search -> deletion 반복
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

		int i=pathnode.size()-1;			// leaf node (path의 제일 끝은 리프노드)
		
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
			//i==0이면 리프=루트인 트리므로 값이 하나이상이면 된다.
			//값이 0개면 empty tree가 되는 것이므로 underflowCtrl이 불필요
			if (i!=0){	
				if (pathnode.elementAt(i).leafkeyarr.size() < (bptree.m-1)/2) {
					underflowCtrl(true, key, pathnode,i,bptree);
	
				}
			}
		}
		
		
		
	}

	private static void underflowCtrl(boolean isleaf, int key, Vector<Node> pathnode, int idx, Tree bptree) {
		// underflow난 노드가 리프일때
		if(idx == 0) {
			if(key < pathnode.elementAt(idx).nonleafkeyarr.elementAt(0).key)
				bptree.root = pathnode.elementAt(idx).nonleafkeyarr.elementAt(0).lcNode;
			else
				bptree.root = pathnode.elementAt(idx).rightNode;
		}
			
		out:
		if(isleaf) {
			//절차 : 	1왼쪽노드가있으며절반이상인가 -> 2오른쪽노드가있으며절반이상인가-> 
			//		3왼쪽노드랑합병->4오른노드랑합병 ->부모노드의불필요페어삭제-> 논리프키값 재조정->
			//		부모노드가 언더플로인가? ->재귀
			out1:
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					if(i==0)
						break out1;
					int lcSize = pathnode.elementAt(idx-1).	// 삭제노드의 왼쪽형제노드의 사이즈
							nonleafkeyarr.elementAt(i-1).lcNode.leafkeyarr.size();
					
					//왼쪽노드의 값 갯수가 n/2보다 많을때
					if(lcSize>(bptree.m-1)/2) {
						//왼쪽노드의 마지막 key,value
						int moveKey =pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).
								lcNode.leafkeyarr.elementAt(lcSize-1).key;
						int moveVal = pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).
								lcNode.leafkeyarr.elementAt(lcSize-1).value;
						
						// 왼쪽노드의 key,value를 삭제노드에 추가
						pathnode.elementAt(idx).leafkeyarr.add(new leafPair(moveKey, moveVal));
						
						//왼쪽노드의 마지막key,value 삭제
						pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).
						lcNode.leafkeyarr.remove(lcSize-1);
						keyChange(pathnode,idx,key);
						break out;
					}
				}
			}
			
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					
					
					//뒤에 더 이상 nonleafkeyPair가 없으면 rightNode 확인
					if(i==pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
						if(pathnode.elementAt(idx-1).rightNode.leafkeyarr.size()>(bptree.m-1)/2) {
							int moveKey =pathnode.elementAt(idx-1).rightNode.leafkeyarr.elementAt(0).key;
							int moveVal = pathnode.elementAt(idx-1).rightNode.leafkeyarr.elementAt(0).value;
							
							// 오른쪽노드의 key,value를 삭제노드에 추가
							pathnode.elementAt(idx).leafkeyarr.add(new leafPair(moveKey, moveVal));
							
							//오른쪽노드의 마지막key,value 삭제
							pathnode.elementAt(idx-1).rightNode.leafkeyarr.remove(0);
							keyChange(pathnode,idx,key);
							break out;
						}
					}
					else {
						int rcSize = pathnode.elementAt(idx-1).	// 삭제노드의 오른쪽쪽형제노드의 사이즈
								nonleafkeyarr.elementAt(i+1).lcNode.leafkeyarr.size();
						
						//오른쪽노드의 값 갯수가 n/2보다 많을때
						if(rcSize>(bptree.m-1)/2) {
							//오른쪽노드의 맨앞 key,value
							int moveKey =pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i+1).
									lcNode.leafkeyarr.elementAt(0).key;
							int moveVal = pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i+1).
									lcNode.leafkeyarr.elementAt(0).value;
							
							// 오른쪽노드의 key,value를 삭제노드에 추가
							pathnode.elementAt(idx).leafkeyarr.add(new leafPair(moveKey, moveVal));
							
							//오른쪽노드의 마지막key,value 삭제
							pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i+1).
							lcNode.leafkeyarr.remove(0);
							keyChange(pathnode,idx,key);
							break out;
						}
					}
				}
			}
			
			//왼노드랑 병합 (왼쪽노드가 삭제되고 값을 삭제노드로 옮김)
			out2:
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					if(i==0)		//삭제노드가 제일 왼쪽노드면 왼노드가 없으므로 탈출
						break out2;
					int delKey = key;
					int lcSize = pathnode.elementAt(idx-1).	// 삭제노드의 왼쪽형제노드의 사이즈
							nonleafkeyarr.elementAt(i-1).lcNode.leafkeyarr.size();
					
					if(pathnode.elementAt(idx).leafkeyarr.size() != 0)
						delKey = pathnode.elementAt(idx).leafkeyarr.elementAt(0).key;
						
					for(int j=0;j<lcSize;j++) {
						int moveKey = pathnode.elementAt(idx-1).	// 삭제노드의 왼쪽형제노드의 j번째 키
								nonleafkeyarr.elementAt(i-1).lcNode.leafkeyarr.elementAt(j).key;
						int moveVal = pathnode.elementAt(idx-1).	// 삭제노드의 왼쪽형제노드의 j번째 값
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
			
			//오른노드랑 병합 (삭제노드가 삭제되고 오른노드로 옮김)
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					int delKey;
					//뒤에 더 이상 nonleafkeyPair가 없으면 rightNode 확인
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
		
		// underflow난 노드가 논리프일때
		else {
			// 1순위 : 왼쪽에서 노드 기부 ( 왼쪽형제노드의 제일 끝 pair를 삭제노드에 기부 )
			out1:
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					if(i==0)
						break out1;
					int lcSize = pathnode.elementAt(idx-1).	// 삭제노드의 왼쪽형제노드의 사이즈
							nonleafkeyarr.elementAt(i-1).lcNode.nonleafkeyarr.size();
					
					//왼쪽노드의 값 갯수가 n/2보다 많을때
					if(lcSize>(bptree.m-1)/2) {
						Node tmpNode = new Node();
						
						//왼쪽에서 줄 값의 left child = 왼노드의 rightNode
						//왼노드의 rightNode =왼쪽에서 줄 값의 left child		값 체인지 
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
			// 2순위 : 오른쪽에서 노드 기부
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					
					
					//뒤에 더 이상 nonleafkeyPair가 없으면 rightNode 확인
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
						int rcSize = pathnode.elementAt(idx-1).	// 삭제노드의 오른쪽쪽형제노드의 사이즈
								nonleafkeyarr.elementAt(i+1).lcNode.nonleafkeyarr.size();
						
						//오른쪽노드의 값 갯수가 n/2보다 많을때
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
			// 3순위 : 왼쪽이랑 병합
			out2:
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					if(i==0)		//삭제노드가 제일 왼쪽노드면 왼노드가 없으므로 탈출
						break out2;
					int lcSize = pathnode.elementAt(idx-1).	// 삭제노드의 왼쪽형제노드의 사이즈
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
			
			 
			// 4순위 : 오른쪽이랑 병합
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					int delKey;
					//뒤에 더 이상 nonleafkeyPair가 없으면 rightNode 확인
					if(i==pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
						int dSize = pathnode.elementAt(idx).nonleafkeyarr.size();	// 삭제노드의 사이즈
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
						int rcSize = pathnode.elementAt(idx-1).	// 삭제노드의 오른쪽쪽형제노드의 사이즈
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
	
	//삭제된 키가 있던 노드로 가는 모든 path의 키값이 타당한지 검사
	private static void keyChange(Vector<Node> pathnode, int idx, int delkey) {
		out:
		for(int i=0; i<pathnode.size();i++) {
		//for문 inside = i번쨰 path node (0이 루트)
			
			//리프노드에서의 변화는 underflowCtrl()에서 진행
			//따라서 non leaf인지 판별
			if(pathnode.elementAt(i).isleaf) {
				break out;
			}
			
			for(int j=0;j<pathnode.elementAt(i).size();j++) {
				if(delkey < pathnode.elementAt(i).nonleafkeyarr.elementAt(j).key) {
					if(j!=0) {	//j==0이면 왼쪽페어가 없기때문에
						pathnode.elementAt(i).nonleafkeyarr.elementAt(j-1).key =
								FindRNearest(pathnode.elementAt(i).nonleafkeyarr.elementAt(j).lcNode);
					}
				}
				
				//rightNode인 case	
				else if(j==pathnode.elementAt(i).size()-1) {
					pathnode.elementAt(i).nonleafkeyarr.elementAt(j).key = 
							FindRNearest(pathnode.elementAt(i).rightNode);
				}
			}
			
		}
			
		
	}

	private static int FindRNearest(Node stdnode) {
		if (stdnode.isleaf) {
			//leaf노드에 도달하면 가장 작은값, 즉 배열의 0번째 키를 리턴
			return stdnode.leafkeyarr.elementAt(0).key;
		}
		
		else {
			//non leaf노드라면 그 노드의 가장 가까운 오른쪽 값을 찾음(재귀)
			
			//기준노드가 삭제로 non leaf Pair가 하나도 없다면 오른노드에 가장 가까운 오른쪽값이 있음
			if (stdnode.nonleafkeyarr.size() == 0)
				return FindRNearest(stdnode.rightNode);
			
			//size가 1이상이면 가장 왼쪽 Left Child에 가장 가까운오른쪽값이 있음
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
		//curDepth -> 현재 뎁스
		//nextDepth -> 현재 뎁스 자식들의 뎁스
		try {
			int vs = curDepth.size();	//현재 뎁스의 노드 갯수
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
		//input.csv를 받아서 Tree구조에 넣는 메서드
		//file readline -> search -> insertion 반복
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
		
		pathNode.add(node);				// method가 leaf노드에 도착하면 leaf노드를 path에 추가하고 method 종료
		
		if (!(node.isleaf))		// 아니라면 다음 path를 찾음 	
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
		
		int i=pathnode.size()-1;			// leaf node (path의 제일 끝은 리프노드)
		
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
				int mid = pathnode.elementAt(i).leafkeyarr.elementAt(bptree.m/2).key; //중간값
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
		
		if (isleaf) {			//leaf에서의 오버플로우
			for(int i = 0; i< m/2;i++) {	//split시 중간 이전값으로 구성된 새로운 childnode를 만듬
				
				newKey = pathnode.elementAt(nodeidx).leafkeyarr.elementAt(0).key;
				newValue = pathnode.elementAt(nodeidx).leafkeyarr.elementAt(0).value;
				newChild.leafkeyarr.add(new leafPair(newKey,newValue));
				newChild.isleaf = true;
				pathnode.elementAt(nodeidx).leafkeyarr.remove(0);
			}
			newChild.rightNode = pathnode.elementAt(nodeidx);	//새 리프노드랑 기존 리프노드랑 연결
			if(nodeidx == 0) {		// leaf == root인 상황에서 오버플로우
				newParent.nonleafkeyarr.add(new nonleafPair(mid, newChild));
				newParent.rightNode = pathnode.elementAt(nodeidx);
				bptree.root = newParent;
				
			}else {					// leaf != root
				newPair.lcNode = newChild;
				if(!(insertPair(newPair, pathnode.elementAt(nodeidx-1)))) {	//pair를 노드에 넣었는데 오버플로우라면 재귀
					int newmid = pathnode.elementAt(nodeidx-1).nonleafkeyarr.elementAt(m/2).key;
					System.out.println("over & over : " + newmid);
					overflowCtrl(false,newmid,pathnode,nodeidx-1,bptree);
				}
			}
			
		}
		else {					//non leaf에서의 오버플로우
			for(int i = 0; i< m/2;i++) {	//split시 중간 이전값으로 구성된 새로운 childnode를 만듬 + 중간값 제거
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
			
			if(nodeidx == 0) {		//root인 상황에서 오버플로우
				newParent.nonleafkeyarr.add(new nonleafPair(mid, newChild));
				newParent.isleaf = false;
				newParent.rightNode = pathnode.elementAt(nodeidx);
				pathnode.elementAt(nodeidx).isleaf = false;
				bptree.root = newParent;
			}else {					// nonleaf노드가 root가 아닌 상황에서의 오버플로우
				newPair.lcNode = newChild;
				newChild.isleaf = false;
				if(!(insertPair(newPair, pathnode.elementAt(nodeidx-1)))) {	//pair를 노드에 넣었는데 오버플로우라면 재귀
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
			return false;			//pair를 넣었을 때 오버플로우

		else 
			return true;			//오버플로우 안일어남
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
				int m = Integer.parseInt(eachKey[0]); //노드의 키 또는 키,값 페어의 수
				if (eachKey.length -1 >m) { 
					// =======================leaf node=========================================
					
					for(int n =1; n<m+1 ;n++) {
						newNode.leafkeyarr.add(new leafPair(Integer.parseInt(eachKey[2*n-1])
									  						,Integer.parseInt(eachKey[2*n])));
					}
					if (linepack.size() == 1) { //line = 1 이면 root=leaf
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
	// work : 파일이름과 m의 값을 받아서 .dat file을 생성한다
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
