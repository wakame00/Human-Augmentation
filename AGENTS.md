# エージェント体制

- メイン: OpenAI Codex（コード実装と全体のオーケストレーション）
- サブ: `@gemini-researcher`（外部ドキュメントの調査、バグ原因の多角的分析）

# ルール

ユーザーから「`@gemini-researcher` に〜を調べさせて」と指示された場合、次のコマンドを自動実行し、その結果を取り込んで作業すること。

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\gemini-researcher.ps1 -Prompt "調査内容"
```

- 認証には環境変数 `GEMINI_API_KEY` を使用する。
- APIキーや `.env` ファイルをリポジトリへコミットしない。
- Geminiには原則として調査・分析を依頼し、ファイル変更はメインエージェントが行う。
- 既定モデルが高負荷・クォータ超過・利用不可で終了した場合、ランチャーは設定済みの別Geminiモデルへ順番に切り替える。
- Geminiの重要な提案は `docs/gemini-reviews/` に、Codexの検証結果と採用・却下理由を添えて保存する。
