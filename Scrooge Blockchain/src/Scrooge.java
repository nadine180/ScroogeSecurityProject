import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

public class Scrooge {

	LinkedList<Transaction> buildingBlock;
	Ledger ledger = new Ledger();
	static int blockID = 0;
	ArrayList<User> users;
	PrivateKey priv;
	PublicKey pub;
	Signature dsa;
	SignedObject lastBlockHashSigned= null;
	Hashtable<String,Coin> coins = new Hashtable<String,Coin>();
	
	public Scrooge()
	{
		coins = new Hashtable<String,Coin>();
		//Ledger ledger = new Ledger();
		buildingBlock = new LinkedList<Transaction>();
		users = new ArrayList<User>();
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keyGen.initialize(1024, random);
			
			KeyPair pair = keyGen.generateKeyPair();
			PrivateKey priv = pair.getPrivate();
			PublicKey pub = pair.getPublic();
			
			this.priv = priv;
			this.pub = pub;
			
			Signature dsa = Signature.getInstance(priv.getAlgorithm()); 
			this.dsa = dsa;
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	//generates the 1000 coins and adds them to a hashtable of coins for scrooge
	public Coin generateCoin()
	{
		for(int i =0; i < 1000;i++)
		{
		Coin c = new Coin();
		try {
			dsa.initSign(priv);
			SignedObject signedObject = new SignedObject((Serializable) c.toString(), priv,dsa );
			
			Transaction t = new Transaction(c.coinID+"");
			t.previousTransaction = null;
			
			dsa.initSign(priv);
			SignedObject signedTransaction = new SignedObject((Serializable) t.toString(), priv,dsa);
			t.signedTransaction = signedTransaction;
			t.lastTransaction = new Transaction(null);
			t.lastTransaction.type = "GENESIS";
			addToBuildingBlock(t);
			c.mostRecentTransaction = t;
			
			c.signedCoin = signedObject;
			
			coins.put((c.coinID+""),c);
			
			
		} catch (InvalidKeyException | SignatureException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		}
		return null;
	}
	
	//add to the blockchain after the building block is completed with 10 verified transactions
	public void addToBlockChain() 
	{		
		
		Block newblock = new Block(buildingBlock);
		publishBlock();
		
		try {
			dsa.initSign(priv);
			lastBlockHashSigned =  new SignedObject((Serializable) newblock.blockHash.toString(), priv,dsa );
		} catch (InvalidKeyException | SignatureException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ledger.addToLedger(newblock);
	}
	
	
	//verifies that the transaction is valid:
	//checks that transaction belongs to owner, not double spending and that the owner actually owns the coin
	public boolean verifyTransaction(Transaction t)
	{
		Boolean valid = true;
		if(t.type.equals("USERTOUSER"))
		{
		User a = t.sender;
		User b = t.receiver;
		SignedObject coin = coins.get(t.coin).signedCoin;
		Transaction previousTransaction = t.lastTransaction;
		
		//verify that the owner initiated the transaction
		try {
			Signature verifyingEngine = Signature.getInstance(a.pub.getAlgorithm());
			Boolean verify = t.signedTransaction.verify(a.pub, verifyingEngine);
			
			if(!verify)
				valid =  false;
			
			
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//verify ownership of coin
		if(previousTransaction.type.equals("USERTOUSER"))
		{
		if(!(previousTransaction.receiver.equals(a.userID) && previousTransaction.type.equals("USERTOUSER")))
			valid =  false;
		}
		//verify not double spending
			String recentHash = t.computeHash();
			int size = buildingBlock.size();
			for(int i = 0; i < size;i++)
			{
				if(t.coin.equals(buildingBlock.get(i).coin))
				{
					System.out.println("DOUBLE SPENDING ERROR: \nTransaction (ID: " + buildingBlock.get(i).transactionID +") was cancelled.");
					valid =  false;
				}
			}
		
		
		}
		return valid;
	}
	
	//adds the transaction to the building block if it is verified and valid
	public void addToBuildingBlock(Transaction t)
	{
			
			//verify

			try {
				if(verifyTransaction(t))
				if(buildingBlock.size() < 10)
				{
					buildingBlock.add(t);
				}
				else
				{
					addToBlockChain();
					
					buildingBlock = new LinkedList<Transaction>();
					buildingBlock.add(t);
				}
				
				
				
				
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		
	}
	
	
	//scrooge pays a coin to the user
	public void payCoinToUser(User temp, String coinID) 
	{
		
		
		try {
			Coin c = coins.get(coinID);
			SignedObject coin = c.signedCoin;
			Transaction newTransaction = new Transaction(temp,coinID);
			dsa.initSign(priv);
			newTransaction.type = "SCROOGETOUSER";
			SignedObject signedTransaction = new SignedObject((Serializable) newTransaction.toString(), priv,dsa );
			newTransaction.signedTransaction = signedTransaction;
			newTransaction.previousTransaction = c.mostRecentTransaction.transactionHash;
			newTransaction.lastTransaction = c.mostRecentTransaction;
			c.mostRecentTransaction = newTransaction;
			addToBuildingBlock(newTransaction);
			temp.currentCoins.add(c);
			
			
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	//performs the needed changes when a block is complete and added to the blockchain. moves coins, updates transactions
	public void publishBlock()
	{
		int size = buildingBlock.size();
		for(int i = 0; i < size;i++)
		{
			Transaction t = buildingBlock.get(i);
			if(t.type.equals("USERTOUSER"))
			{
			int indexSender = users.indexOf(t.sender);
			int indexReceiver = users.indexOf(t.receiver);
			Coin coin = coins.get(t.coin);
			t.lastTransaction = coin.mostRecentTransaction;
			coin.mostRecentTransaction = buildingBlock.get(i);
			coins.remove(coin.coinID+"");
			coins.put(coin.coinID+"",coin);
			User s = users.get(indexSender);
			User r = users.get(indexReceiver);
			users.get(indexReceiver).currentCoins.add(coin);
			users.get(indexSender).currentCoins.remove(coin);

			}
			
		}
	}
	
	
	
}
