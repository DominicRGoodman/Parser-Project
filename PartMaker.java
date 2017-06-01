import java.util.ArrayDeque;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * 
 * @author Dominic Goodman Project 1
 * 
 *         Creates the parts to be added to a panel or GUI
 */
public class PartMaker
{
	/**
	 * 
	 * @param command
	 *            - next command to be processed
	 * @return a text field to be added
	 * @throws ParseError
	 */
	public JTextField getTextField(String command) throws ParseError
	{
		if (!command.endsWith(";"))
		{
			throw new ParseError("Invalid ending in the line:\n\t" + command);
		}

		JTextField nextTextField = new JTextField();
		try
		{
			nextTextField.setColumns(Integer.parseInt(command.substring(10,
					command.length() - 1)));
		} catch (NumberFormatException e)
		{
			throw new ParseError(
					"Invalid or missing size value in the line:\n\t" + command);
		}
		return nextTextField;
	}

	/**
	 * 
	 * @param command
	 *            - next command to be processed
	 * @return a button to be added
	 * @throws ParseError
	 */
	public JButton getButton(String command) throws ParseError
	{
		checkForQuotes(command);
		if (!command.endsWith(";"))
		{
			throw new ParseError("Invalid ending in the line:\n\t" + command);
		}
		JButton nextButton = new JButton(command.substring(
				command.indexOf("\"") + 1, command.lastIndexOf("\"")));
		return nextButton;
	}

	/**
	 * 
	 * @param command
	 *            - next command to be processed
	 * @return a label to be added
	 * @throws ParseError
	 */
	public JLabel getLabel(String command) throws ParseError
	{

		if (!command.endsWith(";"))
		{
			throw new ParseError("Invalid ending in the line:\n\t" + command);
		}
		checkForQuotes(command);

		JLabel nextLbl = new JLabel(command.substring(
				command.indexOf("\"") + 1, command.lastIndexOf("\"")));

		return nextLbl;
	}

	/**
	 * 
	 * @param command
	 *            - next command to be processed
	 * @return An ArrayDeque of the added radio buttons
	 * @throws ParseError
	 */
	public ArrayDeque<JRadioButton> getNextRadio(ArrayDeque<String> commands)
			throws ParseError
	{
		ArrayDeque<JRadioButton> buttons = new ArrayDeque<JRadioButton>();
		ButtonGroup nextGroup = new ButtonGroup();

		while (!commands.isEmpty())
		{
			if (commands.peekFirst().endsWith(";"))
			{
				checkForQuotes(commands.peekFirst());
				JRadioButton nextButton = null;

				try
				{
					nextButton = new JRadioButton(commands.peekFirst()
							.substring(commands.peekFirst().indexOf("\"") + 1,
									commands.peekFirst().lastIndexOf("\"")));
				} catch (StringIndexOutOfBoundsException e)
				{
					throw new ParseError("Invalid command in the line:\n\t"
							+ commands.peekFirst() + "\nMissing either a \"");
				}
				nextGroup.add(nextButton);
				buttons.add(nextButton);
				commands.remove();
			} else
				throw new ParseError("Invalid command in the line:\n\t"
						+ commands.peekFirst() + "\nExpected a radio_button");
		}
		return buttons;
	}

	/**
	 * 
	 * @param toCheck
	 *            - next string to be checked for quotes
	 * @throws ParseError
	 */
	private void checkForQuotes(String toCheck) throws ParseError
	{
		if (toCheck.indexOf("\"") == -1 || toCheck.lastIndexOf("\"") == -1)
		{
			throw new ParseError("Invalid command in the line:\n\t" + toCheck
					+ "\nMissing quotes");
		}

	}

}
