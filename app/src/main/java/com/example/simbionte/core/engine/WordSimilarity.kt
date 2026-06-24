package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock
import kotlin.math.sqrt

class WordSimilarity {

    companion object {
        val stopWords = setOf(
            "para", "com", "que", "uma", "uma", "isso", "aquele", "aquela",
            "quando", "como", "mais", "mas", "por", "pelo", "pela", "tudo",
            "nada", "algo", "todo", "cada", "ainda", "depois", "antes",
            "entre", "desde", "até", "contra", "sobre", "sem", "sob", "atras",
            "nele", "nela", "dela", "dele", "deles", "delas", "nesse", "nessa",
            "naquele", "naquela", "porque", "entao", "sendo", "mesmo", "assim",
            "tambem", "alem", "durante", "enquanto", "nunca", "sempre", "jamais",
            "apenas", "somente", "quase", "apos", "perante", "mediante", "conforme",
            "vai", "vir", "vem", "foi", "era", "ter", "tem", "tinha", "estava",
            "esta", "estao", "estou", "pode", "podem", "poder", "deve", "devem",
            "ficou", "ficar", "acha", "acho", "achou", "disse", "fala", "falei"
        )

        val synonyms = mapOf(
            "carro" to setOf("automovel", "veiculo", "caranga"),
            "triste" to setOf("melancolico", "abatido", "deprimido", "desanimado"),
            "feliz" to setOf("alegre", "contente", "satisfeito", "radiante"),
            "bom" to setOf("otimo", "excelente", "maravilhoso", "positivo"),
            "ruim" to setOf("pessimo", "terrivel", "horrivel", "negativo"),
            "pensar" to setOf("refletir", "meditar", "raciocinar", "ponderar"),
            "falar" to setOf("conversar", "dialogar", "discutir", "comunicar"),
            "trabalho" to setOf("emprego", "servico", "profissao", "carreira"),
            "estudo" to setOf("aprender", "estudar", "escola", "faculdade"),
            "cansado" to setOf("exausto", "fatigado", "esgotado", "desgastado"),
            "amigo" to setOf("companheiro", "camarada", "colega", "parceiro"),
            "problema" to setOf("dificuldade", "desafio", "questao", "impass"),
            "medo" to setOf("receio", "temor", "apreensao", "ansiedade"),
            "amor" to setOf("afeto", "carinho", "paixao", "ternura"),
            "raiva" to setOf("ira", "revolta", "indignacao", "furia"),
            "esperanca" to setOf("otimismo", "confianca", "fe", "crenca"),
            "importante" to setOf("essencial", "fundamental", "crucial", "vital"),
            "dificil" to setOf("complicado", "complexo", "arduo", "custoso"),
            "facil" to setOf("simples", "tranquilo", "suave", "leve"),
            "bonito" to setOf("lindo", "belo", "formoso", "encantador"),
            "feio" to setOf("horroroso", "inespressive", "estranho", "desagradavel"),
            "grande" to setOf("enorme", "gigante", "imenso", "vastante"),
            "pequeno" to setOf("minimo", "reduzido", "compacto", "miudo"),
            "novo" to setOf("recente", "moderno", "atual", "fresco"),
            "velho" to setOf("antigo", "idoso", "ultrapassado", "veterano"),
            "rapido" to setOf("veloz", "agil", "ligeiro", "celere"),
            "devagar" to setOf("lento", "pausado", "moroso", "tranquilo"),
            "querer" to setOf("desejar", "pretender", "almejar", "aspirar"),
            "precisar" to setOf("necessitar", "requerer", "carecer"),
            "saber" to setOf("conhecer", "entender", "compreender"),
            "acreditar" to setOf("crer", "confiar", "considerar"),
            "ajuda" to setOf("socorro", "auxilio", "apoio", "assistencia"),
            "casa" to setOf("lar", "residencia", "moradia", "domicilio"),
            "familia" to setOf("parentes", "familiares", "parentela"),
            "saude" to setOf("bemestar", "vitalidade", "disposicao"),
            "doenca" to setOf("enfermidade", "mal", "condicao"),
            "tempo" to setOf("periodo", "duracao", "momento", "era"),
            "lugar" to setOf("local", "sitio", "regiao", "espaco"),
            "pessoa" to setOf("individuo", "sujeito", "alguem", "gente"),
            "coisa" to setOf("objeto", "item", "elemento", "questao"),
            "vida" to setOf("existencia", "jornada", "trajetoria"),
            "morte" to setOf("fim", "falecimento", "extincao"),
            "sonho" to setOf("desejo", "aspiraçao", "objetivo", "ideal"),
            "lembrar" to setOf("recordar", "relembrar", "reviver"),
            "esquecer" to setOf("esquecer", "omitir", "desconsiderar"),
            "decidir" to setOf("optar", "escolher", "definir", "resolver"),
            "mudar" to setOf("transformar", "alterar", "modificar", "trocar"),
            "comecar" to setOf("iniciar", "principiar", "estrear"),
            "terminar" to setOf("finalizar", "concluir", "encerrar", "acabar"),
            "ansioso" to setOf("nervoso", "inquieto", "aflito", "impaciente"),
            "calmo" to setOf("tranquilo", "sereno", "pacato", "sossegado"),
            "coragem" to setOf("bravura", "ousadia", "audacia", "determinacao"),
            "certeza" to setOf("conviccao", "seguranca", "firmeza"),
            "duvida" to setOf("incerteza", "hesitacao", "indecisao"),
            "esforco" to setOf("dedicacao", "empenho", "persistencia", "garra"),
            "descanso" to setOf("repouso", "relaxamento", "pausa", "folga")
        )
    }

