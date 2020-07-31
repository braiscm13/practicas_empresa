package com.opentach.common.downcenterreport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Class AbstractReportDto.
 */
public abstract class AbstractReportDto implements Serializable {

	/** The company name. */
	private String					companyName;

	/** The company cif. */
	private String					companyCif;

	/** The infraction list. */
	private final List<Infraction>	infractionList;

	/** The next download date. */
	private Date					nextDownloadDate;

	/** The init date. */
	private Date					initDate;

	/** The end date. */
	private Date					endDate;

	/**
	 * Instantiates a new abstract report dto.
	 */
	public AbstractReportDto() {
		super();
		this.infractionList = new ArrayList<>();
	}

	/**
	 * Sets the company name.
	 *
	 * @param companyName
	 *            the new company name
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}


	/**
	 * Sets the company cif.
	 *
	 * @param companyCif
	 *            the new company cif
	 */
	public void setCompanyCif(String companyCif) {
		this.companyCif = companyCif;
	}

	/**
	 * Adds the infraction.
	 *
	 * @param type
	 *            the type
	 * @param nature
	 *            the nature
	 * @param beginDate
	 *            the begin date
	 * @param endDate
	 *            the end date
	 */
	public void addInfraction(String type, String nature, Date beginDate, Date endDate) {
		this.infractionList.add(new Infraction(type, nature, beginDate, endDate));
	}

	/**
	 * Sets the next download date.
	 *
	 * @param nextDownloadDate
	 *            the new next download date
	 */
	public void setNextDownloadDate(Date nextDownloadDate) {
		this.nextDownloadDate = nextDownloadDate;
	}

	/**
	 * Sets the inits the date.
	 *
	 * @param initDate
	 *            the new inits the date
	 */
	public void setInitDate(Date initDate) {
		this.initDate = initDate;
	}

	/**
	 * Sets the end date.
	 *
	 * @param endDate
	 *            the new end date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets the company name.
	 *
	 * @return the company name
	 */
	public String getCompanyName() {
		return this.companyName;
	}

	/**
	 * Gets the company cif.
	 *
	 * @return the company cif
	 */
	public String getCompanyCif() {
		return this.companyCif;
	}

	/**
	 * Gets the infraction list.
	 *
	 * @return the infraction list
	 */
	public List<Infraction> getInfractionList() {
		return this.infractionList;
	}

	/**
	 * Gets the next download date.
	 *
	 * @return the next download date
	 */
	public Date getNextDownloadDate() {
		return this.nextDownloadDate;
	}

	/**
	 * Gets the inits the date.
	 *
	 * @return the inits the date
	 */
	public Date getInitDate() {
		return this.initDate;
	}

	/**
	 * Gets the end date.
	 *
	 * @return the end date
	 */
	public Date getEndDate() {
		return this.endDate;
	}

	/**
	 * The Class Infraction.
	 */
	public static class Infraction implements Serializable {

		/** The type. */
		private String	type;

		/** The nature. */
		private String	nature;

		/** The begin date. */
		private Date	beginDate;

		/** The en date. */
		private Date	enDate;

		/**
		 * Instantiates a new infraction.
		 *
		 * @param type
		 *            the type
		 * @param nature
		 *            the nature
		 * @param beginDate
		 *            the begin date
		 * @param enDate
		 *            the en date
		 */
		public Infraction(String type, String nature, Date beginDate, Date enDate) {
			super();
			this.type = type;
			this.nature = nature;
			this.beginDate = beginDate;
			this.enDate = enDate;
		}

		/**
		 * Gets the type.
		 *
		 * @return the type
		 */
		public String getType() {
			return this.type;
		}

		/**
		 * Sets the type.
		 *
		 * @param type
		 *            the new type
		 */
		public void setType(String type) {
			this.type = type;
		}

		/**
		 * Gets the nature.
		 *
		 * @return the nature
		 */
		public String getNature() {
			return this.nature;
		}

		/**
		 * Sets the nature.
		 *
		 * @param nature
		 *            the new nature
		 */
		public void setNature(String nature) {
			this.nature = nature;
		}

		/**
		 * Gets the begin date.
		 *
		 * @return the begin date
		 */
		public Date getBeginDate() {
			return this.beginDate;
		}

		/**
		 * Sets the begin date.
		 *
		 * @param beginDate
		 *            the new begin date
		 */
		public void setBeginDate(Date beginDate) {
			this.beginDate = beginDate;
		}

		/**
		 * Gets the en date.
		 *
		 * @return the en date
		 */
		public Date getEnDate() {
			return this.enDate;
		}

		/**
		 * Sets the en date.
		 *
		 * @param enDate
		 *            the new en date
		 */
		public void setEnDate(Date enDate) {
			this.enDate = enDate;
		}

	}

}
