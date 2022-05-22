import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Sayit {

    public static void joinChannel(Guild guild,User author,String ID) {
        AudioChannel channel = guild.getMember(author).getVoiceState().getChannel();



    }
    public static String pickYeah() {
        try {
            DBTools.openConnection();
            String result = DBTools.selectYEA_AUDIO();
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static void run(MessageReceivedEvent event) {
        // MessageChannel channel = event.
        // MessageChannel textChannel = event.getGuild().getTextChannelsByName(channel.getName(),t
        MessageChannel channel = event.getChannel();
        Message msg = event.getMessage();
        String content = msg.getContentDisplay().trim(); //text content from message
        User author = msg.getAuthor();            // author object
        String ID = author.getId();//unique user ID
        String command = "!";
        String nickname = Objects.requireNonNull(event.getMember()).getEffectiveName();
        TextChannel textChannel = event.getGuild().getTextChannelsByName(channel.getName(), true).get(0);
        String yeahFile = "src/main/resources/yeahs/" + pickYeah();
        AudioChannel audioChannel = event.getGuild().getMember(author).getVoiceState().getChannel();
        PlayerManager.getInstance()
                .loadAndPlay(textChannel,yeahFile,audioChannel);


    }



}




