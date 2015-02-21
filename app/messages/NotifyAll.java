package messages;

import java.io.Serializable;

public class NotifyAll implements Serializable {

	private static final long serialVersionUID = 1L;

	public final RoomMessage message;

	public NotifyAll(RoomMessage message) {
		this.message = message;
	}

}
