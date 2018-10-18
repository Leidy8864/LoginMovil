package pe.leidy_cs.proyectointegrador;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import pe.leidy_cs.proyectointegrador.clases.Estudiante;

public class ActivitySignin extends AppCompatActivity {
    Estudiante estudiante;
    ImageView signinback;
    EditText codigo, password;

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
    }

    public void btn_clickIniciarSesion(View view){
        new ObtenerPersona().execute();
    }

    private class ObtenerPersona extends AsyncTask<Void, Void, Estudiante> {
        @Override
        public Estudiante doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://192.168.0.7:9090/rest/estudiantes/"+codigo.getText().toString().trim()+"/");
            httpGet.setHeader("content-type", "application/json");
            estudiante = new Estudiante();
            try {
                HttpResponse response = httpClient.execute(httpGet);
                String responString = EntityUtils.toString(response.getEntity());

                JSONObject jsonObject = new JSONObject(responString);
                String pass = jsonObject.getString("password_est");
                estudiante.setId_est(jsonObject.getString("id_est"));
                estudiante.setNombre_est(jsonObject.getString("nombre_est"));
                estudiante.setCorreo_est(jsonObject.getString("correo_est"));
                estudiante.setPassword_est(jsonObject.getString("password_est"));
                return estudiante;

            } catch (org.json.JSONException | java.io.IOException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void onPostExecute(Estudiante estudiante) {
            super.onPostExecute(estudiante);
            if (estudiante.getPassword_est().equals(password.getText().toString().trim())){
                Intent it = new Intent(ActivitySignin.this, ProfileActivity.class);
                it.putExtra("codigo", estudiante.getId_est());
                it.putExtra("nombre", estudiante.getNombre_est());
                it.putExtra("correo", estudiante.getCorreo_est());
                startActivity(it);
            } else {
                Toast.makeText(ActivitySignin.this, "Campos incorrectos", Toast.LENGTH_LONG).show();
            }
        }
    }
}