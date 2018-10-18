package pe.leidy_cs.proyectointegrador;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;

import pe.leidy_cs.proyectointegrador.clases.Estudiante;

public class ProfileActivity extends AppCompatActivity {
    EditText editTextNombre, editTextCodigo, editTextCorreo;
    String nombre, codigo, correo;
    Estudiante estudiante;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle bundle = getIntent().getExtras();
        this.codigo = bundle.getString("codigo");
        this.nombre = bundle.getString("nombre");
        this.correo = bundle.getString("correo");

        this.editTextCodigo = (EditText) findViewById(R.id.textCodigo);
        this.editTextNombre = (EditText) findViewById(R.id.textNombre);
        this.editTextCorreo = (EditText) findViewById(R.id.textCorreo);

        editTextCodigo.setText(codigo);
        editTextNombre.setText(nombre);
        editTextCorreo.setText(correo);
    }

    public void btn_clickEditarPersona(View view){
        estudiante = new Estudiante();
        estudiante.setId_est(editTextCodigo.getText().toString().trim());
        estudiante.setNombre_est(editTextNombre.getText().toString().trim());
        estudiante.setCorreo_est(editTextCorreo.getText().toString().trim());
        new ActualizarPersona().execute();
    }

    public void btn_clickEliminarPersona(View view){
        new EliminarPersona().execute();
    }

    private class ActualizarPersona extends AsyncTask<Void, Void, Boolean> {
        @Override
        public Boolean doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPut httpPut = new HttpPut("http://192.168.0.7:9090/rest/estudiantes/"+codigo.trim()+"/");
            httpPut.setHeader("content-type", "application/json");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id_est", estudiante.getId_est());
                jsonObject.put("nombre_est", estudiante.getNombre_est());
                jsonObject.put("correo_est", estudiante.getCorreo_est());

                StringEntity stringEntity = new StringEntity(jsonObject.toString());
                httpPut.setEntity(stringEntity);
                httpClient.execute(httpPut);
                return true;

            } catch (org.json.JSONException | java.io.IOException e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void onPostExecute(Boolean result) {
            String msj;
            if (result){
                msj = "Actualizado correctamente";
            } else {
                msj = "Problemas al actualizar";
            }
            Toast.makeText(ProfileActivity.this, msj, Toast.LENGTH_SHORT).show();
        }
    }

    //Eliminar Persona
    private class EliminarPersona extends AsyncTask<Void, Void, Boolean>{

        @Override
        public Boolean doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpDelete httpDelete = new HttpDelete("http://192.168.0.7:9090/rest/estudiantes/"+codigo.trim()+"/");
            httpDelete.setHeader("Content-Type", "application/json");

            try {
                httpClient.execute(httpDelete);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void onPostExecute(Boolean result) {
            if (result){
                Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(i);
                Toast.makeText(ProfileActivity.this, "Eliminado correctamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Problema al eliminar", Toast.LENGTH_LONG).show();
            }
        }
    }
}
