import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class KeyListenerDemo implements  KeyListener {

	Simulator simulator;
	
	public KeyListenerDemo(Simulator simulator)
	{
		this.simulator = simulator;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == 32)
		{
 			String origin = simulator.printEverything();
 			File output = new File("output.txt");
 			try {
				FileWriter fw = new FileWriter("output.txt");
				fw.write(origin);
				fw.close();
				System.out.println("Successfully wrote to file");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.exit(0);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
