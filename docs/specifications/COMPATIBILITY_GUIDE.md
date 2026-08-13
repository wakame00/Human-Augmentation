# 外部Mod連携実装ガイド

## 基本方針

連携内容は後で決める。現段階では本体の安定性を優先し、外部Modが未導入、更新済み、ID変更済みでも本体を起動できる構造を維持する。

## リスク別の実装方法

### 低リスク: ID参照

- レシピ材料、Mob DNA、アイテム存在判定。
- `OptionalRegistryLookup`で文字列IDを取得する。
- 結果が空なら機能を登録しないか代替処理へ進む。
- レシピJSONには`neoforge:mod_loaded`と`neoforge:registered`条件を付ける。

### 中リスク: NeoForge標準Capability

- FE、ItemHandler、FluidHandlerなど。
- 外部Mod固有クラスをimportせずNeoForge標準Capabilityだけを利用する。
- 現在のDNA解析装置のFE入力がこの方式。

### 高リスク: 外部Java API・Mixin

- `com.wakame.humanaugmentation.compat.<modid>`へ隔離する。
- 本体クラスから外部API型をメソッド引数、戻り値、フィールドに出さない。
- `CompatibilityManager.initializeIsolated(target, "完全修飾クラス名")`で、対象Mod検出後だけロードする。
- `LinkageError`やリフレクション失敗時はその連携だけ停止する。
- 対応バージョン範囲を決めるまでは自動有効化しない。

## 現在検出できる候補

Create、Mekanism、Applied Energistics 2、Oritech、Alex's Caves、Goety、Iron's Spells 'n Spellbooks、ProjectE、CC: Tweaked、Ars Nouveau、Blood Magic、Ender IO、Draconic Evolution、Mystical Agriculture、Psi、SlashBlade、Ice and Fire系。

この一覧は「対応済み」を意味しない。検出と隔離の入口を用意しているだけで、実際の効果、素材、レシピ、バランスは個別に決定する。

## 新しい連携を追加する順序

1. 正式なMod IDと対象バージョンを確認。
2. `CompatTarget`へMod IDを追加。
3. ID参照だけで実現できるか判断。
4. 外部APIが必要なら隔離クラスと対応バージョン条件を用意。
5. Modなし、本対応版、未対応版の3環境で起動確認。
6. セーブ後にModを外してもワールドが読めることを確認。

## セーブデータ規則

- Attachmentへ外部APIクラスを保存しない。
- 保存値はResourceLocation、数値、文字列、標準NBTだけにする。
- 連携停止中も装着IDと損傷を保持し、効果・燃料消費・危険処理だけ止める。
- 外部アイテムそのものを必須保存する設計は、Mod削除時の欠損を許容できる場合だけ採用する。
