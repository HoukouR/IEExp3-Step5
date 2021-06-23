package ieexp3.id190441148.ieexp3_step5;

import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.Objects;

public class BasicTask5Activity extends AppCompatActivity implements CommunicationTask5Callback {
    /** IP addressを入力するエディットテキスト */
    protected EditText editAddress;
    /** Port numberを入力するエディットテキスト */
    protected EditText editPort;
    /** 1つ目の穴の番号を入力するエディットテキスト */
    protected EditText editFirstHole;
    /** 2つ目の穴の番号を入力するエディットテキスト */
    protected EditText editSecondHole;
    /** 3つ目の穴の番号を入力するエディットテキスト */
    protected EditText editThirdHole;
    /** Logを表示するエディットテキスト */
    protected EditText editLog;
    /** サーバに接続するボタン */
    protected Button buttonConnect;
    /** サーバから切断するボタン */
    protected Button buttonDisconnect;
    /**ロボット制御を開始するボタン */
    protected Button buttonRun;
    /** Logをクリアするボタン */
    protected Button buttonClear;
    /** TCPクライアントタスク */
    private CommunicationTask5 task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_task5);

        // GUIコンポーネントを取得してインスタンス変数に設定
        editAddress = (EditText)findViewById(R.id.editAddress);
        editPort = (EditText)findViewById(R.id.editPort);
        editFirstHole = (EditText)findViewById(R.id.editFirstHole);
        editSecondHole = (EditText)findViewById(R.id.editSecondHole);
        editThirdHole = (EditText)findViewById(R.id.editThirdHole);
        editLog = (EditText)findViewById(R.id.editLog);
        buttonConnect = (Button)findViewById(R.id.buttonConnect);
        buttonDisconnect = (Button)findViewById(R.id.buttonDisconnect);
        buttonRun = (Button)findViewById(R.id.buttonRun);
        buttonClear = (Button)findViewById(R.id.buttonClear);

        // ボタンの有効/無効を設定
        buttonConnect.setEnabled(true);
        buttonDisconnect.setEnabled(false);
        buttonRun.setEnabled(false);

        //editLogを編集不可に設定
        editLog.setFocusable(false);

        // 次の行は消さないこと
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
    }

    /**
     * Connectボタンをクリックした時に呼び出すイベントハンドラ
     * @param view
     */
    public void handleButtonConnect(View view) {
        // サーバのIPアドレスを取得
        String address = editAddress.getText().toString();
        // 入力されていない場合は警告ダイアログを表示
        if (address.equals("")) {
            showAlertDialog("IPアドレスを入力してください！");
            return;
        }

        // 入力されていない場合は警告ダイアログを表示
        if (editPort.getText().toString().equals("")) {
            showAlertDialog("ポート番号を入力してください！");
            return;
        }
        //サーバのポート番号を取得
        int port = Integer.parseInt(editPort.getText().toString());

        // TCPクライアントタスクを生成してバックグラウンドで実行（非同期処理）
        task = new CommunicationTask5(address, port, this);
        task.execute();
    }

    /**
     * Disconnectボタンをクリックした時に呼び出すイベントハンドラ
     * @param view
     */
    public void handleButtonDisconnect(View view) {
        task.sendMessage("disconnect");
        if (task != null)
            task.stop();
    }

    /**
     * Runボタンをクリックした時に呼び出すイベントハンドラ
     * @param view
     */
    public void handleButtonRun(View view) {
        // 入力した穴の番号を取得
        String[] holeNo = new String[3];
        holeNo[0] = editFirstHole.getText().toString();
        holeNo[1] = editSecondHole.getText().toString();
        holeNo[2] = editThirdHole.getText().toString();

        // 入力されていない場合は警告ダイアログを表示
        for(int i = 0; i < 3; i++) {
            if (holeNo[i].equals("")) {
                showAlertDialog(i+1 + "番目の穴を指定してください！");
                return;
            }
            //入力された数字が範囲外の場合は警告ダイアログを表示
            int num = Integer.parseInt(holeNo[i]);
            if(num < 1 || num > 6) {
                showAlertDialog("エディットテキストには1-6の数字を入力してください！");
                return;
            }
        }

        //メッセージをサーバへ送信
        task.sendMessage("run," + holeNo[0] + "," + holeNo[1] + "," + holeNo[2]);
    }

    /**
     * Clearボタンをクリックした時に呼び出すイベントハンドラ
     * @param view
     */
    public void handleButtonClear(View view) {
        editLog.setText("");
    }

    /**
     * CommunicationTask5側のonPreExecute()からコールバックされるメソッド
     */
    @Override
    public void onPreExecute() {
        // ボタンの有効/無効を設定
        buttonConnect.setEnabled(false);
        buttonDisconnect.setEnabled(true);
        buttonRun.setEnabled(true);

        // トーストの表示
        Toast.makeText(this, "CommunicationTask5 is started!.", Toast.LENGTH_SHORT).show();
    }

    /**
     * TcpClientTask側のonProgressUpdate()からコールバックされるメソッド
     * @param values   Logに出力するメッセージ
     */
    @Override
    public void onProgressUpdate(String... values) {
        // メインアクティビティのLogにメッセージを設定または追記
        if (editLog.length() == 0)
            editLog.setText(values[0]);
        else
            editLog.append("\n" + values[0]);
    }

    /**
     * CommunicationTask5側のonPostExecute()からコールバックされるメソッド
     * @param aVoid doInBackground()の戻り値
     */
    @Override
    public void onPostExecute(Void aVoid) {
        // トーストの表示
        Toast.makeText(this, "CommunicationTask5 is finished.", Toast.LENGTH_SHORT).show();

        // ボタンの有効/無効を設定
        buttonConnect.setEnabled(true);
        buttonDisconnect.setEnabled(false);
        buttonRun.setEnabled(false);
    }

    //アラートの表示
    private void showAlertDialog(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Error!");			// ダイアログタイトルの設定
        dialog.setMessage(message);			// ダイアログメッセージの設定
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }
}
