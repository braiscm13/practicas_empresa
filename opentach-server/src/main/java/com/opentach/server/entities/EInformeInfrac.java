package com.opentach.server.entities;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.EntityResultUtils;
import com.ontimize.db.TableEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.activities.IInfractionService;
import com.opentach.common.activities.IInfractionService.EngineAnalyzer;
import com.opentach.server.activities.InfractionService;
import com.opentach.server.util.db.FileTableEntity;

public class EInformeInfrac extends FileTableEntity {

	public static final String	DO_QUERY	= "DO_QUERY";

	public EInformeInfrac(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection conn) throws Exception {
		String cgContrato = null;
		
		if (cv.containsKey(EInformeInfrac.DO_QUERY)) {
			return super.query(cv, av, sesionId, conn);
		}
		if (cv.containsKey(IInfractionService.ENGINE_ANALYZER) && (cv.get(IInfractionService.ENGINE_ANALYZER) instanceof String) && ("DEFAULT".equals(cv.get(IInfractionService.ENGINE_ANALYZER))))  {
			cv.put(IInfractionService.ENGINE_ANALYZER,IInfractionService.EngineAnalyzer.DEFAULT);
		}
		
		if (!cv.containsKey(OpentachFieldNames.CG_CONTRATO_FIELD)) {			
			TableEntity ent = (TableEntity) this.getEntityReference("ECifEmpreReq");	
			Hashtable<String,Object> avContrato = new Hashtable<String,Object>();
			avContrato.put(OpentachFieldNames.CIF_FIELD, cv.get(OpentachFieldNames.CIF_FIELD));
			Vector vContratos = new Vector();
			vContratos.add(OpentachFieldNames.CG_CONTRATO_FIELD);
			EntityResult resContratos = ent.query(avContrato, vContratos, sesionId,conn);
			if (resContratos!=null && resContratos.calculateRecordNumber()>0) {
				cgContrato = (String)resContratos.getRecordValues(0).get(OpentachFieldNames.CG_CONTRATO_FIELD);
			}
			
		}else {
			cgContrato = (String) cv.get(OpentachFieldNames.CG_CONTRATO_FIELD);
		}
		boolean queryAllDrivers = cv.containsKey("QUERY_DRIVER");
		Date beginDate = (Date) cv.get(OpentachFieldNames.FILTERFECINI);
		Date endDate = (Date) cv.get(OpentachFieldNames.FILTERFECFIN);
		String cif = (String) cv.get(OpentachFieldNames.CIF_FIELD);
		Vector<Object> drivers = EInformeActivCond.getDrivers(cv.get(OpentachFieldNames.IDCONDUCTOR_FIELD), false);
		drivers = (queryAllDrivers || (drivers.size() == 0)) ? null : drivers;
		EntityResult res =  this.getService(InfractionService.class).analyzeInfractions(cif, cgContrato, beginDate, endDate, drivers,
				(EngineAnalyzer) cv.get(IInfractionService.ENGINE_ANALYZER),
				sesionId, av);
		//calculamos la fecha de la ultima descarga de cada conductor
		
		if (cv.containsKey("F_DESCARGA")) {
			TableEntity ent = (TableEntity) this.getEntityReference("EInformePendDescargaAgente");	
			Hashtable<String, Object> hdescarga = new Hashtable<String, Object>();
			if (drivers==null) {
				drivers=removeRepeated((Vector)res.get("IDCONDUCTOR"));
				
			}
			SearchValue sv = new SearchValue(SearchValue.IN, drivers);
			hdescarga.put(OpentachFieldNames.IDCONDUCTOR_FIELD, sv);
			hdescarga.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
			hdescarga.put(OpentachFieldNames.CIF_FIELD, cif);
			
			Vector<Object> vconsulta = new Vector<Object>();
			vconsulta.add(OpentachFieldNames.IDCONDUCTOR_FIELD);
			vconsulta.add(OpentachFieldNames.CG_CONTRATO_FIELD);
			vconsulta.add(OpentachFieldNames.CIF_FIELD);
			vconsulta.add("DIAS");
			vconsulta.add("F_DESCARGA_DATOS");
			EntityResult resDescarga = ent.query(hdescarga,vconsulta, sesionId,conn);
			
			EntityResultUtils.addColumn(res, "DIAS");
			EntityResultUtils.addColumn(res, "F_DESCARGA_DATOS");
			
			Hashtable<String,Object> claves = new Hashtable<String,Object>();
			int cont = res.calculateRecordNumber();
			
			EntityResult resFinal = new EntityResult(res.getOrderColumns());
			
			for (int i =0; i<cont;i++) {
				Hashtable<String,Object> registro = res.getRecordValues(i);
				
				claves.put(OpentachFieldNames.IDCONDUCTOR_FIELD, registro.get(OpentachFieldNames.IDCONDUCTOR_FIELD));
				claves.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
				claves.put(OpentachFieldNames.CIF_FIELD, cif);
				
				EntityResult resIntermedio = EntityResultTools.dofilter(resDescarga, claves);	
				if (resIntermedio!=null && !resIntermedio.isEmpty()) {
					if (resIntermedio.getRecordValues(0).get("F_DESCARGA_DATOS")!=null) {
					
					registro.put("F_DESCARGA_DATOS", resIntermedio.getRecordValues(0).get("F_DESCARGA_DATOS"));
					}
					registro.put("DIAS", resIntermedio.getRecordValues(0).get("DIAS")==null ? new BigDecimal(0) :resIntermedio.getRecordValues(0).get("DIAS"));
				}
				resFinal.addRecord(registro);
			}

			return resFinal;
		}
		return res;
	}

	public Vector removeRepeated(Vector drivers) {
		Vector vFinal = new Vector();
		for (int i= 0; i<drivers.size();i++) {
			if (!(vFinal.contains(drivers.get(i)))) {
				vFinal.add(drivers.get(i));
			}
		}
		return vFinal;
	}
}
