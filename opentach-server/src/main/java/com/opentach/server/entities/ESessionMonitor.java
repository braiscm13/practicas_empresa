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
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.sessionstatus.AbstractStatusDto;
import com.opentach.common.sessionstatus.DownCenterStatusDto;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.sessionstatus.SessionStatusService;
import com.opentach.server.util.db.OracleTableEntity;

public class ESessionMonitor extends OracleTableEntity {

	private static final Logger logger = LoggerFactory.getLogger(ESessionMonitor.class);

	public ESessionMonitor(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	protected void readProperties() throws Exception {}

	@Override
	protected void readProperties(Properties prop, URL uRLProp) throws Exception {}

	@Override
	protected void readProperties(String path) throws Exception {}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sessionID) throws Exception {
		return this.query(cv, v, sessionID, null);

	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sessionID, Connection conn) throws Exception {
		this.checkPermissions(sessionID, TableEntity.QUERY_ACTION);
		EntityResult resStatus = ((OpentachServerLocator) this.locator).getService(SessionStatusService.class).getSessionStatus(sessionID);
		List<AbstractStatusDto> status = (List<AbstractStatusDto>) resStatus.get("STATUS");

		Collections.sort(status, new Comparator<AbstractStatusDto>() {
			@Override
			public int compare(AbstractStatusDto o1, AbstractStatusDto o2) {
				int compareTo = o1.getUser().compareTo(o2.getUser());
				if (compareTo == 0) {
					return o1.getSourceAddress().compareTo(o2.getSourceAddress());
				}
				return compareTo;
			}
		});

		this.removeDuplicates(status);
		return this.toEntityResult(status);
	}

	private EntityResult toEntityResult(List<AbstractStatusDto> status) {
		List<Field> allFields = ReflectionTools.getAllFields(AbstractStatusDto.class);
		Vector<String> fieldNames = new Vector<>(allFields.stream().map(Field::getName).collect(Collectors.toList()));
		// fieldNames.add("STATUS");
		fieldNames.add("connected");
		EntityResult er = new EntityResult(fieldNames);
		AtomicInteger counter = new AtomicInteger(0);
		status.forEach(st -> {
			Hashtable<String, Object> record = new Hashtable<>();
			// record.put("STATUS", st);

			MapTools.safePut(record, "connected", st.getPingDate() == null ? null : ((System.currentTimeMillis() - st.getPingDate().getTime()) < ECDOMonitor.DISCONNECT_TIME));

			allFields.forEach(field -> {
				try {
					MapTools.safePut(record, field.getName(), ReflectionTools.getFieldValue(st, field.getName()));
				} catch (Exception error) {
					ESessionMonitor.logger.error(null, error);
				}
			});
			er.addRecord(record, counter.getAndIncrement());
		});
		return er;
	}

	private void removeDuplicates(List<AbstractStatusDto> status) {
		ListIterator<AbstractStatusDto> listIterator = status.listIterator();
		AbstractStatusDto previous = null;
		while (listIterator.hasNext()) {
			AbstractStatusDto current = listIterator.next();
			if (current instanceof DownCenterStatusDto) {
				this.deleteCurrent(listIterator);
			} else if ((previous != null) && (current != null) && current.getUser().equals(previous.getUser()) && current.getSourceAddress().equals(previous.getSourceAddress())) {
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
