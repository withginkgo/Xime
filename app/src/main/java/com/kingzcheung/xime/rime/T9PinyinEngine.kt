package com.kingzcheung.xime.rime

/**
 * T9 拼音引擎
 *
 * 将九宫格数字序列转换为可能的拼音候选项。
 * 工作原理：
 * 1. 预加载所有合法拼音及其对应的数字编码（2=abc, 3=def, ...）
 * 2. 通过 Trie 树索引，接收数字序列快速查找匹配的拼音
 *
 * 使用示例：
 *   val engine = T9PinyinEngine()
 *   engine.lookup("646")  // → ["min", "nin", "mon", "non", ...]
 */
class T9PinyinEngine {

    companion object {
        /** 九宫格键盘字母映射 */
        private val KEY_MAP = mapOf(
            '2' to "abc", '3' to "def",
            '4' to "ghi", '5' to "jkl",
            '6' to "mno", '7' to "pqrs",
            '8' to "tuv", '9' to "wxyz"
        )

        /** 反向查找：字母 → 对应的数字键 */
        private val CHAR_TO_DIGIT = KEY_MAP.entries
            .flatMap { (digit, chars) -> chars.map { it to digit } }
            .toMap()

        /** 完整拼音列表（含所有标准拼音音节） */
        private val PINYIN_LIST = listOf(
            "a", "ai", "an", "ang", "ao",
            "ba", "bai", "ban", "bang", "bao", "bei", "ben", "beng", "bi", "bian", "biao",
            "bie", "bin", "bing", "bo", "bu",
            "ca", "cai", "can", "cang", "cao", "ce", "cen", "ceng", "cha", "chai", "chan",
            "chang", "chao", "che", "chen", "cheng", "chi", "chong", "chou", "chu", "chua",
            "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci", "cong", "cou", "cu",
            "cuan", "cui", "cun", "cuo",
            "da", "dai", "dan", "dang", "dao", "de", "den", "deng", "di", "dia", "dian",
            "diao", "die", "ding", "diu", "dong", "dou", "du", "duan", "dui", "dun", "duo",
            "e", "ei", "en", "eng", "er",
            "fa", "fan", "fang", "fei", "fen", "feng", "fiao", "fo", "fou", "fu",
            "ga", "gai", "gan", "gang", "gao", "ge", "gei", "gen", "geng", "gong", "gou",
            "gu", "gua", "guai", "guan", "guang", "gui", "gun", "guo",
            "ha", "hai", "han", "hang", "hao", "he", "hei", "hen", "heng", "hong", "hou",
            "hu", "hua", "huai", "huan", "huang", "hui", "hun", "huo",
            "ji", "jia", "jian", "jiang", "jiao", "jie", "jin", "jing", "jiong", "jiu",
            "ju", "juan", "jue", "jun",
            "ka", "kai", "kan", "kang", "kao", "ke", "ken", "keng", "kong", "kou",
            "ku", "kua", "kuai", "kuan", "kuang", "kui", "kun", "kuo",
            "la", "lai", "lan", "lang", "lao", "le", "lei", "leng", "li", "lia", "lian",
            "liang", "liao", "lie", "lin", "ling", "liu", "lo", "long", "lou", "lu",
            "luan", "lun", "luo", "lv", "lve",
            "ma", "mai", "man", "mang", "mao", "me", "mei", "men", "meng", "mi", "mian",
            "miao", "mie", "min", "ming", "miu", "mo", "mou", "mu",
            "na", "nai", "nan", "nang", "nao", "ne", "nei", "nen", "neng", "ni", "nian",
            "niang", "niao", "nie", "nin", "ning", "niu", "nong", "nou", "nu", "nuan",
            "nuo", "nv", "nve",
            "o", "ou",
            "pa", "pai", "pan", "pang", "pao", "pei", "pen", "peng", "pi", "pian", "piao",
            "pie", "pin", "ping", "po", "pou", "pu",
            "qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing", "qiong", "qiu",
            "qu", "quan", "que", "qun",
            "ran", "rang", "rao", "re", "ren", "reng", "ri", "rong", "rou", "ru", "ruan",
            "rui", "run", "ruo",
            "sa", "sai", "san", "sang", "sao", "se", "sen", "seng", "sha", "shai", "shan",
            "shang", "shao", "she", "shei", "shen", "sheng", "shi", "shou", "shu", "shua",
            "shuai", "shuan", "shuang", "shui", "shun", "shuo", "si", "song", "sou", "su",
            "suan", "sui", "sun", "suo",
            "ta", "tai", "tan", "tang", "tao", "te", "teng", "ti", "tian", "tiao", "tie",
            "ting", "tong", "tou", "tu", "tuan", "tui", "tun", "tuo",
            "wa", "wai", "wan", "wang", "wei", "wen", "weng", "wo", "wu",
            "xi", "xia", "xian", "xiang", "xiao", "xie", "xin", "xing", "xiong", "xiu",
            "xu", "xuan", "xue", "xun",
            "ya", "yan", "yang", "yao", "ye", "yi", "yin", "ying", "yo", "yong", "you",
            "yu", "yuan", "yue", "yun",
            "za", "zai", "zan", "zang", "zao", "ze", "zei", "zen", "zeng", "zha", "zhai",
            "zhan", "zhang", "zhao", "zhe", "zhei", "zhen", "zheng", "zhi", "zhong", "zhou",
            "zhu", "zhua", "zhuai", "zhuan", "zhuang", "zhui", "zhun", "zhuo", "zi", "zong",
            "zou", "zu", "zuan", "zui", "zun", "zuo"
        )

        /** 将拼音转换为数字编码 */
        private fun pinyinToCode(pinyin: String): String {
            return pinyin.map { CHAR_TO_DIGIT[it] ?: it }.joinToString("")
        }
    }

