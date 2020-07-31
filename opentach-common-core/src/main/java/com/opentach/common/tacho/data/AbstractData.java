package com.opentach.common.tacho.data;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractData {
	public static String UNKNOWN_VEHICLE_NUMBER = "???????????";

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
		String clazz = this.getClass().getName();
		clazz = clazz.substring(clazz.lastIndexOf(".") + 1);
		sb.append(clazz + " >");
		Field[] flds = this.getClass().getFields();

		for (int i = 0; (flds != null) && (i < flds.length); i++) {
			Field fld = flds[i];
			if (Modifier.isPublic(fld.getModifiers()) && !Modifier.isStatic(fld.getModifiers())) {
				Object value;
				try {
					value = fld.get(this);
					if (fld.getType().isArray()) {
						try {
							Object[] values = (Object[]) value;
							sb.append("\n\t" + values.getClass().getName().substring(values.getClass().getName().lastIndexOf(".") + 1));
							for (int j = 0; (values != null) && (j < values.length); j++) {
								try {
									if (values[j] instanceof Date) {
										sb.append("\n\t\t " + df.format((Date) values[j]));
									} else {
										sb.append("\n\t\t " + values[j]);
									}
								} catch (NullPointerException nex) {
								}
							}
						} catch (ClassCastException cex) {

						}
					} else {
						if (value instanceof Date) {
							sb.append("\t " + fld.getName() + " :" + df.format((Date) value));
						} else {
							sb.append("\t " + fld.getName() + " :" + value);
						}
					}
				} catch (NullPointerException nex) {
					// nex.printStackTrace();
				} catch (IllegalArgumentException e) {
					// e.printStackTrace();
				} catch (IllegalAccessException e) {
					// e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Retorna el valor de la matricula sin guiones ni caracteres raros.Unicamente numeros y letras mayusculas.
	 *
	 * @param veh
	 * @return
	 */
	public String getCleanVehicleNumber(String veh) {
		if (veh == null) {
			return AbstractData.UNKNOWN_VEHICLE_NUMBER;
		}
		veh = veh.toUpperCase();
		// Respeto todos los caracteres alfanumericos y las interrogaciones.
		String rtn = veh.replaceAll("[^\\w^\\?]", "");
		try {
			return rtn.substring(0, 11).trim();
		} catch (IndexOutOfBoundsException iobe) {
			if (rtn.trim().length() == 0) {
				return AbstractData.UNKNOWN_VEHICLE_NUMBER;
			}
			return rtn;
		}
	}

}
