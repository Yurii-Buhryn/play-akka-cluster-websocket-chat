package actors;

import java.util.ArrayList;
import java.util.List;

import messages.NotifyAll;
import messages.RoomMessage;
import messages.UserConnection;
import play.PlayInternal;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;

public class ChatCluster extends UntypedActor {

	public static  ActorRef CHAT_CLUSTER;
	private final Cluster cluster;
	private final List<Member> clusterMemberList;
	private final ActorRef chatRoom;

	public ChatCluster() {
		cluster = Cluster.get(getContext().system());
		clusterMemberList = new ArrayList<Member>();
		chatRoom = getContext().actorOf(Props.create(ChatRoom.class));
	}
	
	public static void userConnetion(UserConnection connection) {
		CHAT_CLUSTER.tell(connection, null);
	}
	
	public static void sendMessage(NotifyAll message) {
		CHAT_CLUSTER.tell(message, null);
	}

	@Override
	public void preStart() {
		cluster.subscribe(getSelf(), 
				ClusterEvent.initialStateAsEvents(),
				ClusterEvent.MemberEvent.class,
				ClusterEvent.UnreachableMember.class);
	}

	@Override
	public void postStop() {
		cluster.unsubscribe(getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof UserConnection) {
			UserConnection userConnection = (UserConnection) message;
			chatRoom.tell(userConnection, getSelf());
		} else	if (message instanceof RoomMessage) {
			RoomMessage roomMessage = (RoomMessage) message;
			chatRoom.tell(roomMessage, getSelf());	
		} else if (message instanceof NotifyAll ) {
			RoomMessage roomMessage = ((NotifyAll) message).message;
			
            PlayInternal.logger().info("---------------> start notify ");
            
            if (clusterMemberList.size() != 0) {
                for (Member member : clusterMemberList) {
                    PlayInternal.logger().info("---------------> notify = {}", member.address());
                    getContext().actorSelection(member.address() + "/user/ChatCluster").tell(roomMessage, getSelf());
                }
            }else{
            	CHAT_CLUSTER.tell(roomMessage, getSelf());
            }
		}
		
		
		else if (message instanceof ClusterEvent.MemberUp) {
            ClusterEvent.MemberUp memberUp = (ClusterEvent.MemberUp) message;
            PlayInternal.logger().info("---------------> Member is Up: {}", memberUp.member());
            clusterMemberList.add(memberUp.member());
        } else if (message instanceof ClusterEvent.UnreachableMember) {
            ClusterEvent.UnreachableMember memberUnreachable = (ClusterEvent.UnreachableMember) message;
            PlayInternal.logger().info("---------------> Member detected as unreachable: {}", memberUnreachable.member());
        } else if (message instanceof ClusterEvent.MemberRemoved) {
            ClusterEvent.MemberRemoved memberRemoved = (ClusterEvent.MemberRemoved) message;
            PlayInternal.logger().info("---------------> Member is Removed: {}", memberRemoved.member());
            clusterMemberList.remove(memberRemoved.member());
        } else if (message instanceof ClusterEvent.MemberEvent) {
        	// ignore
        }else
            unhandled(message);
    	}

}