    /** Trie 节点 */
    private class TrieNode {
        val children = mutableMapOf<Char, TrieNode>()
        val results = mutableListOf<String>()
    }

    private val root = TrieNode()

    init {
        // 建立 Trie 索引：数字编码 → 拼音列表
        for (pinyin in PINYIN_LIST.sorted()) {
            val code = pinyinToCode(pinyin)
            var node = root
            for (ch in code) {
                node = node.children.getOrPut(ch) { TrieNode() }
            }
            node.results.add(pinyin)
        }
    }

    /**
     * 根据数字序列查找可能的拼音候选项
     * @param digits 数字序列，如 "646"
     * @return 匹配的拼音列表，按先行匹配拼音、再完整拼音、再字母序排列
     */
    fun lookup(digits: String): List<String> {
        if (digits.isEmpty()) return emptyList()

        var node = root
        for (ch in digits) {
            node = node.children[ch] ?: return emptyList()
        }

        val results = mutableListOf<String>()
        val exactResults = mutableListOf<String>()
        val prefixResults = mutableListOf<String>()

        // 分离精确匹配和前缀匹配
        // 精确匹配：拼音编码长度 == digits.length
        // 前缀匹配：拼音编码长度 > digits.length（用户还没输完这个音节）
        val queue = ArrayDeque<TrieNode>()
        queue.add(node)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            for (pinyin in current.results) {
                val code = pinyinToCode(pinyin)
                if (code.length == digits.length) {
                    exactResults.add(pinyin)
                } else {
                    prefixResults.add(pinyin)
                }
            }
            queue.addAll(current.children.values)
        }

        exactResults.sort()
        prefixResults.sort()

        results.addAll(exactResults)
        results.addAll(prefixResults)
        return results
    }

    /**
     * 返回最佳拼音：优先精确匹配（编码长度 == digits.length），否则返回第一个前缀匹配
     */
    fun bestMatch(digits: String): String? {
        val candidates = lookup(digits)
        return candidates.firstOrNull()
    }

    /**
     * 判断数字序列是否可能对应有效拼音
     */
    fun isValidPrefix(digits: String): Boolean {
        if (digits.isEmpty()) return true
        var node = root
        for (ch in digits) {
            node = node.children[ch] ?: return false
        }
        return node.results.isNotEmpty() || node.children.isNotEmpty()
    }

    /**
     * 判断字母串是否是有效拼音的前缀（用于过滤当前键的字母选择）
     * 例如：isValidPinyinPrefix("ji") = true，isValidPinyinPrefix("jg") = false
     */
    fun isValidPinyinPrefix(letters: String): Boolean {
        if (letters.isEmpty()) return true
        return PINYIN_LIST.any { it.startsWith(letters) }
    }
}