    fun charNGrams(text: String, n: Int = 3): Set<String> {
        val cleaned = text.lowercase().replace(Regex("""\P{L}"""), " ")
        val padded = " $cleaned "
        return (0..padded.length - n).map { padded.substring(it, it + n) }.toSet()
    }

    fun cosineSimilarity(a: Set<String>, b: Set<String>): Double {
        if (a.isEmpty() || b.isEmpty()) return 0.0
        val intersection = a.intersect(b).size.toDouble()
        return intersection / sqrt(a.size.toDouble() * b.size.toDouble())
    }

    fun jaccardSimilarity(a: Set<String>, b: Set<String>): Double {
        if (a.isEmpty() && b.isEmpty()) return 1.0
        val intersection = a.intersect(b).size.toDouble()
        val union = a.union(b).size.toDouble()
        return if (union == 0.0) 0.0 else intersection / union
    }

    fun levenshteinDistance(a: String, b: String): Int {
        val aClean = a.lowercase().trim()
        val bClean = b.lowercase().trim()
        val dp = Array(aClean.length + 1) { IntArray(bClean.length + 1) }
        for (i in 0..aClean.length) dp[i][0] = i
        for (j in 0..bClean.length) dp[0][j] = j
        for (i in 1..aClean.length) {
            for (j in 1..bClean.length) {
                val cost = if (aClean[i - 1] == bClean[j - 1]) 0 else 1
                dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
            }
        }
        return dp[aClean.length][bClean.length]
    }

    fun levenshteinSimilarity(a: String, b: String): Double {
        val maxLen = maxOf(a.length, b.length)
        if (maxLen == 0) return 1.0
        return 1.0 - levenshteinDistance(a, b).toDouble() / maxLen
    }

    fun extractTags(text: String): Set<String> {
        return text.split(" ")
            .map { it.lowercase().replace(Regex("""\P{L}"""), "") }
            .filter { it.length >= 4 && it !in stopWords }
            .toSet()
    }

    fun wordLevelSimilarity(textA: String, textB: String): Double {
        val wordsA = normalizeWords(textA).filter { it !in stopWords }
        val wordsB = normalizeWords(textB).filter { it !in stopWords }
        if (wordsA.isEmpty() || wordsB.isEmpty()) return 0.0

        val expandedA = wordsA.flatMap { synonyms[it]?.plus(it) ?: setOf(it) }.toSet()
        val expandedB = wordsB.flatMap { synonyms[it]?.plus(it) ?: setOf(it) }.toSet()

        val intersection = expandedA.intersect(expandedB).size.toDouble()
        val union = expandedA.union(expandedB).size.toDouble()
        return if (union == 0.0) 0.0 else intersection / union
    }

    private fun normalizeWords(text: String): List<String> {
        return text.lowercase()
            .replace(Regex("""\P{L}"""), " ")
            .split(Regex("\\s+"))
            .filter { it.length >= 3 }
            .map { truncate(it, 5) }
    }

    private fun truncate(word: String, maxLen: Int): String {
        return if (word.length > maxLen) word.substring(0, maxLen) else word
    }

    fun tagOverlap(currentTags: Set<String>, previousTags: Set<String>): Double {
        if (currentTags.isEmpty() || previousTags.isEmpty()) return 0.0
        return currentTags.intersect(previousTags).size.toDouble() /
            maxOf(currentTags.size, previousTags.size).toDouble()
    }

    fun searchSimilar(
        query: String,
        corpus: List<MemoryBlock>,
        topK: Int = 10,
        threshold: Double = 0.08
    ): List<Pair<MemoryBlock, Double>> {
        val queryNGrams = charNGrams(query)
        val queryWords = normalizeWords(query)
        if (queryNGrams.isEmpty() && queryWords.isEmpty()) return emptyList()

        return corpus
            .map { block ->
                val ngramSim = if (queryNGrams.isNotEmpty()) {
                    cosineSimilarity(queryNGrams, charNGrams(block.content))
                } else 0.0

                val tagSim = tagOverlap(queryWords.toSet(), extractTags(block.content))
                val wordSim = wordLevelSimilarity(query, block.content)

                val combined = (ngramSim * 0.3 + tagSim * 0.25 + wordSim * 0.45)
                block to combined
            }
            .filter { it.second >= threshold }
            .sortedByDescending { it.second }
            .take(topK)
    }
}
