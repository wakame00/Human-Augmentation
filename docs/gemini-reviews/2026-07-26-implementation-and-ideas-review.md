# Gemini review: 現在の実装と構想

## 入力

`docs/design-notes/implementation-and-ideas-2026-07-26.md` の全文を渡し、
矛盾、優先順位、T5までのデータ設計、DNA自動化、保存・同期リスク、
今は実装しない方がよいものを確認した。

## Gemini回答の要旨

- プレイヤーデータにはAttachmentを使う。
- DNA物品の可変情報にはData Componentを使う。
- DNA処理をRecipe、RecipeType、RecipeSerializerでデータ駆動化する。
- 人工部位の能力はAttribute Modifierを中心にする。
- FE機械はEnergy CapabilityとItem Handlerを使う。
- Mod連携、視覚効果、設定ファイル、T5終盤要素を段階的に追加する。
- 開発順は基盤、人工部位、DNA、Mod連携、バランス調整とする。

## Geminiの誤認・不採用点

### SPをプレイヤーAttachmentへ保存

現行のSPは手術設備側の動力であり、プレイヤー通貨ではない。
設備BlockEntityへ保存する現在の責務を維持する。

### Tier切替時にSPを消費

Tierは設備構造によって決まり、UIで購入・切替する能力Tierではない。
この提案は不採用。

### PacketDistributorが必須

現行の身体AttachmentはNeoForgeの同期コーデックと `player.syncData` を使用している。
追加の独自Packetが本当に必要な画面状態だけに限定する。

### DNAをMobドロップからPCR変換

現行仕様はDNAサンプラーで採取した、採取元Data Component付きサンプルを入力する。
通常のMobドロップを直接DNAへ変換する仕様にはしない。

### CreateをFE入力として扱う

Create標準は回転力であり、FE互換として一括りにはできない。
将来の専用連携工程として扱う。

### InterModCommsでMob IDを取得

Mob IDとDNA定義はResourceLocationとJSONで扱える。
必要性がない限り、各Mod APIへの直接依存やInterModCommsを増やさない。

## 採用する提案

1. DNA工程を独自Recipe/Serializerへ移し、FE量、時間、試薬、出力段階をJSON化する。
2. 遺伝子定義を採取元定義から分離し、Tier、効果、競合、発現コストをデータ駆動化する。
3. サーバー設定で採取率、FE倍率、手術コスト、失敗率を調整可能にする。
4. Mod連携より先に、バニラDNAと身体改造の完成ループを作る。
5. T5は単純な数値増加だけでなく、超過負荷や能動能力を候補として残す。

## Codexによる優先順位

1. 手術変更を即時反映から確定式トランザクションへ変更。
2. SP消費、時間、成功・失敗、中断時ロールバックをサーバー側で完成。
3. DNA装置の専用GUIと安全な搬入出。
4. DNA工程Recipeのデータ駆動化。
5. 遺伝子プロファイルを採取元単位から遺伝子単位へ分離。
6. バニラMob遺伝子の効果、弱点、競合、除去方法を実装。
7. T1/T2設備とDNAのゲーム内テスト、複製・同期・再ログイン検証。
8. 正式テクスチャ作成。その後にT3と自動化へ進む。

## 今は後回しにするもの

- T4/T5の複数DNA合成と量産。
- Mekanism体内原子炉。
- 多数のMod連携。
- 派手な粒子・身体変形描画。
- ProjectEやDraconic Evolutionを前提とした終盤バランス。

まずバニラ単体で、採取から注射、発現、除去までのループを完成させる。
