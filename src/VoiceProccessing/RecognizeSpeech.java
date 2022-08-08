package VoiceProccessing;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import io.grpc.LoadBalancerProvider;
import io.grpc.LoadBalancerRegistry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * @author Seth Roper
 * @version 7/12/2022
 * 
 * A utility class that provides methods useful for invoking google cloud API services.
 *
 */
public class RecognizeSpeech {
	
	public static void main(String[] args)
	{

	
    // Instantiates a client
    SpeechClient speech = null;
	try {
		speech = SpeechClient.create();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
    String fileName = "135181069271040000LastSync2049.wav"; // for example "./resources/audio.raw";

    String translation = "";
	try {
		translation = RecognizeSpeech.translateAudio(fileName, speech);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    		
   
    speech.close();
    
    System.out.println(translation);
	}
    
  
 
  /**
   * Translates a single .wav or mp3 file into text using google's cloud's AI speech to text APi.
   * Currently method only supports English.
   * 
 * @param fileName the relative directory path of the .wav file to be translated. 
 * 
 * @param speech  SpeechClient object that grants permissions to use  google cloud API
 * @return A String representing the most probably translation, or an empty String if the api
 *         returned no probable translations.
 * @throws IOException if file Name is invalid
 */
public static String translateAudio(String fileName, SpeechClient speech) throws IOException
  {
	  // The path to the audio file to transcribe

	    // Reads the audio file into memory
	    Path path = Paths.get(fileName);
	    byte[] data = Files.readAllBytes(path);
	    ByteString audioBytes = ByteString.copyFrom(data);

	    // Builds the sync recognize request
	    RecognitionConfig config =
	        RecognitionConfig.newBuilder()
	            .setEncoding(AudioEncoding.LINEAR16)
	            .setSampleRateHertz(48000)
	            .setLanguageCode("en-US")
	            .setAudioChannelCount(2)
	            .build();
	    RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();
	    // Performs speech recognition on the audio file
	    RecognizeResponse response = speech.recognize(config, audio);
	    List<SpeechRecognitionResult> results = response.getResultsList();
	    
	    // by default it returns an empty String.
	    String mostLikelyTranslation = "";

	      for (SpeechRecognitionResult result : results) {
	           // get the most likely transcription.
	        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
	         mostLikelyTranslation += alternative.getTranscript();
	      }
	      
	      return mostLikelyTranslation;
  }
}