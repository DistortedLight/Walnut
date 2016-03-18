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

package Token;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import Main.Expression;
import Automata.Automaton;
import Main.Type;

public class LogicalOperator extends Operator{
	int number_of_quantified_variables;
	public LogicalOperator(int position,String op) throws Exception{
		this.op = op;
		setPriority();
		if(op.equals("~")||op.equals("`"))setArity(1);
		else setArity(2);
		setPositionInPredicate(position);
	}
	public LogicalOperator(int position,String op,int number_of_quantified_variables) throws Exception{
		this.number_of_quantified_variables = number_of_quantified_variables;
		this.op = op;

		setPriority();
		setArity(number_of_quantified_variables+1);
		setPositionInPredicate(position);
	}

	public void act(Stack<Expression> S) throws Exception{
		if(S.size() < getArity())throw new Exception("operator " + op + " requires " + getArity()+ " operands");
		
		if(op.equals("~") || op.equals("`")){actNegationOrReverse(S);return;}
		if(op.equals("E") || op.equals("A")){actQuantifier(S);return;}
		
		Expression b = S.pop();
		Expression a = S.pop();
		
		if(a.is(Type.automaton) && b.is(Type.automaton)){
			switch(op){
			case "&":S.push(new Expression("("+a+op+b+")",a.M.and(b.M)));break;
			case "|":S.push(new Expression("("+a+op+b+")",a.M.or(b.M)));break;
			case "^":S.push(new Expression("("+a+op+b+")",a.M.xor(b.M)));break;
			case "=>":S.push(new Expression("("+a+op+b+")",a.M.imply(b.M)));break;
			case "<=>":S.push(new Expression("("+a+op+b+")",a.M.iff(b.M)));break;
			}
			return;
		}	
		throw new Exception("operator " + op + " cannot be applied to operands "+a +" and "+b +" of types " + a.getType() +" and " + b.getType() + " respectively");
			
	}
	private void actNegationOrReverse(Stack<Expression> S) throws Exception{
		Expression a = S.pop();
		if(a.is(Type.automaton)){
			if(op.equals("`"))
				a.M.reverse();
			if(op.equals("~"))
				a.M.not();
			S.push(new Expression(op + a,a.M));
			return;
		}
		throw new Exception("operator " + op + " cannot be applied to the operand "+a +" of type " + a.getType());		
	}
	private void actQuantifier(Stack<Expression> S) throws Exception{
		String stringValue = "("+op + " ";
		Stack<Expression> temp = new Stack<Expression>();
		List<Expression> operands = new ArrayList<Expression>();
		Automaton M = null;
		for(int i = 0; i < getArity();i++){
			temp.push(S.pop());
		}
		List<String> list_of_identifiers_to_quantify = new ArrayList<String>();
		for(int i = 0 ; i < getArity();i++){
			operands.add(temp.pop());
			if(i < getArity()-1){
				if(i == 0)
					stringValue += operands.get(i)+" ";
				else
					stringValue += ", "+operands.get(i)+" ";
				if(!operands.get(i).is(Type.variable))
					throw new Exception("operator " + op + " requires a list of "+number_of_quantified_variables + " variables");
				
				list_of_identifiers_to_quantify.add(operands.get(i).identifier);
			}
			else if(i == getArity()-1){
				stringValue += operands.get(i);
				if(!operands.get(i).is(Type.automaton))
					throw new Exception("the last operand of "+op+" can only be of type " + Type.automaton);
				M = operands.get(i).M;
				if(op.equals("E")){
					M.quantify(new HashSet<String>(list_of_identifiers_to_quantify));
				}
				else{
					M.not();
					M.quantify(new HashSet<String>(list_of_identifiers_to_quantify));
					M.not();
				}
			}
		}
		stringValue += ")";
		S.push(new Expression(stringValue,M));
	}
}
