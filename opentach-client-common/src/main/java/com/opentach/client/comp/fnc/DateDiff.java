package com.opentach.client.comp.fnc;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Stack;

public class DateDiff extends org.nfunk.jep.function.PostfixMathCommand {
	private final String	formato;

	public DateDiff(String formato) {
		super();
		this.numberOfParameters = 2;
		this.formato = formato;
	}

	@Override
	public void run(Stack stack) throws org.nfunk.jep.ParseException {
		try {
			this.checkStack(stack);
			Date ind2 = (Date) stack.pop();
			Date ind1 = (Date) stack.pop();
			String ind3 = this.formato;
			String datedif = "";
			long diff = 0;
			if (ind3 != null) {
				diff = ind2.getTime() - ind1.getTime();
				diff = diff / (1000 * 60); // Minutos restantes.
				datedif += this.parsearTiempoDisponible((int) diff);
			}
			stack.push(datedif);
		} catch (Exception ex) {
			ex.printStackTrace();
			stack.push(null);
		}

	}

	public String getDif(Timestamp from, Timestamp to) {
		long diff = to.getTime() - from.getTime();
		return this.parsearTiempoDisponible((int) (diff / (1000 * 60)));

	}

	private String parsearTiempoDisponible(int minutos) {
		String resultado = "";
		boolean neg = (minutos < 0);
		minutos = Math.abs(minutos);
		int h = minutos / 60;
		int m = minutos % 60;
		String tH;
		if (h <= 9) {
			tH = "0" + h;
		} else {
			tH = String.valueOf(h);
		}
		String tM;
		if (m <= 9) {
			tM = "0" + m;
		} else {
			tM = String.valueOf(m);
		}
		if (neg) {
			resultado = "-" + tH + ":" + tM;
		} else {
			resultado = tH + ":" + tM;
		}
		return resultado;
	}

}
