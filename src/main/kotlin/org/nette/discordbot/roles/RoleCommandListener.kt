package org.nette.discordbot.roles

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

class RoleCommandListener : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val content = event.message.contentRaw
        val member = event.member

        if (event.channel.name != "commands" || member == null || !content.startsWith('.')) {
            return
        }

        val args = content.split(" ").toMutableList()
        val command: String = args[0].replace(".", "")
        args.removeFirst()

        val r = Runnable {
            if (command.equals("addlang", true)) {
                if (args.isEmpty()) {
                    event.channel.sendMessage(
                        "${member.asMention}, arguments are missing, example usage is `.addlang php nette javascript`."
                    ).queue()
                    return@Runnable
                }

                val rolesGiven = mutableListOf<String>()
                val roles = event.guild.roles.filterNotNull()
                    .filter {
                        !it.isHoisted && !it.permissions.contains(Permission.ADMINISTRATOR) && !it.name.contains(
                            "everyone",
                            true
                        )
                    }

                for (arg in args) {
                    roles.forEach {
                        if (it.name.equals(arg, true)) {
                            event.guild.addRoleToMember(member, it).queue()
                            rolesGiven.add(it.name)
                        }
                    }
                }

                if (rolesGiven.isNotEmpty()) {
                    event.channel.sendMessage(
                        "${member.asMention}, following languages were given to you: **${rolesGiven.joinToString("**, **")}**"
                    ).queue()
                } else {
                    event.channel.sendMessage(
                        "${member.asMention}, requested language was not found, type `.langs`"
                    ).queue()
                }

            } else if (command.equals("remlang", true)) {
                if (args.isEmpty()) {
                    event.channel.sendMessage(
                        "${member.asMention}, arguments are missing, example usage is `.remlang symfony`."
                    ).queue()
                    return@Runnable
                }
                val currentRoles =
                    member.roles.filter { !it.isHoisted && !it.permissions.contains(Permission.ADMINISTRATOR) && !it.name.contains(
                        "everyone",
                        true
                    ) }
                val rolesRemoved = mutableListOf<String>()

                for (arg in args) {
                    currentRoles.forEach {
                        if (it.name.equals(arg, true)) {
                            event.guild.removeRoleFromMember(member, it).queue()
                            rolesRemoved.add(it.name)
                        }
                    }
                }

                if (rolesRemoved.isNotEmpty()) {
                    event.channel.sendMessage(
                        "${member.asMention}, following languages were removed from you: **${rolesRemoved.joinToString(
                            "**, **"
                        )}**"
                    ).queue()
                } else {
                    event.channel.sendMessage("${member.asMention}, requested language was not found, type `.langs`")
                        .queue()
                }

            } else if (command.equals("langs", true) || command.equals("languages", true)) {
                val roles = event.guild.roles.filterNotNull()
                    .filter {
                        !it.isHoisted && !it.permissions.contains(Permission.ADMINISTRATOR) && !it.name.contains(
                            "everyone",
                            true
                        )
                    }
                val availableRoles = mutableListOf<String>()

                roles.forEach { availableRoles.add(it.name) }

                event.channel.sendMessage(
                    "${member.asMention}, you can choose following languages/frameworks/technologies: \n**${availableRoles.joinToString(
                        "**, **"
                    )}**"
                ).queue()

            } else if (command.equals("help", true)) {
                val b = EmbedBuilder()
                b.setTitle("Command help")
                b.setColor(Color(75, 115, 216))
                b.addField(
                    ".langs", "Will display all available languages/frameworks/technologies you can add.",
                    false
                )
                b.addField(
                    ".addlang php nette", "Will add you **PHP** and **Nette** roles.",
                    false
                )
                b.addField(
                    ".remlang symfony ruby", "Will remove **Symfony** and **Ruby** role from you.",
                    false
                )
                b.setFooter(
                    "Bot is open-source, check out http://github.com/Rixafy/NetteDiscordBot",
                    "https://i.imgur.com/UwiQZK1.png"
                )
                event.channel.sendMessage(b.build()).queue()
            }
        }
        val t = Thread(r)
        t.name = "Command Executor"
        t.uncaughtExceptionHandler = object : Thread.UncaughtExceptionHandler {
            override fun uncaughtException(t: Thread, e: Throwable) {
                event.channel.sendMessage(
                    """Exception occured in thread: ${t.name}(${t.id}) ${e.javaClass.name}: ${e.message}
                            Check console for errors."""
                )
                    .queue()
                e.printStackTrace()
            }
        }
        t.start()
    }
}
