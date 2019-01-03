# CSVチェックツール
## 概要
試験単語データをチェックするツールです。<br>
チェックする項目は下記です。
- スペイン語の例文に"(...)"が存在するか
- スペイン語の例文と日本語の例文の数が合っているか

※ CSVの形式が正しいかどうかはチェックしません。

## 動作環境
Python 3.6.5

## 使い方
ファイル構造
```
tool/
    └ src/
        ├ csv/
        └ checkcsv.py
```

1. tool/src/csv に 単語データのcsvファイルを入れます。
1. tool/src で `python checkcsv.py` を実行します。
1. 不正なデータがある行が表示されるので、必要に応じてCSVファイルを修正します。
```
Check ./csv/quiz20180923.csv ...
スペイン語の例文にかっこがありません。
Invalid Data! (Row: 20)
スペイン語と日本語の例文の数が合っていません。
Invalid Data! (Row: 28)
Finish checking csv
```
