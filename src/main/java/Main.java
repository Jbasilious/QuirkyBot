import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.mariadb.jdbc.Connection;
import org.mariadb.jdbc.Driver;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;
import java.sql.DriverManager;
import java.util.*;
import java.util.List;

public class Main extends ListenerAdapter {

    //https://discord.com/api/oauth2/authorize?client_id=873594452353110048&permissions=515663986000&scope=bot

    static Map<String, String> yeaCount = new HashMap<String, String>();
    static List<String> reidList = new ArrayList<String>();
    static Properties yeaFileProperties = new Properties();
    static File yeaFile = new File("usr/app/yea.txt");
    static File reidListFile = new File("usr/app/reidlist.txt");
    static String mostRecentYeaID = "";

    EmbedBuilder eb = new EmbedBuilder();

    public static void main(String[] args) throws LoginException, IOException {
        if (!yeaFile.createNewFile()) {
            yeaFileProperties.load(new FileInputStream("usr/app/yea.txt"));
            for (String key : yeaFileProperties.stringPropertyNames()) {
                yeaCount.put(key, (String) yeaFileProperties.get(key));
            }
        }
        if (!reidListFile.createNewFile()) {
            Scanner inFile1 = new Scanner(reidListFile).useDelimiter(",");
            while (inFile1.hasNext()) reidList.add(inFile1.next());
        }

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
        String GID = event.getGuild().getId().toString();



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
            mostRecentYeaID = author.getId();
            int count = 0;
            if (yeaCount.containsKey(ID)) count = 1 + Integer.parseInt(yeaCount.get(ID));
            yeaCount.put(ID, Integer.toString(count));
            Tools.writeYea();
        }

        //shows yea rank, takes args @user, all
        else if (content.startsWith(command + "rank")) {
            String args = content.substring(5).trim();
            String count = yeaCount.get(ID);
            StringBuilder ranks = new StringBuilder();
            StringBuilder names = new StringBuilder();
            StringBuilder counts = new StringBuilder();

            int rank = 1;
            yeaCount = Tools.sortByValue(yeaCount);


            if (content.equals(command + "rank")) {

                for (String key : yeaCount.keySet()) {
                    if (key.equals(ID)) break;
                    else rank++;
                }
                eb.clear();
                eb.setAuthor(nickname, null, author.getEffectiveAvatarUrl());
                eb.setTitle("Your Yea Ranking", null);
                eb.setColor(new Color(0xB24343));
                eb.addField("Rank", Integer.toString(rank), true);
                eb.addField("Count", count, true);

                //displays ranks of tagged users
            } else if (!msg.getMentionedUsers().isEmpty()) {

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

                for (String key : yeaCount.keySet()) {
                    if (userMap.containsKey(key)) {
                        ranks.append(rank).append("\r\n");
                        names.append(userMap.get(key)).append("\r\n");
                        counts.append(yeaCount.get(key)).append("\r\n");
                    }
                    rank++;
                }
                eb.addField("Rank", ranks.toString(), true);
                eb.addField("Name", names.toString(), true);
                eb.addField("Count", counts.toString(), true);


                //displays all stored yea ranks
            } else if (content.equals(command + "rank all")) {

                //initialize embed
                eb.clear();
                eb.setTitle("Your Yea Rankings", null);
                eb.setColor(new Color(0xB24343));

                //interates through yeaCount and retrieves user rank, visible name, count stored in string builders
                for (String key : yeaCount.keySet()) {
                    ranks.append(rank).append("\r\n");
                    names.append(Objects.requireNonNull(event.getGuild().getMemberById(key)).getEffectiveName()).append("\r\n");
                    counts.append(yeaCount.get(key)).append("\r\n");
                    rank++;
                }
                eb.addField("Rank", ranks.toString(), true);
                eb.addField("Name", names.toString(), true);
                eb.addField("Count", counts.toString(), true);
            }

            channel.sendMessageEmbeds(eb.build()).queue();
            Tools.writeYea();
        } else if (content.equalsIgnoreCase(command + "bully reid")) { //reid ID: 170761579195793408
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
    }


    //checks to see if message is a valid "yea"
    public static boolean yeaCheck(String input) {
        input = input.replaceAll("[^\\w\\s]", "");
        return (input.equalsIgnoreCase("yea") || input.equalsIgnoreCase("yeah"));
    }


}