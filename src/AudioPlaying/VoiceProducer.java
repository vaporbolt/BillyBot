package AudioPlaying;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import BotAutomation.BotDriver;
import BotAutomation.Command;
import VoiceProccessing.GuildAudioManager;
import VoiceProccessing.TrackScheduler;
import VoiceProccessing.VoiceRecordManager;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import reactor.core.publisher.Mono;

public class VoiceProducer {


  public static boolean exists = false;
  public static final HashMap<String, Command> VOICECOMMANDS = new HashMap<String, Command>();
  private static final HashMap<String, Command> CHATCOMMANDS = new HashMap<String, Command>();
  private static GatewayDiscordClient gatewayClient;
  private static DiscordClient client;
  public static AudioPlayerManager PLAYER_MANAGER;
  public static VoiceChannel channel;
  static {
    PLAYER_MANAGER = new DefaultAudioPlayerManager();
    // This is an optimization strategy that Discord4J can utilize to minimize allocations
    PLAYER_MANAGER.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
    AudioSourceManagers.registerRemoteSources(PLAYER_MANAGER);
    AudioSourceManagers.registerLocalSource(PLAYER_MANAGER);
  }


  public VoiceProducer() {
    if (exists) {
      throw new IllegalStateException("only one VoiceProducer object can exist at a time");
    }
    // create gateway Client
    createClient();
    populateVoiceCommands();
    populateChatCommands();
    distributeEvents();
    channel = null;

  }



  /**
   * helper method that retrieves the proper token from config.json file.
   * 
   * @return the token stored in the config.json file.
   */
  private static String getToken() {

    // parse JSON file for bot token.
    String token = "";
    JSONParser parser = new JSONParser();

    try {
      File file = new File("DiscordAudioStreamer\\config.json");
      InputStream is = new FileInputStream(file);
  String jsonTxt = IOUtils.toString( is );
  JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON( jsonTxt );  
      //Object obj = parser.parse(new FileReader("DiscordAudioStreamer\\config.json"));
      token =  jsonObject.getString("voice_token");
    }

    catch (Exception e) {
      e.printStackTrace();
    }

    return token;
  }

