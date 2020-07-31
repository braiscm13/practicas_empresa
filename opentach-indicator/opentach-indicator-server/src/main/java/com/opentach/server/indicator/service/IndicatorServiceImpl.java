package com.opentach.server.indicator.service;

import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ExceptionTools;
import com.ontimize.jee.common.tools.ListTools;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.jee.common.tools.StringTools;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.opentach.common.indicator.exception.IndicatorException;
import com.opentach.common.indicator.naming.IndicatorNaming;
import com.opentach.common.indicator.result.IIndicatorResult;
import com.opentach.common.indicator.result.IIndicatorResultCompany;
import com.opentach.common.indicator.result.IIndicatorResultEmployee;
import com.opentach.common.indicator.services.IIndicatorService;
import com.opentach.server.indicator.dao.IndicatorDao;
import com.opentach.server.indicator.dao.IndicatorExecutionDao;
import com.opentach.server.indicator.indicator.IIndicator;
import com.opentach.server.indicator.indicator.IIndicatorLocator;
import com.opentach.server.indicator.indicator.IIndicatorSavable;
import com.opentach.server.util.spring.AbstractSpringDelegate;
import com.utilmize.tools.classpath.ClassPathSeeker;

/**
 * The implementation of IIndicatorService.
 */
@Service("IndicatorService")
public class IndicatorServiceImpl extends AbstractSpringDelegate implements IIndicatorService {

	/** The CONSTANT logger */
	private static final Logger			logger	= LoggerFactory.getLogger(IndicatorServiceImpl.class);

	@Autowired
	private IndicatorDao				indicatorDao;
	@Autowired
	private IndicatorExecutionDao		indicatorExecutionDao;

