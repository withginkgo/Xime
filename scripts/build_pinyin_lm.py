import argparse, json, math, os, struct
from collections import defaultdict
from pathlib import Path

parser = argparse.ArgumentParser(
    description="从 RIME-LMDG 词典构建拼音二元语言模型 (pinyin_lm.bin)"
)
parser.add_argument(
    "--rime-lmdg-dir",
    required=True,
    help="RIME-LMDG 词典目录的路径，例如 /path/to/RIME-LMDG-wanxiang/dicts"
)
args = parser.parse_args()

RIME_LMDG_DIR = Path(args.rime_lmdg_dir)
PROJECT_ROOT = Path(__file__).resolve().parent.parent

TONE_MAP = {'ā':'a','á':'a','ǎ':'a','à':'a','ō':'o','ó':'o','ǒ':'o','ò':'o','ē':'e','é':'e','ě':'e','è':'e','ī':'i','í':'i','ǐ':'i','ì':'i','ū':'u','ú':'u','ǔ':'u','ù':'u','ǖ':'v','ǘ':'v','ǚ':'v','ǜ':'v','ü':'v'}
def rm_tone(s):
    return ''.join(TONE_MAP.get(c, c) for c in s)

standard = {"a","ai","an","ang","ao","ba","bai","ban","bang","bao","bei","ben","beng",
    "bi","bian","biao","bie","bin","bing","bo","bu","ca","cai","can","cang","cao",
    "ce","cen","ceng","cha","chai","chan","chang","chao","che","chen","cheng","chi",
    "chong","chou","chu","chua","chuai","chuan","chuang","chui","chun","chuo","ci",
    "cong","cou","cu","cuan","cui","cun","cuo","da","dai","dan","dang","dao","de",
    "dei","den","deng","di","dia","dian","diao","die","ding","diu","dong","dou","du",
    "duan","dui","dun","duo","e","ei","en","eng","er","fa","fan","fang","fei","fen",
    "feng","fiao","fo","fou","fu","ga","gai","gan","gang","gao","ge","gei","gen",
    "geng","gong","gou","gu","gua","guai","guan","guang","gui","gun","guo","ha","hai",
    "han","hang","hao","he","hei","hen","heng","hong","hou","hu","hua","huai","huan",
    "huang","hui","hun","huo","ji","jia","jian","jiang","jiao","jie","jin","jing",
    "jiong","jiu","ju","juan","jue","jun","ka","kai","kan","kang","kao","ke","kei",
    "ken","keng","kong","kou","ku","kua","kuai","kuan","kuang","kui","kun","kuo","la",
    "lai","lan","lang","lao","le","lei","leng","li","lia","lian","liang","liao","lie",
    "lin","ling","liu","lo","long","lou","lu","luan","lve","lun","luo","lv","ma","mai",
    "man","mang","mao","me","mei","men","meng","mi","mian","miao","mie","min","ming",
    "miu","mo","mou","mu","na","nai","nan","nang","nao","ne","nei","nen","neng","ni",
    "nian","niang","niao","nie","nin","ning","niu","nong","nou","nu","nuan","nve","nun",
    "nuo","nv","o","ou","pa","pai","pan","pang","pao","pei","pen","peng","pi","pian",
    "piao","pie","pin","ping","po","pou","pu","qi","qia","qian","qiang","qiao","qie",
    "qin","qing","qiong","qiu","qu","quan","que","qun","ran","rang","rao","re","ren",
    "reng","ri","rong","rou","ru","rua","ruan","rui","run","ruo","sa","sai","san","sang",
    "sao","se","sen","seng","sha","shai","shan","shang","shao","she","shei","shen",
    "sheng","shi","shou","shu","shua","shuai","shuan","shuang","shui","shun","shuo","si",
    "song","sou","su","suan","sui","sun","suo","ta","tai","tan","tang","tao","te","tei",
    "teng","ti","tian","tiao","tie","ting","tong","tou","tu","tuan","tui","tun","tuo",
    "wa","wai","wan","wang","wei","wen","weng","wo","wu","xi","xia","xian","xiang","xiao",
    "xie","xin","xing","xiong","xiu","xu","xuan","xue","xun","ya","yan","yang","yao","ye",
    "yi","yin","ying","yo","yong","you","yu","yuan","yue","yun","za","zai","zan","zang",
    "zao","ze","zei","zen","zeng","zha","zhai","zhan","zhang","zhao","zhe","zhei","zhen",
    "zheng","zhi","zhong","zhou","zhu","zhua","zhuai","zhuan","zhuang","zhui","zhun",
    "zhuo","zi","zong","zou","zu","zuan","zui","zun","zuo"}

