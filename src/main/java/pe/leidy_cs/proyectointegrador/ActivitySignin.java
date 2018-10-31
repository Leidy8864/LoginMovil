package pe.leidy_cs.proyectointegrador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import pe.leidy_cs.proyectointegrador.clases.Alumno;

public class ActivitySignin extends AppCompatActivity {
    // SharedPreferences
    private SharedPreferences sharedPreferences;

    Alumno alumno;
    ImageView signinback;
    private EditText codigo, password;
    private ProgressBar progressBar;
    private View loginPanel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        signinback = (ImageView)findViewById(R.id.signinback);
        signinback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ActivitySignin.this,MainActivity.class);
                startActivity(it);
            }
        });

        this.codigo = (EditText) findViewById(R.id.editTextCodigo2);
        this.password = (EditText) findViewById(R.id.editTextPassword2);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        loginPanel = findViewById(R.id.login_panel);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // username remember
        String codigo = sharedPreferences.getString("username", null);
        if(codigo != null){
            this.codigo.setText(codigo);
            this.password.requestFocus();
        }

        // islogged remember
        if(sharedPreferences.getBoolean("islogged", false)){
            // Go to Dashboard
            goDashboard();
        }
    }

    public void btn_clickIniciarSesion(View view){
        loginPanel.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        String codigoString = codigo.getText().toString();
        String passwordString = password.getText().toString();
        if(codigoString.isEmpty() || passwordString.isEmpty()){
            Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        new ObtenerPersona().execute();
    }

    private class ObtenerPersona extends AsyncTask<Void, Void, Alumno> {

        @Override
        public Alumno doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://tercerintento-marco121942.c9users.io:8080/rest/alumnos/"+codigo.getText().toString().trim()+"/");
            httpGet.setHeader("content-type", "application/json");
            alumno = new Alumno();
            try {
                HttpResponse response = httpClient.execute(httpGet);
                String responString = EntityUtils.toString(response.getEntity());

                JSONObject jsonObject = new JSONObject(responString);
                alumno.setCodigo(jsonObject.getString("codigo"));
                alumno.setNombres(jsonObject.getString("nombres"));
                alumno.setApellido_p(jsonObject.getString("apellido_p"));
                alumno.setApellido_m(jsonObject.getString("apellido_m"));
                alumno.setCorreo(jsonObject.getString("correo"));
                alumno.setPassword(jsonObject.getString("password"));
                return alumno;

            } catch (org.json.JSONException | java.io.IOException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void onPostExecute(Alumno alumno) {
            super.onPostExecute(alumno);
            if (!alumno.getPassword().equals(password.getText().toString().trim())){
                Toast.makeText(ActivitySignin.this, "Codigo o password incorrectos", Toast.LENGTH_LONG).show();
                loginPanel.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                return;
            }
            // Save to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            boolean success = editor
                    .putString("codigo", alumno.getCodigo())
                    .putString("nombres", alumno.getNombres())
                    .putString("apellido_p", alumno.getApellido_p())
                    .putString("apellido_m", alumno.getApellido_m())
                    .putString("correo", alumno.getCorreo())
                    .putString("num", "1")
                    .putBoolean("islogged", true)
                    .commit();

            // Go to Dashboard
            goDashboard();
        }
    }

    private void goDashboard(){
        Intent it = new Intent(ActivitySignin.this, NavigationActivity.class);
        startActivity(it);
        finish();
    }
}