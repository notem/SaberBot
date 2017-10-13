package ws.nmathe.saber.commands.general;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import ws.nmathe.saber.Main;
import ws.nmathe.saber.commands.Command;
import ws.nmathe.saber.commands.CommandInfo;
import ws.nmathe.saber.core.schedule.ScheduleEntry;
import ws.nmathe.saber.utils.MessageUtilities;
import ws.nmathe.saber.utils.ParsingUtilities;

import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * used for generating the list of valid timezone strings
 */
public class EventsCommand implements Command
{
    @Override
    public String name()
    {
        return "events";
    }

    @Override
    public CommandInfo info(String prefix)
    {
        String head = prefix + this.name();
        String usage = "``" + head + "`` - lists all events";
        CommandInfo info = new CommandInfo(usage, CommandInfo.CommandType.MISC);

        String cat1 = "- Usage\n" + head + "";
        String cont1 = "This command will generate a list of all events for the guild.\n" +
                "Each event is listed with a short summary detailing the event's title, ID, and start-time.\n" +
                "This command is non-destructive, and can be safely used by non-administrator users.";
        info.addUsageCategory(cat1, cont1);

        info.addUsageExample(head);
        info.addUsageExample(head+" #schedule");

        return info;
    }

    @Override
    public String verify(String prefix, String[] args, MessageReceivedEvent event)
    {
        return "";
    }

    @Override
    public void action(String prefix, String[] args, MessageReceivedEvent event)
    {
        Guild guild = event.getGuild();
        List<String> scheduleIds = Main.getScheduleManager().getSchedulesForGuild(guild.getId());

        // build the embed body content
        int count = 0;
        String content = "";
        for(String sId : scheduleIds)
        {
            Collection<ScheduleEntry> entries = Main.getEntryManager().getEntriesFromChannel(sId);
            if(!entries.isEmpty())
            {
                content += "<#" + sId + "> ...\n";
                for(ScheduleEntry se : entries)
                {
                    content += ":id:``"+ ParsingUtilities.intToEncodedID(se.getId())+"`` - " +
                            "**"+se.getTitle()+ "** at *"+se.getStart().format(DateTimeFormatter.ofPattern("h:mm a, MMM d"))+
                            "* ["+se.getStart().getZone().getDisplayName(TextStyle.NARROW, Locale.getDefault())+"]\n";
                    count++;
                }
                content += "\n";
            }
        }

        String title = "Events on " + guild.getName();          // title for embed
        String footer = count + " event(s)";                      // footer for embed

        // build embed and message
        MessageEmbed embed = new EmbedBuilder()
                                .setFooter(footer, null)
                                .setTitle(title)
                                .setDescription(content).build();

        Message message = new MessageBuilder().setEmbed(embed).build();            // build message
        MessageUtilities.sendMsg(message, event.getTextChannel(), null);     // send message
    }
}