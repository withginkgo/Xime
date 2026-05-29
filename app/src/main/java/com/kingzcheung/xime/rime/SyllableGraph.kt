package com.kingzcheung.xime.rime

/**
 * 音节图 (SyllableGraph)
 *
 * 将九宫格数字序列转换为可能的拼音音节图。
 * 每个节点代表数字串中的位置，每条边代表一个可能的拼音音节。
 *
 * 支持四种匹配类型：
 * - EXACT:    数字编码精确匹配到有效拼音（如 "24" → "ai"）
 * - FUZZY:    模糊音匹配（翘舌、前后鼻音等）
 * - NEIGHBOR: 九宫格键盘邻居容错
 * - PARTIAL:  拼音前缀匹配（用户还没输完这个音节）
 */
class SyllableGraph(private val digits: String) {

    /** 音节图中的一条边 */
    data class Edge(
        val pinyin: String,        // 拼音音节
        val type: MatchType,       // 匹配类型
        val start: Int,            // 在 digits 中的起始位置
        val end: Int,              // 在 digits 中的结束位置（不包含）
        val penalty: Double = 0.0  // 惩罚分数（非精确匹配时降低权重）
    )

    enum class MatchType { EXACT, FUZZY, NEIGHBOR, PARTIAL }

    /** 九宫格键盘邻居映射 */
    companion object {
        // 每个键的相邻键（包括自身）
        val KEYBOARD_NEIGHBORS = mapOf(
            '1' to setOf('1', '2', '4', '5'),
            '2' to setOf('1', '2', '3', '4', '5', '6'),
            '3' to setOf('2', '3', '5', '6'),
            '4' to setOf('1', '4', '5', '7', '8'),
            '5' to setOf('1', '2', '3', '4', '5', '6', '7', '8', '9'),
            '6' to setOf('2', '3', '5', '6', '8', '9'),
            '7' to setOf('4', '5', '7', '8'),
            '8' to setOf('4', '5', '6', '7', '8', '9'),
            '9' to setOf('5', '6', '8', '9')
        )

        /** 编码→拼音映射（精确匹配用） */
        private val codeToPinyins: Map<String, List<String>> by lazy {
            FuzzyPinyin.PINYIN_LIST.groupBy { pinyin ->
                pinyin.map { FuzzyPinyin.LETTER_TO_DIGIT[it] ?: it }.joinToString("")
            }
        }

        private const val MAX_PINYIN_LEN = 6
    }

    /** 当前启用的模糊音规则 */
    private var enabledFuzzyRules: List<FuzzyPinyin.Rule> = FuzzyPinyin.ALL_RULES

    /** 是否启用键盘邻居容错 */
    private var neighborEnabled = true

    fun setFuzzyRules(rules: List<FuzzyPinyin.Rule>) {
        enabledFuzzyRules = rules
    }

    fun setNeighborEnabled(enabled: Boolean) {
        neighborEnabled = enabled
    }

    /**
     * 构建音节图，返回从 start 位置出发的所有可能边
     */
    fun buildEdgesFrom(start: Int): List<Edge> {
        if (start >= digits.length) return emptyList()

        val edges = mutableListOf<Edge>()
        val maxLen = minOf(MAX_PINYIN_LEN, digits.length - start)

        for (len in 1..maxLen) {
            val code = digits.substring(start, start + len)

            // 1. 精确匹配
            val exactPinyins = codeToPinyins[code]
            if (exactPinyins != null) {
                for (p in exactPinyins) {
                    edges.add(Edge(p, MatchType.EXACT, start, start + len, 0.0))
                }
            }

            // 2. 模糊音匹配：对精确匹配的拼音应用模糊规则
            if (exactPinyins != null) {
                val fuzzyVariants = FuzzyPinyin.expandAll(exactPinyins, enabledFuzzyRules)
                for (fv in fuzzyVariants) {
                    if (fv !in exactPinyins) {
                        edges.add(Edge(fv, MatchType.FUZZY, start, start + len, -1.5))
                    }
                }
            }

            // 3. 键盘邻居容错：修改编码中的某些数字为其邻居，再查拼音
            if (neighborEnabled && len >= 2) {
                val neighborEdges = findNeighborMatches(code, start, start + len)
                edges.addAll(neighborEdges)
            }
        }

        // 4. 拼音前缀匹配（如 "2"→"a","an","ang"...）
        val prefixEdges = findPrefixMatches(start)
        edges.addAll(prefixEdges)

        // 5. 单字母路径（始终添加）：每个数字对应字母都作为可能的音节片段
        //    虽然单字母不是有效拼音，但让 DP 能探索更多切分可能
        //    bi-gram LM 的低分会自动过滤掉不合理的路径
        val firstLetters = digitToLetters(digits[start])
        if (firstLetters != null) {
            for (l in firstLetters) {
                val letterStr = l.toString()
                edges.add(Edge(letterStr, MatchType.PARTIAL, start, start + 1, 0.0))
            }
        }

        return edges
    }

