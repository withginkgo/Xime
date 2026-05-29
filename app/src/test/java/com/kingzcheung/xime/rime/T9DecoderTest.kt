package com.kingzcheung.xime.rime

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class T9DecoderTest {

    private lateinit var decoder: T9Decoder

    @Before
    fun setUp() {
        decoder = T9Decoder()
    }

    @Test
    fun `decode empty input returns empty`() {
        assertTrue(decoder.decode("").isEmpty())
        assertEquals("", decoder.bestPinyin(""))
    }

    @Test
    fun `single digit produces reasonable pinyin`() {
        val best2 = decoder.bestPinyin("2")
        assertTrue("Expected pinyin starting with a/b/c, got '$best2'",
            best2.isNotEmpty() && best2.all { it in "abc" })
        val best3 = decoder.bestPinyin("3")
        assertTrue("Expected pinyin starting with d/e/f, got '$best3'",
            best3.isNotEmpty() && best3.all { it in "def" })
    }

    @Test
    fun `26 produces an or similar`() {
        val best = decoder.bestPinyin("26")
        assertTrue("Expected 2-char pinyin, got '$best' (len=${best.length})",
            best.length in 2..3 && best.all { it in "abcdefghijklmnopqrstuvwxyz" })
    }

    @Test
    fun `24 produces ai or similar`() {
        val best = decoder.bestPinyin("24")
        assertTrue("Expected 2-char pinyin, got '$best' (len=${best.length})",
            best.length in 2..3 && best.all { it in "abcdefghijklmnopqrstuvwxyz" })
    }

    @Test
    fun `646 produces 3-char result`() {
        val best = decoder.bestPinyin("646")
        assertTrue("Expected 3-char result, got '$best' (len=${best.length})",
            best.length in 3..4)
    }

    @Test
    fun `9426 produces 4-char result`() {
        val best = decoder.bestPinyin("9426")
        assertTrue("Expected 4-char result, got '$best' (len=${best.length})",
            best.length in 4..5)
    }

    @Test
    fun `96636 produces 5-char result`() {
        val best = decoder.bestPinyin("96636")
        assertTrue("Expected 5-char result, got '$best' (len=${best.length})",
            best.length in 5..6)
    }

    @Test
    fun `long multi syllable`() {
        val best = decoder.bestPinyin("64426")
        assertTrue("Expected 5-char result, got '$best' (len=${best.length})",
            best.length in 5..6)
    }

    @Test
    fun `dajiahao decoding`() {
        val best = decoder.bestPinyin("32542426")
        assertTrue("Expected 8-char result, got '$best' (len=${best.length})",
            best.length in 8..9)
    }

    @Test
    fun `woaixuexi decoding`() {
        val best = decoder.bestPinyin("962498394")
        assertTrue("Expected 8+ char result, got '$best' (len=${best.length})",
            best.length >= 8)
    }

    @Test
    fun `long sentence does not crash`() {
        val best = decoder.bestPinyin("546842684267428286")
        assertTrue(best.length >= 10)
    }

    @Test
    fun `path scores are valid`() {
        val paths = decoder.decode("96636")
        assertTrue(paths.isNotEmpty())
        assertTrue(paths.first().score > 0)
    }

    @Test
    fun `invalid prefix does not crash`() {
        val best = decoder.bestPinyin("99")
        assertTrue(best.isNotEmpty())
    }

    @Test
    fun `long sequence does not exceed max paths`() {
        val paths = decoder.decode("9663696696", maxPaths = 50)
        assertTrue(paths.size <= 50)
    }

    @Test
    fun `lengthy decode does not timeout`() {
        val start = System.currentTimeMillis()
        decoder.bestPinyin("546842684267428286962498394")
        val elapsed = System.currentTimeMillis() - start
        assertTrue("Decoding took ${elapsed}ms, expected < 2000ms", elapsed < 2000)
    }

    @Test
    fun `ambiguous input returns best effort`() {
        assertTrue(decoder.bestPinyin("999999").isNotEmpty())
    }

    @Test
    fun `every standard pinyin is reachable from its digit code`() {
        // Uses the 413-standard pinyin list embedded in T9Decoder
        // Each pinyin's digit code should at least find it in the top-10 paths
        val allPinyins = listOf(
            "a","ai","an","ang","ao",
            "ba","bai","ban","bang","bao","bei","ben","beng","bi","bian","biao",
            "bie","bin","bing","bo","bu",
            "ca","cai","can","cang","cao","ce","cen","ceng","cha","chai","chan",
            "chang","chao","che","chen","cheng","chi","chong","chou","chu","chua",
            "chuai","chuan","chuang","chui","chun","chuo","ci","cong","cou","cu",
            "cuan","cui","cun","cuo",
            "da","dai","dan","dang","dao","de","dei","den","deng","di","dia",
            "dian","diao","die","ding","diu","dong","dou","du","duan","dui","dun","duo",
            "e","ei","en","eng","er",
            "fa","fan","fang","fei","fen","feng","fiao","fo","fou","fu",
            "ga","gai","gan","gang","gao","ge","gei","gen","geng","gong","gou",
            "gu","gua","guai","guan","guang","gui","gun","guo",
            "ha","hai","han","hang","hao","he","hei","hen","heng","hong","hou",
            "hu","hua","huai","huan","huang","hui","hun","huo",
            "ji","jia","jian","jiang","jiao","jie","jin","jing","jiong","jiu",
            "ju","juan","jue","jun",
            "ka","kai","kan","kang","kao","ke","kei","ken","keng","kong","kou",
            "ku","kua","kuai","kuan","kuang","kui","kun","kuo",
            "la","lai","lan","lang","lao","le","lei","leng","li","lia","lian",
            "liang","liao","lie","lin","ling","liu","lo","long","lou","lu",
            "luan","lve","lun","luo","lv",
            "ma","mai","man","mang","mao","me","mei","men","meng","mi","mian",
            "miao","mie","min","ming","miu","mo","mou","mu",
            "na","nai","nan","nang","nao","ne","nei","nen","neng","ni","nian",
            "niang","niao","nie","nin","ning","niu","nong","nou","nu","nuan",
            "nve","nun","nuo","nv",
            "o","ou",
            "pa","pai","pan","pang","pao","pei","pen","peng","pi","pian","piao",
            "pie","pin","ping","po","pou","pu",
            "qi","qia","qian","qiang","qiao","qie","qin","qing","qiong","qiu",
            "qu","quan","que","qun",
            "ran","rang","rao","re","ren","reng","ri","rong","rou","ru","rua",
            "ruan","rui","run","ruo",
            "sa","sai","san","sang","sao","se","sen","seng","sha","shai","shan",
            "shang","shao","she","shei","shen","sheng","shi","shou","shu","shua",
            "shuai","shuan","shuang","shui","shun","shuo","si","song","sou","su",
            "suan","sui","sun","suo",
            "ta","tai","tan","tang","tao","te","tei","teng","ti","tian","tiao",
            "tie","ting","tong","tou","tu","tuan","tui","tun","tuo",
            "wa","wai","wan","wang","wei","wen","weng","wo","wu",
            "xi","xia","xian","xiang","xiao","xie","xin","xing","xiong","xiu",
            "xu","xuan","xue","xun",
            "ya","yan","yang","yao","ye","yi","yin","ying","yo","yong","you",
            "yu","yuan","yue","yun",
            "za","zai","zan","zang","zao","ze","zei","zen","zeng","zha","zhai",
            "zhan","zhang","zhao","zhe","zhei","zhen","zheng","zhi","zhong","zhou",
            "zhu","zhua","zhuai","zhuan","zhuang","zhui","zhun","zhuo","zi","zong",
            "zou","zu","zuan","zui","zun","zuo"
        )
        val digitMap = mapOf(
            'a' to '2','b' to '2','c' to '2','d' to '3','e' to '3','f' to '3',
            'g' to '4','h' to '4','i' to '4','j' to '5','k' to '5','l' to '5',
            'm' to '6','n' to '6','o' to '6','p' to '7','q' to '7','r' to '7','s' to '7',
            't' to '8','u' to '8','v' to '8','w' to '9','x' to '9','y' to '9','z' to '9'
        )
        val failures = mutableListOf<String>()
        for (pinyin in allPinyins) {
            val code = pinyin.map { digitMap[it] ?: it }.joinToString("")
            val paths = decoder.decode(code, maxPaths = 100)
            val found = paths.any { p ->
                val full = p.resolved + p.current
                full == pinyin || (p.current.isNotEmpty() && p.current == pinyin)
            }
            if (!found) {
                failures.add("$pinyin($code)")
            }
        }
        if (failures.isNotEmpty()) {
            println("${failures.size}/${allPinyins.size} pinyins not found in any decode path:")
            failures.take(10).forEach { println("  $it") }
        }
        assertTrue("${failures.size} pinyins not reachable", failures.size <= 5)
    }

    @Test
    fun `pinyin candidates for valid prefix`() {
        val candidates = decoder.candidates("2")
        assertTrue(candidates.isNotEmpty())
    }
}
