/*
 * Volvo Group Tachograph Files API Volvo Group Tachograph Files API The version of the OpenAPI document: 1.0.0 NOTE: This class is auto generated by OpenAPI Generator
 * (https://openapi-generator.tech). https://openapi-generator.tech Do not edit the class manually.
 */

package com.opentach.server.remotevehicle.provider.volvo.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * DriverIdObject
 */
@JsonPropertyOrder({ DriverIdObject.JSON_PROPERTY_TACHO_DRIVER_IDENTIFICATION })
public class DriverIdObject {
	public static final String						JSON_PROPERTY_TACHO_DRIVER_IDENTIFICATION	= "tachoDriverIdentification";
	private DriverIdObjectTachoDriverIdentification	tachoDriverIdentification;

	public DriverIdObject tachoDriverIdentification(DriverIdObjectTachoDriverIdentification tachoDriverIdentification) {

		this.tachoDriverIdentification = tachoDriverIdentification;
		return this;
	}

	/**
	 * Get tachoDriverIdentification
	 *
	 * @return tachoDriverIdentification
	 **/
	@JsonProperty(DriverIdObject.JSON_PROPERTY_TACHO_DRIVER_IDENTIFICATION)
	@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
	public DriverIdObjectTachoDriverIdentification getTachoDriverIdentification() {
		return this.tachoDriverIdentification;
	}

	public void setTachoDriverIdentification(DriverIdObjectTachoDriverIdentification tachoDriverIdentification) {
		this.tachoDriverIdentification = tachoDriverIdentification;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}
		DriverIdObject driverIdObject = (DriverIdObject) o;
		return Objects.equals(this.tachoDriverIdentification, driverIdObject.tachoDriverIdentification);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.tachoDriverIdentification);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class DriverIdObject {\n");
		sb.append("    tachoDriverIdentification: ").append(this.toIndentedString(this.tachoDriverIdentification)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}

}
