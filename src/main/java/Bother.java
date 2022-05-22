import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.audio.hooks.ConnectionListener;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Bother extends ListenerAdapter {
// OI I HAVE NO FUCKIN' CLUE 'OW CONNECTION LISTENAH WOI'KS I'S BEEN PLOGGIN AWEY FER HOURS NO LUCK ATOLL


public static void run(MessageReceivedEvent event){
        Guild guild = event.getGuild();
        Message msg = event.getMessage();
        String content = msg.getContentDisplay().trim();
        User author = msg.getAuthor();
        MessageChannel channel = event.getChannel();
        AudioManager audioManager = guild.getAudioManager();
        AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        TextChannel textChannel = event.getGuild().getTextChannelsByName(channel.getName(), true).get(0);
        PlayerManager.getInstance().bother(audioChannel,textChannel,audioManager);




        }



    }

