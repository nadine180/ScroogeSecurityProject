import java.security.SignedObject;

public class Coin {
	int coinID;
	User currentUserID;
	Transaction mostRecentTransaction;
	SignedObject signedCoin;
	static int id = 0;
	public Coin()
	{
		this.coinID = id;
		id++;
	}
	
	public String toString()
	{
		return "Coin ID: " + coinID;
	}
	
}
