package ex;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class LogData {
    private String serverAddress;   // �T�[�o�[�A�h���X
    private List<String> date;      // �m�F�����̃��X�g
    private List<String> ping;      // �������ʂ̃��X�g
    private int responseCount;      // �m�F��
    private String networkAddress;  // �l�b�g���[�N�A�h���X�i�T�u�l�b�g���ʗp�j
    private boolean failureFlag;    // true�̂Ƃ��̏Ⴀ��
    private String failureStart;    // �̏�J�n����
    private String failureEnd;      // �̏�I������
    private boolean overloadFlag;   // true�̂Ƃ��ߕ��ׂ���
    private String overloadStart;   // �ߕ��׊J�n����
    private String overloadEnd;     // �ߕ��׏I������

    // �R���X�g���N�^
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

    // �T�[�o�[�A�h���X��Ԃ�
    public String getServerAddress() {
        return this.serverAddress;
    }

    // n�Ԗڂ̊m�F������Ԃ�
    public String getDate(int n) {
        return this.date.get(n);
    }

    // n�Ԗڂ̉������Ԃ�Ԃ�
    public String getPing(int n) {
        return this.ping.get(n);
    }

    // �l�b�g���[�N�A�h���X��Ԃ�
    public String getNetworkAddress() {
        return this.networkAddress;
    }

    // �m�F�񐔂�Ԃ�
    public int getResponseCount() {
        return this.responseCount;
    }

    // �̏Ⴕ�����ǂ�����Ԃ�
    public boolean getFailureFlag() {
        return this.failureFlag;
    }

    // �V�����������ʂ�ǉ�
    public void newResponse(String date, String ping) {
        this.date.add(date);
        this.ping.add(ping);
        this.responseCount++;
    }

    // �̏�(1��ȏ�^�C���A�E�g)�̊m�F
    public void checkFailure() {
        String failureStart = "";
        boolean flag = false;       // �^�C���A�E�g����true

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

    // �̏�(n��ȏ�A���Ń^�C���A�E�g)�̊m�F
    public void checkFailure(int n) {
        String failureStart = "";
        boolean flag = false;           // �^�C���A�E�g����true
        int count = 0;                  // �A������^�C���A�E�g��

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

    // �l�b�g���[�N�A�h���X�̎Z�o
    public static String checkAddress (String str) {
        int serverAddress[] = new int[4];
        int subnetmask[] = new int[4];
        int networkAddress[] = new int[4];

        // �T�[�o�[�A�h���X��p��
        String[] strings = str.split("/");
        int prefix = Integer.parseInt(strings[1]);  // �l�b�g���[�N�v���t�B�b�N�X����ۑ�
        strings = strings[0].split("\\.");          // �T�[�o�[�A�h���X��4�������Ĕz��ɕۑ�

        for (int i = 0; i < 4; i++) {
            serverAddress[i] = Integer.parseInt(strings[i]);    // String����int�փL���X�g
        }

        // �T�u�l�b�g�}�X�N��p��
        String tmpStr = "";
        for (int j = 0; j < 32; j++) {
            if (prefix > 0) {
                tmpStr += "1";          // �l�b�g���[�N�v���t�B�b�N�X���Ɠ�����1��ǉ����C
                prefix--;
            } else {
                tmpStr += "0";          // �c���0�Ŗ��߂�
            }
        }

        int j = 0;
        for (int i = 0; i < 4; i++) {
            // 4�������CString����int�փL���X�g���ĕۑ�
            subnetmask[i] = Integer.parseInt(tmpStr.substring(j, j + 8), 2);
            j += 8;
        }

        tmpStr = "";
        for (int k = 0; k < 3; k++) {
            // �T�[�o�[�A�h���X�ƃT�u�l�b�g�}�X�N�̘_���ς���
            // �l�b�g���[�N�A�h���X���Z�o����
            networkAddress[k] = serverAddress[k] & subnetmask[k];
            tmpStr = tmpStr + String.valueOf(networkAddress[k]) + ".";
        }
        tmpStr = tmpStr + String.valueOf(networkAddress[3]);

        return tmpStr;

    }

    // �̏���Ԃ̏o��
    public void printFailure(LogData log, int i){
        try {
            FileWriter fw = new FileWriter("result" + i + ".txt", true);
            if (this.getFailureFlag()) {
                fw.write("--- " + this.getServerAddress() + " : �̏Ⴀ�� -------------\r\n");
                fw.write(this.failureStart + " - " + this.failureEnd + "\r\n" );
            } else {
                fw.write("--- " + this.getServerAddress() + " : �̏�Ȃ� -------------\r\n");
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
