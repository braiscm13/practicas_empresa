package com.opentach.server.webservice.driverAnalysis;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ServerLauncherServlet;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.Pair;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.webservice.driverAnalysis.beans.DriverNextRestRequest;
import com.opentach.server.webservice.driverAnalysis.beans.DriverNextRestResponse;
import com.opentach.server.webservice.driverAnalysis.beans.NextRest;
import com.opentach.server.webservice.utils.OpentachWSException;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

@WebService(endpointInterface = "com.opentach.server.webservice.driverAnalysis.IDriverNextRestService", serviceName = "driverNextService")
public class DriverNextRestService implements IDriverNextRestService {

	private static final Logger	logger								= LoggerFactory.getLogger(DriverNextRestService.class);

	private static final String	ERROR_UNKNOWED						= "UNKNOWED_ERROR";
	private static final String	ERROR_REQUIRED_FILTERS				= "REQUIRED_FILTERS";
	private static final String	ERROR_USER_INVALID_CREDENTIALS		= "INVALID_CREDENTIALS";
	private static final String	ERROR_USER_REQUIRED_USER_AND_PASS	= "INVALID_CREDENTIALS-REQUIRED_USER_AND_PASS";
	private static final String	ERROR_USER_WITHOUT_COMPANIES		= "INVALID_CREDENTIALS-USER_WITHOUT_COMPANIES_ASSOCIATED";
	private static final String	ERROR_DRIVER_REQUIRED_DRIVER		= "INVALID_DRIVER_CREDENTIALS-REQUIRED_DRIVER";
	private static final String	ERROR_DRIVER_INVALID_CREDENTIALS	= "INVALID_DRIVER_CREDENTIALS";
	private static final String	ERROR_DATES_REQUIRED_PERIOD			= "INVALID_PERIOD-REQUIRED_BEGIND_AND_END_DATE";
	private static final String	ERROR_DATES_INVALID_PERIOD			= "INVALID_PERIOD-NOT_VALID_TIME_PERIOD";
	private static final String	ERROR_ANALIZING						= "ERROR_ANALIZING";
	private static final String	ERROR_RESULT_EMPTY_ANALYSIS			= "EMPTY_ANALYSIS_RESULT";	
	private static final String	ERROR_RESULT_PROCESSING				= "ERROR_PROCESSING_RESULT";

	private static final int	CODE_UNKNOWED_ERROR_CODE			= -1;
	private static final int	CODE_VALIDATING_INPUTS				= -2;
	private static final int	CODE_VALIDATING_INPUTS_USER			= -3;
	private static final int	CODE_VALIDATING_INPUTS_DRIVER		= -4;
	private static final int	CODE_VALIDATING_INPUTS_DATES		= -5;
	private static final int	CODE_ANALIZING						= -6;
	private static final int	CODE_POST_ANALIZING					= -7;

	@Resource
	private WebServiceContext	context;

	/***************************************************************************************************************/
	/************************************************* ANALYZE *****************************************************/
	/***************************************************************************************************************/

