# T1人体改造マルチブロック設計 Geminiレビュー試行

## 対象

- NeoForge 1.21.1
- 手術台を中心にしたT1マルチブロック
- 燃焼式SP入力と1000 SPのT1容量
- 部位難度・Tier・誤配置倍率による手術SP計算
- 今回は構造判定、SP保存、同期、コスト表示まで実装する段階案

## Gemini実行結果

`scripts/gemini-researcher.ps1` を使用し、次の設定済みモデルを順番に試した。

1. `gemini-3-flash`
2. `gemini-2.5-flash`
3. `gemini-2.5-pro`

すべてのモデルでAPI応答が返らず、CLIが待機状態になった。長時間待機後、各Gemini CLI子プロセスのみを終了した。認証情報は出力・保存していない。

## Gemini提案

取得できなかったため、採用・却下判定なし。

## Codex側で継続する検証

- `ItemStack#getBurnTime(null)` によるNeoForge燃料判定
- `BlockEntity` NBTへのSP永続保存
- `ContainerData` またはメニュー追加データによるクライアント同期
- サーバー側のみを正とするマルチブロック構造判定
- 誤配置コストが正規配置より安くならない計算
- 即時装着式から予約・確定式へ移行する際のアイテム複製対策

## 2026-07-23 再実行

Gemini連携修正後、`gemini-3.1-flash-lite`でレビュー取得に成功した。

### Geminiの主な提案

- SPを`BlockEntity`のサーバーtickと永続フィールドで管理する。
- 将来、燃料スロットを持つ場合は`IItemHandler`を使用する。
- 独自燃料値が必要ならタグ、レジストリ、Data Componentなどで管理する。
- `ItemStack#getBurnTime(null)`には依存せず、独自燃料登録を推奨する。
- 誤配置倍率を機械処理速度の倍率として扱う案。

### Codexの検証と採否

#### 採用

- SPを`BlockEntity`のNBTへ永続保存し、サーバーを正とする方針。
- 将来GUI付き燃料スロットを追加する場合に`IItemHandler`を使う方針。
- 独自の医療燃料を追加する場合、タグまたはData Componentで拡張可能にする方針。

#### 却下

- `ItemStack#getBurnTime(null)`が不安定という指摘。
  - NeoForge 21.1.235の`IItemStackExtension#getBurnTime(@Nullable RecipeType<?>)`では、`null`が正式に許容されている。
  - 今回は「他Modを含む、かまどで燃える全アイテム」を受け入れる仕様なので、このAPIが目的に合う。
- 誤配置倍率を処理速度として扱う案。
  - 1.25～2.0は手術SPコスト倍率であり、機械Tierの速度倍率ではない。
- SPを常時tickで減算する案。
  - 現設計では手術確定時に必要SPをまとめて消費するため、常時減算は行わない。

### 結論

現在の燃料判定、NBT保存、部位別SPコスト計算は維持する。次段階では予約された手術内容をサーバー側で再計算し、SP消費と成功判定を同一トランザクションで処理する。
