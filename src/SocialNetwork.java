import java.util.HashSet;

import org.mockito.internal.stubbing.answers.ReturnsElementsOf;

import java.util.Collection;

public class SocialNetwork implements ISocialNetwork {
	
	private Account currentUser = null;
	
	private Collection<Account> accounts = new HashSet<Account>();

	// join SN with a new user name
	public Account join(String userName) {
		// new userName should be unique and not empty
		if ((userName != null) && !userName.isEmpty() && !listMembers().contains(userName)) {
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

	// list user names of all members
	public Collection<String> listMembers() {
		Collection<String> members = new HashSet<String>();
		for (Account each : accounts) {
			members.add(each.getUserName());
		}
		return members;
	}

	// from my account, send a friend request to user with userName from my
	// account
	public void sendFriendshipToOld(String userName, Account me) {
		if (listMembers().contains(userName)) {
			Account accountForUserName = findAccountForUserName(userName);
			accountForUserName.requestFriendship(me);
		}
	}

	// from my account, accept a pending friend request from another user with
	// userName
	public void acceptFriendshipFromOld(String userName, Account me) {
		Account accountForUserName = findAccountForUserName(userName);
		accountForUserName.friendshipAccepted(me);
	}

	// from my account, accept all the pending friend requests at once
	public void acceptAllFriendships() {
		Object[] incomingRequests = currentUser.getIncomingRequests().toArray();
		for (Object each : incomingRequests) {
			acceptFriendshipFrom(each.toString());
		}
	}

	// From my account, reject friendship request from another member
	public void rejectFriendshipFromOld(String userName, Account me) {
		Account accountForUserName = findAccountForUserName(userName);
		accountForUserName.friendshipRejected(me);
	}

	// From my account, reject all the pending friendship requests
	public void rejectAllFriendships() {
		Object[] incomingRequests = currentUser.getIncomingRequests().toArray();
		for (Object each : incomingRequests) {
			rejectFriendshipFrom(each.toString());
		}
	}

	// From my account, auto accept all the future friendship request
	public void autoAcceptFriendshipsToOld(Account me) {
		me.autoAcceptFriendships();
	}

	// From my account, send a friendship cancellation to another member
	public void sendFriendshipCancellationToOld(String userName, Account me) {
		Account accountForUsername = findAccountForUserName(userName);
		accountForUsername.cancelFriendship(me);
	}

	// From my account, leave social networks
	public void leaveOld(Account me) {
		Object[] friends = me.getFriends().toArray();
		for (Object each : friends) {
			sendFriendshipCancellationTo(each.toString());
		}

		Object[] incomingRequests = me.getIncomingRequests().toArray();
		for (Object each : incomingRequests) {
			rejectFriendshipFrom(each.toString());
		}

		Object[] outgoingRequests = me.getOutgoingRequests().toArray();
		Account leavingUser = me;
		for (Object each : outgoingRequests) {
			//TODO: rename variables
			login(findAccountForUserName(each.toString()));
			rejectFriendshipFrom(leavingUser.getUserName());
		}
		accounts.remove(me);
	}

	public Account login(Account me) {
		currentUser = me;
		return currentUser;
	}

	public void logout() {
		// TODO Auto-generated method stub
		
	}

	public boolean hasMember(String userName) {
		// TODO Auto-generated method stub
		return false;
	}

	public void sendFriendshipTo(String userName) {
		sendFriendshipToOld(userName, currentUser);
		
	}

	public void block(String userName) {
		// TODO Auto-generated method stub
		
	}

	public void unblock(String userName) {
		// TODO Auto-generated method stub
		
	}

	public void sendFriendshipCancellationTo(String userName) {
		sendFriendshipCancellationToOld(userName, currentUser);
		
	}

	public void acceptFriendshipFrom(String userName) {
		acceptFriendshipFromOld(userName, currentUser);
		
	}

	public void rejectFriendshipFrom(String userName) {
		rejectFriendshipFromOld(userName, currentUser);
		
	}

	public void autoAcceptFriendships() {
		autoAcceptFriendshipsToOld(currentUser);
		
	}

	public void cancelAutoAcceptFriendships() {
		// TODO Auto-generated method stub
		
	}

	public Collection<String> recommendFriends() {
		// TODO Auto-generated method stub
		return null;
	}

	public void leave() {
		leaveOld(currentUser);
		
	}

}
