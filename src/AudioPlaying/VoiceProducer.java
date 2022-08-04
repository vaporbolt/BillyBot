package AudioPlaying;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import reactor.core.publisher.Mono;

public class VoiceProducer {


  public static boolean exists = false;
  public static final HashMap<String, Command> VOICECOMMANDS = new HashMap<String, Command>();
  private static final HashMap<String, Command> CHATCOMMANDS = new HashMap<String, Command>();
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


  public VoiceProducer() {
    if (exists) {
      throw new IllegalStateException("only one VoiceProducer object can exist at a time");
    }
    // create gateway Client
    createClient();
    populateVoiceCommands();
    populateChatCommands();
    distributeEvents();

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
      Object obj = parser.parse(new FileReader("DiscordAudioStreamer\\config.json"));
      JSONObject jsonObject = (JSONObject) obj;
      token = (String) jsonObject.get("voice_token");
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
   * @param commands
   */
  private static void populateVoiceCommands() {


    VOICECOMMANDS.put("play imperial march", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem(
            "https://www.youtube.com/watch?v=u7HF4JG1pOg&ab_channel=JohnWilliamsVEVO",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("stop", event -> {
      // GuildAudioManager.of(Snowflake.of("860243639253860376"));
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
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

    VOICECOMMANDS.put("i'm gonna come ", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\do_not_come.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("should i come", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\do_not_come.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("i'm going to come", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\do_not_come.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("i'm coming", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\do_not_come.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("don't come", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\I'm_gonna_come.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("do not come", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\I'm_gonna_come.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("km", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\kilometer.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("kilometers", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\kilometer.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("wow", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\wow.mp3", new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });
    VOICECOMMANDS.put("unlucky", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\better_luck_next_time.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });
    VOICECOMMANDS.put("bad luck", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\better_luck_next_time.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });
    VOICECOMMANDS.put("oh my god", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\oh_my_god.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("based", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\based.mp3", new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("bass", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\based.mp3", new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("oh shit", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\oh_shit.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("oh ship", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\oh_shit.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("ocean", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\oh_shit.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("holy shit", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\holy_shit.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });


    VOICECOMMANDS.put("holy ship", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\holy_shit.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("holyshit", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\holy_shit.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("do you understand", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\do_you_understand.mp3",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });


    VOICECOMMANDS.put("kill the hoe", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\kill_da_hoe",
            new TrackScheduler(trackPlayer));

      });

      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("kill da hoe", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\kill_da_hoe.mp3",
            new TrackScheduler(trackPlayer));

      });



      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("kill da ho", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\kill_da_hoe.mp3",
            new TrackScheduler(trackPlayer));

      });



      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("kill the ho", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\kill_da_hoe.mp3",
            new TrackScheduler(trackPlayer));

      });



      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("missed", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\I_missed.mp3",
            new TrackScheduler(trackPlayer));

      });



      command.subscribe();
      return command;

    });


    VOICECOMMANDS.put("the world", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\time_stop.mp3",
            new TrackScheduler(trackPlayer));

      });



      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("mountain dew", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\mountain_dew.mp3",
            new TrackScheduler(trackPlayer));

      });



      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("mountain do", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\mountain_dew.mp3",
            new TrackScheduler(trackPlayer));

      });



      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("blasting", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\blasting.mp3",
            new TrackScheduler(trackPlayer));

      });



      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("no use", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\no_use.mp3",
            new TrackScheduler(trackPlayer));

      });



      command.subscribe();
      return command;

    });

    VOICECOMMANDS.put("close", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
          .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        // final VoiceConnection connection = channel.join(spec ->
        // spec.setProvider(provider)).block();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\Barely_made_it.mp3",
            new TrackScheduler(trackPlayer));

      });



      command.subscribe();
      return command;
    });

    VOICECOMMANDS.put("metal gear", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
              .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\metal_gear.mp3",
                new TrackScheduler(trackPlayer));
      });

      command.subscribe();
      return command;
    });

    VOICECOMMANDS.put("nanomachines", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
              .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\nanomachines.mp3",
                new TrackScheduler(trackPlayer));
      });

      command.subscribe();
      return command;
    });

    VOICECOMMANDS.put("psycho mantis", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
              .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\psychomantis.mp3",
                new TrackScheduler(trackPlayer));
      });

      command.subscribe();
      return command;
    });

    VOICECOMMANDS.put("revolution", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
              .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\revolution.mp3",
                new TrackScheduler(trackPlayer));
      });

      command.subscribe();
      return command;
    });

    VOICECOMMANDS.put("they want to", event -> {
      VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376"))
              .cast(VoiceChannel.class).block();
      Mono<Void> command = Mono.fromRunnable(() -> {
        System.out.println("audio command initiating");
        final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
        final AudioPlayer trackPlayer = GuildAudioManager.of(channel.getGuildId()).getPlayer();
        BotDriver.PLAYER_MANAGER.loadItem("UserAudios\\who's_they.mp3",
                new TrackScheduler(trackPlayer));
      });

      command.subscribe();
      return command;
    });




  }

  private static void populateChatCommands() {
    CHATCOMMANDS.put("join", event -> {
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
  private static void distributeEvents() {
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

}
