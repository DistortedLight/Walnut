/*   Copyright 2016 Hamoon Mousavi
 *
 *   This file is part of Walnut.
 *
 *   Walnut is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Walnut is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Walnut.  If not, see <http://www.gnu.org/licenses/>.
*/

package Automata;
import Main.GraphViz;
import Main.UtilityMethods;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.TreeMap;

import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

/**
 * This class can represent different types of automaton: deterministic/non-deterministic and/or automata with output/automata without output.<bf>
 * There are also two special automata: true automaton, which accepts everything, and false automaton, which accepts nothing.
 * To represent true/false automata we use the field members: TRUE_FALSE_AUTOMATON and TRUE_AUTOMATA. <br>
 * Let's forget about special true/false automata, and talk about ordinary automata:
 * Inputs to an ordinary automaton are n-tuples. Each coordinate has its own alphabet. <br>
 * Let's see this by means of an example: <br>
 * Suppose our automaton has 3-tuples as its input: <br>
 * -The field A, which is a list of list of integers, is used to store the alphabets of
 * all these three inputs. For example we might have A = [[1,2],[0,-1,1],[1,3]]. This means that the first input is over alphabet
 * {1,2} and the second one is over {0,-1,1},... <br>
 * Note: Input alphabets are subsets of integers. <br>
 * -Each coordinate also has a type which is stored in T. For example we might have T = [Type.arithmetic,Type.arithmetic,Type.arithmetic],
 * which means all three inputs are of type arithmetic. You can find out what this means in the tutorial.
 * -So in total there are 12 = 2*3*2 different inputs (this number is stored in alphabetSize)
 * for this automaton. Here are two example inputs: (2,-1,3),(1,0,3).
 * We can encode these 3-tuples by the following rule:<br>
 * 0 = (1,0,1) <br>
 * 1 = (2,0,1) <br>
 * 2 = (1,-1,1)<br>
 * ...<br>
 * 11 = (2,1,3)<br>
 * We use this encoded numbers in our representation of automaton to refer to a particular input. For example
 * we might say on (2,1,3) we go from state 5 to state 0 by setting d.get(5) = (11,[5]). We'll see more on d (transition function)
 * -Now what about states? Q stores the number of states. For example when Q = 3, the set of states is {0,1,2}.
 * -Initial state: q0 is the initial state. For example we might have q0 = 1.
 * -Now field member O is an important one: O stores the output of an state. Now in the case of DFA/NFA, a value of non-zero
 * in O means a final state, and a value of zero means a non-final state. So for example we might have O = {1,-1,0} which means
 * that the first two states are final states. In the case of an automaton with output O simply represents output of an state.
 * Continuing with this example, in the case of automaton with output,
 * the first state has output 1, the second has output -1, and the third one has output 0. As you
 * have guessed the output alphabet can be any finite subset of integers. <br>
 * We might want to give labels to inputs. For example if we set label = ["x","y","z"], the label of the first input is "x".
 * Then in future, we can refer to this first input by the label "x". <br>
 * -The transition function is d which is a TreeMap<integer,List<Integer>> for each state. For example we might have
 * d.get(1) = {(0,[0]),(1,[1,2]),...} which means that state 1 goes to state 0 on input 0, and goes to states 1 and 2 on 1,....
 *
 * @author Hamoon
 *
 */
public class Automaton {
    /**
     * When TRUE_FALSE_AUTOMATON = false, it means that this automaton is
     * an actual automaton and not one of the special automata: true or false
     * When TRUE_FALSE_AUTOMATON = true and TRUE_AUTOMATON = false then this is a false automaton.
     * When TRUE_FALSE_AUTOMATON = true and TRUE_AUTOMATON = true then this is a true automaton.
    */
    public boolean TRUE_FALSE_AUTOMATON = false;
    public boolean TRUE_AUTOMATON = false;

    /**
     *  Input Alphabet.
     *  For example when A = [[-1,1],[2,3]], the first and the second inputs are over alphabets {-1,1} and {2,3} respectively.
     *  Remember that the input to an automaton is a tuple (a pair in this example).
     *  For example a state might make a transition on input (1,3). Here the
     *  first input is 1 and the second input is 3.
     *  Also note that A is a list of sets, but for technical reasons, we just made it a list of lists. However,
     *  we have to make sure, at all times, that the inner lists of A don't contain repeated elements.
     */
    public List<List<Integer>> A;

    /**
     * Alphabet Size. For example, if A = [[-1,1],[2,3]], then alphabetSize = 4 and if A = [[-1,1],[0,1,2]], then alphabetSize = 6
     */
    public int alphabetSize;

    /**
     * This vector is useful in the encode method.
     * When A = [l1,l2,l3,...,ln] then
     * encoder = [1,|l1|,|l1|*|l2|,...,|l1|*|l2|*...*|ln-1|].
     * It is useful, as mentioned earlier, in the encode method. encode method gets a list x, which represents a viable
     * input to this automaton, and returns a non-negative integer, which is the integer represented by x, in base encoder.
     * Note that encoder is a mixed-radix base. We use the encoded integer, returned by encode(), to store transitions.
     * So we don't store the list x.
     * We can decode, the number returned by encode(), and get x back using decode method.
     */
    public List<Integer> encoder;

    /**
     * Types of the inputs to this automaton.
     * There are two possible types for inputs for an automaton:Type.arithmetic or Type.alphabetLetter.
     * In other words, type of inputs to an automaton is either arithmetic or non-arithmetic.
     * For example we might have A = [[1,-1],[0,1,2],[0,-1]] and T = [Type.alphabetLetter, Type.arithmetic, Type.alphabetLetter]. So
     * the first and third inputs are non-arithmetic (and should not be treated as arithmetic).
     * This type is useful in type checking. So for example, we might have f(a,b+1,c+1), where f is the example automaton. Then this
     * is a type error, because the third input to f is non-arithmetic, and hence we cannot have c+1 as our third argument.
     * It is very important to note that, an input of type arithmetic must always contain 0 and 1 in its alphabet.
     */
    public List<NumberSystem> NS;

    /**Number of States. For example when Q = 3, the set of states is {0,1,2}*/
    public int Q;

    /**Initial State.*/
    public int q0;

    /**State Outputs. In case of DFA/NFA accepting states have a nonzero integer as their output.
     * Rejecting states have output 0.
     * Example: O = [-1,2,...] then state 0 and 1 have outputs -1 and 2 respectively.*/
    public List<Integer> O;

    /**We would like to give label to inputs. For example we might want to call the first input by a and so on.
     * As an example when label = ["a","b","c"], the label of the first, second, and third inputs are a, b, and c respectively.
     * These labels are then useful when we quantify an automaton. So for example, in a predicate like E a f(a,b,c) we are having an automaton
     * of three inputs, where the first, second, and third inputs are labeled "a","b", and "c". Therefore E a f(a,b,c) says, we want to
     * do an existential quantifier on the first input.
     * */
    public List<String> label;

    /** When true, states are sorted in breadth-first order and labels are sorted lexicographically.
     *  It is used in canonize method. For more information read about canonize() method.
     * */
    public boolean canonized;

    /** When true, labels are sorted lexicographically. It is used in sortLabel() method.*/
    public boolean labelSorted;

    /**
     * Transition Function for This State. For example, when d[0] = [(0,[1]),(1,[2,3]),(2,[2]),(3,[4]),(4,[1]),(5,[0])]
     * and alphabet A = [[0,1],[-1,2,3]]
     * then from the state 0 on
     * (0,-1) we go to 1
     * (0,2) we go to 2,3
     * (0,3) we go to 2
     * (1,-1) we go to 4
     * ...
     * So we store the encoded values of inputs in d, i.e., instead of saying on (0,-1) we go to state 1, we say on 0, we go
     * to state 1.
     * Recall that (0,-1) represents 0 in mixed-radix base (1,2) and alphabet A. We have this mixed-radix base (1,2) stored as encoder in
     * our program, so for more information on how we compute it read the information on List<Integer> encoder field.
     */
    public List<TreeMap<Integer,List<Integer>>> d;

    /**
     * Valmari fields
     */
    // blocks (consist of states)
    Partition B;
    // cords (consist of transitions)
    Partition C;

    // number of states
    public int num_states;
    // number of transitions
    public int num_transitions;
    // number of final states
    public int num_finalstates;

    // tails of transitions
    Integer[] T;
    // labels of transitions
    Integer[] L;
    // heads of transitions
    Integer[] H;

    /* Adjacent transitions */
    int[] _A, _F;

    // for use in the combine command, counts how many products we have taken so far, and hence what to set outputs to
    public int combineIndex;

    void make_adjacent(Integer K[]) {
        int q, t;
        for( q = 0; q <= num_states; ++q ) {
            _F[q] = 0;
        }

        for( t = 0; t < num_transitions; ++t ) {
            ++_F[K[t]];
        }

        for( q = 0; q < num_states; ++q ) {
            _F[q+1] += _F[q];
        }

        for( t = num_transitions; t-- != 0; ) {
            _A[--_F[K[t]]] = t;
        }
    }

    /* Removal of irrelevant parts */
    int rr = 0;   // number of reached states

    void reach( int q ) {
      int i = B.L[q];
      if( i >= rr ){
        B.E[i] = B.E[rr]; B.L[B.E[i]] = i;
        B.E[rr] = q; B.L[q] = rr++; }
    }

    void rem_unreachable( Integer T[], Integer H[] ){
        make_adjacent( T ); int i, j;
        for( i = 0; i < rr; ++i ){
            for( j = _F[B.E[i]]; j < _F[B.E[i] + 1]; ++j ){
                reach( H[_A[j]] );
            }
        }
        j = 0;
        for( int t = 0; t < num_transitions; ++t ){
            if( B.L[T[t]] < rr ){
                H[j] = H[t]; L[j] = L[t];
                T[j] = T[t]; ++j;
            }
        }
        num_transitions = j; B.P[0] = rr; rr = 0;
    }

