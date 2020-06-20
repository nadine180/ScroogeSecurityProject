//The project terminates on entering space or closing the JFrame.
//number of users is 100 -->  user id format: user1, user2,...etc
//each user has 10 coins --> coin id format: 1, 2, 3 ..etc
//to print transaction history for a coin: --> t.getHistory()
//the ledger has two print methods: toString() prints the entire blockchain, toStringTrimmed() prints the new block after initializing coins and paying them to users
//The coins in the created transaction is chosen at random
//the output file is created before termination in the KeyListenerDemo class.
//the last signed hash is found in variable lastBlockHashSigned in the Scrooge Class
//Have fun :)


import java.awt.FlowLayout;
import java.io.IOException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Simulator {

	Scrooge scrooge;
	String origin;
	public Simulator() throws ClassNotFoundException, IOException
	{
		scrooge = new Scrooge();
		scrooge.generateCoin();
		
		for(int i = 0; i < 100;i++)
		{
			User u = new User("user"+ i);
			scrooge.users.add(u);
		}
		
		int count = 1;
		int j = 0;
		for(int i = 0; i < 1000;i++)
		{	
			if(i%10 == 0 && i != 0)
				j++;
			Coin c = scrooge.coins.get(i+"");
			User user = scrooge.users.get(j);
			scrooge.payCoinToUser(user,i+"");
		}	
		
		origin = "";
		
		
		for(int i = 0; i < scrooge.users.size();i++)
		{
		  System.out.println("User: " + scrooge.users.get(i).userID);
		  System.out.println("Public Key: " + scrooge.users.get(i).pub);
		  System.out.println("Current coins: " + scrooge.users.get(i).currentCoins.size());
		  
		  origin += "User: " + scrooge.users.get(i).userID +"\n";
		  origin += "Public Key: " + scrooge.users.get(i).pub + "\n";
		  origin += "Current coins: " + scrooge.users.get(i).currentCoins.size()+"\n\n"; 
		  
		}
				
		 startGUI();
		
	}

	public void startGUI()
	{
		JFrame frame = new JFrame();
		JPanel panel = new JPanel(new FlowLayout());
		
		frame.getContentPane().add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(new KeyListenerDemo(this));
		frame.setSize(500,500);
		frame.setVisible(true);
		int count = 0;
		while(true)
		{
			Random rand = new Random();
			int user1 = rand.nextInt(100);
			int user2 = rand.nextInt(100);
			if(user1 != user2) 
			{
				Transaction t = scrooge.users.get(user1).createTransaction(scrooge.users.get(user2));
				if(t != null)
				{
				scrooge.addToBuildingBlock(t);
				System.out.println(scrooge.buildingBlock);
				count++;
				origin += "\n\nBuilding Block: \n" + scrooge.buildingBlock + "\n\n";
				}
				
				if(count == 11)
				{
					count = 0;
					System.out.println(scrooge.ledger.toStringTrimmed());
					origin +="\n\nBlockchain: \n" + scrooge.ledger.toStringTrimmed() + "\n\n";
				}
			}				
		}
		
	}
	
	
	public String printEverything()
	{
		return origin;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		Simulator s = new Simulator();
	}
	 
}
