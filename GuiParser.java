import java.util.ArrayDeque;
import java.util.ArrayList;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JRadioButton;

/**
 * 
 * @author Dominic Goodman Project 1
 * 
 *         Begins the parser, adds all parts until the end of the command file.
 */
public class GuiParser
{
	private GUI createdGUI;
	private ArrayDeque<String> commands = new ArrayDeque<String>();
	private PartMaker nextPart = new PartMaker();

	/**
	 * 
	 * @param nextCommand
	 *            - adds commands to be processed
	 */
	public void addCommand(String nextCommand)
	{
		commands.add(nextCommand);
	}

	/**
	 * creates the Gui
	 * 
	 * @throws ParseError
	 */
	public void createGUI() throws ParseError
	{
		if (!commands.peekFirst().startsWith("Window"))
		{
			throw new ParseError("Code does not start with \"Window\"");
		}
		if (!commands.peekFirst().endsWith(":"))
		{
			throw new ParseError("Invalid ending in the line:\n\t"
					+ commands.peekFirst());
		}

		String[] dimensionValues = null;
		try
		{
			createdGUI = new GUI(commands.peekFirst().substring(
					commands.peekFirst().indexOf("\"") + 1,
					commands.peekFirst().lastIndexOf("\"")));
			dimensionValues = commands
					.peekFirst()
					.substring(commands.peekFirst().lastIndexOf("\""))
					.substring(
							commands.peekFirst().indexOf("(") + 1
									- commands.peekFirst().lastIndexOf("\""),
							commands.peekFirst().indexOf(")")
									- commands.peekFirst().lastIndexOf("\""))
					.split("\\s|,");
		} catch (StringIndexOutOfBoundsException e)
		{
			throw new ParseError("Invalid command in the line:\n\t"
					+ commands.peekFirst() + "\nMissing either a \" or ( or )");
		}
		ArrayList<Integer> dimensions = new ArrayList<Integer>();

		for (int i = 0; i < dimensionValues.length; i++)
		{
			try
			{
				dimensions.add(Integer.parseInt(dimensionValues[i]));
			} catch (NumberFormatException e)
			{
				// ignore non-number values
			}
		}
		if (dimensions.size() == 2)
		{
			createdGUI.setDimensions(dimensions.get(0), dimensions.get(1));
		} else
		{
			throw new ParseError(
					"Invalid dimensions for frame in the line:\n\t"
							+ commands.peekFirst());
		}

		String layout = commands.peekFirst().substring(
				commands.peekFirst().indexOf("Layout") + 7);
		if (layout.startsWith("Flow"))
		{
			createdGUI.setLayout(new FlowLayout());
		} else if (layout.startsWith("Grid"))
		{
			if (layout.indexOf("(") == -1 || layout.indexOf(")") == -1)
			{
				throw new ParseError("Invalid command in the line:\n\t"
						+ commands.peekFirst() + "\nMissing parenthesis");
			}
			String[] formatValues = layout.substring(
					layout.lastIndexOf("(") + 1, layout.lastIndexOf(")"))
					.split("\\s|,");
			ArrayList<Integer> values = new ArrayList<Integer>();
			for (int i = 0; i < formatValues.length; i++)
			{
				try
				{
					values.add(Integer.parseInt(formatValues[i]));
				} catch (NumberFormatException e)
				{
					// ignore non-number values
				}
			}
			if (values.size() == 4)
			{
				int index = 0;
				createdGUI
						.setLayout(new GridLayout(values.get(index), values
								.get(index++), values.get(index++), values
								.get(index++)));
			} else if (values.size() == 2)
			{
				int index = 0;
				createdGUI.setLayout(new GridLayout(values.get(index), values
						.get(index++)));
			} else
			{
				throw new ParseError("Invalid Paramaters in the line:\n\t"
						+ commands.peekFirst());
			}
		} else
			throw new ParseError("Invalid Layout in the line:\n\t"
					+ commands.peekFirst());

		commands.remove();
		generateGuiParts();
		createdGUI.display();
	}

	/**
	 * Creates the parts to be added to the GUI
	 * 
	 * @throws ParseError
	 */
	public void generateGuiParts() throws ParseError
	{

		if (commands.isEmpty())
		{
			throw new ParseError("Program does not end.");
		}
		if (commands.peekFirst().equals("End."))
		{
			return;
		}
		if (commands.peekFirst().startsWith("Panel"))
		{
			GuiPanel nextPanel = new GuiPanel(commands);
			createdGUI.add(nextPanel.getPanel());
			commands = nextPanel.getRemaining();
		} else if (commands.peekFirst().startsWith("Label"))
		{
			createdGUI.add(nextPart.getLabel(commands.peekFirst()));
		} else if (commands.peekFirst().startsWith("Textfield"))
		{
			createdGUI.add(nextPart.getTextField(commands.peekFirst()));
		} else if (commands.peekFirst().startsWith("Button"))
		{
			createdGUI.add(nextPart.getButton(commands.peekFirst()));
		} else if (commands.peekFirst().startsWith("Group"))
		{
			if (commands.peekFirst().startsWith("Group:"))
				throw new ParseError("Invalid ending in the line:\n\t"
						+ commands.peekFirst() + "\nShould have no ending");
			commands.remove();
			ArrayDeque<String> nextRadios = new ArrayDeque<String>();
			while (!commands.peekFirst().equals("End;"))
			{
				if (commands.peekFirst().startsWith("radio_button"))
				{
					nextRadios.add(commands.peekFirst());
					commands.remove();
				} else
					throw new ParseError("Invalid command in the line:\n\t"
							+ commands.peekFirst()
							+ "\nExpected a radio_button");
			}
			ArrayDeque<JRadioButton> toAdd = nextPart.getNextRadio(nextRadios);
			while (!toAdd.isEmpty())
			{
				createdGUI.add(toAdd.peekFirst());
				toAdd.remove();
			}

		} else
		{
			throw new ParseError("Invalid command in the line:\n\t"
					+ commands.peekFirst());

		}
		commands.remove();
		generateGuiParts();

	}
}
