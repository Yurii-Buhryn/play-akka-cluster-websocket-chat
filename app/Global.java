import actors.ChatCluster;
import akka.actor.Props;

import com.google.inject.AbstractModule;  
import com.google.inject.Guice;  
import com.google.inject.Injector;  

import play.Application;  
import play.GlobalSettings;
import play.libs.Akka;

public class Global extends GlobalSettings {
    private Injector injector;
    
    @Override
    public void onStart(Application application) {
    	ChatCluster.CHAT_CLUSTER = Akka.system().actorOf(Props.create(ChatCluster.class),"ChatCluster");
    	
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
            }
        });
    }
 
    @Override
    public <T> T getControllerInstance(Class<T> aClass) throws Exception {
        return injector.getInstance(aClass);
    }
}
