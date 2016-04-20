package view;

import model.Card;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by torgammelgard on 2016-04-15.
 */
public class AddEditCardPanel extends JPanel {

    private final int INPUTFIELD_LENGTH = 30;
    private Font font = new Font("Garamond", Font.PLAIN, 18);

    private Card newCard;
    private int numAnswers;
    private int lastRowY;
    private JButton addAnswerButton;
    private JButton deleteAnswerButton;
    private PriorityPanel priorityPanel;

    private ArrayList<RowPanel> rowPanels;

    public AddEditCardPanel() {
        numAnswers = 1;
        rowPanels = new ArrayList<>();
        newCard = new Card();

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;

        // add priority panel
        priorityPanel = new PriorityPanel();
        add(priorityPanel, c);
        c.gridy++;

        rowPanels.add(new RowPanel("Question"));
        add(rowPanels.get(0), c);

        lastRowY = ++c.gridy;
        rowPanels.add(new RowPanel("Answer " + String.valueOf(numAnswers++) + " [correct]"));
        add(rowPanels.get(1), c);

        c.gridwidth = 1;
        c.gridy++;

        deleteAnswerButton = new JButton("Delete alternative answer");
        deleteAnswerButton.addActionListener(e ->
                deleteAnswerField()
        );
        add(deleteAnswerButton, c);

        addAnswerButton = new JButton("Add alternative answer");
        addAnswerButton.addActionListener(e ->
                addRowPanel("Answer " + String.valueOf(numAnswers++))
        );
        c.gridx = 1;
        add(addAnswerButton, c);
    }

    private void addRowPanel(String labelText) {
        rowPanels.add(new RowPanel(labelText));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = ++lastRowY;
        c.gridwidth = 2;
        add(rowPanels.get(rowPanels.size() - 1), c);

        remove(addAnswerButton);
        remove(deleteAnswerButton);

        c.gridwidth = 1;
        c.gridy++;
        add(deleteAnswerButton, c);

        c.gridx = 1;
        add(addAnswerButton, c);

        revalidate();
        repaint();
    }

    private void deleteAnswerField() {
        if (rowPanels.size() > 2) {
            remove(rowPanels.get(rowPanels.size() - 1));
            rowPanels.remove(rowPanels.size() - 1);
            numAnswers--;
            revalidate();
            repaint();
        }
    }

    public Card getCard() {
        String question = rowPanels.get(0).getInput();
        String[] answers = getInputs();
        newCard = new Card();
        newCard.setQuestion(question);
        newCard.setAnswerAlternatives(answers);
        newCard.setPriority(priorityPanel.getSelectedPriority());
        return newCard;
    }

    private String[] getInputs() {
        // get all answers (the first is the question so this is not included)
        String[] inputs = new String[rowPanels.size() - 1];
        for (int i = 1; i < rowPanels.size(); i++) {
            inputs[i - 1] = rowPanels.get(i).getInput();
        }
        return inputs;
    }

    public void setInputs(Card cardToEdit) {
        rowPanels.get(0).setInput(cardToEdit.getQuestion());
        priorityPanel.setSelectedPriority(cardToEdit.getPriority());

        // one answer row is already present, so start at index 1
        for (int i = 1; i < cardToEdit.getAnswerAlternatives().length; i++) {
            addRowPanel("Answer " + String.valueOf(numAnswers++));
        }

        // fill the answer alternatives
        for (int i = 1; i < rowPanels.size(); i++) {
            rowPanels.get(i).setInput(cardToEdit.getAnswerAlternatives()[i - 1]);
        }
    }

    class RowPanel extends JPanel {

        private JLabel label;
        private JTextField answerTextField;

        public RowPanel(String labelText) {
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4, 4, 4, 4);
            c.gridx = 0;
            c.gridy = 0;
            label = new JLabel(labelText);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            label.setFont(font);
            c.anchor = GridBagConstraints.LINE_END;
            add(label, c);          // add label
            c.gridx++;

            // input field
            answerTextField = new AnswerJTextField();
            add(answerTextField, c);
            c.anchor = GridBagConstraints.LINE_START;
            c.gridx = 1;
        }

        public String getLabelText() {
            return label.getText();
        }

        public void setInput(String answer) {
            answerTextField.setText(answer);
        }

        public String getInput() {
            return answerTextField.getText();
        }
    }

    class AnswerJTextField extends JTextField {

        private boolean isCorrect;

        public AnswerJTextField() {
            super(INPUTFIELD_LENGTH);
            isCorrect = false;
        }

        public void setCorrect() {
            isCorrect = true;
        }

        public boolean isCorrect() {
            return isCorrect;
        }
    }

    class PriorityPanel extends JPanel {

        private ButtonGroup bg;
        private ArrayList<JRadioButton> buttons;

        public PriorityPanel() {
            buttons = new ArrayList<>();

            JRadioButton rb1 = new JRadioButton(Card.PRIORITY.LOW.toString());
            buttons.add(rb1);
            rb1.setActionCommand(Card.PRIORITY.LOW.toString());
            rb1.setSelected(false);
            add(rb1);

            JRadioButton rb2 = new JRadioButton(Card.PRIORITY.MEDIUM.toString());
            buttons.add(rb2);
            rb2.setActionCommand(Card.PRIORITY.MEDIUM.toString());
            rb2.setSelected(true);
            add(rb2);

            JRadioButton rb3 = new JRadioButton(Card.PRIORITY.HIGH.toString());
            buttons.add(rb3);
            rb3.setActionCommand(Card.PRIORITY.HIGH.toString());
            rb3.setSelected(false);
            add(rb3);

            bg = new ButtonGroup();
            bg.add(rb1);
            bg.add(rb2);
            bg.add(rb3);

        }

        public void setSelectedPriority(Card.PRIORITY priority) {
            for (int i = 0; i < buttons.size(); i++) {
                if (priority != null) {
                    if (buttons.get(i).getActionCommand().equals(priority.toString()))
                        buttons.get(i).setSelected(true);
                    else
                        buttons.get(i).setSelected(false);
                } else
                    buttons.get(i).setSelected(false);
            }
        }

        public Card.PRIORITY getSelectedPriority() {
            for (int i = 0; i < buttons.size(); i++) {
                if (buttons.get(i).isSelected())
                    return Card.PRIORITY.values()[i];
            }
            return null;
        }
    }
}
