package com.opentach.client.comp.questionary;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.images.ImageManager;
import com.opentach.client.comp.JScrollablePanel;
import com.opentach.client.comp.questionary.QuestionaryModelEvent.QuestionaryModelEventType;
import com.opentach.common.surveys.QuestionTO;

/**
 * The Class Questionary.
 */
public class Questionary extends JPanel implements IQuestionaryModelChangeListener, IQuestionModelChangeListener {

	/**
	 *
	 */
	private static final long				serialVersionUID	= 1L;

	/** The panel variables. */
	private final JPanel					panelQuestions;

	/** The scroll. */
	private final JScrollPane				scroll;

	/** The model. */
	private QuestionaryModel				model;

	protected boolean						disableChecks;
	protected int							numRows;
	protected int[]							xy					= { 0, 0 };

	private boolean							editable;

	private JButton							bEdit, bDelete;

	private IQuestionaryModelChangeListener	listener;

	/**
	 * Instantiates a new Questionary.
	 *
	 */
	public Questionary() {
		super();
		this.setLayout(new BorderLayout(0, 0));
		this.panelQuestions = new JScrollablePanel(true, false);
		this.panelQuestions.setLayout(new GridBagLayout());

		this.scroll = new JScrollPane(this.panelQuestions) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(10, 10);
			}
		};
		this.add(this.scroll, BorderLayout.CENTER);
	}

	@Override
	public String getName() {
		return "QuestionaryPanel";
	}

	/**
	 * Get the QuestionaryModel.
	 *
	 * @return the QuestionaryModel
	 */
	public QuestionaryModel getModel() {
		return this.model;
	}

	/**
	 * Set the QuestionaryModel.
	 *
	 * @param model
	 *            the new QuestionaryModel
	 */
	public void setModel(QuestionaryModel model) {
		if (this.model != null) {
			this.model.removeQuestionaryModelListener(this);
		}
		this.model = model;
		this.model.addQuestionaryModelListener(this);
		this.updateModel();
	}

	/**
	 * Update model.
	 */
	public void updateModel() {
		this.panelQuestions.removeAll();
		this.xy = new int[] { 0, 0 };
		if (this.model != null) {
			for (int i = 0; i < this.model.getQuestions().size(); i++) {
				this.addPanelQuestion(i);
			}
		}
	}

	private void addPanelQuestion(int i) {
		Question question = new Question();
		question.setDisableChecks(this.disableChecks);
		question.setModel(new QuestionModel(this.model.getQuestions().get(i)));
		String title = question.getModel().getTitle();
		int index = title.indexOf(". ");
		if (index != -1) {
			question.getModel().setTitle(i + 1 + ". " + title.substring(index + 2, title.length()));
		} else {
			question.getModel().setTitle(i + 1 + ". " + title);
		}
		question.getModel().addQuestionModelListener(this);
		if (this.editable) {
			this.attachButtons(question);
		}
		this.panelQuestions.add(question, new GridBagConstraints(this.xy[1], this.xy[0], 1, 1, 1, 0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.xy = this.posXY(this.xy, this.numRows);
	}

	private void attachButtons(Question question) {
		this.bEdit = new JButton(ImageManager.getIcon("com/opentach/client/rsc/edit16.png"));
		this.bEdit.setBorder(BorderFactory.createEmptyBorder());
		this.bEdit.setContentAreaFilled(false);
		this.bEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				QuestionTO questionTO = ((Question) ((JButton) e.getSource()).getParent()).getModel().getQuestionTO();
				Questionary.this.fireQuestionaryListener(QuestionaryEditOptions.EDIT, questionTO);
			}
		});
		this.bEdit.setToolTipText(ApplicationManager.getTranslation("editquestion"));
		this.bDelete = new JButton(ImageManager.getIcon("com/opentach/client/rsc/garbage16.png"));
		this.bDelete.setBorder(BorderFactory.createEmptyBorder());
		this.bDelete.setContentAreaFilled(false);
		this.bDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				QuestionTO questionTO = ((Question) ((JButton) e.getSource()).getParent()).getModel().getQuestionTO();
				Questionary.this.fireQuestionaryListener(QuestionaryEditOptions.DELETE, questionTO);
			}
		});
		this.bDelete.setToolTipText(ApplicationManager.getTranslation("deletequestion"));
		question.add(this.bEdit);
		question.add(this.bDelete);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(10, this.panelQuestions.getPreferredSize().height + this.scroll.getHorizontalScrollBar().getHeight());
	}

	@Override
	public void modelQuestionaryChanged(QuestionaryModelEvent event) {
		switch (event.getType()) {
			case NEW:
				this.addPanelQuestion(this.model.getQuestions().size() - 1);
				this.fireQuestionaryChangeListener();
				break;
			case REMOVE:
				this.updateModel();
				break;
			default:
				break;
		}
		this.revalidate();
		this.validate();
		this.repaint();
	}

	private int[] posXY(int[] xy, int numRows) {
		xy[0]++;
		if (xy[0] == numRows) {
			xy[1]++;
		}
		xy[0] = xy[0] % numRows;
		return xy;
	}

	@Override
	public void modelQuestionChanged(QuestionModelEvent event) {}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public void fireQuestionaryListener(String option, QuestionTO questionTO) {
		if (this.listener != null) {
			this.listener.modelQuestionaryEdit(option, this.model, this.model.getQuestions().indexOf(questionTO));
		}
	}

	public void fireQuestionaryChangeListener() {
		if (this.listener != null) {
			this.listener.modelQuestionaryChanged(new QuestionaryModelEvent(this.model, QuestionaryModelEventType.NEW, 0));
		}
	}

	public void setQuestionaryListener(IQuestionaryModelChangeListener listener) {
		this.listener = listener;
	}

	@Override
	public void modelQuestionaryEdit(String option, QuestionaryModel model, int index) {}

}
