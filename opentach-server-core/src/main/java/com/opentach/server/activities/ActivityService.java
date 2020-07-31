package com.opentach.server.activities;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.activities.IActivityService;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.companies.ContractService;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

public class ActivityService extends UAbstractService implements IActivityService {
	private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);

	public ActivityService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
	}

	@Override
	public EntityResult flatActivities(String cif, Object cgContrato, String cardNumber, Date beginDate, Date endDate, boolean queryPeriods, Vector<Object> vDrivers, int sesionId,
			Vector<String> av) throws Exception {

		try {
			final Pair<Object, Vector<Object>> pair = this.getService(ContractService.class).queryContractAndDrivers(cif, cgContrato, vDrivers, sesionId);
			final Object newCgContrato = pair.getFirst();
			final Vector<Object> newVDrivers = pair.getSecond();
			Object newCif = cif;
			if (newCif == null) {
				newCif = this.getService(ContractService.class).queryCompanyCif(newCgContrato, vDrivers.get(0));
			}

			CheckingTools.failIf((newVDrivers == null) || newVDrivers.isEmpty(), "E_MUST_SPECIFY_DRIVER", new Object[] {});
			CheckingTools.failIf(newCgContrato == null, "M_NO_SE_PUEDE_REALIZAR_ANALISIS", new Object[] {});
			CheckingTools.failIf(newCif == null, "M_NO_SE_PUEDE_REALIZAR_ANALISIS_CIF", new Object[] {});

			final long time = System.currentTimeMillis();
			// ActivitiesAnalyzerInDatabase analyzer = new ActivitiesAnalyzerInDatabase(this.getLocator());
			// EntityResult dbActivities = analyzer.analyze(newCgContrato, cardNumber, newVDrivers, beginDate, endDate, queryPeriods, av, sesionId);
			// ActivityService.logger.error("Time database: {}", System.currentTimeMillis() - time);
			//
			// time = System.currentTimeMillis();
			final ActivitiesAnalyzerInServer serverAnalyzer = ActivitiesAnalyzerInServer.getInstance((IOpentachServerLocator) this.getLocator());
			final EntityResult serverActivities = serverAnalyzer.analyze(newCgContrato, newCif, cardNumber, newVDrivers, beginDate, endDate, queryPeriods, av, sesionId);

			ActivityService.logger.error("Flat activities time server: {}", System.currentTimeMillis() - time);
			return serverActivities;

		} catch (final Exception e) {
			ActivityService.logger.error(null, e);
			throw e;
		}
	}

	@Override
	public EntityResult getConduccion(final Hashtable av, final int sessionID) throws Exception {
		return new OntimizeConnectionTemplate<EntityResult>() {
			@Override
			protected EntityResult doTask(Connection con) throws UException {
				try {
					final TableEntity ent = (TableEntity) ActivityService.this.getEntity("EInformeActivCondSegCond");
					final EntityResult res = ent.query(av, new Vector<Object>(), sessionID);
					return res;
				} catch (final Exception err) {
					throw new UException(err);
				}
			}
		}.execute(this.getConnectionManager(), true);

	}
}
