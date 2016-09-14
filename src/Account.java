import java.util.HashSet;
import java.util.Set;

public class Account {

	// the unique user name of account owner
	private String userName;

	// list of members who are awaiting an acceptance response from this
	// account's owner
	private Set<String> incomingRequests = new HashSet<String>();

	// list of members whom this account owner wants to be friends with
	private Set<String> outGoingRequests = new HashSet<String>();

	// list of members who are friends of this account's owner
	private Set<String> friends = new HashSet<String>();

	// AutoAccept friendships switch
	private boolean autoAccept = false;

	public Account(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	// return list of members who had sent a friend request to this account's
	// owner
	// and are still waiting for a response
	public Set<String> getIncomingRequests() {
		return incomingRequests;
	}

	// an incoming friend request to this account's owner from another member
	// account
	public void requestFriendship(Account fromAccount) {
		if ((fromAccount != null) && !friends.contains(fromAccount.getUserName())) {
			incomingRequests.add(fromAccount.getUserName());
			fromAccount.outGoingRequests.add(this.getUserName());
			if (autoAccept) {
				fromAccount.friendshipAccepted(this);
			}
		}
	}

	// check if account owner has a member with user name userName as a friend
	public boolean hasFriend(String userName) {
		return friends.contains(userName);
	}

	// receive an acceptance to a friend request from this account owner
	public void friendshipAccepted(Account toAccount) {
		if (outGoingRequests.contains(toAccount.getUserName())
				&& toAccount.getIncomingRequests().contains(this.getUserName())) {
			friends.add(toAccount.getUserName());
			outGoingRequests.remove(toAccount.getUserName());
			toAccount.friends.add(this.getUserName());
			toAccount.incomingRequests.remove(this.getUserName());
		}
	}

	// return list of members who are friends of this account's owner
	public Set<String> getFriends() {
		return friends;
	}

	// return list of members whom this account's owner wants to be friends with
	public Set<String> getOutgoingRequests() {
		return outGoingRequests;
	}

	// receive a rejection to a friend request from this account owner
	public void friendshipRejected(Account toAccount) {
		outGoingRequests.remove(toAccount.getUserName());
		toAccount.incomingRequests.remove(this.getUserName());
	}

	// works as a switch to enable auto-accept friend requests
	public void autoAcceptFriendships() {
		autoAccept = !autoAccept;
	}

	// an incoming cancel friendship request to this account owner from another
	// member
	public void cancelFriendship(Account fromAccount) {
		this.friends.remove(fromAccount.getUserName());
		fromAccount.friends.remove(this.userName);
	}

}
