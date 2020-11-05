package org.nette.discordbot.forum

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ForumListener : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot && event.channel.idLong == 773007600832872448) {
            if (event.message.contentRaw.contains("https://forum.nette.org")) {
                Thread.sleep(1000)
                event.message.suppressEmbeds(true).queue()
            }
        }
    }
}
