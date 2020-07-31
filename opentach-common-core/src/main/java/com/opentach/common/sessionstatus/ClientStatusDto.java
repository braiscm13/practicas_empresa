package com.opentach.common.sessionstatus;

import java.util.List;

/**
 * The Class ClientStatusDto.
 */
public class ClientStatusDto extends AbstractStatusDto {

	/** The statistics. */
	private final List<StatisticsDto>	statistics;


	/**
	 * Instantiates a new client status dto.
	 */
	public ClientStatusDto(List<StatisticsDto> statistics) {
		super();
		this.statistics = statistics;
	}



	public List<StatisticsDto> getStatistics() {
		return this.statistics;
	}


	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------------------------");
		sb.append(String.format("%s (%s): %d\n", this.getUser(), this.getLevel(), this.getSessionId()));
		sb.append(String.format("Since %s\n\n", this.getStartupTime()));
		sb.append(String.format("%s\t%s\t%s\t%s\n", "FManager", "Form", "Action", "ClkCount"));
		for (StatisticsDto stat : this.statistics) {
			sb.append(String.format("%s\t%s\t%s\t%d\n", stat.getFormManager(), stat.getForm(), stat.getAction(), stat.getClickCount().intValue()));
		}
		sb.append("------------------------------------------------------");
		return sb.toString();
	}



	@Override
	public String getApp() {
		return "USER_CLIENT";
	}

}
