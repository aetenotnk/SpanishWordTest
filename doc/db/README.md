# アプリを動作させるためには
## DropBoxへの接続
1. [DropBox Developers](https://www.dropbox.com/developers)からアプリ作成します。
1. 作成したアプリのアクセストークンを発行します。
1. /app/src/main/res/values/strings.xml にdropbox_token と dropbox_useragant を追記します。
    ```
    <string name="dropbox_token">コピーしたアクセストークン</string>
    <string name="dropbox_useragant">任意の文字列</string>
    ```
1. /doc/db/example のファイルを DropBox のアプリフォルダにコピーします。
1. Android Studio で ビルド、起動しテスト一覧画面が表示されれば成功です。

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
