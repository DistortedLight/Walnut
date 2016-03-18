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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Automata.Automaton;

public class IntegrationTest {
	String directoryAddress = "Test Results/Integreation Tests/"; 
	List<TestCase> testCases;//list of test cases
	List<String> L;//list of commands
	public IntegrationTest(){
		testCases = new ArrayList<TestCase>();
		L = new ArrayList<String>();
		L.add("eval test0 \"(a = 4) & (b)=(5) & (6) = c & (17 = d)\";");
		L.add("eval test1 \"?lsd_2 (a = 4) & (b)=(5) & (6) = c & (17 = d)\";");
		L.add("eval test2 \"?msd_3 (a = 4) & (b)=(5) & (6) = c & (17 = d)\";");
		L.add("eval test3 \"?msd_fib (a = 4) & (b)=(5) & (6) = c & (17 = d)\";");
		L.add("eval test4 \"?lsd_fib (a = 4) & (b)=(5) & (6) = c & (17 = d)\";");
		L.add("eval test5 \"?lsd_trib (a = 4) & (b)=(5) & (6) = c & (17 = d)\";");
		L.add("eval test6 \"(a = 4) & (?msd_3 (b)=(5)) & (6) = c & (17 = d)\";");
		L.add("eval test7 \"(a = 4) & ?msd_3 (b)=(5) & (6) = c & (17 = d)\";");
		L.add("eval test8 \"(a = 4) & (?msd_3 (b)=(5)) & (?lsd_fib (6) = c) & (17 = d)\";");
		L.add("eval test9 \"(a = 4) & (?msd_3 (b)=(5)) & ?lsd_fib (6) = c & (17 = d)\";");
		L.add("eval test10 \"a <= 9 & a!=8 & a <9 & 4 <= a & 6 != a\";");//a = 4,5,7
		L.add("eval test11 \"?msd_fib a <= 9 & a!=8 & a <9 & 4 <= a & 6 != a\";");//a = 4,5,7
		L.add("eval test12 \"?lsd_10 a <= 9 & a!=8 & a <9 & 4 <= a & 6 != a\";");//a = 4,5,7
		L.add("eval test13 \"~(a >= 10 | a < 4) & ~(a = 9 | (a<7 & a>=6)) & a != 8\";");//a = 4,5,7
		L.add("eval test14 \"?msd_fib ~(a >= 10 | a < 4) & ~(a = 9 | (a<7 & a>=6)) & a != 8\";");//a = 4,5,7
		L.add("eval test15 \"?lsd_fib ~(a >= 10 | a < 4) & ~(a = 9 | (a<7 & a>=6)) & a != 8\";");//a = 4,5,7
		L.add("eval test16 \"?lsd_10 ~(a >= 10 | a < 4) & ~(a = 9 | (a<7 & a>=6)) & a != 8\";");//a = 4,5,7
		L.add("eval test17 \"?msd_trib ~(a >= 10 | a < 4) & ~(a = 9 | (a<7 & a>=6)) & a != 8\";");//a = 4,5,7
		L.add("eval test18 \"((a<=5 & a > 3) | a = 7 | a = 9 | a = 45) & a <= 7\";");//a = 4,5,7
		L.add("eval test19 \"?msd_fib ((a<=5 & a > 3) | a = 7 | a = 9 | a = 45) & a <= 7\";");//a = 4,5,7
		L.add("eval test20 \"?lsd_fib ((a<=5 & a > 3) | a = 7 | a = 9 | a = 45) & a <= 7\";");//a = 4,5,7
		L.add("eval test21 \"?lsd_10 ((a<=5 & a > 3) | a = 7 | a = 9 | a = 45) & a <= 7\";");//a = 4,5,7
		L.add("eval test22 \"?msd_10 (a = 12 | 100=a | a = 9) & 10 <= a\";"); //a = 12 , 100
		L.add("eval test23 \"a >= 2 => a<= 3\";");//a = 0,1,2,3
		L.add("eval test24 \"?lsd_fib a >= 2 => a<= 3\";");//a = 0,1,2,3
		L.add("eval test25 \"?msd_10 a < 20 => a<= 3\";");//a >= 20 | a<=3
		L.add("eval test26 \" a =6 ^ a=6\";");
		L.add("eval test27 \"?msd_fib a =6 ^ a=6\";");
		L.add("eval test28 \" a !=6 ^ a=6\";");
		L.add("eval test29 \"?msd_fib a !=6 ^ a=6\";");
		L.add("eval test30 \" a =6 ^ a<7\";");//a = 0,1,2,3,4,5
		L.add("eval test31 \"?msd_fib a =6 ^ a<7\";");//a = 0,1,2,3,4,5
		L.add("eval test32 \" a <=5 <=> ~(a>2)\";");//a = 0,1,2,6,7,8,...
		L.add("eval test33 \"?msd_fib a <=5 <=> ~(a>2)\";");//a = 0,1,2,6,7,8,...		
		L.add("eval test34 \"?msd_fib a <=b & a>=b\";");//a=b
		L.add("eval test35 \"?lsd_3 a <=b & a>=b\";");//a=b
		L.add("eval test36 \"?msd_fib a <=a+1\";");
		L.add("eval test37 \"?msd_fib a <=a-1\";");
		L.add("eval test38 \"?msd_fib 2+a < a\";");
		L.add("eval test39 \"?msd_fib a =3*a\";");
		L.add("eval test40 \"?msd_fib 5+2*a = 4*a+1\";");
		L.add("eval test41 \"E a , b, c ,d b = 12 & e =a+2*b-c*3+b-a+d/2-3-2+5*2-8/4 & c <6 & d = 11 & c >= 5\";");//e = 29
		L.add("eval test42 \"?lsd_fib E b, c b = 12 & e =2*b-c/3+b-1 & c <20 & c >= 19\";");//e = 29
		L.add("eval test43 \"E b, c ,d b = 12 & a+2*b-c*3+b-a+d/2-3-2+5*2-8/4=a & c <6 & d = 11 & c >= 5\";");//a = 29
		
		L.add("eval test44 \"d = 20 & (?msd_fib b = 3) & a = 33 & (?msd_fib c = 4)\";");
		L.add("eval test45 \"Ea d = 20 & (?msd_fib b = 3) & a = 33 & (?msd_fib c = 4)\";");
		L.add("eval test46 \"Ed d = 20 & (?msd_fib b = 3) & a = 33 & (?msd_fib c = 4)\";");
		L.add("eval test47 \"Ed,a d = 20 & (?msd_fib b = 3) & a = 33 & (?msd_fib c = 4)\";");
		L.add("eval test48 \"Ea,d,c d = 20 & (?msd_fib b = 2) & a = 33 & (?msd_fib c = 4)\";");
		L.add("eval test49 \"Eb,d,a d = 20 & (?msd_fib b = 2) & a = 33 & (?msd_fib c = 4)\";");
		
		L.add("eval test50 \"?lsd_2 d = 20 & (?lsd_fib b = 3) & a = 33 & (?lsd_fib c = 4)\";");
		L.add("eval test51 \"?lsd_2 Ea d = 20 & (?lsd_fib b = 3) & a = 33 & (?lsd_fib c = 4)\";");
		L.add("eval test52 \"?lsd_2 Ed d = 20 & (?lsd_fib b = 3) & a = 33 & (?lsd_fib c = 4)\";");
		L.add("eval test53 \"?lsd_2 Ed,a d = 20 & (?lsd_fib b = 3) & a = 33 & (?lsd_fib c = 4)\";");
		L.add("eval test54 \"?lsd_2 Ea,d,c d = 20 & (?lsd_fib b = 2) & a = 33 & (?lsd_fib c = 4)\";");
		L.add("eval test55 \"?lsd_2 Eb,d,a d = 20 & (?lsd_fib b = 2) & a = 33 & (?lsd_fib c = 4)\";");
		
		L.add("eval test56 \"~( b != 6 | ?msd_fib a != 17)\";");
		L.add("eval test57 \"~( b != 6 | ?lsd_fib a != 17)\";");
		L.add("eval test58 \"Eb ( b = 6 & ?lsd_fib a = 17)\";");
		L.add("eval test59 \"Ea ( b = 6 & ?lsd_fib a = 17)\";");
		
		L.add("eval test60 \"Ed (?msd_fib Eb (a = b-2) & b > 4 & b <= 6) & ( Ec (d = c-2) & c > 4 & c <= 6)\";");
		L.add("eval test61 \"?lsd_2 Ea (Eb (a = b-2) & b > 4 & b <= 6) & (?lsd_fib Ec (d = c-2) & c > 4 & c <= 6)\";");

		L.add("eval test62 \"Ea (Eb (a = b-2) & b > 4 & b <= 6) & (?lsd_fib Ec (d = c-2) & c > 4 & c <= 6)\";");

		L.add("eval test63 \"?lsd_2 Ea  b = a-2 & a = 8\";");
		L.add("eval test64 \"Ea ?lsd_2 b = a-2 & a = 8\";");
		
		L.add("eval test65 \"?lsd_fib Ex,y x < y\";");
		L.add("eval test66 \"Ex,y,z y = 2*x+1 & y = 2*z\";");
		L.add("eval test67 \"?lsd_fib Ex,y,z y = 2*x+1 & y = 2*z\";");
		L.add("eval test68 \"Ex,y,z y = 2*x+1 & y = 3*z\";");
		L.add("eval test69 \"?lsd_fib Ex,y,z y = 2*x+1 & y = 3*z\";");
		L.add("eval test70 \"Ex,y y = 4*x+1 & y = 11\";");
		L.add("eval test71 \"?lsd_fib Ex,y y = 4*x+1 & y = 11\";");
		L.add("eval test72 \"Ex,y y = 4*x+1 & y = 13\";");
		L.add("eval test73 \"?lsd_fib Ex,y y = 4*x+1 & y = 13\";");
		L.add("eval test74 \"Ex 13 = 4*x+1\";");
		L.add("eval test75 \"?lsd_fib Ex 13 = 4*x+1\";");
		L.add("eval test76 \"Ex 11 = 4*x+1\";");
		L.add("eval test77 \"?lsd_fib Ex 11 = 4*x+1\";");

		L.add("eval test78 \"(Ex,y x<y) & (z=3)\";");
		L.add("eval test79 \"(Ex,y x<y) | (z=3)\";");
		L.add("eval test80 \"(Ex,y x<y) ^ (z=3)\";");
		L.add("eval test81 \"(Ex,y x<y) <=> (z=3)\";");
		L.add("eval test82 \"(Ex,y x<y) => (z=3)\";");
		L.add("eval test83 \"~(Ex,y x<y)\";");
		
		L.add("eval test84 \"(z=3) & (Ex x>x+1)\";");
		L.add("eval test85 \"(z=3) | (Ex x>x+1)\";");
		L.add("eval test86 \"(z=3) ^ (Ex x>x+1)\";");
		L.add("eval test87 \"(z=3) <=> (Ex x>x+1)\";");
		L.add("eval test88 \"(z=3) => (Ex x>x+1)\";");
		L.add("eval test89 \"~(Ex x>x+1)\";");
		
		L.add("eval test90 \"(Ex x>2*x)&(Ex,y x <y-4)\";");
		L.add("eval test91 \"(Ex x>2*x)|(Ex,y x <y-4)\";");
		L.add("eval test92 \"(Ex x>2*x)^(Ex,y x <y-4)\";");
		L.add("eval test93 \"(Ex x>2*x)<=>(Ex,y x <y-4)\";");
		L.add("eval test94 \"(Ex x>2*x)=>(Ex,y x <y-4)\";");
		
		L.add("eval test95 \"(Ex,y x <=y)&(Ex x=2 & x = 3)\";");
		L.add("eval test96 \"(Ex,y x <=y)|(Ex x=2 & x = 3)\";");
		L.add("eval test97 \"(Ex,y x <=y)^(Ex x=2 & x = 3)\";");
		L.add("eval test98 \"(Ex,y x <=y)<=>(Ex x=2 & x = 3)\";");
		L.add("eval test99 \"(Ex,y x <=y)=>(Ex x=2 & x = 3)\";");

		L.add("eval test100 \"(Ex 9 = 3*x )&(Ex x = 2 | x = 3)\";");
		L.add("eval test101 \"(Ex 9 = 3*x )|(Ex x = 2 | x = 3)\";");
		L.add("eval test102 \"(Ex 9 = 3*x )^(Ex x = 2 | x = 3)\";");
		L.add("eval test103 \"(Ex 9 = 3*x )<=>(Ex x = 2 | x = 3)\";");
		L.add("eval test104 \"(Ex 9 = 3*x )=>(Ex x = 2 | x = 3)\";");
		
		L.add("eval test105 \"~(Ex 9 = 3*x )&~(Ex x = 2 | x = 3)\";");
		L.add("eval test106 \"~(Ex 9 = 3*x )|~(Ex x = 2 | x = 3)\";");
		L.add("eval test107 \"~(Ex 9 = 3*x )^~(Ex x = 2 | x = 3)\";");
		L.add("eval test108 \"~(Ex 9 = 3*x )<=>~(Ex x = 2 | x = 3)\";");
		L.add("eval test109 \"~(Ex 9 = 3*x )=>~(Ex x = 2 | x = 3)\";");
		
		L.add("eval test110 \"?lsd_fib Eb a = b & b = 3\";");
		L.add("eval test111 \"?lsd_fib Eb b != a & b = 3\";");
		L.add("eval test112 \"?lsd_fib Eb a < b & b = 4\";");
		L.add("eval test113 \"?lsd_fib Eb b > a & b = 4\";");
		L.add("eval test114 \"?lsd_fib Eb a <= b & b = 3\";");
		L.add("eval test115 \"?lsd_fib Eb b >= a & b = 3\";");

		L.add("eval test116 \"?lsd_fib `(a = 15)\";");
		L.add("eval test117 \"?lsd_fib ```(a = 15)\";");
		L.add("eval test118 \"`(?lsd_fib (a = 15))\";");

		L.add("eval test119 \"E a (?lsd_fib a = 10 ) & b = a+1\";");
		L.add("eval test120 \"E a (?lsd_fib `(a = 10) ) & b = a+3\";");
		L.add("eval test121 \"E a `(?lsd_fib a = 10 ) & b = a+3\";");
		L.add("eval test122 \"E a ```(?lsd_fib a = 10 ) & b = a+3\";");

		L.add("eval test123 \"E a (?lsd_fib a = 10 ) & `(b = a+5)\";");
		L.add("eval test124 \"E a (?lsd_fib a = 10 ) & ```(b = a+5)\";");
		L.add("eval test125 \"`(E a `(?lsd_fib a = 10 ) & (b = a+5))\";");

		L.add("eval test126 \"?lsd_fib `~``~~`~(a=15)\";");
		L.add("eval test127 \"`~``~~`~(a=15)\";");
		L.add("eval test128 \"?lsd_fib ``~``~~`~(a=15)\";");
		L.add("eval test129 \"``~``~~`~(a=15)\";");
		L.add("eval test130 \" `~``~~`~(a=3)\";");
		L.add("eval test131 \" ~`~``~~`~(a=3)\";");
		L.add("eval test132 \"?msd_fib `~``~~`~(a=3)\";");
		L.add("eval test133 \"?msd_fib ~`~``~~`~(a=3)\";");
		
		L.add("eval test134 \"Ax  y < x+4\";");
		L.add("eval test135 \"?lsd_fib Ax  y < x+5\";");
		L.add("eval test136 \"Ax  y != 2*x+1\";");
		L.add("eval test137 \"?lsd_2 Au,v  (x != 2*u+1) & (?lsd_3 y != 3*v+2)\";");
		L.add("eval test138 \"Au,v  (x != 2*u+1) & (?msd_3 y != 3*v+2)\";");
		
		L.add("reg test139 {0,1} \"100*\";");
		L.add("reg     test140   msd_2 \"100*\";");
		L.add("reg test141 {  0  ,   1   ,   2  }  \"100*\";");
		L.add("reg test142 {  0  ,   1   ,   - 2  ,+4,        5   }  \"100*\";");
		L.add("reg test143 {  0  ,   1   ,   +  2  ,+4,      +  5   }  \"100*\";");
		L.add("reg test144      msd_fib  \"100*\";");
		L.add("reg test145      lsd_10     \"100*\";");
		L.add("reg test146      fib  \"100*\";");
		L.add("reg test147    2  \"100*\";");
		L.add("reg test148      lsd  \"100*\";");
		L.add("reg test149      lsd  \"100*270*\";");
		L.add("reg test150      lsd  \"100*(271)*\";");
		L.add("reg test151      lsd_3  \"100*(27)?1*\";");
		L.add("reg test152 3 \"100*2?7?1*\";"); 
		L.add("reg test153 {0,4,6,7} \".[5-6]*[3-6]\";");
		L.add("reg test154 msd_5 \".[5-9]*[8-9].\";");
		L.add("reg test155 msd_5 \".[4-9]+.\";");
		
		L.add("reg test156 \"T[a]=1\";");
		L.add("eval test157 \"T[a] = T[2*a]\";");
		L.add("eval test158 \"T[a] <= @2\";");
		L.add("eval test159 \"T[a] = T[2*a+1]\";");
		L.add("eval test160 \"T[a] = @0\";");
		L.add("eval test161 \"@1 = T[  a  ]\";");
		L.add("eval test162 \"@-10 = T[a]\";");
		L.add("eval test163 \"T[a] = T[a+1]\";");
		L.add("eval test164 \"Eb T[a] =T[b] & b = a+1\";");
		L.add("eval test165 \"T[a] =T[b] & b = a+1\";");
		L.add("eval test166 \"T[a<=10 & a>=5] = @1\";");
		L.add("eval test167 \"T[a<=10&T[a]=@1]=@1\";");
		L.add("eval test168 \"T[Eb a = b & T[b]>T[2*a+1]]=T[a<=12]\";");
		L.add("eval test169 \"Ak k<n => T[i+k]=T[i+k+n]\";");
		L.add("eval test170 \"Ak k<=n => T[i+k]=T[i+k+n]\";");
		L.add("eval test171 \"n>=1 & Ak k<=n => T[i+k]=T[i+k+n]\";");
		L.add("eval test172 \"En n>= 1 & Ak k< n => T[i+k]=T[i+k+n]\";");
		L.add("eval test173 \"n>0 & Ei Am (m>0 & m <n) => Ek k<m & T[i+k] != T[i+n-m+k]\";");
		L.add("eval test174 \"?msd_fib n > 0 & Ei Ak k<n => F[i+k]=F[i+k+n]\";");
		L.add("eval test175 \"?msd_fib En n > 0 & Ak k<n => F[i+k]=F[i+k+n]\";");
		L.add("eval test176 \"?msd_fib n > 0 & Ak k <n=>F[i+k]=F[i+k+n]\";");
		L.add("eval test177 \"?msd_fib n>0 & Ei Ak k < n => R[i+k]=R[i+k+n]\";");
		try{
			prover.dispatch("reg endsIn2Zeros lsd_2 \"(0|1)*00\";");
			prover.dispatch("reg startsWith2Zeros msd_2 \"00(0|1)*\";");
			prover.dispatchForIntegrationTest("def func \"(?msd_3 c < 5) & (a = b+1) & (?msd_10 e = 17)\";");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		L.add("eval test178 \"?lsd_2 n > 0 & Ef,i i >= 1 & $endsIn2Zeros(i) & $endsIn2Zeros(n) & (Ak k < n => PF[f][i+k] = PF[f][i+k+n])\";");
		L.add("eval test179 \"n > 0 & Ef,i i >= 1 & $startsWith2Zeros(i) & $startsWith2Zeros(n) & (Ak k < n => PFmsd[f][i+k] = PFmsd[f][i+k+n])\";");//squares
		L.add("eval test180 \"?lsd_2 n > 0 & Ef,i i >= 1 & $endsIn2Zeros(i) & $endsIn2Zeros(n) & (Ak k < 2*n => PF[f][i+k] = PF[f][i+k+n])\";");//cubes
		
		L.add("eval test181 \"Ey,w $func(z,x,y,w)\";");
		L.add("eval test182 \"Ez,x,w $func(z,x,y,w)\";");
		L.add("eval test183 \"Ex,y,z $func(z,x,y,w)\";");
		L.add("eval test184 \"Ew,y,x $func(z,x<4 & x>=2,y,w)\";");
		L.add("eval test185 \"Ew,y,z $func(z<5 & z>=3,x,y,w)\";");
		L.add("eval test186 \"Ez,x,y $func(z,x,y,?msd_10 10)\";");
		L.add("eval test187 \"Ez,x,y $func(z,x,y,?msd_10 w=10)\";");
		L.add("eval test188 \"Ez,x,y $func(z,x,y,?msd_10 w=17)\";");
		L.add("eval test189 \"Ez,x,y $func(z,x,y,?msd_10 17)\";");
		L.add("eval test190 \"Ez,x,y $func(z,x,y,17)\";");
		L.add("eval test191 \"Ez,x,y $func(z,x,y,?msd_10 Eb (a = b+1))\";");
		L.add("eval test192 \"Ez,x,y $func(z,x,y,`(?lsd_10 Eb a = b+1))\";");
		L.add("eval test193 \"Ez,w,x $func(z,x,?msd_3 y+2,w)\";");
		L.add("eval test194 \"Ez,w,x $func(z,x,?msd_3 y-1,w)\";");
		L.add("eval test195 \"Ez,x,y $func(z,x,y,?msd_10 a+1)\";");
		
		try{
			prover.dispatchForIntegrationTest("def thueeq \"T[x]=T[y]\";");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		L.add("eval test196 \"$thueeq(x,y)\";");
		L.add("eval test197 \"$thueeq(x,x)\";");
		L.add("eval test198 \"Ax $thueeq(x,x)\";");
		L.add("eval test199 \"Ex $thueeq(x,x)\";");
		L.add("eval test200 \"Ax,y x=y=>$thueeq(x,y)\";");
		L.add("eval test201 \"(Ax,y x=y=>$thueeq(x,y)) <=> Ax $thueeq(x,x)\";");
		
		try {
			PrintWriter out = new PrintWriter("Word Automata Library/T2.txt", "UTF-16");
			out.write("msd_2 msd_2\n0 1\n0 0 -> 0\n1 0 -> 1\n0 1 -> 1\n1 1 -> 0\n1 0\n0 0 -> 1\n1 0 -> 0\n0 1 -> 0\n1 1 -> 1\n");
			out.close();
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		
		L.add("eval test202 \"T2[x][x]=@1\";");
		L.add("eval test203 \"T2[x][x+1]=@0\";");
		L.add("eval test204 \"Ax T2[x][x]=@1\";");
	}
	public void runTestCases() throws Exception{
		loadTestCases();
		int failedTestsCount = 0;
		if(testCases == null || testCases.size() == 0)return;
		for(int i = 0; i < testCases.size();i++){
			TestCase t = testCases.get(i);
			String command = L.get(i);
			System.out.println("\t\t"+command);
			try{
				Automaton res = prover.dispatchForIntegrationTest(command);
				if(res == null && t.result == null)continue;
				if((res == null || t.result == null) || !res.equals(t.result)){
					failedTestsCount++;
					System.out.println("test " + i + " failed!");
				}
			}
			catch(Exception e){
				if(!e.getMessage().equals(t.error)){
					failedTestsCount++;
					System.out.println("test " + i + " failed!");
				}
			}
		}
		if(failedTestsCount == 0){
			System.out.println("all tests completed successfully!");
			//delete all test generated files
			/*try{
				for(int i = 0; i != L.size();++i){
					File file = new File("../Result/test"+i+".txt");
					file.delete();
				}
				File file = new File("../Word Automata Library/T2.txt");
				file.delete();
				file = new File("../Automata Library/");
			}
			catch(Exception e){
				return;
			}*/
		}
		else{
			System.out.println(failedTestsCount + " test cases failed!");
		}
	}
	public void loadTestCases() throws Exception{
		String command;
		testCases = new ArrayList<TestCase>();
		for(int i = 0 ; i < L.size();i++){
			command = L.get(i);
			Automaton M = null;
			String error = "";
			if(new File(directoryAddress+"automaton"+Integer.toString(i)+".txt").isFile()){
				M = new Automaton(directoryAddress+"automaton"+i+".txt");
			}
			else{
				BufferedReader errorReader = new BufferedReader(new InputStreamReader(new FileInputStream(directoryAddress+"error" + Integer.toString(i)+".txt"), "utf-16"));
				//System.out.println(errorReader.readLine());
				String temp;
				boolean flag = false;
				while((temp = errorReader.readLine())!= null){
					if(flag)
						error +=UtilityMethods.newLine() + temp;
					else 
						error += temp;
					flag = true;
				}
				errorReader.close();
			}
			testCases.add(new TestCase(command,M,error));
		}
	}
	public void createTestCases() throws Exception{
		for(int i = 0; i < L.size();i++){
			String command = L.get(i);
			System.out.println(command);
			Automaton res = null;
			String error = null;
			try{
				res = prover.dispatchForIntegrationTest(command);
			}
			catch(Exception e){
				error = e.getMessage();
			}
			testCases.add(new TestCase(command,res,error));
		}
		writeTestCases();
	}
	private void writeTestCases() throws Exception{
		new File(directoryAddress).mkdirs();
		for(int i = 0 ; i < testCases.size();i++){
			TestCase t = testCases.get(i);
			if(t.result != null){
				t.result.write(directoryAddress+"automaton" +Integer.toString(i)+ ".txt");
				//t.result.draw(directoryAddress+"automaton"+Integer.toString(i)+".gv", "\""+t.command+"\"");
			}
			else{
				PrintWriter errorWriter = new PrintWriter(directoryAddress+"error"+Integer.toString(i)+".txt", "UTF-16");
				errorWriter.println(t.error);
				errorWriter.close();
			}
		}
	}
}
