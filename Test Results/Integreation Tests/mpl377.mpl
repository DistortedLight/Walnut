# The row vector u denotes the indicator vector of the (singleton)
# set of initial states.
u := Vector[row]([1,0,0,0,0,0,0,0,0,0]);

# In what follows, the M_i_x, for a free variable i and a value x, denotes
# an incidence matrix of the underlying graph of (the automaton of)
# the predicate in the query.
# For every pair of states p and q, the entry M_i_x[p][q] denotes the number of
# transitions with i=x from p to q.

M_length_abc_0 := Matrix([[1,0,0,0,0,0,0,0,0,0],
[0,0,0,1,1,0,0,0,0,0],
[0,0,0,0,0,1,0,0,0,0],
[0,0,0,1,1,0,0,0,0,0],
[0,0,0,1,0,0,0,0,0,0],
[0,0,0,0,0,0,0,1,1,0],
[0,0,0,1,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,1,0,0,0,0],
[0,0,0,0,1,1,0,0,0,0]]);

M_length_abc_1 := Matrix([[0,1,1,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,1,0,0,0,0,1,0,0,0],
[0,1,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,1,0,0,1],
[0,0,0,0,0,0,0,0,0,0],
[0,0,1,0,0,0,0,0,0,0],
[0,1,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0]]);

# The column vector v denotes the indicator vector of the
# set of final states.
v := Vector[column]([1,1,1,1,1,1,1,0,1,1]);

