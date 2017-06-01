import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * 
 * @author Dominic Goodman Project 1
 * 
 *         Tests all classes for the GUI parser
 */
public class ParserTest
{

	public static void main(String[] args)
	{
		GUI ui = new GUI("Parser");
		ui.setLayout(new GridLayout(4, 1, 5, 5));
		ui.setDimensions(400, 150);
		final JTextField fileField = new JTextField("");

		final JButton buildButton = new JButton("Build GUI");
		JLabel fileTextField = new JLabel("Input File Name:", JLabel.CENTER);
		final JFileChooser fileChooser = new JFileChooser(".");
		final JButton fileSelectButton = new JButton("Choose File");

		fileSelectButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				int result = fileChooser.showOpenDialog(fileSelectButton);
				if (result == JFileChooser.APPROVE_OPTION)
				{
					fileField.setText(fileChooser.getSelectedFile().toString());
				}

			}
		});

		buildButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				File targetFileIn = new File("");
				Scanner scan = null;
				try
				{
					targetFileIn = new File(fileField.getText());
					scan = new Scanner(targetFileIn);
				} catch (FileNotFoundException e1)
				{
					JOptionPane.showMessageDialog(new GUI("Error"),
							"File Did Not Open");
					return;
				}
				GuiParser gui = new GuiParser();
				String line;
				while (scan.hasNext())
				{
					line = scan.nextLine();
					if (line.length() > 1)
						gui.addCommand(line);
				}
				try
				{
					gui.createGUI();
				} catch (ParseError e1)
				{
					scan.close();
					JOptionPane.showMessageDialog(new GUI("Error"),
							e1.getMessage());
					return;
				}
				scan.close();
			}
		});

		ui.add(fileTextField);
		ui.add(fileField);
		ui.add(fileSelectButton);
		ui.add(buildButton);
		ui.display();

	}
}
