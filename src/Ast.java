import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/* Michael PÉRIN, Verimag / Univ. Grenoble Alpes, december 2017
 *
 * Constructors of the Abstract Syntax Tree of an Algorithm
 */

public abstract class Ast {
	public abstract String toString();
}

class Algorithm extends Ast {
	Sequence sequence;

	Algorithm(Sequence sequence) {
		this.sequence = sequence;
	}

	public String toDot() {
		return "digraph Algorithm{node [shape=circle];" + this.sequence.toDot() + "\n}";
	}

	public String toString() {
		return this.sequence.toString();
	}
}

class Expression extends Ast {
	Ast ast;

	Expression(Ast ast) {
		this.ast = ast;
	}

	public String toString() {
		return ast.toString();
	}
}

class Optional extends Ast {
	Expression opt_expr;

	Optional() {
		this.opt_expr = null;
	}

	Optional(Expression expr) {
		this.opt_expr = expr;
	}

	public String toString() {
		return opt_expr.toString();
	}
}

// OPERATOR

class BinOp extends Ast {
	String operator;
	Ast left_operand;
	Ast right_operand;

	BinOp(Ast l, String o, Ast r) {
		this.operator = o;
		this.left_operand = l;
		this.right_operand = r;
	}

	public String infix_toString() {
		return "(" + this.left_operand + this.operator + this.right_operand + ")";
	}

	public String prefix_toString() {
		return "(" + this.operator + " " + this.left_operand + " " + this.right_operand + ")";
	}

	public String toString() {
		return infix_toString();
	}
}

class UnOp extends Ast {
	String operator;
	Ast operand;

	UnOp(String operator, Ast operand) {
		this.operand = operand;
		this.operator = operator;
	}

	public String toString() {
		return "(" + this.operator + this.operand + ")";
	}

}

// BOOLEAN and INTEGER EXPRESSIONs

// Integer_Expression = IntCst U INTExp

abstract class Integer_Expression extends Ast {
}

class IntCst extends Integer_Expression {
	Integer value;

	IntCst(String input_string) {
		this.value = Integer.parseInt(input_string);
	}

	public String toString() {
		return this.value.toString();
	}
}

class IntExp extends Integer_Expression {
	Expression expr;

	IntExp(Expression expr) {
		this.expr = expr;
	}

	public String toString() {
		return this.expr.toString();
	}
}

// Boolean_Expression = BoolCst U BoolExp

abstract class Boolean_Expression extends Ast {
}

class BoolCst extends Boolean_Expression {
	Boolean bool;

	BoolCst(String input_string) {
		this.bool = Boolean.parseBoolean(input_string);
	}

	public String toString() {
		return this.bool.toString();
	}
}

class BoolExp extends Boolean_Expression {
	Expression expr;

	BoolExp(Expression expr) {
		this.expr = expr;
	}

	public String toString() {
		return this.expr.toString();
	}
}

class Not extends Boolean_Expression {
	Boolean_Expression expr;

	Not(Boolean_Expression expr) {
		this.expr = expr;
	}

	public String toString() {
		return "not(" + this.expr.toString() + ")";
	}
}

// PROGRAM VARIABLES: Var = Identifier U ArrayCell

abstract class Var extends Ast {
}

class Identifier extends Var {
	String identifier;

	Identifier(String identifier) {
		this.identifier = identifier;
	}

	public String toString() {
		return identifier;
	}
}

class ArrayCell extends Var {
	Identifier identifier;
	Indexes indexes;

	ArrayCell(Identifier identifier, Indexes indexes) {
		this.identifier = identifier;
		this.indexes = indexes;
	}

	public String toString() {
		return identifier.toString() + this.indexes.toString();
	}
}

class Indexes extends Ast {
	List<Integer_Expression> indexes;

	Indexes() {
		this.indexes = new LinkedList<Integer_Expression>();
	}

	public Indexes end_with(Integer_Expression e) {
		this.indexes.add(e);
		return (Indexes) this.indexes;
	}

