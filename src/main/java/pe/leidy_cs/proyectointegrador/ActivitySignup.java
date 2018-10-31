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

import pe.leidy_cs.proyectointegrador.clases.Alumno;

public class ActivitySignup extends AppCompatActivity {
    EditText codigo, nombres, apellido_p, apellido_m, correo, password;
    Alumno alumno;
    ImageView signupback;
    String id_Alumno;
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
        this.nombres = (EditText) findViewById(R.id.editTextNombre);
        this.apellido_p = (EditText) findViewById(R.id.editTextApellidoP);
        this.apellido_m = (EditText) findViewById(R.id.editTextApellidoM);
        this.correo = (EditText) findViewById(R.id.editTextCorreo);
        this.password = (EditText) findViewById(R.id.editTextPassword);
    }

    public void btn_clickGuardarPersona(View view) {
        alumno = new Alumno();
        alumno.setCodigo(codigo.getText().toString().trim());
        alumno.setNombres(nombres.getText().toString().trim());
        alumno.setApellido_p(apellido_p.getText().toString().trim());
        alumno.setApellido_m(apellido_m.getText().toString().trim());
        alumno.setCorreo(correo.getText().toString().trim());
        alumno.setPassword(password.getText().toString().trim());
        if (this.operacion.equals("insertar"))
            new InsertarPersona().execute();
    }

    //Insertar Persona
    private class InsertarPersona extends AsyncTask<Void, Void, Boolean> {

        @Override
        public Boolean doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://tercerintento-marco121942.c9users.io:80/rest/alumnos/");
            httpPost.setHeader("Content-Type", "application/json");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("codigo",     alumno.getCodigo());
                jsonObject.put("nombres",    alumno.getNombres());
                jsonObject.put("apellido_p", alumno.getApellido_p());
                jsonObject.put("apellido_m", alumno.getApellido_m());
                jsonObject.put("correo",     alumno.getCorreo());
                jsonObject.put("password",   alumno.getPassword());
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
                Intent it = new Intent(ActivitySignup.this, NavigationActivity.class);
                it.putExtra("codigo", codigo.getText().toString().trim());
                it.putExtra("nombres", nombres.getText().toString().trim());
                it.putExtra("apellido_p", apellido_p.getText().toString().trim());
                it.putExtra("apellido_m", apellido_m.getText().toString().trim());
                it.putExtra("correo", correo.getText().toString().trim());
                startActivity(it);
            } else {
                Toast.makeText(ActivitySignup.this, "Error al registrar", Toast.LENGTH_LONG).show();
            }
        }
    }
}