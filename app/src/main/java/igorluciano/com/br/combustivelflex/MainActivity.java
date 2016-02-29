package igorluciano.com.br.combustivelflex;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText txtValGas,txtValEta;
    Button btnLimpar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtValGas = (EditText)findViewById(R.id.gasolina_edit_text);
        txtValEta = (EditText)findViewById(R.id.etanol_edit_text);
        txtValGas.addTextChangedListener(Mask.insert(Mask.DOUBLE_MASK, txtValGas));
        txtValEta.addTextChangedListener(Mask.insert(Mask.DOUBLE_MASK, txtValEta));

        btnLimpar = (Button) findViewById(R.id.button_limpar);
        btnLimpar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                txtValGas.setText(String.valueOf(""));
                txtValEta.setText(String.valueOf(""));
            }
        });
    }

    public void onClickResult(View v){
        if(TextUtils.isEmpty(txtValGas.getText().toString()) || TextUtils.isEmpty(txtValEta.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Digite os valores.", Toast.LENGTH_SHORT).show();
        }else{
            Intent it = new Intent(this, ResultActivity.class);
            it.putExtra("valGas", Double.parseDouble(txtValGas.getText().toString()));
            it.putExtra("valEta", Double.parseDouble(txtValEta.getText().toString()));
            startActivity(it);
        }
    }
}