    /* Minimization algorithm */
    void minimize_valmari(boolean print, String prefix,StringBuffer log) throws Exception{
        HashSet<Integer> qqq = new HashSet<Integer>();
        qqq.add(q0);
        subsetConstruction(qqq,print,prefix,log);
        num_states = Q;
        num_transitions = 0;
        B = new Partition();
        C = new Partition();
        ArrayList<Integer> _H = new ArrayList<Integer>(),_L = new ArrayList<Integer>(),_T= new ArrayList<Integer>();
        //System.out.println("-------------------------------------------");
        for(int q = 0; q != d.size();++q){
            for(int l : d.get(q).keySet()) {
                for(int p : d.get(q).get(l)) {
                    num_transitions++;
                    _H.add(p);
                    _T.add(q);
                    _L.add(l);
                    //System.out.println(q + " -> " + p + " - " + l);
                }
            }
        }
        //System.out.println("-------------------------------------------");
        T = new Integer[num_transitions];
        L = new Integer[num_transitions];
        H = new Integer[num_transitions];
        _T.toArray(T); _L.toArray(L);_H.toArray(H);
        B.init( num_states );
        _A = new int[ num_transitions ]; _F = new int[ num_states+1 ];

          //reach( q0 ); rem_unreachable( T, H );
        for( int q = 0; q < num_states; ++q ){
            if(O.get(q) != 0){
                reach( q );
            }
        }
        num_finalstates = rr; rem_unreachable( H, T );

        /* Make initial partition */
        Partition.W = new int[ num_transitions+1 ]; Partition.M = new int[ num_transitions+1];
        Partition.M[0] = num_finalstates;
        if( num_finalstates != 0 ){ Partition.W[Partition.w++] = 0; B.split(); }

        /* Make transition partition */
        C.init( num_transitions );
        if( num_transitions != 0 ){
            Arrays.sort(C.E, new Comparator<Integer>() {
                @Override
                public int compare(Integer a, Integer b)
                {

                    return L[a] - L[b];
                }
            });
            C.z = Partition.M[0] = 0; int a = L[C.E[0]];
            for( int i = 0; i < num_transitions; ++i ){
                int t = C.E[i];
                if( L[t] != a ){
                    a = L[t]; C.P[C.z++] = i;
                    C.F[C.z] = i; Partition.M[C.z] = 0;
                }
                C.S[t] = C.z; C.L[t] = i;
            }
            C.P[C.z++] = num_transitions;
        }

        /* Split blocks and cords */
        make_adjacent( H );
        int b = 1, c = 0;
        while( c < C.z ){
            for(int i = C.F[c]; i < C.P[c]; ++i ){
                B.mark( T[C.E[i]] );
            }
            B.split(); ++c;
            while( b < B.z ){
                for(int i = B.F[b]; i < B.P[b]; ++i ){
                    for(int j = _F[B.E[i]];j < _F[B.E[i]+1]; ++j){
                        C.mark( _A[j] );
                    }
                }
                C.split(); ++b;
            }
        }

        /* Turn the result back to Walnut format for Automata */
        Q = B.z;
        q0 = B.S[q0];
        O = new ArrayList<Integer>(Q);
        for( int q = 0; q < B.z; ++q ){
            if( B.F[q] < num_finalstates ){
                O.add(1);
            }
            else {
                O.add(0);
            }
        }

        d = new ArrayList<TreeMap<Integer,List<Integer>>>(Q);
        for( int q = 0; q < Q;++q){
            d.add(new TreeMap<Integer,List<Integer>>());
        }
        for( int t = 0; t < num_transitions; ++t ){
            if( B.L[T[t]] == B.F[B.S[T[t]]] ){
                int q = B.S[T[t]];
                int l = L[t];
                int p = B.S[H[t]];
                if(!d.get(q).containsKey(l)){
                    d.get(q).put(l, new ArrayList<Integer>());
                }
                d.get(q).get(l).add(p);
            }
        }
        canonized = false;
    }

    /**
     * Default constructor. It just initializes the field members.
     */
    public List<String> getLabel() {
        return label;
    }

    public Automaton() {
        TRUE_FALSE_AUTOMATON = false;
        A = new ArrayList<List<Integer>>();
        NS = new ArrayList<NumberSystem>();
        encoder = null;
        O = new ArrayList<Integer>();
        d = new ArrayList<TreeMap<Integer,List<Integer>>>();
        label = new ArrayList<String>();
        canonized = false;
        labelSorted = false;
        dk.brics.automaton.Automaton.setMinimization(dk.brics.automaton.Automaton.MINIMIZE_HOPCROFT);
    }

    /**
     * Initializes a special automaton: true or false.
     * A true automaton, is an automaton that accepts everything. A false automaton is an automaton that accepts nothing.
     * Therefore, M and false is false for every automaton M. We also have that M or true is true for every automaton M.
     * @param true_automaton
     */
    public Automaton(boolean true_automaton){
        TRUE_FALSE_AUTOMATON = true;
        this.TRUE_AUTOMATON = true_automaton;
    }

    /**
     * Takes a regular expression and the alphabet for that regular expression and constructs the corresponding automaton.
     * For example if the regularExpression = "01*" and alphabet = [0,1,2], then the resulting automaton accepts
     * words of the form 01* over the alphabet {0,1,2}.<br>
     *
     * We actually compute the automaton for regularExpression intersected with alphabet*.
     * So for example if regularExpression = [^4]* and alphabet is [1,2,4], then the resulting
     * automaton accepts (1|2)*<br>
     *
     * An important thing to note here is that the automaton being constructed
     * with this constructor, has only one input, and it is of type Type.alphabetLetter.
     * @param address
     * @throws Exception
     */
    public Automaton(String regularExpression, List <Integer> alphabet) throws Exception {
        this();
        if(alphabet == null || alphabet.size()== 0)throw new Exception("empty alphabet is not accepted");
        long timeBefore = System.currentTimeMillis();
        alphabet = new ArrayList<Integer>(alphabet);
        NS.add(null);
        UtilityMethods.removeDuplicates(alphabet);
        /**
         * For example if alphabet = {2,4,1} then intersectingRegExp = [241]*
         */
        String intersectingRegExp = "[";
        for(int x:alphabet){
            if(x < 0 || x>9){
                throw new Exception("the input alphabet of an automaton generated from a regular expression must be a subset of {0,1,...,9}");
            }
            intersectingRegExp += x;
        }
        intersectingRegExp += "]*";
        regularExpression = "("+regularExpression+")&"+intersectingRegExp;
        dk.brics.automaton.RegExp RE = new RegExp(regularExpression);
        dk.brics.automaton.Automaton M = RE.toAutomaton();
        M.minimize();
        /**
         * Recall that the alphabet is a set and does not allow repeated elements. However, the user might enter the command
         * reg myreg {1,1,0,0,0} "10*"; and therefore alphabet = [1,1,0,0,0]. So we need remove duplicates before we
         * move forward.
         */
        A.add(alphabet);
        alphabetSize = alphabet.size();
        NS.add(null);
        List<State> setOfStates = new ArrayList<State>(M.getStates());
        Q = setOfStates.size();
        q0 = setOfStates.indexOf(M.getInitialState());
        for(int q = 0 ; q < Q;q++){
            State state = setOfStates.get(q);
            if(state.isAccept())O.add(1);
            else O.add(0);
            TreeMap<Integer,List<Integer>> currentStatesTransitions = new TreeMap<Integer, List<Integer>>();
            d.add(currentStatesTransitions);
            for(Transition t: state.getTransitions()){
                for(char a = UtilityMethods.max(t.getMin(),'0');a <= UtilityMethods.min(t.getMax(),'9');a++){
                    if(alphabet.contains((int)(a-'0'))){
                        List<Integer> dest = new ArrayList<Integer>();
                        dest.add(setOfStates.indexOf(t.getDest()));
                        currentStatesTransitions.put(alphabet.indexOf((int)(a-'0')), dest);
                    }
                }
            }
        }
        long timeAfter = System.currentTimeMillis();
        String msg = "computed ~:" + Q + " states - "+(timeAfter-timeBefore)+"ms";
        System.out.println(msg);
    }

    public Automaton(
        String regularExpression,
        List<Integer> alphabet,
        NumberSystem numSys) throws Exception {
        this(regularExpression,alphabet);
        NS.set(0,numSys);
    }

    // This handles the generalised case of vectors such as "[0,1]*[0,0][0,1]"
    public Automaton(String regularExpression, List<List<Integer>> alphabet, Integer alphabetSize) throws Exception {

        this();
        if(alphabetSize > ((1<<Character.SIZE) -1)){
            throw new Exception("size of input alphabet exceeds the limit of " + ((1<<Character.SIZE) -1));
        }
        long timeBefore = System.currentTimeMillis();
        String intersectingRegExp = "[";
        for(int x=0; x<alphabetSize; x++){
            char nextChar = (char)(128 + x);
            intersectingRegExp += nextChar;
        }
        intersectingRegExp += "]*";
        regularExpression = "("+regularExpression+")&"+intersectingRegExp;
        dk.brics.automaton.RegExp RE = new RegExp(regularExpression);
        dk.brics.automaton.Automaton M = RE.toAutomaton();
        M.minimize();
        this.setThisAutomatonToRepresent(M);
        // We added 128 to the encoding of every input vector before to avoid reserved characters, now we subtract it again
        // to get back the standard encoding
        List<TreeMap<Integer,List<Integer>>> new_d = new ArrayList<>();
        for(int q = 0; q < Q;q++)new_d.add(new TreeMap<Integer,List<Integer>>());
        for(int q = 0 ; q < Q;q++){
            for(int x:d.get(q).keySet()){
                new_d.get(q).put(x-128, d.get(q).get(x));
            }
        }
        d = new_d;
        long timeAfter = System.currentTimeMillis();
        String msg = "computed ~:" + Q + " states - "+(timeAfter-timeBefore)+"ms";
        System.out.println(msg);
    }

    /**
     * Takes an address and constructs the automaton represented by the file referred to by the address
     * @param address
     * @throws Exception
     */
    public Automaton(String address) throws Exception {
        this();
        final String REGEXP_FOR_WHITESPACE = "^\\s*$";

        //lineNumber will be used in error messages
        int lineNumber = 0;
        alphabetSize = 1;

        try {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream(address), "utf-8"));
            String line;
            boolean[] singleton = new boolean[1];
            while((line = in.readLine())!= null) {
                lineNumber++;
                if(line.matches(REGEXP_FOR_WHITESPACE)) {
                    // Ignore blank lines.
                    continue;
                } else if(ParseMethods.parseTrueFalse(line, singleton)) {
                    // It is a true/false automaton.
                    TRUE_FALSE_AUTOMATON = true;
                    TRUE_AUTOMATON = singleton[0];
                    in.close();
                    return;
                } else {
                    boolean flag = false;
                    try {
                        flag = ParseMethods.parseAlphabetDeclaration(line, A, NS);
                    } catch(Exception e){
                        in.close();
                        throw new Exception(
                            e.getMessage() + UtilityMethods.newLine() +
                            "\t:line "+ lineNumber + " of file " + address);
                    }

                    if(flag) {
                        for(int i = 0; i < A.size();i++) {
                            if(NS.get(i) != null &&
                                (!A.get(i).contains(0) || !A.get(i).contains(1))) {
                                in.close();
                                throw new Exception(
                                    "The " + (i + 1) + "th input of type arithmetic " +
                                    "of the automaton declared in file " + address +
                                    " requires 0 and 1 in its input alphabet: line " +
                                    lineNumber);
                            }
                            UtilityMethods.removeDuplicates(A.get(i));
                            alphabetSize *= A.get(i).size();
                        }

                        break;
                    } else {
                        in.close();
                        throw new Exception(
                            "Undefined statement: line " +
                            lineNumber + " of file " + address);
                    }
                }
            }

            int[] pair = new int[2];
            List<Integer> input = new ArrayList<Integer>();
            List<Integer> dest = new ArrayList<Integer>();
            int currentState = -1;
            int currentOutput;
            TreeMap<Integer,List<Integer>> currentStateTransitions = new TreeMap<>();
            TreeMap<Integer,Integer> state_output = new TreeMap<Integer,Integer>();
            TreeMap<Integer,TreeMap<Integer,List<Integer>>> state_transition =
                new TreeMap<Integer,TreeMap<Integer,List<Integer>>>();
            /**
             * This will hold all states that are destination of some transition.
             * Then we make sure all these states are declared.
             */
            Set<Integer> setOfDestinationStates = new HashSet<Integer>();
            Q = 0;
            while((line = in.readLine())!= null) {
                lineNumber++;
                if(line.matches(REGEXP_FOR_WHITESPACE)) {
                    continue;
                }

                if(ParseMethods.parseStateDeclaration(line, pair)) {
                    Q++;
                    if(currentState == -1) {
                        q0 = pair[0];
                    }

                    currentState = pair[0];
                    currentOutput = pair[1];
                    state_output.put(currentState, currentOutput);
                    currentStateTransitions = new TreeMap<>();
                    state_transition.put(currentState, currentStateTransitions);
                } else if(ParseMethods.parseTransition(line, input, dest)) {
                    setOfDestinationStates.addAll(dest);
                    if(currentState == -1){
                        in.close();
                        throw new Exception(
                            "Must declare a state before declaring a list of transitions: line " +
                            lineNumber + " of file " + address);
                    }

                    if(input.size() != A.size()) {
                        in.close();
                        throw new Exception("This automaton requires a " + A.size() +
                            "-tuple as input: line " + lineNumber + " of file " + address);
                    }
                    List<List<Integer>> inputs = expandWildcard(input);

                    for(List<Integer> i:inputs){
                        currentStateTransitions.put(encode(i), dest);
                    }

                    input = new ArrayList<Integer>();
                    dest = new ArrayList<Integer>();
                }
                else{
                    in.close();
                    throw new Exception("Undefined statement: line "+ lineNumber + " of file " + address);
                }
            }
            in.close();
            for(int q:setOfDestinationStates) {
                if(!state_output.containsKey(q)) {
                    throw new Exception(
                        "State " + q + " is used but never declared anywhere in file: " + address);
                }
            }