    /**
     * 构建完整音节图：返回按位置分组的所有边
     */
    fun buildFull(): Map<Int, List<Edge>> {
        val graph = mutableMapOf<Int, MutableList<Edge>>()
        for (pos in 0 until digits.length) {
            val edges = buildEdgesFrom(pos)
            if (edges.isNotEmpty()) {
                graph.getOrPut(pos) { mutableListOf() }.addAll(edges)
            }
        }
        return graph
    }

    /**
     * 键盘邻居匹配：将编码中的每个数字替换为邻居数字，看能否形成有效拼音
     */
    private fun findNeighborMatches(code: String, start: Int, end: Int): List<Edge> {
        val results = mutableListOf<Edge>()
        val seen = mutableSetOf<String>()

        // 尝试修改编码中的每个位置
        for (i in code.indices) {
            val original = code[i]
            val neighbors = KEYBOARD_NEIGHBORS[original] ?: continue
            for (n in neighbors) {
                if (n == original) continue
                val modified = code.substring(0, i) + n + code.substring(i + 1)
                // 跳过已尝试过的编码
                if (modified in seen) continue
                seen.add(modified)

                val pinyins = codeToPinyins[modified]
                if (pinyins != null) {
                    for (p in pinyins) {
                        // 只添加与精确匹配不同的结果
                        val exactPinyins = codeToPinyins[code]
                        if (exactPinyins == null || p !in exactPinyins) {
                            results.add(Edge(p, MatchType.NEIGHBOR, start, end, -2.0))
                        }
                    }
                }
            }
        }

        return results
    }

    /**
     * 拼音前缀匹配：对于单个数字，找到所有以该数字对应字母开头的拼音
     * 例如 digits="2" → letters a,b,c → 找到 a, ai, an, ang, ao, ba, bi, bo, bu, ca...
     */
    private fun findPrefixMatches(start: Int): List<Edge> {
        if (start >= digits.length) return emptyList()

        val results = mutableListOf<Edge>()
        // 只对不足2位数字时做前缀匹配
        val remaining = digits.substring(start)

        // 取前1-2位数字找前缀
        val firstDigit = remaining[0]
        val firstLetters = digitToLetters(firstDigit) ?: return emptyList()

        val remainingLen = remaining.length

        // 对每个可能的首字母组合，找以之开头的拼音
        for (fl in firstLetters) {
            // 如果有两位数字，尝试两位前缀
            if (remainingLen >= 2) {
                val secondLetters = digitToLetters(remaining[1])
                if (secondLetters != null) {
                    for (sl in secondLetters) {
                        val twoLetterPrefix = "$fl$sl"
                        val completions = completePinyin(twoLetterPrefix)
                        for (c in completions) {
                            val code = c.map { FuzzyPinyin.LETTER_TO_DIGIT[it] ?: it }.joinToString("")
                            val exactPinyins = codeToPinyins[code]
                            if (exactPinyins == null || c !in exactPinyins) {
                                results.add(Edge(c, MatchType.PARTIAL, start, start + code.length, -1.0))
                            }
                        }
                    }
                }
            }

            // 单字母前缀：找以该字母开头的拼音
            val singleCompletions = completePinyin(fl.toString())
            for (c in singleCompletions) {
                if (c.length >= 2) {  // 跳过单字母 a/e/o 等
                    val code = c.map { FuzzyPinyin.LETTER_TO_DIGIT[it] ?: it }.joinToString("")
                    val exactPinyins = codeToPinyins[code]
                    if (exactPinyins == null || c !in exactPinyins) {
                        results.add(Edge(c, MatchType.PARTIAL, start, start + code.length, -0.5))
                    }
                }
            }
        }

        return results
    }

    private fun digitToLetters(d: Char): List<Char>? {
        return when (d) {
            '2' -> listOf('a', 'b', 'c')
            '3' -> listOf('d', 'e', 'f')
            '4' -> listOf('g', 'h', 'i')
            '5' -> listOf('j', 'k', 'l')
            '6' -> listOf('m', 'n', 'o')
            '7' -> listOf('p', 'q', 'r', 's')
            '8' -> listOf('t', 'u', 'v')
            '9' -> listOf('w', 'x', 'y', 'z')
            else -> null
        }
    }

    private fun completePinyin(prefix: String): List<String> {
        return FuzzyPinyin.PINYIN_LIST.filter { it.startsWith(prefix) }
    }
}
