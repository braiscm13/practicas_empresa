package com.opentach.client.modules.chart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JDialog;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;
import com.opentach.common.util.DateUtil;

public class IMGraficaCondCertifAct28 extends IMGraficaCondPrimafrio {

	private OpentachClientLocator	ocl;

	private Form					formInsertV	= null;
	private JDialog					dialogInsertV	= null;
	private Form					FMatriculaV	= null;
	private JDialog					JMatriculaV	= null;
	private Button					bGenTemplate;

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();

		DateDataField cf = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
		if (cf != null) {
			cf.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChanged(ValueEvent arg0) {

					IMGraficaCondCertifAct28.this.setDateInterval();

				}
			});
		}

		Button bMatriculas = f.getButton("matriculas_used");
		if (bMatriculas != null) {
			bMatriculas.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					EntityResult res = IMGraficaCondCertifAct28.this.chartwpp.getEntityData("EActividades");
					if (res != null) {
						Vector<String> v = (Vector<String>) res.get("MATRICULA");
						if ((v == null) || (v.size() == 0)) {
							IMGraficaCondCertifAct28.this.managedForm.message("M_NO_HAY_MATRICULAS", Form.MESSAGE);
						} else {

							EntityResult resMatr = new EntityResult();
							Vector<Object> vFin = new Vector<Object>();
							Hashtable<String, Object> av = new Hashtable<String, Object>();
							for (int i = 0; i < v.size(); i++) {
								if ((v.get(i) != null) && !vFin.contains(v.get(i))) {
									av = new Hashtable<String, Object>();
									av.put("MATRICULA", (v.get(i)));
									vFin.add(v.get(i));
									resMatr.addRecord(av);
								}
							}

							if (IMGraficaCondCertifAct28.this.JMatriculaV == null) {
								IMGraficaCondCertifAct28.this.FMatriculaV = IMGraficaCondCertifAct28.this.formManager
										.getFormCopy("formGraficaVehiculos.xml");

								IMGraficaCondCertifAct28.this.JMatriculaV = IMGraficaCondCertifAct28.this.FMatriculaV.putInModalDialog();
								IMGraficaCondCertifAct28.this.JMatriculaV.setTitle(ApplicationManager.getTranslation("GRAFICA_COND"));
							}

							IMGraficaCondCertifAct28.this.FMatriculaV.getInteractionManager().setUpdateMode();

							Table tbIndef = (Table) IMGraficaCondCertifAct28.this.FMatriculaV.getDataFieldReference("EGraficaVehiculos");
							tbIndef.setValue(resMatr);

							IMGraficaCondCertifAct28.this.JMatriculaV.setVisible(true);
						}

					} else {
						IMGraficaCondCertifAct28.this.managedForm.message("M_NO_DATA", Form.INFORMATION_MESSAGE);
					}
				}
			});

		}

		Button bInformeCAP = f.getButton("btnInformeCAP");
		if (bInformeCAP != null) {
			bInformeCAP.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (IMGraficaCondCertifAct28.this.checkRequiredVisibleDataFields(true)) {

						DateFormat datef = new SimpleDateFormat("dd/MM/yyyy HH:mm");
						EntityResult res = IMGraficaCondCertifAct28.this.chartwpp.getEntityData("EActividades");

						EntityResult resPeriodos = IMGraficaCondCertifAct28.this.chartwpp.getEntityData("EPeriodosTrabajo");

						if ((res == null) || ((res != null) && (res.calculateRecordNumber() == 0) && (res.getCode() == EntityResult.OPERATION_SUCCESSFUL))) {
							IMGraficaCondCertifAct28.this.managedForm.message("M_NO_HAY_ACTIVIDADES", Form.MESSAGE);
							return;
						}

						EntityResult resultado = new EntityResult();
						Hashtable<String, Object> av = new Hashtable<String, Object>();
						for (int i = 0; i < res.calculateRecordNumber(); i++) {

							av = res.getRecordValues(i);

							if ("INDEFINIDA".equals(av.get("DSCR_ACT"))
									|| (!("CONDUCCION".equals(av.get("DSCR_ACT"))) && ((av.get("ORIGEN") != null) && ((String) av.get("ORIGEN"))
											.startsWith("M")))) {

								Date dini = (Date) av.get("FECINI");
								Date dfin = (Date) av.get("FECFIN");

								// si es indefinida
								if ("INDEFINIDA".equals(av.get("DSCR_ACT"))) {
									long minutos = (dfin.getTime() - dini.getTime()) / 1000 / 60;

									// si es indefinida y no termina a las 00:00
									// no se hace nada
									if (!((dfin.getHours() == 0) && (dfin.getMinutes() == 0) && (dfin.getSeconds() == 0))) {

									} else {
										// si termina a las 00:00h hay que ver
										// si continua al d�a siguiente
										long sumMinutos = minutos;
										int j = i + 1;
										Date finiDef = (Date) av.get("FECINI");
										Date ffinDef = (Date) av.get("FECFIN");
										for (j = i + 1; j < res.calculateRecordNumber(); j++) {

											Hashtable<String, Object> avPeriodosSig = res.getRecordValues(j);
											Date diniSig = (Date) avPeriodosSig.get("FECINI");
											Date dfinSig = (Date) avPeriodosSig.get("FECFIN");
											long minutosSig = (dfinSig.getTime() - diniSig.getTime()) / 1000 / 60;
											if ("INDEFINIDA".equals(avPeriodosSig.get("DSCR_ACT"))
													&& ((diniSig.getHours() == 0) && (diniSig.getMinutes() == 0) && (diniSig.getSeconds() == 0))) {
												// continua al d�a siguiente y
												// dura 24horas
												if (1440 == minutosSig) {
													sumMinutos += minutosSig;
													ffinDef = dfinSig;
												} else {
													// continua al d�a siguiente
													// y pero no dura 24horas
													sumMinutos += minutosSig;
													if (sumMinutos >= 1440) {
														ffinDef = dfinSig;
														av.put("FECINI_INFO", datef.format(finiDef));
														av.put("FECFIN_INFO", datef.format(ffinDef));
														av.put("FECINI", finiDef);
														av.put("FECFIN", ffinDef);
														av.put("MINUTOS", sumMinutos);
														resultado.addRecord(av);
													}
													break;
												}

											} else { // no continua al d�a
												// siguiente, as� que la
												// a�ado sin m�s
												av.put("FECINI_INFO", datef.format(finiDef));
												av.put("FECFIN_INFO", datef.format(ffinDef));
												av.put("FECINI", finiDef);
												av.put("FECFIN", ffinDef);
												av.put("MINUTOS", sumMinutos);
												resultado.addRecord(av);
												j--;
												break;

											}
										}
										i = j;
									}

								} else { // si no es indefinida

									long minutos = (dfin.getTime() - dini.getTime()) / 1000 / 60;

									// si es manual y no termina a las 00:00
									int periodoRecordNumber = resPeriodos.calculateRecordNumber();
									if (!((dfin.getHours() == 0) && (dfin.getMinutes() == 0) && (dfin.getSeconds() == 0))) {
										// tengo que ver si viene precedida de
										// una indefinida
										Hashtable<String, Object> registroRes = resultado.getRecordValues(0);
										if ((registroRes != null) && registroRes.containsKey("MINUTOS")) {
											Date dindeffin = (Date) registroRes.get("FECFIN");

											if (((dini.getTime() - dindeffin.getTime()) < 60000)
													&& (((Long) registroRes.get("MINUTOS")).longValue() >= 1440)
													&& "INDEFINIDA".equals(registroRes.get("DSCR_ACT"))) {
												av.put("FECINI_INFO", datef.format(dini));
												av.put("FECFIN_INFO", datef.format(dfin));
												av.put("FECINI", dini);
												av.put("FECFIN", dfin);
												av.put("MINUTOS", minutos);
												// busco el periodo
												boolean encontrado = false;
												for (int k = 0; k < periodoRecordNumber; k++) {
													Hashtable<String, Object> avPeriodos = resPeriodos.getRecordValues(k);
													Date dPeriodo = (Date) avPeriodos.get("FECINI");
													dPeriodo.setSeconds(0);
													if (dPeriodo.equals(dini)) {
														av.put("PERIODO", avPeriodos.get("DSCRTIPO") != null ? avPeriodos.get("DSCRTIPO") : "");
														av.put("FECINI_INFO", datef.format(dini) + " -  "
																+ (avPeriodos.get("DSCRTIPO") != null ? avPeriodos.get("DSCRTIPO") : ""));
														av.put("FECINI", dini);
														encontrado = true;
														int g = 1;
														while (avPeriodos.get("FECINI").equals((resPeriodos.getRecordValues(k + g)).get("FECINI"))) {
															g++;
														}
														k = k + g;
														avPeriodos = resPeriodos.getRecordValues(k);
														dPeriodo = (Date) avPeriodos.get("FECINI");
														dPeriodo.setSeconds(0);
														if (dPeriodo.equals(dfin)) {
															av.put("PERIODO", avPeriodos.get("DSCRTIPO") != null ? avPeriodos.get("DSCRTIPO") : "");
															av.put("FECFIN_INFO", datef.format(dfin) + " -  "
																	+ (avPeriodos.get("DSCRTIPO") != null ? avPeriodos.get("DSCRTIPO") : ""));
															av.put("FECFIN", dfin);
														}
													}
													if (!encontrado && dPeriodo.equals(dfin)) {
														av.put("PERIODO", avPeriodos.get("DSCRTIPO") != null ? avPeriodos.get("DSCRTIPO") : "");
														av.put("FECFIN_INFO", datef.format(dfin) + " -  "
																+ (avPeriodos.get("DSCRTIPO") != null ? avPeriodos.get("DSCRTIPO") : ""));
														av.put("FECFIN", dfin);
														encontrado = true;
													}
													if (encontrado) {
														break;
													}
												}
												resultado.addRecord(av);
											}
										}

									} else {
										// si termina a las 00:00h hay que ver
										// si continua al d�a siguiente
										long sumMinutos = minutos;
										int j = i + 1;
										Date finiDef = (Date) av.get("FECINI");
										Date ffinDef = (Date) av.get("FECFIN");

										for (j = i + 1; j < res.calculateRecordNumber(); j++) {

											Hashtable avPeriodosSig = res.getRecordValues(j);
											Date diniSig = (Date) avPeriodosSig.get("FECINI");
											Date dfinSig = (Date) avPeriodosSig.get("FECFIN");
											long minutosSig = ((dfinSig.getTime() - diniSig.getTime()) / 1000 / 60);

											if (av.get("DSCR_ACT").equals(avPeriodosSig.get("DSCR_ACT"))
													&& av.get("ORIGEN").equals(avPeriodosSig.get("ORIGEN"))
													&& ((diniSig.getHours() == 0) && (diniSig.getMinutes() == 0) && (diniSig.getSeconds() == 0))) {
												// continua al d�a siguiente y
												// dura 24horas
												if (1440 == minutosSig) {
													sumMinutos += minutosSig;
													ffinDef = dfinSig;
												} else {
													// continua al d�a siguiente
													// y pero no dura 24horas
													sumMinutos += minutosSig;
													ffinDef = dfinSig;
													// si el total es de m�s de
													// 24horas lo a�ado
													if (sumMinutos >= 1440) {
														av.put("FECINI_INFO", datef.format(finiDef));
														av.put("FECFIN_INFO", datef.format(ffinDef));
														av.put("FECINI", finiDef);
														av.put("FECFIN", ffinDef);
														av.put("MINUTOS", sumMinutos);
														// busco el periodo
														boolean encontrado = false;
														for (int k = 0; k < periodoRecordNumber; k++) {
															Hashtable avPeriodos = resPeriodos.getRecordValues(k);
															Date dPeriodo = (Date) avPeriodos.get("FECINI");
															dPeriodo.setSeconds(0);
															if (dPeriodo.equals(finiDef)) {
																av.put("PERIODO", avPeriodos.get("DSCRTIPO") != null ? avPeriodos.get("DSCRTIPO")
																		: "");
																av.put("FECINI_INFO", datef.format(dini) + " -  "
																		+ (avPeriodos.get("DSCRTIPO") != null ? avPeriodos.get("DSCRTIPO") : ""));
																av.put("FECINI", dini);
																encontrado = true;

																int g = 1;
																while (avPeriodos.get("FECINI").equals(
																		(resPeriodos.getRecordValues(k + g)).get("FECINI"))) {
																	g++;
																}
																k = k + g;
																if (k >= periodoRecordNumber) {
																	break;
																}
																avPeriodos = resPeriodos.getRecordValues(k);
																dPeriodo = (Date) avPeriodos.get("FECINI");
																dPeriodo.setSeconds(0);
																if (dPeriodo.equals(ffinDef)) {
																	av.put("PERIODO", avPeriodos.get("DSCRTIPO") != null ? avPeriodos.get("DSCRTIPO")
																			: "");
																	av.put("FECFIN_INFO",
																			datef.format(ffinDef)
																			+ " -  "
																			+ (avPeriodos.get("DSCRTIPO") != null ? avPeriodos
																					.get("DSCRTIPO") : ""));
																	av.put("FECFIN", ffinDef);
																}
															}
															if (!encontrado && dPeriodo.equals(ffinDef)) {
																av.put("PERIODO", avPeriodos.get("DSCRTIPO") != null ? avPeriodos.get("DSCRTIPO")
																		: "");
																av.put("FECFIN_INFO", datef.format(ffinDef) + " -  "
																		+ (avPeriodos.get("DSCRTIPO") != null ? avPeriodos.get("DSCRTIPO") : ""));
																av.put("FECFIN", ffinDef);
																encontrado = true;
															}
															if (encontrado) {
																break;
															}
														}
														resultado.addRecord(av);
														break;
													} else {
														break;
													}
												}

											} else { // no continua al d�a
												// siguiente, tengo que
												// ver si hay una
												// indefinida de 24h
												// despu�s
												if ("INDEFINIDA".equals(avPeriodosSig.get("DSCR_ACT")) && (1440 == minutosSig)) {
													av.put("FECINI_INFO", datef.format(finiDef));
													av.put("FECFIN_INFO", datef.format(ffinDef));
													av.put("FECINI", finiDef);
													av.put("FECFIN", ffinDef);
													av.put("MINUTOS", sumMinutos);
													// busco el periodo
													boolean encontrado = false;
													for (int k = 0; k < periodoRecordNumber; k++) {
														Hashtable<String, Object> avPeriodos = resPeriodos.getRecordValues(k);
														Date dPeriodo = (Date) avPeriodos.get("FECINI");
														dPeriodo.setSeconds(0);
														if (dPeriodo.equals(finiDef)) {
															av.put("PERIODO", avPeriodos.get("DSCRTIPO") != null ? avPeriodos.get("DSCRTIPO") : "");
															av.put("FECINI_INFO", datef.format(dini) + " -  "
																	+ (avPeriodos.get("DSCRTIPO") != null ? avPeriodos.get("DSCRTIPO") : ""));
															av.put("FECINI", dini);
															encontrado = true;
														}
														if (dPeriodo.equals(ffinDef)) {
															av.put("PERIODO", avPeriodos.get("DSCRTIPO") != null ? avPeriodos.get("DSCRTIPO") : "");
															av.put("FECFIN_INFO", datef.format(dfin) + " -  "
																	+ (avPeriodos.get("DSCRTIPO") != null ? avPeriodos.get("DSCRTIPO") : ""));
															av.put("FECFIN", dfin);

															encontrado = true;
														}
														if (encontrado) {
															break;
														}
													}
													resultado.addRecord(av);
													j--;
													break;

												}
											}
										}
										i = j;
									}
								}

							}
						}
						resultado = EntityResultTools.doSort(resultado, "FECINI");

						if (IMGraficaCondCertifAct28.this.dialogInsertV == null) {
							IMGraficaCondCertifAct28.this.formInsertV = IMGraficaCondCertifAct28.this.formManager
									.getFormCopy("formGraficaCondIndef.xml");
							IMGraficaCondCertifAct28.this.dialogInsertV = IMGraficaCondCertifAct28.this.formInsertV.putInModalDialog();
							IMGraficaCondCertifAct28.this.dialogInsertV.setTitle(ApplicationManager.getTranslation("GRAFICA_COND"));
						}

						IMGraficaCondCertifAct28.this.formInsertV.getInteractionManager().setUpdateMode();
						if (IMGraficaCondCertifAct28.this.managedForm.getDataFieldValue("CIF_CERTIFICADO") != null) {
							IMGraficaCondCertifAct28.this.formInsertV.setDataFieldValue("CIF",
									IMGraficaCondCertifAct28.this.managedForm.getDataFieldValue("CIF_CERTIFICADO"));
						} else {
							IMGraficaCondCertifAct28.this.formInsertV.setDataFieldValue("CIF",
									IMGraficaCondCertifAct28.this.managedForm.getDataFieldValue("CIF"));
						}
						IMGraficaCondCertifAct28.this.formInsertV.setDataFieldValue("CG_CONTRATO",
								IMGraficaCondCertifAct28.this.managedForm.getDataFieldValue("CG_CONTRATO"));
						IMGraficaCondCertifAct28.this.formInsertV.setDataFieldValue("IDCONDUCTOR",
								IMGraficaCondCertifAct28.this.managedForm.getDataFieldValue("IDCONDUCTOR"));


						// a�ado el firmante
						IMGraficaCondCertifAct28.this.formInsertV.setDataFieldValue("NIF",
								IMGraficaCondCertifAct28.this.managedForm.getDataFieldValue("NIF"));
						IMGraficaCondCertifAct28.this.formInsertV.setDataFieldValue("FECHA",
								IMGraficaCondCertifAct28.this.managedForm.getDataFieldValue("FILTERFECFIN"));

						Table tbIndef = (Table) IMGraficaCondCertifAct28.this.formInsertV.getDataFieldReference("EInformeIndef");
						if (tbIndef != null) {
							tbIndef.setValue(resultado);
						}
						IMGraficaCondCertifAct28.this.dialogInsertV.setVisible(true);
					}
				}
			});
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		this.managedForm.getButton("matriculas_used").setEnabled(true);
		this.managedForm.getButton("btnInformeCAP").setEnabled(true);
		try {
			IUserData ud = this.ocl.getUserData();
			this.managedForm.setDataFieldValue("NOMBRE_FIRMA", ud.getNfirmante());
			this.managedForm.setDataFieldValue("APELLIDOS_FIRMA", ud.getAfirmante());
			this.managedForm.setDataFieldValue("CARGO", ud.getCargo());
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.updateChainStatus("CG_CONTRATO", this.managedForm.getDataFieldValue("CG_CONTRATO") == null ? true : false);
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (this.bGenTemplate != null) {
			this.bGenTemplate.setEnabled(true);
		}
	}

	protected void setDateInterval() {
		try {
			DateDataField cf = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
			Date d = (Date) cf.getDateValue();
			DateDataField ci = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECINI);
			ci.setValue(DateUtil.addDays(d, -28));
		} catch (Exception cex) {
		}
	}
}