/*	 Copyright 2016 Hamoon Mousavi
 * 
 * 	 This file is part of Walnut.
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

package Main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Stack;

import Automata.Automaton;
import Token.Token;


public class Computer {
	List<List<String>> matrix_requests;
	Predicate predicate_object;
	String predicate_string;
	Expression result;
	Automaton D;
	String log;
	boolean printSteps;
	public Computer(String predicate,List<List<String>> matrix_requests,boolean printSteps)throws Exception{
		this.log = "";
		this.predicate_string = predicate;
		predicate_object = new Predicate(predicate);
		this.matrix_requests = matrix_requests;
		this.printSteps = printSteps;
		compute();
		if(matrix_requests.size() > 0)
			computeMatrices();
	}
	private void computeMatrices(){
	}
	public Automaton getTheFinalResult(){
		return result.M;
	}
	public void writeMatrices(String address){
	}
	public void writeLog(String address){
		PrintWriter out;
		try {
			out = new PrintWriter(address, "UTF-16");
			out.write(log);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	public void drawAutomaton(String address) throws Exception{
		result.M.draw(address, predicate_string);
	}
	public void write(String address){
		result.M.write(address);
	}
	public String toString(){
		return result.toString();
	}
	private void compute() throws Exception{
		Stack<Expression> expression_Stack = new Stack<Expression>();
		List<Token> postOrder = predicate_object.get_postOrder();
		String prefix ="";
		long timeBeginning = System.currentTimeMillis();
		String step;
		for(Token t:postOrder){
			try{
				long timeBefore = System.currentTimeMillis();
				t.act(expression_Stack);
				long timeAfter = System.currentTimeMillis();
				if(t.isOperator() && expression_Stack.peek().is(Type.automaton)){
					step = prefix + expression_Stack.peek() + " has " + expression_Stack.peek().M.Q +" states: " + (timeAfter-timeBefore)+"ms";
					log += step + UtilityMethods.newLine();
					if(printSteps){
						System.out.println(step);
					}
					prefix += " ";
				}
			}catch(Exception e){
				e.printStackTrace();
				String message = e.getMessage();
				message += UtilityMethods.newLine() + "\t: char at "+t.getPositionInPredicate();
				throw new Exception(message);
			}
		}
		long timeEnd = System.currentTimeMillis();
		step = "total computation time: " + (timeEnd - timeBeginning)+"ms";
		log += step;
		if(printSteps)System.out.println(step);
		if(expression_Stack.size() > 1){
			String message = "cannot evaluate the followings into a single automaton:"+UtilityMethods.newLine();
			Stack<Expression> tmp = new Stack<Expression>();
			
			while(!expression_Stack.isEmpty())
				tmp.push(expression_Stack.pop());
				
			while(!tmp.isEmpty())
				message += tmp.pop() + UtilityMethods.newLine();
			
			message += "probably some operators are missing";
			throw new Exception(message);
		}
		else if(expression_Stack.isEmpty()){
			throw new Exception("evaluation ended in no result");
		}
		else if(expression_Stack.size() == 1){
			result = expression_Stack.pop();
			if(!result.is(Type.automaton)){
				throw new Exception("the final result of the evaluation is not of type " + Type.automaton);
			}
		}
	}
}
