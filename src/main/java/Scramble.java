import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class Scramble {

    public static void run(MessageReceivedEvent event){
        Message msg = event.getMessage();
        User author = msg.getAuthor();              // author object
        String ID = author.getId();                 //unique user ID
        String nickname = Objects.requireNonNull(event.getMember()).getEffectiveName();

        List<User> users = msg.getMentionedUsers();  //list of tagged users

        for (User u : users) {
            ID = u.getId();
            nickname = Tools.stringScramble(Objects.requireNonNull(event.getGuild().getMemberById(u.getId())).getEffectiveName());
            nickname = nickname.substring(0, 1).toUpperCase() + nickname.substring(1);
            event.getGuild().modifyNickname(Objects.requireNonNull(event.getGuild().getMemberById(ID)), nickname).queue();
        }
    }
}
