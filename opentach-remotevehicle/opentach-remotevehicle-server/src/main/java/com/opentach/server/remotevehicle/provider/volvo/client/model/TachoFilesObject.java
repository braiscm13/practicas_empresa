/*
 * Volvo Group Tachograph Files API Volvo Group Tachograph Files API The version of the OpenAPI document: 1.0.0 NOTE: This class is auto generated by OpenAPI Generator
 * (https://openapi-generator.tech). https://openapi-generator.tech Do not edit the class manually.
 */

package com.opentach.server.remotevehicle.provider.volvo.client.model;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * TachoFilesObject
 */
@JsonPropertyOrder({ TachoFilesObject.JSON_PROPERTY_FILENAME, TachoFilesObject.JSON_PROPERTY_RECEIVED_DATE_TIME, TachoFilesObject.JSON_PROPERTY_VIN, TachoFilesObject.JSON_PROPERTY_TACHO_GEN, TachoFilesObject.JSON_PROPERTY_FILE_CONTENT })
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2020-06-05T08:01:01.728+02:00[Europe/Berlin]")
public class TachoFilesObject {
	public static final String	JSON_PROPERTY_FILENAME				= "filename";
	private String				filename;

	public static final String	JSON_PROPERTY_RECEIVED_DATE_TIME	= "receivedDateTime";
	private Date				receivedDateTime;

	public static final String	JSON_PROPERTY_VIN					= "vin";
	private String				vin;

	public static final String	JSON_PROPERTY_TACHO_GEN				= "tachoGen";
	private String				tachoGen;

	public static final String	JSON_PROPERTY_FILE_CONTENT			= "fileContent";
	private byte[]				fileContent;

	public TachoFilesObject filename(String filename) {

		this.filename = filename;
		return this;
	}

	/**
	 * Name of file
	 *
	 * @return filename
	 **/
	@JsonProperty(TachoFilesObject.JSON_PROPERTY_FILENAME)
	@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public TachoFilesObject receivedDateTime(Date receivedDateTime) {

		this.receivedDateTime = receivedDateTime;
		return this;
	}

	/**
	 * Reception at Server. To be used for handling of \&quot;more data available\&quot;
	 *
	 * @return receivedDateTime
	 **/
	@JsonProperty(TachoFilesObject.JSON_PROPERTY_RECEIVED_DATE_TIME)
	@JsonInclude(value = JsonInclude.Include.ALWAYS)
	public Date getReceivedDateTime() {
		return this.receivedDateTime;
	}

	public void setReceivedDateTime(Date receivedDateTime) {
		this.receivedDateTime = receivedDateTime;
	}

	public TachoFilesObject vin(String vin) {

		this.vin = vin;
		return this;
	}

	/**
	 * vehicle identification number. See ISO 3779 (17 characters)
	 *
	 * @return vin
	 **/
	@JsonProperty(TachoFilesObject.JSON_PROPERTY_VIN)
	@JsonInclude(value = JsonInclude.Include.ALWAYS)
	public String getVin() {
		return this.vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public TachoFilesObject tachoGen(String tachoGen) {

		this.tachoGen = tachoGen;
		return this;
	}

	/**
	 * Generation of tachograph the file was downloaded from Example values - GEN1, GEN2 Optional
	 *
	 * @return tachoGen
	 **/
	@JsonProperty(TachoFilesObject.JSON_PROPERTY_TACHO_GEN)
	@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
	public String getTachoGen() {
		return this.tachoGen;
	}

	public void setTachoGen(String tachoGen) {
		this.tachoGen = tachoGen;
	}

	public TachoFilesObject fileContent(byte[] fileContent) {

		this.fileContent = fileContent;
		return this;
	}

	/**
	 * Get fileContent
	 *
	 * @return fileContent
	 **/
	@JsonProperty(TachoFilesObject.JSON_PROPERTY_FILE_CONTENT)
	@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
	public byte[] getFileContent() {
		return this.fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}
		TachoFilesObject tachoFilesObject = (TachoFilesObject) o;
		return Objects.equals(this.filename, tachoFilesObject.filename) && Objects.equals(this.receivedDateTime, tachoFilesObject.receivedDateTime) && Objects.equals(this.vin,
				tachoFilesObject.vin) && Objects.equals(this.tachoGen, tachoFilesObject.tachoGen) && Arrays.equals(this.fileContent, tachoFilesObject.fileContent);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.filename, this.receivedDateTime, this.vin, this.tachoGen, Arrays.hashCode(this.fileContent));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class TachoFilesObject {\n");
		sb.append("    filename: ").append(this.toIndentedString(this.filename)).append("\n");
		sb.append("    receivedDateTime: ").append(this.toIndentedString(this.receivedDateTime)).append("\n");
		sb.append("    vin: ").append(this.toIndentedString(this.vin)).append("\n");
		sb.append("    tachoGen: ").append(this.toIndentedString(this.tachoGen)).append("\n");
		sb.append("    fileContent: ").append(this.toIndentedString(this.fileContent)).append("\n");
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