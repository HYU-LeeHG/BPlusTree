package bptree;
import java.io.*;
import java.lang.*;
import java.util.Vector;

import javax.swing.plaf.synth.SynthSplitPaneUI;


//Leaf 노드에 들어갈 페어
class leafPair {
	
	//키와 값으로 구성
	int key;
	int value;
	
	public leafPair(int k, int v) {
		key = k;
		value = v;
	}
}

//Non-Leaf 노드에 들어갈 페어
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
		
		//트리 생성
		Tree bptree = new Tree();
		
		//명령어가 없으면 
		if(args[0] == null) {
			System.out.println("Wrong CommandLine");	
			System.exit(0);	//종료
		}
		
		//Command Line을 읽어서 알맞은 메서드로 보내는 구문
		switch(args[0]) {
			//CREATION
			case "-c":
				System.out.println("creation");
				makeDat(args[1],args[2]);			//dat파일을 만듬.
				break;
			
			//INSERTION
			case "-i": // -i index.dat input.csv
				System.out.println("insertion");
				makeTree(args[1], bptree);			//index.dat를 읽고 트리를 만듬.
				getInputfile(args[2],bptree);		//input.csv을 읽고 트리에 추가함.
				saveTree(bptree,args[1]);			//트리를 index.dat에 저장함
				break;
				
			//DELETION
			case "-d":
				System.out.println("deletion");		
				makeTree(args[1], bptree);			//index.dat를 읽고 트리를 만듬.
				getDeletefile(args[2],bptree);		//delete.csv을 읽고 트리에서 제거함.
				saveTree(bptree,args[1]);			//트리를 index.dat에 저장함
				break;
				
			//SEARCH
			case "-s":
				System.out.println("search");
				makeTree(args[1], bptree);			//index.dat를 읽고 트리를 만듬.
				//커맨드라인 입력값을 찾고 path와 value값(또는 NOT FOUND)를 출력
				search(bptree.root, Integer.parseInt(args[2]));	
				break;
				
			//RANGE SEARCH
			case "-r":
				System.out.println("range search");
				makeTree(args[1], bptree);			//index.dat를 읽고 트리를 만듬.
				////커맨드라인 입력값인 최소값에서 최대값 사이의 수를 출력함
				rangeSearch(bptree.root, Integer.parseInt(args[2]),Integer.parseInt(args[3]));
				break;
				
			//WRONG COMMANDLINE
			default : 
				System.out.println("Wrong CommandLine");
		}
	
	}

	

	
	/* ==================================================================================
	 * 								DELETE 관련 함수들
	 * ==================================================================================*/
	
	//delete.csv를 받아서 Tree구조에 넣는 메서드
	//file readline -> search -> deletion 반복
	private static void getDeletefile(String filename, Tree bptree) {
		
		//file변수 생성
		FileReader in = null;
		//예외처리 try-catch문
		try {
			//file변수에 delete.csv대응
			in = new FileReader("C:/Users/Lee/eclipse-workspace/bptree/"+filename);
			//readLine을 위해 버퍼리더 생성 후 delete.csv대응
			BufferedReader br = new BufferedReader(in);
			//delete.csv의 한 줄씩 저장할 string vector 생성
			Vector<String> deletepack = new Vector<String>();
			//readLine을 위한 임시 String 선언
			String rltmp;
			
			//delete.csv가 끝날 때 까지 라인을 읽어 deletepack이라는 string vector에 저장
			 while((rltmp = br.readLine())!=null) {
				 deletepack.add(rltmp);
			 }

			 //deletepack을 0부터 읽으며 Tree에서 제거
			 for(int i=0; i<deletepack.size();i++ ) {
				 //지울 값이 있는(또는 있을 것이라고 추정되는) 리프노드로 가는 Path Node들을 저장하는 벡터
				 Vector<Node> pathNode = new Vector<Node>();
				 int delkey = Integer.parseInt(deletepack.elementAt(i));	//지울 값 선언
				 //root == null, 즉 트리가 비어있을 경우를 제외하고 path를 탐색
				 if (bptree.root != null) {
					 findPath(delkey,bptree.root, pathNode);
				 }
				 //bptree에서 delkey 삭제
				 deletion(delkey,bptree,pathNode);
				 
				 }
		}
		//입출력 예외처리
		catch(IOException ioe) {
		} finally {
				try {
					//file close
					in.close();
				}catch(Exception e) {
			}
		}
		
	}
	
	//key와 tree를 입력받아서 leaf 노드에서 key를 지우는 함수
	//리프노드에서 제거 	-> 이상없음 -> 상위노드의 key값 변경있는지 여부체크(keyChange메서드)
	//				-> 언더플로우 -> 언더플로우관리메서드 call
	private static void deletion(int key, Tree bptree, Vector<Node> pathnode) {

		int i=pathnode.size()-1;			// leaf node (path의 제일 끝은 리프노드)
		
		//Tree가 비었다면 비었음을 알리고 종료
		if ( i== -1) {		
			System.out.println("Tree is empty");
			System.exit(0);
		}
		
		//Tree가 비어있지 않다면
		else{
			int ns = pathnode.elementAt(i).leafkeyarr.size();	//삭제값이 있다고 추정되는 leaf node의 사이즈
			
			
			//처음부터 노드사이즈까지 루프돌며 같은 값 있는지 확인 후 제거
			out:	//탈출지점
			for(int j=0; j<ns;j++) {
				if(key == pathnode.elementAt(i).leafkeyarr.elementAt(j).key) {
					pathnode.elementAt(i).leafkeyarr.remove(j);
					break out;
				}
			}
			//i==0이면 리프=루트인 트리므로 값이 하나이상이면 된다.
			//값이 0개면 empty tree가 되는 것이므로 underflowCtrl이 불필요
			if (i!=0){	
				if (pathnode.elementAt(i).leafkeyarr.size() < (bptree.m-1)/2) {		//underflow 조건
					underflowCtrl(true, key, pathnode,i,bptree);					//Control underflow
				}
				else
					keyChange(pathnode, i, key);	//바뀔 키값이 상위노드에 있는 지 확인 후 변경
			}
		}
		
		
		
	}

	/*underflow를 관리하는 함수
	 * -순서대로 시도해보고 안되면 다음 방법을 쓴다.
	 * -모든 경우가 안되는 경우는 없다. ( root일 경우는 위에서 처리 )
	 *	A. 왼쪽노드에서 값 하나를 받는다.
	 *	B. 오른쪽노드에서 값 하나를 받는다.
	 *	C. 왼쪽노드랑 병합한다.
	 *	D. 오른쪽노드랑 병합한다.
	 *
	 *   ※설명에서 NODE는 삭제가 일어난 노드!!						
	 */

	private static void underflowCtrl(boolean isleaf, int key, Vector<Node> pathnode, int idx, Tree bptree) {
		
		// underflow난 노드가 루트일때
		if(idx == 0) {	
			//root의 자식이 하나만 있을때 -> 그 하나의 자식을 새 root로 설정
			if (!(pathnode.elementAt(idx).isleaf) && pathnode.elementAt(idx).nonleafkeyarr.size() ==0) {
					bptree.root = pathnode.elementAt(idx).rightNode;
			}
		}
		
		//root가 아닐 때의 underflow control 
		else {	
		
		//underflow control이 끝났음을 알리는 탈출지점
		out:
			
		//NODE(underflow난 노드, 이하생략)가 leaf 노드 일때
		if(isleaf) {
			
			// A방법( 왼쪽노드에서 값 받기 )=========================================================
			// 해당 방법(A)은 조건이 맞지 않아 다음 방법으로 넘어가야됨을 알리는 탈출지점
			out1:
			//부모노드에서 NODE의 index를 찾는 루프
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				
				//NODE의 위치가 i일때
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					//i==0이면 왼쪽노드가 없으므로 다음 방법으로 이동
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
						pathnode.elementAt(idx).leafkeyarr.add(0,new leafPair(moveKey, moveVal));
						
						// 왼쪽노드의 마지막key,value 삭제
						pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).
								lcNode.leafkeyarr.remove(lcSize-1);
						// 값 이동으로 인한 non-leaf노드의 key값 변경관리
						keyChange(pathnode,idx,key);
						// CTRL(underflow control, 이하생략)이 끝났음을 알림
						break out;
					}
					//왼쪽노드의 값의 수가 충분치 않으므로 다음 방법으로 이동
					else
						break out1;
				}
				
				//NODE가 부모노드의 right-child node인 case
				//세부 방법은 위와 동일(여기서 i는 삭제노드의 왼쪽노드인 것만 다름)
				else if(i == pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
					int lcSize = pathnode.elementAt(idx-1).	// 삭제노드의 왼쪽형제노드의 사이즈
							nonleafkeyarr.elementAt(i).lcNode.leafkeyarr.size();
					
					//왼쪽노드의 값 갯수가 n/2보다 많을때
					if(lcSize>(bptree.m-1)/2) {
						//왼쪽노드의 마지막 key,value
						int moveKey =pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).
								lcNode.leafkeyarr.elementAt(lcSize-1).key;
						int moveVal = pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).
								lcNode.leafkeyarr.elementAt(lcSize-1).value;
						
						// 왼쪽노드의 key,value를 삭제노드에 추가
						pathnode.elementAt(idx).leafkeyarr.add(0,new leafPair(moveKey, moveVal));
						
						//왼쪽노드의 마지막key,value 삭제
						pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).
						lcNode.leafkeyarr.remove(lcSize-1);
						keyChange(pathnode,idx,key);
						break out;
					}
					else
						break out1;
				}
			}
			// B방법( 오른쪽노드에서 값 받기 )=========================================================
			// 해당 방법(B)은 조건이 맞지 않아 다음 방법으로 넘어가야됨을 알리는 탈출지점
			out2:
			// 부모노드에서 NODE의 index를 찾는 루프
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				//i == index인 경우
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					//우측 노드가 부모노드의 right child node일 때
					if(i==pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
						//우측노드가 값이 충분하면
						if(pathnode.elementAt(idx-1).rightNode.leafkeyarr.size()>(bptree.m-1)/2) {
							
							//옮길 키,값 => 오른노드의 제일 작은 키,값
							int moveKey =pathnode.elementAt(idx-1).rightNode.leafkeyarr.elementAt(0).key;
							int moveVal = pathnode.elementAt(idx-1).rightNode.leafkeyarr.elementAt(0).value;
							
							// 오른쪽노드의 key,value를 삭제노드에 추가
							pathnode.elementAt(idx).leafkeyarr.add(new leafPair(moveKey, moveVal));
							
							//오른쪽노드의 마지막key,value 삭제
							pathnode.elementAt(idx-1).rightNode.leafkeyarr.remove(0);
							keyChange(pathnode,idx,key);
							break out;
						}
						else
							break out2;
					}
					// 우측 노드가 NODE의 다음 index(i+1)에 위치한 노드일 때
					else {
						int rcSize = pathnode.elementAt(idx-1).	// 삭제노드의 오른쪽쪽형제노드의 사이즈
								nonleafkeyarr.elementAt(i+1).lcNode.leafkeyarr.size();
						
						//오른쪽노드의 값 갯수가 n/2보다 많을때
						if(rcSize>(bptree.m-1)/2) {
							if(key == 27)
								System.out.println("case5");
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
						else break out2;
					}
				}
			}

			// C방법( 왼쪽노드와 병합 )=========================================================
			// -왼노드와 병합할 때 왼노드의 값을 NODE로 옮긴다.

			// 해당 방법(C)은 조건이 맞지 않아 다음 방법으로 넘어가야됨을 알리는 탈출지점
			out3:
			// 부모노드에서 NODE의 index를 찾는 루프
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				//NODE가 부모노드에서 i번째 index일떄
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					if(i==0)		//삭제노드가 제일 왼쪽노드면 왼노드가 없으므로 탈출
						break out3;
					// 병합으로 인해 사라질 부모의 non-leaf페어의 키
					int delKey = key;
					int lcSize = pathnode.elementAt(idx-1).	// 삭제노드의 왼쪽형제노드의 사이즈
							nonleafkeyarr.elementAt(i-1).lcNode.leafkeyarr.size();
					
					//NODE에 삭제키말고 다른 키가 더 있을 때
					if(pathnode.elementAt(idx).leafkeyarr.size() != 0) {
						//NODE에서 제일 작은 값을 delKey로 설정(삭제키 제외)
						delKey = pathnode.elementAt(idx).leafkeyarr.elementAt(0).key;
						//제일 작은 값이 삭제키보다 작다면, delKey 변경
						if (delKey > key)
							delKey = key;
					}
					
					//왼쪽노드의 pair를 전부 NODE에 넣음
					for(int j=0;j<lcSize;j++) {
						int moveKey = pathnode.elementAt(idx-1).	// 삭제노드의 왼쪽형제노드의 j번째 키
								nonleafkeyarr.elementAt(i-1).lcNode.leafkeyarr.elementAt(j).key;
						int moveVal = pathnode.elementAt(idx-1).	// 삭제노드의 왼쪽형제노드의 j번째 값
								nonleafkeyarr.elementAt(i-1).lcNode.leafkeyarr.elementAt(j).value;
						pathnode.elementAt(idx).leafkeyarr.add(j, new leafPair(moveKey,moveVal));
					}
					
					// 병합으로 인해 페어가 삭제된 부모노드에서 underflow가 일어나는 지에 대한 boolean
					// 정상적 삭제 -> true, underflow-> false
					boolean delsuccess = deletePair(delKey,pathnode.elementAt(idx-1));
					keyChange(pathnode,idx,key);
					if(!(delsuccess)) {
						//재귀형식으로 부모노드 underflow control
						underflowCtrl(false,delKey,pathnode,idx-1,bptree);
					}
					
					break out;
				}
				
				//NODE가 부모노드의 right-child node일때 (세부내용은 위와 같음)
				else if(i == pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
					int delKey = key;
					int lcSize = pathnode.elementAt(idx-1).	// 삭제노드의 왼쪽형제노드의 사이즈
							nonleafkeyarr.elementAt(i).lcNode.leafkeyarr.size();
					
					if(pathnode.elementAt(idx).leafkeyarr.size() != 0) {
						delKey = pathnode.elementAt(idx).leafkeyarr.elementAt(0).key;
						if (key < delKey)
							delKey = key;
					}
					for(int j=0;j<lcSize;j++) {
						int moveKey = pathnode.elementAt(idx-1).	// 삭제노드의 왼쪽형제노드의 j번째 키
								nonleafkeyarr.elementAt(i).lcNode.leafkeyarr.elementAt(j).key;
						int moveVal = pathnode.elementAt(idx-1).	// 삭제노드의 왼쪽형제노드의 j번째 값
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
			
			// D방법( 오른쪽노드와 병합 )=========================================================
			// -오른노드와 병합할 때 NODE의 값을 오른노드로 옮긴다.

			// 부모노드에서 NODE의 index를 찾는 루프
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				// i가 NODE의 부모노드에서의 index일때
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					//병합으로 인해 삭제될 부모노드의 페어의 키
					int delKey;
					//뒤에 더 이상 nonleafkeyPair가 없으면 rightNode 확인
					if(i==pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
						//delKey는 오른노드의 첫번째값
						delKey = pathnode.elementAt(idx-1).rightNode.leafkeyarr.elementAt(0).key;
						
						//NODE의 값을 오른노드로 옮기는 루프
						for(int j=0;j<pathnode.elementAt(idx).leafkeyarr.size();j++) {
							//NODE의 값을 순서대로 끼워넣는다.
							//오른노드의 구조가 {(NODE의 값들)(기존값들)}
							int moveKey = pathnode.elementAt(idx).leafkeyarr.elementAt(j).key;
							int moveVal = pathnode.elementAt(idx).leafkeyarr.elementAt(j).value;
							
							//j번째값을 오른노드의 j번째에 넣는다.
							pathnode.elementAt(idx-1).rightNode.leafkeyarr.add(j, new leafPair(moveKey, moveVal)); 
						}
						break out;
					}
					//오른노드의 부모에서의 index가 i+1일때( 세부사항은 위와 동일 )
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
					
					// C와 동일
					boolean delsuccess = deletePair(delKey,pathnode.elementAt(idx-1));
					keyChange(pathnode,idx,key);
					if(!(delsuccess)) {
						underflowCtrl(false,delKey,pathnode,idx-1,bptree);
					} 
					break out;
				}
			}
		
		}
		
		// NODE가 non-leaf일때
		// 세부사항이 NODE가 leaf일 때와 일치
		else {
		
			// 방법 A : 왼노드에서 페어 받기
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
						
						//NODE에 왼쪽에서 준 pair 삽입
						insertPair(pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).lcNode
								.nonleafkeyarr.elementAt(lcSize-1), pathnode.elementAt(idx));
						
						keyChange(pathnode,idx,key);
						break out;
					}
					else 
						break out1;
				}
				else if(i == pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
					int lcSize = pathnode.elementAt(idx-1).	// 삭제노드의 왼쪽형제노드의 사이즈
							nonleafkeyarr.elementAt(i).lcNode.nonleafkeyarr.size();
					
					//왼쪽노드의 값 갯수가 n/2보다 많을때
					if(lcSize>(bptree.m-1)/2) {
						Node tmpNode = new Node();
						
						//왼쪽에서 줄 값의 left child = 왼노드의 rightNode
						//왼노드의 rightNode =왼쪽에서 줄 값의 left child		값 체인지 
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
			// 방법 B : 오른노드에서 페어 받기
			out2:
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					
					
					//뒤에 더 이상 nonleafkeyPair가 없으면 rightNode 확인
					if(i==pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
						if(pathnode.elementAt(idx-1).rightNode.nonleafkeyarr.size()>(bptree.m-1)/2) {
							Node tmpNode= new Node();
							
							//NODE의 right-child node -> 오른노드가 주는값
							//오른노드에서 주는 pair의 left-child node -> NODE의 right-child 노드		값 체인지
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
						else
							break out2;
					}
				}
			}
			// 방법 C : 왼노드랑 병합
			out3:
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					if(i==0)		//NODE가 제일 왼쪽노드면 왼노드가 없으므로 탈출
						break out3;
					int lcSize = pathnode.elementAt(idx-1).	// NODE의 왼쪽형제노드의 사이즈
							nonleafkeyarr.elementAt(i-1).lcNode.nonleafkeyarr.size();
					//왼쪽노드의 값을 NODE로 옮김
					for(int j=0;j<lcSize;j++) {
						pathnode.elementAt(idx).nonleafkeyarr.add(j,
								pathnode.elementAt(idx-1).nonleafkeyarr.
								elementAt(i-1).lcNode.nonleafkeyarr.elementAt(j));
					}
					//병합될 때 왼쪽노드를 가리키는 부모노드의 페어가 같이 병합된다.
					// {왼쪽노드/부모의페어/NODE}가 연결되어 하나의 노드가 된다.
					
					// 부모페어의 leftchild -> 왼쪽노드의 rightchild
					pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1).lcNode = 
							pathnode.elementAt(idx-1).nonleafkeyarr.
							elementAt(i-1).lcNode.rightNode;
					//NODE에 부모페어 추가(왼쪽노드값과 NODE값의 사이)
					pathnode.elementAt(idx).nonleafkeyarr.add(lcSize,
							pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i-1));	
					// 부모노드에서 부모페어 삭제
					pathnode.elementAt(idx-1).nonleafkeyarr.remove(i-1);
					keyChange(pathnode,idx,key);
					
					// 부모노드에서 underflow일어나는 지 확인
					if(pathnode.elementAt(idx-1).nonleafkeyarr.size() < (bptree.m-1)/2) {
						int delKey = pathnode.elementAt(idx).nonleafkeyarr.elementAt(0).key;
						if (delKey > key)
							key = delKey;
						// 부모노드의 underflow를 재귀적으로 처리
						underflowCtrl(false,delKey,pathnode,idx-1,bptree);
						break out;
					} 
					else 
						break out3;
				}
				//NODE가 부모노드의 right-child node일 때 ( 세부사항은 위와 동일 )
				else if(i == pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
					int lcSize = pathnode.elementAt(idx-1).	// 삭제노드의 왼쪽형제노드의 사이즈
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
			
			 
			// 방법 D : 오른쪽노드랑 병합
			for(int i=0; i<pathnode.elementAt(idx-1).nonleafkeyarr.size();i++) {
				if(key<pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).key) {
					int delKey;
					//뒤에 더 이상 nonleafkeyPair가 없으면 rightNode 확인
					if(i==pathnode.elementAt(idx-1).nonleafkeyarr.size()-1) {
						int dSize = pathnode.elementAt(idx).nonleafkeyarr.size();	// 삭제노드의 사이즈
						//NODE의 값을 오른노드로 옮김
						for(int j=0;j<dSize;j++) {
							pathnode.elementAt(idx-1).rightNode.nonleafkeyarr.add(j,
									pathnode.elementAt(idx-1).nonleafkeyarr.
									elementAt(i).lcNode.nonleafkeyarr.elementAt(j));
						}
						
						//병합될 때 NODE를 가리키는 부모노드의 페어가 같이 병합된다.
						// {NODE/부모의페어/오른노드}가 연결되어 하나의 노드가 된다.
						
						//부모페어의 left-child -> NODE의 right-child
						pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i).lcNode = 
								pathnode.elementAt(idx).rightNode;
						//오른노드에 부모페어 삽입
						pathnode.elementAt(idx-1).rightNode.nonleafkeyarr.add(dSize,
								pathnode.elementAt(idx-1).nonleafkeyarr.elementAt(i));
						//부모노드에서 부모페어 삭제
						pathnode.elementAt(idx-1).nonleafkeyarr.remove(i);
						
						
						 
					}
					//오른노드의 부모에서의 index가 NODE의 index +1 일때( 세부사항은 위와 동일 )
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
	}
	
	/* 삭제된 키가 있던 노드로 가는 모든 path의 키값이 타당한지 검사
	 * pathnode(idx)부터 삭제된 키값이 있는 리프노드까지 가면서
	 * 키값이 변할게 있는지 확인 후 변경하는 메서드				*/
	private static void keyChange(Vector<Node> pathnode, int idx, int delkey) {
		out:
		for(int i=0; i<pathnode.size();i++) {
		//for문 inside = i번쨰 path node (0이 루트)
			
			//리프노드에서의 변화는 underflowCtrl()에서 진행
			//따라서 non leaf인지 판별
			if(pathnode.elementAt(i).isleaf) {
				break out;
			}
			
			//삭제된 키가 있는 path를 찾음
			for(int j=0;j<pathnode.elementAt(i).size();j++) {
				if(delkey < pathnode.elementAt(i).nonleafkeyarr.elementAt(j).key) {
					if(j!=0) {	//j==0이면 왼쪽페어가 없기때문에
						
						//바뀔 노드의 키값 = 그 노드의 오른쪽에 있는 가장 작은 수 (FindRNearest가 리턴해줌)
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

	//target노드에 있는 delKey를 가진 페어를 지움
	private static boolean deletePair(int delKey, Node target) {
		
		//target노드의 index를 순회하는 루프
		for(int i =0; i<target.nonleafkeyarr.size();i++) {
			//target의 페어 중 delKey와 같은 키를 가진 페어가 있다면 삭제
			if(target.nonleafkeyarr.elementAt(i).key == delKey)
				target.nonleafkeyarr.remove(i);
		}
		
		//삭제 후 underflow가 일어나면 false, 삭제 후 정상이면 true 리턴
		if(target.nonleafkeyarr.size()<(target.m-1)/2)
			return false;
		else
			return true;
	}

	
	/*
	 * ==================================================================================
	 * 								SEARCH 관련 함수들
	 * ==================================================================================*/

	//stdnode부터 num을 찾아 출력하는 함수
	private static void search(Node stdnode, int num) {
		if (stdnode.isleaf == true) {
			//stdnode가 leaf면 num과 같은 키를 페어를 찾아 있다면 value를 없다면 NOT FOUND를 출력
			for(int i = 0 ; i<stdnode.leafkeyarr.size();i++) {
				if (stdnode.leafkeyarr.elementAt(i).key == num)
					System.out.println(stdnode.leafkeyarr.elementAt(i).value);
				else if(i == stdnode.leafkeyarr.size()-1)
					System.out.println("NOT FOUND");
			}
		}
		else {
			//stdnode가 non-leaf면 
			//stdnode도 num을 찾는 path node 중 하나 이므로
			//stdnode의 데이타를 출력
			for(int i =0 ; i < stdnode.nonleafkeyarr.size();i++) {
				//stdnode의 데이터 출력
				System.out.print(stdnode.nonleafkeyarr.elementAt(i).key);
				//마지막 값이 아니라면 사이사이 콤마를 출력
				if(i != stdnode.nonleafkeyarr.size()-1) 
					System.out.print(",");
				//마지막 값이면 라인 띄우기
				else
					System.out.println();
			}
			
			//출력을 하고 num이 있을 stdnode의 childnode에서 다시 search (재귀)
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
	
	//입력받은 min이상 max이하의 key를 가진 페어 출력
	private static void rangeSearch(Node root, int min, int max) {
		
		//min이 있거나 또는 있을 것이라 추정되는 Node를 찾아서 저장
		Node minIncluded = findMin(root, min);
		
		int i = 0;	//loop index
		out:
		while(true) {
			//Node의 페어의 키값이 min이상 max이하라면 출력 
			if(minIncluded.leafkeyarr.elementAt(i).key >= min
					&& minIncluded.leafkeyarr.elementAt(i).key <=max) {
				System.out.println(minIncluded.leafkeyarr.elementAt(i).key + ","+
						minIncluded.leafkeyarr.elementAt(i).value);
			}
			
			//만약 max보다 큰 키를 가진 페어가 나왔다면 그 뒤로도 점점 커지므로 loop break
			else if (minIncluded.leafkeyarr.elementAt(i).key >max)
				break out;
			
			//만약 Node의 끝에 도달했다면 Node의 rightNode로 이동
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
			//Node의 끝이 아니라면 i값 증가
			else
				i++;
			
			if (i > 100000) break out; 		//무분별한 무한루프를 위한 제한
		}
		
		
	}

	//stdnode에서 min을 포함하는 자식노드를 리턴하는 함수 ( 재귀하면 결국 leaf노드가 리턴 )
	private static Node findMin(Node stdnode, int min) {
		//stdnode가 leaf면 리턴
		if (stdnode.isleaf)
			return stdnode;
		
		//아니라면 stdnode의 자식 중 min이 조건에 맞는 자식노드로 findMin 다시 call
		else {
			for(int i = 0; i<stdnode.size();i++) {
				if(min < stdnode.nonleafkeyarr.elementAt(i).key) {
					return findMin(stdnode.nonleafkeyarr.elementAt(i).lcNode, min);
				}	
			}
			return findMin(stdnode.rightNode, min);
		}
			
	}


	//Tree를 index.dat에 저장하는 메서드
	private static void saveTree(Tree bptree, String filename) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter("C:/Users/Lee/eclipse-workspace/bptree/"+filename));
			//index.dat 최상단에 Tree의 m 값 저장
			out.println(String.valueOf(bptree.m));
			//Tree의 한 Depth의 노드를 저장하는 벡터
			Vector<Node> nextDepth = new Vector<Node>();
			//Depth의 시작은 root
			nextDepth.add(bptree.root);
			//현재 Depth의 출력 및 다음 Depth를 재귀적으로 찾음
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
		try {	//nullptrException : 빈 포인터를 참조할 수 있으므로 (빈 곳은 무시)
			int vs = curDepth.size();	//현재 뎁스의 노드 갯수
			if(curDepth.elementAt(0).isleaf) {
				//현재 뎁스가 Leaf Depth면 모든 Node의 key,value를 오름차순으로 출력하고 메서드 종료
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
				//현재 뎁스가 Non-Leaf Depth라면 모든 Node의 key를 출력하며 NextDepth에 CurDepth의 자식들 저장
				//다음 뎁스의 노드를 저장하는 벡터
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
				//현재 뎁스를 끝내면 다음 뎁스로 재귀적으로 이동
				findNextDepth(out, nextDepth);
			}
		} catch(NullPointerException npe) {
			
		}
		
	}

	private static void getInputfile(String filename, Tree bptree) {
		//input.csv를 받아서 Tree구조에 넣는 메서드
		//file readline -> search -> insertion 반복 (getDeletefile과 유사)
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
				 //읽어들인 라인을 콤마로 쪼개서 각각 저장(각각 key, value)
				 String[] kv = inputpack.elementAt(i).split(",");
				 
				
				 if (bptree.root != null) {
					 //tree가 비어있지 않으면 새 key,value가 들어갈 path찾음
					 findPath(Integer.parseInt(kv[0]),bptree.root, pathNode);
				 }
				 //tree에 새 key,value를 넣음
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

	//key를 기준으로 새 값이 들어갈 node의 path를 찾아서 pathNode에 저장
	private static void findPath(int key, Node node, Vector<Node> pathNode) {
		
		pathNode.add(node);				// method가 leaf노드에 도착하면 leaf노드를 path에 추가하고 method 종료
		
		if (!(node.isleaf))				// 아니라면 다음 path를 찾음 	
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
	
	//새 값을 pathnode의 제일 끝(리프노드)에 넣음
	private static void insertion(int key, int value, Tree bptree, Vector<Node> pathnode) {
		
		int i=pathnode.size()-1;			// leaf node (path의 제일 끝은 리프노드)
		
		if ( i== -1) {		//first input
			//첫 입력이므로 루트를 만들어 새 값을 넣음
			Node newRoot = new Node(bptree.m);
			newRoot.isleaf = true;
			newRoot.leafkeyarr.add(new leafPair(key,value));
			bptree.root = newRoot;
		}	
		else{				//첫 입력이 아니면
			int ns = pathnode.elementAt(i).leafkeyarr.size();
			//새 값을 leaf node의 오름차순이 유지되도록 삽입
			out:
			for(int j=0; j<ns;j++) {
				if(key < pathnode.elementAt(i).leafkeyarr.elementAt(j).key) {
					//j번째값이 key보다 크면 j번째에 삽입
					pathnode.elementAt(i).leafkeyarr.add(j,new leafPair(key,value));
					break out;
				}
				else if(j == pathnode.elementAt(i).leafkeyarr.size()-1) {
					// key보다 큰 값이 없으면 마지막에 삽입
					pathnode.elementAt(i).leafkeyarr.add(new leafPair(key,value));
				}
			}
			
			if (pathnode.elementAt(i).leafkeyarr.size() >= bptree.m) {
				// 삽입이 overflow를 초래하면 overflowCtrl Call
				int mid = pathnode.elementAt(i).leafkeyarr.elementAt(bptree.m/2).key; //중간값
				overflowCtrl(true, mid,pathnode,i,bptree);
			}	
		}	
	}
	
	//overflow가 일어나면 처리하는 함수
	//leaf case : 중간값을 올리고 부모노드에서도 오버플로가 일어나면 overflowCtrl recall
	//nonleaf case : 중간값을 올리고 해당 값은 지우고 부모노드에서도 오버플로가 일어나면 overflowCtrl recall
	private static void overflowCtrl(boolean isleaf, int mid, Vector<Node> pathnode, int nodeidx,Tree bptree) {
		int m = bptree.m;
		int newKey, newValue;
		
		Node newChild = new Node(m);
		Node newParent = new Node(m,false);
		nonleafPair newPair = new nonleafPair(mid, null);
		
		if (isleaf) {			//leaf에서의 오버플로우
			for(int i = 0; i< m/2;i++) {	//split시 중간 이전값으로 구성된 새로운 childnode를 만듬
				//loop를 m/2-1번만 진행하고 매번 0번째 값들만 pop해서 새노드에 넣음
				newKey = pathnode.elementAt(nodeidx).leafkeyarr.elementAt(0).key;
				newValue = pathnode.elementAt(nodeidx).leafkeyarr.elementAt(0).value;
				newChild.leafkeyarr.add(new leafPair(newKey,newValue));
				newChild.isleaf = true;
				pathnode.elementAt(nodeidx).leafkeyarr.remove(0);
			}
			newChild.rightNode = pathnode.elementAt(nodeidx);	//새 리프노드랑 기존 리프노드랑 연결
			if(nodeidx == 0) {		// leaf == root인 상황에서 오버플로우
				//새로운 root를 만듬
				newParent.nonleafkeyarr.add(new nonleafPair(mid, newChild));
				newParent.rightNode = pathnode.elementAt(nodeidx);
				bptree.root = newParent;
				
			}else {					// leaf != root
				//leftchild가 newChild인 pair를 만듬
				newPair.lcNode = newChild;
				if(!(insertPair(newPair, pathnode.elementAt(nodeidx-1)))) {	//pair를 노드에 넣었는데 오버플로우라면 재귀
					int newmid = pathnode.elementAt(nodeidx-1).nonleafkeyarr.elementAt(m/2).key;
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
			//newChild의 right-child는 올라가는 중간값이 가리키던 leftchild가 됨
			//(중간값의 leftchild는 newChild가 됨)
			newChild.rightNode = pathnode.elementAt(nodeidx).nonleafkeyarr.elementAt(0).lcNode;
			//스플릿시 중간값은 낮은 뎁스에서는 삭제
			pathnode.elementAt(nodeidx).nonleafkeyarr.remove(0);
			
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

	//newpair을 target노드에 넣는 메서드
	private static boolean insertPair(nonleafPair newpair,Node target) {
		int ns = target.nonleafkeyarr.size();
		
		out:
		for(int i =0; i<ns;i++)
		{
			//오름차순이 깨지지 않도록 맞는 위치를 찾아서 넣음
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

	
	//index.dat을 읽고 Tree를 만드는 메서드
	//한 줄씩(한 뎁스씩) -> 한 노드씩 -> 한 페어씩 으로 점점 쪼개서 읽어들임
	private static void makeTree(String filename, Tree bptree) {
		String rltmp;//readLineTmp
		FileReader in = null;
		try {
		in = new FileReader("C:/Users/Lee/eclipse-workspace/bptree/"+filename);
		BufferedReader br = new BufferedReader(in);
		bptree.m = Character.getNumericValue(in.read());
		br.readLine();
		
		//한 라인( 한 뎁스 )가 저장되는 String Vector
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
					
					//새 노드에 읽어들인 key, value 페어를 넣음
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
						nodeLink(bptree.root,std,newNode);	//만들어진 Tree에 newNode를 연결함
						
					}
					
					//리프노드에서 노드끼리 연결하기 위해 예전 값을 저장해둠
					oldstd = Integer.parseInt(eachKey[1]);
				} else{						
					// ========================non leaf node====================================
					newNode = new Node(bptree.m, false);
					//newNode에 새 값페어를 넣어줌
					for(int n =1;n<m+1;n++) {
						newNode.nonleafkeyarr.add(new nonleafPair(Integer.parseInt(eachKey[n])
																  ,null));
					}
					if (r==0) { //root case
						bptree.root = newNode;
					} else {
						int std = newNode.nonleafkeyarr.elementAt(0).key;
						nodeLink(bptree.root,std,newNode);		//기존 tree랑 링크
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

	//newnode를 stdnode의 제일 낮은 레벨이랑 연결하는 메서드
	private static void nodeLink(Node stdnode, int std, Node newnode) {
		out:
		for(int i=0; i<stdnode.nonleafkeyarr.size();i++)
		{
			if (std < stdnode.nonleafkeyarr.elementAt(i).key) {			// goto left
				if (stdnode.nonleafkeyarr.elementAt(i).lcNode ==null) {
					//만약 비어있다면 newnode를 빈 곳에 넣음
					stdnode.nonleafkeyarr.elementAt(i).lcNode = newnode;
					newnode.m = stdnode.m;
				}
				else {
					// 비어있지않다면 하나 더 낮은 레벨로 들어감 ( 비어있을때까지 재귀 )
					nodeLink(stdnode.nonleafkeyarr.elementAt(i).lcNode,std,newnode);
					
				}
				break out;
			}
			else if(i == stdnode.nonleafkeyarr.size()-1) {				// goto right
				if (stdnode.rightNode == null) {
					//비었다면 연결
					stdnode.rightNode= newnode;
					newnode.m = stdnode.m;
				}
				else {
					//아니라면 낮은 레벨로...
					nodeLink(stdnode.rightNode,std,newnode);
				}
					
			}
		}
	}
	
	//리프노드와 리프노드를 연결하는 메서드
	//stdnode에서 oldstd를 찾은 후 stdnode.right = newnode로 연결
	//세부사항은 nodeLink와 동일
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
	// work : 파일이름과 m의 값을 받아서 .dat file을 생성한다
		FileWriter out = null;
		try {
		out = new FileWriter("C:/Users/Lee/eclipse-workspace/bptree/"+filename);
		out.write(bnum);
		
		//tree 활성화
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
	public int m;		//차수
	Node root;			//root 저장
	public Tree() {
	}
	
	public Tree(int num){
		m = num;
		root.m = num;
	}
	
}

class Node{
	boolean isleaf; //true = leaf, false= non-leaf
	Vector<leafPair> leafkeyarr;		//leafpair(key,value)을 저장하는 벡터
	Vector<nonleafPair> nonleafkeyarr;	//nonleafpair(key,leftchild)을 저장하는 벡터
	Node rightNode;	//non-leaf -> right child node ,, leaf ->rightmost node
	int m;
	
	//다양한 생성자들...
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
