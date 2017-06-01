import java.util.ArrayDeque;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * 
 * @author Dominic Goodman Project 1
 * 
 *         adds all parts of a panel including any nested panels or radio groups
 */
public class GuiPanel
{
	private ArrayDeque<String> components;
	private JPanel nextPanel = new JPanel();
	private PartMaker nextPart = new PartMaker();

	/**
	 * default constructor
	 */
	public GuiPanel()
	{
		components = new ArrayDeque<String>();
	}

	/**
	 * 
	 * @param components
	 *            - parts to be added to the panel
	 */
	public GuiPanel(ArrayDeque<String> components)
	{
		this.components = components;
	}

	/**
	 * 
	 * @param comp
	 *            - component to be added to the panel
	 */
	public void addComponent(String comp)
	{
		components.add(comp);
	}

	/**
	 * 
	 * @return the panel with all components added
	 * @throws ParseError
	 */
	public JPanel getPanel() throws ParseError
	{
		beginPanel();
		return nextPanel;
	}

	/**
	 * 
	 * @return ArrayDeque<String> of the remaining commands
	 */
	public ArrayDeque<String> getRemaining()
	{
		return components;
	}

	/**
	 * Starts creating the panel, sets its format
	 * 
	 * @throws ParseError
	 */
	private void beginPanel() throws ParseError
	{
		if (!components.peekFirst().endsWith(":"))
		{
			throw new ParseError("Invalid command in the line:\n\t"
					+ components.peekFirst() + "\nExpected a :");
		}
		String layout = components.peekFirst().substring(
				components.peekFirst().indexOf("Layout") + 7);
		if (layout.startsWith("Flow"))
		{
			nextPanel.setLayout(new FlowLayout());
		} else if (layout.startsWith("Grid"))
		{
			if (layout.indexOf("(") == -1 || layout.indexOf(")") == -1)
			{
				throw new ParseError("Invalid command in the line:\n\t"
						+ components.peekFirst() + "\nMissing parenthesis");
			}
			String[] formatValues = layout.substring(layout.indexOf("(") + 1,
					layout.lastIndexOf(")")).split("\\s|,");
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
				nextPanel
						.setLayout(new GridLayout(values.get(index), values
								.get(index++), values.get(index++), values
								.get(index++)));
			} else if (values.size() == 2)
			{
				int index = 0;
				nextPanel.setLayout(new GridLayout(values.get(index), values
						.get(index++)));
			} else
			{
				throw new ParseError("Invalid Paramaters in the line:\n\t"
						+ components.peekFirst());
			}
		} else
			throw new ParseError("Invalid Layout in the line:\n\t"
					+ components.peekFirst()
					+ "\nExpected either a Flow or Grid");
		components.remove();
		generatePanel();
	}

	/**
	 * Creates the parts to be added to the panel
	 * 
	 * @throws ParseError
	 */
	public void generatePanel() throws ParseError
	{
		if ((components.peekFirst().indexOf("\"") == components.peekFirst()
				.lastIndexOf("\"") && components.peekFirst().indexOf("\"") > -1)
				|| (components.peekFirst().indexOf("(") != components
						.peekFirst().lastIndexOf("(") && components.peekFirst()
						.indexOf(")") != components.peekFirst()
						.lastIndexOf(")")))
		{
			throw new ParseError("Invalid format in the line:\n\t"
					+ components.peekFirst());
		}
		if (components.peekFirst().startsWith("Panel"))
		{
			GuiPanel nestedPanel = new GuiPanel(components);
			nextPanel.add(nestedPanel.getPanel());
			components = nestedPanel.getRemaining();
		} else if (components.peekFirst().startsWith("Label"))
		{
			nextPanel.add(nextPart.getLabel(components.peekFirst()));
		} else if (components.peekFirst().startsWith("Textfield"))
		{
			nextPanel.add(nextPart.getTextField(components.peekFirst()));

		} else if (components.peekFirst().startsWith("Button"))
		{
			nextPanel.add(nextPart.getButton(components.peekFirst()));
		} else if (components.peekFirst().startsWith("Group"))
		{
			if (components.peekFirst().startsWith("Group:"))
				throw new ParseError("Invalid ending in the line:\n\t"
						+ components.peekFirst() + "\nShould have no ending");

			components.remove();
			ArrayDeque<String> nextRadios = new ArrayDeque<String>();
			while (!components.peekFirst().equals("End;"))
			{
				if (components.peekFirst().startsWith("radio_button"))
				{
					nextRadios.add(components.peekFirst());
					components.remove();
				} else
					throw new ParseError("Invalid command in the line:\n\t"
							+ components.peekFirst()
							+ "\nExpected a radio_button");
			}
			ArrayDeque<JRadioButton> toAdd = nextPart.getNextRadio(nextRadios);
			while (!toAdd.isEmpty())
			{
				nextPanel.add(toAdd.peekFirst());
				toAdd.remove();
			}
		} else if (components.peekFirst().equals("End;"))
		{
			return;
		} else
		{
			if (components.peekFirst().equals("End."))
			{
				throw new ParseError(
						"Panel does not end correctly:\n\t Possible solution change \"End.\" to \"End;\"");
			} else
				throw new ParseError("Invalid command in the line:\n\t"
						+ components.peekFirst());
		}
		components.remove();
		generatePanel();
	}

}
