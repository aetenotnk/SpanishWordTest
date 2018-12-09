package jp.yutayamazaki.spanishwordtest.dropbox;

import android.util.Log;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class DropBox {
    private DbxClientV2 client;

    public DropBox(String accessToken, String userAgent){
        DbxRequestConfig config = DbxRequestConfig.newBuilder(userAgent).build();
        this.client = new DbxClientV2(config, accessToken);
    }

    /**
     * オンライン上からファイルをダウンロードする
     * @param saveDir 保存するディレクトリ
     * @param filename ドロップボックス上のアプリケーションフォルダより後のファイル名またはパス
     * @return String ファイルをダウンロードしたパス
     */
    public String downloadFile(String filename, String saveDir){
        String fromPath = "/" + filename;
        String toPath = saveDir + "/" + filename;

        try{
            DbxDownloader downloader  = client.files().download(fromPath);
            File outFile = new File(toPath);

            InputStreamReader isr = new InputStreamReader(downloader.getInputStream());
            OutputStreamWriter osr = new OutputStreamWriter(new FileOutputStream(outFile));

            int c;
            while((c = isr.read()) != -1){
                osr.write(c);
            }
            osr.flush();
        }
        catch(DbxException | IOException e){
            Log.e("DropBoxError", "Cannot downloadTextFile");
            e.printStackTrace();
        }

        return toPath;
    }

    public String getCurrentUserName(){
        try{
            return this.client.users().getCurrentAccount().getName().getDisplayName();
        }
        catch (DbxException e){
            e.printStackTrace();
        }

        return null;
    }
}