  /**
   * helper method which initalizes the discordGateWay Client object with proper token.
   */
  private static void createClient() {
    VoiceProducer.gatewayClient = DiscordClientBuilder.create(getToken()).build().login().block();
  }
  
  
  /**
   * subssribes to a single command. to be called when adding commands during runTime.
   * @param command command name
   * @param path the audio path
   */
  public static void addCommand(String voiceCommand, String path)
  {
    VOICECOMMANDS.put(voiceCommand, event -> {
      channel = gatewayClient.getChannelById(channel.getId())
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        PLAYER_MANAGER.loadItem(path,
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });
  }


  /**
   * @param commands
   */
  private static void populateVoiceCommands() {
    
    File file = new File("VoiceCommands.json");
    InputStream is = null;
    
    try {
      is = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String jsonTxt = null;
    try {
      jsonTxt = IOUtils.toString( is );
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
     JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON( jsonTxt );  
    

    JSONArray commands = jsonObject.getJSONArray("voice_commands");
    
    for(int i = 0; i < commands.size(); i++) {
      JSONObject commandObj = commands.getJSONObject(i);
      String voiceCommand = commandObj.getString("command");
      String path = commandObj.getString("audioPath");
      
      VOICECOMMANDS.put(voiceCommand, event -> {
        channel = gatewayClient.getChannelById(channel.getId())
            .cast(VoiceChannel.class).block();
        Mono<Void> command = Mono.fromRunnable(() -> {
          System.out.println("audio command initiating");
          final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
          // final VoiceConnection connection = channel.join(spec ->
          // spec.setProvider(provider)).block();
          final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
          PLAYER_MANAGER.loadItem(path,
              new TrackScheduler(trackPlayer));

        });

        command.subscribe();
        return command;

      });
      
      
    }
    
   

    
    VOICECOMMANDS.put("stop", event -> {
      // GuildAudioManager.of(Snowflake.of("860243639253860376"));
      Mono<Void> command = Mono.fromRunnable(() -> {
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        System.out.println("audio command initiating");
        AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        trackPlayer.stopTrack();

      });

      command.subscribe();
      return command;

    });

    


  }

  private static void populateChatCommands() {
    CHATCOMMANDS.put("join", event -> {
      System.out.println("reached");
      final Member member = event.getMember().orElse(null);
      final VoiceState voiceState = member.getVoiceState().block();
      channel = voiceState.getChannel().block();
      GuildAudioManager.of(channel.getGuildId());
      final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
      Mono<Void> setProvider =
          voiceState.getChannel().block().join(spec -> spec.setProvider(provider)).then();
      setProvider.subscribe();
      return setProvider;

    });

    CHATCOMMANDS.put("leave", event -> {

      Mono<Void> c = event.getClient().getVoiceConnectionRegistry()
          .disconnect(event.getGuildId().get()).then();
      c.subscribe();



      return c;
    });
    
    
  }

  /**
   * helper method which subscribes to all chat commands from the gatewayClient's event dispatcher.
   */
  public static void distributeEvents() {
    gatewayClient.getEventDispatcher().on(MessageCreateEvent.class)
        // subscribe is like block, in that it will *request* for action
        // to be done, but instead of blocking the thread, waiting for it
        // to finish, it will just execute the results asynchronously.
        .subscribe(event -> {
          final String content = event.getMessage().getContent();
          for (final Map.Entry<String, Command> entry : CHATCOMMANDS.entrySet()) {
            // We will be using ! as our "prefix" to any command in the system.
            if (content.startsWith('!' + entry.getKey())) {
              entry.getValue().execute(event);
              break;
            }
          }
        });
  }
  
  /**
   * helper method which updates the JSON file to either replace a command with a given audio file
   * or add a new command.
   * 
   * @param commandName the name of the command to add(what the audio que is)
   * @param audioPath the path of the audio file.
   */
  public static void AddVoiceCommandToJSON(String commandName, String audioPath) {
    
    
    File file = new File("VoiceCommands.json");
    InputStream is = null;
    
    try {
      is = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String jsonTxt = null;
    try {
      jsonTxt = IOUtils.toString( is );
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonTxt);


    JSONArray commands = jsonObject.getJSONArray("voice_commands");

    // replace existing command if it exists.
    for (int i = 0; i < commands.size(); i++) {
      JSONObject commandObj = commands.getJSONObject(i);
      if (commandObj.getString("command").equals(commandName)) {
        File oldAudioFile = new File(commandObj.getString("audioPath"));
        oldAudioFile.delete();
        commandObj.put("audioPath", audioPath);
        try (FileWriter writer = new FileWriter("VoiceCommands.json")) 
        {
          writer.write(jsonObject.toString());
          return;
        } catch (IOException e) {
          e.printStackTrace();
        }
        
      }
      
      // add to list of commands if it doesn't already exist.
      JSONObject addedCommand = new JSONObject();
      addedCommand.put("command", commandName);
      addedCommand.put("audioPath", audioPath);
      
     commands.add(addedCommand);
     try (FileWriter writer = new FileWriter("VoiceCommands.json")) 
     {
       writer.write(jsonObject.toString());
       return;
     } catch (IOException e) {
       e.printStackTrace();
     }

    }



  }
  
  /**
   * should only be called when the command's existence is validated.
   * Removes a command from the JSON file and deletes the associated AudioFile.
   * @param command the name of the command to remove.
   */
  public static void removeVoiceCommandFromJSON(String commandName)
  {
    
    File file = new File("VoiceCommands.json");
    InputStream is = null;
    
    try {
      is = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String jsonTxt = null;
    try {
      jsonTxt = IOUtils.toString( is );
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonTxt);


    JSONArray commands = jsonObject.getJSONArray("voice_commands");

    // replace existing command if it exists.
    for (int i = 0; i < commands.size(); i++) {
      JSONObject commandObj = commands.getJSONObject(i);
      if (commandObj.getString("command").equals(commandName)) {
         File audioFile = new File(commandObj.getString("audioPath"));
         audioFile.delete();
         commands.remove(i);
        try (FileWriter writer = new FileWriter("VoiceCommands.json")) {
          writer.write(jsonObject.toString());
          return;
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
    }
  }

}