	@Override
	public DriverNextRestResponse queryNextRest(final DriverNextRestRequest filters) throws OpentachWSException {
		ServletContext servletContext = (ServletContext) this.context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
		final OpentachServerLocator locator = (OpentachServerLocator) servletContext.getAttribute(ServerLauncherServlet.COM_ONTIMIZE_GUI_LOCATOR_ATTRIBUTE_CONTEXT);
		try {
			if (filters == null) {
				throw new OpentachWSException(DriverNextRestService.CODE_VALIDATING_INPUTS, DriverNextRestService.ERROR_REQUIRED_FILTERS);
			}
			return new OntimizeConnectionTemplate<DriverNextRestResponse>() {
				@Override
				protected DriverNextRestResponse doTask(Connection con) throws UException {
					try {
						return DriverNextRestService.this.queryNextRest(filters, locator, con);
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}
			}.execute(locator, true);
		} catch (OpentachWSException ex) {
			DriverNextRestService.logger.error(null, ex);
			throw ex;
		} catch (Throwable error) {
			DriverNextRestService.logger.error(null, error);
			throw new OpentachWSException(DriverNextRestService.CODE_UNKNOWED_ERROR_CODE, DriverNextRestService.ERROR_UNKNOWED + " " + error.getMessage());
		}
	}

	protected DriverNextRestResponse queryNextRest(DriverNextRestRequest filters, OpentachServerLocator locator, Connection con) throws Exception {
		int sessionId = -1;
		try {
			// Step 1 : Validate user credentials ans start session
			sessionId = this.validateUserAndStartSession(filters.getUser(), filters.getPass(), locator);

			// Step 2: Validate driver
			Pair<String, String> pair = this.validateDriver(filters.getDriver(), locator, con, sessionId);
			String numReq = pair.getFirst();
			String cif = pair.getSecond();

			// Step 3 : Validate period
			this.validateDate(filters.getDate());

			// Step 4 : Analize with input data
			Date result = this.getNextRest(filters, numReq, cif, locator, con, sessionId);

			// Step 6 : Convert to response
			return DriverNextRestService.this.convertResultToAnalysisResponse(result);
		} finally {
			// Step 5: End session
			if (sessionId >= 0) {
				locator.endSession(sessionId);
			}
		}
	}

	/**
	 * Authenticate user/pass in server to operate from this moment under this sessionId.
	 *
	 * @param user
	 * @param pass
	 * @return
	 * @throws OpentachWSException
	 */
	private int validateUserAndStartSession(String user, String pass, OpentachServerLocator locator) throws OpentachWSException {
		if ((user == null) || (pass == null)) {
			throw new OpentachWSException(DriverNextRestService.CODE_VALIDATING_INPUTS_USER, DriverNextRestService.ERROR_USER_REQUIRED_USER_AND_PASS);
		}
		try {
			int startSession = locator.startSessionWS(user, pass, null);
			// if (!IUserData.NIVEL_TDI.equals(locator.getUserData(startSession).getLevel())) {
			// throw new OpentachWSException(DriverAnalysisService.CODE_VALIDATING_INPUTS_USER, DriverAnalysisService.ERROR_USER_INVALID_CREDENTIALS);
			// }
			return startSession;
		} catch (Exception e) {
			DriverNextRestService.logger.error("INVALD_AUTHENTICATION", e);
			throw new OpentachWSException(DriverNextRestService.CODE_VALIDATING_INPUTS_USER, DriverNextRestService.ERROR_USER_INVALID_CREDENTIALS);
		}
	}

	/**
	 * Check from input driver identifier and get NUMREQ or CG_CONTRATO
	 *
	 * @param driver
	 * @param sessionId
	 * @return
	 * @throws OpentachWSException
	 */
	protected Pair<String, String> validateDriver(String driver, OpentachServerLocator locator, Connection con, int sessionId) throws OpentachWSException {
		if (driver == null) {
			throw new OpentachWSException(DriverNextRestService.CODE_VALIDATING_INPUTS_DRIVER, DriverNextRestService.ERROR_DRIVER_REQUIRED_DRIVER);
		}
		try {
			IUserData userData = locator.getUserData(sessionId);
			List<String> cifs = userData.getCompaniesList();
			if ((cifs == null) || cifs.isEmpty()) {
				throw new OpentachWSException(DriverNextRestService.CODE_VALIDATING_INPUTS_USER, DriverNextRestService.ERROR_USER_WITHOUT_COMPANIES);
			}
			// Try to localice the contract that match with this driver, this companies and is active
			TableEntity entity = (TableEntity) locator.getEntityReferenceFromServer("EConductorCont");
			Hashtable<Object, Object> kv = EntityResultTools.keysvalues("IDCONDUCTOR", driver, "CIF", //
					new SearchValue(SearchValue.IN, new Vector<String>(cifs)), //
					"F_BAJA", new SearchValue(SearchValue.NULL, null));
			EntityResult erDrivers = entity.query(kv, EntityResultTools.attributes(OpentachFieldNames.CG_CONTRATO_FIELD, OpentachFieldNames.CIF_FIELD), sessionId, con);
			CheckingTools.checkValidEntityResult(erDrivers, "INVALID_DRIVER_CREDENTIALS-DRIVER_NOT_FOUND", true, true, (Object[]) null);
			String contractId = (String) erDrivers.getRecordValues(0).get(OpentachFieldNames.CG_CONTRATO_FIELD);
			String cif = (String) erDrivers.getRecordValues(0).get(OpentachFieldNames.CIF_FIELD);
			return new Pair<>(contractId, cif);
		} catch (Exception e) {
			DriverNextRestService.logger.error("INVALD_DRIVER", e);
			throw new OpentachWSException(DriverNextRestService.CODE_VALIDATING_INPUTS_DRIVER, DriverNextRestService.ERROR_DRIVER_INVALID_CREDENTIALS);
		}
	}

	/**
	 * Check input date filters
	 *
	 * @param initDate
	 * @param endDate
	 * @throws OpentachWSException
	 */
	protected void validateDate(Date initDate) throws OpentachWSException {
		
		if ((initDate == null)) {
			throw new OpentachWSException(DriverNextRestService.CODE_VALIDATING_INPUTS_DATES, DriverNextRestService.ERROR_DATES_REQUIRED_PERIOD);
		}
		if (initDate.after(new Date())) {
			throw new OpentachWSException(DriverNextRestService.CODE_VALIDATING_INPUTS_DATES, DriverNextRestService.ERROR_DATES_INVALID_PERIOD);
		}
	}

	private Date getNextRest(DriverNextRestRequest filters, String numReq, String cif, OpentachServerLocator locator, Connection con, int sessionId)
			throws OpentachWSException {
		try {
			
			Calendar c = Calendar.getInstance();
			c.setTime(filters.getDate());
			c.set(Calendar.MONTH,filters.getDate().getMonth() -30);
			Date finicio = filters.getDate();
			Date ffin = c.getTime();
		
			Hashtable<String, Object> cv = new Hashtable<String, Object>();
			cv.put(OpentachFieldNames.CG_CONTRATO_FIELD, numReq);
			cv.put(OpentachFieldNames.CIF_FIELD, cif);
			cv.put(OpentachFieldNames.IDCONDUCTOR_FIELD, filters.getDriver());
			cv.put(OpentachFieldNames.FILTERFECINI,finicio);
			cv.put(OpentachFieldNames.FILTERFECFIN,ffin);
			TableEntity entity = (TableEntity) locator.getEntityReferenceFromServer("EInformeInfracNextRest");
			EntityResult erInfractions = entity.query(cv, EntityResultTools.attributes(OpentachFieldNames.CG_CONTRATO_FIELD, OpentachFieldNames.CIF_FIELD), sessionId, con);
			
			if (erInfractions!=null && erInfractions.calculateRecordNumber()>0) {
				Vector vFecIni = (Vector)erInfractions.get("FECHORAINI");
				if (vFecIni.size() ==1) {
					return (Date)vFecIni.get(0);
				}
				else {
					Date factual = (Date)vFecIni.get(0);
					for (int i=1; i< vFecIni.size();i++) {
						Date aux = (Date) vFecIni.get(i);
						if (aux.before(factual)) {
							factual = aux;
						}
					}
					return factual;	
				}
				
			}else {
				throw new OpentachWSException(DriverNextRestService.CODE_ANALIZING, DriverNextRestService.ERROR_ANALIZING);
			}
		
		} catch (Exception ex) {
			DriverNextRestService.logger.error(null, ex);
			throw new OpentachWSException(DriverNextRestService.CODE_ANALIZING, DriverNextRestService.ERROR_ANALIZING);
		}
	}

	protected DriverNextRestResponse convertResultToAnalysisResponse(Date nextDate) throws OpentachWSException {

		try {
			DriverNextRestResponse response = new DriverNextRestResponse();

			NextRest nextRest = new NextRest();
			nextRest.setInit(nextDate);
			response.setNextRestDriver(nextRest);
			return response;
		} catch (Exception ex) {
			DriverNextRestService.logger.error(null, ex);
			throw new OpentachWSException(DriverNextRestService.CODE_POST_ANALIZING, DriverNextRestService.ERROR_RESULT_PROCESSING);
		}
	}
}