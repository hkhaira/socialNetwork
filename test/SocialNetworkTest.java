import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SocialNetworkTest {

	SocialNetwork sn;
	Account me, her, another;

	@Before
	public void setUp() throws Exception {
		sn = new SocialNetwork();
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		another = sn.join("Harpreet");
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void accountsEmptyOnStartUp() {
		SocialNetwork sn1 = new SocialNetwork();
		assertTrue(sn1.listAllMembers().isEmpty());
	}

	@Test
	public void firstAccountOnSocialNetworkIsCreated() {
		assertNotNull(me);
		assertEquals("Hakan", me.getUserName());
	}

	@Test
	public void canListSingleMemberOfSocialNetworkAfterOnePersonJoiningAndSizeOfNetworkEqualsOne() {
		SocialNetwork sn1 = new SocialNetwork();
		sn1.join("Hakan");
		assertEquals(1, sn1.listAllMembers().size());
		assertTrue(sn1.listAllMembers().contains("Hakan"));
	}

	@Test
	public void twoMemberCanJoinSocialNetworkAndSizeOfNetworkEqualsTwo() {
		SocialNetwork sn1 = new SocialNetwork();
		sn1.join("Hakan");
		sn1.join("Cecile");
		assertEquals(2, sn1.listAllMembers().size());
		assertTrue(sn1.listAllMembers().contains(me.getUserName()));
		assertTrue(sn1.listAllMembers().contains(her.getUserName()));
	}

	@Test
	public void aMemberCanSendAFriendRequestToAnother() throws NoUserLoggedInException {
		assertNotNull(me);
		assertNotNull(her);
		sn.login(me);
		sn.sendFriendshipTo(her.getUserName());
		assertTrue(her.getIncomingRequests().contains(me.getUserName()));
	}

	@Test
	public void aMemberCanAcceptFriendRequestFromAnother() throws NoUserLoggedInException {
		sn.login(me);
		sn.sendFriendshipTo(her.getUserName());
		sn.login(her);
		sn.acceptFriendshipFrom(me.getUserName());
		assertTrue(me.hasFriend(her.getUserName()));
		assertTrue(her.hasFriend(me.getUserName()));
	}

	@Test
	public void aMemberCannotJoinWithEmptyUserName() {
		Account emptyUserName = sn.join("");
		assertNull(emptyUserName);
	}

	@Test
	public void aMemberCannotJoinWithNullUserName() {
		Account nullUserName = sn.join(null);
		assertNull(nullUserName);
	}

	@Test
	public void twoMembersCannotHaveSameUserName() {
		Account duplicate = sn.join("Hakan");
		assertNotNull(me);
		assertNull(duplicate);
	}

	@Test
	public void sendingFriendRequestToNonExistingAccountHasNoEffect() throws NoUserLoggedInException {
		sn.login(me);
		sn.sendFriendshipTo("nonExisting");
		assertFalse(me.getFriends().contains("nonExisiting"));
	}

	@Test
	public void aMemberCanAcceptAllFriendRequestAtOnce() throws NoUserLoggedInException {
		sendTwoFriendRequests(me, her, another);
		sn.login(me);
		sn.acceptAllFriendships();
		assertTrue(me.hasFriend(her.getUserName()));
		assertTrue(her.hasFriend(me.getUserName()));
		assertTrue(me.hasFriend(another.getUserName()));
		assertTrue(another.hasFriend(me.getUserName()));
	}

	@Test
	public void incomingRequestsIsEmptyWhenMemberAcceptAllFriendRequests() throws NoUserLoggedInException {
		sendTwoFriendRequests(me, her, another);
		assertEquals(2, me.getIncomingRequests().size());
		sn.login(me);
		sn.acceptAllFriendships();
		assertTrue(me.getIncomingRequests().isEmpty());
	}

	@Test
	public void outgoingRequestsUpdatesWhenTargetMemberAcceptAllFriendRequests() throws NoUserLoggedInException {
		sendTwoFriendRequests(me, her, another);
		sn.login(me);
		sn.acceptAllFriendships();
		assertFalse(her.getOutgoingRequests().contains(me.getUserName()));
		assertFalse(another.getOutgoingRequests().contains(me.getUserName()));

	}

	private void sendTwoFriendRequests(Account to, Account friend1, Account friend2) throws NoUserLoggedInException {
		sn.login(friend1);
		sn.sendFriendshipTo(to.getUserName());
		sn.login(friend2);
		sn.sendFriendshipTo(to.getUserName());

	}

	@Test
	public void incomingRequestsIsUpdatedWhenMemberRejectFriendRequestFromAnother() throws NoUserLoggedInException {
		sn.login(her);
		sn.sendFriendshipTo(me.getUserName());
		assertTrue(me.getIncomingRequests().contains(her.getUserName()));
		sn.login(me);
		sn.rejectFriendshipFrom(her.getUserName());
		assertFalse(me.getIncomingRequests().contains(her.getUserName()));
	}

	@Test
	public void outgoingRequestsIsUpdatedWhenMemberRejectFriendRequestFromAnother() throws NoUserLoggedInException {
		sn.login(her);
		sn.sendFriendshipTo(me.getUserName());
		assertTrue(her.getOutgoingRequests().contains(me.getUserName()));
		sn.login(me);
		sn.rejectFriendshipFrom(her.getUserName());
		assertFalse(her.getOutgoingRequests().contains(me.getUserName()));
	}

	@Test
	public void incomingRequestsIsUpdatedWhenMemberRejectAllFriendshipRequestsAtOnce() throws NoUserLoggedInException {
		sendTwoFriendRequests(me, her, another);
		assertTrue(me.getIncomingRequests().contains(her.getUserName()));
		assertTrue(me.getIncomingRequests().contains(another.getUserName()));
		sn.login(me);
		sn.rejectAllFriendships();
		assertFalse(me.getIncomingRequests().contains(her.getUserName()));
		assertFalse(me.getIncomingRequests().contains(another.getUserName()));
	}

	@Test
	public void outgoingRequestsIsUpdatedWhenMemberRejectAllFriendRequestsAtOnce() throws NoUserLoggedInException {
		sendTwoFriendRequests(me, her, another);
		assertTrue(her.getOutgoingRequests().contains(me.getUserName()));
		assertTrue(another.getOutgoingRequests().contains(me.getUserName()));
		sn.login(me);
		sn.rejectAllFriendships();
		assertFalse(her.getOutgoingRequests().contains(me.getUserName()));
		assertFalse(another.getOutgoingRequests().contains(me.getUserName()));
	}

	@Test
	public void autoAcceptAllFutureFriendships() throws NoUserLoggedInException {
		assertFalse(me.getFriends().contains(her.getUserName()));
		assertFalse(me.getFriends().contains(another.getUserName()));
		sn.login(me);
		sn.autoAcceptFriendships();
		sendTwoFriendRequests(me, her, another);
		assertTrue(me.getFriends().contains(her.getUserName()));
		assertTrue(me.getFriends().contains(another.getUserName()));
	}

	@Test
	public void incomingRequestsIsUpdatedWhenMemberAutoAcceptAllFutureFriendships() throws NoUserLoggedInException {
		sn.login(me);
		sn.autoAcceptFriendships();
		sendTwoFriendRequests(me, her, another);
		assertFalse(me.getIncomingRequests().contains(her.getUserName()));
		assertFalse(me.getIncomingRequests().contains(another.getUserName()));
	}

	@Test
	public void outgoingRequestsIsUpdatedWhenMemberAutoAcceptAllFutureFriendships() throws NoUserLoggedInException {
		sn.login(me);
		sn.autoAcceptFriendships();
		sendTwoFriendRequests(me, her, another);
		assertFalse(her.getOutgoingRequests().contains(me.getUserName()));
		assertFalse(another.getOutgoingRequests().contains(me.getUserName()));
	}

	@Test
	public void friendshipCancellation() throws NoUserLoggedInException {
		sn.login(her);
		sn.sendFriendshipTo(me.getUserName());
		sn.login(me);
		sn.acceptFriendshipFrom(her.getUserName());
		assertTrue(me.getFriends().contains(her.getUserName()));
		sn.sendFriendshipCancellationTo(her.getUserName());
		assertFalse(me.getFriends().contains(her.getUserName()));
		assertFalse(her.getFriends().contains(me.getUserName()));
	}

	@Test
	public void leaveSocialNetwork() throws NoUserLoggedInException {
		assertTrue(sn.listAllMembers().contains(me.getUserName()));
		sn.login(me);
		sn.leave();
		assertFalse(sn.listAllMembers().contains(me.getUserName()));
	}

	@Test
	public void memberGetsDeletedFromEveryonesFriendListOnLeaving() throws NoUserLoggedInException {
		sendTwoFriendRequests(me, her, another);
		sn.login(me);
		sn.acceptAllFriendships();
		assertTrue(her.getFriends().contains(me.getUserName()));
		assertTrue(another.getFriends().contains(me.getUserName()));
		sn.login(me);
		sn.leave();
		assertFalse(her.getFriends().contains(me.getUserName()));
		assertFalse(another.getFriends().contains(me.getUserName()));
	}

	@Test
	public void memberGetsDeletedFromEveryonesOutgoingListOnLeaving() throws NoUserLoggedInException {
		sendTwoFriendRequests(me, her, another);
		assertTrue(her.getOutgoingRequests().contains(me.getUserName()));
		assertTrue(another.getOutgoingRequests().contains(me.getUserName()));
		sn.login(me);
		sn.leave();
		assertFalse(her.getOutgoingRequests().contains(me.getUserName()));
		assertFalse(another.getOutgoingRequests().contains(me.getUserName()));
	}

	@Test
	public void memberGetsDeletedFromEveryonesIncomingListOnLeaving() throws NoUserLoggedInException {
		sn.login(me);
		sn.sendFriendshipTo(her.getUserName());
		sn.sendFriendshipTo(another.getUserName());
		assertTrue(her.getIncomingRequests().contains(me.getUserName()));
		assertTrue(another.getIncomingRequests().contains(me.getUserName()));
		sn.login(me);
		sn.leave();
		assertFalse(her.getIncomingRequests().contains(me.getUserName()));
		assertFalse(another.getIncomingRequests().contains(me.getUserName()));
	}

	@Test
	public void loginUser() {
		assertNotNull(sn.login(me));
	}

	@Test
	public void hasMemberWhoPreviouslyJoined() throws NoUserLoggedInException {
		sn.login(me);
		assertTrue(sn.hasMember("Hakan"));
	}

	@Test
	public void cancelAutoAcceptFriendships() throws NoUserLoggedInException {
		sn.login(me);
		sn.autoAcceptFriendships();
		sn.login(her);
		sn.sendFriendshipTo(me.getUserName());
		assertTrue(me.hasFriend(her.getUserName()));
		sn.login(me);
		sn.cancelAutoAcceptFriendships();
		sn.login(another);
		sn.sendFriendshipTo(me.getUserName());
		assertFalse(me.hasFriend(another.getUserName()));
		assertTrue(me.getIncomingRequests().contains(another.getUserName()));
	}

	@Test
	public void blockedUserCantSeeMe() throws NoUserLoggedInException {
		sn.login(me);
		sn.block(her.getUserName());
		sn.login(her);
		assertFalse(sn.listMembers().contains(me.getUserName()));
	}

	@Test
	public void blockedUserCantSendMeFriendRequest() throws NoUserLoggedInException {
		sn.login(me);
		sn.block(her.getUserName());
		sn.login(her);
		sn.sendFriendshipTo(me.getUserName());
		assertFalse(me.getIncomingRequests().contains(her.getUserName()));
	}

	@Test
	public void canUnblockAndSendFriendRequest() throws NoUserLoggedInException {
		sn.login(me);
		sn.block(her.getUserName());
		sn.login(her);
		assertFalse(sn.listMembers().contains(me.getUserName()));
		sn.login(me);
		sn.unblock(her.getUserName());
		sn.login(her);
		assertTrue(sn.listMembers().contains(me.getUserName()));
		sn.sendFriendshipTo(me.getUserName());
		assertTrue(me.getIncomingRequests().contains(her.getUserName()));
	}

	@Test
	public void recommendFriends() throws NoUserLoggedInException {
		sn.login(me);
		sn.autoAcceptFriendships();
		sendTwoFriendRequests(me, her, another);
		assertTrue(me.getFriends().contains(her.getUserName()));
		Account fourth = sn.join("khaira");
		sn.login(fourth);
		sn.autoAcceptFriendships();
		sendTwoFriendRequests(fourth, her, another);
		assertTrue(fourth.getFriends().contains(her.getUserName()));
		sn.login(me);
		Collection<String> recommendedFriends = sn.recommendFriends();
		assertTrue(recommendedFriends.contains(fourth.getUserName()));
	}

	@Test
	public void blockingMemberTerminatesPendingFriendships() throws NoUserLoggedInException {
		sn.login(me);
		sn.sendFriendshipTo(her.getUserName());
		sn.login(her);
		assertTrue(her.getIncomingRequests().contains(me.getUserName()));
		sn.block(me.getUserName());
		assertFalse(her.getIncomingRequests().contains(me.getUserName()));
	}

	@Test
	public void blockingMemberTerminatesExistingFriendships() throws NoUserLoggedInException {
		sn.login(me);
		sn.autoAcceptFriendships();
		sn.login(her);
		sn.sendFriendshipTo(me.getUserName());
		assertTrue(her.getFriends().contains(me.getUserName()));
		sn.block(me.getUserName());
		assertFalse(her.getFriends().contains(me.getUserName()));
		assertFalse(me.getFriends().contains(her.getUserName()));

	}

	@Test(expected = NoUserLoggedInException.class)
	public void memberFunctionsThrowsNoUserLoggedInException() throws NoUserLoggedInException {
		sn.listMembers();
		sn.hasMember("");
		sn.sendFriendshipTo("");
		sn.block("");
		sn.unblock("");
		sn.sendFriendshipCancellationTo("");
		sn.acceptFriendshipFrom("");
		sn.rejectFriendshipFrom("");
		sn.autoAcceptFriendships();
		sn.cancelAutoAcceptFriendships();
		sn.recommendFriends();
		sn.leave();
	}
}
