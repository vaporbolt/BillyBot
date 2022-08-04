package VoiceProccessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;

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
import BotAutomation.BotDriver;
import BotAutomation.Command;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.gateway.GatewayClient;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Seth Roper
 * 
 * @version 7/13/2022
 *
 */
public class VoiceRecordManager implements Runnable {

	public static boolean exists = false;
	public static final String AUDIODIRECTORY = "DiscordAudioStreamer/recordings"; // holds converted .wav files
	public static final File AUDIOFOLDER = new File(AUDIODIRECTORY);
	private SpeechClient speech; // the google Speech Client needed to use api calls.
	private static final HashMap<String, Command> VOICECOMMANDS = new HashMap<String, Command>();
	private GatewayDiscordClient gatewayClient;
	
	public VoiceRecordManager(SpeechClient speech, DiscordClient client, GatewayDiscordClient gatewayClient) {

		if (exists) {
			throw new IllegalStateException("Only one voice record manager object can be instanitated");
		}

		exists = true;
		
		this.speech = speech;
		this.gatewayClient = gatewayClient;
		this.populateCommands();
		
		

	}

	
	
	/**
	 * constantly parses the audio folder for commands.
	 */
	private void update()
	{
		// constantly parse folder.
	
			File[] files = AUDIOFOLDER.listFiles();
			
			for(File file: files)
			{
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String extentsion = FilenameUtils.getExtension(file.getName());
				if(!extentsion.equals("wav"))
				{
					continue;
				}
				try {
					String decodedText = RecognizeSpeech.translateAudio(AUDIODIRECTORY + "/" + file.getName(), speech);
					decodedText = decodedText.toLowerCase();
					String command = parseCommand(decodedText);
					System.out.println(decodedText);
					
					if(command.length() > 0 )
					{
						// execute a voice command. pass null because it is not in response to an event.
						VoiceProducer.VOICECOMMANDS.get(command).execute(null);
					}
					
				
					// delete non pcm files
					if(!extentsion.equals("pcm"))
					{
						file.delete();
					}
				} catch (IOException e) {
					continue;
				}
			}
			
		}
		
	



	@Override
	public void run() {
		while(true)
		{
			update();
		}
		
	}
	
	
	/**
	 * Populates the HashMap of voice commands with commands.
	 */
	private  void populateCommands()
	{
		VOICECOMMANDS.put("can i get a hell yeah", event -> {
			
			Mono<Void> command =  this.gatewayClient.getChannelById(Snowflake.of("860243639253860375"))
		    .cast(MessageChannel.class)
		    .flatMap(MessageChannel -> MessageChannel.createMessage("HELL YEAH BROTHER!")).then();
			command.subscribe();
			return command;
			
		});
		 VOICECOMMANDS.put("play imperial march", event ->
		 {      
				VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376")).cast(VoiceChannel.class).block();
				Mono<Void> command = Mono.fromRunnable(() -> {
				                System.out.println("audio command iniating");
				                final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
				                final VoiceConnection connection = channel.join(spec -> spec.setProvider(provider)).block();
							    final AudioPlayer trackPlayer =  GuildAudioManager.of(channel.getGuildId()).getPlayer();
								BotDriver.PLAYER_MANAGER.loadItem("https://www.youtube.com/watch?v=u7HF4JG1pOg&ab_channel=JohnWilliamsVEVO",  new TrackScheduler(trackPlayer)); 
		
				 });
				
				command.subscribe();
				return command;
		 
				});
		 
		 VOICECOMMANDS.put("stop", event ->
		 {      
			  //  GuildAudioManager.of(Snowflake.of("860243639253860376"));
				VoiceChannel channel = gatewayClient.getChannelById(Snowflake.of("860243639253860376")).cast(VoiceChannel.class).block();
				Mono<Void> command = Mono.fromRunnable(() -> {
					final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
					//final VoiceConnection connection = channel.join(spec -> spec.setProvider(provider)).block();
				                System.out.println("audio command iniating");
							    AudioPlayer trackPlayer =  GuildAudioManager.of(channel.getGuildId()).getPlayer();
								trackPlayer.stopTrack();
		
				 });
				
				command.subscribe();
				return command;
		 
				});

		
	}
	
	
	

	/**
	 * @param command the command to be parsed.
	 * 
	 * @return the command the text contains or an empty string if no command was found.
	 */
	private  String parseCommand(String command)
	{
		Set<String> validCommands = VoiceProducer.VOICECOMMANDS.keySet();
		
		for(String current: validCommands)
		{
			if(command.contains(current))
			{
				System.out.println("valid command detected");
				return current;
			}
		}
		return "";
	}
}