            for(int q = 0; q < Q; q++) {
                O.add(state_output.get(q));
                d.add(state_transition.get(q));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("File does not exist: " + address);
        }
    }

    /**
     * returns a deep copy of this automaton.
     * @return a deep copy of this automaton
     */
    public Automaton clone(){
        Automaton M;
        if(TRUE_FALSE_AUTOMATON){
            M = new Automaton(TRUE_AUTOMATON);
            return M;
        }
        M = new Automaton();
        M.Q = Q;
        M.q0 = q0;
        M.alphabetSize = alphabetSize;
        M.canonized = canonized;
        M.labelSorted = labelSorted;
        for(int i = 0 ; i < A.size();i++){
            M.A.add(new ArrayList<Integer>(A.get(i)));
            M.NS.add(NS.get(i));
            if(encoder != null && encoder.size()>0){
                if(M.encoder == null) M.encoder = new ArrayList<Integer>();
                M.encoder.add(encoder.get(i));
            }
            if(label != null && label.size() == A.size())
                M.label.add(label.get(i));
        }
        for(int q = 0;q < Q;q++){
            M.O.add(O.get(q));
            M.d.add(new TreeMap<Integer,List<Integer>>());
            for(int x:d.get(q).keySet()){
                M.d.get(q).put(x, new ArrayList<Integer>(d.get(q).get(x)));
            }
        }
        return M;
    }

    public void quantify(String labelToQuantify,boolean print, String prefix,StringBuffer log)throws Exception{
        Set<String> listOfLabelsToQuantify = new HashSet<String>();
        listOfLabelsToQuantify.add(labelToQuantify);
        quantify(listOfLabelsToQuantify,print,prefix,log);
    }

    public void quantify(String labelToQuantify1,String labelToQuantify2,boolean leadingZeros,boolean print, String prefix,StringBuffer log)throws Exception{
        Set<String> listOfLabelsToQuantify = new HashSet<String>();
        listOfLabelsToQuantify.add(labelToQuantify1);
        listOfLabelsToQuantify.add(labelToQuantify2);
        quantify(listOfLabelsToQuantify,print,prefix,log);
    }

    /**
     * This method computes the existential quantification of this automaton.
     * Takes a list of labels and performs the existential quantifier over
     * the inputs with labels in listOfLabelsToQuantify. It simply eliminates inputs in listOfLabelsToQuantify.
     * After the quantification is done, we address the issue of
     * leadingZeros or trailingZeors (depending on the value of leadingZeros), if all of the inputs
     * of the resulting automaton are of type arithmetic.
     * This is why we mandate that an input of type arithmetic must have 0 in its alphabet, also that
     * every number system must use 0 to denote its additive identity.
     * @param listOfLabelsToQuantify must contain at least one element. listOfLabelsToQuantify must be a subset of this.label.
     * @param leadingZero determines which of leadingZeros or trailingZeros should be addressed after quantification.
     * @return
     */
    public void quantify(Set<String> listOfLabelsToQuantify, boolean print, String prefix,StringBuffer log)throws Exception{
        quantifyHelper(listOfLabelsToQuantify,print,prefix,log);
        if(TRUE_FALSE_AUTOMATON)return;

        boolean isMsd = true;
        boolean flag = false;
        for(NumberSystem ns:NS){
            if(ns == null)
                return;
            if(flag && (ns.isMsd() != isMsd) )
                return;
            isMsd = ns.isMsd();
            flag = true;
        }
        if(isMsd)
            fixLeadingZerosProblem(print,prefix,log);
        else
            fixTrailingZerosProblem(print,prefix,log);
    }

    /**
     * This method is very similar to public void quantify(Set<String> listOfLabelsToQuantify,boolean leadingZeros)throws Exception
     * with the exception that, this method does not deal with leading/trailing zeros problem.
     * @param listOfLabelsToQuantify
     * @throws Exception
     */
    private void quantifyHelper(
        Set<String> listOfLabelsToQuantify,
        boolean print,
        String prefix,
        StringBuffer log) throws Exception {
        if(listOfLabelsToQuantify.isEmpty() || label == null) {
            return;
        }

        // throw new Exception("quantification requires a non empty list of qunatified variables");
        String name_of_labels = "";
        for(String s:listOfLabelsToQuantify) {
            if(!label.contains(s)) {
                throw new Exception(
                    "Variable " + s + " in the list of quantified variables is not a free variable.");
            }

            if(name_of_labels.length() == 0) {
                name_of_labels += s;
            } else {
                name_of_labels += "," + s;
            }
        }
        long timeBefore = System.currentTimeMillis();
        if(print) {
            String msg = prefix + "quantifying:" + Q + " states";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }

        /**
         * If this is the case, then the quantified automaton is either the true or false automaton.
         * It is true if this's language is not empty.
         */
        if(listOfLabelsToQuantify.size() == A.size()){
            if(isEmpty())
                TRUE_AUTOMATON = false;
            else
                TRUE_AUTOMATON = true;
            TRUE_FALSE_AUTOMATON = true;
            clear();
            return;
        }

        List<Integer> listOfInputsToQuantify = new ArrayList<Integer>();//extract the list of indices of inputs we would like to quantify
        for(String l:listOfLabelsToQuantify)
            listOfInputsToQuantify.add(label.indexOf(l));
        List<List<Integer>> allInputs = new ArrayList<List<Integer>>();
        for(int i = 0; i < alphabetSize;i++)
            allInputs.add(decode(i));
        //now we remove those indices in listOfInputsToQuantify from A,T,label, and allInputs
        UtilityMethods.removeIndices(A,listOfInputsToQuantify);
        encoder = null;
        alphabetSize = 1;
        for(List<Integer> x:A)
            alphabetSize*=x.size();
        UtilityMethods.removeIndices(NS,listOfInputsToQuantify);
        UtilityMethods.removeIndices(label,listOfInputsToQuantify);
        for(List<Integer> i:allInputs)
            UtilityMethods.removeIndices(i,listOfInputsToQuantify);
        //example: permutation[1] = 7 means that encoded old input 1 becomes encoded new input 7
        List<Integer> permutation = new ArrayList<Integer>();
        for(List<Integer> i:allInputs)
            permutation.add(encode(i));
        List<TreeMap<Integer,List<Integer>>> new_d = new ArrayList<TreeMap<Integer,List<Integer>>>();
        for(int q = 0; q < Q;q++){
            TreeMap<Integer,List<Integer>> newTransitionFunction = new TreeMap<Integer,List<Integer>>();
            new_d.add(newTransitionFunction);
            for(int x:d.get(q).keySet()){
                int y = permutation.get(x);
                if(newTransitionFunction.containsKey(y))
                    UtilityMethods.addAllWithoutRepetition(newTransitionFunction.get(y),d.get(q).get(x));
                else
                    newTransitionFunction.put(y,new ArrayList<Integer>(d.get(q).get(x)));
            }
        }
        d = new_d;
        minimize(print,prefix +" ",log);
        long timeAfter = System.currentTimeMillis();
        if(print){
            String msg = prefix + "quantified:" + Q + " states - "+(timeAfter-timeBefore)+"ms";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
    }

    /**
     * this automaton should not be a word automaton (automaton with output). However, it can be non deterministic.
     * @return the reverse of this automaton
     * @throws Exception
     */
    public void reverse(boolean print, String prefix, StringBuffer log) throws Exception {
        if(TRUE_FALSE_AUTOMATON)return;
        long timeBefore = System.currentTimeMillis();
        if(print) {
            String msg = prefix + "reversing:" + Q + " states";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }

        // We change the direction of transitions first.
        List<TreeMap<Integer,List<Integer>>> new_d = new ArrayList<>();
        for(int q = 0; q < Q;q++)new_d.add(new TreeMap<Integer,List<Integer>>());
        for(int q = 0 ; q < Q;q++){
            for(int x:d.get(q).keySet()){
                for(int dest:d.get(q).get(x)){
                    if(new_d.get(dest).containsKey(x))
                        new_d.get(dest).get(x).add(q);
                    else{
                        List<Integer> destinationSet = new ArrayList<Integer>();
                        destinationSet.add(q);
                        new_d.get(dest).put(x, destinationSet);
                    }
                }
            }
        }
        d = new_d;
        HashSet<Integer> setOfFinalStates = new HashSet<Integer>();
        /**final states become non final*/
        for(int q = 0 ; q < Q;q++){
            if(O.get(q) != 0){
                setOfFinalStates.add(q);
                O.set(q, 0);
            }
        }
        O.set(q0, 1);/**initial state becomes the final state.*/

        subsetConstruction(setOfFinalStates,print,prefix+" ",log);

        minimize(print,prefix+" ",log);

        long timeAfter = System.currentTimeMillis();
        if(print){
            String msg = prefix + "reversed:" + Q + " states - "+(timeAfter-timeBefore)+"ms";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
    }

    /**
     * This method is used in and, or, not, and many others.
     * This automaton and M should have TRUE_FALSE_AUTOMATON = false.
     * Both this automaton and M must have labeled inputs.
     * For the sake of an example, suppose that Q = 3, q0 = 1, M.Q = 2, and M.q0 = 0. Then N.Q = 6 and the states of N
     * are {0=(0,0),1=(0,1),2=(1,0),3=(1,1),4=(2,0),5=(2,1)} and N.q0 = 2. The transitions of state (a,b) is then
     * based on the transitions of a and b in this and M.
     * To continue with this example suppose that label = ["i","j"] and
     * M.label = ["p","q","j"]. Then N.label = ["i","j","p","q"], and inputs to N are four tuples.
     * Now suppose in this we go from 0 to 1 by reading (i=1,j=2)
     * and in M we go from 1 to 0 by reading (p=-1,q=-2,j=2).
     * Then in N we go from (0,1) to (1,0) by reading (i=1,j=2,p=-1,q=-2).
     * @param M
     * @return this automaton cross product M.

     */
    private Automaton crossProduct(
        Automaton M,
        String op,
        boolean print,
        String prefix,
        StringBuffer log) throws Exception{
        if(this.TRUE_FALSE_AUTOMATON || M.TRUE_FALSE_AUTOMATON)
            throw new Exception("Invalid use of the crossProduct method: " +
                "the automata for this method cannot be true or false automata.");
        if(this.label == null ||
            M.label == null ||
            this.label.size() != A.size() ||
            M.label.size() != M.A.size())
            throw new Exception("Invalid use of the crossProduct method: " +
                "the automata for this method must have labeled inputs.");
        /**N is going to hold the cross product*/
        Automaton N = new Automaton();

        long timeBefore = System.currentTimeMillis();
        if(print){
            String msg = prefix + "computing cross product:" + Q + " states - " + M.Q + " states";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }

        /**
         * for example when sameLabelsInMAndThis[2] = 3, then input 2 of M has the same label as input 3 of this
         * and when sameLabelsInMAndThis[2] = -1, it means that input 2 of M is not an input of this
         */
        int[] sameInputsInMAndThis = new int[M.A.size()];
        for(int i = 0 ; i < M.label.size();i++){
            sameInputsInMAndThis[i] = -1;
            if(label.contains(M.label.get(i))){
                int j = label.indexOf(M.label.get(i));
                if(!UtilityMethods.areEqual(A.get(j),M.A.get(i))){
                    throw new Exception("in computing cross product of two automaton, variables with the same label must have the same alphabet");
                }
                /*if(M.NS.get(i) != NS.get(j)){
                    System.out.println(M.NS.get(i) + " "+ NS.get(j));
                    throw new Exception("in computing cross product of two automaton, variables with the same label must be of the same type");
                }*/
                sameInputsInMAndThis[i] = j;
            }
        }
        for(int i = 0 ; i < A.size();i++){
            N.A.add(A.get(i));
            N.label.add(label.get(i));
            N.NS.add(NS.get(i));
        }
        for(int i = 0 ; i < M.A.size();i++){
            if(sameInputsInMAndThis[i] == -1){
                N.A.add(new ArrayList<Integer>(M.A.get(i)));
                N.label.add(M.label.get(i));
                N.NS.add(M.NS.get(i));
            }
            else{
                int j = sameInputsInMAndThis[i];
                if(M.NS.get(i) != null && N.NS.get(j) == null)
                    N.NS.set(j, M.NS.get(i));
            }
        }
        N.alphabetSize = 1;
        for(List<Integer> i:N.A)
            N.alphabetSize *= i.size();
        List<Integer> allInputsOfN = new ArrayList<Integer>();
        for(int i = 0 ; i < alphabetSize;i++){
            for(int j = 0 ; j < M.alphabetSize;j++){
                List<Integer> inputForN = joinTwoInputsForCrossProduct(decode(i),M.decode(j),sameInputsInMAndThis);
                if(inputForN == null)
                    allInputsOfN.add(-1);
                else
                    allInputsOfN.add(N.encode(inputForN));
            }
        }
        ArrayList<Integer> statesList = new ArrayList<Integer>();
        Hashtable<Integer,Integer> statesHash = new Hashtable<Integer,Integer>();
        N.q0 = 0;
        statesList.add(q0*M.Q + M.q0);
        statesHash.put(q0*M.Q + M.q0,0);
        int currentState = 0;
        while(currentState<statesList.size()){
            int s = statesList.get(currentState);
            int p = s/M.Q;
            int q = s%M.Q;
            TreeMap<Integer,List<Integer>> thisStatesTransitions = new TreeMap<Integer,List<Integer>>();
            N.d.add(thisStatesTransitions);
            switch(op){
            case "&":
                N.O.add((O.get(p) != 0 && M.O.get(q) != 0) ? 1 : 0);
                break;
            case "|":
                N.O.add((O.get(p) != 0 || M.O.get(q) != 0) ? 1 : 0);
                break;
            case "^":
                N.O.add(((O.get(p) != 0 && M.O.get(q) == 0)||(O.get(p) == 0 && M.O.get(q) != 0)) ? 1 : 0);
                break;
            case "=>":
                N.O.add((O.get(p) == 0 || M.O.get(q) != 0) ? 1 : 0);
                break;
            case "<=>":
                N.O.add(((O.get(p) == 0 && M.O.get(q) == 0) || (O.get(p) != 0 && M.O.get(q) != 0)) ? 1 : 0);
                break;
            case "<":
                N.O.add((O.get(p) < M.O.get(q)) ? 1 : 0);
                break;
            case ">":
                N.O.add((O.get(p) > M.O.get(q)) ? 1 : 0);
                break;
            case "=":
                N.O.add((O.get(p) == M.O.get(q)) ? 1 : 0);
                break;
            case "!=":
                N.O.add((O.get(p) != M.O.get(q)) ? 1 : 0);
                break;
            case "<=":
                N.O.add((O.get(p) <= M.O.get(q)) ? 1 : 0);
                break;
            case ">=":
                N.O.add((O.get(p) >= M.O.get(q)) ? 1 : 0);
                break;
            case "combine":
                N.O.add((M.O.get(q) == 1) ? (combineIndex + 1) : O.get(p));
            }

            for(int x:d.get(p).keySet()){
                for(int y:M.d.get(q).keySet()){
                    int z = allInputsOfN.get(x*M.alphabetSize+y);
                    if(z != -1){
                        List<Integer> dest = new ArrayList<Integer>();
                        thisStatesTransitions.put(z, dest);
                        for(int dest1:d.get(p).get(x)){
                            for(int dest2:M.d.get(q).get(y)){
                                int dest3 = dest1*M.Q+dest2;
                                if(!statesHash.containsKey(dest3)){
                                    statesList.add(dest3);
                                    statesHash.put(dest3, statesList.size()-1);
                                }
                                dest.add(statesHash.get(dest3));
                            }
                        }
                    }
                }
            }
            currentState++;
        }
        N.Q = statesList.size();
        long timeAfter = System.currentTimeMillis();
        if(print){
            String msg = prefix + "computed cross product:" + N.Q + " states - "+(timeAfter-timeBefore)+"ms";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
        return N;
    }

    /**
     * @param M
     * @return this automaton and M.
     * @throws Exception

     */
    public Automaton and(
        Automaton M,
        boolean print,
        String prefix,
        StringBuffer log) throws Exception {
        if((TRUE_FALSE_AUTOMATON && TRUE_AUTOMATON) &&
            (M.TRUE_FALSE_AUTOMATON && M.TRUE_AUTOMATON)) {
            return new Automaton(true);
        }

        if((TRUE_FALSE_AUTOMATON && !TRUE_AUTOMATON) ||
            (M.TRUE_FALSE_AUTOMATON && !M.TRUE_AUTOMATON)) {
            return new Automaton(false);
        }

        if(TRUE_FALSE_AUTOMATON && TRUE_AUTOMATON) {
            return M;
        }

        if(M.TRUE_FALSE_AUTOMATON && M.TRUE_AUTOMATON) {
            return this;
        }

        long timeBefore = System.currentTimeMillis();
        if(print) {
            String msg = prefix + "computing &:" + Q + " states - " + M.Q + " states";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }

        Automaton N = crossProduct(M,"&",print,prefix,log);
        N.minimize(print,prefix+" ",log);

        long timeAfter = System.currentTimeMillis();
        if(print){
            String msg = prefix + "computed &:" + N.Q + " states - "+(timeAfter-timeBefore)+"ms";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }

        return N;
    }

    /**
     * @param M
     * @return  this automaton or M
     * @throws Exception
     */
    public Automaton or(Automaton M, boolean print, String prefix, StringBuffer log) throws Exception{
        if((TRUE_FALSE_AUTOMATON && TRUE_AUTOMATON) || (M.TRUE_FALSE_AUTOMATON && M.TRUE_AUTOMATON)) return new Automaton(true);
        if((TRUE_FALSE_AUTOMATON && !TRUE_AUTOMATON) && (M.TRUE_FALSE_AUTOMATON && !M.TRUE_AUTOMATON)) return new Automaton(false);

        if(TRUE_FALSE_AUTOMATON && !TRUE_AUTOMATON)return M;
        if(M.TRUE_FALSE_AUTOMATON && !M.TRUE_AUTOMATON)return this;

        long timeBefore = System.currentTimeMillis();
        if(print){
            String msg = prefix + "computing |:" + Q + " states - " + M.Q + " states";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }

        totalize(print,prefix+" ",log);
        M.totalize(print,prefix+" ",log);
        Automaton N = crossProduct(M,"|",print,prefix,log);

        N.minimize(print,prefix +" ",log);
        N.applyAllRepresentations();

        long timeAfter = System.currentTimeMillis();
        if(print){
            String msg = prefix + "computed |:" + N.Q + " states - "+(timeAfter-timeBefore)+"ms";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }

        return N;
    }

    /**
     *
     * @param M
     * @return this automaton xor M
     * @throws Exception
     */
    public Automaton xor(Automaton M, boolean print, String prefix, StringBuffer log) throws Exception{
        if((TRUE_FALSE_AUTOMATON && TRUE_AUTOMATON) && (M.TRUE_FALSE_AUTOMATON && !M.TRUE_AUTOMATON)) return new Automaton(true);
        if((TRUE_FALSE_AUTOMATON && !TRUE_AUTOMATON) && (M.TRUE_FALSE_AUTOMATON && M.TRUE_AUTOMATON)) return new Automaton(true);
        if((TRUE_FALSE_AUTOMATON && TRUE_AUTOMATON) && (M.TRUE_FALSE_AUTOMATON && M.TRUE_AUTOMATON)) return new Automaton(false);
        if((TRUE_FALSE_AUTOMATON && !TRUE_AUTOMATON) && (M.TRUE_FALSE_AUTOMATON && !M.TRUE_AUTOMATON)) return new Automaton(false);

        if(TRUE_FALSE_AUTOMATON && !TRUE_AUTOMATON)return M;
        if(M.TRUE_FALSE_AUTOMATON && !M.TRUE_AUTOMATON)return this;

        if(TRUE_FALSE_AUTOMATON && TRUE_AUTOMATON){
            M.not(print,prefix,log);
            return M;
        }
        if(M.TRUE_FALSE_AUTOMATON && M.TRUE_AUTOMATON){
            this.not(print,prefix,log);
            return this;
        }

        long timeBefore = System.currentTimeMillis();
        if(print){
            String msg = prefix + "computing ^:" + Q + " states - " + M.Q + " states";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }

        totalize(print,prefix+" ",log);
        M.totalize(print,prefix+" ",log);
        Automaton N = crossProduct(M,"^",print,prefix + " ", log);
        N.minimize(print,prefix+" ",log);
        N.applyAllRepresentations();

        long timeAfter = System.currentTimeMillis();
        if(print){
            String msg = prefix + "computed ^:" + N.Q + " states - "+(timeAfter-timeBefore)+"ms";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
        return N;
    }

    /**
     * @param M
     * @return  this automaton imply M
     * @throws Exception
     */
    public Automaton imply(Automaton M,boolean print, String prefix, StringBuffer log) throws Exception{
        if((TRUE_FALSE_AUTOMATON && TRUE_AUTOMATON) && (M.TRUE_FALSE_AUTOMATON && !M.TRUE_AUTOMATON)) return new Automaton(false);
        if((TRUE_FALSE_AUTOMATON && !TRUE_AUTOMATON) || (M.TRUE_FALSE_AUTOMATON && M.TRUE_AUTOMATON)) return new Automaton(true);
        if(TRUE_FALSE_AUTOMATON && TRUE_AUTOMATON)return M;
        if(M.TRUE_FALSE_AUTOMATON && !M.TRUE_AUTOMATON){
            this.not(print,prefix,log);
            return this;
        }

        long timeBefore = System.currentTimeMillis();
        if(print){
            String msg = prefix + "computing =>:" + Q + " states - " + M.Q + " states";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }

        totalize(print,prefix+" ",log);
        M.totalize(print,prefix+" ",log);
        Automaton N = crossProduct(M,"=>",print,prefix+" ",log);
        N.minimize(print,prefix+" ",log);
        N.applyAllRepresentations();

        long timeAfter = System.currentTimeMillis();
        if(print){
            String msg = prefix + "computed =>:" + N.Q + " states - "+(timeAfter-timeBefore)+"ms";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }

        return N;
    }

    /**
     * @param M
     * @return  this automaton iff M
     * @throws Exception
     */
    public Automaton iff(Automaton M,boolean print, String prefix, StringBuffer log) throws Exception{
        if(((TRUE_FALSE_AUTOMATON && TRUE_AUTOMATON) && (M.TRUE_FALSE_AUTOMATON && M.TRUE_AUTOMATON)) ||
                ((TRUE_FALSE_AUTOMATON && !TRUE_AUTOMATON) && (M.TRUE_FALSE_AUTOMATON && !M.TRUE_AUTOMATON))) return new Automaton(true);
        if(((TRUE_FALSE_AUTOMATON && TRUE_AUTOMATON) && (M.TRUE_FALSE_AUTOMATON && !M.TRUE_AUTOMATON)) ||
                ((TRUE_FALSE_AUTOMATON && !TRUE_AUTOMATON) && (M.TRUE_FALSE_AUTOMATON && M.TRUE_AUTOMATON))) return new Automaton(false);

        if(TRUE_FALSE_AUTOMATON && TRUE_AUTOMATON)return M;
        if(M.TRUE_FALSE_AUTOMATON && M.TRUE_AUTOMATON)return this;
        if(TRUE_FALSE_AUTOMATON && !TRUE_AUTOMATON){
            M.not(print,prefix,log);
            return M;
        }
        if(M.TRUE_FALSE_AUTOMATON && !M.TRUE_AUTOMATON){
            this.not(print,prefix,log);
            return this;
        }

        long timeBefore = System.currentTimeMillis();
        if(print){
            String msg = prefix + "computing <=>:" + Q + " states - " + M.Q + " states";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }

        totalize(print,prefix+" ",log);
        M.totalize(print,prefix+" ",log);
        Automaton N = crossProduct(M,"<=>",print,prefix+" ",log);
        N.minimize(print,prefix+" ",log);
        N.applyAllRepresentations();

        long timeAfter = System.currentTimeMillis();
        if(print){
            String msg = prefix + "computed <=>:" + N.Q + " states - "+(timeAfter-timeBefore)+"ms";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }

        return N;
    }

    /**
     * @return changes this automaton to its negation
     * @throws Exception
     */
    public void not(boolean print, String prefix, StringBuffer log) throws Exception{
        if(TRUE_FALSE_AUTOMATON){
            TRUE_AUTOMATON = !TRUE_AUTOMATON;
            return;
        }

        long timeBefore = System.currentTimeMillis();
        if(print){
            String msg = prefix + "computing ~:" + Q + " states";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }

        totalize(print,prefix+" ",log);
        for(int q = 0 ; q < Q;q++)
            O.set(q, O.get(q) != 0 ? 0 : 1 );

        minimize(print,prefix+" ",log);
        applyAllRepresentations();

        long timeAfter = System.currentTimeMillis();
        if(print){
            String msg = prefix + "computed ~:" + Q + " states - "+(timeAfter-timeBefore)+"ms";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
    }

    public boolean equals(Automaton M)throws Exception{
        if(M == null)return false;
        if(TRUE_FALSE_AUTOMATON != M.TRUE_FALSE_AUTOMATON)return false;
        if(TRUE_FALSE_AUTOMATON && M.TRUE_FALSE_AUTOMATON){
            if(TRUE_AUTOMATON != M.TRUE_AUTOMATON)return false;
            return true;
        }
        dk.brics.automaton.Automaton Y = M.to_dk_bricks_automaton();
        dk.brics.automaton.Automaton X = to_dk_bricks_automaton();
        return X.equals(Y);
    }

    public Automaton combine(List<String> automataNames, boolean print, String prefix, StringBuffer log) throws Exception {
        Queue<Automaton> subautomata =  new LinkedList<Automaton>();
		for (String name : automataNames) {
			Automaton M = new Automaton(UtilityMethods.get_address_for_automata_library()+name+".txt");
			subautomata.add(M);
		}
		Automaton first = this.clone();
	
		// In an automaton without output, every non-zero output value represents an accepting state
		for (int q = 0; q < first.Q; q++) {
			if (first.O.get(q) != 0) {
				first.O.set(q, 1);
			}
		}
		first.combineIndex = 1;
		while (subautomata.size() > 0) {
			Automaton next = subautomata.remove();
			// potentially add logging later
            
            // crossProduct requires labelling so we make an arbitrary labelling and use it for both: this is valid since
            // input alphabets and arities are assumed to be identical for the combine method
            first.randomLabel();
            next.label = first.label;
            // crossProduct requires both automata to be totalized, otherwise it has no idea which cartesian states to transition to
            first.totalize(print,prefix+" ",log);
            next.totalize(print,prefix+" ",log);
			Automaton product = first.crossProduct(next, "combine", print, prefix, log);
			product.combineIndex = first.combineIndex + 1;
			first = product;
		}
        return first;
    }

    public void applyAllRepresentations() throws Exception{
        boolean flag = false;
        if(label == null || label.size() != A.size()){
            flag = true;
            randomLabel();
        }
        Automaton K = this;
        for(int i = 0 ; i < A.size();i++){
            if(NS.get(i) != null){
                Automaton N = NS.get(i).getAllRepresentations();
                if(N != null && NS.get(i).should_we_use_allRepresentations()) {
                    N.bind(label.get(i));
                    K = K.and(N,false,null,null);
                }
            }
        }
        if(flag)
            unlabel();
        copy(K);
    }

    private void randomLabel() {
        if(label == null)label = new ArrayList<String>();
        else if(label.size() > 0)label = new ArrayList<String>();
        for(int i = 0 ; i < A.size();i++){
            label.add(Integer.toString(i));
        }
    }

    private void unlabel(){
        label = new ArrayList<String>();
        labelSorted = false;
    }

    private void copy(Automaton M){
        TRUE_FALSE_AUTOMATON = M.TRUE_FALSE_AUTOMATON;
        TRUE_AUTOMATON = M.TRUE_AUTOMATON;
        A = M.A;
        NS = M.NS;
        alphabetSize = M.alphabetSize;
        encoder = M.encoder;
        Q = M.Q;
        q0 = M.q0;
        O = M.O;
        label = M.label;
        canonized = M.canonized;
        labelSorted = M.labelSorted;
        d = M.d;
    }

    /**
     * This method adds a dead state to totalize the transition function
     * @throws Exception
     */
    private void totalize(boolean print, String prefix, StringBuffer log) throws Exception{
        long timeBefore = System.currentTimeMillis();
        if(print){
            String msg = prefix + "totalizing:" + Q + " states";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
        //we first check if the automaton is totalized
        boolean totalized = true;
        for(int q = 0 ; q < Q;q++){
            for(int x = 0; x < alphabetSize;x++){
                if(!d.get(q).containsKey(x)){
                    List<Integer> nullState = new ArrayList<Integer>();
                    nullState.add(Q);
                    d.get(q).put(x, nullState);
                    totalized = false;
                }
            }
        }
        if(!totalized){
            O.add(0);
            Q++;
            for(int x = 0;x < alphabetSize;x++){
                List<Integer> nullState = new ArrayList<Integer>();
                nullState.add(Q-1);
                d.add(new TreeMap<Integer,List<Integer>>());
                d.get(Q-1).put(x, nullState);
            }
        }

        long timeAfter = System.currentTimeMillis();
        if(print){
            String msg = prefix + "totalized:" + Q + " states - "+(timeAfter-timeBefore)+"ms";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
    }

    /**
     * The operator can be one of "<" ">" "=" "!=" "<=" ">=".
     * For example if operator = "<" then this method returns
     * a DFA that accepts x iff this[x] < W[x] lexicographically.
     * To be used only when this automaton and M are DFAOs (words).
     * @param W
     * @param operator
     * @return
     * @throws Exception
     */
    public Automaton compare(Automaton W, String operator,boolean print, String prefix,StringBuffer log) throws Exception{
        long timeBefore = System.currentTimeMillis();
        if(print){
            String msg = prefix + "comparing ("+operator+"):" + Q + " states - " + W.Q + " states";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
        Automaton M = crossProduct(W,operator,print,prefix+" ",log);
        M.minimize(print,prefix+" ",log);
        long timeAfter = System.currentTimeMillis();
        if(print){
            String msg = prefix + "compared ("+operator+ "):" + Q + " states - "+(timeAfter-timeBefore)+"ms";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
        return M;
    }

    /**
     * The operator can be one of "<" ">" "=" "!=" "<=" ">=".
     * For example if operator = "<" then this method changes the word automaton
     * to a DFA that accepts x iff this[x] < o lexicographically.
     * To be used only when this automaton is a DFAO (word).
     * @param W
     * @param operator
     * @return
     * @throws Exception
     */
    public void compare(int o, String operator, boolean print, String prefix,StringBuffer log) throws Exception{
        long timeBefore = System.currentTimeMillis();
        if(print){
            String msg = prefix + "comparing ("+operator+") against "+ o +":" + Q + " states";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
        for(int p = 0 ; p < Q;p++){
            switch(operator){
            case "<":
                O.set(p,(O.get(p) < o) ? 1 : 0);
                break;
            case ">":
                O.set(p,(O.get(p) > o) ? 1 : 0);
                break;
            case "=":
                O.set(p,(O.get(p) == o) ? 1 : 0);
                break;
            case "!=":
                O.set(p,(O.get(p) != o) ? 1 : 0);
                break;
            case "<=":
                O.set(p,(O.get(p) <= o) ? 1 : 0);
                break;
            case ">=":
                O.set(p,(O.get(p) >= o) ? 1 : 0);
                break;
            }
        }
        minimize(print,prefix+" ",log);
        long timeAfter = System.currentTimeMillis();
        if(print){
            String msg = prefix + "compared ("+operator+ ") against "+o+":" + Q + " states - "+(timeAfter-timeBefore)+"ms";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
    }

    /**
     * Writes this automaton to a file given by the address.
     * This automaton can be non deterministic. It can also be a DFAO. However it cannot have epsilon transition.
     * @param address
     * @throws
     */
    public void write(String address){
        try {
            PrintWriter out = new PrintWriter(address, "UTF-8");
            if(TRUE_FALSE_AUTOMATON){
                if(TRUE_AUTOMATON)
                    out.write("true");
                else
                    out.write("false");
            }
            else{
                canonize();
                writeAlphabet(out);
                for(int q = 0; q < Q;q++){
                    writeState(out, q);
                }
            }
            out.close();
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
    }

    private void writeAlphabet(PrintWriter out) {
        for(int i = 0; i < A.size();i++){
            List<Integer> l = A.get(i);
            if(NS.get(i) == null){
                out.write("{");
                for(int j = 0 ; j < l.size(); j++) {
                    if(j == 0) {
                        out.write(Integer.toString(l.get(j)));
                    }
                    else out.write(", " + Integer.toString(l.get(j)));
                }

                out.write("} ");
            }
            else {
                if(i == 0)
                    out.write(NS.get(i).toString());
                else
                    out.write(" " + NS.get(i).toString());

            }
        }
        out.write(UtilityMethods.newLine());
    }

    private void writeState(PrintWriter out,int q){
        out.write(
            UtilityMethods.newLine() + q + " " +
            Integer.toString(O.get(q)) + UtilityMethods.newLine());
        for(int n: d.get(q).keySet()){
            List<Integer> l = decode(n);
            for(int j = 0 ; j < l.size();j++)
                out.write(Integer.toString(l.get(j)) + " ");
            out.write("->");
            for(int dest:d.get(q).get(n))
                out.write(" " + Integer.toString(dest));
            out.write(UtilityMethods.newLine());
        }
    }

    /**
     * Writes down this automaton to a .gv file given by the address. It uses the predicate that
     * caused this automaton as the label of this drawing.
     * This automaton can be a non deterministic automaton but cannot be a DFAO. In case of a DFAO the drawing
     * does not contain state outputs.
     * @param address
     */
    public void draw(String address,String predicate)throws Exception{
        GraphViz gv = new GraphViz();
        if(TRUE_FALSE_AUTOMATON){
            gv.addln(gv.start_graph());
            gv.addln("label = \"(): "+predicate+"\";");
            gv.addln("rankdir = LR;");
            if(TRUE_AUTOMATON)
                gv.addln("node [shape = doublecircle, label=\""+0+"\", fontsize=12]"+0 +";");
            else
                gv.addln("node [shape = circle, label=\""+0+"\", fontsize=12]"+0 +";");
            gv.addln("node [shape = point ]; qi");
            gv.addln("qi ->" + 0+";");
            if(TRUE_AUTOMATON)
                gv.addln(0 + " -> " + 0+ "[ label = \"*\"];");
            gv.addln(gv.end_graph());
        }
        else{
            canonize();
            gv.addln(gv.start_graph());
            gv.addln("label = \""+ UtilityMethods.toTuple(label) +": "+predicate+"\";");
            gv.addln("rankdir = LR;");
            for(int q = 0 ; q < Q;q++){
                if(O.get(q)!=0)
                    gv.addln("node [shape = doublecircle, label=\""+q+"\", fontsize=12]"+q +";");
                else
                    gv.addln("node [shape = circle, label=\""+q+"\", fontsize=12]"+q +";");
            }

            gv.addln("node [shape = point ]; qi");
            gv.addln("qi -> " + q0+";");

            TreeMap<Integer, TreeMap<Integer, List<String>>> transitions =
                new TreeMap<Integer, TreeMap<Integer, List<String>>>();
            for(int q = 0; q < Q; q++) {
                transitions.put(q, new TreeMap<>());
                for(int x : d.get(q).keySet()) {
                    for(int dest : d.get(q).get(x)) {
                        transitions.get(q).putIfAbsent(dest, new ArrayList<String>());
                        transitions.get(q).get(dest).add(
                            UtilityMethods.toTransitionLabel(decode(x)));
                    }
                }
            }

            for(int q = 0; q < Q; q++) {
                for(int dest : transitions.get(q).keySet()) {
                    String transition_label = String.join(", ", transitions.get(q).get(dest));
                    gv.addln(q + " -> " + dest + "[ label = \"" + transition_label + "\"];");
                }
            }

            gv.addln(gv.end_graph());
        }
        try {
            PrintWriter out = new PrintWriter(address, "UTF-8");
            out.write(gv.getDotSource());
            out.close();
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
    }

    /**
     * Writes down matrices for this automaton to a .mpl file given by the address.
     * @param address
     */
    public String write_matrices(String address,List<String> free_variables)throws Exception{
        if(TRUE_FALSE_AUTOMATON){
            throw new Exception("incidence matrices cannot be calculated, because the automaton does not have a free variable.");
        }
        canonize();
        StringBuffer s = new StringBuffer();
        s.append("with(ArrayTools):" + UtilityMethods.newLine());
        write_initial_state_vector(s);
        s.append(UtilityMethods.newLine() + "# In what follows, the M_i_x, for a free variable i and a value x, denotes" + UtilityMethods.newLine());
        s.append("# an incidence matrix of the underlying graph of (the automaton of)" + UtilityMethods.newLine());
        s.append("# the predicate in the query." + UtilityMethods.newLine());
        s.append("# For every pair of states p and q, the entry M_i_x[p][q] denotes the number of" + UtilityMethods.newLine());
        s.append("# transitions with i=x from p to q." + UtilityMethods.newLine());
        for(String variable : free_variables){
            if(!label.contains(variable)){
                throw new Exception("incidence matrices for the variable " + variable + " cannot be calculated, because " + variable +" is not a free variable.");
            }
        }
        List<Integer> indices = free_variables.stream().map(variable -> label.indexOf(variable)).collect(Collectors.toList());
        List<List<Integer>> indexValueLists = indices.stream().map(index -> A.get(index)).collect(Collectors.toList());
        List<List<Integer>> valueLists = cartesianProduct(indexValueLists);
        for (List<Integer> valueList: valueLists) {
            write_matrix_for_a_variable_list_value_pair(free_variables,valueList,indices,s);
        }
        write_final_states_vector(s);
        s.append(UtilityMethods.newLine() + "for i from 1 to Size(v)[2] do v := v.M_");
        s.append(String.join("_", free_variables)+"_");
        s.append(String.join("_", Collections.nCopies(free_variables.size(), "0")));
        s.append("; od; #fix up v by multiplying");

        String res = s.toString();

        try {
            PrintWriter out = new PrintWriter(address, "UTF-8");
            out.write(res);
            out.close();
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        return res;
    }

    private void write_matrix_for_a_variable_list_value_pair(List<String> variables, List<Integer> valueList, List<Integer> indices, StringBuffer s) {
        s.append(UtilityMethods.newLine() + "M_"+String.join("_", variables)+"_");
        s.append(valueList.stream().map(String::valueOf).collect(Collectors.joining("_")));
        s.append(" := Matrix([");
        Set<Integer> encoded_values = new HashSet<Integer>();
        for(int x = 0; x != alphabetSize;++x){
            List<Integer> decoding = decode(x);
            List<Integer> compareList = indices.stream().map(index -> decoding.get(index)).collect(Collectors.toList());
            if(compareList.equals(valueList)){
                encoded_values.add(x);
            }
        }
        int[][] M = new int[Q][Q];
        for(int p = 0 ; p < Q;++p){
            TreeMap<Integer, List<Integer>> transitions_p = d.get(p);
            for(int v : encoded_values){
                if(transitions_p.containsKey(v)){
                    List<Integer> dest = transitions_p.get(v);
                    for(int q:dest){
                        M[p][q]++;
                    }
                }
            }

            s.append("[");
            for(int q = 0; q < Q;++q){
                s.append(M[p][q]);
                if(q < (Q-1)){
                    s.append(",");
                }
            }
            s.append("]");
            if(p < (Q-1)){
                s.append("," + UtilityMethods.newLine());
            }
        }
        s.append("]);" + UtilityMethods.newLine());
    }

    private void write_initial_state_vector(StringBuffer s){
        s.append("# The row vector v denotes the indicator vector of the (singleton)" + UtilityMethods.newLine());
        s.append("# set of initial states." + UtilityMethods.newLine());
        s.append("v := Vector[row]([");
        for(int q = 0 ; q != Q; ++q){
            if(q == q0){
                s.append("1");
            }
            else{
                s.append("0");
            }
            if(q < (Q-1)){
                s.append(",");
            }
        }
        s.append("]);" + UtilityMethods.newLine());
    }

    private void write_final_states_vector(StringBuffer s){
        s.append(UtilityMethods.newLine()+"# The column vector w denotes the indicator vector of the" + UtilityMethods.newLine());
        s.append("# set of final states." + UtilityMethods.newLine());
        s.append("w := Vector[column]([");
        for(int q = 0; q != Q; ++q){
            if(O.get(q) != 0){
                s.append("1");
            }
            else{
                s.append("0");
            }
            if(q < (Q-1)){
                s.append(",");
            }
        }
        s.append("]);" + UtilityMethods.newLine());
    }

    private <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        List<List<T>> resultLists = new ArrayList<List<T>>();
        if (lists.size() == 0) {
            resultLists.add(new ArrayList<T>());
            return resultLists;
        } else {
            List<T> firstList = lists.get(0);
            List<List<T>> remainingLists = cartesianProduct(lists.subList(1, lists.size()));
            for (T condition : firstList) {
                for (List<T> remainingList : remainingLists) {
                    ArrayList<T> resultList = new ArrayList<T>();
                    resultList.add(condition);
                    resultList.addAll(remainingList);
                    resultLists.add(resultList);
                }
            }
        }
        return resultLists;
    }

    /**
     * We can choose to do Valmari or Hopcroft.
     * @throws Exception
     */
    public void minimize(boolean print, String prefix, StringBuffer log) throws Exception {
        long timeBefore = System.currentTimeMillis();
        if(print) {
            String msg = prefix + "Minimizing: " + Q + " states.";
            System.out.println("----- " + msg);
            log.append(msg + UtilityMethods.newLine());
        }

        minimize_valmari(print, prefix + " ", log);
        //minimize_hopcroft();

        long timeAfter = System.currentTimeMillis();
        if(print) {
            String msg = prefix + "Minimized:" + Q + " states - " + (timeAfter-timeBefore) + "ms.";
            System.out.println("----- " + msg);
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
    }

    /**
     * Uses the Hopcroft minimization algorithm of the package dk.brics.automaton to minimize this automaton.
     */
    private void minimize_hopcroft()throws Exception{
        dk.brics.automaton.Automaton M = to_dk_bricks_automaton();
        if(M.isDeterministic()){
            M.minimize();
        }
        else{
            M.determinize();
            M.minimize();
        }
        setThisAutomatonToRepresent(M);
    }

    /**
     * Transform this automaton from Automaton to dk.bricks.automaton.Automaton. This automaton can be
     * any automaton (deterministic/non-deterministic and with output/without output).
     * @return
     * @throws Exception
     */
    public dk.brics.automaton.Automaton to_dk_bricks_automaton()throws Exception{
        /**
         * Since the dk.bricks.automaton uses char as its input alphabet for an automaton, then in order to transform
         * Automata.Automaton to dk.bricks.automaton.Automata we've got to make sure, the input alphabet is less than
         * size of char which 2^16 - 1
         */
        if(alphabetSize > ((1<<Character.SIZE) -1)){
            //System.out.println("Character size: " + );
            throw new Exception("size of input alphabet exceeds the limit of " + ((1<<Character.SIZE) -1));
        }
        boolean deterministic = true;
        List<dk.brics.automaton.State> setOfStates = new ArrayList<>();
        for(int q = 0 ; q < Q;q++){
            setOfStates.add(new dk.brics.automaton.State());
            if(O.get(q) != 0)setOfStates.get(q).setAccept(true);
        }
        dk.brics.automaton.State initialState = setOfStates.get(q0);
        for(int q = 0 ; q < Q;q++){
            for(int x: d.get(q).keySet()){
                for(int dest:d.get(q).get(x)){
                    setOfStates.get(q).addTransition(new dk.brics.automaton.Transition((char)x,setOfStates.get(dest)));
                }
                if(d.get(q).get(x).size() > 1)deterministic = false;
            }
        }
        dk.brics.automaton.Automaton M = new dk.brics.automaton.Automaton();
        M.setInitialState(initialState);
        M.restoreInvariant();
        M.setDeterministic(deterministic);
        return M;
    }

    /**
     * Set the fields of this automaton to represent a dk.brics.automaton.Automaton.
     * An automata in our program can be of type Automaton or dk.brics.automaton.Automaton. We use package
     * dk.bricks.automaton for automata minimization. This method transforms an automaton of type dk.bricks.automaton.Automaton
     * to an automaton of type Automaton.
     * @param M is a deterministic automaton without output.
     */
    private void setThisAutomatonToRepresent(dk.brics.automaton.Automaton M)throws Exception{
        if(!M.isDeterministic())
            throw new Exception("cannot set an automaton of type Automaton to a non-deterministic automaton of type dk.bricks.automaton.Automaton");
        List<State> setOfStates = new ArrayList<State>(M.getStates());
        Q = setOfStates.size();
        q0 = setOfStates.indexOf(M.getInitialState());
        O = new ArrayList<Integer>();
        d = new ArrayList<TreeMap<Integer,List<Integer>>>();
        canonized = false;
        for(int q = 0 ; q < Q;q++){
            State state = setOfStates.get(q);
            if(state.isAccept())O.add(1);
            else O.add(0);
            TreeMap<Integer,List<Integer>> currentStatesTransitions = new TreeMap<Integer, List<Integer>>();
            d.add(currentStatesTransitions);
            for(Transition t: state.getTransitions()){
                for(char a = t.getMin();a <= t.getMax();a++){
                    List<Integer> dest = new ArrayList<Integer>();
                    dest.add(setOfStates.indexOf(t.getDest()));
                    currentStatesTransitions.put((int)a, dest);
                }
            }
        }
    }

    /**
     *  Sorts states in Q based on their breadth-first order. It also calls sortLabel().
     *  The method also removes states that are not reachable from the initial state.
     *  In draw() and write() methods, we first call the canonize the automaton.
     *  It is also used in write() method.
     *  Note that before we try to canonize, we check if this automaton is already canonized.
     *  @throws Exception
     */
    public void canonize(){
        if(canonized) return;

        sortLabel();
        if(TRUE_FALSE_AUTOMATON) return;

        Queue<Integer> state_queue = new LinkedList<Integer>();
        state_queue.add(q0);

        /**map holds the permutation we need to apply to Q. In other words if map = {(0,3),(1,10),...} then
        *we have got to send Q[0] to Q[3] and Q[1] to Q[10]*/
        HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
        map.put(q0,0);
        int i = 1;
        while(!state_queue.isEmpty()) {
            int q = state_queue.poll();
            for(int x:d.get(q).keySet()) {
                for(int p: d.get(q).get(x)) {
                    if(!map.containsKey(p)) {
                        map.put(p, i++);
                        state_queue.add(p);
                    }
                }
            }
        }

        q0 = map.get(q0);
        int newQ = map.size();
        List<Integer> newO = new ArrayList<Integer>();
        for(int q = 0 ; q < newQ;q++) {
            newO.add(0);
        }
        for(int q = 0; q < Q;q++) {
            if(map.containsKey(q)) {
                newO.set(map.get(q),O.get(q));
            }
        }

        List<TreeMap<Integer,List<Integer>>> new_d = new ArrayList<TreeMap<Integer,List<Integer>>>();
        for(int q = 0 ; q < newQ;q++) {
            new_d.add(null);
        }

        for(int q = 0; q < Q;q++) {
            if(map.containsKey(q)) {
                new_d.set(map.get(q), d.get(q));
            }
        }

        Q = newQ;
        O = newO;
        d = new_d;
        for(int q = 0 ; q < Q;q++) {
            for(int x:d.get(q).keySet()) {
                List<Integer> newDestination = new ArrayList<Integer>();
                for(int p:d.get(q).get(x)) {
                    if(map.containsKey(p)) {
                        newDestination.add(map.get(p));
                    }
                }

                if(newDestination.size() > 0) {
                    d.get(q).put(x,newDestination);
                } else {
                    d.get(q).remove(x);
                }
            }
        }

        canonized = true;
    }

    /**
     *  Sorts inputs based on their labels lexicographically.
     *  For example if the labels of the inputs are ["b","c","a"], then the first, second, and third
     *  inputs are "a", "b", and "c". Now if we call sortLabels(), the order of inputs changes: label becomes
     *  sorted in lexicographic order ["a","b","c"], and therefore, the first, second, and third inputs are
     *  now "a", "b", and "c". Before we draw this automaton using draw() method,
     *  we first sort the labels (inside canonize method).
     *  It is also used in write() method.
     *  Note that before we try to sort, we check if the label is already sorted.
     *  The label cannot have repeated element.
     */
    protected void sortLabel() {
        if(labelSorted)return;
        labelSorted = true;
        if(TRUE_FALSE_AUTOMATON)return;
        if(label == null || label.size() != A.size())return;
        //first we check if label is already sorted.
        boolean sorted = true;
        for(int i = 0 ; i < label.size()-1;i++){
            if(label.get(i).compareTo(label.get(i+1)) > 0){
                sorted = false;
                break;
            }
        }
        if(sorted)return;
        List<String> sorted_label = new ArrayList<String>(label);
        Collections.sort(sorted_label);
        /**
         * For example if label_permutation[1]=[3], then input number 1 becomes input number 3 after sorting.
         * For example if label = ["z","a","c"], and A = [[-1,2],[0,1],[1,2,3]],
         * then label_permutation = [2,0,1] and permuted_A = [[0,1],[1,2,3],[-1,2]].
         */
        int[] label_permutation = new int[label.size()];
        for(int i = 0 ; i < label.size();i++){
            label_permutation[i] = sorted_label.indexOf(label.get(i));
        }
        /**
         * permuted_A is going to hold the alphabet of the sorted inputs.
         * For example if label = ["z","a","c"], and A = [[-1,2],[0,1],[1,2,3]],
         * then label_permutation = [2,0,1] and permuted_A = [[0,1],[1,2,3],[-1,2]].
         * The same logic is behind permuted_encoder.
         */
        List<List<Integer>> permuted_A = UtilityMethods.permute(A, label_permutation);
        List<Integer> permuted_encoder = new ArrayList<Integer>();
        permuted_encoder.add(1);
        for(int i = 0 ; i < A.size()-1;i++){
            permuted_encoder.add(permuted_encoder.get(i)*permuted_A.get(i).size());
        }
        /**
         * For example encoded_input_permutation[2] = 5 means that encoded input 2 becomes
         * 5 after sorting.
         */
        int[] encoded_input_permutation = new int[alphabetSize];
        for(int i = 0 ; i < alphabetSize;i++){
            List<Integer> input = decode(i);
            List<Integer> permuted_input = UtilityMethods.permute(input, label_permutation);
            encoded_input_permutation[i] = encode(permuted_input, permuted_A,permuted_encoder);
        }

        label = sorted_label;
        A = permuted_A;
        encoder = permuted_encoder;
        NS = UtilityMethods.permute(NS,label_permutation);

        for(int q = 0; q < Q;q++){
            TreeMap<Integer,List<Integer>> permuted_d = new TreeMap<Integer,List<Integer>>();
            for(int x:d.get(q).keySet())
                permuted_d.put(encoded_input_permutation[x], d.get(q).get(x));
            d.set(q,permuted_d);
        }
    }

    /**
     * Input to dk.brics.automaton.Automata is a char. Input to Automaton is List<Integer>.
     * Thus this method transforms an integer to its corresponding List<Integer>
     * Example: A = [[0,1],[-1,2,3]] and if
     * n = 0 then we return [0,-1]
     * n = 1 then we return [1,-1]
     * n = 2 then we return [0,2]
     * n = 3 then we return [1,2]
     * n = 4 then we return [0,3]
     * n = 5 then we return [1,3]
     * @param c
     * @return
     */
    private List<Integer> decode(int n){
        List<Integer> l = new ArrayList<Integer>();
        for(int i = 0 ; i < A.size();i++){
            l.add(A.get(i).get(n % A.get(i).size()));
            n = n / A.get(i).size();
        }
        return l;
    }

    /**
     * Input to dk.brics.automaton.Automata is a char. Input to Automata.Automaton is List<Integer>.
     * Thus this method transforms a List<Integer> to its corresponding integer.
     * The other application of this function is when we use the transition function d in State. Note that the transtion function
     * maps an integer (encoding of List<Integer>) to a set of states.
     *
     * Example: A = [[0,1],[-1,2,3]] and if
     * l = [0,-1] then we return 0
     * l = [1,-1] then we return 1
     * l = [0,2] then we return 2
     * l = [1,2] then we return 3
     * l = [0,3] then we return 4
     * l = [1,3] then we return 5
     * Second Example: A = [[-2,-1,-3],[0,1],[-1,0,3],[7,8]] and if
     * l = [-2,0,-1,7] then we return 0
     * l = [-1,0,-1,7] then we return 1
     * l = [-3,0,-1,7] then we return 2
     * l = [-2,1,-1,7] then we return 3
     * l = [-1,1,-1,7] then we return 4
     * l = [-3,1,-1,7] then we return 5
     * l = [-2,0,0,7] then we return 6
     * ...
     * @param l
     * @return
     */
    public int encode(List<Integer> l){
        if(encoder == null){
            encoder = new ArrayList<Integer>();
            encoder.add(1);
            for(int i = 0 ; i < A.size()-1;i++){
                encoder.add(encoder.get(i)*A.get(i).size());
            }
        }
        int encoding = 0;
        for(int i = 0 ; i < l.size();i++){
            encoding += encoder.get(i) * A.get(i).indexOf(l.get(i));
        }
        return encoding;
    }

    public int encode(List<Integer> l,List<List<Integer>> A,List<Integer> encoder){
        int encoding = 0;
        for(int i = 0 ; i < l.size();i++){
            encoding += encoder.get(i) * A.get(i).indexOf(l.get(i));
        }
        return encoding;
    }

    /**A wildcard is denoted by null in L. What do we mean by expanding wildcard?
     * Here is an example: suppose that A = [[1,2],[0,-1],[3,4,5]] and L = [1,*,4]. Then the method would return
     * [[1,0,4],[1,-1,4]]. In other words, it'll replace * in the second position with 0 and -1.
     * */
    private List<List<Integer>> expandWildcard(List<Integer> L){
        List<List<Integer>> R = new ArrayList<List<Integer>>();
        R.add(new ArrayList<Integer>(L));
        for(int i = 0 ; i < L.size();i++){
            if(L.get(i) == null){
                List<List<Integer>> tmp = new ArrayList<List<Integer>>();
                for(int x:A.get(i)){
                    for(List<Integer> tmp2:R){
                        tmp.add(new ArrayList<Integer>(tmp2));
                        tmp.get(tmp.size()-1).set(i, x);
                    }
                }
                R = new ArrayList<List<Integer>>(tmp);
            }
        }
        return R;
    }

    public void bind(String a)throws Exception{
        if(TRUE_FALSE_AUTOMATON || A.size() != 1)throw new Exception("invalid use of method bind");
        if(label == null || label.size()!=0)label = new ArrayList<String>();
        label.add(a);
        labelSorted = false;
    }

    public void bind(String a,String b)throws Exception{
        if(TRUE_FALSE_AUTOMATON || A.size() != 2)throw new Exception("invalid use of method bind");
        if(label == null || label.size()!=0)label = new ArrayList<String>();
        label.add(a);label.add(b);
        canonized = false;
        labelSorted = false;
        removeSameInputs(0);
    }

    public void bind(String a,String b,String c)throws Exception{
        if(TRUE_FALSE_AUTOMATON || A.size() != 3)throw new Exception("invalid use of method bind");
        if(label == null || label.size()!=0)label = new ArrayList<String>();
        label.add(a);label.add(b);label.add(c);
        labelSorted = false;
        canonized = false;
        removeSameInputs(0);
    }

    public void bind(List<String> names)throws Exception{
        if(TRUE_FALSE_AUTOMATON || A.size() != names.size())throw new Exception("invalid use of method bind");
        if(label == null || label.size()!=0)label = new ArrayList<String>();
        this.label.addAll(names);
        labelSorted = false;
        canonized = false;
        removeSameInputs(0);
    }

    public boolean isBound(){
        if(label == null || label.size() != A.size())
            return false;
        return true;
    }

    public int getArity(){
        if(TRUE_FALSE_AUTOMATON)return 0;
        return A.size();
    }

    /*public Type getTypeOfInput(int i)throws Exception{
        if(TRUE_FALSE_AUTOMATON || i >= T.size())throw new Exception("invalid use of method getTypeOfInput");
        return T.get(i);
    }*/

    /**clears this automaton*/
    private void clear(){
        A = null;
        NS = null;
        encoder = null;
        O = null;
        label = null;
        d = null;
        canonized = false;
        labelSorted = false;
    }

    protected boolean isEmpty()throws Exception{
        if(TRUE_FALSE_AUTOMATON){
            return !TRUE_AUTOMATON;
        }
        return to_dk_bricks_automaton().isEmpty();
    }

    private void subsetConstruction(HashSet<Integer> initial_state,boolean print, String prefix, StringBuffer log)throws Exception{
        long timeBefore = System.currentTimeMillis();
        if(print){
            String msg = prefix + "determinizing:" + Q + " states";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
        int number_of_states = 0,current_state = 0;
        Hashtable<HashSet<Integer>,Integer> statesHash = new Hashtable<HashSet<Integer>,Integer>();
        List<HashSet<Integer>> statesList = new ArrayList<HashSet<Integer>>();
        statesList.add(initial_state);
        statesHash.put(initial_state, statesList.size()-1);
        number_of_states++;

        List<TreeMap<Integer,List<Integer>>> new_d = new ArrayList<TreeMap<Integer,List<Integer>>>();

        while(current_state < number_of_states){
            HashSet<Integer> state = statesList.get(current_state);
            new_d.add(new TreeMap<Integer,List<Integer>>());
            HashSet<Integer> dest;
            for(int in = 0;in!=alphabetSize;++in){
                dest = new HashSet<Integer>();
                for(int q:state){
                    if(d.get(q).containsKey(in))
                        for(int p:d.get(q).get(in))
                            dest.add(p);
                }
                if(!dest.isEmpty()){
                    if(statesHash.containsKey(dest)){
                        List<Integer> destination = new ArrayList<Integer>();
                        destination.add(statesHash.get(dest));
                        new_d.get(current_state).put(in, destination);
                    }
                    else{
                        statesList.add(dest);
                        number_of_states++;
                        statesHash.put(dest,number_of_states-1);
                        List<Integer> destination = new ArrayList<Integer>();
                        destination.add(number_of_states-1);
                        new_d.get(current_state).put(in, destination);
                    }
                }
            }
            current_state++;
        }

        d = new_d;
        Q = number_of_states;
        q0 = 0;
        List<Integer> newO = new ArrayList<Integer>();
        for(HashSet<Integer> state:statesList){
            boolean flag = false;
            for(int q:state){
                if(O.get(q)!=0){
                    newO.add(1);
                    flag=true;
                    break;
                }
            }
            if(!flag){
                newO.add(0);
            }
        }
        O = newO;
        long timeAfter = System.currentTimeMillis();
        if(print){
            String msg = prefix + "determinized:" + Q + " states - "+(timeAfter-timeBefore)+"ms";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
    }

    private void fixLeadingZerosProblem(boolean print, String prefix,StringBuffer log)throws Exception{
        if(TRUE_FALSE_AUTOMATON)return;
        long timeBefore = System.currentTimeMillis();
        if(print){
            String msg = prefix + "fixing leading zeros:" + Q + " states";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
        canonized = false;
        List<Integer> ZERO = new ArrayList<Integer>();//all zero input
        for(List<Integer> i:A)ZERO.add(i.indexOf(0));
        int zero = encode(ZERO);
        if(!d.get(q0).containsKey(zero)){
            d.get(q0).put(zero,new ArrayList<Integer>());
        }
        if(!d.get(q0).get(zero).contains(q0)){
            d.get(q0).get(zero).add(q0);
        }

        HashSet<Integer> initial_state = zeroReachableStates();
        subsetConstruction(initial_state,print,prefix+" ",log);
        minimize(print, prefix+" ", log);
        long timeAfter = System.currentTimeMillis();
        if(print){
            String msg = prefix + "fixed leading zeros:" + Q + " states - "+(timeAfter-timeBefore)+"ms";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
    }

    private void fixTrailingZerosProblem(boolean print, String prefix,StringBuffer log) throws Exception{
        long timeBefore = System.currentTimeMillis();
        if(print){
            String msg = prefix + "fixing trailing zeros:" + Q + " states";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
        canonized = false;
        Set<Integer> newFinalStates;// = statesReachableFromFinalStatesByZeros();
        newFinalStates = statesReachableToFinalStatesByZeros();
        List<Integer> ZERO = new ArrayList<Integer>();//all zero input
        for(List<Integer> i:A)ZERO.add(i.indexOf(0));
        for(int q:newFinalStates){
            O.set(q, 1);
            /*if(!d.get(q).containsKey(zero)){
                List<Integer> dest = new ArrayList<Integer>();
                dest.add(q);
                d.get(q).put(zero,dest);
            }*/
        }

        minimize(print,prefix+" ",log);

        long timeAfter = System.currentTimeMillis();
        if(print){
            String msg = prefix + "fixed trailing zeros:" + Q + " states - "+(timeAfter-timeBefore)+"ms";
            log.append(msg + UtilityMethods.newLine());
            System.out.println(msg);
        }
    }

    /**Returns the set of states reachable from the initial state by reading 0*
     */
    private HashSet<Integer> zeroReachableStates(){
        HashSet<Integer> result = new HashSet<Integer>();
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.add(q0);
        List<Integer> ZERO = new ArrayList<Integer>();//all zero input
        for(List<Integer> i:A)ZERO.add(i.indexOf(0));
        int zero = encode(ZERO);
        while(!queue.isEmpty()){
            int q = queue.poll();
            result.add(q);
            if(d.get(q).containsKey(zero))
                for(int p:d.get(q).get(zero))
                    if(!result.contains(p))
                        queue.add(p);
        }
        return result;
    }

    /**
     * So for example if f is a final state and f is reachable from q by reading 0*
     * then q will be in the resulting set of this method.
     * @return
     */
    private Set<Integer> statesReachableToFinalStatesByZeros(){
        Set<Integer> result = new HashSet<Integer>();
        Queue<Integer> queue = new LinkedList<Integer>();
        List<Integer> ZERO = new ArrayList<Integer>();
        for(List<Integer> i:A)ZERO.add(i.indexOf(0));
        int zero = encode(ZERO);
        //this is the adjacency matrix of the reverse of the transition graph of this automaton on 0
        List<List<Integer>>adjacencyList = new ArrayList<List<Integer>>();
        for(int q = 0 ; q < Q;q++)adjacencyList.add(new ArrayList<Integer>());
        for(int q = 0 ; q < Q;q++){
            if(d.get(q).containsKey(zero)){
                List<Integer> destination = d.get(q).get(zero);
                for(int p:destination){
                    adjacencyList.get(p).add(q);
                }
            }
            if(O.get(q) != 0)queue.add(q);
        }
        while(!queue.isEmpty()){
            int q = queue.poll();
            result.add(q);
            for(int p:adjacencyList.get(q))
                if(!result.contains(p))
                    queue.add(p);
        }
        return result;
    }

    /**
     * For example, suppose that first = [1,2,3], second = [-1,4,2], and equalIndices = [-1,-1,1].
     * Then the result is [1,2,3,-1,4].
     * However if second = [-1,4,3] then the result is null
     * because 3rd element of second is not equal two 2nd element of first.
     * @param first
     * @param second
     * @param equalIndices
     * @return
     */
    private List<Integer> joinTwoInputsForCrossProduct(List<Integer> first,List<Integer> second,int[] equalIndices){
        List<Integer> R = new ArrayList<Integer>();
        R.addAll(first);
        for(int i = 0 ; i < second.size();i++)
            if(equalIndices[i] == -1)
                R.add(second.get(i));
            else{
                if(!first.get(equalIndices[i]).equals(second.get(i)))
                    return null;
            }
        return R;
    }

    /**
     * Checks if any input has the same label as input i. It then removes copies of input i appropriately. So for example an
     * expression like f(a,a) becomes
     * an automaton with one input. After we are done with input i, we call removeSameInputs(i+1)
     * @param i
     * @throws Exception
     */
    private void removeSameInputs(int i) throws Exception{
        if(i >= A.size())return;
        List<Integer> I = new ArrayList<Integer>();
        I.add(i);
        for(int j = i+1; j < A.size();j++){
            if(label.get(i) == label.get(j)){
                if(!UtilityMethods.areEqual(A.get(i), A.get(j))){
                    throw new Exception("Inputs " + i + " and " + j + " have the same label but different alphabets.");
                }
                I.add(j);
            }
        }
        if(I.size() > 1){
            reduceDimension(I);
        }
        removeSameInputs(i+1);
    }

    private void reduceDimension(List<Integer> I){
        List<List<Integer>> newAlphabet = new ArrayList<List<Integer>>();
        List<Integer> newEncoder = new ArrayList<Integer>();
        newEncoder.add(1);
        for(int i = 0 ; i < A.size();i++)
            if(!I.contains(i) || I.indexOf(i) == 0)
                newAlphabet.add(new ArrayList<Integer>(A.get(i)));
        for(int i = 0 ; i < newAlphabet.size()-1;i++)
            newEncoder.add(newEncoder.get(i)*newAlphabet.get(i).size());
        List<Integer> map = new ArrayList<Integer>();
        for(int n = 0 ; n < alphabetSize;n++)
            map.add(mapToReducedEncodedInput(n, I, newEncoder, newAlphabet));
        List<TreeMap<Integer,List<Integer>>> new_d = new ArrayList<TreeMap<Integer,List<Integer>>>();
        for(int q = 0 ; q < Q;q++){
            TreeMap<Integer,List<Integer>> currentStatesTransition = new TreeMap<Integer,List<Integer>>();
            new_d.add(currentStatesTransition);
            for(int n:d.get(q).keySet()){
                int m = map.get(n);
                if(m != -1){
                    if(currentStatesTransition.containsKey(m))
                        currentStatesTransition.get(m).addAll(d.get(q).get(n));
                    else
                        currentStatesTransition.put(m, new ArrayList<Integer>(d.get(q).get(n)));
                }
            }
        }
        d = new_d;
        I.remove(0);
        A = newAlphabet;
        UtilityMethods.removeIndices(NS,I);
        encoder = null;
        alphabetSize = 1;
        for(List<Integer> x:A)
            alphabetSize *= x.size();
        UtilityMethods.removeIndices(label, I);
    }

    private int mapToReducedEncodedInput(int n,List<Integer> I,List<Integer> newEncoder,List<List<Integer>> newAlphabet){
        if(I.size() <= 1)return n;
        List<Integer> x = decode(n);
        for(int i = 1 ; i < I.size();i++)
            if(x.get(I.get(i)) != x.get(I.get(0)))
                return -1;
        List<Integer> y = new ArrayList<Integer>();
        for(int i = 0 ; i < x.size();i++)
            if(!I.contains(i) || I.indexOf(i) == 0)
                y.add(x.get(i));
        return encode(y, newAlphabet, newEncoder);
    }

    public List<TreeMap<Integer,List<Integer>>> get_transition_function() {
        return d;
    }
    /*private boolean connected(int p,int q,int i){
        if(d.get(p).containsKey(i)){
            if(d.get(p).get(i).contains(q))return true;
        }
        return false;
    }
    public void computeMatrices(List<String> inputToComputeMatrices, List<String> variationsOfTheInputs, List<int[][]> matrices) throws Exception{
        canonize();

        for(String x:inputToComputeMatrices){
            if(!label.contains(x)){
                throw new Exception("no free variable with label "+x +" to compute the matrix");
            }
        }
        HashMap<String,List<Integer>> H = computeAllVariationsOfInputs(inputToComputeMatrices);
        for(String x:H.keySet()){
            variationsOfTheInputs.add(x);
            List<Integer> var = H.get(x);
            int[][] mat = new int[Q][Q];
            matrices.add(mat);
            for(int p = 0; p < Q;p++){
                for(int q = 0;q < Q;q++){
                    int count = 0;
                    for(int i:var){
                        if(connected(p,q,i))count++;
                    }
                    mat[p][q] = count;
                }
            }
        }
    }
    private HashMap<String,List<Integer>> computeAllVariationsOfInputs(List<String> inputToComputeMatrices){

    }*/
}
