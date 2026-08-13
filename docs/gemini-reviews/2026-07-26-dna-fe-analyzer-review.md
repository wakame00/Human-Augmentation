# Gemini review: FE式DNA解析装置

## 依頼した内容

採取、抽出、増幅、解析、注射器充填、体内定着からなるFE式DNA解析工程について、
NeoForge 1.21.1 API、永続化、同期、複製バグの観点でレビューを依頼した。

## Geminiの回答要旨

- `IEnergyStorage` と `ItemStackHandler` をCapabilityとして公開する。
- インベントリとFEをNBTへ保存する。
- 内容変更時は `setChanged()` を呼ぶ。
- FE入力では容量超過を防ぐ。

Geminiは依頼を「DNA個数に応じてFE容量が増える装置」と誤解し、
動的FE容量を中心とした実装例を返した。

## Codexによる検証

- Capability、NBT、`setChanged()`の一般論は採用。
- DNA個数による動的FE容量は仕様外なので却下。
- 回答中のCapability名には版差異が疑われるため、実プロジェクトでコンパイルして確認する。
- サーバー側だけで工程を進め、入力消費と出力生成を同一tick内で確定させる。
- FEはsimulate後に実抽出し、隣接源から受け取れた量だけを確定する。
- DNA採取元は既存の同期対応Data Componentを引き継ぎ、体内定着は既存Attachmentへ保存する。

## 結論

回答の中心設計は不採用。NeoForgeの一般的な永続化・Capability注意点のみ採用し、
実装はCodex側の工程状態機械として検証する。
