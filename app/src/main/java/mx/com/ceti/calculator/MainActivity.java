package mx.com.ceti.calculator;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import mx.com.ceti.calculator.models.DataSQLiteHelper;

public class MainActivity extends AppCompatActivity {

    private static final String SOAP_ACTION = "http://tempuri.org/Divide";
    private static final String METHOD_NAME = "Divide";
    private static final String NAMESPACE = "http://tempuri.org/";
    private Button btnDivide;
    private EditText txtNum1, txtNum2;
    private TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        
        btnDivide = findViewById(R.id.btnDivide);
        txtNum1 = findViewById(R.id.inpNumber1);
        txtNum2 = findViewById(R.id.inpNumber2);
        txtResult = findViewById(R.id.txtResult);

        btnDivide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate the data
                if (!txtNum1.getText().toString().equals("") && !txtNum2.getText().toString().equals("")){
                    if (!txtNum1.getText().toString().equals("0") && !txtNum2.getText().toString().equals("0")){
                        Integer num1, num2;
                        num1 = Integer.valueOf(txtNum1.getText().toString());
                        num2 = Integer.valueOf(txtNum2.getText().toString());
                        taskDivisionRequest myRequest = new taskDivisionRequest();
                        myRequest.execute(num1, num2);
                    } else {
                        Toast.makeText(MainActivity.this, "Por favor ingrese un valor diferente de 0.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Por favor llene todos los campos para poder realizar la operación correctamente.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class taskDivisionRequest extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected Integer doInBackground(Integer... integers) {
            String URL = "http://www.dneonline.com/calculator.asmx";

            // Numbers to make the operation
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("intA", integers[0].toString()); // adding method property here serially
            request.addProperty("intB", integers[1].toString()); // adding method property here serially

            SoapSerializationEnvelope envelope = new      SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            httpTransport.debug = true;

            try {
                httpTransport.call(SOAP_ACTION, envelope);
            } catch (HttpResponseException e) {
                // Auto-generated catch block
                Log.e("HTTPLOG", e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                // Auto-generated catch block
                Log.e("IOLOG", e.getMessage());
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // Auto-generated catch block
                Log.e("XMLLOG", e.getMessage());
                e.printStackTrace();
            }

            final Object  result;
            try {
                result = envelope.getResponse();
                Log.i("RESPONSE", integers[0].toString() + " / " + integers[1].toString() + " = " + String.valueOf(result)); // see output in the console
                // Get the instance of the DB
                DataSQLiteHelper dataSQLiteHelper = DataSQLiteHelper.getInstance(MainActivity.this);
                SQLiteDatabase database = dataSQLiteHelper.getWritableDatabase();
                // Set de content values to insert into the DB
                ContentValues contentValues = new ContentValues();
                contentValues.put(DataSQLiteHelper.getColumnOperation(), integers[0].toString() + " / " + integers[1].toString());
                contentValues.put(DataSQLiteHelper.getColumnResult(), String.valueOf(result));

                long rowInserted = database.insertOrThrow(DataSQLiteHelper.getTableOperations(), null, contentValues);
                if ( rowInserted != -1 ){
                    Log.i("DB_isInserted", "Datos insertados correctamente.");
                } else {
                    Log.e("DB_isInserted", "Problemas la insertar la información, por favor vuelva a intentarlo.");
                }

                return Integer.valueOf(String.valueOf(result));
            } catch (SoapFault e) {
                // Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Integer result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtResult.append(String.valueOf(result));
                    Toast.makeText(MainActivity.this, "El resultado de la operación es: " + String.valueOf(result), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
