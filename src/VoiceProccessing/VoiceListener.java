package VoiceProccessing;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import AudioPlaying.VoiceProducer;
import BotAutomation.Command;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;
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
        .put("progress",
            event -> 
        {
           Mono<Void> progressCommand = event.getMessage().getChannel().flatMap(
        
                channel -> channel.createMessage("This command should be updated but too lazy.")
                .then());
    
           progressCommand.subscribe();
           return progressCommand;
    
        });
    



    commands.put("leave", event -> {

      Mono<Void> c = event.getClient().getVoiceConnectionRegistry()
          .disconnect(event.getGuildId().get()).then();

      c.subscribe();


      return c;
    });
    
    commands.put("add voice command", event -> {
      
      
      Mono<Void> addCommand = Mono.fromRunnable(() 
          ->
      {
      Message message = event.getMessage();
      String command = "";
      
      String contents  = message.getContent();
      try
      {
        int commandIndex = contents.indexOf(",") + 2;
        command = contents.substring(commandIndex, contents.length());
        
      }
      
      // user inputed invalid command syntax.
      catch(Exception e)
      {
        Mono<Void> error = this.createMessage(event.getMessage().getChannelId(), "Invalid syntax for adding a command. It should look like: !add voice command, command");
        error.subscribe();
        return;
      }
      
      // user did not provide attachment.
      if(message.getAttachments() == null || message.getAttachments().size() != 1)
      {
        Mono<Void> error = createMessage(event.getMessage().getChannelId(), "Please provided a single  mp3 file as an attatchment to this command.");
        error.subscribe();
        return;

      }

      Attachment audio = message.getAttachments().get(0);

      // audio file with that name already exists.
      if (hasAudioFile(audio.getFilename())) {
        Mono<Void> error = createMessage(event.getMessage().getChannelId(),
            "a command with this audio file name already exists. please delete the associated voice command first");
        error.subscribe();
        return;
      }
      
               
     // download audioFile.
      try (BufferedInputStream in = new BufferedInputStream(new URL(audio.getUrl()).openStream());
          FileOutputStream fileOutputStream = new FileOutputStream("UserAudios\\" + audio.getFilename())) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
          Mono<Void> error = createMessage(event.getMessage().getChannelId(),
              "error downloading file.");
          error.subscribe();
          return;
        }
      
      // add to VoiceCommands JSON file 
      VoiceProducer.AddVoiceCommandToJSON(command, "UserAudios\\" + audio.getFilename());
      
      // add command to voice Producer gateway client.
      VoiceProducer.addCommand(command, "UserAudios\\" + audio.getFilename());
      Mono<Void> successMessage = createMessage(event.getMessage().getChannelId(), " sucessfully added command!");
      successMessage.subscribe();
      
      });
      addCommand.subscribe();
      return addCommand;


    });



  }
  
  /**
   * Helper method which posts a message to a message channel.
   * @param messageChannel the snowflake of the message channel to create a message for 
   * @return an unsubcribed Mono<Void> object that creates the given message
   */
  private  Mono<Void> createMessage(Snowflake messageChannel, String text)
  {
    Mono<Void> progressCommand = gatewayClient.getChannelById(messageChannel).flatMap( 
        channel -> ((MessageChannel) channel).createMessage(text)
        .then());

   return progressCommand;
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


  /**
   * Helper method which determines whether a file with that name exists already in the audio folder
   * directory.
   * 
   * @param fileName the file name to check
   * @return true if a file with that name is already in the audio folder, or false otherwise
   */
  private static boolean hasAudioFile(String fileName) {
    File[] files = new File("UserAudios").listFiles();


    for (File file : files) {
      if (file.getName().equals(fileName)) {
        return true;
      }
    }


    return false;

  }



}
