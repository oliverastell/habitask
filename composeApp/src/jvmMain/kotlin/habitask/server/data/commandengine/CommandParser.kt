package habitask.server.data.commandengine

import habitask.common.Logger
import kotlin.jvm.Throws

data class Token(val string: String, val isWhitespace: Boolean)

private class CommandLexer(val source: String) {
    var index = 0
    val tokens = mutableListOf<Token>()

    fun next(): Char? {
        val char = source.getOrNull(index)
        index++
        return char
    }

    fun peek(): Char? {
        return source.getOrNull(index)
    }

    fun addToken(string: String, isWhitespace: Boolean) {
        tokens.add(Token(
            string = string,
            isWhitespace = isWhitespace
        ))
    }

    fun readString() {
        val cap = next()
        val string = StringBuilder()

        while (peek() != cap && peek() != null) {
            val char = next()
            if (char == '\\') {
                string.append(when (val next = next()) {
                    'n' -> '\n'
                    't' -> '\t'
                    'r' -> '\r'
                    '\\' -> '\\'
                    '"' -> '"'
                    else -> next
                })
            } else {
                string.append(char)
            }
        }

        next()

        addToken(string.toString(), isWhitespace = false)
    }

    fun readRawString() {
        val string = StringBuilder()
        var previous = next() ?: return
        string.append(previous)

        while (true) {
            val peeked = peek() ?: break

            if (!isSimilar(previous, peeked))
                break

            previous = next()!!
            string.append(previous)
        }

        addToken(string.toString(), previous.isWhitespace())
    }

    fun parseCommand(): Command {
        while (true) {
            val peeked = peek() ?: break

            if (peeked == '"' || peeked == '\'')
                readString()
            else readRawString()
        }

        return Command(tokens)
    }
}




interface CommandContext {
    val parent: CommandContext?
    val command: Command
    val index: Int
    val output: (Any) -> Unit
    var globallyHandled: Boolean

    private fun getContextOfFirstTokenIgnoreWhitespace(startingIndex: Int): CommandContext {
        var newIndex = startingIndex
        while (true) {
            val nextToken = command.tokens.getOrNull(newIndex) ?: break
            if (!nextToken.isWhitespace)
                break

            newIndex++
        }

        return CommandContext(this, command, newIndex)
    }

    fun token(body: CommandContext.(token: String) -> Unit) {
        if (globallyHandled)
            return

        val token = command.tokens.getOrNull(index)
        if (token != null) {
            getContextOfFirstTokenIgnoreWhitespace(index+1).body(token.string)
        }
    }

    fun word(body: CommandContext.(word: String) -> Unit) {
        if (globallyHandled)
            return

        tokensUntil({ it.isWhitespace }) { tokens ->
            body(tokens.joinToString("") { it.string })
        }
    }

    fun number(body: CommandContext.(number: Double) -> Unit) {
        token { whole ->
            token(".") {
                token { decimal ->
                    val number = "$whole.$decimal".toDoubleOrNull()
                    if (number != null)
                        body(number)
                }
            }

            val number = whole.toDoubleOrNull()
            if (number != null)
                body(number)
        }
    }

    fun integer(body: CommandContext.(int: Int) -> Unit) {
        token { token ->
            val int = token.toIntOrNull()
            if (int != null)
                body(int)
        }
    }

//    private fun tokensUntil(until: (Token) -> Boolean, startingIndex: Int = index): Int {
//        val tokens = mutableListOf<Token>()
//        var newIndex = startingIndex
//
//        while (true) {
//            val token = command.tokens[newIndex]
//
//            if (until(token))
//                break
//
//            tokens += token
//
//            newIndex++
//        }
//
//        return newIndex
//    }

    fun tokensUntil(until: (Token) -> Boolean, body: CommandContext.(List<Token>) -> Unit) {
        val tokens = mutableListOf<Token>()
        var newIndex = index

        while (newIndex < command.tokens.size) {
            val token = command.tokens[newIndex]

            if (until(token))
                break

            tokens += token

            newIndex++
        }

        getContextOfFirstTokenIgnoreWhitespace(newIndex).body(tokens)
    }

    fun token(keytoken: String, ignoreCase: Boolean = true, body: CommandContext.() -> Unit) {
        if (globallyHandled)
            return

        token { token ->
            if (token.equals(keytoken, ignoreCase = ignoreCase))
                body()
        }
    }

    fun word(keyword: String, ignoreCase: Boolean = true, body: CommandContext.() -> Unit) {
        if (globallyHandled)
            return

        word { word ->
            if (word.equals(keyword, ignoreCase = ignoreCase))
                body()
        }
    }

    fun <T> greedySubCommand(body: (Command) -> T) {
        if (globallyHandled)
            return

        globallyHandled = true
        body(command.tokens.subList(index, command.tokens.size).asCommand())
    }

    fun <T> greedyText(body: (String) -> T) {
        if (globallyHandled)
            return

        val text = StringBuilder()
        for (i in index..<command.tokens.size) {
            val token = command.tokens[i]
            text.append(token.string)
        }

        body(text.toString())
        globallyHandled = true
    }

    fun noEnd() {
        if (globallyHandled)
            return

        if (index >= command.tokens.size)
            throw MalformedCommand()
    }

    fun end(body: () -> Unit) {
        if (globallyHandled)
            return

        if (index < command.tokens.size)
            return

        globallyHandled = true
        body()
    }
}

fun CommandContext(parent: CommandContext, command: Command, index: Int) = object : CommandContext {
    override val command: Command = command
    override val index: Int = index
    override val parent: CommandContext = parent

    override var globallyHandled: Boolean
        get() = this.parent.globallyHandled
        set(value) { this.parent.globallyHandled = value }

    override val output: (Any) -> Unit
        get() = this.parent.output
}

data class Command(val tokens: List<Token>) {
    @Throws(MalformedCommand::class)
    fun execute(onOutput: (Any) -> Unit, body: CommandContext.() -> Unit) {
        val commandContext = object : CommandContext {
            override val parent: CommandContext? = null
            override val command: Command = this@Command
            override val index: Int = 0
            override val output: (Any) -> Unit = onOutput
            override var globallyHandled: Boolean = false
        }

        commandContext.body()

        if (!commandContext.globallyHandled) {
            throw MalformedCommand()
        }
    }
}



private fun isSimilar(a: Char, b: Char): Boolean {
    if (a.isWhitespace() && b.isWhitespace())
        return true

    if (a.isDigit() && b.isDigit())
        return true

    if (a.isLetter() && b.isLetter())
        return true

    return a == b
}



fun List<Token>.asCommand(): Command =
    Command(this)

fun parseCommand(source: String): Command = CommandLexer(source.trim()).parseCommand()