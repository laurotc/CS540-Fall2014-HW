import java.util.*;

public class PlayerImpl implements Player {
	// Identifies the player
	private int name = 0;
	int n = 0;       

	// Constructor
	public PlayerImpl(int name, int n) {
		this.name = 0;
		this.n = n;
	}

	// Function to find possible successors
	@Override
	public ArrayList<Integer> generateSuccessors(int lastMove, int[] crossedList) {
		// TODO Add your code here
                ArrayList<Integer> succ = new ArrayList<>();
               
                if(lastMove == -1){ 
                    //Calculate the the even values less than n/2
                    int n2 = this.n/2;
                    if( (this.n % 2)!=0 ){
                        n2++;
                    }
                    
                    for(int i=1; i<n2; i++){
                        if( (i % 2)==0 ){
                            succ.add(i);
                        }
                    }
                }
                else{
                    //Get the values that are multiple or factor of the lastMove
                    for(int i=1; i<=this.n; i++){
                        if(crossedList[i]==0){
                            if( i > lastMove ){
                                if( i%lastMove == 0 ){
                                    succ.add(i);  
                                }
                            }
                            else{
                                if( lastMove%i == 0 ){
                                    succ.add(i);
                                }                          
                            }
                        }
                    }
                }
                
		return succ;
	}

	// The max value function
	@Override
	public int max_value(GameState s) {
                // TODO Add your code here
                ArrayList<Integer> succ = generateSuccessors(s.lastMove, s.crossedList); 
                int best_a = -1;
                int a;
                
                if(succ.isEmpty()){
                    s.leaf = true;
                    return -1; 
                }      
                
                for(int i : succ){
                    GameState nextState = new GameState(s.crossedList, i);
                    nextState.crossedList[i] = 1;
                    a = min_value(nextState);
                    if((i > s.bestMove) && (a >= best_a)){
                        best_a = a;
                        s.bestMove = i;
                    }
                }

                return best_a;
	}

	// The min value function
	@Override
	public int min_value(GameState s) {
		// TODO Add your code here
                ArrayList<Integer> succ = generateSuccessors(s.lastMove, s.crossedList);
                int best_b = this.n+1;
                int b;
                
                if(succ.isEmpty()){
                    s.leaf = true;
                    return 1; 
                }
                
                for(int i : succ){
                    GameState nextState = new GameState(s.crossedList, i);
                    nextState.crossedList[i] = 2;
                    b = max_value(nextState);
                    if((i > s.bestMove) && (b <= best_b)){
                        best_b = b;
                        s.bestMove = i;
                    }
                }
                
		return best_b;
	}

	// Function to find the next best move
	@Override
	public int move(int lastMove, int[] crossedList) {
		// TODO Add your code here
                GameState gs = new GameState(crossedList, lastMove);
                gs.bestMove = -1;
                max_value(gs);
                
                return gs.bestMove;
	}
}