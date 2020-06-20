import java.util.LinkedList;

public class Ledger {
	LinkedList<Block> blockchain;
	
	
	public Ledger()
	{
		blockchain = new LinkedList<Block>();
		genesisCreation();
		
	}
	
	public void genesisCreation()
	{
		Block blk = new Block(new LinkedList<Transaction>());
		blk.setPreviousHash(null);
		blk.blockHash = blk.computeHash();
		blockchain.add(blk);
	}
	
	public void addToLedger(Block b)
	{

		b.setPreviousHash(getLastBlock().computeHash());		
		blockchain.add(b);
	}
	
	public void traverseLedger()
	{
		
	}
	
	public Block getLastBlock()
	{
		return blockchain.get(blockchain.size()-1);
	}
	public String toString()
	{
		String out = "Number of blocks: " + (blockchain.size()-1)+"\n\n";
		for(int i = 0; i < blockchain.size();i++)
		{
			out += "Block ID: "+ blockchain.get(i).blockID 
					+"\nBlock Hash: " + blockchain.get(i).computeHash()
					+"\nPrevious Block Hash: " + blockchain.get(i).previousHash
					+ "\nTransactions:\n"+ blockchain.get(i).transactions.toString() 
					+"\n---------------------------------------\n";
		}
		
		
		return out;
		
	}
	
	public String toStringTrimmed()
	{
		String out = "Number of blocks: " + (blockchain.size()-1)+"\n\n";
		
		for(int i = 201 ;i < blockchain.size();i++)
		{
			out += "Block ID: "+ blockchain.get(i).blockID 
					+"\nBlock Hash: " + blockchain.get(i).computeHash()
					+"\nPrevious Block Hash: " + blockchain.get(i).previousHash
					+ "\nTransactions:\n"+ blockchain.get(i).transactions.toString() 
					+"\n---------------------------------------\n";
		}
		
		return out;
	}
	
	public boolean isValid()
	{

		for(int i = 1; i < blockchain.size();i++ )
		{
			String computedHash = blockchain.get(i).computeHash();
			if(!((blockchain.get(i).getBlockHash())
					.equals  ( computedHash )))
			{
				return false;
			}
		}
		
		for(int i = 1; i < blockchain.size();i++)
		{
			if(!   ((blockchain.get(i-1).getBlockHash())
					.equals  (blockchain.get(i).getPreviousHash())  ))
				return false;
		}
		
		return true;
	}
		
}
