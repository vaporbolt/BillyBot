package VoiceProccessing;

import java.io.File;
import java.io.IOException;

/**
 * A Singleton that runs concurrently by running java script code to download the audio produced by
 * individual users on a voice channel.
 * 
 * @author Seth Roper
 * @version 7/12/2022
 * 
 *
 */
public class VoiceStreamer implements Runnable {


  private ProcessBuilder streamProcess;
  private static boolean exists = false;


  /**
   * constructs a new VoiceStreamer if one does not already exist already.
   */
  public VoiceStreamer() {
    if (exists) {
      throw new IllegalStateException("Can not have more than one Voice Streamer object");
    }

    exists = true;
    

    streamProcess = new ProcessBuilder("node", "AudioStreamer.js");
    streamProcess.directory(new File("DiscordAudioStreamer"));
    // set the working directory
    
    // set output to the java console.
    streamProcess.redirectOutput(ProcessBuilder.Redirect.INHERIT);
    streamProcess.redirectError(ProcessBuilder.Redirect.INHERIT);

  }

  @Override
  public void run() {

    // run the audioStreamer node js file.
    try {
      streamProcess.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    

  }
}
