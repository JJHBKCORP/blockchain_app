package com.example.block_jj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.*;
import org.web3j.protocol.rx.Web3jRx;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Convert;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import com.jj.contract.Greeter;

import java8.util.Optional;
import java8.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {
   // LinearLayout layout = findViewById(R.id.linear);
public TextView address1,credentialst,status,contractadd;
public  TextView network1;
public  Web3j web3;
public  int RequestCode=15;
public EditText enter_add;
public EditText enter_key;
public  Web3ClientVersion web3ClientVersion;
//public final static BigInteger gasprice=BigInteger.valueOf(210000L);
public final static BigInteger gasprice=BigInteger.valueOf(45000L);
public   Credentials credentials;
    public TransactionManager transactionManager;
    public Transfer transfer;
   // public final static BigInteger gaslimit=BigInteger.valueOf(200000000L);
   public final static BigInteger gaslimit=BigInteger.valueOf(2000000L);
    //private final static BigInteger value= BigInteger.valueOf(1588888888888L);
private final static  String recepient = "0x4215EFbc9B4145d29C36D1a7B3e232E4A231Fa78";
// private final static String privkey="f65a2a2c3a4c53b4eaf07b34d9c447cd1c851546b96424582cefa92350c4d6e2"; // ropsten

    private final static String privkey="2e33d59ce36d58edaccdcdb273a5e6cd5e1effcf96d9e1361b7086d8e8297490"; // kovan
 //  private final static String privkey="0c612ba9eaa7b438ca5f8f45b2f057afb9f7976681f50d6303293e3ea5cf7827";  // ganache
            Greeter greeter;
    private  final  static  long value = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},RequestCode);
        setContentView(R.layout.activity_main);
        address1 = findViewById(R.id.address);
        network1 = findViewById(R.id.network);
        enter_add= findViewById(R.id.enter_add);
        enter_key= findViewById(R.id.enter_key);
        status=findViewById(R.id.STATUS);
        contractadd= findViewById(R.id.contractaddress);
       // web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/fef16b16c8b1494593c726bdc91e8920"));
        //web3 = Web3j.build(new HttpService("http://192.168.31.126:7545"));
        web3 = Web3j.build(new HttpService( "https://kovan.infura.io/v3/fef16b16c8b1494593c726bdc91e8920"));
    }


    @Override
    protected void onStart() {
        super.onStart();
        final Handler handler= new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                getnetworkversion(web3);
            }
        });

        }


private void getnetworkversion(Web3j web31){
    try {
        web3ClientVersion = web31.web3ClientVersion().sendAsync().get();
        Log.d("try","here bc");
        String web3ClientversionString= web3ClientVersion.getWeb3ClientVersion();

        network1.setText(web3ClientversionString);

    } catch ( InterruptedException | ExecutionException e) {
        e.printStackTrace();
    }

    //private Credentials getCredentialsfromwallet(){
    //Credentials credentials= WalletUtils.loadCredentials("jjblockchain",path);


}
private Credentials getCredentialsfromprivateKey(String priv_key){
     credentials = Credentials.create(priv_key);
credentialst= findViewById(R.id.credentials);
credentialst.setText(String.valueOf( credentials));
return credentials;
}
    public void getCredentials(View view){
              // getCredentialsfromprivateKey(String.valueOf(enter_key.getText()));
        getCredentialsfromprivateKey(String.valueOf(privkey));
        Log.d("cred", "getCredentials: ");
        

    }
public  void  sendEther(View view) throws Exception {
    Log.d("tran", "sendEther: ");
    transactionManager = new RawTransactionManager(
            web3,
            getCredentialsfromprivateKey(privkey)
    );
    transfer = new Transfer(web3,transactionManager);

//    TransactionReceipt transactionReceipt= transfer.sendFunds(recepient, BigDecimal.ONE, Convert.Unit.ETHER, gasprice, gaslimit).send();



    CompletableFuture<TransactionReceipt> transactionReceipt = Transfer.sendFunds(web3,credentials, String.valueOf(enter_add.getText()), BigDecimal.valueOf(0.02), Convert.Unit.ETHER).sendAsync();


    while(transactionReceipt.isDone() != true  ){
        status.setText("transaction under process....");
        TimeUnit.SECONDS.sleep(10);
            }


    TransactionReceipt transactionReceipt1= transactionReceipt.get(2, TimeUnit.SECONDS);
    String hash= transactionReceipt1.getBlockHash();
    
    status.setText("transaction complete & hash is:"+ hash);

}
public void Deploy_Contract(View view) throws InterruptedException, ExecutionException {
    String provider= "0x8C46E1f01AB9A5f7c61F76b1cCb4d1e734946B67";
    ContractGasProvider contractGasProvider;
    RemoteCall<Greeter> greeter1 = Greeter.deploy(web3,credentials,gasprice,gaslimit,new String("jj is coolest"));
  //  RemoteCall<Greeter> greeter1 = Greeter.deploy(web3,credentials,gasprice,gaslimit);
/*greeter1.
greeter1.wait();
greeter1.notify();*/

        CompletableFuture<Greeter> greeter2 = greeter1.sendAsync();

while (greeter2.isDone() != true){
    contractadd.setText("deployment under progress");
    TimeUnit.SECONDS.sleep(10);
    Log.d("contract", "Deploy_Contract:pocessing ");
}
greeter=greeter2.get();
    Log.d("contract", "Deploy_Contract: successful");
    //Optional<TransactionReceipt> contractaddress1 = greeter.getTransactionReceipt();
  //  TransactionReceipt contractaddress = contractaddress1.get();
   // contractadd.setText(contractaddress.getContractAddress());
    contractadd.setText(greeter.getContractAddress());
}

/* Web3j web3= Web3j.build(new HttpService());
    Web3ClientVersion clientVersion =web3.web3ClientVersion().sendAsync().get();
    Credentials credentials = WalletUtils.loadCredentials("password","pathtowalletfile");
    TransactionReceipt transactionReceipt = Transfer.sendFunds(web3,credentials,"0xaddress", BigDecimal.valueOf(0.2), Convert.Unit.ETHER).get();
    contract greeter.deploy(web3, credentials, BigInteger.ZERO,new Utf8String("hejkko"));
    EthGetBalance ethGetBalance=  web3.ethGetBalance(strAddress, DefaultBlockParameterName.LATEST).sendAsync().get();

try {
        Process process = Runtime.getRuntime().exec("top -n 1 -d 1");
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
    } catch (InterruptedException e) {
        e.printStackTrace();
    }*/

}