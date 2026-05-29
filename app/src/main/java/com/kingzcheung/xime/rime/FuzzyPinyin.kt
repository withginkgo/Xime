package com.kingzcheung.xime.rime

/**
 * 模糊音规则
 *
 * 基于 Augmenter 设计模式，为九宫格输入提供拼音容错。
 * 每种模糊规则是一个 Augmenter，将用户输入的数字序列扩展出额外的拼音候选项。
 *
 * 使用示例：
 *   FuzzyPinyin.applyAll("zhan")  → [zhan, zhang, zan, zang]  (翘舌+前后鼻音)
 */
object FuzzyPinyin {

    /** 单个模糊规则 */
    data class Rule(
        val name: String,
        val replacements: List<Pair<String, String>>
    )

    /**
     * 翘舌音模糊：zh↔z, ch↔c, sh↔s
     * 翘舌音模糊增强器
     */
    val RETROFLEX = Rule("翘舌音", listOf(
        "zh" to "z", "z" to "zh",
        "ch" to "c", "c" to "ch",
        "sh" to "s", "s" to "sh"
    ))

    /**
     * 前后鼻音模糊：in↔ing, en↔eng, an↔ang, ian↔iang, uan↔uang
     * 前后鼻音模糊增强器
     */
    val NASAL = Rule("前后鼻音", listOf(
        "in" to "ing", "ing" to "in",
        "en" to "eng", "eng" to "en",
        "an" to "ang", "ang" to "an",
        "ian" to "iang", "iang" to "ian",
        "uan" to "uang", "uang" to "uan"
    ))

    /**
     * 鼻音边音模糊：n↔l
     */
    val N_L = Rule("鼻音边音", listOf(
        "n" to "l", "l" to "n"
    ))

    /**
     * 灰飞模糊：hui↔fei
     * 灰飞模糊增强器
     */
    val HUI_FEI = Rule("灰飞模糊", listOf(
        "hui" to "fei", "fei" to "hui"
    ))

    /**
     * huang↔wang
     * 黄王不分增强器
     */
    val HUANG_WANG = Rule("黄王不分", listOf(
        "huang" to "wang", "wang" to "huang"
    ))

    /**
     * un↔ong, un↔iong
     * un/ong 模糊增强器
     */
    val UN_ONG = Rule("un/ong", listOf(
        "un" to "ong", "ong" to "un",
        "un" to "iong", "iong" to "un"
    ))

    /** 所有规则的组合 */
    val ALL_RULES = listOf(RETROFLEX, NASAL, N_L, HUI_FEI, HUANG_WANG, UN_ONG)

    /**
     * 对单个拼音应用所有模糊规则，生成可能的变体
     * 例如 "zhan" → [zhan, zhang, zan, zang]
     */
    fun applyAll(pinyin: String, enabledRules: List<Rule> = ALL_RULES): Set<String> {
        val results = mutableSetOf(pinyin)
        for (rule in enabledRules) {
            val currentBatch = results.toList()
            for (variant in currentBatch) {
                for ((from, to) in rule.replacements) {
                    var idx = variant.indexOf(from)
                    while (idx >= 0) {
                        val newVariant = variant.substring(0, idx) +
                                to + variant.substring(idx + from.length)
                        // 只保留拼音列表中的有效变体
                        if (isValidPinyin(newVariant)) {
                            results.add(newVariant)
                        }
                        idx = variant.indexOf(from, idx + 1)
                    }
                }
            }
        }
        return results
    }

    /**
     * 对拼音列表批量生成模糊变体
     */
    fun expandAll(pinyins: List<String>, enabledRules: List<Rule> = ALL_RULES): List<String> {
        val seen = mutableSetOf<String>()
        val result = mutableListOf<String>()
        for (p in pinyins) {
            val variants = applyAll(p, enabledRules)
            for (v in variants) {
                if (v !in seen) {
                    seen.add(v)
                    result.add(v)
                }
            }
        }
        return result
    }

    /** 验证是否是有效拼音 */
    private fun isValidPinyin(s: String): Boolean {
        return s in PINYIN_LIST
    }

