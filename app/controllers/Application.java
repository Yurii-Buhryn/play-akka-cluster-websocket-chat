package controllers;

import messages.NotifyAll;
import messages.RoomMessage;
import messages.UserConnection;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.chatRoom;
import views.html.index;
import actors.ChatCluster;
import akka.actor.ActorRef;

import com.fasterxml.jackson.databind.JsonNode;

public class Application extends Controller {
     
	public static Result index() {
        return ok(index.render());
    }

    public static Result chatRoom(String chatname, String username) {
        if(username == null || username.trim().equals("")) {
            flash("error", "Please choose a valid username.");
            return redirect(routes.Application.index());
        }
        return ok(chatRoom.render(chatname, username));
    }
    
    public static Result chatRoomJs(final String chatname, final String username) {
        return ok(views.js.chatRoom.render(chatname, username));
    }
    
    public static WebSocket<JsonNode> chat(final String chatname, final String username) {
        return new WebSocket<JsonNode>() {

            public void onReady(WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out){

            		ChatCluster.userConnetion((new UserConnection(chatname, username, out, messages.UserConnection.ConnectionType.CONNECTED)));
            	
                	in.onMessage(new Callback<JsonNode>() {
                		public void invoke(JsonNode event) {
                			 ChatCluster.sendMessage(new NotifyAll(new RoomMessage(chatname, username, event.get("text").asText())));
                		} 
                	});
                	
                    in.onClose(new Callback0() {
                        public void invoke() {
                        	ChatCluster.userConnetion((new UserConnection(chatname, username, out, messages.UserConnection.ConnectionType.DISCONNECTED)));
                        }
                     });
                }
        };
    }
    
	public static class ActorBox {

		private ActorRef actor;

		public ActorBox () {
			actor = null;
		}
		
		public ActorRef getActor() {
			return actor;
		}

		public void setActor(ActorRef actor) {
			this.actor = actor;
		}

	}
  
}