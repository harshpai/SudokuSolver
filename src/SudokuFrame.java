import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;


@SuppressWarnings("serial")
public class SudokuFrame extends JFrame {

	JButton check;
	JCheckBox autoCheck;
	JTextArea puzzle,solution;
	Sudoku sudoku;
	int numSolutions;

	public SudokuFrame() {
		super("Sudoku Solver");

		setLayout(new BorderLayout(4, 4));

		// Add puzzle textarea in the center
		puzzle = new JTextArea(15, 20);
		puzzle.setBorder(new TitledBorder("Puzzle"));
		add(puzzle,BorderLayout.CENTER);

		// Add puzzle textarea in the center
		solution = new JTextArea(15, 20);
		solution.setBorder(new TitledBorder("Solution"));
		add(solution,BorderLayout.EAST);

		// Add a box in the south that holds "Check" button and "Auto Check" checkbox
		JPanel southBox = new JPanel();
		check = new JButton("Check");
		autoCheck = new JCheckBox("Auto Check",true);
		southBox.setLayout(new BoxLayout(southBox , BoxLayout.X_AXIS));
		southBox.add(check);
		southBox.add(autoCheck);
		add(southBox, BorderLayout.SOUTH);

		// add listeners to the check button and document model of
		// puzzle text area to parse and solve sudoku
		// Note the use of MVC model here.
		check.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkSudoku(true);
			}
		});

		Document doc = puzzle.getDocument();

		doc.addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				checkSudoku(false);
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				checkSudoku(false);
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				checkSudoku(false);
			}
		});

		setLocationByPlatform(true);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * private helper method that formats the text to be displayed
	 * in the solution text area
	 * */
	private String getSolutionToPrint(){
		StringBuilder buff = new StringBuilder(sudoku.getSolutionText());
		buff.append("\n");
		buff.append("solutions:"+numSolutions+"\n");
		buff.append("elapsed:"+sudoku.getElapsed()+"\n");
		return buff.toString();
	}

	/**
	 * private helper method that encapsulates the check functionality.
	 * It is used to implement button and document listeners
	 * <p>
	 * In summary, it does the following:
	 * Construct a Sudoku for the text in the left text area and try to solve it.
	 * If the text is mal-formed in any way so the construction of the Sudoku
	 * throws an exception, just write "Parsing problem" in the results text area.
	 * Otherwise, after the solve(), if there is at least one solution, write its
	 * text into the results text area.
	 * If there is a solution, write the "solutions:xxx\n" "elapsed:xxx\n" at the
	 * end of the results text area. (This is done using a helper method.)
	 * */
	private void checkSudoku(boolean isInvokedByCheckbutton)
	{
		try
		{
			if(autoCheck.isSelected() || isInvokedByCheckbutton)
			{
				sudoku = new Sudoku(puzzle.getText());
				numSolutions = sudoku.solve();

				if(numSolutions > 0)
				{
					solution.setText(getSolutionToPrint());
				}
			}
		}
		catch(Exception ex)
		{
			solution.setText("Parsing Error");
		}
	}

	public static void main(String[] args) {
		// GUI Look And Feel
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }

		new SudokuFrame();
	}

}