pinyin_dict = PROJECT_ROOT / "app/src/main/assets/rime/pinyin_simp.dict.yaml"
print(f"Loading {pinyin_dict} ...", flush=True)
with open(pinyin_dict, 'r') as f:
    ok = False
    py_cnt = defaultdict(float)
    for line in f:
        s = line.strip()
        if s == '...':
            ok = True; continue
        if not ok or not s or s.startswith('#'):
            continue
        parts = s.split('\t')
        if len(parts) >= 3:
            try:
                w = float(parts[2])
                for py in parts[1].split():
                    py_cnt[rm_tone(py)] += w
            except ValueError:
                pass
py_cnt = {p: max(py_cnt.get(p, 0.0), 1.0) for p in standard}
total = sum(py_cnt.values())
print(f"  413 unigrams, total={total:.0f}", flush=True)

# 词典列表：https://github.com/amzxyz/RIME-LMDG
dict_names = [
    "jichu.dict.yaml", "zi.dict.yaml", "diming.dict.yaml",
    "renming.dict.yaml", "shici.dict.yaml", "duoyin.dict.yaml",
    "lianxiang.dict.yaml", "wuzhong.dict.yaml",
]
dict_paths = [RIME_LMDG_DIR / name for name in dict_names]

print("Loading word dicts for bigrams ...", flush=True)
bi_cnt = defaultdict(float)
for path in dict_paths:
    print(f"  {path.name} ...", flush=True)
    with open(path, 'r', encoding='utf-8') as f:
        ok2 = False
        for line in f:
            s = line.strip()
            if s == '...':
                ok2 = True; continue
            if not ok2 or not s or s.startswith('#'):
                continue
            parts = s.split('\t')
            if len(parts) >= 3:
                try:
                    w = float(parts[2])
                    if w > 0:
                        syls = [rm_tone(p) for p in parts[1].strip().split()]
                        for i in range(len(syls) - 1):
                            if syls[i] in standard and syls[i+1] in standard:
                                bi_cnt[f"{syls[i]}|{syls[i+1]}"] += w
                except ValueError:
                    pass
print(f"  {len(bi_cnt)} bigrams", flush=True)

uni_lp = {p: math.log(c / total) for p, c in py_cnt.items()}

bi_lp = {}
for key, cnt in bi_cnt.items():
    p1, p2 = key.split('|')
    c1 = py_cnt[p1]
    bi_lp[key] = math.log(cnt / c1)

# Write binary LM: header + unigrams + bigrams
# Format:
#   4 bytes: num unigrams (int, big-endian)
#   4 bytes: num bigrams (int, big-endian)
#   8 bytes: k value (double, big-endian)
#   unigrams: for each,
#     1 byte: pinyin length
#     N bytes: pinyin ASCII
#     8 bytes: log prob (double, big-endian)
#   bigrams: for each,
#     2 bytes: p1 index (short, big-endian)
#     2 bytes: p2 index (short, big-endian)
#     8 bytes: log prob (double, big-endian)

vocab = sorted(standard)
p2idx = {p: i for i, p in enumerate(vocab)}
k = 0.5

outpath = PROJECT_ROOT / "app/src/main/assets/pinyin_lm.bin"
with open(outpath, 'wb') as f:
    f.write(struct.pack('>I', len(vocab)))
    f.write(struct.pack('>I', len(bi_lp)))
    f.write(struct.pack('>d', k))
    for p in vocab:
        pb = p.encode('ascii')
        f.write(struct.pack('B', len(pb)))
        f.write(pb)
        f.write(struct.pack('>d', uni_lp[p]))
    for key, lp in bi_lp.items():
        p1, p2 = key.split('|')
        f.write(struct.pack('>H', p2idx[p1]))
        f.write(struct.pack('>H', p2idx[p2]))
        f.write(struct.pack('>d', lp))

sz = os.path.getsize(outpath)
print(f"Binary LM saved: {sz:,} bytes", flush=True)
# test resources 已通过软链接指向 main assets，无需重复写入
