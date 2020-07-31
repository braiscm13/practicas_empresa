package com.opentach.client.employee.fim;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.IntegerDataField;
import com.ontimize.gui.field.ObjectDataField;
import com.ontimize.gui.field.RealDataField;
import com.ontimize.gui.field.TextComboDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.common.company.beans.EmployeeType;
import com.opentach.common.employee.naming.EmployeeNaming;
import com.opentach.common.labor.LaborNaming;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.Row;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.field.UTextComboDataField;
import com.utilmize.client.gui.field.UTimeDataField;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMDriverContracts extends UBasicFIM {
	@FormComponent(attr = LaborNaming.DRC_TYPE)
	TextComboDataField						contractType;

	@FormComponent(attr = LaborNaming.CIF)
	ObjectDataField							cif;
	@FormComponent(attr = LaborNaming.IDCONDUCTOR)
	ObjectDataField							employeeId;
	@FormComponent(attr = LaborNaming.AGR_ID)
	UReferenceDataField						agreementId;

	@FormComponent(attr = "DRC_HOURS_PER_DAY")
	IntegerDataField						hoursPerDay;
	@FormComponent(attr = "DRC_HOURS_PER_WEEK")
	IntegerDataField						hoursPerWeek;
	@FormComponent(attr = "DRC_HOURS_PER_YEAR")
	IntegerDataField						hoursPerYear;
	@FormComponent(attr = LaborNaming.DRC_MINUTES_PER_DAY)
	IntegerDataField						minutesPerDay;
	@FormComponent(attr = LaborNaming.DRC_MINUTES_PER_WEEK)
	IntegerDataField						minutesPerWeek;
	@FormComponent(attr = LaborNaming.DRC_MINUTES_PER_YEAR)
	IntegerDataField						minutesPerYear;

	@FormComponent(attr = "LIGHTDRIVERTASKS")
	Row										laboralTasks;
	@FormComponent(attr = LaborNaming.DRC_IS_DRIVETIME_WORKTIME)
	CheckDataField							isDriveTimeWorktime;
	@FormComponent(attr = LaborNaming.DRC_IS_AVAILABLE_WORKTIME)
	CheckDataField							isAvailableWorktime;
	@FormComponent(attr = LaborNaming.DRC_IS_OTHERTASK_WORKTIME)
	CheckDataField							isOtherTaskWorktime;
	@FormComponent(attr = LaborNaming.DRC_IS_BREAKTIME_WORKTIME)
	CheckDataField							isBreakTimeWorktime;

	@FormComponent(attr = LaborNaming.DRC_SHIFT_TYPE)
	UTextComboDataField						shiftType;
	@FormComponent(attr = LaborNaming.DRC_START_HOUR_1)
	UTimeDataField							startHour1;
	@FormComponent(attr = LaborNaming.DRC_END_HOUR_1)
	UTimeDataField							endHour1;
	@FormComponent(attr = LaborNaming.DRC_START_HOUR_2)
	UTimeDataField							startHour2;
	@FormComponent(attr = LaborNaming.DRC_END_HOUR_2)
	UTimeDataField							endHour2;

	@FormComponent(attr = "DRC_MAX_OVERTIME_HOURS_YEAR")
	IntegerDataField						maxOvertimeHoursPerYear;
	@FormComponent(attr = LaborNaming.DRC_MAX_OVERTIME_MINUTES_YEAR)
	IntegerDataField						maxOvertimeMinutesPerYear;
	@FormComponent(attr = LaborNaming.DRC_OVERTIME_PRICE)
	RealDataField						overtimePrice;

	@FormComponent(attr = LaborNaming.DRC_NOCTURNE_SUPPLEMENT)
	RealDataField						nocturneSupplement;
	@FormComponent(attr = LaborNaming.DRC_NOCTURNE_START_HOUR)
	UTimeDataField							nocturneStartHour;
	@FormComponent(attr = LaborNaming.DRC_NOCTURNE_END_HOUR)
	UTimeDataField							nocturneEndHour;
	@FormComponent(attr = LaborNaming.DRC_SHIFTS_SUPPLEMENT)
	RealDataField						shiftsSupplement;
	@FormComponent(attr = LaborNaming.DRC_AVAILABILITY_SUPPLEMENT)
	RealDataField						availabilitySupplement;

	@FormComponent(attr = "DRC_REST_HOURS_PER_DAY")
	IntegerDataField						restHoursPerDay;
	@FormComponent(attr = "DRC_REST_HOURS_PER_WEEK")
	IntegerDataField						restHoursPerWeek;
	@FormComponent(attr = LaborNaming.DRC_REST_MINUTES_PER_DAY)
	IntegerDataField						restMinutesPerDay;
	@FormComponent(attr = LaborNaming.DRC_REST_MINUTES_PER_WEEK)
	IntegerDataField						restMinutesPerWeek;
	@FormComponent(attr = LaborNaming.DRC_HOLIDAYS_PER_YEAR)
	IntegerDataField						holidaysPerYear;

	@FormComponent(attr = LaborNaming.DRC_HOLIDAYS_POLICY)
	UTextComboDataField						holidaysPolicy;

	@FormComponent(attr = "DRC_PARTIAL_DAILY_HOURS")
	IntegerDataField						partialDailyHours;
	@FormComponent(attr = "DRC_PARTIAL_WEEKLY_HOURS")
	IntegerDataField						partialWeeklyHours;
	@FormComponent(attr = "DRC_PARTIAL_BIWEEKLY_HOURS")
	IntegerDataField						partialBiWeeklyHours;
	@FormComponent(attr = "DRC_PARTIAL_MONTHLY_HOURS")
	IntegerDataField						partialMonthlyHours;
	@FormComponent(attr = "DRC_PARTIAL_ANNUAL_HOURS")
	IntegerDataField						partialAnnualHours;

	@FormComponent(attr = LaborNaming.DRC_PARTIAL_DAILY_MINUTES)
	IntegerDataField						partialDailyMinutes;
	@FormComponent(attr = LaborNaming.DRC_PARTIAL_WEEKLY_MINUTES)
	IntegerDataField						partialWeeklyMinutes;
	@FormComponent(attr = LaborNaming.DRC_PARTIAL_BIWEEKLY_MINUTES)
	IntegerDataField						partialBiWeeklyMinutes;
	@FormComponent(attr = LaborNaming.DRC_PARTIAL_MONTHLY_MINUTES)
	IntegerDataField						partialMonthlyMinutes;
	@FormComponent(attr = LaborNaming.DRC_PARTIAL_ANNUAL_MINUTES)
	IntegerDataField						partialAnnualMinutes;

	@FormComponent(attr = "DRC_COMPL_DAILY_HOURS")
	IntegerDataField						complDailyHours;
	@FormComponent(attr = "DRC_COMPL_WEEKLY_HOURS")
	IntegerDataField						complWeeklyHours;
	@FormComponent(attr = "DRC_COMPL_BIWEEKLY_HOURS")
	IntegerDataField						complBiWeeklyHours;
	@FormComponent(attr = "DRC_COMPL_MONTHLY_HOURS")
	IntegerDataField						complMonthlyHours;
	@FormComponent(attr = "DRC_COMPL_ANNUAL_HOURS")
	IntegerDataField						complAnnualHours;

	@FormComponent(attr = LaborNaming.DRC_COMPL_DAILY_MINUTES)
	IntegerDataField						complDailyMinutes;
	@FormComponent(attr = LaborNaming.DRC_COMPL_WEEKLY_MINUTES)
	IntegerDataField						complWeeklyMinutes;
	@FormComponent(attr = LaborNaming.DRC_COMPL_BIWEEKLY_MINUTES)
	IntegerDataField						complBiWeeklyMinutes;
	@FormComponent(attr = LaborNaming.DRC_COMPL_MONTHLY_MINUTES)
	IntegerDataField						complMonthlyMinutes;
	@FormComponent(attr = LaborNaming.DRC_COMPL_ANNUAL_MINUTES)
	IntegerDataField						complAnnualMinutes;

	private static final Logger				logger				= LoggerFactory.getLogger(IMDriverContracts.class);

	private final ValueChangeListener	valueChangeListener	= this::formLogic;

	private int								driverType			= 0;

	private List<DataField>				linkedDataFields	= new ArrayList<>();

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);

		FIMUtils.setEventsOnFieldEnabled(this.managedForm, "MARK_CURRENT_CONTRACT", false);

		this.linkedDataFields = Arrays.asList( //
				this.hoursPerDay, this.minutesPerDay, //
				this.hoursPerWeek, this.minutesPerWeek, //
				this.hoursPerYear, this.minutesPerYear, //
				this.maxOvertimeHoursPerYear, this.maxOvertimeMinutesPerYear, //
				//
				this.restHoursPerDay, this.restMinutesPerDay, //
				this.restHoursPerWeek, this.restMinutesPerWeek, //
				//
				this.partialDailyHours, this.partialDailyMinutes, //
				this.partialWeeklyHours, this.partialWeeklyMinutes, //
				this.partialBiWeeklyHours, this.partialBiWeeklyMinutes, //
				this.partialMonthlyHours, this.partialMonthlyMinutes, //
				this.partialAnnualHours, this.partialAnnualMinutes, //
				//
				this.complDailyHours, this.complDailyMinutes, //
				this.complWeeklyHours, this.complWeeklyMinutes, //
				this.complBiWeeklyHours, this.complBiWeeklyMinutes, //
				this.complMonthlyHours, this.complMonthlyMinutes, //
				this.complAnnualHours, this.complAnnualMinutes //
				); //

		// In an attempt to unify the validation of as many components of the
		// form as we can, we add a listener to all of them descending from
		// DataField which in turn calls to just one validation procedure
		// (formLogic()) into which the logic of interaction must be written:

		for (final Object component : this.getManagedForm().getComponentList()) {
			if (component instanceof DataField && !this.linkedDataFields.contains(component)) {
				((DataField) component).addValueChangeListener(this.valueChangeListener);
			}
		}
	}

	public static class IMDriverContractsTypeValueChangeListener extends AbstractValueChangeListener {
		private static String[]	attrsFullTime		= new String[] {};
		private static String[]	attrsPartialTime	= new String[] {	//
				LaborNaming.DRC_PARTIAL_DAILY_MINUTES,					//
				LaborNaming.DRC_PARTIAL_WEEKLY_MINUTES,					//
				LaborNaming.DRC_PARTIAL_BIWEEKLY_MINUTES,				//
				LaborNaming.DRC_PARTIAL_MONTHLY_MINUTES,				//
				LaborNaming.DRC_PARTIAL_ANNUAL_MINUTES,					//
				LaborNaming.DRC_COMPL_DAILY_MINUTES,					//
				LaborNaming.DRC_COMPL_WEEKLY_MINUTES,					//
				LaborNaming.DRC_COMPL_BIWEEKLY_MINUTES,					//
				LaborNaming.DRC_COMPL_MONTHLY_MINUTES,					//
				LaborNaming.DRC_COMPL_ANNUAL_MINUTES,					//
				"DRC_PARTIAL_DAILY_HOURS",								//
				"DRC_PARTIAL_WEEKLY_HOURS",								//
				"DRC_PARTIAL_BIWEEKLY_HOURS",							//
				"DRC_PARTIAL_MONTHLY_HOURS",							//
				"DRC_PARTIAL_ANNUAL_HOURS",								//
				"DRC_COMPL_DAILY_HOURS",								//
				"DRC_COMPL_WEEKLY_HOURS",								//
				"DRC_COMPL_BIWEEKLY_HOURS",								//
				"DRC_COMPL_MONTHLY_HOURS",								//
				"DRC_COMPL_ANNUAL_HOURS",								//
				"LABEL_WORKING",										//
				"LABEL_COMPL"											//
		};																//

		public IMDriverContractsTypeValueChangeListener(IUFormComponent formComponent, Map<?, ?> params) throws Exception {
			super(formComponent, (Hashtable<?, ?>) params);
		}

		@Override
		public void valueChanged(ValueEvent e) {
			this.applyLogic(e.getNewValue());
		}

		@Override
		public void interactionManagerModeChanged(InteractionManagerModeEvent e) {
			super.interactionManagerModeChanged(e);
			final Object newValue = ((DataField) this.getFormComponent()).getValue();
			this.applyLogic(newValue);
		}

		protected void applyLogic(Object newValue) {
			if (newValue == null) {
				this.getUBasicInteractionManager().setDataFieldsEnable(false, IMDriverContractsTypeValueChangeListener.attrsPartialTime);
				this.getUBasicInteractionManager().setDataFieldsEnable(false, IMDriverContractsTypeValueChangeListener.attrsFullTime);
			} else {
				this.getUBasicInteractionManager().setDataFieldsEnable("TIEMPO_PARCIAL".equals(newValue), IMDriverContractsTypeValueChangeListener.attrsPartialTime);
				this.getUBasicInteractionManager().setDataFieldsEnable(!"TIEMPO_PARCIAL".equals(newValue), IMDriverContractsTypeValueChangeListener.attrsFullTime);
			}
		}
	}

	public static class IMDriverContractsCheckCurrentContractValueChangeListener extends AbstractValueChangeListener {
		@FormComponent(attr = "MARK_CURRENT_CONTRACT")
		private CheckDataField	checkCurrentContract;

		@FormComponent(attr = "DRC_DATE_TO")
		private DateDataField	drcDateTo;

		public IMDriverContractsCheckCurrentContractValueChangeListener(IUFormComponent formComponent, Map<?, ?> params) throws Exception {
			super(formComponent, (Hashtable<?, ?>) params);
		}

		@Override
		public void valueChanged(ValueEvent e) {
			if (e.getType() == ValueEvent.USER_CHANGE) {
				if (this.checkCurrentContract.isSelected()) {
					if (this.drcDateTo.getValue() != null) {
						this.drcDateTo.setValue(null);
					}
					this.drcDateTo.setEnabled(false);
				} else {
					this.drcDateTo.setEnabled(true);
				}
			}
		}
	}

	public static class IMDriverContractsDateToValueChangeListener extends AbstractValueChangeListener {
		@FormComponent(attr = "MARK_CURRENT_CONTRACT")
		private CheckDataField	checkCurrentContract;

		@FormComponent(attr = "DRC_DATE_TO")
		private DateDataField	drcDateTo;

		public IMDriverContractsDateToValueChangeListener(IUFormComponent formComponent, Map<?, ?> params) throws Exception {
			super(formComponent, (Hashtable<?, ?>) params);
		}

		@Override
		public void valueChanged(ValueEvent e) {
			SwingUtilities.invokeLater(() -> {
				if (IMDriverContractsDateToValueChangeListener.this.drcDateTo.getValue() != null) {
					IMDriverContractsDateToValueChangeListener.this.checkCurrentContract.setValue(Boolean.FALSE);
					IMDriverContractsDateToValueChangeListener.this.drcDateTo.setEnabled(true);
				} else {
					IMDriverContractsDateToValueChangeListener.this.checkCurrentContract.setValue(Boolean.TRUE);
					IMDriverContractsDateToValueChangeListener.this.drcDateTo.setEnabled(false);
				}
			});
		}
	}

	/**
	 * An effort to centralize the logic of interdependent controls. See comment at
	 * {@link IMDriverContracts#registerInteractionManager(Form, IFormManager)}.
	 */
	private void formLogic(ValueEvent event) {
		final boolean isTachoDriver = this.driverType == EmployeeType.DRIVER_WITH_TACHOGRAPH.toId();
		final boolean isLightDriver = this.driverType == EmployeeType.DRIVER_WITHOUT_TACHOGRAPH.toId();
		final boolean isSplitShift = LaborNaming.DRC_SPLIT_SHIFT.equals(this.shiftType.getValue());
		final boolean isPartialTime = "TIEMPO_PARCIAL".equals(this.contractType.getValue());

		this.agreementId.setVisible(this.driverType == EmployeeType.DRIVER_WITH_TACHOGRAPH.toId());

		this.laboralTasks.setVisible(isLightDriver);
		this.isDriveTimeWorktime.setEnabled(isLightDriver);
		this.isOtherTaskWorktime.setEnabled(isLightDriver);
		this.isAvailableWorktime.setEnabled(isLightDriver);
		this.isBreakTimeWorktime.setEnabled(isLightDriver);

		this.hoursPerDay.setEnabled(!isTachoDriver);
		this.hoursPerWeek.setEnabled(!isTachoDriver);
		this.hoursPerYear.setEnabled(!isTachoDriver);

		this.shiftType.setEnabled(!isTachoDriver);
		this.startHour1.setEnabled(!isTachoDriver);
		this.endHour1.setEnabled(!isTachoDriver);
		this.startHour2.setEnabled(!isTachoDriver && isSplitShift);
		this.endHour2.setEnabled(!isTachoDriver && isSplitShift);

		this.maxOvertimeHoursPerYear.setEnabled(!isTachoDriver);
		this.overtimePrice.setEnabled(!isTachoDriver);

		this.nocturneSupplement.setEnabled(true);
		this.nocturneStartHour.setEnabled(true);
		this.nocturneEndHour.setEnabled(true);
		this.shiftsSupplement.setEnabled(!isTachoDriver);
		this.availabilitySupplement.setEnabled(!isTachoDriver);

		this.restHoursPerDay.setEnabled(!isTachoDriver);
		this.restHoursPerWeek.setEnabled(!isTachoDriver);
		this.holidaysPerYear.setEnabled(!isTachoDriver);
		this.holidaysPolicy.setEnabled(!isTachoDriver);

		this.partialDailyHours.setEnabled(isPartialTime);
		this.partialWeeklyHours.setEnabled(isPartialTime);
		this.partialBiWeeklyHours.setEnabled(isPartialTime);
		this.partialMonthlyHours.setEnabled(isPartialTime);
		this.partialAnnualHours.setEnabled(isPartialTime);

		this.complDailyHours.setEnabled(isPartialTime);
		this.complWeeklyHours.setEnabled(isPartialTime);
		this.complBiWeeklyHours.setEnabled(isPartialTime);
		this.complMonthlyHours.setEnabled(isPartialTime);
		this.complAnnualHours.setEnabled(isPartialTime);
	}

	private void getDriverType() {
		final ReferenceLocator referenceLocator = (ReferenceLocator) IMDriverContracts.this.formManager.getReferenceLocator();

		try {
			final Entity entity = referenceLocator.getEntityReference(EmployeeNaming.ENTITY);
			final EntityResult entityResult = entity.query( //
					EntityResultTools.keysvalues( //
							EmployeeNaming.CIF, this.cif.getValue(), //
							EmployeeNaming.IDCONDUCTOR, this.employeeId.getValue()), //
					EntityResultTools.attributes( //
							EmployeeNaming.TYPE), //
					referenceLocator.getSessionId()); //

			this.driverType = ((Vector<BigDecimal>) entityResult.get("TYPE")).get(0).intValue();
		} catch (final Exception exception) {
			// We assign the default value since we can't determine the employee
			// type (either it has not been set or we found an error condition):
			IMDriverContracts.logger.debug("NO DRIVER TYPE SPECIFIED: ASSUMING {}", EmployeeType.DRIVER_WITH_TACHOGRAPH, exception);
			this.driverType = EmployeeType.DRIVER_WITH_TACHOGRAPH.toId();
		}
	}

	@Override
	public void setUpdateMode() {
		this.getDriverType();
		super.setUpdateMode();
	}

	@Override
	public void setInsertMode() {
		this.getDriverType();
		super.setInsertMode();

		this.nocturneStartHour.setValue(22 * 60 * 60 * 1000l);
		this.nocturneEndHour.setValue(6 * 60 * 60 * 1000l);
	}

	@Override
	public void setQueryMode() {
		this.getDriverType();
		super.setQueryMode();
	}

	@Override
	public void setQueryInsertMode() {
		this.getDriverType();
		super.setQueryInsertMode();
	}
}
