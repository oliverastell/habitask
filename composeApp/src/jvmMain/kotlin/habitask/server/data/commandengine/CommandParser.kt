package habitask.server.data.commandengine

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

    fun readRawToken() {
        val token = StringBuilder()
        var previous = next() ?: return
        token.append(previous)

        while (true) {
            val peeked = peek() ?: break

            if (!isSimilar(previous, peeked))
                break

            previous = next()!!
            token.append(previous)
        }

        addToken(token.toString(), previous.isWhitespace())
    }

    fun parseCommand(): Command {
        while (true) {
            val peeked = peek() ?: break

            if (peeked == '"' || peeked == '\'')
                readString()
            else
                readRawToken()
        }

        return Command(tokens)
    }
}




interface CommandContext {
    val parent: CommandContext?
    val command: Command
    val index: Int
    val output: (Any) -> Unit
    var hasEndpointBeenMet: Boolean

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
        if (hasEndpointBeenMet)
            return

        val token = command.tokens.getOrNull(index)
        if (token != null) {
            getContextOfFirstTokenIgnoreWhitespace(index+1).body(token.string)
        }
    }

    fun word(body: CommandContext.(word: String) -> Unit) {
        if (hasEndpointBeenMet)
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
        if (hasEndpointBeenMet)
            return

        token { token ->
            if (token.equals(keytoken, ignoreCase = ignoreCase))
                body()
        }
    }

    fun word(keyword: String, ignoreCase: Boolean = true, body: CommandContext.() -> Unit) {
        if (hasEndpointBeenMet)
            return

        word { word ->
            if (word.equals(keyword, ignoreCase = ignoreCase))
                body()
        }
    }

    fun <T> greedySubCommand(body: (Command) -> T) {
        if (hasEndpointBeenMet)
            return

        hasEndpointBeenMet = true
        body(command.tokens.subList(index, command.tokens.size).asCommand())
    }

    fun <T> greedyText(body: (String) -> T) {
        if (hasEndpointBeenMet)
            return

        val text = StringBuilder()
        for (i in index..<command.tokens.size) {
            val token = command.tokens[i]
            text.append(token.string)
        }

        body(text.toString())
        hasEndpointBeenMet = true
    }

    fun noEnd() {
        if (hasEndpointBeenMet)
            return

        if (index >= command.tokens.size)
            throw MalformedCommandException()
    }

    fun end(body: () -> Unit) {
        if (hasEndpointBeenMet)
            return

        if (index < command.tokens.size)
            return

        hasEndpointBeenMet = true
        body()
    }
}

fun CommandContext(parent: CommandContext, command: Command, index: Int) = object : CommandContext {
    override val command: Command = command
    override val index: Int = index
    override val parent: CommandContext = parent

    override var hasEndpointBeenMet: Boolean
        get() = this.parent.hasEndpointBeenMet
        set(value) { this.parent.hasEndpointBeenMet = value }

    override val output: (Any) -> Unit
        get() = this.parent.output
}

data class Command(val tokens: List<Token>) {
    @Throws(MalformedCommandException::class)
    fun execute(onOutput: (Any) -> Unit, body: CommandContext.() -> Unit) {
        val commandContext = object : CommandContext {
            override val parent: CommandContext? = null
            override val command: Command = this@Command
            override val index: Int = 0
            override val output: (Any) -> Unit = onOutput
            override var hasEndpointBeenMet: Boolean = false
        }

        commandContext.body()

        if (!commandContext.hasEndpointBeenMet) {
            throw MalformedCommandException()
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

    return false
}



fun List<Token>.asCommand(): Command =
    Command(this)

fun parseCommand(source: String): Command = CommandLexer(source.trim()).parseCommand()