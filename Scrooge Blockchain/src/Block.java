import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignedObject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;


public class Block { 
	String blockHash;
	String previousHash;
	String blockID;
	static int id = 0;
	LinkedList<Transaction> transactions;
	
	
	public Block(LinkedList<Transaction> transactions)
	{
		
		this.transactions = transactions;
		this.blockHash = computeHash();
		this.blockID = id+"";
		id++;
		
		
	}
	
	public void addTransactions(ArrayList<Transaction> t)
	{
		for(int i = 0; i < t.size();i++)
		{
			this.transactions.add(t.get(i));
		}
	}
	
	public String computeHash()
	{
		String toHash = previousHash+""+transactions.toString();
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
		
		this.blockHash = encoded;
		
		return encoded;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public String getBlockHash() {
		return blockHash;
	}

	
}
