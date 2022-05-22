import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.audio.hooks.ConnectionListener;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);

    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildID) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public GuildMusicManager getBotherManager(Guild guild, ConnectionListener connectionListener) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildID) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            guild.getAudioManager().setConnectionListener(connectionListener);
            return guildMusicManager;
        });

    }

    //UNUSED for the time being... this is for the !bother command which is supposed to
    //detect when the user talks and then play a random "YEAH" over top of them.
    //The connectionListener seems to work once then something disconnects, and it wont work unles the user
    //leaves and re-joins the voice channel. I tried using an audiorecieve handler and it just SPAMS yeahs like CRAZY
    //There is definitely a way to make this work but for now I am stuck.
    public void bother(AudioChannel audioChannel,TextChannel channel, AudioManager audioManager) {
        try {
            audioManager.openAudioConnection(audioChannel);
        } catch (Exception e) {
            System.out.println("Error while joining " + e.getMessage());
        }
  /*      audioManager.setReceivingHandler(new AudioReceiveHandler() {
            @Override
            public boolean canReceiveCombined() {
                return true;
            }

            @Override
            public boolean canReceiveUser() {
                return false;
            }

            @Override
            public void handleCombinedAudio(@NotNull CombinedAudio combinedAudio) {
                channel.sendMessage("Handling Audio!").queue();
                String yeahFile = "src/main/resources/yeahs/" + Sayit.pickYeah();
                loadAndPlay(channel,yeahFile,audioChannel);
            }

            @Override
            public void handleUserAudio(@NotNull UserAudio userAudio) {
                AudioReceiveHandler.super.handleUserAudio(userAudio);
            }

        });
   */
        audioManager.setConnectionListener(new ConnectionListener() {

            @Override
            public void onPing(long ping) {

            }

            @Override
            public void onStatusChange(@NotNull ConnectionStatus status) {
                channel.sendMessage("Status changed!");
            }

            @Override
            public void onUserSpeaking(@NotNull User user, boolean speaking) {
                channel.sendMessage("Speaking");
                System.out.println(speaking);
                String yeahFile = "src/main/resources/yeahs/" + Sayit.pickYeah();
                getInstance().loadAndPlay(channel,yeahFile,audioChannel);
            }

        });

    }

    public void loadAndPlay(TextChannel channel, final String yeahpath, AudioChannel audioChannel) {
        final GuildMusicManager musicManager =  this.getMusicManager(channel.getGuild());
        this.audioPlayerManager.loadItem(yeahpath, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Yeah").queue();

                play(audioChannel, channel.getGuild(), musicManager, track);

            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {

            }

            @Override
            public void noMatches() {
                channel.sendMessage("Not Found").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play " + yeahpath).queue();
            }
        });
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;

    }

    private static void play(AudioChannel audioChannel, Guild guild, GuildMusicManager musicManager, AudioTrack track) {
        //connectToFirstVoiceChannel(guild.getAudioManager());
        connectToVoiceChannel(guild.getAudioManager(), audioChannel);
        try {
            Thread.sleep(444);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        musicManager.scheduler.queue(track);
        //musicManager.wait(150);
    }

    private static void connectToVoiceChannel(AudioManager audioManager, AudioChannel audioChannel) {
        audioManager.openAudioConnection(audioChannel);

    }
}
