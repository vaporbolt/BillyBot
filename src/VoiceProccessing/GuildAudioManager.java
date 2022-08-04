package VoiceProccessing;

import BotAutomation.BotDriver;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import AudioPlaying.VoiceProducer;
import discord4j.common.util.Snowflake;

public final class GuildAudioManager {

	  private static final Map<Snowflake, GuildAudioManager> MANAGERS = new ConcurrentHashMap<>();

	  public static GuildAudioManager of(final Snowflake id) {
	    return MANAGERS.computeIfAbsent(id, ignored -> new GuildAudioManager());
	  }

	  private final AudioPlayer player;
	  private final AudioTrackScheduler scheduler;
	  private final LavaPlayerAudioProvider provider;

	  private GuildAudioManager() {
	    player = VoiceProducer.PLAYER_MANAGER.createPlayer();
	    scheduler = new AudioTrackScheduler(player);
	    provider = new LavaPlayerAudioProvider(player);

	    player.addListener(scheduler);
	  }
	  
	  public LavaPlayerAudioProvider getProvider()
	  {
		  return this.provider;
	  }
	  
	  public AudioPlayer getPlayer()
	  {
		  return this.player;
	  }
	  public AudioTrackScheduler getScheduler()
	  {
		  return this.scheduler;
	  }

	  // getters
	}