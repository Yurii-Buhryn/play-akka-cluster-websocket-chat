package actors;

import play.PlayInternal;
import messages.RoomMessage;
import messages.UserConnection;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class ChatRoom extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		
		if (message instanceof UserConnection) {
			UserConnection userConnection = (UserConnection) message;

			ActorRef roomActor = getContext().getChild(userConnection.roomName);
			
			if(roomActor != null) {
				PlayInternal.logger().info("Use existing actor : " + userConnection.roomName);
			} else {
				roomActor = getContext().actorOf(Props.create(ChatMessageSender.class),userConnection.roomName);
				PlayInternal.logger().info("Create new actor : " + userConnection.roomName);
			}
			
			roomActor.tell(userConnection, getSender());
		} else if (message instanceof RoomMessage) {
			RoomMessage roomMessage = (RoomMessage) message;

			ActorRef roomActor = getContext().getChild(roomMessage.roomName);
			
			if(roomActor != null) {
				roomActor.tell(roomMessage, null);
			}			
		} 
		else {
            unhandled(message);
        }
	}

}
