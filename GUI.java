import javax.swing.*;

/**
 * 
 * @author Dominic Goodman Project 1 GUI.java
 * 
 *         Subclass of JFrame to create initialize the basic GUI I will use for
 *         Project 1
 */
class GUI extends JFrame
{
	public GUI(String name)
	{
		super(name);
	}

	public void display()
	{
		setVisible(true);
	}

	private void setFrame(int width, int height)
	{
		setSize(width, height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public void setDimensions(int x, int y)
	{
		setFrame(x, y);
	}

}
