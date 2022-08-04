package BotAutomation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.google.cloud.speech.v1.SpeechClient;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import AudioPlaying.VoiceProducer;
import VoiceProccessing.GuildAudioManager;
import VoiceProccessing.LavaPlayerAudioProvider;
import VoiceProccessing.TrackScheduler;
import VoiceProccessing.VoiceRecordManager;
import VoiceProccessing.VoiceStreamer;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import reactor.core.publisher.Mono;

public class BotDriver {
  private static final HashMap<String, Command> commands = new HashMap<String, Command>();
  private static Set<String> commandSet;
  private static GatewayDiscordClient gatewayClient;
  private static DiscordClient client;
  public static AudioPlayerManager PLAYER_MANAGER;
  static {
    PLAYER_MANAGER = new DefaultAudioPlayerManager();
    // This is an optimization strategy that Discord4J can utilize to minimize allocations
    PLAYER_MANAGER.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
    AudioSourceManagers.registerRemoteSources(PLAYER_MANAGER);
    AudioSourceManagers.registerLocalSource(PLAYER_MANAGER);
  }
  private static TrackScheduler scheduler;

  public static void main(String[] args) {

    // parse JSON file for bot token.
    String token = "";
    JSONParser parser = new JSONParser();
    
    try
    {
    Object obj = parser.parse(new FileReader("DiscordAudioStreamer\\config.json"));
    JSONObject jsonObject = (JSONObject)obj;
     token = (String)jsonObject.get("token");
    }
    
    catch(Exception e)
    {
      e.printStackTrace();
    }

    gatewayClient = DiscordClientBuilder
        .create(token).build()
        .login().block();



    commandSet = commands.keySet();

    VoiceProducer producer = new VoiceProducer();

    SpeechClient speech = null;
    try {
      speech = SpeechClient.create();
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }



    VoiceStreamer streamer = new VoiceStreamer();
    VoiceRecordManager manager = new VoiceRecordManager(speech, client, gatewayClient);
    populateCommands(gatewayClient);


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


    // parse voice commands in another thread.
    Thread managingThread = new Thread(manager);
    managingThread.start();
    
    // stream audio in another thread.
    Thread streamingThread = new Thread(streamer);
    streamingThread.start();
    
    // login.block();



  }

  public static Mono<Void> ReadyEventcHandeler(GatewayDiscordClient gateway) {

    Mono<Void> printOnLogin;

    Mono<Channel> general = gateway.getChannelById(Snowflake.of("860243639253860375"));

    printOnLogin = gateway.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
      general.ofType(GuildMessageChannel.class)

          .flatMap(channel -> channel.createMessage("OI CUNTS I HAVE CONNECTED TO THE SERVER"))
          .subscribe();

      final User self = event.getSelf();
      System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());

    })).then();

    return printOnLogin;
  }

  public static Mono<Void> MessageEventHandler(GatewayDiscordClient gateway) {
    Mono<Void> handlePingCommand = gateway.on(MessageCreateEvent.class, event -> {
      Message message = event.getMessage();

      //
      boolean isBot = message.getAuthor().get().isBot();

      // only parse input if it begins with !, meaning it's a command.
      if (!isBot && message.getContent().charAt(0) == '!') {

        // respond with the appropriate command.
        for (String temp : commandSet) {
          if (message.getContent().toLowerCase().equals(temp)) {

            return commands.get(temp).execute(event);
          }
        }
      }


      if (BotDriver.isFallic(message.getContent())) {
        message.addReaction(ReactionEmoji.codepoints("U+1F3F3", "U+FE0F", "U+200D", "U+1F308"))
            .subscribe();
      }

      return Mono.empty();
    }).then();

    return handlePingCommand;
  }



  public static Mono<Void> createLogin(DiscordClient client)

  {


    Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
      // ReadyEvent example


      Mono<Void> printOnLogin = BotDriver.ReadyEventcHandeler(gateway);

      // MessageCreateEvent example
      Mono<Void> handlePingCommand = BotDriver.MessageEventHandler(gateway);


      // combine them!
      return printOnLogin.and(handlePingCommand);
    });

    return login;
  }

  /*
   * private static InputStream createInputStream(File file) { InputStream targetStream = null; try
   * { targetStream = new FileInputStream(file); } catch (FileNotFoundException e) { // TODO
   * Auto-generated catch block e.printStackTrace(); }
   * 
   * return targetStream; }
   */

  /**
   * @param
   * @return
   */
  public static boolean isFallic(String message) {
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
   * @param commands
   */
  private static void populateCommands(GatewayDiscordClient gateway) {

    commands.put("!progress",
        event -> event.getMessage().getChannel().flatMap(channel -> channel.createMessage(
            "Currently this bot only has one command, and that's checking for progress. Voice command implementations coming soon\nAlso reacts with the pride flag when appropriate."))
            .then());


    /*
    commands.put("join", event -> {
      final Member member = event.getMember().orElse(null);
      final VoiceState voiceState = member.getVoiceState().block();
      final VoiceChannel channel = voiceState.getChannel().block();
      GuildAudioManager.of(channel.getGuildId());
      final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
      Mono<Void> setProvider =
          voiceState.getChannel().block().join(spec -> spec.setProvider(provider)).then();
      setProvider.subscribe();
      return setProvider;

    });
    */
    

    commands.put("play imperial march", event -> {
      // GuildAudioManager.of(Snowflake.of("860243639253860376"));
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        System.out.println("audio command initiating");
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        PLAYER_MANAGER.loadItem(
            "https://www.youtube.com/watch?v=u7HF4JG1pOg&ab_channel=JohnWilliamsVEVO",
            new TrackScheduler(trackPlayer));


      });
      command.subscribe();
      return command;

    });



    
      commands.put("leave", event -> {
     
      Mono<Void> c =
      event.getClient().getVoiceConnectionRegistry().disconnect(event.getGuildId().get()).then();
     
     c.subscribe();
     
     
      return c; });
      
     



  }
}
