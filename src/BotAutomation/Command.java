package BotAutomation;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

/**
 * Classes that implement the Command interface can reactively execute commands
 * based on Certain events.
 * 
 * 
 * 
 * @author Seth Roper
 * @version 7/15/22
 *
 */
public interface Command {

	/**
	 * @param event the  Message event to react to. 
	 * @return a Mono that executes a response upon subscription.
	 */
	Mono<Void> execute(MessageCreateEvent event);
	

}
