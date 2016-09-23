import java.util.HashSet;

import java.util.Collection;

public class SocialNetwork implements ISocialNetwork {

	private Account currentUser = null;

	private Collection<Account> accounts = new HashSet<Account>();

	// join SN with a new user name
	public Account join(String userName) {
		// new userName should be unique and not empty
		if ((userName != null) && !userName.isEmpty() && !listAllMembers().contains(userName)) {
			Account newAccount = new Account(userName);
			accounts.add(newAccount);
			return newAccount;
		} else
			return null;
	}

	// find a member by user name
	private Account findAccountForUserName(String userName) {
		// find account with user name userName
		// not accessible to outside because that would give a user full access
		// to another member's account
		if (userName != null) {
			for (Account each : accounts) {
				if (each.getUserName().equals(userName))
					return each;
			}
		}
		return null;
	}

	// List all the members of social network
	public Collection<String> listAllMembers() {
		Collection<String> members = new HashSet<String>();
		for (Account each : accounts) {
			members.add(each.getUserName());
		}
		return members;
	}

	// List all members visible to the logged-in user.
	public Collection<String> listMembers() throws NoUserLoggedInException {
		if (currentUser == null)
			throw new NoUserLoggedInException("NoUser");
		Collection<String> visibleMembers = listAllMembers();
		visibleMembers.removeAll(currentUser.getBlockedBy());
		return visibleMembers;

	}

	// from my account, accept all the pending friend requests at once
	public void acceptAllFriendships() throws NoUserLoggedInException {
		if (currentUser == null)
			throw new NoUserLoggedInException("NoUser");
		Object[] incomingRequests = currentUser.getIncomingRequests().toArray();
		for (Object each : incomingRequests) {
			acceptFriendshipFrom(each.toString());
		}
	}

	// From my account, reject all the pending friendship requests
	public void rejectAllFriendships() throws NoUserLoggedInException {
		if (currentUser == null)
			throw new NoUserLoggedInException("NoUser");
		Object[] incomingRequests = currentUser.getIncomingRequests().toArray();
		for (Object each : incomingRequests) {
			rejectFriendshipFrom(each.toString());
		}
	}

	/*
	 * login using a valid Account handle -- only one user can be logged in
	 * returns an updated handle to then member's account
	 */
	public Account login(Account me) {
		currentUser = me;
		return currentUser;
	}

	// log out: the user is no longer logged in after a logout
	public void logout() {
		currentUser = null;
	}

	// This method returns true if a member with user name userName has
	// previously joined the social network
	public boolean hasMember(String userName) throws NoUserLoggedInException {
		if (currentUser == null)
			throw new NoUserLoggedInException("NoUser");
		return listAllMembers().contains(userName);
	}

	// from my account, send a friend request to user with userName from my
	// account
	public void sendFriendshipTo(String userName) throws NoUserLoggedInException {
		if (currentUser == null)
			throw new NoUserLoggedInException("NoUser");
		if (listMembers().contains(userName)) {
			findAccountForUserName(userName).requestFriendship(currentUser);
		}

	}

	// This method prevents the logged-in member from being visible to the
	// member with user name userName.
	public void block(String userName) throws NoUserLoggedInException {
		if (currentUser == null)
			throw new NoUserLoggedInException("NoUser");
		currentUser.addBlockedUser(userName);
		findAccountForUserName(userName).addBlockedBy(currentUser.getUserName());
		if (currentUser.getIncomingRequests().contains(userName))
			rejectFriendshipFrom(userName);
		if (currentUser.getFriends().contains(userName))
			sendFriendshipCancellationTo(userName);
	}

	// Unblock a previously blocked member
	public void unblock(String userName) throws NoUserLoggedInException {
		if (currentUser == null)
			throw new NoUserLoggedInException("NoUser");
		findAccountForUserName(userName).removeBlockedBy(currentUser.getUserName());

	}

	// From my account, send a friendship cancellation to another member
	public void sendFriendshipCancellationTo(String userName) throws NoUserLoggedInException {
		if (currentUser == null)
			throw new NoUserLoggedInException("NoUser");
		Account accountForUsername = findAccountForUserName(userName);
		accountForUsername.cancelFriendship(currentUser);

	}
	
	// from my account, accept a pending friend request from another user with
	// userName
	public void acceptFriendshipFrom(String userName) throws NoUserLoggedInException {
		if (currentUser == null)
			throw new NoUserLoggedInException("NoUser");
		Account accountForUserName = findAccountForUserName(userName);
		accountForUserName.friendshipAccepted(currentUser);

	}

	// From my account, reject friendship request from another member
	public void rejectFriendshipFrom(String userName) throws NoUserLoggedInException {
		if (currentUser == null)
			throw new NoUserLoggedInException("NoUser");
		Account accountForUserName = findAccountForUserName(userName);
		accountForUserName.friendshipRejected(currentUser);

	}

	// From my account, auto accept all the future friendship request
	public void autoAcceptFriendships() throws NoUserLoggedInException {
		if (currentUser == null)
			throw new NoUserLoggedInException("NoUser");
		currentUser.autoAcceptFriendships();
	}

	// Cancel auto-acceptance, and require explicit acceptance in the future
	public void cancelAutoAcceptFriendships() throws NoUserLoggedInException {
		if (currentUser == null)
			throw new NoUserLoggedInException("NoUser");
		currentUser.cancelAutoAcceptFriendships();
	}
	
	/*
	 * Recommend friends to logged-in user: if two friends have a common friend,
	 * include that member in return Collection. Don't recommend members blocked
	 * by the logged-in user
	 */
	public Collection<String> recommendFriends() throws NoUserLoggedInException {
		if (currentUser == null)
			throw new NoUserLoggedInException("NoUser");
		Collection<String> finalList = new HashSet<String>();
		Collection<String> potentialList = listAllMembers();
		potentialList.removeAll(currentUser.getFriends());
		for (String each : potentialList) {
			Collection<String> friendsOfEach = findAccountForUserName(each).getFriends();
			int totalFriends = friendsOfEach.size();
			friendsOfEach.removeAll(currentUser.getFriends());
			int uncommonFriends = friendsOfEach.size();
			if (totalFriends - uncommonFriends >= 2) {
				finalList.add(each);
			}
		}
		finalList.removeAll(currentUser.getBlockedUsers());
		return finalList;
	}

	// From my account, leave social networks
	public void leave() throws NoUserLoggedInException {
		if (currentUser == null)
			throw new NoUserLoggedInException("NoUser");
		Object[] friends = currentUser.getFriends().toArray();
		for (Object each : friends) {
			sendFriendshipCancellationTo(each.toString());
		}

		Object[] incomingRequests = currentUser.getIncomingRequests().toArray();
		for (Object each : incomingRequests) {
			rejectFriendshipFrom(each.toString());
		}

		Object[] outgoingRequests = currentUser.getOutgoingRequests().toArray();
		Account leavingUser = currentUser;
		for (Object each : outgoingRequests) {
			login(findAccountForUserName(each.toString()));
			rejectFriendshipFrom(leavingUser.getUserName());
		}
		accounts.remove(currentUser);

	}

}
