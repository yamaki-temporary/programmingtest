package ex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        ArrayList<LogData> logList = inputLog();
        ArrayList<String> subnetList = setSubnetList(logList);

        // 設問１
        if (Integer.parseInt(args[0]) == 1) {
            for(int i = 0; i < logList.size(); i++) {
                LogData data = logList.get(i);
                data.checkFailure();
                data.printFailure(data, 1);
            }
        }

        // 設問２
        if (Integer.parseInt(args[0]) == 2) {
            for(int i = 0; i < logList.size(); i++) {
                LogData data = logList.get(i);
                data.checkFailure(Integer.parseInt(args[1]));
                data.printFailure(data, 2);
            }
        }

        // 設問３(未実装)
        if (Integer.parseInt(args[0]) == 3) {
        }

        // 設問４(未実装)
        if (Integer.parseInt(args[0]) == 4) {
        }
    }

    // サーバーアドレスで検索
    private static int addressSearch(ArrayList<LogData> list, String searchAddress) {
        String address;
        for(int i = 0; i < list.size(); i++) {
            address = list.get(i).getServerAddress();
            // サーバーリストを検索し，引数で指定されたアドレスを持つインデックスを返す
            if (address.equals(searchAddress)) return i;
        }
        return -1;
    }

    // サブネットのリストを作成
    private static ArrayList<String> setSubnetList(ArrayList<LogData> list) {
        ArrayList<String> subnetList = new ArrayList<String>();
        boolean existFlag = false;      // ネット枠アドレスがリストに存在していればtrue
        String networkAddress = "";
        for(int i = 0; i < list.size(); i++) {
            networkAddress = list.get(i).getNetworkAddress();
            existFlag = false;
            for(int j = 0; j < subnetList.size(); j++) {
                if (networkAddress.equals(subnetList.get(j))) existFlag = true;
            }
            if(!existFlag){
                subnetList.add(networkAddress);
            }
        }
        return subnetList;
    }

    // 監視ログファイル(csv形式)を読み込む
    private static ArrayList<LogData> inputLog(){
        ArrayList<LogData> logList = new ArrayList<LogData>();
        String filename = "logdata.csv";
        File file = new File(filename);
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ( ( line = br.readLine()) != null ) {
                String[] cols = line.split(",");            // カンマで分割
                // 同じサーバーアドレスのインスタンスが生成されているか検索
                int i = addressSearch(logList, cols[1]);
                if(i == -1){
                    logList.add(new LogData(cols));         // 生成されていなければ生成
                }else{
                    logList.get(i).newResponse(cols[0], cols[2]);   // 生成されていれば追加
                }
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return logList;
    }
}
