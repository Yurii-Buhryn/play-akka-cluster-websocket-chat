package messages;

import java.io.Serializable;

import play.mvc.WebSocket;
import play.mvc.WebSocket.Out;

import com.fasterxml.jackson.databind.JsonNode;

public class UserConnection implements Serializable {

	private static final long serialVersionUID = 1L;

	public static enum ConnectionType {
		CONNECTED, DISCONNECTED
	}

	public final String roomName;
	public final String userName;
	public final WebSocket.Out<JsonNode> channel;
	public final ConnectionType connectionType;

	public UserConnection(String roomName, String userName, Out<JsonNode> channel, ConnectionType connectionType) {
		this.roomName = roomName;
		this.userName = userName;
		this.channel = channel;
		this.connectionType = connectionType;
	}

}
