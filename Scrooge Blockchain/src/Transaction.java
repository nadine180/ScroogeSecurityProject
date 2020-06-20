import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignedObject;
import java.util.ArrayList;
import java.util.Base64;

public class Transaction {

	int amount;
	String transactionID;
	User sender;
	User receiver;
	String message = "";
	String previousTransaction;
	String transactionHash;
	String type;
	Transaction lastTransaction;
	SignedObject signedTransaction;
	String coin;
	static int id = 0;
	
	public Transaction(User sender, User receiver, String coin, int amount)
	{
		this.transactionID = "Transaction "+id+"";
		id++;
		this.sender = sender;
		this.receiver = receiver;
		this.coin = coin;
		this.amount = amount;
		type = "USERTOUSER";
		transactionHash = computeHash();
	}
	
	public Transaction(String coinId)
	{
		this.coin = coinId;
		this.transactionID = "Transaction "+id+"";
		id++;
		type = "CREATE";
		transactionHash = computeHash();
	}
	
	public Transaction (User receiver, String coinID )
	{
		this.coin = coinID;
		this.transactionID ="Transaction "+ id+"";
		id++;
		this.receiver = receiver;
		type = "SCROOGETOUSER";
		transactionHash = computeHash();
	}
	
	public String toString()
	{
		String coinID;

			coinID = this.coin;
		
		
		if(type.equals("USERTOUSER"))
		{
		return "\nTransaction ID: "+transactionID+"\nTransaction Hash: " + transactionHash 
				+ "\nPrevious Hash: "+ previousTransaction 
				+"\nSender ID: "+sender.userID
				+"\nReceiver ID: " + receiver.userID
				+"\nAmount: " + amount 
				+"\nCoin ID: " + coinID +"\nType: " + type+"\n\n";
		
		}
		else
			if(type.equals("CREATE")) {
				String out = "";
				String temp = coinID;
				 out =  "\nTransaction ID: "+transactionID+ "\nTransaction Hash: "+ transactionHash 
						 + "\nPrevious Hash: "+ previousTransaction +
						 "\nSender ID: "+"Scrooge"
				+"\nCoin ID: " + coinID +"\nType: " + type+"\n\n";
				return out;
			} else
				if(type.equals("SCROOGETOUSER"))
					return "\nTransaction ID: "+transactionID+ "\nTransaction Hash: "+ transactionHash
							 + "\nPrevious Hash: "+ previousTransaction 
							+"\nSender ID: "+"Scrooge"+"\nReceiver ID: " + receiver.userID
							+"\nAmount: " + amount 
							+"\nCoin ID: " + coinID +"\nType: " + type+"\n\n";
			
		return null;	
	}
	
	public String computeHash()
	{
		String toHash = "";
		if(type.equals("USERTOUSER"))
			toHash += coin + sender.userID;
		if(type.equals("SCROOGETOUSER"))
			toHash += receiver.userID + amount;
		MessageDigest digest;
		String encoded = null;
		
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(toHash.getBytes(StandardCharsets.UTF_8));
			encoded = Base64.getEncoder().encodeToString(hash);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.transactionHash = encoded;
		
		return encoded;
	}
	
	public boolean isValid()
	{
		
		if(lastTransaction.equals(null))
			return true;
		
		Transaction t = lastTransaction;
		String lastTransactionHash = t.transactionHash;
		if(!lastTransactionHash.equals(transactionHash))
			return false;
		
		return t.isValid();
	
		
	}
	
	public String getHistory()
	{
		if(lastTransaction.type.equals("GENESIS"))
			return this.toString();
		
		return lastTransaction.getHistory() +"\n\n"+ toString();
	}

}
