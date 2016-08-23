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
import java.util.Stack;

import Automata.NumberSystem;
import Main.Expression;


public class NumberLiteral extends Token {
	int value;
	NumberSystem base;
	public NumberLiteral(int position,int value,NumberSystem base){
		setPositionInPredicate(position);
		setArity(0);
		this.value = value;
		this.base = base;
	}
	public String toString(){
		return Integer.toString(value);
	}
	public void act(Stack<Expression> S,boolean print,String prefix,StringBuffer log) throws Exception{
		S.push(new Expression(Integer.toString(value), value,base));
	}
}
