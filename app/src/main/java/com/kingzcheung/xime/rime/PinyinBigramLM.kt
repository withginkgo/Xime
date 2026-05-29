package com.kingzcheung.xime.rime

import java.io.DataInputStream
import java.io.InputStream

class PinyinBigramLM private constructor(
    private val unigramLogProb: DoubleArray,
    private val bigramLogProb: Map<Int, Double>,
    private val vocab: Array<String>,
    private val vocabIndex: Map<String, Int>,
    private val k: Double
) {
    private val fallbackProb: Double by lazy {
        unigramLogProb.minOrNull() ?: -20.0
    }

    fun transitionLogProb(prev: String, cur: String): Double {
        val p1 = vocabIndex[prev] ?: return unigramProb(cur)
        val p2 = vocabIndex[cur] ?: return fallbackProb
        val key = (p1 shl 16) or p2
        val cached = bigramLogProb[key]
        if (cached != null) return cached
        return unigramLogProb[p2]
    }

    fun unigramProb(pinyin: String): Double {
        val idx = vocabIndex[pinyin] ?: return fallbackProb
        return unigramLogProb[idx]
    }

    companion object {
        fun loadFromStream(stream: InputStream): PinyinBigramLM {
            val dis = DataInputStream(stream)
            val numVocab = dis.readInt()
            val numBigrams = dis.readInt()
            val k = dis.readDouble()

            val vocab = Array(numVocab) { "" }
            val unigramLP = DoubleArray(numVocab)
            val vocabIndex = HashMap<String, Int>(numVocab)

            for (i in 0 until numVocab) {
                val len = dis.readUnsignedByte()
                val bytes = ByteArray(len)
                dis.readFully(bytes)
                val pinyin = String(bytes, Charsets.US_ASCII)
                vocab[i] = pinyin
                vocabIndex[pinyin] = i
                unigramLP[i] = dis.readDouble()
            }

            val bigramLP = HashMap<Int, Double>(numBigrams)
            for (i in 0 until numBigrams) {
                val p1 = dis.readUnsignedShort()
                val p2 = dis.readUnsignedShort()
                val lp = dis.readDouble()
                bigramLP[(p1 shl 16) or p2] = lp
            }

            return PinyinBigramLM(unigramLP, bigramLP, vocab, vocabIndex, k)
        }
    }
}
