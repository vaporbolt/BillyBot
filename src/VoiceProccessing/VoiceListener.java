package VoiceProccessing;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import BotAutomation.Command;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

/**
 * @author Seth Roper
 * @version 8/4/2022
 * 
 *          Represents the audio listener Discord client and associated commands.
 *
 */
public class VoiceListener{

  public static boolean EXISTS = false;
  private static final HashMap<String, Command> commands = new HashMap<String, Command>();
  public static GatewayDiscordClient gatewayClient;
  public static AudioPlayerManager PLAYER_MANAGER;
  private String token;

  /**
   * Constructs a voice listener object with it's own gatewayClient read from config.json
   */
  public VoiceListener() {
    if (EXISTS) {
      throw new IllegalStateException(
          "Only one voice listener object can be instaniated at a time");
    }

    else {

      populateCommands();
      setToken();
      createClient();
      subscribeToCommands();


    }


  }

  /**
   * Populate chat commands for the listening bot.
   */
  private void populateCommands() {

    commands
        .put("!progress",
            event -> event.getMessage().getChannel().flatMap(
                channel -> channel.createMessage("This command should be updated but too lazy."))
                .then());



    commands.put("leave", event -> {

      Mono<Void> c = event.getClient().getVoiceConnectionRegistry()
          .disconnect(event.getGuildId().get()).then();

      c.subscribe();


      return c;
    });



  }

  private static boolean isFallic(String message) {
    ArrayList<String> fallicList = new ArrayList<String>();
    fallicList.add("penis");
    fallicList.add("cock");
    fallicList.add("dick");
    fallicList.add("fallice");
    fallicList.add("richard");
    fallicList.add("peewee");
    fallicList.add("schlong");
    fallicList.add("tip");
    fallicList.add("shaft");
    fallicList.add("sausage");
    fallicList.add("willie");
    fallicList.add("johnson");
    String lowerCase = message.toLowerCase();
    for (String temp : fallicList) {
      if (lowerCase.contains(temp)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Set the token to the "token" property of the config.json file.
   */
  private void setToken() {

    // parse JSON file for bot token.
    token = "";
    JSONParser parser = new JSONParser();

    try {
      Object obj = parser.parse(new FileReader("DiscordAudioStreamer\\config.json"));
      JSONObject jsonObject = (JSONObject) obj;
      token = (String) jsonObject.get("token");
    }

    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * helper method which instanties the DiscordGateWayClient
   */
  private void createClient() {

    gatewayClient = DiscordClientBuilder.create(token).build().login().block();
  }

  /**
   * subscribes to all commands in response to events generate from the gatewayDiscordClient.
   */
  private void subscribeToCommands() {
    gatewayClient.getEventDispatcher().on(MessageCreateEvent.class)
        // subscribe is like block, in that it will *request* for action
        // to be done, but instead of blocking the thread, waiting for it
        // to finish, it will just execute the results asynchronously.
        .subscribe(event -> {
          final String content = event.getMessage().getContent();
          for (final Map.Entry<String, Command> entry : commands.entrySet()) {
            // We will be using ! as our "prefix" to any command in the system.
            if (content.startsWith('!' + entry.getKey())) {
              entry.getValue().execute(event);
              break;
            }
          }
        });
  }

 

  
  


}
