package messages;

import java.io.Serializable;

public class RoomMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	public final String roomName;
	public final String userName;
	public final String message;

	public RoomMessage(String roomName, String userName, String message) {
		this.roomName = roomName;
		this.userName = userName;
		this.message = message;
	}

}
