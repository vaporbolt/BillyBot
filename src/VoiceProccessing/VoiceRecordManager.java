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
	
	public VoiceRecordManager(SpeechClient speech) {

		if (exists) {
			throw new IllegalStateException("Only one voice record manager object can be instanitated");
		}

		exists = true;
		
		this.speech = speech;
		
		

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
