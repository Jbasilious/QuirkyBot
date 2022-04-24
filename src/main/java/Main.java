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

    EmbedBuilder eb = new EmbedBuilder();

    public static void main(String[] args) throws LoginException, IOException {
       /* if (!yeaFile.createNewFile()) {
            yeaFileProperties.load(new FileInputStream("usr/app/yea.txt"));
            for (String key : yeaFileProperties.stringPropertyNames()) {
                yeaCount.put(key, (String) yeaFileProperties.get(key));
            }
        }
        if (!reidListFile.createNewFile()) {
            Scanner inFile1 = new Scanner(reidListFile).useDelimiter(",");
            while (inFile1.hasNext()) reidList.add(inFile1.next());
        }*/

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

    public void onMemberJoinEvent(GuildMemberJoinEvent event) {
        try {
            DBTools.openConnection();
            DBTools.insertGUILD_USER(event.getGuild().getId(), event.getMember().getId(), 0);
            DBTools.closeConnection();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {    //Test Channel ID 961299108897890305


        MessageChannel channel = event.getChannel();
        Message msg = event.getMessage();
        String content = msg.getContentDisplay().trim(); //text content from message
        User author = msg.getAuthor();            // author object
        String ID = author.getId();//unique user ID
        String command = "!";
        String nickname = Objects.requireNonNull(event.getMember()).getEffectiveName();

        System.out.print(msg.toString() + "\r\n" + ID + "  " + content);

        if (yeaCheck(content) && !mostRecentYeaID.equals(ID)) {
            mostRecentYeaID = ID;
            try {
                DBTools.openConnection();
                ResultSet result = DBTools.selectGUILD_USER("where GUILD='"+event.getGuild().getId()+"' and UID='" + mostRecentYeaID + "'");
                result.next();
                int count = result.getInt(2)+1;
                DBTools.updateGUILD_USER(event.getGuild().getId(), ID, count);
                DBTools.closeConnection();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        } else if (content.startsWith(command)) {
            switch (content.substring(1).split(" ")[0]) {
                case "rank": //shows yea rank, takes args @user, all
                    String args = content.substring(5).trim();
                    // String count = yeaCount.get(ID);
                    StringBuilder ranks = new StringBuilder();
                    StringBuilder names = new StringBuilder();
                    StringBuilder counts = new StringBuilder();

                    int rank = 1;


                    if (content.equals(command + "rank")) {
                        try {
                            DBTools.openConnection();
                            ResultSet result = DBTools.selectGUILD_USER("where GUILD='" + event.getGuild().getId() + "' Order by YEACOUNT desc");
                            while (result.next()) {
                                if (ID.equals(result.getString(1))) {
                                    eb.clear();
                                    eb.setAuthor(nickname, null, author.getEffectiveAvatarUrl());
                                    eb.setTitle("Your Yea Ranking", null);
                                    eb.setColor(new Color(0xB24343));
                                    eb.addField("Rank", Integer.toString(rank), true);
                                    eb.addField("Count", String.valueOf(result.getInt(2)), true);
                                    channel.sendMessageEmbeds(eb.build()).queue();

                                    break;
                                }
                                rank++;
                            }


                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }


                    }


                    //displays ranks of tagged users
                    else if (!msg.getMentionedUsers().isEmpty()) {

                        List<User> users = msg.getMentionedUsers();  //list of tagged users

                        //creates map of (user ID, visible name) from the user list
                        Map<String, String> userMap = new HashMap<String, String>();
                        for (User user : users)
                            userMap.put(user.getId(), event.getGuild().getMemberById(user.getId()).getEffectiveName());
                        userMap.put(ID, event.getGuild().getMemberById(ID).getEffectiveName());//add author to list
                        //initialize embed
                        eb.clear();
                        eb.setTitle("Your Yea Rankings", null);
                        eb.setColor(new Color(0xB24343));


                        try {
                            DBTools.openConnection();
                            ResultSet result = DBTools.selectGUILD_USER("where GUILD='" + event.getGuild().getId() + "' Order by YEACOUNT desc");
                            while (result.next()) {
                                if (userMap.containsKey(result.getString(1))) {
                                    ranks.append(rank).append("\r\n");
                                    names.append(userMap.get(result.getString(1))).append("\r\n");
                                    counts.append(result.getInt(2)).append("\r\n");

                                }
                                rank++;
                            }
                            eb.addField("Rank", ranks.toString(), true);
                            eb.addField("Name", names.toString(), true);
                            eb.addField("Count", counts.toString(), true);
                            channel.sendMessageEmbeds(eb.build()).queue();

                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }


                        //displays all stored yea ranks
                    } else if (content.equals(command + "rank all")) {

                        //initialize embed
                        eb.clear();
                        eb.setTitle("Your Yea Rankings", null);
                        eb.setColor(new Color(0xB24343));

                        //iterates through yeaCount and retrieves user rank, visible name, count stored in string builders
                        try {
                            DBTools.openConnection();
                            ResultSet result = DBTools.selectGUILD_USER("where GUILD='" + event.getGuild().getId() + "' Order by YEACOUNT desc");
                            while (result.next() && result.getInt(2)>0) {
                                ranks.append(rank).append("\r\n");
                                names.append(event.getGuild().getMemberById(result.getString(1)).getEffectiveName()).append("\r\n");
                                counts.append(result.getInt(2)).append("\r\n");
                                rank++;
                            }
                            eb.addField("Rank", ranks.toString(), true);
                            eb.addField("Name", names.toString(), true);
                            eb.addField("Count", counts.toString(), true);
                            channel.sendMessageEmbeds(eb.build()).queue();

                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                    }

                    //Tools.writeYea();
                    break;


                case "scramble":
                    break;
                case "bully":
                    break;

            }


        } /*else if (content.equalsIgnoreCase(command + "bully reid")) { //reid ID: 170761579195793408
            event.getGuild().modifyNickname(event.getGuild().getMemberById("170761579195793408"), reidList.get((int) (Math.random() * reidList.size()))).queue();

        } else if (content.toLowerCase().startsWith(command + "reidlist add") && ID.equals("328689134606614528")) {

            for (String str : reidList) {
                if (content.substring(13).trim().equalsIgnoreCase(str)) {
                    channel.sendMessage("'" + content.substring(13).trim() + "'" + " is already in the list.").queue();
                    return;
                }
            }
            try {
                FileWriter writer = new FileWriter("usr/app/reidlist.txt");
                for (String str : reidList) writer.append(str).append(",");
                writer.append(content.substring(13).trim());
                writer.close();
                reidList.add(content.substring(13).trim());
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (content.equalsIgnoreCase(command + "scramble mora")) {
            String mora = Tools.stringScramble("mora");
            mora = mora.substring(0, 1).toUpperCase() + mora.substring(1);
            event.getGuild().modifyNickname(event.getGuild().getMemberById("303593496521211904"), mora).queue();
        }
    }*/

    }

    //checks to see if message is a valid "yea"
    public static boolean yeaCheck(String input) {
        input = input.replaceAll("[^\\w\\s]", "");
        return (input.equalsIgnoreCase("yea") || input.equalsIgnoreCase("yeah"));
    }


}