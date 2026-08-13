# Human Augmentation テクスチャ制作ガイド

更新日: 2026-08-09

## 1. 基本ルール

- 基本解像度は**16×16ピクセル**。
- 描画中だけ800%～1600%へ拡大してよいが、提出PNGは16×16原寸にする。
- 拡大・縮小は必ず「最近傍」「ニアレストネイバー」「アンチエイリアスなし」。
- カラーモードはRGBA、PNG形式。
- アイテム背景は完全透明。白、灰色、チェッカー模様を画像へ焼き込まない。
- ブロックテクスチャは通常不透明。ガラス・発光部など必要な場合だけ透明度を使う。
- ファイル名は小文字英数字とアンダースコアのみ。空白、日本語、大文字、連番だけの名前を避ける。
- JPEGは輪郭と色が崩れるため使用しない。

推奨ツールはAseprite、LibreSprite、Piskel、Paint.NET、GIMPなど。どのツールでも原寸PNGを出力できればよい。

## 2. 共通パレット

| 用途 | 色 |
|---|---|
| UI・機械の暗部 | `#0F1423` |
| 医療・電力 | `#00D6FF` |
| 生体・培養 | `#00FF66` |
| 危険・損傷 | `#FF3344` |
| 明るい文字・ハイライト | `#E0F7FA` |

自然臓器には肉色・暗赤・骨色を追加してよい。Tier差はネオン量だけでなく素材で表現する。

- T1: 鉄、ボルト、煤、炉、赤ランプ。
- T2: 鋼鉄、精密部品、シアン表示。
- T3: 密閉容器、生体液、緑配管。
- T4: 黒青装甲、神経接続、赤い危険コア。
- T5: 未確定。T4より派手にするだけではなく、異質な素材や積層構造で差別化する。

## 3. アイテムテクスチャ

### 3.1 保存場所

```text
src/main/resources/assets/humanaugmentation/textures/item/<item_id>.png
```

例:

```text
textures/item/artificial_heart.png
textures/item/natural_right_hand.png
textures/item/augmentation_repair_kit.png
```

モデルJSON:

```text
src/main/resources/assets/humanaugmentation/models/item/<item_id>.json
```

平面アイテムの基本形:

```json
{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "humanaugmentation:item/artificial_heart"
  }
}
```

注射器、サンプラー、工具のように手へ斜めに持たせる場合:

```json
{
  "parent": "minecraft:item/handheld",
  "textures": {
    "layer0": "humanaugmentation:item/dna_sampler"
  }
}
```

### 3.2 描画基準

- 外周1ピクセルを透明余白として残すのを基本にする。
- 輪郭は真っ黒一色より、暗い紺・暗灰・暗赤を使う。
- 16×16で判別できるシルエットを最優先する。
- 細線を増やしすぎず、明暗を3～5段階程度に抑える。
- 左右パーツはコピーだけで済ませず、正しく左右反転して別ファイルにする。
- 自然部位は有機的、人工部位は金属枠・端子・配線を入れて区別する。
- T1～T4は同じ輪郭の色違いだけにせず、コア、装甲、配管の量を変える。

## 4. ブロックテクスチャ

### 4.1 保存場所

```text
src/main/resources/assets/humanaugmentation/textures/block/<name>.png
```

全面共通なら1枚、向きがある機械は最低3枚を用意する。

```text
<machine>_front.png   正面・操作面
<machine>_side.png    左右・背面
<machine>_top.png     上面
```

底面が必要なら `<machine>_bottom.png` を追加する。

### 4.2 全面共通モデル

```json
{
  "parent": "minecraft:block/cube_all",
  "textures": {
    "all": "humanaugmentation:block/medical_frame_t3"
  }
}
```

### 4.3 向き付き機械モデル

モデルは北向きを基準に描く。

```json
{
  "parent": "minecraft:block/cube",
  "textures": {
    "down": "humanaugmentation:block/machine_bottom",
    "up": "humanaugmentation:block/machine_top",
    "north": "humanaugmentation:block/machine_front",
    "south": "humanaugmentation:block/machine_side",
    "west": "humanaugmentation:block/machine_side",
    "east": "humanaugmentation:block/machine_side",
    "particle": "humanaugmentation:block/machine_side"
  }
}
```