	public boolean isEmpty() {
		if (this.indexes.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public String toString() {
		String output = new String();
		ListIterator<Integer_Expression> expressions = this.indexes.listIterator();
		while (expressions.hasNext()) {
			// PARTIE A COMPLETER
		}
		return output;
	}
}

// TRANSITION: NO OPERATION, ASSIGNMENT, BREAK, RETURN

abstract class Basic_Instruction extends Ast {
	public abstract boolean is_break();

	public abstract boolean is_return();
}

// NO OPERATION = epsilon transition
class NOOP extends Basic_Instruction {
	NOOP() {
	}

	public boolean is_break() {
		return false;
	}

	public boolean is_return() {
		return false;
	}

	public String toString() {
		return "•";
	}
}

class Assignment extends Basic_Instruction {
	Var lhs;
	Expression rhs;

	Assignment(Var var, Expression exp) {
		this.lhs = var;
		this.rhs = exp;
	}

	public boolean is_break() {
		return false;
	}

	public boolean is_return() {
		return false;
	}

	public String toString() {
		return (lhs + ":=" + rhs);
	}
}

class Break extends Basic_Instruction {
	Break() {
	}

	public boolean is_break() {
		return true;
	}

	public boolean is_return() {
		return false;
	}

	public String toString() {
		return "break";
	}
}

class Return extends Basic_Instruction {
	Optional opt_expr;

	Return(Optional opt_expr) {
		this.opt_expr = opt_expr;
	}

	public boolean is_break() {
		return false;
	}

	public boolean is_return() {
		return true;
	}

	public String toString() {
		return "return (" + opt_expr.toString() + ")";
	}
}

// GUARD = the label of TEST transition
class Guard extends Basic_Instruction {
	Boolean_Expression condition;

	Guard(Boolean_Expression condition) {
		this.condition = condition;
	}

	public boolean is_break() {
		return false;
	}

	public boolean is_return() {
		return false;
	}

	public String toDot() {
		return this.condition.toString();
	}

	public String toString() {
		return "?" + this.condition.toString();
	}
}

// Instruction = Transition U IfThenElse U Loop

abstract class Instruction extends Ast {
	public abstract String toDot();

	public abstract void set_exit_state(State exit);
}

class Transition extends Instruction {
	State entry, exit;
	Basic_Instruction basic_inst;

	Transition(State entry, Basic_Instruction basic_inst, State exit) {
		this.entry = entry;
		this.basic_inst = basic_inst;
		this.exit = exit;
	}

	public void set_exit_state(State exit) {
		if (basic_inst.is_break() || basic_inst.is_return()) {
		} else {
			this.exit = exit;
		}
	}

	public String toDot() {
		/*
		 * THE DOT LINE entry -> exit [label="instruction"]; PRODUCES (entry)
		 * ---instruction---> (entry)
		 */
		return ("\n" + this.entry + "->" + this.exit + "[label=\"" + basic_inst.toString() + "\"];");
	}

	public String toString() {
		String output = new String();
		output += "\n";
		output += entry + ": ";
		output += this.basic_inst.toString();
		// output += " : " + exit ;
		return output;
	}
}

// SEQUENCEß

class Sequence extends Ast {
	List<Instruction> sequence;
	State sequence_exit;
	// with OR without EPSILON TRANSITION
	boolean with_epsilon = true;

	Sequence(State entry, State exit) {
		this.sequence_exit = exit;
		this.sequence = new LinkedList<Instruction>();
		// (***) Any sequence much ends with an epsilon transition joining the exit
		// point
		// doing the Basic_Instruction no_operation
		if (with_epsilon) {
			this.sequence.add(new Transition(entry, new NOOP(), exit));
		}
	}

	public Sequence begin_with(Instruction inst) {
		this.sequence.add(0, inst);
		return this;
	}

	// (***) YOU CAN AVOID THE GENERATION OF EPSILON TRANSITION
	// BY CHANGING THE EXIT STATE OF THE LAST TRANSITON
	public void update_last_transition_exit_state() {
		// PARTIE A COMPLETER
	}

	public void set_exit_state(State exit) {
		if (with_epsilon == false) {
			this.sequence_exit = exit;
			update_last_transition_exit_state();
		}
	}

	public String toDot() {
		String output = new String();
		ListIterator<Instruction> transitions = this.sequence.listIterator();
		while (transitions.hasNext()) {
			output += transitions.next().toDot();
		}
		return output;
	}

	public String toString() {
		String output = new String();
		ListIterator<Instruction> transitions = this.sequence.listIterator();
		while (transitions.hasNext()) {
			output += transitions.next().toString() + ";";
		}
		output += "\n" + sequence_exit + ":";
		return output;
	}
}

// BLOCK

class Block extends Ast {
	Sequence sequence;

	Block(Sequence sequence) {
		this.sequence = sequence;
	}

	public void begin_with(Instruction inst) {
		this.sequence.begin_with(inst);
	}

	public void set_exit_state(State exit) {
		this.sequence.set_exit_state(exit);
	}

	public String toDot() {
		return sequence.toDot();
	}

	public String toString() {
		String output = new String();
		output += "{" + this.sequence + "\n}";
		return output;
	}
}

// IF THEN ELSE

class IfThenElse extends Instruction {
	State entry;
	Boolean_Expression condition;
	Block then_block;
	Block else_block;
	Transition transition_condition_true;
	Transition transition_condition_false;

	IfThenElse(State entry, Boolean_Expression condition, State then_entry, Block then_block, State else_entry,
			Block else_block) {
		this.entry = entry;
		this.condition = condition;
		this.then_block = then_block;
		this.then_block.begin_with(new Transition(entry, new Guard(condition), then_entry));
		this.else_block = else_block;
		// PARTIE A COMPLETER
	}

	public void set_exit_state(State exit) {
		this.then_block.set_exit_state(exit);
		this.else_block.set_exit_state(exit);
	}

	public String toDot() {
		String output = new String();
		output += then_block.toDot();
		output += else_block.toDot();
		return output;
	}

	public String toString() {
		String output = new String();
		output += "\n";
		output += entry + ": ";
		output += "if (" + condition.toString() + ")";
		output += "\nthen\n" + then_block.toString();
		output += "\nelse\n" + else_block.toString();
		return output;
	}
}

// LOOP

class Loop extends Instruction {
	Block block;

	Loop(Block block) {
		this.block = block;
	}

	public void set_exit_state(State exit) { // TODO
		// this.block.set_break_state(exit)
	}

	public String toDot() {
		return this.block.toDot();
	}

	public String toString() {
		String output = new String();
		output += "\n";
		output += "loop" + this.block.toString();
		return output;
	}
}
