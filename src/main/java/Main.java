import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class Main extends ListenerAdapter {


    static String mostRecentYeaID = "";
    static MessageReceivedEvent mostRecentEvent;
    static EmbedBuilder eb = new EmbedBuilder();
    static final String command = "!";
    static Map<String, Runnable> commands = new HashMap<>();

    // Populate commands map


    public static void main(String[] args) throws LoginException, IOException {

        //Populates commands map
        commands.put("rank",()-> Rank.run(mostRecentEvent));
        commands.put("scramble",()-> Scramble.run(mostRecentEvent));


        JDA jda = JDABuilder.createDefault(Config.BOT_TOKEN)
                          .addEventListeners(new Main())
                          .setActivity(Activity.listening(" Yea "))
                          .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
                          .setMemberCachePolicy(MemberCachePolicy.ALL) // ignored if chunking enabled
                          .enableIntents(GatewayIntent.GUILD_MEMBERS)
                          .build();
    }


    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        try {
            DBTools.openConnection();
            List<Member> members = event.getGuild().getMembers();
            String GID = event.getGuild().getId().toString();
            for (Member m : members) {
                DBTools.insertGUILD_USER(GID, m.getId(), 0);
            }
            DBTools.closeConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        try {
            DBTools.openConnection();
            DBTools.insertGUILD_USER(event.getGuild().getId(), event.getMember().getId(), 0);
            DBTools.closeConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
    mostRecentEvent = event;
        Message msg = event.getMessage();
        String content = msg.getContentDisplay().trim();    //text content from message
        User author = msg.getAuthor();                      // author object
        String ID = author.getId();                         //unique user ID

// Runs commands from command Hash Map
    if (content.startsWith(command)) {
        String commandContent = content.substring(1).split(" ")[0];
            try {
                commands.get(commandContent).run();
            }catch (NullPointerException E){
                E.printStackTrace();
            }
        }
    //increments yea count if message is not a command
    else if (Tools.yeaCheck(content) && !mostRecentYeaID.equals(ID)) {
            mostRecentYeaID = ID;
            try {
                DBTools.openConnection();
                ResultSet result = DBTools.selectGUILD_USER("where GUILD='" + event.getGuild().getId() + "' and UID='" + mostRecentYeaID + "'");
                assert result != null;
                result.next();
                int count = result.getInt(2) + 1;
                DBTools.updateGUILD_USER(event.getGuild().getId(), ID, count);
                DBTools.closeConnection();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    //if input message is not a command or yea
    else{}
    }





}