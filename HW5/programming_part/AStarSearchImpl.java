import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class AStarSearchImpl implements AStarSearch {

    @Override
    public SearchResult search(String initConfig, int modeFlag) {
        // TODO Add your code here
        int iter = 0;
        char[] movements = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
        State[] sucessors = new State[8];

        /*Auxiliary Open List */
        Map<String, Integer> open = new HashMap<>();
        /*Open List - Priority Queue*/
        Queue<State> openPriority = new PriorityQueue<>(100, State.comparator);
        /*Closed List*/
        Map<String, Integer> closed = new HashMap<>();

        //Put the start state S on the priority queue, called OPEN
        State initial = new State(initConfig, 0, getHeuristicCost(initConfig, modeFlag), "");
        open.put(initial.config, initial.heuristicCost + initial.realCost);
        openPriority.add(initial);

        while (!open.isEmpty()) {
            //Remove from OPEN and place in CLOSED a state n for which f(n) is the minimum
            State n = openPriority.poll();
            open.remove(n.config);
            closed.put(n.config, n.heuristicCost + n.realCost);
            iter++;

            if (checkGoal(n.config)) {
                return new SearchResult(n.config, n.opSequence, iter);
            }

            //Expand n, generating all its successors and attach to them pointers back to n.
            int mov = 0;
            for (char movement : movements) {
                String newConfig = move(n.config, movement);
                sucessors[mov] = new State(newConfig, n.realCost + 1, getHeuristicCost(newConfig, modeFlag), "" + n.opSequence + movement);
                mov++;
            }

            for (State nPrime : sucessors) {
                if (!open.containsKey(nPrime.config) || !closed.containsKey(nPrime.config)) {
                    /*Estimate h(n'), g(n') = g(n) + c(n, n'), f (n') = g(n') + h(n')
                     Place n' on OPEN*/
                    openPriority.add(nPrime);
                    open.put(nPrime.config, nPrime.heuristicCost + nPrime.realCost);
                } else {
                    /*if g(n') strictly less than its old g value in OPEN or CLOSED*/
                    int g_nPrimeNew = nPrime.heuristicCost + nPrime.realCost;
                    if (g_nPrimeNew < open.get(nPrime.config) || g_nPrimeNew < closed.get(nPrime.config)) {
                        if (open.containsKey(nPrime.config)) {
                            //Update g(n') in OPEN.
                            openPriority.remove(nPrime);
                            openPriority.add(nPrime);
                            open.put(nPrime.config, g_nPrimeNew);
                        } else {
                            //Remove n' from CLOSED and place it on OPEN with new g(n')
                            closed.remove(nPrime.config);
                            openPriority.add(nPrime);
                            open.put(nPrime.config, g_nPrimeNew);
                        }

                    }

                }
            }
        }
        return null;
    }

    @Override
    public boolean checkGoal(String config) {
        // TODO Add your code here
        int block1[] = new int[]{1, 1, 1, 1, 1, 1, 1, 1};
        int block2[] = new int[]{2, 2, 2, 2, 2, 2, 2, 2};
        int block3[] = new int[]{3, 3, 3, 3, 3, 3, 3, 3};

        int[] block = getBlock(config);

        return Arrays.equals(block, block1) || Arrays.equals(block, block2) || Arrays.equals(block, block3);
    }

    @Override
    public String move(String config, char op) {
        // TODO Add your code here
        char[][] matrix = configToMatrix(config);
        char[] aux = new char[7];

        /*Each movement moves only the col or row related to the op.
         The values are shifted and put in an array. 
         After finish, they are put back to the respective col or row from where they came from.*/
        if (op == 'A') {
            for (int i = 0; i < 7; i++) {
                if (i == 6) {
                    aux[i] = matrix[0][2];
                } else {
                    aux[i] = matrix[i + 1][2];
                }
            }
            for (int i = 0; i < 7; i++) {
                matrix[i][2] = aux[i];
            }
        } else if (op == 'B') {
            for (int i = 0; i < 7; i++) {
                if (i == 6) {
                    aux[i] = matrix[0][4];
                } else {
                    aux[i] = matrix[i + 1][4];
                }
            }
            for (int i = 0; i < 7; i++) {
                matrix[i][4] = aux[i];
            }
        } else if (op == 'C') {
            for (int i = 6; i > -1; i--) {

                if (i == 0) {
                    aux[i] = matrix[2][6];
                } else {
                    aux[i] = matrix[2][i - 1];
                }
            }
            for (int i = 6; i > -1; i--) {
                matrix[2][i] = aux[i];
            }
        } else if (op == 'D') {
            for (int i = 6; i > -1; i--) {
                if (i == 0) {
                    aux[i] = matrix[4][6];
                } else {
                    aux[i] = matrix[4][i - 1];
                }
            }
            for (int i = 6; i > -1; i--) {
                matrix[4][i] = aux[i];
            }
        } else if (op == 'E') {
            for (int i = 6; i > -1; i--) {
                if (i == 0) {
                    aux[i] = matrix[6][4];
                } else {
                    aux[i] = matrix[i - 1][4];
                }
            }
            for (int i = 6; i > -1; i--) {
                matrix[i][4] = aux[i];
            }
        } else if (op == 'F') {
            for (int i = 6; i > -1; i--) {
                if (i == 0) {
                    aux[i] = matrix[6][2];
                } else {
                    aux[i] = matrix[i - 1][2];
                }
            }
            for (int i = 6; i > -1; i--) {
                matrix[i][2] = aux[i];
            }
        } else if (op == 'G') {
            for (int i = 0; i < 7; i++) {
                if (i == 6) {
                    aux[i] = matrix[4][0];
                } else {
                    aux[i] = matrix[4][i + 1];
                }
            }
            for (int i = 0; i < 7; i++) {
                matrix[4][i] = aux[i];
            }

        } else { //op == 'H'
            for (int i = 0; i < 7; i++) {
                if (i == 6) {
                    aux[i] = matrix[2][0];
                } else {
                    aux[i] = matrix[2][i + 1];
                }
            }
            for (int i = 0; i < 7; i++) {
                matrix[2][i] = aux[i];
            }
        }

        return matrixToConfig(matrix);
    }

    @Override
    public int getHeuristicCost(String config, int modeFlag) {
        // TODO Add your code here
        int h;
        int blockValues = getBlockValues(config, modeFlag);

        switch (modeFlag) {
            case 1:
                /*Heuristic Given*/
                h = 8 - blockValues;
                break;
            case 3:
                /*Heuristic Created*/
                h = 3 - (blockValues /(int) 8);
                break;
            default:
                h = 0;
        }
        return h;
    }

    /*Return the biggest value of numbers in the central block*/
    public int getBlockValues(String config, int modeFlag) {
        int ret = 0;
        int sum1 = 0;
        int sum2 = 0;
        int sum3 = 0;
        int[] block = getBlock(config);

        if (modeFlag == 1) {
            /*Calculate value for heuristic given*/
            for (int i = 0; i < block.length; i++) {
                if (block[i] == 1) {
                    sum1++;
                } else if (block[i] == 2) {
                    sum2++;
                } else {
                    sum3++;
                }
            }

            ret = sum1;
            if (sum2 > sum1 && sum2 > sum3) {
                ret = sum2;
            } else if (sum3 > sum1 && sum3 > sum2) {
                ret = sum3;
            }
        }
        else{
            /*Calculate value for heuristic created*/
            boolean equals = true;
            for (int i = 0; i < block.length; i++) {
                ret = ret+block[i];
                /*If the values in the block are not equal, itś not a goal*/
                if(block[i]!=block[0]){
                    equals = false;
                }
            }
            /*If is goal, then return 24 to the H value be 0*/
            if(equals){
                ret = 24;
            }
        }

        return ret;

    }

    /*Get central block of numbers*/
    public int[] getBlock(String config) {
        int[] block = new int[8];

        block[0] = config.charAt(6) - '0';
        block[1] = config.charAt(7) - '0';
        block[2] = config.charAt(8) - '0';
        block[3] = config.charAt(11) - '0';
        block[4] = config.charAt(12) - '0';
        block[5] = config.charAt(15) - '0';
        block[6] = config.charAt(16) - '0';
        block[7] = config.charAt(17) - '0';

        return block;
    }

    /*Generate a matrix to do the movements by the String config*/
    public char[][] configToMatrix(String config) {
        int n = 4;
        char matrix[][] = new char[7][7];

        matrix[0][2] = config.charAt(0);
        matrix[0][4] = config.charAt(1);
        matrix[1][2] = config.charAt(2);
        matrix[1][4] = config.charAt(3);

        matrix[3][2] = config.charAt(11);
        matrix[3][4] = config.charAt(12);

        matrix[5][2] = config.charAt(20);
        matrix[5][4] = config.charAt(21);
        matrix[6][2] = config.charAt(22);
        matrix[6][4] = config.charAt(23);

        for (int i = 0; i < 7; i++) {
            matrix[2][i] = config.charAt(n);
            matrix[4][i] = config.charAt(n + 9);
            n++;
        }

        return matrix;
    }

    /*Go back from the matrix to the String config*/
    private String matrixToConfig(char[][] matrix) {

        String config = "" + matrix[0][2] + matrix[0][4] + matrix[1][2] + matrix[1][4];

        for (int i = 0; i < 7; i++) {
            config = config + matrix[2][i];
        }
        config = config + matrix[3][2] + matrix[3][4];

        for (int i = 0; i < 7; i++) {
            config = config + matrix[4][i];
        }

        config = config + matrix[5][2] + matrix[5][4] + matrix[6][2] + matrix[6][4];

        return config;
    }

}

