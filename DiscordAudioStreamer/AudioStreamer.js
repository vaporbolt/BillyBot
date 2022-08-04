// Require the necessary discord.js classes
const { entersState, joinVoiceChannel, VoiceConnectionStatus, EndBehaviorType, VoiceReceiver, getVoiceConnection } = require('@discordjs/voice');
const { createWriteStream } = require('node:fs');
const prism = require('prism-media');
const { pipeline } = require('node:stream');
const { Client, Intents, MessageAttachment, Collection } = require('discord.js');
//const ffmpeg = require('ffmpeg');
const sleep = require('util').promisify(setTimeout);
const fs = require('fs');
const { token } = require('./config.json');
const ffmpeg = require('fluent-ffmpeg');




// Create a new client instance
const client = new Client({
    intents: [
        Intents.FLAGS.GUILDS,
        Intents.FLAGS.GUILD_MESSAGES,
        Intents.FLAGS.GUILD_MEMBERS,
        Intents.FLAGS.GUILD_VOICE_STATES
    ]
})


  /* Collection to store voice state */
client.voiceManager = new Collection()

// When the client is ready, run this code (only once)
client.once('ready', () => {
	console.log('Ready!');
    
});

var receiver = new VoiceReceiver(null);
const map = new Map();
var receiver = null;

