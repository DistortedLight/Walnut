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
import Main.Expression;
import Automata.Automaton;
import Automata.NumberSystem;
import Main.Type;

import java.util.HashSet;
import java.util.Stack;


public class RelationalOperator extends Operator{
	NumberSystem number_system;
	public RelationalOperator(int position,String type, NumberSystem number_system) throws Exception{
		this.op = type;
		setPriority();
		setArity(2);
		setPositionInPredicate(position);
		this.number_system = number_system;
	}
	public String toString(){
		return op+"_"+number_system;
	}
	public void act(Stack<Expression> S) throws Exception{

		if(S.size() < getArity())throw new Exception("operator " + op + " requires " + getArity()+ " operands");
		Expression b = S.pop();
		Expression a = S.pop();
		
		if(a.is(Type.alphabetLetter) && b.is(Type.alphabetLetter)){
			S.push(new Expression(a+op+b,new Automaton(compare(a.constant,b.constant))));
			return;
		}
		else if(a.is(Type.numberLiteral) && b.is(Type.numberLiteral)){
			S.push(new Expression(a+op+b,new Automaton(compare(a.constant,b.constant ))));	
			return;
		}
		else if((a.is(Type.arithmetic) || a.is(Type.variable))
				&& (b.is(Type.arithmetic) || b.is(Type.variable))){
			Automaton M = number_system.comparison(a.identifier, b.identifier, op);
			if(a.is(Type.arithmetic)){
				M = M.and(a.M);
				M.quantify(a.identifier);
			}
			if(b.is(Type.arithmetic)){
				M = M.and(b.M);
				M.quantify(b.identifier);
			}
			
			S.push(new Expression(a+op+b,M));
		}
		else if(a.is(Type.numberLiteral) && ((b.is(Type.arithmetic) || b.is(Type.variable)))){
			Automaton M = number_system.comparison(a.constant, b.identifier, op);
			if(b.is(Type.arithmetic)){
				M = M.and(b.M);
				M.quantify(b.identifier);
			}
			S.push(new Expression(a+op+b,M));
		}
		else if((a.is(Type.arithmetic) || a.is(Type.variable))
				&& b.is(Type.numberLiteral)){
			Automaton M = number_system.comparison(a.identifier, b.constant, op);
			if(a.is(Type.arithmetic)){
				M = M.and(a.M);
				M.quantify(a.identifier);
			}	
			S.push(new Expression(a+op+b,M));
		}
		else if(a.is(Type.word) && b.is(Type.word)){
			Automaton M = a.W.compare(b.W, op);
			M = M.and(a.M);
			M = M.and(b.M);
			M.quantify(new HashSet<String>(a.list_of_identifiers_to_quantify));
			M.quantify(new HashSet<String>(b.list_of_identifiers_to_quantify));			
			S.push(new Expression(a+op+b,M));
		}
		else if(a.is(Type.word) && b.is(Type.alphabetLetter)){
			a.W.compare(b.constant, op);
			Automaton M = a.W;
			M = M.and(a.M);
			M.quantify(new HashSet<String>(a.list_of_identifiers_to_quantify));
			S.push(new Expression(a+op+b,M));
		}
		else if(a.is(Type.alphabetLetter) && b.is(Type.word)){
			b.W.compare(a.constant, reverseOperator(op));
			Automaton M = b.W;
			M = M.and(b.M);
			M.quantify(new HashSet<String>(b.list_of_identifiers_to_quantify));
			S.push(new Expression(a+op+b,M));
		}
		else{
			throw new Exception("operator " + op + " cannot be applied to operands "+a+" and " +b+ " of types " + a.getType() +" and " + b.getType() + " respectively");
		}	
	}
	private boolean compare(int a,int b){
		switch(op){
		case "=":return a == b;
		case "!=":return a != b;
		case "<":return a <b;
		case ">":return a>b;
		case "<=":return a<=b;
		case ">=":return a>=b;
		default:
			return false;
		}
		
	}
	public String reverseOperator(String a){
		switch(op){
		case "=":return "=";
		case "!=":return "!=";
		case "<":return ">";
		case ">":return "<";
		case "<=":return ">=";
		case ">=":return "<=";
		default:
			return "";
		}
	}
}
