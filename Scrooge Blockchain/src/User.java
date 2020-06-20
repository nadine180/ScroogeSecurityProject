import java.io.IOException;
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
import java.util.Random;

public class User {
	
	
	String userID;
	ArrayList <Coin> currentCoins;
	PrivateKey priv;
	PublicKey pub;
	
	public User()
	{
		
	}
	
	public User(String userID)
	{
		//user id
		currentCoins = new ArrayList<Coin>();
		this.userID = userID;
		
		//creating signature according to the dsa algorithm
		
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keyGen.initialize(1024, random);
			
			KeyPair pair = keyGen.generateKeyPair();
			PrivateKey priv = pair.getPrivate();
			PublicKey pub = pair.getPublic();
			
			this.priv = priv;
			this.pub = pub;
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public Transaction createTransaction(User b)
	{
		
		//choose coin
		int size = currentCoins.size();
		Random rand = new Random();
		if(size == 0)
			return null;
		int random = rand.nextInt(size);
		if(currentCoins.size() > 0)
		{
		Coin coin = currentCoins.get(random);
		
		//create transaction
		Transaction newTransaction = new Transaction(this, b, coin.coinID+"", 1 );
		newTransaction.previousTransaction = coin.mostRecentTransaction.transactionHash;
		newTransaction.lastTransaction = coin.mostRecentTransaction;
		
		//hash transaction
		newTransaction.computeHash();
		
		//sign transaction
		newTransaction = signTransaction(newTransaction);
		
		//send it to scrooge  --the simulator will return it to scrooge
		return newTransaction;
		}
//		System.out.println("ERROR: NO COINS");
		return null;

		
	}
	
	public void addCoins(ArrayList<Coin> coins)
	{
		currentCoins = coins;
	}
	
	public Transaction signTransaction(Transaction t)
	{
		String toSign = t.toString() + t.previousTransaction;
		
		try {
			Signature dsa = Signature.getInstance(priv.getAlgorithm());
			SignedObject signTransaction = new SignedObject(toSign, priv,dsa);
			t.signedTransaction = signTransaction;
			
			return t;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
