package ex;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class LogData {
    private String serverAddress;   // サーバーアドレス
    private List<String> date;      // 確認日時のリスト
    private List<String> ping;      // 応答結果のリスト
    private int responseCount;      // 確認回数
    private String networkAddress;  // ネットワークアドレス（サブネット判別用）
    private boolean failureFlag;    // trueのとき故障あり
    private String failureStart;    // 故障開始時間
    private String failureEnd;      // 故障終了時間
    private boolean overloadFlag;   // trueのとき過負荷あり
    private String overloadStart;   // 過負荷開始時間
    private String overloadEnd;     // 過負荷終了時間

    // コンストラクタ
    public LogData(String[] cols) {
        this.date = new ArrayList<String>();
        this.date.add(cols[0]);
        this.serverAddress = cols[1];
        this.ping = new ArrayList<String>();
        this.ping.add(cols[2]);
        this.responseCount = 1;
        this.networkAddress = checkAddress(this.serverAddress);
        this.failureFlag = false;
        this.failureStart = "";
        this.failureEnd = "";
        this.overloadFlag = false;
        this.overloadStart = "";
        this.overloadEnd = "";
    }

    // サーバーアドレスを返す
    public String getServerAddress() {
        return this.serverAddress;
    }

    // n番目の確認日時を返す
    public String getDate(int n) {
        return this.date.get(n);
    }

    // n番目の応答時間を返す
    public String getPing(int n) {
        return this.ping.get(n);
    }

    // ネットワークアドレスを返す
    public String getNetworkAddress() {
        return this.networkAddress;
    }

    // 確認回数を返す
    public int getResponseCount() {
        return this.responseCount;
    }

    // 故障したかどうかを返す
    public boolean getFailureFlag() {
        return this.failureFlag;
    }

    // 新しい応答結果を追加
    public void newResponse(String date, String ping) {
        this.date.add(date);
        this.ping.add(ping);
        this.responseCount++;
    }

    // 故障(1回以上タイムアウト)の確認
    public void checkFailure() {
        String failureStart = "";
        boolean flag = false;       // タイムアウト中はtrue

        for (int i = 0; i < this.getResponseCount(); i++) {
            if (this.getPing(i).equals("-") && !flag) {
                flag = true;
                failureStart = this.getDate(i);
            } else if (!this.getPing(i).equals("-") && flag) {
                this.failureFlag = true;
                this.failureStart = failureStart;
                this.failureEnd = this.getDate(i);
                return;
            }
        }
        if (flag) {
             this.failureFlag = true;
             this.failureStart = failureStart;
             this.failureEnd = "now";
        }
    }

    // 故障(n回以上連続でタイムアウト)の確認
    public void checkFailure(int n) {
        String failureStart = "";
        boolean flag = false;           // タイムアウト中はtrue
        int count = 0;                  // 連続するタイムアウト数

        for (int i = 0; i < this.getResponseCount(); i++) {
            if (this.getPing(i).equals("-")) {
                if (!flag) {
                    flag = true;
                    failureStart = this.getDate(i);
                }
                count++;
            } else {
                if (flag && count >= n) {
                    this.failureFlag = true;
                    this.failureStart = failureStart;
                    this.failureEnd = this.getDate(i);
                    return;
                }
                flag = false;
                count = 0;
            }
        }
        if (flag && count >= n) {
             this.failureFlag = true;
             this.failureStart = failureStart;
             this.failureEnd = "now";
        }
    }

    // ネットワークアドレスの算出
    public static String checkAddress (String str) {
        int serverAddress[] = new int[4];
        int subnetmask[] = new int[4];
        int networkAddress[] = new int[4];

        // サーバーアドレスを用意
        String[] strings = str.split("/");
        int prefix = Integer.parseInt(strings[1]);  // ネットワークプレフィックス長を保存
        strings = strings[0].split("\\.");          // サーバーアドレスを4分割して配列に保存

        for (int i = 0; i < 4; i++) {
            serverAddress[i] = Integer.parseInt(strings[i]);    // Stringからintへキャスト
        }

        // サブネットマスクを用意
        String tmpStr = "";
        for (int j = 0; j < 32; j++) {
            if (prefix > 0) {
                tmpStr += "1";          // ネットワークプレフィックス長と同じ数1を追加し，
                prefix--;
            } else {
                tmpStr += "0";          // 残りを0で埋める
            }
        }

        int j = 0;
        for (int i = 0; i < 4; i++) {
            // 4分割し，Stringからintへキャストして保存
            subnetmask[i] = Integer.parseInt(tmpStr.substring(j, j + 8), 2);
            j += 8;
        }

        tmpStr = "";
        for (int k = 0; k < 3; k++) {
            // サーバーアドレスとサブネットマスクの論理積から
            // ネットワークアドレスを算出する
            networkAddress[k] = serverAddress[k] & subnetmask[k];
            tmpStr = tmpStr + String.valueOf(networkAddress[k]) + ".";
        }
        tmpStr = tmpStr + String.valueOf(networkAddress[3]);

        return tmpStr;

    }

    // 故障期間の出力
    public void printFailure(LogData log, int i){
        try {
            FileWriter fw = new FileWriter("result" + i + ".txt", true);
            if (this.getFailureFlag()) {
                fw.write("--- " + this.getServerAddress() + " : 故障あり -------------\r\n");
                fw.write(this.failureStart + " - " + this.failureEnd + "\r\n" );
            } else {
                fw.write("--- " + this.getServerAddress() + " : 故障なし -------------\r\n");
            }
            fw.write(" \r\n");
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }
}
