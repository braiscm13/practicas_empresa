package com.opentach.client.comp.fnc;

import java.util.Stack;

public class NumberDiff extends org.nfunk.jep.function.PostfixMathCommand {

	public NumberDiff() {
		super();
		this.numberOfParameters = 2;
	}

	@Override
	public void run(Stack stack) throws org.nfunk.jep.ParseException {
		try {
			this.checkStack(stack);
			Number ind2 = (Number) stack.pop();
			Number ind1 = (Number) stack.pop();
			Number diff = Long.valueOf(ind2.longValue() - ind1.longValue());
			stack.push(diff);
		} catch (Exception ex) {
			ex.printStackTrace();
			stack.push(null);
		}
	}

}