BlockStateで設置方向へ回転させる。

```json
{
  "variants": {
    "facing=north": { "model": "humanaugmentation:block/machine" },
    "facing=east":  { "model": "humanaugmentation:block/machine", "y": 90 },
    "facing=south": { "model": "humanaugmentation:block/machine", "y": 180 },
    "facing=west":  { "model": "humanaugmentation:block/machine", "y": 270 }
  }
}
```

正面を複数面へ描くと向きが分かりにくくなるため、操作画面・炉口・入出力口は原則1面だけにする。

## 5. アニメーション

液体、心電図、電力、DNA解析画面は縦長PNGと`.png.mcmeta`でアニメーション可能。

16×16を4フレームにする場合、PNGは16×64。

```json
{
  "animation": {
    "frametime": 4,
    "interpolate": false
  }
}
```

ピクセル感を維持するため、通常は`interpolate: false`。点滅が激しすぎないよう1秒あたり2～5フレーム程度を基準にする。

## 6. こちらへ渡す方法

最も確実なのは、16×16原寸PNGを次の情報と一緒に渡す方法。

```text
用途: DNA解析装置の正面
希望ID: dna_analyzer_front
向き: 北が正面
透明背景: なし
アニメーション: なし
```

複数ファイルはZIPにまとめてもよい。フォルダは次のようにすると、そのまま配置できる。

```text
textures/
  item/
    artificial_heart.png
  block/
    dna_analyzer_front.png
    dna_analyzer_side.png
    dna_analyzer_top.png
```

拡大スクリーンショットしかない場合も取り込み可能だが、次の問題が起きやすい。

- 切り抜き位置が1ピクセルずれる。
- 灰色背景と本体の灰色が一緒に透明化される。
- 拡大時の補間で中間色が混ざる。

可能ならスクリーンショットではなく、編集ソフトから出力した原寸PNGを渡す。

## 7. 現在の提供画像の再取り込み

今回の拡大画像から16×16へ変換する処理は次に保存している。

```text
scripts/import-user-textures.ps1
```

元画像名と切り抜き座標が固定されているため、新しい画像へそのまま使わない。新しい原寸PNGはスクリプトを通さず、対応する`textures/item`または`textures/block`へ配置する。

## 8. 現在、専用画像を優先して用意したいもの

### 優先度A: ゲーム内で頻繁に見るもの

- `augmentation_repair_kit`
- T1人工心臓、肺、眼、筋肉、骨格、脊椎、皮膚
- T2適応皮膚、動力筋肉
- 自然脳、眼、心臓、肺、消化器、血液、筋肉、骨格、脊椎、皮膚
- 自然右手、左手、右足、左足
- DNA抽出試薬、DNA増幅ミックス、滅菌注射器

### 優先度B: Tier差が現在弱い設備

- T2/T3/T4手術台
- T2/T3/T4手術アーム
- T3/T4医療フレーム
- 精密制御モジュール
- 生命維持モジュール
- 神経制御モジュール

### 優先度C: 開発専用

- クリエイティブ手術端末
- 四肢配置プレビュー
- 両脚配置プレビュー

開発専用品は最終公開ビルドで非表示または削除する可能性があるため、最後に描く。

## 9. ゲーム内確認

1. PNGとモデルJSONを保存。
2. 開発中のゲームで`F3 + T`を押してリソースを再読み込み。
3. 紫黒の欠落テクスチャ、上下左右、透明縁、手持ち角度を確認。
4. ブロックは北・東・南・西へ置いて正面を確認。
5. 問題がなければ次を実行。

```powershell
.\gradlew.bat build
```

新しいアイテムIDやブロックIDを追加した場合は、PNGだけでは足りない。登録Java、モデル、翻訳、レシピ、ブロックならBlockStateとLoot Tableも必要になる。
