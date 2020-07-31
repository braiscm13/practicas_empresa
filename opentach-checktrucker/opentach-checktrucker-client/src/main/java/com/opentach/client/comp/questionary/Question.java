package com.opentach.client.comp.questionary;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * The Class QuestionNode.
 */
public class Question extends JPanel implements IQuestionModelChangeListener {

	private static final long	serialVersionUID	= 1L;

	/** The model. */
	private QuestionModel		model				= null;

	/** The options. */
	private final ButtonGroup	options;

	private final ItemListener	checkBoxListener;

	private boolean				disableChecks;

	/**
	 * Instantiates a new Question.
	 *
	 */
	public Question() {
		super();
		this.setLayout(new GridBagLayout());
		this.options = new ButtonGroup();
		this.setBorder(BorderFactory.createTitledBorder(""));

		// this.setBorder(BorderFactory.createLineBorder(Color.BLUE));

		// this.setBackground(Color.ORANGE);

		this.checkBoxListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				Question.this.onResponseChecked((AbstractButton) e.getSource());
			}
		};
	}

	@Override
	public String getName() {
		return "QuestionPanel";
	}

	/**
	 * Get the Question Model.
	 *
	 * @return the Question Model
	 */
	public QuestionModel getModel() {
		return this.model;
	}

	/**
	 * Set the Question Model.
	 *
	 * @param model
	 *            the new Question Model
	 */
	public void setModel(QuestionModel model) {
		if (this.model != null) {
			this.model.removeQuestionModelListener(this);
		}
		this.model = model;
		this.model.addQuestionModelListener(this);
		this.updateModel();
	}

	/**
	 * Update model.
	 */
	protected void updateModel() {
		this.removeOptions();
		this.removeAll();
		if (this.model != null) {
			this.setBorder(BorderFactory.createTitledBorder(""));
			this.setToolTipText(this.model.getTitle());
			((TitledBorder) this.getBorder()).setTitle(this.model.getTitle());
			int i = 0;
			for (String option : this.model.getOptions()) {
				JCheckBox checkBox = this.createCheckBox(option);
				if (this.disableChecks) {// Disable Checks
					checkBox.setEnabled(false);
				} else {// Search the choice for the question
					if (this.model.getQuestionTO().getChoice() != null) {
						if (i == this.model.getQuestionTO().getChoice()) {
							int choice = i;
							checkBox.setSelected(true);
							this.model.getQuestionTO().setChoice(choice);
						}
					}
				}
				this.options.add(checkBox);
				this.add(checkBox, new GridBagConstraints(0, i, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
						new Insets(0, 0, 0, 0), 0, 0));
				i++;
			}
			this.setPreferredSize(this.getPreferredSize());
		}
	}

	/**
	 * Remove options.
	 */
	private void removeOptions() {
		while (this.options.getButtonCount() > 0) {
			this.removeOption(0);
		}
	}

	/**
	 * Remove option.
	 *
	 * @param optIndex
	 *            the position
	 */
	private void removeOption(int optIndex) {
		AbstractButton button = this.getOption(optIndex);
		this.options.remove(button);
		this.remove(button);
		button.removeItemListener(this.checkBoxListener);
	}

	/**
	 * Create check box.
	 *
	 * @param option
	 *            the option
	 * @return the check box
	 */
	private JCheckBox createCheckBox(String option) {
		JCheckBox checkBox = new JCheckBox(option);
		checkBox.addItemListener(this.checkBoxListener);
		return checkBox;
	}

	/**
	 * Response checked.
	 *
	 * @param checkBox
	 *            the check box
	 */
	private void onResponseChecked(AbstractButton checkBox) {
		this.model.setChoice(this.getOption(checkBox));
	}

	/**
	 * Question changed.
	 *
	 * @param event
	 *            the Question Model Event
	 */
	@Override
	public void modelQuestionChanged(QuestionModelEvent event) {
		switch (event.getPart()) {
			case TITLE:
				((TitledBorder) this.getBorder()).setTitle(this.model.getTitle());
				break;
			case OPT:
				switch (event.getType()) {
					case NEW:
						JCheckBox checkBox = this.createCheckBox(this.model.getOptions().get(event.getOptIndex()));
						if (this.disableChecks) {
							checkBox.setEnabled(false);
						}
						this.options.add(checkBox);
						this.add(checkBox, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 0, GridBagConstraints.CENTER,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
						break;
					case UPDATE:
						this.getOption(event.getOptIndex()).setText(this.model.getOptions().get(event.getOptIndex()));
						break;
					case REMOVE:
						this.removeOption(event.getOptIndex());
					case SELECTION_CHANGE:
						break;
				}
				break;
		}
		this.setPreferredSize(this.getPreferredSize());
		this.revalidate();
		this.validate();
		this.repaint();
	}

	/**
	 * Get option.
	 *
	 * @param pos
	 *            the position
	 * @return the AbstractButton
	 */
	protected AbstractButton getOption(int pos) {
		int index = 0;
		AbstractButton button = null;
		for (Enumeration<AbstractButton> buttons = this.options.getElements(); buttons.hasMoreElements();) {
			button = buttons.nextElement();
			if (index == pos) {
				break;
			}
			index++;
		}
		return button;
	}

	/**
	 * Get option.
	 *
	 * @param check
	 *            the AbstractButton
	 * @return the position
	 */
	protected Integer getOption(AbstractButton check) {
		int index = 0;
		AbstractButton button = null;
		for (Enumeration<AbstractButton> buttons = this.options.getElements(); buttons.hasMoreElements();) {
			button = buttons.nextElement();
			if (button == check) {
				return index;
			}
			index++;
		}
		return -1;
	}

	@Override
	public Dimension getMinimumSize() {
		Dimension preferredSize = super.getPreferredSize();
		return preferredSize;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(300, 30 + (25 * this.model.getOptions().size()));
	}

	/**
	 * Get the Disable Checks
	 *
	 * @return the disable checks
	 */
	public boolean isDisableChecks() {
		return this.disableChecks;
	}

	/**
	 * Set Disable Checks
	 *
	 * @param disableChecks
	 *            Disable the checks *
	 */
	public void setDisableChecks(boolean disableChecks) {
		this.disableChecks = disableChecks;
	}
}
