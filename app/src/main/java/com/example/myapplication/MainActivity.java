package com.example.myapplication;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.util.NdefMessageParser;
import com.example.myapplication.util.ParsedNdefRecord;
import com.example.myapplication.util.PatternLockUtils;
import com.github.ajalt.reprint.core.Reprint;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.os.StrictMode.setThreadPolicy;

public class MainActivity extends BaseActivity {
    //String host = "113.198.80.215";    // 서버 컴퓨터 IP
    //int port = 80;
    FirstConnectThread thread;
    Bundle bundle;
    Handler mHandler;
    public static String save_id;

    long now;
    Date date;
    SimpleDateFormat sdfNow;
    String formatDate;
    TextView dateNow;
    private SharedPreferences pref;
    private TelephonyManager telephonyManager;
    private EditText Stuid;
    Intent intent;
    private BackPressCloseHandler backPressCloseHandler;

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        init();

        //Toast.makeText(getApplication(), getPackageName().toString(), Toast.LENGTH_LONG).show();



        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                bundle = msg.getData();

                String ss = bundle.getString("key");

                Toast.makeText(getApplicationContext(), bundle.getString("key"), Toast.LENGTH_SHORT).show();
            }
        };
    }

    // NFC 기능
    @Override
    protected void onNewIntent(Intent intent) {
        System.out.println("new1");
        setIntent(intent);
        System.out.println("new2");
        Taginit(intent);
        System.out.println("new3");
    }

    // NFC Read 기능
    public void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled())
                showWirelessSettings();

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }

        NdefMessage[] msgs = null;



        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {

            Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

                byte[] payload = msgs[0].getRecords()[0].getPayload();


               /* TextView myText = (TextView)findViewById(R.id.textView);
                myText.setText(new String(payload));*/

               onNewIntent(getIntent());            // 어플이 시작되면서 펜딩인텐트를 통한 NewIntent가 불러지면서 TagInit이 불러짐
               Toast.makeText(getApplication(), new String(payload), Toast.LENGTH_LONG).show();

            }
        }
    }

    class FirstConnectThread extends Thread {
        Object input;
        String output_id;
        String output_pw;
        String output_num;
        SharedPreferences id_pref;
        SharedPreferences.Editor id_commit;

        SharedPreferences storage_pref;
        SharedPreferences.Editor storage_commit;

        public void run() {

            String host = "113.198.84.29";    // 서버 컴퓨터 IP
            //String host = "113.198.80.215";
            int port = 80;

            try {
                Log.e("ffff","!!" + host + " " + port);
                InetSocketAddress socketAddress = new InetSocketAddress(host,port);
                Socket socket = new Socket();
                socket.connect(socketAddress);
                System.out.println("서버로 연결되었습니다. : " + host + ", " + port);
                telephonyManager = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
                String output_num = telephonyManager.getLine1Number();
                System.out.println("전화번호 1: " + output_num);
                output_num = output_num.replace("+82", "0");
                System.out.println("전화번호 2: " + output_num);
                output_num = "01041199582";
                EditText id = (EditText) findViewById(R.id.login_id_et);
                EditText password = (EditText) findViewById(R.id.login_pw_et);

                output_id = id.getText().toString();
                ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                outstream.writeObject(output_id);
                outstream.flush();
                System.out.println("서버로 보낸 데이터 : " + output_id);

                output_pw = password.getText().toString() + "%3B%3B" + output_num;
                ObjectOutputStream outstream2 = new ObjectOutputStream(socket.getOutputStream());
                outstream2.writeObject(output_pw);
                outstream2.flush();
                System.out.println("서버로 보낸 데이터 : " + output_pw);

                ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
                input = instream.readObject();
                System.out.println("서버로부터 받은 데이터: " + input);


                if (input.toString().equals("허가")) {
                    //intent.putExtra("pid", id.getText().toString());
                    String pid = id.getText().toString();
                    id_pref = getSharedPreferences("login", MODE_APPEND);
                    id_commit = id_pref.edit();
                    id_commit.putString("id", pid);
                    id_commit.commit();
                    System.out.println(">>>>>>>>>>>>>>>> : " + id_pref.getString("id", ""));

                    Reprint.initialize(getApplicationContext());
                    //지문이 단말기에 등록되어있는지 ||  패턴이 등록되어있는지 확인하는 조건문!!
                    if (PatternLockUtils.hasPattern(getApplicationContext())) {
                        intent = new Intent(getApplicationContext(), OldFirstView.class);
                    } else
                        intent = new Intent(getApplicationContext(), NewFirstView.class);

                    startActivity(intent);

                } else if (input.toString().equals("불허가")) {
                    bundle = new Bundle();
                    bundle.putString("key", "ID와 PW를 다시 확인해주세요");
                    Message msg = new Message();
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
                instream.close();
                outstream.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("접근실패");
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    public void init(){

        // NFC 어댑터및 인텐트 세팅
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // NFC를 지원하지 않는 기기일때
        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


        // 서버로 접속하는 쓰레드 권한부여,
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        setThreadPolicy(policy);
        backPressCloseHandler = new BackPressCloseHandler(this); //뒤로버튼 종료

        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        String state = Environment.getExternalStorageState();
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE
            }, 466);
        }

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 466);
        }

        // ***아이디(학번) 저장하는 부분
        pref = getSharedPreferences("login", MODE_PRIVATE); // Shared Preference를 불러옵니다.
        save_id = pref.getString("id", "");
        EditText idet = (EditText)findViewById(R.id.login_id_et);
        idet.setText(save_id);


        now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다.
        formatDate = sdfNow.format(date);

        dateNow = (TextView) findViewById(R.id.qr_date);

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        Button btn = (Button) findViewById(R.id.login_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // intent = new Intent(getApplicationContext(),NewFirstView.class);
                // startActivity(intent);
                thread = new FirstConnectThread();
                thread.start();
            }
        });
    }

    public void Taginit(Intent nfcIntent){
        NdefMessage[] msgs;
        byte[] empty = new byte[0];
        byte[] id = nfcIntent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        Tag tag = (Tag) nfcIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] payload = dumpTagData(tag).getBytes();
        NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
        NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
        msgs = new NdefMessage[] {msg};

        displayMsgs(msgs);
    }

    private void displayMsgs(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0)
            return;

        StringBuilder builder = new StringBuilder();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
        final int size = records.size();

        for (int i = 0; i < size; i++) {
            ParsedNdefRecord record = records.get(i);
            String str = record.str();
            builder.append(str).append("\n");
        }

        Log.e("NFCC : ", builder.toString());
        Toast.makeText(getApplication(), builder.toString(), Toast.LENGTH_LONG).show();
        //text.setText(builder.toString());
    }

    private String dumpTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        sb.append("ID (hex): ").append(toHex(id)).append('\n');
        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n');
        sb.append("ID (dec): ").append(toDec(id)).append('\n');
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n');

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                String type = "Unknown";

                try {
                    MifareClassic mifareTag = MifareClassic.get(tag);

                    switch (mifareTag.getType()) {
                        case MifareClassic.TYPE_CLASSIC:
                            type = "Classic";
                            break;
                        case MifareClassic.TYPE_PLUS:
                            type = "Plus";
                            break;
                        case MifareClassic.TYPE_PRO:
                            type = "Pro";
                            break;
                    }
                    sb.append("Mifare Classic type: ");
                    sb.append(type);
                    sb.append('\n');

                    sb.append("Mifare size: ");
                    sb.append(mifareTag.getSize() + " bytes");
                    sb.append('\n');

                    sb.append("Mifare sectors: ");
                    sb.append(mifareTag.getSectorCount());
                    sb.append('\n');

                    sb.append("Mifare blocks: ");
                    sb.append(mifareTag.getBlockCount());
                } catch (Exception e) {
                    sb.append("Mifare classic error: " + e.getMessage());
                }
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

}