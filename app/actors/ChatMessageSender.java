package actors;

import java.util.HashSet;
import java.util.Set;

import messages.RoomMessage;
import messages.UserConnection;
import play.libs.Json;
import play.mvc.WebSocket;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ChatMessageSender extends UntypedActor {

	private final Set<WebSocket.Out<JsonNode>> channelsMap;

	public ChatMessageSender() {
		channelsMap = new HashSet<WebSocket.Out<JsonNode>>();
	}

	@Override
	public void onReceive(Object message) throws Exception {
		
		if (message instanceof UserConnection) {
			UserConnection connection = (UserConnection) message;

			switch (connection.connectionType) {
			case CONNECTED:
				channelsMap.add(connection.channel);
				break;
			case DISCONNECTED:
				channelsMap.remove(connection.channel);
				break;
			}
		} else if (message instanceof RoomMessage) {
			RoomMessage roomMessage = (RoomMessage) message;
			notifyAll(roomMessage);
		} else {
			unhandled(message);
		}
	}
	
	private void notifyAll(RoomMessage message) {
		for (WebSocket.Out<JsonNode> channel : channelsMap) {

			ObjectNode event = Json.newObject();
			event.put("kind", "talk");
			event.put("user", message.userName);
			event.put("message", message.message);

			channel.write(event);
		}
	}

}
