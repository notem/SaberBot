package ws.nmathe.saber.utils;

import ws.nmathe.saber.Main;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.time.Month;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * static methods frequently used in the verify() methods used by classes implementing the
 * Command interface.
 */
public class VerifyUtilities
{
    public static boolean verifyTime(String arg)
    {
        if( arg.toUpperCase().endsWith("AM") || arg.toUpperCase().endsWith("PM") )
        {
            String[] start = arg.substring(0,arg.length()-2).split(":");
            if (start.length != 2)
                return false;
            if (start[0].length() > 2)
                return false;
            if( !verifyInteger( start[0] ) )
                return false;
            if (Integer.parseInt(start[0]) > 12 || Integer.parseInt(start[0]) == 0)
                return false;
            if (start[1].length() > 2)
                return false;
            if( !verifyInteger( start[1] ) )
                return false;
            if (Integer.parseInt(start[1]) > 59)
                return false;

        }
        else
        {
            String[] start = arg.split(":");
            if (start.length != 2)
                return false;
            if (start[0].length() > 2)
                return false;
            if( !verifyInteger( start[0] ) )
                return false;
            if (Integer.parseInt(start[0]) > 24)
                return false;
            if (start[1].length() > 2)
                return false;
            if( !verifyInteger( start[1] ) )
                return false;
            if (Integer.parseInt(start[1]) > 59)
                return false;
            if (Integer.parseInt(start[0]) == 24 && Integer.parseInt(start[1]) != 0)
                return false;
        }
        return true;
    }

    public static boolean verifyDate( String arg )
    {
        String[] date = arg.split("/");
        if(date.length != 2)
            return false;
        if( verifyInteger(date[0]) )
            return false;
        else
        {
            if(Integer.parseInt(date[0])>12||Integer.parseInt(date[0])==0)
                return false;
        }
        if( verifyInteger(date[2]) )
            return false;
        else
        {
            if(Integer.parseInt(date[1])>Month.of(Integer.parseInt(date[0])).minLength()
                    ||Integer.parseInt(date[0])==0)
                return false;
        }

        return true;
    }

    public static boolean verifyHex( String arg )
    {
        try
        {
            Integer.decode("0x"+arg);
        }
        catch( Exception e )
        {
            return false;
        }

        return true;
    }

    public static boolean verifyRepeat( String arg )
    {
        String argLower = arg.toLowerCase();
        if(argLower.equals("no")||argLower.equals("weekly")||argLower.equals("daily"))
            return true;

        else if( verifyInteger( arg ) && Integer.parseInt( arg ) <= 2 )
            return true;

        return false;
    }

    public static boolean verifyInteger( String arg )
    {
        try
        {
            Integer.parseInt(arg);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public static boolean verifyScheduleChannel( Guild guild )
    {
        Collection<TextChannel> chans = GuildUtilities.getValidScheduleChannels(guild);
        if( chans == null || chans.isEmpty() )
        {
            return false;
        }

        Member botAsMember = guild.getMember(Main.getBotSelfUser());

        List<Permission> perms = Arrays.asList( // required permissions
                Permission.MESSAGE_HISTORY, Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE, Permission.MESSAGE_MANAGE
        );

        for( TextChannel chan : chans ) // if any one channel has required permissions
        {
            if( botAsMember.hasPermission( chan, perms ) )
            {
                return true;
            }
        }
        return false;
    }
}
