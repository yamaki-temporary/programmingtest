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

        // �ݖ�P
        if (Integer.parseInt(args[0]) == 1) {
            for(int i = 0; i < logList.size(); i++) {
                LogData data = logList.get(i);
                data.checkFailure();
                data.printFailure(data, 1);
            }
        }

        // �ݖ�Q
        if (Integer.parseInt(args[0]) == 2) {
            for(int i = 0; i < logList.size(); i++) {
                LogData data = logList.get(i);
                data.checkFailure(Integer.parseInt(args[1]));
                data.printFailure(data, 2);
            }
        }

        // �ݖ�R(������)
        if (Integer.parseInt(args[0]) == 3) {
        }

        // �ݖ�S(������)
        if (Integer.parseInt(args[0]) == 4) {
        }
    }

    // �T�[�o�[�A�h���X�Ō���
    private static int addressSearch(ArrayList<LogData> list, String searchAddress) {
        String address;
        for(int i = 0; i < list.size(); i++) {
            address = list.get(i).getServerAddress();
            // �T�[�o�[���X�g���������C�����Ŏw�肳�ꂽ�A�h���X�����C���f�b�N�X��Ԃ�
            if (address.equals(searchAddress)) return i;
        }
        return -1;
    }

    // �T�u�l�b�g�̃��X�g���쐬
    private static ArrayList<String> setSubnetList(ArrayList<LogData> list) {
        ArrayList<String> subnetList = new ArrayList<String>();
        boolean existFlag = false;      // �l�b�g�g�A�h���X�����X�g�ɑ��݂��Ă����true
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

    // �Ď����O�t�@�C��(csv�`��)��ǂݍ���
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
                String[] cols = line.split(",");            // �J���}�ŕ���
                // �����T�[�o�[�A�h���X�̃C���X�^���X����������Ă��邩����
                int i = addressSearch(logList, cols[1]);
                if(i == -1){
                    logList.add(new LogData(cols));         // ��������Ă��Ȃ���ΐ���
                }else{
                    logList.get(i).newResponse(cols[0], cols[2]);   // ��������Ă���Βǉ�
                }
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return logList;
    }
}