    /** 拼音列表（与 T9Decoder/T9PinyinEngine 共用） */
    internal val PINYIN_LIST = setOf(
        "a", "ai", "an", "ang", "ao",
        "ba", "bai", "ban", "bang", "bao", "bei", "ben", "beng", "bi", "bian", "biao",
        "bie", "bin", "bing", "bo", "bu",
        "ca", "cai", "can", "cang", "cao", "ce", "cen", "ceng", "cha", "chai", "chan",
        "chang", "chao", "che", "chen", "cheng", "chi", "chong", "chou", "chu", "chua",
        "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci", "cong", "cou", "cu",
        "cuan", "cui", "cun", "cuo",
        "da", "dai", "dan", "dang", "dao", "de", "dei", "den", "deng", "di", "dia",
        "dian", "diao", "die", "ding", "diu", "dong", "dou", "du", "duan", "dui", "dun", "duo",
        "e", "ei", "en", "eng", "er",
        "fa", "fan", "fang", "fei", "fen", "feng", "fiao", "fo", "fou", "fu",
        "ga", "gai", "gan", "gang", "gao", "ge", "gei", "gen", "geng", "gong", "gou",
        "gu", "gua", "guai", "guan", "guang", "gui", "gun", "guo",
        "ha", "hai", "han", "hang", "hao", "he", "hei", "hen", "heng", "hong", "hou",
        "hu", "hua", "huai", "huan", "huang", "hui", "hun", "huo",
        "ji", "jia", "jian", "jiang", "jiao", "jie", "jin", "jing", "jiong", "jiu",
        "ju", "juan", "jue", "jun",
        "ka", "kai", "kan", "kang", "kao", "ke", "kei", "ken", "keng", "kong", "kou",
        "ku", "kua", "kuai", "kuan", "kuang", "kui", "kun", "kuo",
        "la", "lai", "lan", "lang", "lao", "le", "lei", "leng", "li", "lia", "lian",
        "liang", "liao", "lie", "lin", "ling", "liu", "lo", "long", "lou", "lu",
        "luan", "lve", "lun", "luo", "lv",
        "ma", "mai", "man", "mang", "mao", "me", "mei", "men", "meng", "mi", "mian",
        "miao", "mie", "min", "ming", "miu", "mo", "mou", "mu",
        "na", "nai", "nan", "nang", "nao", "ne", "nei", "nen", "neng", "ni", "nian",
        "niang", "niao", "nie", "nin", "ning", "niu", "nong", "nou", "nu", "nuan",
        "nve", "nun", "nuo", "nv",
        "o", "ou",
        "pa", "pai", "pan", "pang", "pao", "pei", "pen", "peng", "pi", "pian", "piao",
        "pie", "pin", "ping", "po", "pou", "pu",
        "qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing", "qiong", "qiu",
        "qu", "quan", "que", "qun",
        "ran", "rang", "rao", "re", "ren", "reng", "ri", "rong", "rou", "ru", "rua",
        "ruan", "rui", "run", "ruo",
        "sa", "sai", "san", "sang", "sao", "se", "sen", "seng", "sha", "shai", "shan",
        "shang", "shao", "she", "shei", "shen", "sheng", "shi", "shou", "shu", "shua",
        "shuai", "shuan", "shuang", "shui", "shun", "shuo", "si", "song", "sou", "su",
        "suan", "sui", "sun", "suo",
        "ta", "tai", "tan", "tang", "tao", "te", "tei", "teng", "ti", "tian", "tiao",
        "tie", "ting", "tong", "tou", "tu", "tuan", "tui", "tun", "tuo",
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

    /** 字母→数字映射 */
    internal val LETTER_TO_DIGIT = mapOf(
        'a' to '2', 'b' to '2', 'c' to '2',
        'd' to '3', 'e' to '3', 'f' to '3',
        'g' to '4', 'h' to '4', 'i' to '4',
        'j' to '5', 'k' to '5', 'l' to '5',
        'm' to '6', 'n' to '6', 'o' to '6',
        'p' to '7', 'q' to '7', 'r' to '7', 's' to '7',
        't' to '8', 'u' to '8', 'v' to '8',
        'w' to '9', 'x' to '9', 'y' to '9', 'z' to '9'
    )
}