var connection = null;
/* When message is sent*/
client.on('messageCreate', async (message) => {
    // time stamp of request
    var currentdate = new Date(); 
    var datetime = "Last Sync: " + currentdate.getDate() + 
     + currentdate.getSeconds();
    /* If content starts with `!record` */
    if (message.content.startsWith('!join')) {
        /* If member do not have admin perms */
        //
       // if (!message.member.permissions.has('ADMINISTRATOR')) return message.channel.send('You do not have permission to use this command.'); 
        /* Get the voice channel the user is in */
        const voiceChannel = message.member.voice.channel
        /* Check if the bot is in voice channel */
         connection = getVoiceConnection(message.guild.id,);
        

        /* If the bot is not in voice channel */
       
            /* if user is not in any voice channel then return the error message */
            if(!voiceChannel) return message.channel.send("You must be in a voice channel to use this command!")

            /* Join voice channel*/
            connection = joinVoiceChannel({
                channelId: voiceChannel.id,
                guildId: voiceChannel.guild.id,
                selfDeaf: false,
                selfMute: false,
                adapterCreator: voiceChannel.guild.voiceAdapterCreator,
            });
                receiver = connection.receiver;
        
       
           
            /* Add voice state to collection */
            client.voiceManager.set(message.channel.guild.id, connection);
            await entersState(connection, VoiceConnectionStatus.Ready, 20e3);
            //receiver = connection.receiver; 

            /* When user speaks in vc*/
           // receiver.speaking.on('start', (userId) => {
                //if(userId !== message.author.id) return;
                /* create live stream to save audio */
               // createListeningStream(receiver, userId, client.users.cache.get(userId), datetime);
          //  });
          
      // time stamp of user speaking.
          
          var datetime = '';

          receiver.speaking.on('start', (userId)  => {
        
            try{

                const user =  voiceChannel.members.get(userId).user;
                if(user.bot)
                {
                    return;
                }

            var currentdate = new Date(); 
             datetime = "LastSync" + currentdate.getDate() + 
            + currentdate.getSeconds();
            
            map.set(userId, datetime);
            
        //if(userId !== message.author.id) return;
        /* create live stream to save audio */
        createListeningStream(receiver, userId, client.users.cache.get(userId), datetime);
            }

            catch(err)
            {
                return;
            }
    

        
    });
            // when the user ends their speech. 
             receiver.speaking.on('end',  (userId) => {
                const user =  voiceChannel.members.get(userId).user;
                try
                {

                    if(user.bot)
                    {
                        return;
                    }
    
            const filename = `./recordings/${userId}${map.get(userId)}`;
            
         
        
            // convert this file into a mp3 to be transcribed later.
         //   if(!fs.existsSync(filename + '.pcm'))
          //  {
           //     fs.mkdir(filename + '.pcm', 8, (err) => {
            //        if (err) {
                      //  return console.error(err);
              //      }
                    
              //  });
               
           // }
           try
           {
            ffmpeg(fs.createReadStream(filename + ".pcm")). 
           toFormat("wav").output(filename + ".wav")
              .on('end', (err) => {
                if(err)
                {
                    return;
                }
                fs.unlinkSync(`${filename}.pcm`) //TODO ISSUE IS HERE FIX WHEN YOU WAKE UP
              }).on("error", function(err) {

                return;

              })
              
              .run()
            }

            catch(err)
            {
                return;
            }
           // const process = new ffmpeg(`${filename}.pcm`);
          //  process.then(function (audio) {
           //     audio.fnExtractSoundToMP3(`${filename}.mp3`, async function (error, file) {
                      //delete the pcm file.
                      

                   // inputOptions([
                      //  '-f s16le',
                      //  '-ar 48.0k',
                     //  '-ac 1'
                     // ])
                    
           //   fs.unlinkSync(`${filename}.pcm`);
          //      });
         //   }, function (err) {
         //       /* handle error by sending error message to discord */
         //       console.log(err);
         //       return;
        //    }

     
            
           // );
            }
            catch(err)
            {
                return;
            }
            });

            /* Return success message */
            return message.channel.send(`🎙️ I am now listening for commands in  ${voiceChannel.name}`);
        
            /* If the bot is in voice channel */
        
        

        
        //else if (connection) {
            /* Send waiting message */
           // const msg = await message.channel.send("Please wait while I am preparing your recording...")
            /* wait for 5 seconds */
          //  await sleep(5000)

            /* disconnect the bot from voice channel */
          // connection.destroy();

            /* Remove voice state from collection */
          //  client.voiceManager.delete(message.channel.guild.id)
            
            // time stamp
         //   const filename = `./recordings/${message.author.id}${datetime}`;

            /* Create ffmpeg command to convert pcm to mp3 */
          //  const process = new ffmpeg(`${filename}.pcm`);
          //  process.then(function (audio) {
              //  audio.fnExtractSoundToMP3(`${filename}.mp3`, async function (error, file) {
                    //edit message with recording as attachment
               //     await msg.edit({
             //           content: `🔉 Here is your recording!`,
              //          files: [new MessageAttachment(`./recordings/${message.author.id}.mp3`, 'recording.mp3')]
              //      });

              //      //delete both files
             //       fs.unlinkSync(`${filename}.pcm`)
             //       fs.unlinkSync(`${filename}.mp3`)
           //     });
          //  }, function (err) {
                /* handle error by sending error message to discord */
           //     return msg.edit(`❌ An error occurred while processing your recording: ${err.message}`);
           // });

        //}

    
    }
    else if (message.content.startsWith('!leave')) {

        if(connection != null)
        {
            connection.destroy();
        }

        else{
            return msg.edit('Bot is not currently in a voice channel');
        }
          


    }
    
})


// create a new stream every time a user speaks.






client.login(token)

/* Function to write audio to file (from discord.js example) */
function createListeningStream(receiver, userId, user, datetime, ) {
    try{
    const opusStream = receiver.subscribe(userId, {
        end: {
            behavior: EndBehaviorType.AfterSilence,
            duration: 100,
        },
    });

    const oggStream = new prism.opus.OggLogicalBitstream({
        opusHead: new prism.opus.OpusHead({
            channelCount: 2,
            sampleRate: 48000,
        }),
        pageSizeControl: {
            maxPackets: 10,
        },
    });


    const filename = './recordings/' + user.id +  datetime +'.pcm';
    console.log(filename);
   // const filename = `./recordings/${user.id}${datetime}.pcm`;

    const out = createWriteStream(filename, { flags: 'a' });
    console.log(`👂 Started recording ${filename}`);

    pipeline(opusStream, oggStream, out, (err) => {
        if (err) {
            console.warn(`❌ Error recording file ${filename} - ${err.message}`);
        } else {
            console.log(`✅ Recorded ${filename}`);
        }
    });
}

catch(err)
{
    return
}
}


