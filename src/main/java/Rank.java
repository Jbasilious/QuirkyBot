import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Rank {

        public static void run(MessageReceivedEvent event){


            MessageChannel channel = event.getChannel();
            Message msg = event.getMessage();
            String content = msg.getContentDisplay().trim(); //text content from message
            User author = msg.getAuthor();            // author object
            String ID = author.getId();//unique user ID
            String command = "!";
            String nickname = Objects.requireNonNull(event.getMember()).getEffectiveName();


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
                    while (true) {
                        assert result != null;
                        if (!result.next()) break;
                        if (ID.equals(result.getString(1))) {
                            Main.eb.clear();
                            Main.eb.setAuthor(nickname, null, author.getEffectiveAvatarUrl());
                            Main.eb.setTitle("Your Yea Ranking", null);
                            Main. eb.setColor(new Color(0xB24343));
                            Main.eb.addField("Rank", Integer.toString(rank), true);
                            Main.eb.addField("Count", String.valueOf(result.getInt(2)), true);
                            channel.sendMessageEmbeds(Main.eb.build()).queue();

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
                    userMap.put(user.getId(), Objects.requireNonNull(event.getGuild().getMemberById(user.getId())).getEffectiveName());
                userMap.put(ID, Objects.requireNonNull(event.getGuild().getMemberById(ID)).getEffectiveName());//add author to list
                //initialize embed
                Main.eb.clear();
                Main.eb.setTitle("Your Yea Rankings", null);
                Main.eb.setColor(new Color(0xB24343));


                try {
                    DBTools.openConnection();
                    ResultSet result = DBTools.selectGUILD_USER("where GUILD='" + event.getGuild().getId() + "' Order by YEACOUNT desc");
                    while (true) {
                        assert result != null;
                        if (!result.next()) break;
                        if (userMap.containsKey(result.getString(1))) {
                            ranks.append(rank).append("\r\n");
                            names.append(userMap.get(result.getString(1))).append("\r\n");
                            counts.append(result.getInt(2)).append("\r\n");

                        }
                        rank++;
                    }
                    Main.eb.addField("Rank", ranks.toString(), true);
                    Main.eb.addField("Name", names.toString(), true);
                    Main.eb.addField("Count", counts.toString(), true);
                    channel.sendMessageEmbeds(Main.eb.build()).queue();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }


                //displays all stored yea ranks
            } else if (content.equals(command + "rank all")) {

                //initialize embed
                Main.eb.clear();
                Main.eb.setTitle("Your Yea Rankings", null);
                Main.eb.setColor(new Color(0xB24343));

                //iterates through yeaCount and retrieves user rank, visible name, count stored in string builders
                try {
                    DBTools.openConnection();
                    ResultSet result = DBTools.selectGUILD_USER("where GUILD='" + event.getGuild().getId() + "' Order by YEACOUNT desc");
                    while (true) {
                        assert result != null;
                        if (!(result.next() && result.getInt(2) > 0)) break;
                        ranks.append(rank).append("\r\n");
                        names.append(event.getGuild().getMemberById(result.getString(1)).getEffectiveName()).append("\r\n");
                        counts.append(result.getInt(2)).append("\r\n");
                        rank++;
                    }
                    Main.eb.addField("Rank", ranks.toString(), true);
                    Main.eb.addField("Name", names.toString(), true);
                    Main.eb.addField("Count", counts.toString(), true);
                    channel.sendMessageEmbeds(Main.eb.build()).queue();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }

        }
    }


