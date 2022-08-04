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
import VoiceProccessing.VoiceListener;
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


  public static void main(String[] args) {


    
    // validate speech to text api
    SpeechClient speech = null;
    try {
      speech = SpeechClient.create();
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    VoiceStreamer streamer = new VoiceStreamer();
    VoiceRecordManager manager = new VoiceRecordManager(speech);
    // create the audio producing bot.
    VoiceProducer producer = new VoiceProducer();
    // create the audio listening bot.
    VoiceListener voiceListener = new VoiceListener();


    // parse voice commands in another thread.
    Thread managingThread = new Thread(manager);
    managingThread.start();
    // stream audio in another thread.
    Thread streamingThread = new Thread(streamer);
    streamingThread.start();
    




  }



}
