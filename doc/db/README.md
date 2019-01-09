# アプリを動作させるためには
## DropBoxに配置するデータ構成
DropBox上に下記データが必要です。
- テストのタイトルデータ (testlist.csv)
- テストの単語データ (例: quiz.csv)
- テストの品詞データ (wordtype.csv)

## CSVの仕様
本アプリではcsvファイルの1行目はヘッダ行となります。

例(testlist.csv)
```
id,titile,caption,filepath
1,テスト1,テスト1です。,quiz.csv
```
