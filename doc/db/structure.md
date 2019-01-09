# データベースの構成

## 使用するデータベース
- SQLite

## 各テーブル定義
### db.TestTitle
テストタイトルデータ

| 項番 | カラム論理名 | カラム物理名 | 型 | Nullable | 主キー制約 | 外部キー制約 | コメント|
---|---|---|---|---|---|---|---|
|1|テストID|id|integer|true|true| | |
|2|テストのタイトル|title|text|true|false| | |
|3|テストの説明|caption|text|true|false| | |
|4|単語データのパス|filepath|text|true|false|DropBox上の単語ファイルのパス|

### db.TestWord_WORD
テストの単語データ。
テスト毎に単語データテーブルを作成。（例:db.TestTitle.id=1 のデータは TestWord**1**_WORD)

| 項番 | カラム論理名 | カラム物理名 | 型 | Nullable | 主キー制約 | 外部キー制約 | コメント|
---|---|---|---|---|---|---|---|
|1|スペイン語の単語|word_spanish|text|true|false| | |
|2|日本語の単語|word_japanese|text|true|false| | |
|3|スペイン語の例文|example_spanish|text|true|false| | |
|4|日本語の例文|example_japanese|text|true|false|db.WordType.word_type_string| |

### db.WordType
単語の品詞データ

| 項番 | カラム論理名 | カラム物理名 | 型 | Nullable | 主キー制約 | 外部キー制約 | コメント|
---|---|---|---|---|---|---|---|
|1|品詞を識別する文字列|word_type_string|text|true|true| | |
|2|表示名|word_type_display|text|true|false| | |
