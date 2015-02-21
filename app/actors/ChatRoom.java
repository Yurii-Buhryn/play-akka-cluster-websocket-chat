package actors;

import java.util.HashMap;
import java.util.Map;

import messages.RoomMessage;
import messages.UserConnection;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class ChatRoom extends UntypedActor {

	public final Map<String, ActorRef> roomActors;

	public ChatRoom() {
		roomActors = new HashMap<String, ActorRef>();
	}

	@Override
	public void onReceive(Object message) throws Exception {
		
		if (message instanceof UserConnection) {
			UserConnection userConnection = (UserConnection) message;

			ActorRef roomActor = null;
			if (roomActors.containsKey(userConnection.roomName)) {
				roomActor = roomActors.get(userConnection.roomName);
			} else {
				roomActor = getContext().actorOf(Props.create(ChatMessageSender.class),userConnection.roomName);
				roomActors.put(userConnection.roomName, roomActor);
			}
			
			roomActor.tell(userConnection, getSender());
		} else if (message instanceof RoomMessage) {
			RoomMessage roomMessage = (RoomMessage) message;

			if (roomActors.containsKey(roomMessage.roomName)) {
				ActorRef roomActor = roomActors.get(roomMessage.roomName);
				roomActor.tell(roomMessage, null);
			} 
			
		} 
		else {
            unhandled(message);
        }
	}

}
