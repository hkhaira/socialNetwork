import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AccountTest {

	Account me, her, another;

	@Before
	public void setUp() throws Exception {
		me = new Account("Hakan");
		her = new Account("Serra");
		another = new Account("Cecile");
	}

	@Test
	public void noFriends() {
		assertEquals(0, me.getFriends().size());
	}

	@Test
	public void noFriendRequests() {
		assertEquals(0, me.getIncomingRequests().size());
	}

	@Test
	public void noOutgoingRequests() {
		assertEquals(0, me.getOutgoingRequests().size());
	}

	@Test
	public void canBefriendAnother() {
		me.requestFriendship(her);
		assertTrue(me.getIncomingRequests().contains(her.getUserName()));
	}

	@Test
	public void MultipleFriendRequests() {
		me.requestFriendship(her);
		me.requestFriendship(another);
		assertEquals(2, me.getIncomingRequests().size());
		assertTrue(me.getIncomingRequests().contains(another.getUserName()));
		assertTrue(me.getIncomingRequests().contains(her.getUserName()));
	}

	@Test
	public void doubleFriendRequestsAreOk() {
		me.requestFriendship(her);
		me.requestFriendship(her);
		assertEquals(1, me.getIncomingRequests().size());
	}

	@Test
	public void afterAcceptingFriendRequestWhoWantsToBeFriendsUpdated() {
		me.requestFriendship(her);
		her.friendshipAccepted(me);
		assertFalse(me.getIncomingRequests().contains(her.getUserName()));
	}

	@Test
	public void everybodyAreFriends() {
		me.requestFriendship(her);
		me.requestFriendship(another);
		her.requestFriendship(another);
		her.friendshipAccepted(me);
		another.friendshipAccepted(her);
		another.friendshipAccepted(me);
		assertTrue(me.hasFriend(her.getUserName()));
		assertTrue(me.hasFriend(another.getUserName()));
		assertTrue(her.hasFriend(me.getUserName()));
		assertTrue(her.hasFriend(another.getUserName()));
		assertTrue(another.hasFriend(her.getUserName()));
		assertTrue(her.hasFriend(me.getUserName()));
	}

	@Test
	public void cannotBefriendAnExistingFriend() {
		me.requestFriendship(her);
		her.friendshipAccepted(me);
		assertTrue(her.hasFriend(me.getUserName()));
		me.requestFriendship(her);
		assertFalse(me.getIncomingRequests().contains(her.getUserName()));
		assertFalse(her.getIncomingRequests().contains(me.getUserName()));
	}

	@Test
	public void outGoingFriendRequests() {
		me.requestFriendship(her);
		assertTrue(her.getOutgoingRequests().contains(me.getUserName()));
	}

	@Test
	public void userIsRemovedFromOutGoingFriendRequestsWhenFriendshipAccepted() {
		me.requestFriendship(her);
		her.friendshipAccepted(me);
		assertFalse(her.getOutgoingRequests().contains(me.getUserName()));
	}

	@Test
	public void outgoingRequestsIsUpdatedWhenFriendshipIsRejected() {
		me.requestFriendship(her);
		her.friendshipRejected(me);
		assertFalse(her.getOutgoingRequests().contains(me.getUserName()));

	}

	@Test
	public void incomingResuestsIsUpdatedWhenFriendshipIsRejected() {
		me.requestFriendship(her);
		her.friendshipRejected(me);
		assertFalse(me.getIncomingRequests().contains(her.getUserName()));
	}

	@Test
	public void autoAcceptFriendRequests() {
		assertFalse(me.getFriends().contains(her.getUserName()));
		me.autoAcceptFriendships();
		me.requestFriendship(her);
		assertTrue(me.getFriends().contains(her.getUserName()));
	}

	@Test
	public void outgoingRequestsIsUpdatedWhenAutoAcceptFriendshipIsOn() {
		me.autoAcceptFriendships();
		me.requestFriendship(her);
		assertFalse(her.getOutgoingRequests().contains(me.getUserName()));
	}

	@Test
	public void incomingResuestsIsUpdatedWhenAutoAcceptFriendshipIsOn() {
		me.autoAcceptFriendships();
		me.requestFriendship(her);
		assertFalse(me.getIncomingRequests().contains(her.getUserName()));
	}

	@Test
	public void cancelFriendship() {
		me.requestFriendship(her);
		her.friendshipAccepted(me);
		assertTrue(me.getFriends().contains(her.getUserName()));
		assertTrue(her.getFriends().contains(me.getUserName()));
		her.cancelFriendship(me);
		assertFalse(me.getFriends().contains(her.getUserName()));
		assertFalse(her.getFriends().contains(me.getUserName()));
	}

	@Test
	public void cannotAcceptFriendshipUntillInitiatedByTheOtherMember() {
		her.friendshipAccepted(me);
		assertFalse(me.getFriends().contains(her.getUserName()));
		assertFalse(her.getFriends().contains(me.getUserName()));
	}

	@Test
	public void receivingFriendRequestFromNullAccount() {
		Account nullAccount = null;
		me.requestFriendship(nullAccount);
	}

}
