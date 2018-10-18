package pe.leidy_cs.proyectointegrador;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import pe.leidy_cs.proyectointegrador.clases.Estudiante;

public class ActivitySignup extends AppCompatActivity {
    String URI = "http://192.168.0.8:8000/rest/estudiantes/";
    EditText codigo, nombre, correo, password;
    Estudiante estudiante;
    ImageView signupback;
    String id_estudiante;
    String operacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        Bundle bundle = getIntent().getExtras();
        this.operacion = bundle.getString("operacion");
        inicializar();

        signupback = (ImageView)findViewById(R.id.signupback);
        signupback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ActivitySignup.this, MainActivity.class);
                startActivity(it);
            }
        });
    }

    public void inicializar(){
        this.codigo = (EditText) findViewById(R.id.editTextCodigo);
        this.nombre = (EditText) findViewById(R.id.editTextNombre);
        this.correo = (EditText) findViewById(R.id.editTextCorreo);
        this.password = (EditText) findViewById(R.id.editTextPassword);
    }

    public void btn_clickGuardarPersona(View view) {
        estudiante = new Estudiante();
        estudiante.setId_est(codigo.getText().toString().trim());
        estudiante.setNombre_est(nombre.getText().toString().trim());
        estudiante.setCorreo_est(correo.getText().toString().trim());
        estudiante.setPassword_est(password.getText().toString().trim());
        if (this.operacion.equals("insertar"))
            new InsertarPersona().execute();
    }

    //Insertar Persona
    private class InsertarPersona extends AsyncTask<Void, Void, Boolean> {

        @Override
        public Boolean doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://192.168.0.7:9090/rest/estudiantes/");
            httpPost.setHeader("Content-Type", "application/json");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id_est", estudiante.getId_est());
                jsonObject.put("nombre_est", estudiante.getNombre_est());
                jsonObject.put("correo_est", estudiante.getCorreo_est());
                jsonObject.put("password_est", estudiante.getPassword_est());
                StringEntity stringEntity = new StringEntity(jsonObject.toString());
                httpPost.setEntity(stringEntity);
                httpClient.execute(httpPost);
                return true;
            } catch (org.json.JSONException | java.io.IOException e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void onPostExecute(Boolean result) {
            if (result){
                Toast.makeText(ActivitySignup.this, "Registrado correctamente", Toast.LENGTH_LONG).show();
                Intent it = new Intent(ActivitySignup.this, ProfileActivity.class);
                it.putExtra("codigo", codigo.getText().toString().trim());
                it.putExtra("nombre", nombre.getText().toString().trim());
                it.putExtra("correo", correo.getText().toString().trim());
                startActivity(it);
            } else {
                Toast.makeText(ActivitySignup.this, "Error al registrar", Toast.LENGTH_LONG).show();
            }
        }
    }
}