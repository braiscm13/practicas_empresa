package com.opentach.server.entities;

import java.lang.reflect.Field;
import java.net.URL;
import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.EntityResultTools.JoinType;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.sessionstatus.AbstractStatusDto;
import com.opentach.common.sessionstatus.DownCenterStatusDto;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.sessionstatus.SessionStatusService;
import com.opentach.server.util.db.OracleTableEntity;

public class ECDOMonitor extends OracleTableEntity {

	private static final Logger	logger			= LoggerFactory.getLogger(ECDOMonitor.class);

	public final static long	DISCONNECT_TIME	= 2 * 60 * 1000;

	public ECDOMonitor(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	protected void readProperties() throws Exception {}

	@Override
	protected void readProperties(Properties prop, URL uRLProp) throws Exception {}

	@Override
	protected void readProperties(String path) throws Exception {}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sessionID, Connection conn) throws Exception {
		this.checkPermissions(sessionID, TableEntity.QUERY_ACTION);
		EntityResult resCDO = ((TransactionalEntity) this.getEntityReference("EUsuariosCDO")).query(new Hashtable(), EntityResultTools.attributes("USUARIO", "PAIS", "PROVINCIA"),
				sessionID, conn);
		EntityResult resStatus = ((OpentachServerLocator) this.locator).getService(SessionStatusService.class).getSessionStatus(sessionID);
		List<AbstractStatusDto> listStatus = (List<AbstractStatusDto>) resStatus.get("STATUS");
		List<String> listUser = new Vector<String>();
		for (AbstractStatusDto dto : listStatus) {
			listUser.add(dto.getUser());
		}
		EntityResult res2 = new EntityResult();
		EntityResultTools.initEntityResult(res2, "STATUS", "USUARIO");
		res2.put("STATUS", listStatus);
		res2.put("USUARIO", listUser);
		res2 = EntityResultTools.doJoin(resCDO, res2, new String[] { "USUARIO" }, JoinType.LEFT);

		List<AbstractStatusDto> status = (List<AbstractStatusDto>) res2.get("STATUS");
		List usuario = (List) res2.get("USUARIO");
		this.fillHoles(status, usuario);

		Collections.sort(status, new Comparator<AbstractStatusDto>() {
			@Override
			public int compare(AbstractStatusDto o1, AbstractStatusDto o2) {
				return o1.getUser().compareTo(o2.getUser());
			}
		});

		this.removeDuplicates(status);
		return this.toEntityResult(status);
	}

	private EntityResult toEntityResult(List<AbstractStatusDto> status) {
		List<Field> allFields = ReflectionTools.getAllFields(status.get(0).getClass());
		Vector<String> fieldNames = new Vector<>(allFields.stream().map(Field::getName).collect(Collectors.toList()));
		// fieldNames.add("STATUS");
		fieldNames.add("connected");
		EntityResult er = new EntityResult(fieldNames);
		AtomicInteger counter = new AtomicInteger(0);
		status.forEach(st -> {
			Hashtable<String, Object> record = new Hashtable<>();
			// record.put("STATUS", st);

			MapTools.safePut(record, "connected",
					st.getPingDate() == null ? null : ((System.currentTimeMillis() - st.getPingDate().getTime()) < ECDOMonitor.DISCONNECT_TIME));

			allFields.forEach(field -> {
				try {
					MapTools.safePut(record, field.getName(), ReflectionTools.getFieldValue(st, field.getName()));
				} catch (Exception error) {
					ECDOMonitor.logger.error(null, error);
				}
			});
			er.addRecord(record, counter.getAndIncrement());
		});
		return er;
	}

	private void fillHoles(List<AbstractStatusDto> status, List usuario) {
		for (int i = 0; i < status.size(); i++) {
			if (status.get(i) == null) {
				DownCenterStatusDto element = new DownCenterStatusDto();
				element.setSourceAddress(null);
				element.setUser((String) usuario.get(i));
				status.set(i, element);
			}
		}
	}

	private void removeDuplicates(List<AbstractStatusDto> status) {
		ListIterator<AbstractStatusDto> listIterator = status.listIterator();
		AbstractStatusDto previous = null;
		while (listIterator.hasNext()) {
			AbstractStatusDto current = listIterator.next();
			if ((previous != null) && (current != null) && current.getUser().equals(previous.getUser())) {
				Date previousPing = previous.getPingDate();
				Date currentPing = current.getPingDate();
				if ((previousPing != null) && (currentPing != null)) {
					if (previousPing.getTime() > currentPing.getTime()) {
						this.deleteCurrent(listIterator);
					} else {
						this.deletePrevious(listIterator);
						previous = current;
					}
				} else if (previousPing == null) {
					this.deletePrevious(listIterator);
					previous = current;
				} else {
					this.deleteCurrent(listIterator);
				}
			} else {
				previous = current;
			}
		}
	}

	private void deleteCurrent(ListIterator<AbstractStatusDto> listIterator) {
		listIterator.remove();
	}


	private void deletePrevious(ListIterator<AbstractStatusDto> listIterator) {
		AbstractStatusDto previous = listIterator.previous();
		if (listIterator.hasPrevious()) {
			previous = listIterator.previous();
			this.deleteCurrent(listIterator);
			if (listIterator.hasNext()) {
				listIterator.next();
			}
		}
	}

	@Override
	public EntityResult update(Hashtable atributosValoresA, Hashtable clavesValoresA, int sesionId, Connection con) throws Exception {
		throw new Exception("NOT_AVAILABLE");
	}

	@Override
	public EntityResult delete(Hashtable keysValues, int sessionId, Connection con) throws Exception {
		throw new Exception("NOT_AVAILABLE");
	}

	@Override
	public EntityResult insert(Hashtable attributesValues, int sessionId, Connection con) throws Exception {
		throw new Exception("NOT_AVAILABLE");
	}
}
