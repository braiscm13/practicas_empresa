package com.opentach.server.webservice.driverAnalysis.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Input bean received in "DriverAnalysis.queryActivities" WS method.
 */
@XmlAccessorType(XmlAccessType.FIELD)
// @XmlType(propOrder = { "user", "pass", "driver", "initDate", "endDate" })
public class DriverActivityRequest extends DriverAnalysisRequest {

}