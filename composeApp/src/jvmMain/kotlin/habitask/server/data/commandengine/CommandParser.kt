package habitask.server.data.commandengine

import androidx.compose.runtime.DontMemoize
import androidx.compose.runtime.key
import kotlin.jvm.Throws

data class Token(val string: String, val isWhitespace: Boolean)

//data class Token(val string: String, val padding: String)
//
//private fun Token.withPadding(char: Char) = Token(this.string, padding + char)
//
private class Context(val source: String) {
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

    private fun tokensUntil(until: (Token) -> Boolean, startingIndex: Int = index): Int {
        val tokens = mutableListOf<Token>()
        var newIndex = startingIndex

        while (true) {
            val token = command.tokens[newIndex]

            if (until(token))
                break

            tokens += token

            newIndex++
        }

        return newIndex
    }

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

    fun tokens(keyTokens: String, ignoreCase: Boolean = true, body: CommandContext.() -> Unit) {
        val checkTokens = parseCommand(keyTokens).tokens
        var checkIndex = 0

        val matchTokens = command.tokens
        var matchIndex = index

        while (checkIndex < checkTokens.size) {
            while (checkTokens[checkIndex].isWhitespace)
                checkIndex++
            while (matchTokens[matchIndex].isWhitespace)
                matchIndex++

            if (checkTokens[checkIndex].string.equals(matchTokens[matchIndex].string, ignoreCase = ignoreCase))
                return

            matchIndex++
            checkIndex++
        }

        CommandContext(this, command, matchIndex).body()
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

private fun readString(context: Context) {
    val cap = context.next()
    val string = StringBuilder()

    while (!(context.peek()?.equals(cap) ?: true)) {
        val char = context.next()
        if (char == '\\') {
            string.append(when (val next = context.next()) {
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

    context.next()

    context.addToken(string.toString(), isWhitespace = false)
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

private fun readRawString(context: Context) {
    val string = StringBuilder()
    var previous = context.next() ?: return
    string.append(previous)

    while (true) {
        val peeked = context.peek() ?: break

        if (!isSimilar(previous, peeked))
            break

        previous = context.next()!!
        string.append(previous)
    }

    context.addToken(string.toString(), previous.isWhitespace())
}

fun List<Token>.asCommand(): Command =
    Command(this)

fun List<Token>.joinToString(): String =
    joinToString("") { it.string }

fun parseCommand(source: String): Command {
    val context = Context(source.trim())

    while (true) {
        if (context.peek() == '"')
            readString(context)
        else if (context.peek() != null)
            readRawString(context)
        else break
    }

    return Command(context.tokens)
}