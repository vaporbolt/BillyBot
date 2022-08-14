# BillyBot

Introducing Billy Bot! a customizable discord bot that responds to voice commands in a Discord server!

                                                Check out the tutorial video below to see it's features!

[![Watch the video](https://img.youtube.com/vi/EyHBpJnrkfU/maxresdefault.jpg)](https://www.youtube.com/watch?v=EyHBpJnrkfU&ab_channel=SethLastname)


I host the bot on an an ec2 instance, but currently do not have ec2 configured to handle deployment across many servers. however here are some instructions to host the bot on your local machine!

## Step 1: installing ffmpeg and adding it to your system path

### Windows

follow the guide [here](https://windowsloop.com/install-ffmpeg-windows-10)

### Linux (Mint / Ubuntu)

follow the guide [here](https://www.tecmint.com/install-ffmpeg-in-linux)

## Step 2: Downloading and installing node.js

### Windows

Download and install node from [here](https://nodejs.org/en/download)

### Linux (Mint/ Ubuntu)

Follow the tutorial [here](https://techviewleo.com/how-to-install-nodejs-in-linux-mint) 

## Setting up the config.json file
 Create a config.json file inside the DiscordAudioStreamer directory of your local repo to look like this


![Screenshot 2022-08-13 172304](https://user-images.githubusercontent.com/64103718/184516537-329bbfb4-d2e2-4b07-86a0-9828d8ca587a.png)

Replace the tokens with Two unique Discord Bot tokens one for to produce audio, and one to listen to users.

You can create Discord Bots by going [to the Discord Developer Portal Website](https://discord.com/developers/applications)

To find the token for the bots, navigate to here under the "Bot" settings

![billy bot](https://user-images.githubusercontent.com/64103718/184516594-4355b954-24f3-40ca-a39e-86616194eb09.png)

## Invite the bots into the server

You can invite the bots into a discord server by using the following link

https://discord.com/api/oauth2/authorize?client_id=YOUR_BOT_ID_HERE=0&scope=bot%20applications.commands

replace the YOUR_BOT_ID_HERE with your bot's application ID token which can be found here

![Screenshot 2022-08-13 195633](https://user-images.githubusercontent.com/64103718/184517136-344c0fe1-bad4-440b-9387-b6619707bc9c.png)


## Installing the node Packages

Make sure you have the latest version of python installed (python3.0+)

navigate to the DiscordAudioStreamer directory in your local repo. Make sure you remove the dependency binaries in the package-lock.json file.

and type npm install


![npm install](https://user-images.githubusercontent.com/64103718/184550971-e03372e8-bf0b-4a49-81bf-ad3e60262076.png)