	@Autowired
	private DefaultOntimizeDaoHelper	daoHelper;

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////// INDICATOR METHODS //////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult indicatorQuery(Map<?, ?> keysValues, List<?> attributes) throws IndicatorException {
		return this.daoHelper.query(this.indicatorDao, keysValues, attributes, null, "default");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult indicatorInsert(Map<?, ?> attributes) throws IndicatorException {
		return this.daoHelper.insert(this.indicatorDao, attributes);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult indicatorUpdate(Map<?, ?> attributes, Map<?, ?> keysValues) throws IndicatorException {
		return this.daoHelper.update(this.indicatorDao, attributes, keysValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult indicatorDelete(Map<?, ?> keysValues) throws IndicatorException {
		return this.daoHelper.delete(this.indicatorDao, keysValues);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////// INDICATOR EXECTUION METHODS ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult indicatorExecutionQuery(Map<?, ?> keysValues, List<?> attributes) throws IndicatorException {
		return this.daoHelper.query(this.indicatorExecutionDao, keysValues, attributes, null, "query");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult indicatorExecutionInsert(Map<?, ?> attributes) throws IndicatorException {
		return this.daoHelper.insert(this.indicatorExecutionDao, attributes);
	}

	protected Number getNextExecutionNumber() throws IndicatorException {
		EntityResult res = this.daoHelper.query(this.indicatorExecutionDao, new HashMap(), EntityResultTools.attributes(IndicatorNaming.EXE_NUMBER), "nextExecutionNumber");
		CheckingTools.failIf((res == null) || (res.calculateRecordNumber() != 1), IndicatorException.class, "E_GETTING_NEXT_EXECUTION_NUMBER");
		return (Number) ((List) res.get(IndicatorNaming.EXE_NUMBER)).get(0);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public List<IIndicatorResult> executeIndicator(Object indId, boolean save) throws IndicatorException {
		CheckingTools.failIf(indId == null, IndicatorException.class, "E_REQUIRED_IND_ID");
		IndicatorServiceImpl.logger.info("executeIndicator: {}", indId);

		// Get atomic execution number
		Number executionNumber = this.getNextExecutionNumber();
		IndicatorServiceImpl.logger.debug("executionNumber:{}", executionNumber);
		try {
			// Query Indicator settings ------------------------
			IndicatorServiceImpl.logger.trace("QUERYING_INDICATOR_SETTINGS__IND_ID:{}", indId);
			EntityResult indSettingsRes = this.indicatorQuery(//
					EntityResultTools.keysvalues(IndicatorNaming.IND_ID, indId), //
					EntityResultTools.attributes(IndicatorNaming.IND_NAME, IndicatorNaming.IND_ACTION, IndicatorNaming.IND_PROPERTIES));
			CheckingTools.failIf((indSettingsRes == null) || (indSettingsRes.calculateRecordNumber() != 1), IndicatorException.class, "E_GETTING_INDICATOR_SETTINGS");
			Hashtable indSettings = indSettingsRes.getRecordValues(0);

			// Instantiate Indicator ------------------------------
			IndicatorServiceImpl.logger.trace("INSTANTIATING_INDICATOR__IND_ID:{}  values:{}", indId, indSettings);
			IIndicator instance = this.buildIndicator(indId, indSettings);

			// Launch indicator ------------------------
			IndicatorServiceImpl.logger.trace("LAUNCHING_INDICATOR__IND_ID:{}  values:{}", indId, indSettings);
			List<IIndicatorResult> results = instance.execute(executionNumber);
			// TODO consider to parallelize
			// TODO ThreadPoolExecutor.submit((Runnable) instance);

			// Finally save result (if interesting) ------------------------------------
			if (save) {
				if (instance instanceof IIndicatorSavable) {
					((IIndicatorSavable) instance).save(indId, executionNumber, results);
				} else {
					this.saveIndicatorResults(indId, executionNumber, results);
				}
			}
			return results;
		} catch (Exception err) {
			if (save) {
				//Audit fail
				this.indicatorExecutionInsert(EntityResultTools.keysvalues(//
						IndicatorNaming.IND_ID, indId, //
						IndicatorNaming.EXE_NUMBER, executionNumber, //
						IndicatorNaming.EXE_DATE, new Date(), //
						IndicatorNaming.EXE_ERROR, ExceptionTools.getStackTrace(err) //
						));
			}
			throw err;
		}
	}

	private IIndicator buildIndicator(Object indId, Hashtable indSettings) throws IndicatorException {
		try {
			String setName = (String) indSettings.get(IndicatorNaming.IND_NAME);
			String className = (String) indSettings.get(IndicatorNaming.IND_ACTION);
			String propString = (String) indSettings.get(IndicatorNaming.IND_PROPERTIES);
			Properties prop = new Properties();
			if (!StringTools.isEmpty(propString)) {
				prop.load(new StringReader(propString));
			}

			// Support to multiple constructors
			IIndicator instance = null;
			try {
				instance = (IIndicator) ReflectionTools.newInstance(className, indId, setName, prop);
			} catch (Exception err) {
				try {
					instance = (IIndicator) ReflectionTools.newInstance(className, setName, prop);
				} catch (Exception err2) {
					instance = (IIndicator) ReflectionTools.newInstance(className, prop);
				}
			}
			if ((instance != null) && IIndicatorLocator.class.isAssignableFrom(instance.getClass())) {
				((IIndicatorLocator) instance).setLocator(this.getLocator());
			}

			return instance;
		} catch (Exception err3) {
			throw new IndicatorException("E_INSTANTIATING_INDICATOR", err3);
		}
	}

	private void saveIndicatorResults(Object indId, Number executionNumber, List<IIndicatorResult> results) throws IndicatorException {
		if ((results == null) || results.isEmpty()) {
			IndicatorServiceImpl.logger.debug("NO_RESULTS_TO_SAVE__executionNumber:{} indId:{}", executionNumber, indId);
			return;
		}
		for (IIndicatorResult result : results) {
			this.indicatorExecutionInsert(EntityResultTools.keysvalues(//
					IndicatorNaming.IND_ID, indId, //
					IndicatorNaming.EXE_NUMBER, executionNumber, //
					IndicatorNaming.EXE_DATE, result.getDate(), //
					IndicatorNaming.EXE_VALUE, result.getValue(), //
					IndicatorNaming.COM_ID, result instanceof IIndicatorResultCompany ? ((IIndicatorResultCompany) result).getCompany() : null, //
							IndicatorNaming.DRV_ID, result instanceof IIndicatorResultEmployee ? ((IIndicatorResultEmployee) result).getEmployee() : null//
					));
		}
	}

	// Another utilities ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult indicatorImplementationQuery(Map<?, ?> keysValues, List<?> attributes) throws IndicatorException {
		// Complete this one using getIndicatorImplementations and return ER to use in combo.
		List<String> indicatorClasses = this.getIndicatorImplementations();
		EntityResult res = new EntityResult(Arrays.asList(new String[] { IndicatorNaming.IND_ACTION }));
		indicatorClasses.stream().forEach((className) -> EntityResultTools.fastAddRecord(res, EntityResultTools.keysvalues(IndicatorNaming.IND_ACTION, className)));
		return EntityResultTools.dofilter(res, new Hashtable(keysValues));
	}

	protected List<String> getIndicatorImplementations() throws IndicatorException {
		// Get all classes that implements IIndicator class
		List<List<String>> indicatorClasses = ClassPathSeeker.find((pckgName, file) -> {
			if (!IndicatorServiceImpl.this.isPackageNameCompliant(pckgName) || !file.endsWith(".class")) {
				return null;
			}
			List<String> res = new ArrayList<>();
			try {
				if (file.startsWith("/")) {
					file = file.substring(1);
				}
				String className = file.substring(0, file.length() - 6);
				String fullClassName = pckgName + "." + className;
				Class<?> cl = Class.forName(fullClassName);
				if (IIndicator.class.isAssignableFrom(cl) && !Modifier.isAbstract(cl.getModifiers())) {
					res.add(fullClassName);
				}
			} catch (ClassNotFoundException error) {
				IndicatorServiceImpl.logger.trace(null, error);
			}
			return res.isEmpty() ? null : res;
		});
		List<String> finalList = new ArrayList<String>();
		indicatorClasses.stream().forEach((List<String> list) -> ListTools.ensureAllValues(finalList, list));
		Collections.sort(finalList);
		return finalList;
	}

	private boolean isPackageNameCompliant(String pckg) {
		String pckgName = pckg.toLowerCase();
		return pckgName.contains("tacolab") || pckgName.contains("tacholab") || pckgName.contains("opentach");
	}

}